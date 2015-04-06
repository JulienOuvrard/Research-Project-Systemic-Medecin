package appli;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import fr.inria.acacia.corese.exceptions.EngineException;
import tools.ListGenerator;
import tools.MatrixGenerator;
import tools.ResourceReader;


public class Main {

	private static String model;
	private static String ph;
	private static String csv_folder;
	
	public static void main(String[] args) throws XMLStreamException {

		Options options = new Options();
		Option helpOpt = new Option("h", "help", false, "print the help");
		Option modelOpt = new Option("m", "model", true, "the SBML file to parse");
		Option phOpt = new Option("p", "ph", true, "the ph on which you want to have informations");
		Option csvOpt = new Option("o", "output", true, "the folder where csv's will be written");
		options.addOption(helpOpt);
		options.addOption(modelOpt);
		options.addOption(phOpt);
		options.addOption(csvOpt);
		
		try {
            CommandLineParser parser = new BasicParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("SysMed", options, true);
                System.exit(0);
            }
            if (cmd.hasOption("m")) {
                model = cmd.getOptionValue("m");
            } else {
                System.out.println("Please specify a sbml model input file !");
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("SysMed",options, true);
                System.exit(0);
            }
            if (cmd.hasOption("p")) {
                ph= cmd.getOptionValue("p");
            } else {
                System.out.println("Please specify a ph !");
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("SysMed",options, true);
                System.exit(0);
            }
            if (cmd.hasOption("o")) {
                csv_folder = cmd.getOptionValue("o");
            } else {
                System.out.println("Please specify an output folder !");
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("SysMed",options,true);
                System.exit(0);
            }

            ResourceReader r=new ResourceReader();
            
            String kegg_energy=r.readPh(ph);
    		String kegg_cpd=r.readCpd();

    		String[] files = {kegg_energy,kegg_cpd};
    		
    		ListGenerator lgen=new ListGenerator(model);
    		lgen.generate();
    		
    		String[] kegg_ids=lgen.getArrayFromMap(lgen.getK_ids());

    		String[] elements={"C","H","N","O","P","S","R"};
    		
    		MatrixGenerator gen=new MatrixGenerator(files,kegg_ids,elements);
    		gen.generate();
    		gen.generate_NoEnergy();
    		gen.generateCSV(model,ph,csv_folder);
    		
		}catch(ParseException ex) {
            System.out.println("Error while parsing the command line " + args.toString());
            ex.printStackTrace();
        } catch (IOException ex) {
        	System.out.println("Error while loading " + model + " file.");
            ex.printStackTrace();
        } catch (EngineException ex) {
        	System.out.println("Error while querying the local sparql engine ");
            ex.printStackTrace();
        }
		
	}

}
