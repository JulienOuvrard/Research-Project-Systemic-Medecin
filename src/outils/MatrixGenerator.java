package outils;

import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

import outils.Matrix;
import outils.Parser;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.core.Mapping;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import fr.inria.edelweiss.kgram.api.core.Node;

public class MatrixGenerator {
	
	private String[] files;
	private String[] kegg_ids;
	private String[] elements;
	private Matrix compound_mass;
	private Matrix compound_energy;
	
	public MatrixGenerator(String[] f,String[] k_ids, String[] elts){
		kegg_ids=k_ids;
		elements=elts;
		compound_mass=new Matrix(elements.length,kegg_ids.length);
		compound_energy=new Matrix(1,kegg_ids.length);
		files=f;
	}
	
	public String[] getKegg_ids() {
		return kegg_ids;
	}

	public void setKegg_ids(String[] kegg_ids) {
		this.kegg_ids = kegg_ids;
	}

	public String[] getElements() {
		return elements;
	}

	public void setElements(String[] elements) {
		this.elements = elements;
	}

	public Matrix getCompound_mass() {
		return compound_mass;
	}

	public Matrix getCompound_energy() {
		return compound_energy;
	}

	public String makeQuery(String id, String nom, String formule){
		
		String prefix = "prefix bpax: <http://www.biopax.org/release/biopax-level3.owl#>"
				+ "prefix dc: <http://purl.org/dc/elements/1.1/> "
				+ "prefix btr: <http://bio2rdf.org/ns/bio2rdf#> ";

		id = (id=="") ? "?id": "\"cpd:"+id+"\"";
		nom = (nom=="") ? "?nom": "\""+nom+"\"";
		formule = (formule=="") ? "?formule": "\""+formule+"\"";
		
		String ret="select ?nom ?formule ?energie "
				+ "where { "
				+ "?s  dc:identifier "+id+"; "
				+ "dc:title "+nom+". "
				+ "OPTIONAL{?s btr:formula "+formule+". }"
				+ "OPTIONAL{?s bpax:deltaGPrime0 ?energie.} "
				+ "}";
		
		return prefix+ret;
	}
	
	public void generate() throws EngineException{
		System.out.println("[Matrix] generation started");
		Graph graph= Graph.create(true);
		Load ld=Load.create(graph);
		for(String file: files){
			ld.load(file);
		}
		
		QueryProcess exec = QueryProcess.create(graph);
		int k_index=0;
		for(String s:kegg_ids){
			Parser p = new Parser();
			String query=makeQuery(s,"","");
			
			Mappings map= exec.query(query);
			for(Mapping m: map){
				for(Node var : m.getQueryNodes()){
					if(var.getLabel().substring(1).compareTo("formule")==0){
						p.explodeFormula(m.getValue(var).toString());
						int e_index=0;
						for(String elem:elements){
							compound_mass.add(p.getAtom_number(elem),e_index,k_index);
							e_index++;
						}
					}
					if(var.getLabel().substring(1).compareTo("energie")==0){
						compound_energy.add(p.getEnergy(m.getValue(var).toString()),0,k_index);
					}
				}
			}
			k_index++;
		}
		System.out.println("[Matrix] generation finished");
	}
	
	public void afficheMatrices(){
		System.out.println("===========");
		System.out.println(" MATRICE DES MASSES ");
		System.out.println("===========");
		compound_mass.affiche();
		System.out.println("===========");
		System.out.println(" MATRICE DES ENERGIES ");
		System.out.println("===========");
		compound_energy.affiche();
	}
	
	public void generateCSV() throws IOException{
		CSVWriter writer1 =new CSVWriter(new FileWriter(System.getProperty("user.dir")+"/csv/masses.csv"), ',');
		System.out.println("[CSV] Writing mass");
		String[] ids=new String[kegg_ids.length+1];
		ids[0]="";
		for(int i=1;i<kegg_ids.length+1;i++){
			ids[i]=kegg_ids[i-1];
		}
		writer1.writeNext(ids);
		String[][] masses=compound_mass.toStringArrayWithElements(elements);
		for(int k=0;k<elements.length;k++){
			writer1.writeNext(masses[k]);
		}
		writer1.close();
		System.out.println("[CSV] mass finished");
		System.out.println("[CSV] Writing energy");
		CSVWriter writer2 =new CSVWriter(new FileWriter(System.getProperty("user.dir")+"/csv/energies.csv"), ',');
		writer2.writeNext(kegg_ids);
		String[][] energies=compound_energy.toStringArray();
		writer2.writeNext(energies[0]);
		writer2.close();
		System.out.println("[CSV] energy finished");
	}
}
