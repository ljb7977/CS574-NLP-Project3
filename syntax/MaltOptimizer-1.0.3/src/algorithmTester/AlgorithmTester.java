/**
 * 
 */
package algorithmTester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import optimizer.CoNLLHandler;
import optimizer.LibraryOptionsSetter;
import optimizer.Optimizer;
import optimizer.OptionsGenerator;

/**
 * @author Miguel Ballesteros
 *
 */
public class AlgorithmTester {

	String algorithm;
	double nonprojectivities;
	boolean nonprojectivity;
	private CoNLLHandler ch;
	private String language;
	String trainingCorpus;
	String bestAlgorithm;
	
	double bestResult;
	
	public static String training80;
	String testing80;
	
	private String rootHandling;
	private Double bestLabelLASResult;
	
	/**
	 * Constructor of AlgorithmTesters
	 * 
	 * @param language Language used in the corpus.
	 * @param np percentage of non-projectivity
	 * @param ch CoNLLHandler instance
	 * @param trainingCorpus Training Corpus
	 */
	public AlgorithmTester(String language, double np, CoNLLHandler ch, String trainingCorpus, String rootHandling){
		this.trainingCorpus=trainingCorpus;
		this.language=language;
		bestResult=0.0;
		this.rootHandling=rootHandling;
		
		if (trainingCorpus.contains("/")) {
			StringTokenizer st=new StringTokenizer(trainingCorpus,"/");
			String relPath="";
			while (st.hasMoreTokens()){
				relPath=st.nextToken("/");
			}
			//System.out.println(relPath);
			training80=relPath.replaceAll(".conll","");
			testing80=relPath.replaceAll(".conll","");
			training80+="_train80.conll";
			testing80+="_test20.conll";
		}
		else {
			training80=trainingCorpus.replaceAll(".conll","");
			testing80=trainingCorpus.replaceAll(".conll","");
			training80+="_train80.conll";
			testing80+="_test20.conll";
		}
		this.nonprojectivities=np;
		if (np>25) {
			nonprojectivity=true;
		}
		else {
			nonprojectivity=false;
		}
		this.ch=ch;
	}
	
	public AlgorithmTester(String language, CoNLLHandler ch, String trainingCorpus){
		this.trainingCorpus=trainingCorpus;
		this.language=language;
		bestResult=0.0;
		
		if (trainingCorpus.contains("/")) {
			StringTokenizer st=new StringTokenizer(trainingCorpus,"/");
			String relPath="";
			while (st.hasMoreTokens()){
				relPath=st.nextToken("/");
			}
			//System.out.println(relPath);
			training80=relPath.replaceAll(".conll","");
			testing80=relPath.replaceAll(".conll","");
			training80+="_train80.conll";
			testing80+="_test20.conll";
		}
		else {
			training80=trainingCorpus.replaceAll(".conll","");
			testing80=trainingCorpus.replaceAll(".conll","");
			training80+="_train80.conll";
			testing80+="_test20.conll";
		}
	
		this.ch=ch;
	}
	
	/**
	 * 
	 * @return LAS accuracy for NivreEager algorithm
	 */
	public double executeNivreEagerDefault(String feature){
		
		OptionsGenerator og=new OptionsGenerator(language, training80);
		String out=System.getProperty("user.dir")+"/outNivreEager.conll";
		//Firstly, Execute Nivre Eager
		String optionsCat=og.generateOptionsNivreEager();
		String optionsNivreEager="optionsNivreEager.xml";
		BufferedWriter bwOptionsNivreEager;
		/*String testCorpus=trainingCorpus.replaceAll(".conll","");
		testCorpus+="_test20.conll";
		String trainCorpus=trainingCorpus.replaceAll(".conll","");
		trainCorpus+="_train80.conll";*/
		
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
			//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F NivreEager.xml");
			String s=null;
			String maltPath=Optimizer.maltPath;
			Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
                    p.getErrorStream()));
			while ((s = stdError.readLine()) != null) {}

			// Leemos la salida del comando
			//System.out.println("Ésta es la salida standard del comando:\n");
			while ((s = stdInput.readLine()) != null) {}
				//System.out.println(s);
			
			//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outNivreEager.conll -m parse");
			//String out=System.getProperty("user.dir")+"/outNivreEager.conll";
			Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
			BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
                    p2.getInputStream()));

			BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
                    p2.getErrorStream()));
			while ((s = stdInput2.readLine()) != null) {}
				//System.out.println(s);}
			while ((s = stdError2.readLine()) != null) {}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		double nivreEagerResults=ch.evaluator(out,testing80);
		try {
			Runtime.getRuntime().exec("rm "+out);
			Runtime.getRuntime().exec("rm "+language+"Model.mco");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nivreEagerResults;
	}
		
	/**
	 * 
	 * @return LAS accuracy for NivreEager algorithm
	 */
	public boolean executeCovNonProjEagerDefaultJavaHeapTesting(String feature){
		
		//System.out.println("Testing JAVA heap: "+Optimizer.javaHeapValue);
		
		
		OptionsGenerator og=new OptionsGenerator(language, training80);
		LibraryOptionsSetter lo=LibraryOptionsSetter.getSingleton();
		//Firstly, Execute Nivre Eager
		String optionsCat=og.generateIncOptionsTestingsPhases("lang", "covnonproj", training80, "normal", lo.getLibraryOptions(), "ROOT", "none");
		String optionsNivreEager="optionsCovTest.xml";
		BufferedWriter bwOptionsNivreEager;
		/*String testCorpus=trainingCorpus.replaceAll(".conll","");
		testCorpus+="_test20.conll";
		String trainCorpus=trainingCorpus.replaceAll(".conll","");
		trainCorpus+="_train80.conll";*/
		
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
			//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F NivreEager.xml");
			String s=null;
			String maltPath=Optimizer.maltPath;
			Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
                    p.getErrorStream()));

			// Leemos la salida del comando
			//System.out.println("Ésta es la salida standard del comando:\n");
			while ((s = stdInput.readLine()) != null) {
				//System.out.println(s);
				if (s.contains("Out of memory."))
					return false;
				if (s.contains("Could not reserve enough space for object heap")) {
					return false;
				}
				//System.out.println(s);
			}	
			while ((s = stdError.readLine()) != null) {
				//System.out.println(s);
				if (s.contains("exceeds"))
					return false;
				if (s.contains("Out of memory."))
					return false;
				if (s.contains("Could not reserve enough space for object heap"))
					return false;
				
				//System.out.println(s);
			}	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 
	 * @return LAS accuracy for NivreEager algorithm testing the best root label
	 */
	public double executeNivreEagerRootLabelTest(String feature, String label){
		
		//System.out.println(testing80);
		OptionsGenerator og=new OptionsGenerator(language, training80);
		String out=System.getProperty("user.dir")+"/outNivreEager.conll";
		//Firstly, Execute Nivre Eager
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		String optionsCat=og.generateIncOptionsPrevioGRL(language, "nivreeager", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions());
		String optionsNivreEager="optionsNivreEagerGRL.xml";
		BufferedWriter bwOptionsNivreEager;
		/*String testCorpus=trainingCorpus.replaceAll(".conll","");
		testCorpus+="_test20.conll";
		String trainCorpus=trainingCorpus.replaceAll(".conll","");
		trainCorpus+="_train80.conll";*/
		
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
			//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F NivreEager.xml");
			//System.out.println("--Generating the Model for NivreEager");
			String s=null;
			
			String maltPath=Optimizer.maltPath;
			//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -grl "+label);
			Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -grl "+label);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
                    p.getErrorStream()));

			// Leemos la salida del comando
			//System.out.println("Ésta es la salida standard del comando:\n");
			while ((s = stdInput.readLine()) != null) {}
				//System.out.println(s);
			
			//System.out.println("--Parsing with the NivreEager Model");
			//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outNivreEager.conll -m parse");
			Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
			BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
                    p2.getInputStream()));

			BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
                    p2.getErrorStream()));
			while ((s = stdInput2.readLine()) != null) {}
				//System.out.println(s);}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		double nivreEagerResults=ch.evaluator(out,testing80);
		try {
			Runtime.getRuntime().exec("rm "+out);
			Runtime.getRuntime().exec("rm "+language+"Model.mco");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nivreEagerResults;
	}
	
	/**
	 * 
	 * @return LAS accuracy for NivreEager algorithm testing the best PCR test
	 */
	private Double executeNivreEagerPCRTest(String feature, String option) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, training80);
				String out=System.getProperty("user.dir")+"/outNivreEager.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				String optionsCat=og.generateIncOptionsPrevioPCR(language, "nivreeager", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL);
				String optionsNivreEager="optionsNivreEagerPCR.xml";
				BufferedWriter bwOptionsNivreEager;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
					bwOptionsNivreEager.write(optionsCat);
					bwOptionsNivreEager.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F NivreEager.xml");
					//System.out.println("--Generating the Model for NivreEager");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -pcr "+option);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -pcr "+option);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the NivreEager Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outNivreEager.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double nivreEagerResults=ch.evaluator(out,testing80);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return nivreEagerResults;
	}
	
	
	
	/**
	 * 
	 * @return LAS accuracy for NivreEager algorithm testing using the current configuration
	 */
	public Double executeNivreEager(String feature) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, training80);
				String out=System.getProperty("user.dir")+"/outNivreEager.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "nivreeager", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionsNivreEager="optionsNivreEager.xml";
				BufferedWriter bwOptionsNivreEager;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
					bwOptionsNivreEager.write(optionsCat);
					bwOptionsNivreEager.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F NivreEager.xml");
					//System.out.println("--Generating the Model for NivreEager");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -pcr "+option);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the NivreEager Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outNivreEager.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double nivreEagerResults=ch.evaluator(out,testing80);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return nivreEagerResults;
	}
	
		
	
	/**
	 * 
	 * @return LAS accuracy for NivreEager algorithm testing using the current configuration
	 */
	public Double executeNivreEagerTestTrain(String feature,String trainCorpus, String testCorpus) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
				String out=System.getProperty("user.dir")+"/outNivreEager.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "nivreeager", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionsNivreEager="optionsNivreEager.xml";
				BufferedWriter bwOptionsNivreEager;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
					bwOptionsNivreEager.write(optionsCat);
					bwOptionsNivreEager.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F NivreEager.xml");
					//System.out.println("--Generating the Model for NivreEager");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -pcr "+option);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the NivreEager Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outNivreEager.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double nivreEagerResults=ch.evaluator(testCorpus, out);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return nivreEagerResults;
	}
	
	/**
	 * 
	 * @return LAS accuracy for NivreEager algorithm testing using the current configuration
	 */
	public Double executeNivreEagerTestTrainPPOption(String feature,String trainCorpus, String testCorpus, String ppOption) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
				String out=System.getProperty("user.dir")+"/outNivreEager.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "nivreeager", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionsNivreEager="optionsNivreEager.xml";
				BufferedWriter bwOptionsNivreEager;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
					bwOptionsNivreEager.write(optionsCat);
					bwOptionsNivreEager.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F NivreEager.xml");
					//System.out.println("--Generating the Model for NivreEager");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -pcr "+option);
					//Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -pp "+ppOption);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the NivreEager Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outNivreEager.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double nivreEagerResults=ch.evaluator(testCorpus, out);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return nivreEagerResults;
	}
	
	/**
	 * 
	 * @return LAS accuracy for stackproj algorithm testing using the current configuration
	 */
	public Double executeStackProjective(String feature) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, training80);
				String out=System.getProperty("user.dir")+"/outstackproj.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "stackproj", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionsstackproj="optionsstackproj.xml";
				BufferedWriter bwOptionsstackproj;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionsstackproj = new BufferedWriter(new FileWriter(optionsstackproj));
					bwOptionsstackproj.write(optionsCat);
					bwOptionsstackproj.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionsstackproj+" -F stackproj.xml");
					//System.out.println("--Generating the Model for stackproj");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionsstackproj+" -F "+feature+" -pcr "+option);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsstackproj+" -F "+feature);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the stackproj Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outstackproj.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double stackprojResults=ch.evaluator(out,testing80);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return stackprojResults;
	}
	
	/**
	 * 
	 * @return LAS accuracy for stackproj algorithm testing using the current configuration
	 */
	public Double executeStackProjectiveTestTrain(String feature, String trainCorpus, String testCorpus) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
				String out=System.getProperty("user.dir")+"/outstackproj.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "stackproj", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionsstackproj="optionsstackproj.xml";
				BufferedWriter bwOptionsstackproj;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionsstackproj = new BufferedWriter(new FileWriter(optionsstackproj));
					bwOptionsstackproj.write(optionsCat);
					bwOptionsstackproj.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionsstackproj+" -F stackproj.xml");
					//System.out.println("--Generating the Model for stackproj");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionsstackproj+" -F "+feature+" -pcr "+option);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsstackproj+" -F "+feature);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the stackproj Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outstackproj.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double stackprojResults=ch.evaluator(testCorpus, out);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return stackprojResults;
	}
	
	
	/**
	 * 
	 * @return LAS accuracy for stackproj algorithm testing using the current configuration
	 */
	public Double executeStackProjectiveTestTrainPPOption(String feature, String trainCorpus, String testCorpus, String ppOption) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
				String out=System.getProperty("user.dir")+"/outstackproj.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "stackproj", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionsstackproj="optionsstackproj.xml";
				BufferedWriter bwOptionsstackproj;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionsstackproj = new BufferedWriter(new FileWriter(optionsstackproj));
					bwOptionsstackproj.write(optionsCat);
					bwOptionsstackproj.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionsstackproj+" -F stackproj.xml");
					//System.out.println("--Generating the Model for stackproj");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionsstackproj+" -F "+feature+" -pcr "+option);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsstackproj+" -F "+feature+" -pp "+ppOption);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the stackproj Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outstackproj.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double stackprojResults=ch.evaluator(testCorpus, out);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return stackprojResults;
	}
	
	/**
	 * 
	 * @return LAS accuracy for covproj algorithm testing using the current configuration
	 */
	public Double executeCovingtonProjective(String feature) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, training80);
				String out=System.getProperty("user.dir")+"/outcovproj.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "covproj", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionscovproj="optionscovproj.xml";
				BufferedWriter bwOptionscovproj;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionscovproj = new BufferedWriter(new FileWriter(optionscovproj));
					bwOptionscovproj.write(optionsCat);
					bwOptionscovproj.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F covproj.xml");
					//System.out.println("--Generating the Model for covproj");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pcr "+option);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovproj+" -F "+feature);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the covproj Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovproj.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double covprojResults=ch.evaluator(out,testing80);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return covprojResults;
	}
	
	
	
	/**
	 * 
	 * @return LAS accuracy for covproj algorithm testing using the current configuration
	 */
	public Double executeCovingtonProjectiveTestTrain(String feature, String trainCorpus, String testCorpus) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
				String out=System.getProperty("user.dir")+"/outcovproj.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "covproj", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionscovproj="optionscovproj.xml";
				BufferedWriter bwOptionscovproj;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionscovproj = new BufferedWriter(new FileWriter(optionscovproj));
					bwOptionscovproj.write(optionsCat);
					bwOptionscovproj.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F covproj.xml");
					//System.out.println("--Generating the Model for covproj");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pcr "+option);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovproj+" -F "+feature);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the covproj Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovproj.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double covprojResults=ch.evaluator(testCorpus, out);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return covprojResults;
	}
	
	/**
	 * 
	 * @return LAS accuracy for covproj algorithm testing using the current configuration
	 */
	public Double executeCovingtonProjectiveTestTrainPPOption(String feature, String trainCorpus, String testCorpus, String ppOption) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
				String out=System.getProperty("user.dir")+"/outcovproj.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "covproj", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionscovproj="optionscovproj.xml";
				BufferedWriter bwOptionscovproj;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionscovproj = new BufferedWriter(new FileWriter(optionscovproj));
					bwOptionscovproj.write(optionsCat);
					bwOptionscovproj.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F covproj.xml");
					//System.out.println("--Generating the Model for covproj");fd
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pcr "+option);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pp "+ppOption);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the covproj Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovproj.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double covprojResults=ch.evaluator(testCorpus, out);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return covprojResults;
	}
	
	/**
	 * 
	 * @return LAS accuracy for nivrestandard algorithm testing using the current configuration
	 */
	public Double executeNivreStandard(String feature) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, training80);
				String out=System.getProperty("user.dir")+"/outnivrestandard.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "nivrestandard", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionsnivrestandard="optionsnivrestandard.xml";
				BufferedWriter bwOptionsnivrestandard;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionsnivrestandard = new BufferedWriter(new FileWriter(optionsnivrestandard));
					bwOptionsnivrestandard.write(optionsCat);
					bwOptionsnivrestandard.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionsnivrestandard+" -F nivrestandard.xml");
					//System.out.println("--Generating the Model for nivrestandard");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature+" -pcr "+option);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the nivrestandard Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outnivrestandard.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double nivrestandardResults=ch.evaluator(out,testing80);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return nivrestandardResults;
	}
	
	
	/**
	 * 
	 * @return LAS accuracy for nivrestandard algorithm testing using the current configuration
	 */
	public Double executeNivreStandardTestTrain(String feature, String trainCorpus, String testCorpus) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
				String out=System.getProperty("user.dir")+"/outnivrestandard.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "nivrestandard", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionsnivrestandard="optionsnivrestandard.xml";
				BufferedWriter bwOptionsnivrestandard;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionsnivrestandard = new BufferedWriter(new FileWriter(optionsnivrestandard));
					bwOptionsnivrestandard.write(optionsCat);
					bwOptionsnivrestandard.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionsnivrestandard+" -F nivrestandard.xml");
					//System.out.println("--Generating the Model for nivrestandard");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature+" -pcr "+option);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the nivrestandard Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outnivrestandard.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double nivrestandardResults=ch.evaluator(testCorpus, out);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return nivrestandardResults;
	}
	
	/**
	 * 
	 * @return LAS accuracy for nivrestandard algorithm testing using the current configuration
	 */
	public Double executeNivreStandardTestTrainPPOption(String feature, String trainCorpus, String testCorpus,String ppOption) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
				String out=System.getProperty("user.dir")+"/outnivrestandard.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "nivrestandard", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionsnivrestandard="optionsnivrestandard.xml";
				BufferedWriter bwOptionsnivrestandard;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionsnivrestandard = new BufferedWriter(new FileWriter(optionsnivrestandard));
					bwOptionsnivrestandard.write(optionsCat);
					bwOptionsnivrestandard.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionsnivrestandard+" -F nivrestandard.xml");
					//System.out.println("--Generating the Model for nivrestandard");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature+" -pcr "+option);
					//Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature+" -pp "+ppOption);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the nivrestandard Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outnivrestandard.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double nivrestandardResults=ch.evaluator(testCorpus, out);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return nivrestandardResults;
	}
	
	
	
	 /**
	  *  
	 * @return LAS accuracy for covnonproj algorithm testing using the current configuration
	 */
	public Double executeCovingtonNonProjective(String feature) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, training80);
				String out=System.getProperty("user.dir")+"/outcovnonproj.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "covnonproj", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionscovnonproj="optionscovnonproj.xml";
				BufferedWriter bwOptionscovnonproj;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionscovnonproj = new BufferedWriter(new FileWriter(optionscovnonproj));
					bwOptionscovnonproj.write(optionsCat);
					bwOptionscovnonproj.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionscovnonproj+" -F covnonproj.xml");
					//System.out.println("--Generating the Model for covnonproj");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovnonproj+" -F "+feature);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovnonproj+" -F "+feature);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the covnonproj Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovnonproj.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double covnonprojResults=ch.evaluator(out,testing80);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return covnonprojResults;
	}
	
	
	 /**
	  *  
	 * @return LAS accuracy for covnonproj algorithm testing using the current configuration
	 */
	public Double executeCovingtonNonProjectiveTestTrain(String feature, String trainCorpus, String testCorpus) {
		// TODO Auto-generated method stub
		//System.out.println(testing80);
				OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
				String out=System.getProperty("user.dir")+"/outcovnonproj.conll";
				//Firstly, Execute Nivre Eager
				LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
				//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
				String optionsCat=og.generateIncOptionsTestingsPhases(language, "covnonproj", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
				String optionscovnonproj="optionscovnonproj.xml";
				BufferedWriter bwOptionscovnonproj;
				/*String testCorpus=trainingCorpus.replaceAll(".conll","");
				testCorpus+="_test20.conll";
				String trainCorpus=trainingCorpus.replaceAll(".conll","");
				trainCorpus+="_train80.conll";*/
				
				try {
					bwOptionscovnonproj = new BufferedWriter(new FileWriter(optionscovnonproj));
					bwOptionscovnonproj.write(optionsCat);
					bwOptionscovnonproj.close();
					//System.out.println("java -jar "+maltPath+" -f "+optionscovnonproj+" -F covnonproj.xml");
					//System.out.println("--Generating the Model for covnonproj");
					String s=null;
					
					String maltPath=Optimizer.maltPath;
					//System.out.println("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovnonproj+" -F "+feature);
					Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovnonproj+" -F "+feature);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                    p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                    p.getErrorStream()));

					// Leemos la salida del comando
					//System.out.println("Ésta es la salida standard del comando:\n");
					while ((s = stdInput.readLine()) != null) {}
						//System.out.println(s);
					
					//System.out.println("--Parsing with the covnonproj Model");
					//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovnonproj.conll -m parse");
					Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
					BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
		                    p2.getInputStream()));

					BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
		                    p2.getErrorStream()));
					while ((s = stdInput2.readLine()) != null) {}
						//System.out.println(s);}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				double covnonprojResults=ch.evaluator(testCorpus, out);
				try {
					Runtime.getRuntime().exec("rm "+out);
					Runtime.getRuntime().exec("rm "+language+"Model.mco");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return covnonprojResults;
	}

	
	 /** 
		 * @return LAS accuracy for stackLazy algorithm testing using the current configuration
		 */
		public Double executeStackLazy(String feature) {
			// TODO Auto-generated method stub
			//System.out.println(testing80);
					OptionsGenerator og=new OptionsGenerator(language, training80);
					String out=System.getProperty("user.dir")+"/outstackLazy.conll";
					//Firstly, Execute Nivre Eager
					LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
					//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
					String optionsCat=og.generateIncOptionsTestingsPhases(language, "stacklazy", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
					String optionsstackLazy="optionsstackLazy.xml";
					BufferedWriter bwOptionsstackLazy;
					/*String testCorpus=trainingCorpus.replaceAll(".conll","");
					testCorpus+="_test20.conll";
					String trainCorpus=trainingCorpus.replaceAll(".conll","");
					trainCorpus+="_train80.conll";*/
					
					try {
						bwOptionsstackLazy = new BufferedWriter(new FileWriter(optionsstackLazy));
						bwOptionsstackLazy.write(optionsCat);
						bwOptionsstackLazy.close();
						//System.out.println("java -jar "+maltPath+" -f "+optionsstackLazy+" -F stackLazy.xml");
						//System.out.println("--Generating the Model for stackLazy");
						String s=null;
						
						String maltPath=Optimizer.maltPath;
						//System.out.println("java -jar "+maltPath+" -f "+optionsstackLazy+" -F "+feature+" -pcr "+option);
						Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsstackLazy+" -F "+feature);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
			                    p.getInputStream()));

						BufferedReader stdError = new BufferedReader(new InputStreamReader(
			                    p.getErrorStream()));

						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
							//System.out.println(s);
						
						//System.out.println("--Parsing with the stackLazy Model");
						//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outstackLazy.conll -m parse");
						Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
						BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
			                    p2.getInputStream()));

						BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
			                    p2.getErrorStream()));
						while ((s = stdInput2.readLine()) != null) {}
							//System.out.println(s);}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					double stackLazyResults=ch.evaluator(out,testing80);
					//System.out.println(stackLazyResults);
					try {
						Runtime.getRuntime().exec("rm "+out);
						Runtime.getRuntime().exec("rm "+language+"Model.mco");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						e.printStackTrace();
					}
					return stackLazyResults;
		}
		
		

		 /** 
			 * @return LAS accuracy for stackLazy algorithm testing using the current configuration
			 */
			public Double executeStackLazyTestTrain(String feature, String trainCorpus, String testCorpus) {
				// TODO Auto-generated method stub
				//System.out.println(testing80)es;
						OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
						String out=System.getProperty("user.dir")+"/outstackLazy.conll";
						//Firstly, Execute Nivre Eager
						LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
						//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
						String optionsCat=og.generateIncOptionsTestingsPhases(language, "stacklazy", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
						String optionsstackLazy="optionsstackLazy.xml";
						BufferedWriter bwOptionsstackLazy;
						/*String testCorpus=trainingCorpus.replaceAll(".conll","");
						testCorpus+="_test20.conll";
						String trainCorpus=trainingCorpus.replaceAll(".conll","");
						trainCorpus+="_train80.conll";*/
						
						try {
							bwOptionsstackLazy = new BufferedWriter(new FileWriter(optionsstackLazy));
							bwOptionsstackLazy.write(optionsCat);
							bwOptionsstackLazy.close();
							//System.out.println("java -jar "+maltPath+" -f "+optionsstackLazy+" -F stackLazy.xml");
							//System.out.println("--Generating the Model for stackLazy");
							String s=null;
							
							String maltPath=Optimizer.maltPath;
							//System.out.println("java -jar "+maltPath+" -f "+optionsstackLazy+" -F "+feature+" -pcr "+option);
							Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsstackLazy+" -F "+feature);
							BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				                    p.getInputStream()));

							BufferedReader stdError = new BufferedReader(new InputStreamReader(
				                    p.getErrorStream()));

							// Leemos la salida del comando
							//System.out.println("Ésta es la salida standard del comando:\n");
							while ((s = stdInput.readLine()) != null) {}
								//System.out.println(s);
							
							//System.out.println("--Parsing with the stackLazy Model");
							//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outstackLazy.conll -m parse");
							Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
							BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
				                    p2.getInputStream()));

							BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
				                    p2.getErrorStream()));
							while ((s = stdInput2.readLine()) != null) {}
								//System.out.println(s);}
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						double stackLazyResults=ch.evaluator(testCorpus, out);
						//System.out.println(stackLazyResults);
						try {
							Runtime.getRuntime().exec("rm "+out);
							Runtime.getRuntime().exec("rm "+language+"Model.mco");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							e.printStackTrace();
						}
						return stackLazyResults;
			}
			
			
			/** 
			 * @return LAS accuracy for stackLazy algorithm testing using the current configuration
			 */
			public Double executeStackLazyTestTrainPPOption(String feature, String trainCorpus, String testCorpus, String ppOption) {
				// TODO Auto-generated method stub
				//System.out.println(testing80)es;
						OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
						String out=System.getProperty("user.dir")+"/outstackLazy.conll";
						//Firstly, Execute Nivre Eager
						LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
						//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
						String optionsCat=og.generateIncOptionsTestingsPhases(language, "stacklazy", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
						String optionsstackLazy="optionsstackLazy.xml";
						BufferedWriter bwOptionsstackLazy;
						/*String testCorpus=trainingCorpus.replaceAll(".conll","");
						testCorpus+="_test20.conll";
						String trainCorpus=trainingCorpus.replaceAll(".conll","");
						trainCorpus+="_train80.conll";*/
						
						try {
							bwOptionsstackLazy = new BufferedWriter(new FileWriter(optionsstackLazy));
							bwOptionsstackLazy.write(optionsCat);
							bwOptionsstackLazy.close();
							//System.out.println("java -jar "+maltPath+" -f "+optionsstackLazy+" -F stackLazy.xml");
							//System.out.println("--Generating the Model for stackLazy");
							String s=null;
							
							String maltPath=Optimizer.maltPath;
							//System.out.println("java -jar "+maltPath+" -f "+optionsstackLazy+" -F "+feature+" -pcr "+option);
							Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsstackLazy+" -F "+feature+" -pp "+ppOption);
							BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				                    p.getInputStream()));

							BufferedReader stdError = new BufferedReader(new InputStreamReader(
				                    p.getErrorStream()));

							// Leemos la salida del comando
							//System.out.println("Ésta es la salida standard del comando:\n");
							while ((s = stdInput.readLine()) != null) {}
								//System.out.println(s);
							
							//System.out.println("--Parsing with the stackLazy Model");
							//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outstackLazy.conll -m parse");
							Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
							BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
				                    p2.getInputStream()));

							BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
				                    p2.getErrorStream()));
							while ((s = stdInput2.readLine()) != null) {}
								//System.out.println(s);}
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						double stackLazyResults=ch.evaluator(testCorpus, out);
						//System.out.println(stackLazyResults);
						try {
							Runtime.getRuntime().exec("rm "+out);
							Runtime.getRuntime().exec("rm "+language+"Model.mco");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							e.printStackTrace();
						}
						return stackLazyResults;
			}
	
	
		/** 
		 * @return LAS accuracy for stackEager algorithm testing using the current configuration
		 */
		public Double executestackEager(String feature) {
			// TODO Auto-generated method stub
			//System.out.println(testing80);
					OptionsGenerator og=new OptionsGenerator(language, training80);
					String out=System.getProperty("user.dir")+"/outstackEager.conll";
					//Firstly, Execute Nivre Eager
					LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
					//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
					String optionsCat=og.generateIncOptionsTestingsPhases(language, "stackeager", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
					String optionsstackEager="optionsstackEager.xml";
					BufferedWriter bwOptionsstackEager;
					/*String testCorpus=trainingCorpus.replaceAll(".conll","");
					testCorpus+="_test20.conll";
					String trainCorpus=trainingCorpus.replaceAll(".conll","");
					trainCorpus+="_train80.conll";*/
					
					try {
						bwOptionsstackEager = new BufferedWriter(new FileWriter(optionsstackEager));
						bwOptionsstackEager.write(optionsCat);
						bwOptionsstackEager.close();
						//System.out.println("java -jar "+maltPath+" -f "+optionsstackEager+" -F stackEager.xml");
						//System.out.println("--Generating the Model for stackEager");
						String s=null;
						
						String maltPath=Optimizer.maltPath;
						//System.out.println("java -jar "+maltPath+" -f "+optionsstackEager+" -F "+feature+" -pcr "+option);
						Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsstackEager+" -F "+feature);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
			                    p.getInputStream()));

						BufferedReader stdError = new BufferedReader(new InputStreamReader(
			                    p.getErrorStream()));

						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
							//System.out.println(s);
						
						//System.out.println("--Parsing with the stackEager Model");
						//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outstackEager.conll -m parse");
						Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
						BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
			                    p2.getInputStream()));

						BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
			                    p2.getErrorStream()));
						while ((s = stdInput2.readLine()) != null) {}
							//System.out.println(s);}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					double stackEagerResults=ch.evaluator(out,testing80);
					try {
						Runtime.getRuntime().exec("rm "+out);
						Runtime.getRuntime().exec("rm "+language+"Model.mco");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return stackEagerResults;
		}
		
		
		
		/** 
		 * @return LAS accuracy for stackEager algorithm testing using the current configuration
		 */
		public Double executestackEagerTestTrain(String feature,String trainCorpus, String testCorpus) {
			// TODO Auto-generated method stub
			//System.out.println(testing80);
					OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
					String out=System.getProperty("user.dir")+"/outstackEager.conll";
					//Firstly, Execute Nivre Eager
					LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
					//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
					String optionsCat=og.generateIncOptionsTestingsPhases(language, "stackeager", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
					String optionsstackEager="optionsstackEager.xml";
					BufferedWriter bwOptionsstackEager;
					/*String testCorpus=trainingCorpus.replaceAll(".conll","");
					testCorpus+="_test20.conll";
					String trainCorpus=trainingCorpus.replaceAll(".conll","");
					trainCorpus+="_train80.conll";*/
					
					try {
						bwOptionsstackEager = new BufferedWriter(new FileWriter(optionsstackEager));
						bwOptionsstackEager.write(optionsCat);
						bwOptionsstackEager.close();
						//System.out.println("java -jar "+maltPath+" -f "+optionsstackEager+" -F stackEager.xml");
						//System.out.println("--Generating the Model for stackEager");
						String s=null;
						
						String maltPath=Optimizer.maltPath;
						//System.out.println("java -jar "+maltPath+" -f "+optionsstackEager+" -F "+feature+" -pcr "+option);
						Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsstackEager+" -F "+feature);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
			                    p.getInputStream()));

						BufferedReader stdError = new BufferedReader(new InputStreamReader(
			                    p.getErrorStream()));

						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
							//System.out.println(s);
						
						//System.out.println("--Parsing with the stackEager Model");
						//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outstackEager.conll -m parse");
						Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
						BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
			                    p2.getInputStream()));

						BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
			                    p2.getErrorStream()));
						while ((s = stdInput2.readLine()) != null) {}
							//System.out.println(s);}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					double stackEagerResults=ch.evaluator(testCorpus, out);
					try {
						Runtime.getRuntime().exec("rm "+out);
						Runtime.getRuntime().exec("rm "+language+"Model.mco");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return stackEagerResults;
		}
		
		/** 
		 * @return LAS accuracy for stackEager algorithm testing using the current configuration
		 */
		public Double executestackEagerTestTrainPPOption(String feature,String testCorpus, String trainCorpus, String ppOption) {
			// TODO Auto-generated method stub
			//System.out.println(testing80);
					OptionsGenerator og=new OptionsGenerator(language, trainCorpus);
					String out=System.getProperty("user.dir")+"/outstackEager.conll";
					//Firstly, Execute Nivre Eager
					LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
					//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
					String optionsCat=og.generateIncOptionsTestingsPhases(language, "stackeager", trainCorpus, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
					String optionsstackEager="optionsstackEager.xml";
					BufferedWriter bwOptionsstackEager;
					/*String testCorpus=trainingCorpus.replaceAll(".conll","");
					testCorpus+="_test20.conll";
					String trainCorpus=trainingCorpus.replaceAll(".conll","");
					trainCorpus+="_train80.conll";*/
					
					try {
						bwOptionsstackEager = new BufferedWriter(new FileWriter(optionsstackEager));
						bwOptionsstackEager.write(optionsCat);
						bwOptionsstackEager.close();
						//System.out.println("java -jar "+maltPath+" -f "+optionsstackEager+" -F stackEager.xml");
						//System.out.println("--Generating the Model for stackEager");
						String s=null;
						
						String maltPath=Optimizer.maltPath;
						//System.out.println("java -jar "+maltPath+" -f "+optionsstackEager+" -F "+feature+" -pcr "+option);
						Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsstackEager+" -F "+feature+" -pp "+ppOption);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
			                    p.getInputStream()));

						BufferedReader stdError = new BufferedReader(new InputStreamReader(
			                    p.getErrorStream()));

						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
							//System.out.println(s);
						
						//System.out.println("--Parsing with the stackEager Model");
						//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outstackEager.conll -m parse");
						Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o "+out+" -m parse");
						BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
			                    p2.getInputStream()));

						BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
			                    p2.getErrorStream()));
						while ((s = stdInput2.readLine()) != null) {}
							//System.out.println(s);}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					double stackEagerResults=ch.evaluator(testCorpus, out);
					try {
						Runtime.getRuntime().exec("rm "+out);
						Runtime.getRuntime().exec("rm "+language+"Model.mco");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return stackEagerResults;
		}
		
		/**
		 * 
		 * @return LAS accuracy for NivreEager algorithm testing using the current configuration
		 */
		public Double executeNivreEagerPPOption(String feature, String ppOption) {
			// TODO Auto-generated method stub
			//System.out.println(testing80);
					OptionsGenerator og=new OptionsGenerator(language, training80);
					String out=System.getProperty("user.dir")+"/outNivreEager.conll";
					//Firstly, Execute Nivre Eager
					LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
					//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
					String optionsCat=og.generateIncOptionsTestingsPhases(language, "nivreeager", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
					String optionsNivreEager="optionsNivreEager.xml";
					BufferedWriter bwOptionsNivreEager;
					/*String testCorpus=trainingCorpus.replaceAll(".conll","");
					testCorpus+="_test20.conll";
					String trainCorpus=trainingCorpus.replaceAll(".conll","");
					trainCorpus+="_train80.conll";*/
					
					try {
						bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
						bwOptionsNivreEager.write(optionsCat);
						bwOptionsNivreEager.close();
						//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F NivreEager.xml");
						//System.out.println("--Generating the Model for NivreEager");
						String s=null;
						
						String maltPath=Optimizer.maltPath;
						//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -pcr "+option);
						Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -pp "+ppOption);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
			                    p.getInputStream()));

						BufferedReader stdError = new BufferedReader(new InputStreamReader(
			                    p.getErrorStream()));

						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
							//System.out.println(s);
						
						//System.out.println("--Parsing with the NivreEager Model");
						//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outNivreEager.conll -m parse");
						Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
						BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
			                    p2.getInputStream()));

						BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
			                    p2.getErrorStream()));
						while ((s = stdInput2.readLine()) != null) {}
							//System.out.println(s);}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					double nivreEagerResults=ch.evaluator(out,testing80);
					try {
						Runtime.getRuntime().exec("rm "+out);
						Runtime.getRuntime().exec("rm "+language+"Model.mco");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return nivreEagerResults;
		}
		
		/**
		 * 
		 * @return LAS accuracy for NivreEager algorithm testing using the current configuration
		 */
		public Double executeNivreEagerROption(String feature, String rOption) {
			// TODO Auto-generated method stub
			//System.out.println(testing80);
					OptionsGenerator og=new OptionsGenerator(language, training80);
					String out=System.getProperty("user.dir")+"/outNivreEager.conll";
					//Firstly, Execute Nivre Eager
					LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
					//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
					String optionsCat=og.generateIncOptionsTestingsPhasesb(language, "nivreeager", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption, Optimizer.ppOption);
					String optionsNivreEager="optionsNivreEager.xml";
					BufferedWriter bwOptionsNivreEager;
					/*String testCorpus=trainingCorpus.replaceAll(".conll","");
					testCorpus+="_test20.conll";
					String trainCorpus=trainingCorpus.replaceAll(".conll","");
					trainCorpus+="_train80.conll";*/
					
					try {
						bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
						bwOptionsNivreEager.write(optionsCat);
						bwOptionsNivreEager.close();
						//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F NivreEager.xml");
						//System.out.println("--Generating the Model for NivreEager");
						String s=null;
						
						String maltPath=Optimizer.maltPath;
						//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -pcr "+option);
						Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -r "+rOption);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
			                    p.getInputStream()));

						BufferedReader stdError = new BufferedReader(new InputStreamReader(
			                    p.getErrorStream()));

						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
							//System.out.println(s);
						
						//System.out.println("--Parsing with the NivreEager Model");
						//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outNivreEager.conll -m parse");
						Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
						BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
			                    p2.getInputStream()));

						BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
			                    p2.getErrorStream()));
						while ((s = stdInput2.readLine()) != null) {}
							//System.out.println(s);}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					double nivreEagerResults=ch.evaluator(out,testing80);
					try {
						Runtime.getRuntime().exec("rm "+out);
						Runtime.getRuntime().exec("rm "+language+"Model.mco");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return nivreEagerResults;
		}
		
		/**
		 * 
		 * @return LAS accuracy for NivreEager algorithm testing using the current configuration
		 */
		public Double executeNivreEagerROption17(String feature, String rOption) {
			// TODO Auto-generated method stub
			//System.out.println(testing80);
					OptionsGenerator og=new OptionsGenerator(language, training80);
					String out=System.getProperty("user.dir")+"/outNivreEager.conll";
					//Firstly, Execute Nivre Eager
					LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
					//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
					String optionsCat=og.generateIncOptionsTestingsPhasesb(language, "nivreeager", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption, Optimizer.ppOption);
					String optionsNivreEager="optionsNivreEager.xml";
					BufferedWriter bwOptionsNivreEager;
					/*String testCorpus=trainingCorpus.replaceAll(".conll","");
					testCorpus+="_test20.conll";
					String trainCorpus=trainingCorpus.replaceAll(".conll","");
					trainCorpus+="_train80.conll";*/
					
					try {
						bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
						bwOptionsNivreEager.write(optionsCat);
						bwOptionsNivreEager.close();
						//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F NivreEager.xml");
						//System.out.println("--Generating the Model for NivreEager");
						String s=null;
						
						String maltPath=Optimizer.maltPath;
						//System.out.println("java -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" -pcr "+option);
						//System.out.println("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature+" "+rOption);
						
						//StringTokenizer st=new StringTokenizer(rOption);
						
						//System.out.println(optionsNivreEager);
						//System.out.println("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature +" "+rOption);
						Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsNivreEager+" -F "+feature +" "+rOption);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
			                    p.getInputStream()));

						BufferedReader stdError = new BufferedReader(new InputStreamReader(
			                    p.getErrorStream()));

						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
							//System.out.println(s);
						
						//System.out.println("--Parsing with the NivreEager Model");
						//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outNivreEager.conll -m parse");
						Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
						BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
			                    p2.getInputStream()));

						BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
			                    p2.getErrorStream()));
						while ((s = stdInput2.readLine()) != null) {}
							//System.out.println(s);}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					double nivreEagerResults=ch.evaluator(out,testing80);
					try {
						Runtime.getRuntime().exec("rm "+out);
						Runtime.getRuntime().exec("rm "+language+"Model.mco");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return nivreEagerResults;
		}
		
		/**
		 * 
		 * @return LAS accuracy for covproj algorithm testing using the current configuration
		 */
		public Double executecovprojPPOption(String feature, String ppOption) {
			// TODO Auto-generated method stub
			//System.out.println(testing80);
					OptionsGenerator og=new OptionsGenerator(language, training80);
					String out=System.getProperty("user.dir")+"/outcovproj.conll";
					//Firstly, Execute Nivre Eager
					LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
					//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
					String optionsCat=og.generateIncOptionsTestingsPhases(language, "covproj", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
					String optionscovproj="optionscovproj.xml";
					BufferedWriter bwOptionscovproj;
					/*String testCorpus=trainingCorpus.replaceAll(".conll","");
					testCorpus+="_test20.conll";
					String trainCorpus=trainingCorpus.replaceAll(".conll","");
					trainCorpus+="_train80.conll";*/
					
					try {
						bwOptionscovproj = new BufferedWriter(new FileWriter(optionscovproj));
						bwOptionscovproj.write(optionsCat);
						bwOptionscovproj.close();
						//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F covproj.xml");
						//System.out.println("--Generating the Model for covproj");
						String s=null;
						
						String maltPath=Optimizer.maltPath;
						//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pcr "+option);
						Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pp "+ppOption);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
			                    p.getInputStream()));

						BufferedReader stdError = new BufferedReader(new InputStreamReader(
			                    p.getErrorStream()));

						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
							//System.out.println(s);
						
						//System.out.println("--Parsing with the covproj Model");
						//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovproj.conll -m parse");
						Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
						BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
			                    p2.getInputStream()));

						BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
			                    p2.getErrorStream()));
						while ((s = stdInput2.readLine()) != null) {}
							//System.out.println(s);}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					double covprojResults=ch.evaluator(out,testing80);
					try {
						Runtime.getRuntime().exec("rm "+out);
						Runtime.getRuntime().exec("rm "+language+"Model.mco");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return covprojResults;
		}
		
		/**
		 * 
		 * @return LAS accuracy for stackproj algorithm testing using the current configuration
		 */
		public Double executestackprojPPOption(String feature, String ppOption) {
			// TODO Auto-generated method stub
			//System.out.println(testing80);
					OptionsGenerator og=new OptionsGenerator(language, training80);
					String out=System.getProperty("user.dir")+"/outstackproj.conll";
					//Firstly, Execute Nivre Eager
					LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
					//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
					String optionsCat=og.generateIncOptionsTestingsPhases(language, "stackproj", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
					String optionsstackproj="optionsstackproj.xml";
					BufferedWriter bwOptionsstackproj;
					/*String testCorpus=trainingCorpus.replaceAll(".conll","");
					testCorpus+="_test20.conll";
					String trainCorpus=trainingCorpus.replaceAll(".conll","");
					trainCorpus+="_train80.conll";*/
					
					try {
						bwOptionsstackproj = new BufferedWriter(new FileWriter(optionsstackproj));
						bwOptionsstackproj.write(optionsCat);
						bwOptionsstackproj.close();
						//System.out.println("java -jar "+maltPath+" -f "+optionsstackproj+" -F stackproj.xml");
						//System.out.println("--Generating the Model for stackproj");
						String s=null;
						
						String maltPath=Optimizer.maltPath;
						//System.out.println("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsstackproj+" -F "+feature+" -pp "+ppOption);
						Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsstackproj+" -F "+feature+" -pp "+ppOption);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
			                    p.getInputStream()));

						BufferedReader stdError = new BufferedReader(new InputStreamReader(
			                    p.getErrorStream()));

						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
							//System.out.println(s);
						
						//System.out.println("--Parsing with the stackproj Model");
						//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
						Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
						BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
			                    p2.getInputStream()));

						BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
			                    p2.getErrorStream()));
						while ((s = stdInput2.readLine()) != null) {}
							//System.out.println(s);}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					double stackprojResults=ch.evaluator(out,testing80);
					try {
						Runtime.getRuntime().exec("rm "+out);
						Runtime.getRuntime().exec("rm "+language+"Model.mco");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return stackprojResults;
		}
		
		/**
		 * 
		 * @return LAS accuracy for nivrestandard algorithm testing using the current configuration
		 */
		public Double executenivrestandardPPOption(String feature, String ppOption) {
			// TODO Auto-generated method stub
			//System.out.println(testing80);
					OptionsGenerator og=new OptionsGenerator(language, training80);
					String out=System.getProperty("user.dir")+"/outnivrestandard.conll";
					//Firstly, Execute Nivre Eager
					LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
					//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
					String optionsCat=og.generateIncOptionsTestingsPhases(language, "nivrestandard", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
					String optionsnivrestandard="optionsnivrestandard.xml";
					BufferedWriter bwOptionsnivrestandard;
					/*String testCorpus=trainingCorpus.replaceAll(".conll","");
					testCorpus+="_test20.conll";
					String trainCorpus=trainingCorpus.replaceAll(".conll","");
					trainCorpus+="_train80.conll";*/
					
					try {
						bwOptionsnivrestandard = new BufferedWriter(new FileWriter(optionsnivrestandard));
						bwOptionsnivrestandard.write(optionsCat);
						bwOptionsnivrestandard.close();
						//System.out.println("java -jar "+maltPath+" -f "+optionsnivrestandard+" -F nivrestandard.xml");
						//System.out.println("--Generating the Model for nivrestandard");
						String s=null;
						
						String maltPath=Optimizer.maltPath;
						//System.out.println("java -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature+" -pcr "+option);
						Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature+" -pp "+ppOption);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
			                    p.getInputStream()));

						BufferedReader stdError = new BufferedReader(new InputStreamReader(
			                    p.getErrorStream()));

						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
							//System.out.println(s);
						
						//System.out.println("--Parsing with the nivrestandard Model");
						//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outnivrestandard.conll -m parse");
						Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
						BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
			                    p2.getInputStream()));

						BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
			                    p2.getErrorStream()));
						while ((s = stdInput2.readLine()) != null) {}
							//System.out.println(s);}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					double nivrestandardResults=ch.evaluator(out,testing80);
					try {
						Runtime.getRuntime().exec("rm "+out);
						Runtime.getRuntime().exec("rm "+language+"Model.mco");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return nivrestandardResults;
		}
		
		
		/**
		 * 
		 * @return LAS accuracy for nivrestandard algorithm testing using the current configuration
		 */
		public Double executenivrestandardROption(String feature, String rOption) {
			// TODO Auto-generated method stub
			//System.out.println(testing80);
					OptionsGenerator og=new OptionsGenerator(language, training80);
					String out=System.getProperty("user.dir")+"/outnivrestandard.conll";
					//Firstly, Execute Nivre Eager
					LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
					//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
					String optionsCat=og.generateIncOptionsTestingsPhasesb(language, "nivrestandard", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption, Optimizer.ppOption);
					String optionsnivrestandard="optionsnivrestandard.xml";
					BufferedWriter bwOptionsnivrestandard;
					/*String testCorpus=trainingCorpus.replaceAll(".conll","");
					testCorpus+="_test20.conll";
					String trainCorpus=trainingCorpus.replaceAll(".conll","");
					trainCorpus+="_train80.conll";*/
					
					try {
						bwOptionsnivrestandard = new BufferedWriter(new FileWriter(optionsnivrestandard));
						bwOptionsnivrestandard.write(optionsCat);
						bwOptionsnivrestandard.close();
						//System.out.println("java -jar "+maltPath+" -f "+optionsnivrestandard+" -F nivrestandard.xml");
						//System.out.println("--Generating the Model for nivrestandard");
						String s=null;
						
						String maltPath=Optimizer.maltPath;
						//System.out.println("java -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature+" -pcr "+option);
						Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature+" -r "+rOption);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
			                    p.getInputStream()));

						BufferedReader stdError = new BufferedReader(new InputStreamReader(
			                    p.getErrorStream()));

						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
							//System.out.println(s);
						
						//System.out.println("--Parsing with the nivrestandard Model");
						//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outnivrestandard.conll -m parse");
						Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
						BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
			                    p2.getInputStream()));

						BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
			                    p2.getErrorStream()));
						while ((s = stdInput2.readLine()) != null) {}
							//System.out.println(s);}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					double nivrestandardResults=ch.evaluator(out,testing80);
					try {
						Runtime.getRuntime().exec("rm "+out);
						Runtime.getRuntime().exec("rm "+language+"Model.mco");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return nivrestandardResults;
		}
		
		/**
		 * 
		 * @return LAS accuracy for nivrestandard algorithm testing using the current configuration
		 */
		public Double executenivrestandardROption17(String feature, String rOption) {
			// TODO Auto-generated method stub
			//System.out.println(testing80);
					OptionsGenerator og=new OptionsGenerator(language, training80);
					String out=System.getProperty("user.dir")+"/outnivrestandard.conll";
					//Firstly, Execute Nivre Eager
					LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
					//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
					String optionsCat=og.generateIncOptionsTestingsPhasesb(language, "nivrestandard", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption, Optimizer.ppOption);
					String optionsnivrestandard="optionsnivrestandard.xml";
					BufferedWriter bwOptionsnivrestandard;
					/*String testCorpus=trainingCorpus.replaceAll(".conll","");
					testCorpus+="_test20.conll";
					String trainCorpus=trainingCorpus.replaceAll(".conll","");
					trainCorpus+="_train80.conll";*/
					
					try {
						bwOptionsnivrestandard = new BufferedWriter(new FileWriter(optionsnivrestandard));
						bwOptionsnivrestandard.write(optionsCat);
						bwOptionsnivrestandard.close();
						//System.out.println("java -jar "+maltPath+" -f "+optionsnivrestandard+" -F nivrestandard.xml");
						//System.out.println("--Generating the Model for nivrestandard");
						String s=null;
						
						String maltPath=Optimizer.maltPath;
						//System.out.println("java -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature+" -pcr "+option);
						Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionsnivrestandard+" -F "+feature+" "+rOption);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
			                    p.getInputStream()));

						BufferedReader stdError = new BufferedReader(new InputStreamReader(
			                    p.getErrorStream()));

						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
							//System.out.println(s);
						
						//System.out.println("--Parsing with the nivrestandard Model");
						//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outnivrestandard.conll -m parse");
						Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
						BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
			                    p2.getInputStream()));

						BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
			                    p2.getErrorStream()));
						while ((s = stdInput2.readLine()) != null) {}
							//System.out.println(s);}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					double nivrestandardResults=ch.evaluator(out,testing80);
					try {
						Runtime.getRuntime().exec("rm "+out);
						Runtime.getRuntime().exec("rm "+language+"Model.mco");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return nivrestandardResults;
		}
		
		
		
		
		
		
	/**
	 * This method infers the best projectivity algorithm
	 * @return the best projectivity algorithm
	 */
	public String executeProjectivity(){
		/*HashMap<String, Double> results=new HashMap<String, Double>();
		results.put("NivreEager", executeNivreEager());
		results.put("StackLazy", executeStackLazy());*/
		//ch.generateDivision8020();
		System.out.println("--------------------------------------");
		System.out.println("The system is going to check with the best projectivity algorithms");
		
		double nivreEager=executeNivreEagerDefault("NivreEager.xml");
		String ne=""+nivreEager;
		ne=ne.substring(0, 5);
		System.out.println("NivreEager LAS= "+ne);
		
		double stackLazy=executeStackLazy("StackSwap.xml");
		
		String sl=""+stackLazy;
		sl=sl.substring(0, 5);
		System.out.println("StackLazy LAS= "+sl);
		
		try {
			Runtime.getRuntime().exec("rm *.mco");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (nivreEager>=stackLazy){
			bestResult=nivreEager;
			return "nivreeager";
		}
		else {
			bestResult=stackLazy;
			return "stacklazy";
		}
		
	}
	
	
	public double getBestResult() {
		return bestResult;
	}

	/**
	* This method infers the best non-oprojectivity algorithm
	 * @return the best non-projectivity algorithm
	 */
	public String executeNonProjectivity(){
		
		return null;
	}

	/**
	 * This method tries to find the best ROOT label configuration
	 * @param threeLabels
	 * @return best label
	 */
	public String executeLabelTest(ArrayList<String> threeLabels) {
		// TODO Auto-generated method stub
		Iterator<String> it=threeLabels.iterator();
		Double maxD=0.0;
		String max="";
		while(it.hasNext()) {
			String lab=it.next();
			System.out.println(lab);
			Double d=executeNivreEagerRootLabelTest("NivreEager.xml",lab);
			//System.out.println(lab+"("+d+")");
			if (d>maxD){
				maxD=d;
				max=lab;
				//System.out.println("New best label:" +max);
			}
		}
		bestLabelLASResult=maxD;
		//System.out.println("("+maxD+")");
		return max;
	}

	public Double getBestLabelLASResult() {
		return bestLabelLASResult;
	}

	/**
	 * This method tries to find the best PCR configuration
	 * @return best config value
	 */
	public String executePCRTest() {
		// TODO Auto-generated method stub
		ArrayList<String> options=new ArrayList<String>();
		//-p none, -p left, -p right, -p head
		options.add("none");options.add("left");options.add("right");options.add("head");
		Iterator<String> it=options.iterator();
		Double maxD=0.0;
		String max="";
		while(it.hasNext()) {
			String lab=it.next();
			Double d=executeNivreEagerPCRTest("NivreEager.xml",lab);
			//System.out.println(lab+"("+d+")");
			if (d>maxD){
				maxD=d;
				max=lab;
				//System.out.println("New best label:" +max);
			}
			
		}
		bestLabelLASResult=maxD;
		//System.out.println("("+maxD+")");
		return max;
	}
	
	/**
	 * This method tries to find the best PP configuration
	 * @return best config value
	 */
	public String executePPTest(String algorithm) {
		// TODO Auto-generated method stub
		ArrayList<String> options=new ArrayList<String>();
		//-p none, -p left, -p right, -p head
		options.add("baseline");options.add("head");options.add("path");options.add("head+path");
		Iterator<String> it=options.iterator();
		Double maxD=Optimizer.bestResult;
		String max="head";
		while(it.hasNext()) {
			String lab=it.next();
			Double d=0.0;
			if (algorithm.equals("nivreeager"))
				d=this.executeNivreEagerPPOption("NivreEager.xml", lab);
			if (algorithm.equals("nivrestandard"))
				d=this.executenivrestandardPPOption("NivreStandard.xml", lab);
			if (algorithm.equals("covproj"))
				d=this.executecovprojPPOption("CovingtonProjective.xml", lab);
			if (algorithm.equals("stackproj"))
				d=this.executestackprojPPOption("StackProjective.xml", lab);
			//System.out.println(lab+"("+d+")");
			if (d>maxD){
				maxD=d;
				max=lab;
				System.out.println("New best pp option:" +max);
				//
				Double difference=maxD-Optimizer.bestResult;
				Optimizer.bestResult=maxD;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			
				String s=""+Optimizer.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+Optimizer.evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		}
		bestLabelLASResult=maxD;
		//System.out.println("("+maxD+")");
		return max;
	}
	
	/**
	 * This method tries to find the best RH configuration
	 * @return best config value
	 */
	public String executeRootHandlingTest(String algorithm) {
		// TODO Auto-generated method stub
		ArrayList<String> options=new ArrayList<String>();
		//-p none, -p left, -p right, -p head
		
		options.add("normal");options.add("strict");options.add("relaxed");
		
		Iterator<String> it=options.iterator();
		Double maxD=Optimizer.bestResult;
		String max="normal";
		while(it.hasNext()) {
			String lab=it.next();
			Double d=0.0;
			if (algorithm.equals("nivreeager"))
				d=this.executeNivreEagerROption("NivreEager.xml", lab);
			if (algorithm.equals("nivrestandard"))
				d=this.executenivrestandardROption("NivreStandard.xml", lab);
			if (d>maxD){
				maxD=d;
				max=lab;
				System.out.println("New best root handling option:" +max);
				//
				Double difference=maxD-Optimizer.bestResult;
				Optimizer.bestResult=maxD;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			
				String s=""+Optimizer.bestResult;
				if (s.length()==4);
				System.out.println("Incremental LAS improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			}
			
		}
		bestLabelLASResult=maxD;
		System.out.println("("+max+")");
		return max;
	}
	
	
	/**
	 * This method tries to find the best RH configuration
	 * @return best config value
	 */
	public String executeRootHandlingTestNivre17(String algorithm) {
		// TODO Auto-generated method stub
		ArrayList<String> options=new ArrayList<String>();
		//-p none, -p left, -p right, -p head
		
		options.add("-nr false -ne false");options.add("-nr true -ne true");options.add("-nr false -ne true");
		
		Iterator<String> it=options.iterator();
		Double maxD=Optimizer.bestResult;
		String max="normal";
		
		int optionCounter=0;
		
		while(it.hasNext()) {
			String lab=it.next();
			optionCounter++;
			Double d=0.0;
			if (algorithm.equals("nivreeager"))
				d=this.executeNivreEagerROption17("NivreEager.xml", lab);
				//d=this.executeNivreEagerROption("NivreEager.xml", lab);
			if (algorithm.equals("nivrestandard"))
				d=this.executenivrestandardROption17("NivreStandard.xml", lab);
				//d=this.executenivrestandardROption("NivreStandard.xml", lab);
			System.out.println(d);
			if (d>maxD){
				maxD=d;
				max=lab;
				
				if (optionCounter==1) {
					Optimizer.allow_rootNiv=false;
					Optimizer.allow_reduceNiv=false;
					//System.out.println("New best root handling option:");
					//System.out.println("\t"+"allow_root = false");
					
				}
				if (optionCounter==2) {
					Optimizer.allow_rootNiv=true;
					Optimizer.allow_reduceNiv=true;
					//System.out.println("New best root handling option:");
					//System.out.println("\t"+"allow_reduce = true");
					
				}
				if (optionCounter==3) {
					Optimizer.allow_rootNiv=false;
					Optimizer.allow_reduceNiv=true;
					//System.out.println("New best root handling options:");
					//System.out.println("\t"+"allow_root = false");
					//System.out.println("\t"+"allow_reduce = true");
					
				}
				//
				Double difference=maxD-Optimizer.bestResult;
				Optimizer.bestResult=maxD;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			
				String s=""+Optimizer.bestResult;
				if (s.length()==4);
				System.out.println("Incremental LAS improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			}
			
		}
		bestLabelLASResult=maxD;
		System.out.println("("+max+")");
		return max;
	}

	public Double executeDefault() {
		// TODO Auto-generated method stub
		double d=executeNivreEagerDefault("NivreEager.xml");
		return new Double(d);
	}

	
	public Double executeCovingtonProjectivePPAllowShiftAllowRoot(String feature, String ppOption, boolean allow_shift, boolean allow_root){
		// TODO Auto-generated method stub
					//System.out.println(testing80);
							OptionsGenerator og=new OptionsGenerator(language, training80);
							String out=System.getProperty("user.dir")+"/outcovproj.conll";
							//Firstly, Execute Nivre Eager
							LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
							//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
							String optionsCat=og.generateIncOptionsTestingsPhases(language, "covproj", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
							String optionscovproj="optionscovproj.xml";
							BufferedWriter bwOptionscovproj;
							/*String testCorpus=trainingCorpus.replaceAll(".conll","");
							testCorpus+="_test20.conll";
							String trainCorpus=trainingCorpus.replaceAll(".conll","");
							trainCorpus+="_train80.conll";*/
							
							try {
								bwOptionscovproj = new BufferedWriter(new FileWriter(optionscovproj));
								bwOptionscovproj.write(optionsCat);
								bwOptionscovproj.close();
								//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F covproj.xml");
								//System.out.println("--Generating the Model for covproj");
								String s=null;
								
								String maltPath=Optimizer.maltPath;
								//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pcr "+option);
								Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pp "+ppOption +" -cs "+allow_shift + " -cr "+allow_root);
								BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					                    p.getInputStream()));

								BufferedReader stdError = new BufferedReader(new InputStreamReader(
					                    p.getErrorStream()));

								// Leemos la salida del comando
								//System.out.println("Ésta es la salida standard del comando:\n");
								while ((s = stdInput.readLine()) != null) {}
									//System.out.println(s);
								
								//System.out.println("--Parsing with the covproj Model");
								//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovproj.conll -m parse");
								Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
								BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
					                    p2.getInputStream()));

								BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
					                    p2.getErrorStream()));
								while ((s = stdInput2.readLine()) != null) {}
									//System.out.println(s);}
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
							
							double covprojResults=ch.evaluator(out,testing80);
							try {
								Runtime.getRuntime().exec("rm "+out);
								Runtime.getRuntime().exec("rm "+language+"Model.mco");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return covprojResults;
	}
	
	public Double executeCovingtonProjectivePPAllowShiftAllowRootTestTrain(String feature, String ppOption, boolean allow_shift, boolean allow_root, String train, String test){
		// TODO Auto-generated method stub
					//System.out.println(testing80);
							OptionsGenerator og=new OptionsGenerator(language, train);
							String out=System.getProperty("user.dir")+"/outcovproj.conll";
							//Firstly, Execute Nivre Eager
							LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
							//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
							String optionsCat=og.generateIncOptionsTestingsPhases(language, "covproj", train, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
							String optionscovproj="optionscovproj.xml";
							BufferedWriter bwOptionscovproj;
							/*String testCorpus=trainingCorpus.replaceAll(".conll","");
							testCorpus+="_test20.conll";
							String trainCorpus=trainingCorpus.replaceAll(".conll","");
							trainCorpus+="_train80.conll";*/
							
							try {
								bwOptionscovproj = new BufferedWriter(new FileWriter(optionscovproj));
								bwOptionscovproj.write(optionsCat);
								bwOptionscovproj.close();
								//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F covproj.xml");
								//System.out.println("--Generating the Model for covproj");
								String s=null;
								
								String maltPath=Optimizer.maltPath;
								//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pcr "+option);
								Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pp "+ppOption +" -cs "+allow_shift + " -cr "+allow_root);
								BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					                    p.getInputStream()));

								BufferedReader stdError = new BufferedReader(new InputStreamReader(
					                    p.getErrorStream()));

								// Leemos la salida del comando
								//System.out.println("Ésta es la salida standard del comando:\n");
								while ((s = stdInput.readLine()) != null) {}
									//System.out.println(s);
								
								//System.out.println("--Parsing with the covproj Model");
								//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovproj.conll -m parse");
								Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+test+" -o "+out+" -m parse");
								BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
					                    p2.getInputStream()));

								BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
					                    p2.getErrorStream()));
								while ((s = stdInput2.readLine()) != null) {}
									//System.out.println(s);}
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
							
							double covprojResults=ch.evaluator(out,testing80);
							try {
								Runtime.getRuntime().exec("rm "+out);
								Runtime.getRuntime().exec("rm "+language+"Model.mco");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return covprojResults;
	}

	public Double executeCovingtonProjectiveAllowShiftAllowRoot(String feature, boolean allow_shift, boolean allow_root) {
		// TODO Auto-generated method stub
				//System.out.println(testing80);
						OptionsGenerator og=new OptionsGenerator(language, training80);
						String out=System.getProperty("user.dir")+"/outcovproj.conll";
						//Firstly, Execute Nivre Eager
						LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
						//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
						String optionsCat=og.generateIncOptionsTestingsPhases(language, "covproj", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
						String optionscovproj="optionscovproj.xml";
						BufferedWriter bwOptionscovproj;
						/*String testCorpus=trainingCorpus.replaceAll(".conll","");
						testCorpus+="_test20.conll";
						String trainCorpus=trainingCorpus.replaceAll(".conll","");
						trainCorpus+="_train80.conll";*/
						
						try {
							bwOptionscovproj = new BufferedWriter(new FileWriter(optionscovproj));
							bwOptionscovproj.write(optionsCat);
							bwOptionscovproj.close();
							//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F covproj.xml");
							//System.out.println("--Generating the Model for covproj");
							String s=null;
							
							String maltPath=Optimizer.maltPath;
							//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pcr "+option);
							Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovproj+" -F "+feature +" -cs "+allow_shift + " -cr "+allow_root);
							BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				                    p.getInputStream()));

							BufferedReader stdError = new BufferedReader(new InputStreamReader(
				                    p.getErrorStream()));

							// Leemos la salida del comando
							//System.out.println("Ésta es la salida standard del comando:\n");
							while ((s = stdInput.readLine()) != null) {}
								//System.out.println(s);
							
							//System.out.println("--Parsing with the covproj Model");
							//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovproj.conll -m parse");
							Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
							BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
				                    p2.getInputStream()));

							BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
				                    p2.getErrorStream()));
							while ((s = stdInput2.readLine()) != null) {}
								//System.out.println(s);}
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						double covprojResults=ch.evaluator(out,testing80);
						try {
							Runtime.getRuntime().exec("rm "+out);
							Runtime.getRuntime().exec("rm "+language+"Model.mco");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return covprojResults;
	}
	
	
	public Double executeCovingtonProjectiveAllowShiftAllowRootTestTrain(String feature, boolean allow_shift, boolean allow_root,String train, String test) {
		// TODO Auto-generated method stub
				//System.out.println(testing80);
						OptionsGenerator og=new OptionsGenerator(language, train);
						String out=System.getProperty("user.dir")+"/outcovproj.conll";
						//Firstly, Execute Nivre Eager
						LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
						//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
						String optionsCat=og.generateIncOptionsTestingsPhases(language, "covproj", train, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
						String optionscovproj="optionscovproj.xml";
						BufferedWriter bwOptionscovproj;
						/*String testCorpus=trainingCorpus.replaceAll(".conll","");
						testCorpus+="_test20.conll";
						String trainCorpus=trainingCorpus.replaceAll(".conll","");
						trainCorpus+="_train80.conll";*/
						
						try {
							bwOptionscovproj = new BufferedWriter(new FileWriter(optionscovproj));
							bwOptionscovproj.write(optionsCat);
							bwOptionscovproj.close();
							//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F covproj.xml");
							//System.out.println("--Generating the Model for covproj");
							String s=null;
							
							String maltPath=Optimizer.maltPath;
							//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pcr "+option);
							Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovproj+" -F "+feature +" -cs "+allow_shift + " -cr "+allow_root);
							BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				                    p.getInputStream()));

							BufferedReader stdError = new BufferedReader(new InputStreamReader(
				                    p.getErrorStream()));

							// Leemos la salida del comando
							//System.out.println("Ésta es la salida standard del comando:\n");
							while ((s = stdInput.readLine()) != null) {}
								//System.out.println(s);
							
							//System.out.println("--Parsing with the covproj Model");
							//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovproj.conll -m parse");
							Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+test+" -o "+out+" -m parse");
							BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
				                    p2.getInputStream()));

							BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
				                    p2.getErrorStream()));
							while ((s = stdInput2.readLine()) != null) {}
								//System.out.println(s);}
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						double covprojResults=ch.evaluator(test, out);
						try {
							Runtime.getRuntime().exec("rm "+out);
							Runtime.getRuntime().exec("rm "+language+"Model.mco");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return covprojResults;
	}
	
	
	public Double executeCovingtonNonProjectiveAllowShiftAllowRootTestTrain(String feature, boolean allow_shift, boolean allow_root,String train, String test) {
		// TODO Auto-generated method stub
				//System.out.println(testing80);
						OptionsGenerator og=new OptionsGenerator(language, train);
						String out=System.getProperty("user.dir")+"/outcovnonproj.conll";
						//Firstly, Execute Nivre Eager
						LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
						//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
						String optionsCat=og.generateIncOptionsTestingsPhases(language, "covnonproj", train, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
						String optionscovnonproj="optionscovnonproj.xml";
						BufferedWriter bwOptionscovproj;
						/*String testCorpus=trainingCorpus.replaceAll(".conll","");
						testCorpus+="_test20.conll";
						String trainCorpus=trainingCorpus.replaceAll(".conll","");
						trainCorpus+="_train80.conll";*/
						
						try {
							bwOptionscovproj = new BufferedWriter(new FileWriter(optionscovnonproj));
							bwOptionscovproj.write(optionsCat);
							bwOptionscovproj.close();
							//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F covproj.xml");
							//System.out.println("--Generating the Model for covproj");
							String s=null;
							
							String maltPath=Optimizer.maltPath;
							//System.out.println("java -jar "+maltPath+" -f "+optionscovproj+" -F "+feature+" -pcr "+option);
							Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovnonproj+" -F "+feature +" -cs "+allow_shift + " -cr "+allow_root);
							BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				                    p.getInputStream()));

							BufferedReader stdError = new BufferedReader(new InputStreamReader(
				                    p.getErrorStream()));

							// Leemos la salida del comando
							//System.out.println("Ésta es la salida standard del comando:\n");
							while ((s = stdInput.readLine()) != null) {}
								//System.out.println(s);
							
							//System.out.println("--Parsing with the covproj Model");
							//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovproj.conll -m parse");
							Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+test+" -o "+out+" -m parse");
							BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
				                    p2.getInputStream()));

							BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
				                    p2.getErrorStream()));
							while ((s = stdInput2.readLine()) != null) {}
								//System.out.println(s);}
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						double covprojResults=ch.evaluator(test, out);
						try {
							Runtime.getRuntime().exec("rm "+out);
							Runtime.getRuntime().exec("rm "+language+"Model.mco");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return covprojResults;
	}
	
	public Double executeCovingtonNonProjectivePPAllowShiftAllowRoot(String feature, String ppOption, boolean allow_shift, boolean allow_root){
		// TODO Auto-generated method stub
					//System.out.println(testing80);
							OptionsGenerator og=new OptionsGenerator(language, training80);
							String out=System.getProperty("user.dir")+"/outcovnonproj.conll";
							//Firstly, Execute Nivre Eager
							LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
							//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
							String optionsCat=og.generateIncOptionsTestingsPhases(language, "covnonproj", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
							String optionscovnonproj="optionscovnonproj.xml";
							BufferedWriter bwOptionscovnonproj;
							/*String testCorpus=trainingCorpus.replaceAll(".conll","");
							testCorpus+="_test20.conll";
							String trainCorpus=trainingCorpus.replaceAll(".conll","");
							trainCorpus+="_train80.conll";*/
							
							try {
								bwOptionscovnonproj = new BufferedWriter(new FileWriter(optionscovnonproj));
								bwOptionscovnonproj.write(optionsCat);
								bwOptionscovnonproj.close();
								//System.out.println("java -jar "+maltPath+" -f "+optionscovnonproj+" -F covnonproj.xml");
								//System.out.println("--Generating the Model for covnonproj");
								String s=null;
								
								String maltPath=Optimizer.maltPath;
								//System.out.println("java -jar "+maltPath+" -f "+optionscovnonproj+" -F "+feature+" -pcr "+option);
								Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovnonproj+" -F "+feature+" -pp "+ppOption +" -cs "+allow_shift + " -cr "+allow_root);
								BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					                    p.getInputStream()));

								BufferedReader stdError = new BufferedReader(new InputStreamReader(
					                    p.getErrorStream()));

								// Leemos la salida del comando
								//System.out.println("Ésta es la salida standard del comando:\n");
								while ((s = stdInput.readLine()) != null) {}
									//System.out.println(s);
								
								//System.out.println("--Parsing with the covnonproj Model");
								//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovnonproj.conll -m parse");
								Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
								BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
					                    p2.getInputStream()));

								BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
					                    p2.getErrorStream()));
								while ((s = stdInput2.readLine()) != null) {}
									//System.out.println(s);}
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
							
							double covnonprojResults=ch.evaluator(out,testing80);
							try {
								Runtime.getRuntime().exec("rm "+out);
								Runtime.getRuntime().exec("rm "+language+"Model.mco");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return covnonprojResults;
	}
	
	
	
	public double executeCovingtonNonProjectivePPAllowShiftAllowRootTestTrain(String feature, String ppOption, boolean allow_shift, boolean allow_root,String train, String test){
		// TODO Auto-generated method stub
					//System.out.println(testing80);
							OptionsGenerator og=new OptionsGenerator(language, train);
							String out=System.getProperty("user.dir")+"/outcovnonproj.conll";
							//Firstly, Execute Nivre Eager
							LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
							//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
							String optionsCat=og.generateIncOptionsTestingsPhases(language, "covnonproj", train, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
							String optionscovnonproj="optionscovnonproj.xml";
							BufferedWriter bwOptionscovnonproj;
							/*String testCorpus=trainingCorpus.replaceAll(".conll","");
							testCorpus+="_test20.conll";
							String trainCorpus=trainingCorpus.replaceAll(".conll","");
							trainCorpus+="_train80.conll";*/
							
							try {
								bwOptionscovnonproj = new BufferedWriter(new FileWriter(optionscovnonproj));
								bwOptionscovnonproj.write(optionsCat);
								bwOptionscovnonproj.close();
								//System.out.println("java -jar "+maltPath+" -f "+optionscovnonproj+" -F covnonproj.xml");
								//System.out.println("--Generating the Model for covnonproj");
								String s=null;
								
								String maltPath=Optimizer.maltPath;
								//System.out.println("java -jar "+maltPath+" -f "+optionscovnonproj+" -F "+feature+" -pcr "+option);
								Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovnonproj+" -F "+feature+" -pp "+ppOption +" -cs "+allow_shift + " -cr "+allow_root);
								BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					                    p.getInputStream()));

								BufferedReader stdError = new BufferedReader(new InputStreamReader(
					                    p.getErrorStream()));

								// Leemos la salida del comando
								//System.out.println("Ésta es la salida standard del comando:\n");
								while ((s = stdInput.readLine()) != null) {}
									//System.out.println(s);
								
								//System.out.println("--Parsing with the covnonproj Model");
								//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovnonproj.conll -m parse");
								Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+test+" -o "+out+" -m parse");
								BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
					                    p2.getInputStream()));

								BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
					                    p2.getErrorStream()));
								while ((s = stdInput2.readLine()) != null) {}
									//System.out.println(s);}
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
							
							double covnonprojResults=ch.evaluator(test, out);
							try {
								Runtime.getRuntime().exec("rm "+out);
								Runtime.getRuntime().exec("rm "+language+"Model.mco");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return covnonprojResults;
	}

	public Double executeCovingtonNonProjectiveAllowShiftAllowRoot(String feature, boolean allow_shift, boolean allow_root) {
		// TODO Auto-generated method stub
				//System.out.println(testing80);
						OptionsGenerator og=new OptionsGenerator(language, training80);
						String out=System.getProperty("user.dir")+"/outcovnonproj.conll";
						//Firstly, Execute Nivre Eager
						LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
						//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
						String optionsCat=og.generateIncOptionsTestingsPhases(language, "covnonproj", AlgorithmTester.training80, Optimizer.optionMenosR, los.getLibraryOptions(), Optimizer.optionGRL, Optimizer.pcrOption);
						String optionscovnonproj="optionscovnonproj.xml";
						BufferedWriter bwOptionscovnonproj;
						/*String testCorpus=trainingCorpus.replaceAll(".conll","");
						testCorpus+="_test20.conll";
						String trainCorpus=trainingCorpus.replaceAll(".conll","");
						trainCorpus+="_train80.conll";*/
						
						try {
							bwOptionscovnonproj = new BufferedWriter(new FileWriter(optionscovnonproj));
							bwOptionscovnonproj.write(optionsCat);
							bwOptionscovnonproj.close();
							//System.out.println("java -jar "+maltPath+" -f "+optionscovnonproj+" -F covnonproj.xml");
							//System.out.println("--Generating the Model for covnonproj");
							String s=null;
							
							String maltPath=Optimizer.maltPath;
							//System.out.println("java -jar "+maltPath+" -f "+optionscovnonproj+" -F "+feature+" -pcr "+option);
							Process p=Runtime.getRuntime().exec("java "+Optimizer.javaHeapValue+" -jar "+maltPath+" -f "+optionscovnonproj+" -F "+feature +" -cs "+allow_shift + " -cr "+allow_root);
							BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				                    p.getInputStream()));

							BufferedReader stdError = new BufferedReader(new InputStreamReader(
				                    p.getErrorStream()));

							// Leemos la salida del comando
							//System.out.println("Ésta es la salida standard del comando:\n");
							while ((s = stdInput.readLine()) != null) {}
								//System.out.println(s);
							
							//System.out.println("--Parsing with the covnonproj Model");
							//System.out.println("java -jar "+maltPath+" -c "+language+"Model -i "+testCorpus+" -o outcovnonproj.conll -m parse");
							Process p2=Runtime.getRuntime().exec("java -jar "+maltPath+" -c "+language+"Model -i "+testing80+" -o "+out+" -m parse");
							BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
				                    p2.getInputStream()));

							BufferedReader stdError2 = new BufferedReader(new InputStreamReader(
				                    p2.getErrorStream()));
							while ((s = stdInput2.readLine()) != null) {}
								//System.out.println(s);}
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						double covnonprojResults=ch.evaluator(out,testing80);
						try {
							Runtime.getRuntime().exec("rm "+out);
							Runtime.getRuntime().exec("rm "+language+"Model.mco");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return covnonprojResults;
	}

}
