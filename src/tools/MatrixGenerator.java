package tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.opencsv.CSVWriter;

import tools.Matrix;
import tools.Parser;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.core.Mapping;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import fr.inria.edelweiss.kgram.api.core.Node;

/**
 * A class that generate Matrix by parsing rdf files
 * It uses SPARQL queries
 * 
 * @author Julien OUVRARD
 *
 */
public class MatrixGenerator {
	
	private String[] files;
	private String[] kegg_ids;
	private String[] elements;
	private Matrix compound_mass;
	private Matrix compound_energy;
	private List<String> compound_no_energy;
	
	/**
	 * Constructor of class MatrixGenerator
	 * @param fls The rdf files that will be parsed 
	 * @param k_ids The Kegg ids
	 * @param elts The chemical elements we search
	 */
	public MatrixGenerator(String[] fls,String[] k_ids, String[] elts){
		kegg_ids=k_ids;
		elements=elts;
		compound_mass=new Matrix(elements.length,kegg_ids.length);
		compound_energy=new Matrix(1,kegg_ids.length);
		files=fls;
		compound_no_energy=new ArrayList<String>();
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

	public List<String> getCompound_no_energy() {
		return compound_no_energy;
	}

	/**
	 * A function that make a SPARQL query that search all elements in the rdf files
	 * @param id The id of the Kegg element we search
	 * @param name The name of the element
	 * @param formula The formula of the element
	 * @return The SPARQL query formed with params
	 */
	public String makeQuery(String id, String name, String formula){
		
		String prefix = "prefix bpax: <http://www.biopax.org/release/biopax-level3.owl#>"
				+ "prefix dc: <http://purl.org/dc/elements/1.1/> "
				+ "prefix btr: <http://bio2rdf.org/ns/bio2rdf#> ";

		id = (id=="") ? "?id": "\"cpd:"+id+"\"";
		name = (name=="") ? "?name": "\""+name+"\"";
		formula = (formula=="") ? "?formula": "\""+formula+"\"";
		
		String ret="select ?name ?formula ?energy "
				+ "where { "
				+ "?s  dc:identifier "+id+"; "
				+ "dc:title "+name+". "
				+ "OPTIONAL{?s btr:formula "+formula+". }"
				+ "OPTIONAL{?s bpax:deltaGPrime0 ?energy.} "
				+ "}";
		
		return prefix+ret;
	}
	
	/**
	 * A function that generate the mass and energy matrix
	 * @throws EngineException
	 */
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
					if(var.getLabel().substring(1).compareTo("formula")==0){
						p.explodeFormula(m.getValue(var).toString());
						int e_index=0;
						for(String elem:elements){
							compound_mass.add(p.getAtom_numberByName(elem),e_index,k_index);
							e_index++;
						}
					}
					if(var.getLabel().substring(1).compareTo("energy")==0){
						compound_energy.add(p.getEnergy(m.getValue(var).toString()),0,k_index);
					}
				}
			}
			k_index++;
		}
		System.out.println("[Matrix] generation finished");
	}
	
	/**
	 * A function that make a SPARQL query that search only element without energy
	 * @param id The id of the Kegg element we search
	 * @param name The name of the element
	 * @param formula The formula of the element
	 * @return The SPARQL query formed with params
	 */
	public String makeQuery_NoEnergy(String id, String name, String formula){
		String prefix = "prefix bpax: <http://www.biopax.org/release/biopax-level3.owl#>"
				+ "prefix dc: <http://purl.org/dc/elements/1.1/> "
				+ "prefix btr: <http://bio2rdf.org/ns/bio2rdf#> ";

		id = (id=="") ? "?id": "\"cpd:"+id+"\"";
		name = (name=="") ? "?name": "\""+name+"\"";
		formula = (formula=="") ? "?formula": "\""+formula+"\"";
		
		String ret="select ?name ?formula ?energy "
				+ "where { "
				+ "?s  dc:identifier "+id+". "
				+ "FILTER NOT EXISTS {?s bpax:deltaGPrime0 ?energy}"
				+ "}";
		
		return prefix+ret;
	}
	
	/**
	 * A function that fill a list of element without energy
	 * @throws EngineException
	 */
	public void generate_NoEnergy() throws EngineException{
		System.out.println("[List of NoEnergy] generation started");
		Graph graph= Graph.create(true);
		Load ld=Load.create(graph);
		for(String file: files){
			ld.load(file);
		}
		
		QueryProcess exec = QueryProcess.create(graph);

		for(String s:kegg_ids){
			String query=makeQuery_NoEnergy(s,"","");
			
			Mappings map= exec.query(query);
			for(Mapping m: map){
				for(Node var : m.getQueryNodes()){
					if(var.getLabel()!=null){
						if(!compound_no_energy.contains(s)){
							compound_no_energy.add(s);							
						}
					}
					
				}
			}
		}
		System.out.println("[List of NoEnergy] generation finished");
	}
	
	/**
	 * A function that generate CSV for future use
	 * @param model The model on which we generate csv's
	 * @param ph The ph on which we generate csv's
	 * @param folder The output folder where csv's will be saved
	 * @throws IOException
	 */
	public void generateCSV(String model, String ph, String folder) throws IOException{
		String model_name = FilenameUtils.getBaseName(model);
		File dir = new File(folder+File.separator+model_name+File.separator+ph);
		dir.mkdirs();
		
		System.out.println("[CSV] Writing mass");
		File mass_file= new File(dir, "masses.csv");
		CSVWriter writer1 =new CSVWriter(new FileWriter(mass_file), ',');
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
		File energy_file= new File(dir, "energy.csv");
		CSVWriter writer2 =new CSVWriter(new FileWriter(energy_file), ',');
		writer2.writeNext(kegg_ids);
		String[][] energies=compound_energy.toStringArray();
		writer2.writeNext(energies[0]);
		writer2.close();
		System.out.println("[CSV] energy finished");
		
		System.out.println("[CSV] Writing element without energy");
		File no_energy_file= new File(dir, "elements_no_energies.csv");
		CSVWriter writer3 =new CSVWriter(new FileWriter(no_energy_file), ',');
		String[] no_energies=compound_no_energy.toArray(new String[compound_no_energy.size()]);
		writer3.writeNext(no_energies);
		writer3.close();
		System.out.println("[CSV] element without energy finished");
	}
	
	/**
	 * A printing function for matrix
	 */
	public void printMatrix(){
		System.out.println("===========");
		System.out.println(" MASS MATRIX ");
		System.out.println("===========");
		compound_mass.print();
		System.out.println("===========");
		System.out.println(" ENERGY MATRIX ");
		System.out.println("===========");
		compound_energy.print();
	}
	
	/**
	 * A printing function for list
	 */
	public void afficheListe(){
		System.out.println("===========");
		System.out.println(" ELEMENTS WITHOUT ENERGY ");
		System.out.println("===========");
		System.out.println("--------------");
		System.out.print("|");
		for(String st:compound_no_energy){
			System.out.print(st+"\t |");
		}
		System.out.println("--------------");
	}
}
