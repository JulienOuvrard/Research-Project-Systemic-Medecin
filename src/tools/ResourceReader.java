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
        return this.getClass().getResource("/kegg-compounds-alberty-ph"+arg+".rdf").toString();
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
