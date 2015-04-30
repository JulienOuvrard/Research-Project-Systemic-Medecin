package tools;

import java.io.IOException;

/**
 * A class helping reading resources
 * @author Julien OUVRARD
 *
 */
public class ResourceReader {
	
	/**
	 * A function that return the path of an rdf file with a specific ph
	 * @param arg The ph we search 
	 * @return Path to the resource targeted
	 * @throws IOException
	 */
	public String readPh(String arg) throws IOException{
		String ph="";
		if(arg.matches("([0-9]*)")){
			ph=arg+"-0";
		}else if(arg.matches("([0-9]*)-([0-9]*)")){
			ph=arg;
		}else{
			String part1=arg.substring(0,1);
			String part2=arg.substring(2,3);
			ph=part1+"-"+part2;
		}
        	return this.getClass().getResource("/kegg-compounds-alberty-ph"+ph+".rdf").toString();
	}
	
	/**
	 * 
	 * @return Path to the resource of the Kegg compounds
	 * @throws IOException
	 */
	public String readCpd() throws IOException{
        	return this.getClass().getResource("/kegg.cpd.rdf").toString();
	}
}
