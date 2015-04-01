package appli;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import fr.inria.acacia.corese.exceptions.EngineException;
import outils.ListGenerator;
import outils.MatrixGenerator;


public class Main {

	public static void main(String[] args) throws XMLStreamException, IOException, EngineException {
		String model="resources/MODEL1109130000.xml";
		
		String kegg_energy="resources/kegg-compounds-alberty-ph7-0.rdf";
		String kegg_cpd="resources/kegg.cpd.rdf";
		String[] files = {kegg_energy,kegg_cpd};
		
		ListGenerator lgen=new ListGenerator(model);
		lgen.generate();
		
		String[] kegg_ids=lgen.getArrayFromMap(lgen.getK_ids());

		String[] elements={"C","H","N","O","P","S","R"};
		
		MatrixGenerator gen=new MatrixGenerator(files,kegg_ids,elements);
		gen.generate();
		gen.afficheMatrices();
	}

}
