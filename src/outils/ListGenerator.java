package outils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.Species;

public class ListGenerator {

	private String filename;
	private SBMLReader reader;
	private SBMLDocument document;
	private Model model;
	private Map<String,String> k_ids;
	private Map<String,String> cheb_ids;
	private Map<String,String> hm_ids;
	
	public ListGenerator(String file) throws XMLStreamException, IOException{
		filename=file;
		reader=new SBMLReader();
		document=reader.readSBML(filename);
		model = document.getModel();
		k_ids=new HashMap<String,String>();
		cheb_ids=new HashMap<String,String>();
		hm_ids=new HashMap<String,String>();
	}
	
	public void generate(){
	
		ListOf<Species> species = model.getListOfSpecies();
		
		for(int i = 0; i<species.getChildCount();i++){
			Species spec=species.get(i);
			String name=spec.getName();
			Annotation ann=spec.getAnnotation();
			for(int j= 0;j<ann.getCVTermCount();j++){
				String uri=ann.getCVTerm(j).getResourceURI(0);
				if(uri.contains("kegg.compound")){
					String comp = uri.substring(37);
					k_ids.put(comp, name);
				}
				if(uri.contains("chebi")){
					String comp2 = uri.substring(29);
					cheb_ids.put(comp2, name);
				}
				if(uri.contains("hmdb")){
					String comp3 = uri.substring(28);
					hm_ids.put(comp3, name);
				}
			}
		}
	}

	public Map<String, String> getK_ids() {
		return k_ids;
	}

	public Map<String, String> getCheb_ids() {
		return cheb_ids;
	}

	public Map<String, String> getHm_ids() {
		return hm_ids;
	}
	
	public String[] getArrayFromMap(Map<String,String> m){
		return (String[]) m.keySet().toArray(new String[m.keySet().size()]);
	}
}