package optimizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.StringTokenizer;


import algorithmTester.AlgorithmTester;


/**
 * 
 * @author Miguel Ballesteros
 *
 */
public class Optimizer {
	
	public static boolean pseudoRandomizeSelection=false;
	public static boolean chooseMajority=false;
	public static boolean chooseAverage=false;
	public static boolean chooseAllOfThem=false;
	
	public static boolean crossValidation=false;
	
	
	public static String maltPath="malt.jar"; //malt path  
	
	private String trainingCorpus;
	
	public static String  testCorpus="";
	
	private String language="lang";
	private boolean projective;
	private boolean strictRoot;
	private boolean coveredRoots;
	private boolean coveredRootsWithoutChildren;
	public static int numbTokens;
	public static int numbSentences;
	private double percentage;
	private int numbDanglingCases=0;
	public static double defaultBaseline=0.0;
	
	public static boolean allow_rootNiv=true;
	public static boolean allow_reduceNiv=false;
	
	
	public int getNumbDanglingCases() {
		return numbDanglingCases;
	}

	public void setNumbDanglingCases(int numbDanglingCases) {
		this.numbDanglingCases = numbDanglingCases;
	}

	public int getNumbTokens() {
		return numbTokens;
	}

	public void setNumbTokens(int numbTokens) {
		this.numbTokens = numbTokens;
	}

	public int getNumbSentences() {
		return numbSentences;
	}

	public void setNumbSentences(int numbSentences) {
		this.numbSentences = numbSentences;
	}

	public static String bestAlgorithm;
	
	private boolean rootGRL;
	
	private boolean danglingPunctuation;
	
	public static String pcrOption="none";
	private boolean pcr;
	
	public static int numRootLabels=1;
	
	public static String optionGRL="ROOT";
	public static String optionMenosR="normal";
	
	public static String ppOption="head";
	public static boolean usePPOption=false;
	
	public static boolean allow_shift=false;
	public static boolean allow_root=true;
	
	public static Double bestResult=0.0;
	public static String javaHeapValue="";
	
	private boolean smallCaseBothThings=false;
	private boolean noNonProjective=false;
	private boolean substantialNonProjective=false;
	
	public static int nMaxTokens=Integer.MAX_VALUE;
	
	public static String featureModel="NivreEager.xml";
	
	public static String featureModelBruteForce="bruteForce1.xml";
	
	public static String InputLookAhead="Input";
	
	public static boolean cposEqPos=false;
	public static boolean lemmaBlank=true;
	public static boolean featsBlank=true;
	
	public static double threshold=0.05;
	
	public static double bestResultBruteForce=0.0;
	
	public static String libraryValue="-s_4_-c_0.1";
	
	public static boolean includePunctuation=true;
	public static int order=0;
	public static String evaluationMeasure="LAS";
	
	public static String featureAlgorithm="Greedy";
	
	
	
	
	public boolean isSmallCaseBothThings() {
		return smallCaseBothThings;
	}

	public void setSmallCaseBothThings(boolean smallCaseBothThings) {
		this.smallCaseBothThings = smallCaseBothThings;
	}

	public boolean isNoNonProjective() {
		return noNonProjective;
	}

	public void setNoNonProjective(boolean noNonProjective) {
		this.noNonProjective = noNonProjective;
	}

	public boolean isSubstantialNonProjective() {
		return substantialNonProjective;
	}

	public void setSubstantialNonProjective(boolean substantialNonProjective) {
		this.substantialNonProjective = substantialNonProjective;
	}

	public Optimizer() {	
	}
	
	public void setCorpus(String c){
		trainingCorpus=c;
	}
	
	public void runPhase1(){
		smallCaseBothThings=false;
		
		//System.out.println(System.getProperty("user.dir"));
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		//System.out.println("          *Complutense University of Madrid (Spain)  ");
		//System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 1: DATA ANALYSIS");
		System.out.println("In order to optimize MaltParser for your training set, MaltOptimizer will " +"\n"+
				"first analyze the data and set some basic parameters.");
		
		/*for (int i=0;i<10;i++) {
			try {
				Thread.sleep (300);
				} catch (InterruptedException ie) {
					ie.printStackTrace ();
				}
			System.out.print(".");
		}
		System.out.println(".");*/
		System.out.println("-----------------------------------------------------------------------------");

		String s=null;
		Process p;
		try {
			System.out.println("DATA VALIDATION");
			System.out.print("Validating the CoNLL data format ... ");
			for (int i=0;i<2;i++) {
			try {
				Thread.sleep (300);
				} catch (InterruptedException ie) {
					ie.printStackTrace ();
				}
			//System.out.print(".");
			}
			System.out.println(" (may take a few minutes)");
			p = Runtime.getRuntime().exec("python validateFormat.py "+trainingCorpus);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
	                p.getInputStream()));
			/*BufferedReader stdOutput = new BufferedReader(new InputStreamReader(
	                p.getOutputStream()));*/
			BufferedReader stdError = new BufferedReader(new InputStreamReader(
	                p.getErrorStream()));
			
			// Leemos la salida del comando
			//System.out.println("Ésta es la salida standard del comando:\n");
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}
			BufferedWriter bwLog=new BufferedWriter(new FileWriter("logValidationFile.txt"));
		
			boolean right=false;
			int warnings=0;
			while ((s = stdError.readLine()) != null) {
				bwLog.write(s+"\n");
				warnings++;
				if (s.equals("Exit status =  0")) {
					right=true;
				}
			}
			bwLog.close();
			warnings--;
			if (right) {
				if (warnings==0) {
					System.out.println("Your training set is in valid CoNLL format.");
				}
				if (warnings>0){
					System.out.println("Your training set is in valid CoNLL format, but the validation script");
					System.out.println("gave some warnings, so you may want to consult the logfile\n "+System.getProperty("user.dir")+"/logValidationFile.txt .");
				}
			}
			else{
				System.out.println("Your training set is not in valid CoNLL format. MaltOptimizer will");
				System.out.println("terminate. Please consult the logfile "+System.getProperty("user.dir")+"/logValidationFile.txt");
				System.out.println("to find out what needs to be fixed.");
				System.exit(0);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("DATA CHARACTERISTICS");
		
			//System.out.println(s);
		
		
		CoNLLHandler ch = new CoNLLHandler(trainingCorpus);
		
		
		System.out.println(ch.extraDataCharacteristics());	
		int numToks=ch.getNumbTokens();
		
		
		
		percentage=ch.projectiveOrNonProjective();
		String cad=String.valueOf(percentage);
		if (cad.length()>5)
		cad=cad.substring(0,5);
		//System.out.println("There are "+ch.getNumberOfSentences()+" sentences in the corpus.");
		if (percentage>15){ //non-negligible argument
			System.out.println("Your training set contains a substantial amount of non-projective trees ("+cad+" %).");
			//System.out.println("The system is going to test only non-projective algorithms.");
			projective=false;
		}
		else {
			if (percentage==0) {
				System.out.println("Your training set contains no non-projective trees.");
				//System.out.println("The system is going to test only projective algorithms.");
				projective=true;
			}
			else {
				System.out.println("Your training set contains a small amount of non-projective trees ("+cad+" %).");
				//System.out.println("The system is going to test both kind of algorithms: projective and non-projective.");
				smallCaseBothThings=true;
			}
		}
		
		
		
		//Dangling Punctuation
		/*coveredRoots=ch.coveredRoots(); 
		if (coveredRoots) {
			System.out.println("There are signs of covered roots.");	
		}
		else {
			System.out.println("There are no signs of covered roots.");
		}
		
		coveredRootsWithoutChildren=ch.coveredRootsWithoutChildren(); 
		if (coveredRootsWithoutChildren) {
			System.out.println("There are signs of covered roots without children.");	
		}
		else {
			System.out.println("There are no signs of covered roots without children.");
		}*/
		
		danglingPunctuation=ch.danglingPunctuation(); 
		String danglingFreq=ch.getDanglingFreq();
		Integer di=Integer.parseInt(danglingFreq);
		this.numbDanglingCases=di;
		if (danglingPunctuation) {
			System.out.println("Your training set contains unattached internal punctuation ("+danglingFreq+" instances).");	
		}
		else {
			System.out.println("Your training set does not contain unattached internal punctuation.");
		}
			
		
		
		
		//ROOT GRL?
		String realRoot="";
		rootGRL=ch.rootLabels();
		HashMap<String,Double> rootLabels=ch.getRootlabels();
		//ArrayList<String> getThreeFrequent(rootLabels);
		if (rootGRL) {
			Set<String> set=rootLabels.keySet();
			Iterator<String> it=set.iterator();
			System.out.println("Your training set has multiple DEPREL labels for tokens where HEAD=0:");
			//System.out.println("Frequency of labels used for tokens with HEAD = 0 .");
			while(it.hasNext()){
				String r=it.next();
				Double d=rootLabels.get(r)*100;
				String val=new String(d.toString());
				if (val.length()<3) val=val.substring(0,2);
				else if (val.length()>5) val=val.substring(0,5);
				System.out.println(r+": "+val+"% ");
				numRootLabels++;
			}
			if (numRootLabels>1) numRootLabels--;
			/*System.out.println("");
			System.out.println("The system is going to test which root label configuration is better.");
			System.out.println("it may take a few minutes...");*/
			
			optionMenosR="normal";
			
			
			
		}
		else {
			Set<String> set=rootLabels.keySet();
			Iterator<String> it=set.iterator();
			while(it.hasNext()){
				String r=it.next();
				realRoot=r;
			}
			System.out.println("Your training set has a unique DEPREL label for tokens where HEAD=0:"+realRoot+".");
			if (realRoot.equals("ROOT")) {
				//System.out.println("There is no need to test which ROOT label configuration is better.");
				pcr=false;
				//optionMenosR="strict";
			}
			else {
				pcr=true;
				optionGRL=realRoot;
				//optionMenosR="strict";
				//System.out.println("There is no need to test which ROOT label configuration is better.");
			}
		}
		
		System.out.println("-----------------------------------------------------------------------------");
		
		
		
		
		
		
		
		
		
		System.out.println("BASIC OPTIMIZATION SETUP ");
		System.out.print("Generating training and test files for optimization");
		for (int i=0;i<2;i++) {
		try {
			Thread.sleep (300);
			} catch (InterruptedException ie) {
				ie.printStackTrace ();
			}
		System.out.print(".");
		}
		System.out.println(".");
		//
		
		ch.generateDivision8020();
		ch.generate5FoldCrossCorporaPseudo();
		
		System.out.println(ch.getMessageDivision());
		
		//System.out.println("**\n"+ch.getMessageDivision());
		
		if (ch.getNumbTokens()>100000)
		{
			System.out.println("Given that your data set is relatively large, we recommend using a single \ndevelopment set during subsequent optimization phases. If you prefer to use 5-fold cross-validation, you can specify this instead (-v cv).");
		}
		else {
			System.out.println("Given that your data set is relatively small, we recommend using 5-fold \ncross-validation during subsequent optimization phases (-v cv).");
		}
		
		//Language Detection
				/*String frase=ch.getSamplePlainText();
				/*LanguageDetector ld=new LanguageDetector(frase);
				language=ld.getLanguage();
				System.out.println("The system has detected your corpus is written in:"+language+".");*/
				language="lang";

				//String realRoot=ch.getHead0();
				//Head 0 to root?:"+rootGRL+"(Root:"+realRoot+")");
		
		System.out.print("Testing the default settings ");
		for (int i=0;i<2;i++) {
			try {
				Thread.sleep (300);
				} catch (InterruptedException ie) {
					ie.printStackTrace ();
				}
			System.out.print(".");
			}
			
		System.out.println(". (may take a few seconds)");
		AlgorithmTester atdefault=new AlgorithmTester(language,ch,trainingCorpus);
		
		Double bestDefaultResult=atdefault.executeDefault();
		
		String sBestDefLabelResult=""+bestDefaultResult;
		if (sBestDefLabelResult.length()>5)
			sBestDefLabelResult=sBestDefLabelResult.substring(0, 5);
		System.out.println("LAS with default settings: "+sBestDefLabelResult+"%");
		defaultBaseline=bestDefaultResult;
		bestResult=bestDefaultResult;
		
		
		//System.out.println("-------------------");	
		
		
		if (rootGRL){
			System.out.print("Testing root labels ");
			for (int i=0;i<2;i++) {
			try {
				Thread.sleep (300);
				} catch (InterruptedException ie) {
					ie.printStackTrace ();
				}
			System.out.print(".");
			}
			System.out.println(". ");
			//
			ArrayList<String> threeLabels=getThreeFrequent(rootLabels);
			//System.out.println(threeLabels);
			AlgorithmTester atroot=new AlgorithmTester(language,ch,trainingCorpus);
			
			String bestLabel=atroot.executeLabelTest(threeLabels);
			System.out.println("Default root label reset: -grl "+bestLabel);
			Double bestLabelResult=atroot.getBestLabelLASResult();
			String sBestLabelResult=""+bestLabelResult;
			if (sBestLabelResult.length()>5)
				sBestLabelResult=sBestLabelResult.substring(0, 5);
			Double difference=0.0;
			System.out.println(bestResult);
			System.out.println(bestLabelResult);
			if (bestResult<bestLabelResult) { 
				difference=bestLabelResult-bestResult;
				bestResult=bestLabelResult;
			
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+sBestLabelResult+"%)");
			optionGRL=bestLabel;
			optionMenosR="normal";
			}
			//System.out.println("-------------------");
			
		}
		
		
		if (danglingPunctuation) {
			System.out.print("Testing preprocessing of unattached punctuation ");
			for (int i=0;i<2;i++) {
			try {
				Thread.sleep (300);
				} catch (InterruptedException ie) {
					ie.printStackTrace ();
				}
			System.out.print(".");
			}
			System.out.println(". ");
			//
			//System.out.println(threeLabels);
			AlgorithmTester atpcr=new AlgorithmTester(language,ch,trainingCorpus);
			
			String bestOption=atpcr.executePCRTest();
			pcrOption=bestOption;
			System.out.println("Treatment of covered roots reset: -pcr "+bestOption);
			Double bestLabelResult=atpcr.getBestLabelLASResult();
			String sBestLabelResult=""+bestLabelResult;
			if (sBestLabelResult.length()>5)
				sBestLabelResult=sBestLabelResult.substring(0, 5);
			Double difference=0.0;
			if (bestResult<bestLabelResult) {
				difference=bestLabelResult-bestResult;
				bestResult=bestLabelResult;
			
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+sBestLabelResult+"%)");
			}
			//System.out.println("Incremental "+evaluationMeasure+" improvement: "+sBestLabelResult+"%");
			
			
			/*Double bestLabelResult=atroot.getBestLabelLASResult();
			String sBestLabelResult=""+bestLabelResult;
			if (sBestLabelResult.length()>5)
				sBestLabelResult=sBestLabelResult.substring(0, 5);
			Double difference=0.0;
			if (bestResult<bestLabelResult) 
				difference=bestLabelResult-bestResult;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("Incremental "+evaluationMeasure+" improvement: +"+sDifferenceLabel+"% ("+sBestLabelResult+"%)");*/
			
			//System.out.println("-------------------");
		}
		
		
		
		OptionsGenerator ogen=new OptionsGenerator();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel) 
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		String optionsFile=ogen.generateIncOptionsPhase1(language, "nivreeager", AlgorithmTester.training80, optionMenosR, los.getLibraryOptions(), optionGRL, pcrOption);
		
		BufferedWriter bwOptionsNivreEager;
		
			try {
				bwOptionsNivreEager = new BufferedWriter(new FileWriter("incr_optionFile.xml"));
				bwOptionsNivreEager.write(optionsFile);
				bwOptionsNivreEager.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		BufferedWriter bwPhase1HiddenLogFile;
			
		try {
			bwPhase1HiddenLogFile = new BufferedWriter(new FileWriter("phase1_logFile.txt"));
			bwPhase1HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase1HiddenLogFile.write("Size (tokens):"+ch.getNumbTokens()+"\n");
			bwPhase1HiddenLogFile.write("Size (sentences):"+ch.getNumbSentences()+"\n");
			bwPhase1HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase1HiddenLogFile.write("Dangling Punctuation:"+danglingFreq+"\n");
			bwPhase1HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase1HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase1HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			bwPhase1HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase1HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase1HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase1HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase1HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			/*if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}*/
			bwPhase1HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase1LogFile;
		
		try {
			bwPhase1LogFile = new BufferedWriter(new FileWriter("phase1_optFile.txt"));
			if (rootGRL)
				bwPhase1LogFile.write("1. root_label(-grl):"+optionGRL+"\n");
			bwPhase1LogFile.write("2. covered_root(-pcr):"+pcrOption+"\n");
			bwPhase1LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the analysis of your training set and saved the");
		System.out.println("results for future use in /phase1_logFile.txt. Updated MaltParser options can be found"); 
		System.out.println("in /phase1_optFile.txt. If you want to change any of these options, you should");
		System.out.println("edit /phase1_optFile.txt before you start the next optimization phase.");
		System.out.println("");
		System.out.println("To proceed with Phase 2 (Parsing Algorithm) run the following command:");
		System.out.println("java -jar MaltOptimizer.jar -p 2 -m <malt_path> -c <trainingCorpus>");

		System.exit(0);

		//*******
		//*******
		//*******
		//*******
		
		//*******
		
		//*******
		//*******
		//*******
		
		//*******
		//*******
		
		//*******
		//HAY QUE ENCAJAR AHORA LA PHASE 1: Data characteristics con la Phase 2: Algorithm Testing
		//Es decir: cosas que faltarían, option GRL!, option PCR!, etc
		
		//Testing the ALGORITHMS!
		/*System.out.println("-----------------------------------------------------------------------------");
		System.out.println("ALGORITHM TESTING");
		System.out.println("it may take a few minutes...");
		
		AlgorithmTester at=new AlgorithmTester(language, percentage,ch,trainingCorpus,optionMenosR);
		bestAlgorithm="";
		if (projective) {
			bestAlgorithm=at.executeProjectivity();
			System.out.println("The System has inferred that the best algorithm for your corpus is: "+bestAlgorithm+".");
			try {
				Process p2=Runtime.getRuntime().exec("rm "+language+"Model.mco");
				Process p3=Runtime.getRuntime().exec("rm "+language+"ModelStack.mco");
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		                p2.getInputStream()));
				/*BufferedReader stdOutput = new BufferedReader(new InputStreamReader(
		                p.getOutputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(
		                p2.getErrorStream()));
				
				BufferedReader stdInput3 = new BufferedReader(new InputStreamReader(
		                p3.getInputStream()));
				/*BufferedReader stdOutput = new BufferedReader(new InputStreamReader(
		                p.getOutputStream()));
				BufferedReader stdError3 = new BufferedReader(new InputStreamReader(
		                p3.getErrorStream()));
				
				// Leemos la salida del comando
				//System.out.println("Ésta es la salida standard del comando:\n");
				while ((s = stdInput.readLine()) != null) {}
				while ((s = stdInput3.readLine()) != null) {}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			bestAlgorithm=at.executeNonProjectivity();
			System.out.println("The System has inferred that the best non-projective algorithm for your corpus is: "+bestAlgorithm+".");
			//NON-PROJECTIVE CODE
			//....
			
			if (smallCaseBothThings) {
					bestAlgorithm=at.executeProjectivity();
					System.out.println("The System has inferred that the best projective algorithm for your corpus is: "+bestAlgorithm+".");
					try {
						Process p2=Runtime.getRuntime().exec("rm "+language+"Model.mco");
						Process p3=Runtime.getRuntime().exec("rm "+language+"ModelStack.mco");
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				                p2.getInputStream()));
						/*BufferedReader stdOutput = new BufferedReader(new InputStreamReader(
				                p.getOutputStream()));
						BufferedReader stdError = new BufferedReader(new InputStreamReader(
				                p2.getErrorStream()));
						
						BufferedReader stdInput3 = new BufferedReader(new InputStreamReader(
				                p3.getInputStream()));
						/*BufferedReader stdOutput = new BufferedReader(new InputStreamReader(
				                p.getOutputStream()));
						BufferedReader stdError3 = new BufferedReader(new InputStreamReader(
				                p3.getErrorStream()));
						
						// Leemos la salida del comando
						//System.out.println("Ésta es la salida standard del comando:\n");
						while ((s = stdInput.readLine()) != null) {}
						while ((s = stdInput3.readLine()) != null) {}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		//}
		
		//Cleaning files
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("FEATURE TESTING");
		System.out.println("it may take a few minutes...");
		double bestResult=at.getBestResult();
		String bestFeature="";
		boolean nivreeager=false;
		if (bestAlgorithm.equals("nivreeager")){
			nivreeager=true;
		}
		
		FeatureGenerator fg=new FeatureGenerator(language);
		String featureBaseline="StackSwap.xml";
		if (nivreeager){
			featureBaseline="NivreEager.xml";
		}
		// Decision tree
		if (nivreeager) {
			fg.removeInputNivreEager(featureBaseline, "1"+featureBaseline);
			//Run the experiment
			double result1=at.executeNivreEagerDefault("1"+featureBaseline);
			if (result1>bestResult) {
				bestResult=result1;
				fg.removeStack("1"+featureBaseline, "2"+featureBaseline);
				double result2=at.executeNivreEagerDefault("2"+featureBaseline);
				if (result2>bestResult) {
					bestFeature="2"+featureBaseline;
				}
				else {
					bestFeature="1"+featureBaseline;
				}
			}
			else{
				fg.addInputNivreEager(featureBaseline, "3"+featureBaseline);
				double result3=at.executeNivreEagerDefault("3"+featureBaseline);
				if (result3>bestResult) {
					bestResult=result3;
					bestFeature="3"+featureBaseline;
				}
				else {
					fg.removeStack(featureBaseline, "4"+featureBaseline);
					double result4=at.executeNivreEagerDefault("4"+featureBaseline);
					if (result4>bestResult) {
						bestResult=result4;
						bestFeature="4"+featureBaseline;
					}
					else {
						bestFeature=featureBaseline;
					}
				}
			}
		}
		else { //STACKLAZY
			
			
			fg.removeLookAheadStackLazy(featureBaseline, "1"+featureBaseline);
			System.out.println("Trying with the first modification of the feature model.");
			double result1=at.executeStackLazy("1"+featureBaseline);
			System.out.println(result1);
			if (result1>bestResult) {
				bestResult=result1;
				fg.removeStack("1"+featureBaseline, "2"+featureBaseline);
				System.out.println("Trying with the another modification of the feature model.");
				double result2=at.executeStackLazy("2"+featureBaseline);
				System.out.println(result2);
				if (result2>bestResult) {
					bestFeature="2"+featureBaseline;
				}
				else {
					bestFeature="1"+featureBaseline;
				}
			}
			else{
				fg.addLookAheadStackLazy(featureBaseline, "3"+featureBaseline);
				System.out.println("Trying with another modification of the feature model.");
				double result3=at.executeStackLazy("3"+featureBaseline);
				System.out.println(result3);
				if (result3>bestResult) {
					bestResult=result3;
					bestFeature="3"+featureBaseline;
				}
				else {
					fg.removeStack(featureBaseline, "4"+featureBaseline);
					System.out.println("Trying with another modification of the feature model.");
					double result4=at.executeStackLazy("4"+featureBaseline);
					System.out.println(result4);
					if (result4>bestResult) {
						bestResult=result4;
						bestFeature="4"+featureBaseline;
					}
					else {
						bestFeature=featureBaseline;
					}
				}
			}
		}
		
		System.out.println("The system has inferred that the best results possible is:"+bestResult);
		System.out.println("It is obtained with this feature model:"+ bestFeature);
		fg.printFeature(bestFeature);
		
		//
		
		try {
			Process p2=Runtime.getRuntime().exec("rm optionsNivreEager.xml");
			Process p3=Runtime.getRuntime().exec("rm optionsStackLazy.xml");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
	                p2.getInputStream()));
			/*BufferedReader stdOutput = new BufferedReader(new InputStreamReader(
	                p.getOutputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(
	                p2.getErrorStream()));
			
			BufferedReader stdInput3 = new BufferedReader(new InputStreamReader(
	                p3.getInputStream()));
			/*BufferedReader stdOutput = new BufferedReader(new InputStreamReader(
	                p.getOutputStream()));
			BufferedReader stdError3 = new BufferedReader(new InputStreamReader(
	                p3.getErrorStream()));
			
			// Leemos la salida del comando
			//System.out.println("Ésta es la salida standard del comando:\n");
			while ((s = stdInput.readLine()) != null) {}
			while ((s = stdInput3.readLine()) != null) {}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("LIBRARY OPTIONS TESTING");
		System.out.println("it may take a few minutes...");
		
		String bestLibraryOptions="-s_0_-t_1_-d_2_-g_0.2_-c_1.0_-r_0.4_-e_0.1";
		
		LibraryOptionsSetter lo=LibraryOptionsSetter.getSingleton();
		
		System.out.println("First Experiment. C=0.5.");
		
		lo.incrementC(-0.5);
		double result;
		if (nivreeager) {
				result=at.executeNivreEagerDefault(bestFeature);
		}
		else { //STACKLAZY
			result=at.executeStackLazy(bestFeature);
				
		}
		if (result>bestResult){
			bestLibraryOptions=lo.getLibraryOptions();
			System.out.println("This new set of library options is better.");
		}
		else {
			lo.incrementC(-0.4);
			System.out.println("First Experiment. C=0.1");
			double result2;
			if (nivreeager) {
					result2=at.executeNivreEagerDefault(bestFeature);
			}
			else { //STACKLAZY
				result2=at.executeStackLazy(bestFeature);
					
			}
			if (result2>bestResult){
				bestLibraryOptions=lo.getLibraryOptions();
				System.out.println("This new set of library options is better.");
			}
		}
		
		System.out.println("The best results the systems can provide is:"+bestResult+".");
		System.out.println("The best library options are "+ bestLibraryOptions+".");
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("LIBRARY OPTIONS TESTING");
						
		OptionsGenerator og=new OptionsGenerator(bestAlgorithm,language,trainingCorpus,bestLibraryOptions+".");
		System.out.println("Therefore, this is the <OptionsFile>.xml that the system suggests:");
		System.out.println(og.generateOptionsFile());
		*/
	}
	
	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	private void loadPhase1Results(String pathTrainingSet) {
		// TODO Auto-generated method stub
		//phase1_optFile.txt
		//phase1_logFile.txt
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("phase1_logFile.txt"));
		try {
			int contador=0;
			while(br.ready()){
				String line;
				try {
					line = br.readLine();
					StringTokenizer st=new StringTokenizer(line,":");
					String tok="";
					while(st.hasMoreTokens()){
						tok=st.nextToken();
					}
					contador++;
					if (contador==1) {
						if (pathTrainingSet.equals(tok)) {
							this.setTrainingCorpus(tok);
							//System.out.println(tok);
						}
						else {
							try {
								throw new PathNotFoundException();
							} catch (PathNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					if (contador==2) {
						Integer nt=Integer.parseInt(tok);
						this.setNumbTokens(nt);
						//System.out.println(nt);
					}
					if (contador==3) {
						Integer nt=Integer.parseInt(tok);
						this.setNumbSentences(nt);
						//System.out.println(nt);
					}
					if (contador==4) {
						Double nt=Double.parseDouble(tok);
						this.setPercentage(nt);
						//System.out.println(nt);
						if (nt==0.0){
							this.setNoNonProjective(true);
						}
						else {
							if (nt>15) {
								this.setSubstantialNonProjective(true);
							}
							else {
								this.setSmallCaseBothThings(true);
							}
						}
						
						
					}
					if (contador==5) {
						Integer it=Integer.parseInt(tok);
						if (it>0) this.setDanglingPunctuation(true);
						this.setNumbDanglingCases(it);
						//System.out.println(it);
					}
					if (contador==6) {
						Double nt=Double.parseDouble(tok);
						this.setBestResult(nt);
						//System.out.println(nt);
					}
					if (contador==7) {
						Double nt=Double.parseDouble(tok);
						this.setDefaultBaseline(nt);
						//System.out.println(nt);
					}
					if (contador==8) {
						Integer nt=Integer.parseInt(tok);
						this.numRootLabels=nt;
						//System.out.println(nt);
					}
					if (contador==9) {
						javaHeapValue=tok;
						//System.out.println(nt);
					}
					if (contador==10) {
						Integer nt=Integer.parseInt(tok);
						nMaxTokens=nt;
						//System.out.println(nt);
					}
					
					if (contador==11) {
						if (tok.equals("true"))
							cposEqPos=true;
						else
							cposEqPos=false;
						//System.out.println(nt);
					}
					if (contador==12) {
						if (tok.equals("true"))
							lemmaBlank=true;
						else
							lemmaBlank=false;
					}
					if (contador==13) {
						if (tok.equals("true"))
							featsBlank=true;
						else
							featsBlank=false;
						//System.out.println(nt);
					}
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		BufferedReader brOpt;
		try {
			brOpt = new BufferedReader(new FileReader("phase1_optFile.txt"));
		try {
			int contador=0;
			boolean grl=false;
			boolean pcr=false;
			while(brOpt.ready()){
				String line;
				try {
					line = brOpt.readLine();
					StringTokenizer st=new StringTokenizer(line,":");
					grl=false;
					pcr=false;
					if (line.contains("grl")) grl=true;
					if (line.contains("pcr")) pcr=true;
					String tok="";
					while(st.hasMoreTokens()){
						tok=st.nextToken();
					}
					contador++;
					if (grl) {
						this.setOptionGRL(tok);
						//System.out.println(line);
						grl=false;
					}
					if (pcr) {
						this.setPcrOption(tok);
						//System.out.println(line);
						pcr=false;
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
	}
	
	
	public static double getDefaultBaseline() {
		return defaultBaseline;
	}

	public static void setDefaultBaseline(double defaultBaseline) {
		Optimizer.defaultBaseline = defaultBaseline;
	}

	public static int getNumRootLabels() {
		return numRootLabels;
	}

	public static void setNumRootLabels(int numRootLabels) {
		Optimizer.numRootLabels = numRootLabels;
	}

	public static String getPpOption() {
		return ppOption;
	}

	public static void setPpOption(String ppOption) {
		Optimizer.ppOption = ppOption;
	}

	public static boolean isUsePPOption() {
		return usePPOption;
	}

	public static void setUsePPOption(boolean usePPOption) {
		Optimizer.usePPOption = usePPOption;
	}

	public static boolean isAllow_shift() {
		return allow_shift;
	}

	public static void setAllow_shift(boolean allow_shift) {
		Optimizer.allow_shift = allow_shift;
	}

	public static boolean isAllow_root() {
		return allow_root;
	}

	public static void setAllow_root(boolean allow_root) {
		Optimizer.allow_root = allow_root;
	}

	public void runPhase2() {
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 2: PARSING ALGORITHM SELECTION\n");
		this.bestAlgorithm="nivreeager";
		
		if (this.noNonProjective) { //case: Strictly projective case (non projectivities==0)
			System.out.println("MaltOptimizer found in Phase 1 that your training set contains");
			System.out.println("no non-projective trees and will therefore only try projective parsing algorithms.\n");
			//System.out.println("Testing the no non projective algorithms ...");
			runStrictlyProjective();
		}
		else {
			if (this.substantialNonProjective) { //case non projectivities>15
				System.out.println("MaltOptimizer found in Phase 1 that your training set contains");
				System.out.println("a substantial amount of non-projective trees and will therefore \nonly try non-projective algorithms.\n");
				//System.out.println("Testing the non-projective algorithms ...");
				runLargeAmountNonProjective();
			}
			else if (this.smallCaseBothThings) { //case non projectivities <15
				System.out.println("MaltOptimizer found in Phase 1 that your training set contains");
				System.out.println("a small amount of non-projective trees and will therefore \ntry both projective and non-projective algorithms.\n");
				runStrictlyProjective();
				runLargeAmountNonProjective();
			}
		}
		System.out.println("-----------------------------------------------------------------------------");
		String bestAlgoPrintOut=bestAlgorithm;
		if (bestAlgorithm.equals("nivreeager")) {
			bestAlgoPrintOut="NivreEager";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			bestAlgoPrintOut="NivreStandard";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			bestAlgoPrintOut="CovingtonNonProjective";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			bestAlgoPrintOut="CovingtonProjective";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			bestAlgoPrintOut="StackProjective";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			bestAlgoPrintOut="StackEager";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			bestAlgoPrintOut="StackLazy";	
		}
		if (Optimizer.usePPOption) {
			System.out.println("MaltOptimizer found that the best parsing algorithm is: "+bestAlgorithm +"+ pp option");
		}
		else System.out.println("MaltOptimizer found that the best parsing algorithm is: "+bestAlgorithm);
		
		//after this: Run algorithm specific parameters with the best one.
		//nivre*----> rootHandling (normal|strict|relaxed)
		//cov*-----> allowshift (true|false) and allowroot(true|false)
		//stack*---> don't have any
		//if -pp is better, test (baseline | head | path | head+path)
		CoNLLHandler ch =new CoNLLHandler(this.trainingCorpus);
		AlgorithmTester at=new AlgorithmTester(this.language,ch,this.trainingCorpus);
		Double difference=0.0;
		
		
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		
		if (bestAlgorithm.contains("cov")){
			//allow_shift test2
			if (bestAlgorithm.equals("covproj")){
				System.out.println("Testing the Covington--Projective algorithm ...");
				Double covprojLAS=0.0;
				if (this.usePPOption) {
					covprojLAS=at.executeCovingtonProjectivePPAllowShiftAllowRoot("CovingtonProjective.xml","head",true, allow_root);
				}
				else {
				  covprojLAS=at.executeCovingtonProjectiveAllowShiftAllowRoot("CovingtonProjective.xml",true, allow_root);
				}
				if (covprojLAS>(this.bestResult+threshold)) {
					this.bestAlgorithm="covproj";
					difference=covprojLAS-bestResult;
					this.bestResult=covprojLAS;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					this.allow_shift=true;
					System.out.println("New allow_shift option: true");
					this.bestAlgorithm="covproj";
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+this.bestResult+"%)");
					
				}
				if (this.usePPOption) {
					covprojLAS=at.executeCovingtonProjectivePPAllowShiftAllowRoot("CovingtonProjective.xml","head",allow_shift, false);
				}
				else {
				  covprojLAS=at.executeCovingtonProjectiveAllowShiftAllowRoot("CovingtonProjective.xml",allow_shift, false);
				}
				if (covprojLAS>(this.bestResult+threshold)) {
					this.bestAlgorithm="covproj";
					difference=covprojLAS-bestResult;
					this.bestResult=covprojLAS;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					this.allow_root=false;
					System.out.println("New allow_root option: false");
					this.bestAlgorithm="covproj";
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+this.bestResult+"%)");
					
				}
				
			}
			else {
				if (bestAlgorithm.equals("covnonproj")){
					System.out.println("Testing the Covington-Non-Projective algorithm ...");
					Double covnonprojLAS=0.0;
					if (this.usePPOption) {
						covnonprojLAS=at.executeCovingtonNonProjectivePPAllowShiftAllowRoot("CovingtonNonProjective.xml","head",true, allow_root);
					}
					else {
					  covnonprojLAS=at.executeCovingtonNonProjectiveAllowShiftAllowRoot("CovingtonNonProjective.xml",true, allow_root);
					}
					if (covnonprojLAS>(this.bestResult+threshold)) {
						this.bestAlgorithm="covnonproj";
						difference=covnonprojLAS-bestResult;
						this.bestResult=covnonprojLAS;
						String sDifferenceLabel=""+difference;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						this.allow_shift=true;
						System.out.println("New allow_shift option: true");
						this.bestAlgorithm="covnonproj";
						System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+this.bestResult+"%)");
						
					}
					if (this.usePPOption) {
						covnonprojLAS=at.executeCovingtonNonProjectivePPAllowShiftAllowRoot("CovingtonNonProjective.xml","head",allow_shift, false);
					}
					else {
					  covnonprojLAS=at.executeCovingtonNonProjectiveAllowShiftAllowRoot("CovingtonNonProjective.xml",allow_shift, false);
					}
					if (covnonprojLAS>(this.bestResult+threshold)) {
						this.bestAlgorithm="covnonproj";
						difference=covnonprojLAS-bestResult;
						this.bestResult=covnonprojLAS;
						String sDifferenceLabel=""+difference;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						this.allow_root=false;
						System.out.println("New allow_root option: false");
						this.bestAlgorithm="covnonproj";
						System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+this.bestResult+"%)");
						
					}
					
				}
			}
		}
		
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		
		//System.out.println(bestAlgorithm);
		
		if (bestAlgorithm.contains("nivre")) {// && numRootLabels==1) {
			
			/*System.out.println("Root handling testing ...");
			optionMenosR=at.executeRootHandlingTest(bestAlgorithm);
			if (!optionMenosR.equals("normal")){
				System.out.println("Default root handling strategy reset: "+optionMenosR);
			}*/
			
			/////////////////////////NEW FOR VERSION 1.7 //////////
			///allow_root and allow_reduce
			
			//allow_root=true  --- allow_reduce=false  (DEFAULT-> No need to run)
			//allow_root=false --- allow_reduce=false
			//allow_root=true  --- allow_reduce=true
			//allow_root=False --- allow_reduce=true
			
			System.out.println("Root Handling testing ...");
			at.executeRootHandlingTestNivre17(bestAlgorithm);
			System.out.println("Root handling selected strategy:");
			System.out.println("\t"+"allow_root: "+allow_rootNiv);
			System.out.println("\t"+"allow_reduce: "+allow_reduceNiv);
			
			
			
			
			////////////////////////////////////////////////////////
			
		}
		
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		if (usePPOption){ 
			System.out.println("Testing pseudo-projective (PP) options ...");
			ppOption=at.executePPTest(bestAlgorithm);
			if (!ppOption.equals("head")){
				System.out.println("Default marking strategy reset: "+ppOption);
			}
		}
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		if (sDifferenceLabel.equals("0.0")){
			System.out.println("Incremental improvement over the baseline at the end of Phase 2: 0.00% ("+this.bestResult+"%) ");
		}
		else
			System.out.println("Incremental improvement over the baseline at the end of Phase 2: +"+sDifferenceLabel+"% ("+this.bestResult+"%) ");
		
		
		OptionsGenerator ogen=new OptionsGenerator();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel) 
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr, String pp, boolean allowShift, boolean allowROOT) {
		String optionsFile=ogen.generateIncOptionsTestingsEndPhase2(language, "nivreeager", AlgorithmTester.training80, optionMenosR, los.getLibraryOptions(), optionGRL, pcrOption, ppOption, allow_shift, allow_root);
		
		BufferedWriter bwOptionsNivreEager;
		
			try {
				bwOptionsNivreEager = new BufferedWriter(new FileWriter("incr_optionFile.xml"));
				bwOptionsNivreEager.write(optionsFile);
				bwOptionsNivreEager.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
		BufferedWriter bwPhase2HiddenLogFile;
		
		try {
			bwPhase2HiddenLogFile = new BufferedWriter(new FileWriter("phase2_logFile.txt"));
			bwPhase2HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase2HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase2HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase2HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase2HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase2HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase2HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase2HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase2HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase2HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase2HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase2HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase2HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase2HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase2HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			
			bwPhase2HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase2LogFile;
		
		try {
			bwPhase2LogFile = new BufferedWriter(new FileWriter("phase2_optFile.txt"));
			bwPhase2LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase2LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase2LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase2LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase2LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase2LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase2LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase2LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase2LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the parsing algorithm selection phase for your");
		System.out.println("training set and saved the results for future use in phase2_logFile.txt. "); 
		System.out.println("Updated MaltParser options can be found in phase2_optFile.txt. If you want");
		System.out.println("to change any of these options, you should edit phase2_optFile.txt before.");
		System.out.println("you start the next optimization phase.\n");
		System.out.println("To proceed with Phase 3 (Feature Selection) run the following command:");
		System.out.println("java -jar MaltOptimizer.jar -p 3 -m <malt_path> -c <trainingCorpus>");

		System.exit(0);
		
		
		
	}
	
	
	public void runPhase25Fold() {
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 2: PARSING ALGORITHM SELECTION - 5-FOLD CROSS-VALIDATION\n");
		//System.out.println("Cross-validation\n");
		this.bestAlgorithm="nivreeager";
		
		if (this.noNonProjective) { //case: Strictly projective case (non projectivities==0)
			System.out.println("MaltOptimizer found in Phase 1 that your training set contains");
			System.out.println("no non-projective trees and will therefore only try projective parsing algorithms.\n");
			//System.out.println("Testing the no non projective algorithms ...");
			runStrictlyProjective5Fold();
		}
		else {
			if (this.substantialNonProjective) { //case non projectivities>15
				System.out.println("MaltOptimizer found in Phase 1 that your training set contains");
				System.out.println("a substantial amount of non-projective trees and will therefore \nonly try non-projective algorithms.\n");
				//System.out.println("Testing the non-projective algorithms ...");
				runLargeAmountNonProjective5Fold();
			}
			else if (this.smallCaseBothThings) { //case non projectivities <15
				System.out.println("MaltOptimizer found in Phase 1 that your training set contains");
				System.out.println("a small amount of non-projective trees and will therefore \ntry both projective and non-projective algorithms.\n");
				runStrictlyProjective5Fold();
				runLargeAmountNonProjective5Fold();
			}
		}
		System.out.println("-----------------------------------------------------------------------------");
		String bestAlgoPrintOut=bestAlgorithm;
		if (bestAlgorithm.equals("nivreeager")) {
			bestAlgoPrintOut="NivreEager";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			bestAlgoPrintOut="NivreStandard";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			bestAlgoPrintOut="CovingtonNonProjective";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			bestAlgoPrintOut="CovingtonProjective";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			bestAlgoPrintOut="StackProjective";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			bestAlgoPrintOut="StackEager";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			bestAlgoPrintOut="StackLazy";	
		}
		if (Optimizer.usePPOption) {
			System.out.println("MaltOptimizer found that the best parsing algorithm is: "+bestAlgorithm +"+ pp option");
		}
		else System.out.println("MaltOptimizer found that the best parsing algorithm is: "+bestAlgorithm);
		
		//after this: Run algorithm specific parameters with the best one.
		//nivre*----> rootHandling (normal|strict|relaxed)
		//cov*-----> allowshift (true|false) and allowroot(true|false)
		//stack*---> don't have any
		//if -pp is better, test (baseline | head | path | head+path)
		CoNLLHandler ch =new CoNLLHandler(this.trainingCorpus);
		AlgorithmTester at=new AlgorithmTester(this.language,ch,this.trainingCorpus);
		Double difference=0.0;
		
		
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		
		if (bestAlgorithm.contains("cov")){
			//allow_shift test2
			if (bestAlgorithm.equals("covproj")){
				System.out.println("Testing the Covington--Projective algorithm ...");
				Double covprojLAS=0.0;
				if (this.usePPOption) {
					covprojLAS=runCovingtonProjectivePPAllowShiftAllowRoot5Fold("CovingtonProjective.xml","head",true, allow_root);
				}
				else {
				  covprojLAS=runCovingtonProjectiveAllowShiftAllowRoot5Fold("CovingtonProjective.xml",true, allow_root);
				}
				if (covprojLAS>(this.bestResult+threshold)) {
					this.bestAlgorithm="covproj";
					difference=covprojLAS-bestResult;
					this.bestResult=covprojLAS;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					this.allow_shift=true;
					System.out.println("New allow_shift option: true");
					this.bestAlgorithm="covproj";
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+this.bestResult+"%)");
					
				}
				if (this.usePPOption) {
					covprojLAS=runCovingtonProjectivePPAllowShiftAllowRoot5Fold("CovingtonProjective.xml","head",allow_shift, false);
				}
				else {
				  covprojLAS=runCovingtonProjectiveAllowShiftAllowRoot5Fold("CovingtonProjective.xml",allow_shift, false);
				}
				if (covprojLAS>(this.bestResult+threshold)) {
					this.bestAlgorithm="covproj";
					difference=covprojLAS-bestResult;
					this.bestResult=covprojLAS;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					this.allow_root=false;
					System.out.println("New allow_root option: false");
					this.bestAlgorithm="covproj";
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+this.bestResult+"%)");
					
				}
				
			}
			else {
				if (bestAlgorithm.equals("covnonproj")){
					System.out.println("Testing the Covington-Non-Projective algorithm ...");
					Double covnonprojLAS=0.0;
					if (this.usePPOption) {
						//covnonprojLAS=at.executeCovingtonNonProjectivePPAllowShiftAllowRoot5Fold("CovingtonNonProjective.xml","head",true, allow_root);
						covnonprojLAS=runCovingtonNonProjectivePPAllowShiftAllowRoot5Fold("CovingtonNonProjective.xml","head",true, allow_root);
					}
					else {
					  covnonprojLAS=runCovingtonNonProjectiveAllowShiftAllowRoot5Fold("CovingtonNonProjective.xml",true, allow_root);
					}
					if (covnonprojLAS>(this.bestResult+threshold)) {
						this.bestAlgorithm="covnonproj";
						difference=covnonprojLAS-bestResult;
						this.bestResult=covnonprojLAS;
						String sDifferenceLabel=""+difference;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						this.allow_shift=true;
						System.out.println("New allow_shift option: true");
						this.bestAlgorithm="covnonproj";
						System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+this.bestResult+"%)");
						
					}
					if (this.usePPOption) {
						covnonprojLAS=runCovingtonNonProjectivePPAllowShiftAllowRoot5Fold("CovingtonNonProjective.xml","head",allow_shift, false);
					}
					else {
					  covnonprojLAS=runCovingtonNonProjectiveAllowShiftAllowRoot5Fold("CovingtonNonProjective.xml",allow_shift, false);
					}
					if (covnonprojLAS>(this.bestResult+threshold)) {
						this.bestAlgorithm="covnonproj";
						difference=covnonprojLAS-bestResult;
						this.bestResult=covnonprojLAS;
						String sDifferenceLabel=""+difference;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						this.allow_root=false;
						System.out.println("New allow_root option: false");
						this.bestAlgorithm="covnonproj";
						System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+this.bestResult+"%)");
						
					}
					
				}
			}
		}
		
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		
		/*if (bestAlgorithm.contains("nivre") && numRootLabels==1) {
			System.out.println("Root handling testing ...");
			optionMenosR=at.executeRootHandlingTest(bestAlgorithm);
			if (!optionMenosR.equals("normal")){
				System.out.println("Default root handling strategy reset: "+optionMenosR);
			}
			
		}*/
		
		if (bestAlgorithm.contains("nivre")) {// && numRootLabels==1) {
			
			/*System.out.println("Root handling testing ...");
			optionMenosR=at.executeRootHandlingTest(bestAlgorithm);
			if (!optionMenosR.equals("normal")){
				System.out.println("Default root handling strategy reset: "+optionMenosR);
			}*/
			
			/////////////////////////NEW FOR VERSION 1.7 //////////
			///allow_root and allow_reduce
			
			//allow_root=true  --- allow_reduce=false  (DEFAULT-> No need to run)
			//allow_root=false --- allow_reduce=false
			//allow_root=true  --- allow_reduce=true
			//allow_root=False --- allow_reduce=true
			
			System.out.println("Root Handling testing ...");
			at.executeRootHandlingTestNivre17(bestAlgorithm);
			System.out.println("Root handling selected strategy:");
			System.out.println("\t"+"allow_root: "+allow_rootNiv);
			System.out.println("\t"+"allow_reduce: "+allow_reduceNiv);
			
			
			
			
			////////////////////////////////////////////////////////
			
		}
		
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		if (usePPOption){ 
			System.out.println("Testing pseudo-projective (PP) options ...");
			ppOption=at.executePPTest(bestAlgorithm);
			if (!ppOption.equals("head")){
				System.out.println("Default marking strategy reset: "+ppOption);
			}
		}
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		if (sDifferenceLabel.equals("0.0")){
			System.out.println("Incremental improvement over the baseline at the end of Phase 2: 0.00% ("+this.bestResult+"%) ");
		}
		else
			System.out.println("Incremental improvement over the baseline at the end of Phase 2: +"+sDifferenceLabel+"% ("+this.bestResult+"%) ");
		
		
		OptionsGenerator ogen=new OptionsGenerator();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel) 
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr, String pp, boolean allowShift, boolean allowROOT) {
		String optionsFile=ogen.generateIncOptionsTestingsEndPhase2(language, "nivreeager", AlgorithmTester.training80, optionMenosR, los.getLibraryOptions(), optionGRL, pcrOption, ppOption, allow_shift, allow_root);
		
		BufferedWriter bwOptionsNivreEager;
		
			try {
				bwOptionsNivreEager = new BufferedWriter(new FileWriter("incr_optionFile.xml"));
				bwOptionsNivreEager.write(optionsFile);
				bwOptionsNivreEager.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
		BufferedWriter bwPhase2HiddenLogFile;
		
		try {
			bwPhase2HiddenLogFile = new BufferedWriter(new FileWriter("phase2_logFile.txt"));
			bwPhase2HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase2HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase2HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase2HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase2HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase2HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase2HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase2HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase2HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase2HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase2HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase2HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase2HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase2HiddenLogFile.write("allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase2HiddenLogFile.write("allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			
			bwPhase2HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase2LogFile;
		
		try {
			bwPhase2LogFile = new BufferedWriter(new FileWriter("phase2_optFile.txt"));
			bwPhase2LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase2LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase2LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase2LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase2LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase2LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase2LogFile.write("7. allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase2LogFile.write("8. allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase2LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the parsing algorithm selection phase for your");
		System.out.println("training set and saved the results for future use in phase2_logFile.txt. "); 
		System.out.println("Updated MaltParser options can be found in phase2_optFile.txt. If you want");
		System.out.println("to change any of these options, you should edit phase2_optFile.txt before.");
		System.out.println("you start the next optimization phase.\n");
		System.out.println("To proceed with Phase 3 (Feature Selection) run the following command:");
		System.out.println("java -jar MaltOptimizer.jar -p 3 -m <malt_path> -c <trainingCorpus>");

		System.exit(0);
		
		
		
	}
	
	
	/*public ArrayList<String> getThreeFrequent(HashMap<String,Double>()) {
		
		return null;
	}*/
	
	private void runLargeAmountNonProjective() {
		// TODO Auto-generated method stub
		
		Double bestNonProjective=0.0;
		String bestAlgNonProjective="";
		System.out.println("Testing the non-projective algorithms ...");
		System.out.println("");
		System.out.println("               CovingtonNonProjective --vs-- StackLazy");
		System.out.println("                          /                     \\");
		System.out.println("                         /                       \\");
		System.out.println("                        /                         \\");
		System.out.println("                       /                           \\");
		System.out.println("                      /                             \\");
		System.out.println("                     /                               \\");
		System.out.println("                    /                                 \\");
		System.out.println("               NivreEager+PP             StackEager --vs-- StackProjective+PP");
		System.out.println("                    |                                  |");
		System.out.println("                    |                                  |");
		System.out.println("         CovingtonProjective+PP                 NivreStandard+PP");
		System.out.println("");
		System.out.println("");
		
		
		CoNLLHandler ch =new CoNLLHandler(this.trainingCorpus);
		AlgorithmTester at=new AlgorithmTester(this.language,ch,this.trainingCorpus);
		Double difference=0.0;
		
		System.out.println("Testing the Covington-Non-Projective algorithm ...");
		Double covnonprojLAS=at.executeCovingtonNonProjective("CovingtonNonProjective.xml");
		bestNonProjective=covnonprojLAS;
		bestAlgNonProjective="covnonproj";
		if (covnonprojLAS>(this.bestResult)) {
			difference=covnonprojLAS-bestResult;
			this.bestResult=covnonprojLAS;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best algorithm: covnonproj");
			this.bestAlgorithm="covnonproj";
			
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		
		System.out.println("Testing the StackLazy algorithm ...");
		Double stacklazyLAS=at.executeStackLazy("StackSwap.xml");
		if (stacklazyLAS>bestNonProjective) {
			bestNonProjective=stacklazyLAS;
			bestAlgNonProjective="stacklazy";
		}
		if (stacklazyLAS>(this.bestResult)) {
			difference=stacklazyLAS-bestResult;
			this.bestResult=stacklazyLAS;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best algorithm: stacklazy");
			this.bestAlgorithm="stacklazy";
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		
		if (bestAlgNonProjective.equals("covnonproj")) {
		//if (bestAlgNonProjective.equals("nivreeager")) {
			System.out.println("Testing the NivreEager algorithm with pseudo-projective parsing (PP) ...");
			Double nivreEagerLAS=at.executeNivreEagerPPOption("NivreEager.xml",this.ppOption);
			if (nivreEagerLAS>(this.bestResult)) {
				difference=nivreEagerLAS-bestResult;
				this.bestResult=nivreEagerLAS;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best algorithm: nivreeager");
				this.bestAlgorithm="nivreeager";
				this.usePPOption=true;
				String s=""+this.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				System.out.println("Testing the Covington-Projective algorithm with pseudo-projective parsing (PP) ...");
				Double covprojLAS=at.executecovprojPPOption("CovingtonProjective.xml",this.ppOption);
				if (covprojLAS>(this.bestResult)) {
					this.bestAlgorithm="covproj";
					difference=covprojLAS-bestResult;
					this.bestResult=covprojLAS;
					String sDifferenceLabel2=""+difference;
					if (sDifferenceLabel2.length()>5)
						sDifferenceLabel2=sDifferenceLabel2.substring(0, 5);
					System.out.println("New best algorithm: covproj + pp option");
					this.bestAlgorithm="covproj";
					this.usePPOption=true;
					s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel2+"% ("+s+"%)");
					
				}
				
				
			}
		}
		else if (bestAlgNonProjective.equals("stacklazy")) {
			
			System.out.println("Testing the StackEager algorithm ...");
			Double stackeagerLAS=at.executestackEager("StackSwap.xml");
			if (stackeagerLAS>(this.bestResult)) {
				difference=stackeagerLAS-bestResult;
				this.bestResult=stackeagerLAS;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best algorithm: stackeager");
				this.bestAlgorithm="stackeager";
				String s=""+this.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
			System.out.println("Testing the StackProjective algorithm with pseudo-projective parsing (PP) ...");
			Double stackprojLAS=at.executestackprojPPOption("StackProjective.xml",this.ppOption);
			if (stackprojLAS>(this.bestResult)) {
				this.bestAlgorithm="stackproj";
				difference=stackprojLAS-bestResult;
				this.bestResult=stackprojLAS;
				String sDifferenceLabel2=""+difference;
				if (sDifferenceLabel2.length()>5)
					sDifferenceLabel2=sDifferenceLabel2.substring(0, 5);
				System.out.println("New best algorithm: stackproj + pp option");
				this.bestAlgorithm="stackproj";
				this.usePPOption=true;
				String s=""+this.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel2+"% ("+s+"%)");
				
			}
			
			if (this.bestAlgorithm.equals("stackproj") && (this.usePPOption==true)) {
				
				System.out.println("Testing the NivreStandard algorithm with pseudo-projective parsing (PP) ...");
				Double nivrestandardLAS=at.executenivrestandardPPOption("NivreStandard.xml",this.ppOption);
				if (nivrestandardLAS>(this.bestResult)) {
					this.bestAlgorithm="nivrestandard";
					difference=nivrestandardLAS-bestResult;
					this.bestResult=nivrestandardLAS;
					String sDifferenceLabel2=""+difference;
					if (sDifferenceLabel2.length()>5)
						sDifferenceLabel2=sDifferenceLabel2.substring(0, 5);
					System.out.println("New best algorithm: nivrestandard + pp option");
					this.bestAlgorithm="nivrestandard";
					this.usePPOption=true;
					String s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel2+"% ("+s+"%)");
					
				}
			}
			
			
		}
		
		String bestAlgoPrintOut=bestAlgNonProjective;
		if (bestAlgorithm.equals("nivreeager")) {
			bestAlgoPrintOut="NivreEager";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			bestAlgoPrintOut="NivreStandard";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			bestAlgoPrintOut="CovingtonNonProjective";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			bestAlgoPrintOut="CovingtonProjective";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			bestAlgoPrintOut="StackProjective";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			bestAlgoPrintOut="StackEager";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			bestAlgoPrintOut="StackLazy";	
		}
		System.out.println("Best Non-Projective algorithm: "+bestAlgoPrintOut+"\n");
		
		
		
	}
	
	
	private void runLargeAmountNonProjective5Fold() {
		// TODO Auto-generated method stub
		
		Double bestNonProjective=0.0;
		String bestAlgNonProjective="";
		System.out.println("Testing the non-projective algorithms ...");
		System.out.println("");
		System.out.println("               CovingtonNonProjective --vs-- StackLazy");
		System.out.println("                          /                     \\");
		System.out.println("                         /                       \\");
		System.out.println("                        /                         \\");
		System.out.println("                       /                           \\");
		System.out.println("                      /                             \\");
		System.out.println("                     /                               \\");
		System.out.println("                    /                                 \\");
		System.out.println("               NivreEager+PP             StackEager --vs-- StackProjective+PP");
		System.out.println("                    |                                  |");
		System.out.println("                    |                                  |");
		System.out.println("         CovingtonProjective+PP                 NivreStandard+PP");
		System.out.println("");
		System.out.println("");
		
		
		CoNLLHandler ch =new CoNLLHandler(this.trainingCorpus);
		AlgorithmTester at=new AlgorithmTester(this.language,ch,this.trainingCorpus);
		Double difference=0.0;
		
		System.out.println("Testing the Covington-Non-Projective algorithm ...");
		Double covnonprojLAS=this.runAlgorithm5Fold("CovingtonNonProjective.xml","covnonproj");
		bestNonProjective=covnonprojLAS;
		bestAlgNonProjective="covnonproj";
		if (covnonprojLAS>(this.bestResult)) {
			difference=covnonprojLAS-bestResult;
			this.bestResult=covnonprojLAS;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best algorithm: covnonproj");
			this.bestAlgorithm="covnonproj";
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		
		System.out.println("Testing the StackLazy algorithm ...");
		Double stacklazyLAS=runAlgorithm5Fold("StackSwap.xml","stacklazy");
		if (stacklazyLAS>bestNonProjective) {
			bestNonProjective=stacklazyLAS;
			bestAlgNonProjective="stacklazy";
		}
		if (stacklazyLAS>(this.bestResult)) {
			difference=stacklazyLAS-bestResult;
			this.bestResult=stacklazyLAS;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best algorithm: stacklazy");
			this.bestAlgorithm="stacklazy";
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		
		if (bestAlgNonProjective.equals("covnonproj")) {
		//if (bestAlgNonProjective.equals("nivreeager")) {
			System.out.println("Testing the NivreEager algorithm with pseudo-projective parsing (PP) ...");
			//Double nivreEagerLAS=at.executeNivreEagerPPOption5Fold("NivreEager.xml",this.ppOption);
			Double nivreEagerLAS=this.runAlgorithm5FoldPPOption("NivreEager.xml", "nivreeager");
			if (nivreEagerLAS>(this.bestResult)) {
				difference=nivreEagerLAS-bestResult;
				this.bestResult=nivreEagerLAS;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best algorithm: nivreeager");
				this.bestAlgorithm="nivreeager";
				this.usePPOption=true;
				String s=""+this.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				System.out.println("Testing the Covington-Projective algorithm with pseudo-projective parsing (PP) ...");
				Double covprojLAS=this.runAlgorithm5FoldPPOption("CovingtonProjective.xml","covproj");
				if (covprojLAS>(this.bestResult)) {
					this.bestAlgorithm="covproj";
					difference=covprojLAS-bestResult;
					this.bestResult=covprojLAS;
					String sDifferenceLabel2=""+difference;
					if (sDifferenceLabel2.length()>5)
						sDifferenceLabel2=sDifferenceLabel2.substring(0, 5);
					System.out.println("New best algorithm: covproj + pp option");
					this.bestAlgorithm="covproj";
					this.usePPOption=true;
					s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel2+"% ("+s+"%)");
					
				}
				
				
			}
		}
		else if (bestAlgNonProjective.equals("stacklazy")) {
			
			System.out.println("Testing the StackEager algorithm ...");
			//Double stackeagerLAS=at.executestackEager5Fold("StackSwap.xml");
			Double stackeagerLAS=runAlgorithm5Fold("StackSwap.xml","stackeager");
			if (stackeagerLAS>(this.bestResult)) {
				difference=stackeagerLAS-bestResult;
				this.bestResult=stackeagerLAS;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best algorithm: stackeager");
				this.bestAlgorithm="stackeager";
				String s=""+this.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
			System.out.println("Testing the StackProjective algorithm with pseudo-projective parsing (PP) ...");
			Double stackprojLAS=runAlgorithm5FoldPPOption("StackProjective.xml","stackproj");
			if (stackprojLAS>(this.bestResult)) {
				this.bestAlgorithm="stackproj";
				difference=stackprojLAS-bestResult;
				this.bestResult=stackprojLAS;
				String sDifferenceLabel2=""+difference;
				if (sDifferenceLabel2.length()>5)
					sDifferenceLabel2=sDifferenceLabel2.substring(0, 5);
				System.out.println("New best algorithm: stackproj + pp option");
				this.bestAlgorithm="stackproj";
				this.usePPOption=true;
				String s=""+this.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel2+"% ("+s+"%)");
				
			}
			
			if (this.bestAlgorithm.equals("stackproj") && (this.usePPOption==true)) {
				
				System.out.println("Testing the NivreStandard algorithm with pseudo-projective parsing (PP) ...");
				Double nivrestandardLAS=runAlgorithm5FoldPPOption("NivreStandard.xml","nivrestandard");
				if (nivrestandardLAS>(this.bestResult)) {
					this.bestAlgorithm="nivrestandard";
					difference=nivrestandardLAS-bestResult;
					this.bestResult=nivrestandardLAS;
					String sDifferenceLabel2=""+difference;
					if (sDifferenceLabel2.length()>5)
						sDifferenceLabel2=sDifferenceLabel2.substring(0, 5);
					System.out.println("New best algorithm: nivrestandard + pp option");
					this.bestAlgorithm="nivrestandard";
					this.usePPOption=true;
					String s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel2+"% ("+s+"%)");
					
				}
			}
			
			
		}
		
		String bestAlgoPrintOut=bestAlgNonProjective;
		if (bestAlgorithm.equals("nivreeager")) {
			bestAlgoPrintOut="NivreEager";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			bestAlgoPrintOut="NivreStandard";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			bestAlgoPrintOut="CovingtonNonProjective";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			bestAlgoPrintOut="CovingtonProjective";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			bestAlgoPrintOut="StackProjective";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			bestAlgoPrintOut="StackEager";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			bestAlgoPrintOut="StackLazy";	
		}
		System.out.println("Best Non-Projective algorithm: "+bestAlgoPrintOut+"\n");
		
		
	}

	private void runStrictlyProjective() {
		// TODO Auto-generated method stub
		
		//First: nivreeager against stackproj
		//if nivreeager better, test covproj
		//else test nivrestandard
		System.out.println("Testing projective algorithms ...");
		System.out.println("");
		System.out.println("                       NivreEager --vs-- StackProjective");
		System.out.println("                           /                  \\");
		System.out.println("                          /                    \\");
		System.out.println("                         /                      \\");
		System.out.println("                        /                        \\");
		System.out.println("                       /                          \\");
		System.out.println("                      /                            \\");
		System.out.println("                     /                              \\");
		System.out.println("            CovingtonProjective                 NivreStandard");
		System.out.println("");
		System.out.println("");
		
		
		
		CoNLLHandler ch =new CoNLLHandler(this.trainingCorpus);
		AlgorithmTester at=new AlgorithmTester(this.language,ch,this.trainingCorpus);
		Double difference=0.0;
		//this.bestAlgorithm="nivreeager";
		
		System.out.println("Testing the NivreEager algorithm ...");
		Double nivreEagerLAS=at.executeNivreEager("NivreEager.xml");
		if (nivreEagerLAS>(this.bestResult)) {
			difference=nivreEagerLAS-bestResult;
			this.bestResult=nivreEagerLAS;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best algorithm: nivreeager");
			this.bestAlgorithm="nivreeager";
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		
		System.out.println("Testing the StackProjective algorithm ...");
		Double stackprojLAS=at.executeStackProjective("StackProjective.xml");
		if (stackprojLAS>(this.bestResult)) {
			this.bestAlgorithm="stackproj";
			difference=stackprojLAS-bestResult;
			this.bestResult=stackprojLAS;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best algorithm: stackproj");
			this.bestAlgorithm="stackproj";
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		
		if (this.bestAlgorithm.equals("stackproj")) {
			System.out.println("Testing the NivreStandard algorithm ...");
			Double nivrestandardLAS=at.executeNivreStandard("NivreStandard.xml");
			if (nivrestandardLAS>(this.bestResult)) {
				this.bestAlgorithm="nivrestandard";
				difference=nivrestandardLAS-bestResult;
				this.bestResult=nivrestandardLAS;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best algorithm: nivrestandard");
				this.bestAlgorithm="nivrestandard";
				String s=""+this.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
		else {
			System.out.println("Testing the Covington--Projective algorithm ...");
			Double covprojLAS=at.executeCovingtonProjective("CovingtonProjective.xml");
			if (covprojLAS>(this.bestResult)) {
				this.bestAlgorithm="covproj";
				difference=covprojLAS-bestResult;
				this.bestResult=covprojLAS;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best algorithm: covproj");
				this.bestAlgorithm="covproj";
				String s=""+this.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
		
		String bestAlgoPrintOut=bestAlgorithm;
		if (bestAlgorithm.equals("nivreeager")) {
			bestAlgoPrintOut="NivreEager";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			bestAlgoPrintOut="NivreStandard";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			bestAlgoPrintOut="CovingtonNonProjective";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			bestAlgoPrintOut="CovingtonProjective";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			bestAlgoPrintOut="StackProjective";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			bestAlgoPrintOut="StackEager";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			bestAlgoPrintOut="StackLazy";	
		}
		System.out.println("Best projective algorithm: "+bestAlgoPrintOut+"\n");
	}
	
	
	private void runStrictlyProjective5Fold() {
		// TODO Auto-generated method stub
		
		//First: nivreeager against stackproj
		//if nivreeager better, test covproj
		//else test nivrestandard
		System.out.println("Testing projective algorithms ...");
		System.out.println("");
		System.out.println("                       NivreEager --vs-- StackProjective");
		System.out.println("                           /                  \\");
		System.out.println("                          /                    \\");
		System.out.println("                         /                      \\");
		System.out.println("                        /                        \\");
		System.out.println("                       /                          \\");
		System.out.println("                      /                            \\");
		System.out.println("                     /                              \\");
		System.out.println("            CovingtonProjective                 NivreStandard");
		System.out.println("");
		System.out.println("");
		
		
		
		CoNLLHandler ch =new CoNLLHandler(this.trainingCorpus);
		AlgorithmTester at=new AlgorithmTester(this.language,ch,this.trainingCorpus);
		Double difference=0.0;
		//this.bestAlgorithm="nivreeager";
		
		System.out.println("Testing the NivreEager algorithm ...");
		Double nivreEagerLAS=runAlgorithm5Fold("NivreEager.xml","nivreeager");
		if (nivreEagerLAS>(this.bestResult)) {
			difference=nivreEagerLAS-bestResult;
			this.bestResult=nivreEagerLAS;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best algorithm: nivreeager");
			this.bestAlgorithm="nivreeager";
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		
		System.out.println("Testing the StackProjective algorithm ...");
		Double stackprojLAS=runAlgorithm5Fold("StackProjective.xml","stackproj");
		if (stackprojLAS>(this.bestResult)) {
			this.bestAlgorithm="stackproj";
			difference=stackprojLAS-bestResult;
			this.bestResult=stackprojLAS;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best algorithm: stackproj");
			this.bestAlgorithm="stackproj";
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		
		if (this.bestAlgorithm.equals("stackproj")) {
			System.out.println("Testing the NivreStandard algorithm ...");
			Double nivrestandardLAS=runAlgorithm5Fold("NivreStandard.xml","nivrestandard");
			if (nivrestandardLAS>(this.bestResult)) {
				this.bestAlgorithm="nivrestandard";
				difference=nivrestandardLAS-bestResult;
				this.bestResult=nivrestandardLAS;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best algorithm: nivrestandard");
				this.bestAlgorithm="nivrestandard";
				String s=""+this.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
		else {
			System.out.println("Testing the Covington--Projective algorithm ...");
			Double covprojLAS=runAlgorithm5Fold("CovingtonProjective.xml","covproj");
			if (covprojLAS>(this.bestResult)) {
				this.bestAlgorithm="covproj";
				difference=covprojLAS-bestResult;
				this.bestResult=covprojLAS;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best algorithm: covproj");
				this.bestAlgorithm="covproj";
				String s=""+this.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
		
		String bestAlgoPrintOut=bestAlgorithm;
		if (bestAlgorithm.equals("nivreeager")) {
			bestAlgoPrintOut="NivreEager";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			bestAlgoPrintOut="NivreStandard";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			bestAlgoPrintOut="CovingtonNonProjective";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			bestAlgoPrintOut="CovingtonProjective";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			bestAlgoPrintOut="StackProjective";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			bestAlgoPrintOut="StackEager";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			bestAlgoPrintOut="StackLazy";	
		}
		System.out.println("Best projective algorithm: "+bestAlgoPrintOut+"\n");
	}
	
	private void loadPhase2Results(String pathTrainingSet) {
		// TODO Auto-generated method stub
		
		// TODO Auto-generated method stub
				//phase1_optFile.txt
				//phase1_logFile.txt
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader("phase2_logFile.txt"));
				try {
					int contador=0;
					while(br.ready()){
						String line;
						try {
							line = br.readLine();
							StringTokenizer st=new StringTokenizer(line,":");
							String tok="";
							while(st.hasMoreTokens()){
								tok=st.nextToken();
							}
							contador++;
							if (contador==1) {
								if (pathTrainingSet.equals(tok)) {
									this.setTrainingCorpus(tok);
									//System.out.println(tok);
								}
								else {
									try {
										throw new PathNotFoundException();
									} catch (PathNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
							if (contador==2) {
								Integer nt=Integer.parseInt(tok);
								this.setNumbTokens(nt);
								//System.out.println(nt);
							}
							if (contador==3) {
								Integer nt=Integer.parseInt(tok);
								this.setNumbSentences(nt);
								//System.out.println(nt);
							}
							if (contador==4) {
								Double nt=Double.parseDouble(tok);
								this.setPercentage(nt);
								//System.out.println(nt);
								if (nt==0.0){
									this.setNoNonProjective(true);
								}
								else {
									if (nt>15) {
										this.setSubstantialNonProjective(true);
									}
									else {
										this.setSmallCaseBothThings(true);
									}
								}
								
								
							}
							if (contador==5) {
								Integer it=Integer.parseInt(tok);
								if (it>0) this.setDanglingPunctuation(true);
								this.setNumbDanglingCases(it);
								//System.out.println(it);
							}
							if (contador==6) {
								Double nt=Double.parseDouble(tok);
								this.setBestResult(nt);
								//System.out.println(nt);
							}
							if (contador==7) {
								Double nt=Double.parseDouble(tok);
								this.setDefaultBaseline(nt);
								//System.out.println(nt);
							}
							if (contador==8) {
								Integer nt=Integer.parseInt(tok);
								this.numRootLabels=nt;
								//System.out.println(nt);
							}
							if (contador==9) {
								this.javaHeapValue=tok;
								//System.out.println(nt);
							}
							
							if (contador==10) {
								//this.javaHeapValue=tok;
								//System.out.println(nt);
							}
							
							if (contador==11) {
								if (tok.equals("true"))
									cposEqPos=true;
								else
									cposEqPos=false;
								//System.out.println(nt);
							}
							if (contador==12) {
								if (tok.equals("true"))
									lemmaBlank=true;
								else
									lemmaBlank=false;
							}
							if (contador==13) {
								if (tok.equals("true"))
									featsBlank=true;
								else
									featsBlank=false;
								//System.out.println(featsBlank);
							}
							
							/*if (contador==14) {
								if (tok.equals("true"))
									allow_rootNiv=true;
								else
									allow_rootNiv=false;
								//System.out.println("allow_root:"+allow_rootNiv);
							}
							
							if (contador==15) {
								if (tok.equals("true"))
									allow_reduceNiv=true;
								else
									allow_reduceNiv=false;
								//System.out.println("allow_reduce:"+allow_reduceNiv);
							}*/
							
							
							
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				BufferedReader brOpt;
				try {
					brOpt = new BufferedReader(new FileReader("phase2_optFile.txt"));
				try {
					int contador=0;
					boolean grl=false;
					boolean pcr=false;
					boolean algo=false;
					boolean pp=false;
					boolean cs=false;
					boolean cr=false;
					boolean menosr=false;
					boolean nr=false;
					boolean ne=false;
					while(brOpt.ready()){
						String line;
						try {
							line = brOpt.readLine();
							StringTokenizer st=new StringTokenizer(line,":");
							grl=false;
							pcr=false;
							algo=false;
							pp=false;
							cr=false;
							cs=false;
							menosr=false;
							nr=false;
							nr=false;
							if (line.contains("-grl")) grl=true;
							if (line.contains("-pcr")) pcr=true;
							if (line.contains("-a")) algo=true;
							if (line.contains("-pp")) pp=true;
							if (line.contains("-cs")) cs=true;
							if (line.contains("-cr")) cr=true;
							if (line.contains("-r")) menosr=true;
							if (line.contains("-nr")) nr=true;
							if (line.contains("-ne")) ne=true;
							String tok="";
							while(st.hasMoreTokens()){
								tok=st.nextToken();
							}
							contador++;

							
							if (grl) {
								this.setOptionGRL(tok);
								//System.out.println(line);
								grl=false;
							}
							if (pcr) {
								this.setPcrOption(tok);
								//System.out.println(line);
								pcr=false;
							}
							if (algo) {
								this.setBestAlgorithm(tok);
								//System.out.println(line);
								algo=false;
							}
							if (pp) {
								this.usePPOption=true;
								this.setPpOption(tok);
								//System.out.println(line);
								//System.out.println(this.getPpOption());
								pp=false;
							}
							if (cs) {
								allow_shift=true;
								//System.out.println(line);
								cs=false;
							}
							if (cr) {
								allow_root=true;
								//System.out.println(line);
								cr=false;
							}
							if (menosr) {
								optionMenosR=tok;
								//System.out.println(line);
								menosr=false;
							}
							if (nr) {
								Optimizer.allow_rootNiv=Boolean.parseBoolean(tok);
								//System.out.println(line);
								menosr=false;
							}
							if (ne) {
								//optionMenosR=tok;
								Optimizer.allow_reduceNiv=Boolean.parseBoolean(tok);
								//System.out.println(line);
								menosr=false;
							}
							//System.out.println(allow_rootNiv);
							//System.out.println(allow_reduceNiv);
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					
	}
	
	private void runPhase3() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 3: FEATURE SELECTION\n");

		
		System.out.println("MaltOptimizer is going to perform the following feature selection experiments:");
		System.out.println("1. Tune the window of POSTAG n-grams over the parser state.");
		System.out.println("2. Tune the window of FORM features over the parser state.");
		System.out.println("3. Tune DEPREL and POSTAG features over the partially built dependency tree.");
		System.out.println("4. Add POSTAG and FORM features over the input string.");
		System.out.println("5. Add CPOSTAG, FEATS, and LEMMA features if available.");
		System.out.println("6. Add conjunctions of POSTAG and FORM features.");
		System.out.println("-----------------------------------------------------------------------------");
		
		if (bestAlgorithm.equals("nivreeager")) {
			featureModel="NivreEager.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			featureModel="NivreStandard.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			featureModel="CovingtonNonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			featureModel="CovingtonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			featureModel="StackProjective.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			featureModel="StackSwap.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			featureModel="StackSwap.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Lookahead";
			//InputLookAhead="Input";
			
		}
		
		System.out.println("1. Tuning the window of POSTAG n-grams ... \n");
		postagTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("2. Tuning the window of FORM features ... \n");
		formTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");

		
		System.out.println("3. Tuning dependency tree features ... \n");
		deprelTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		
		System.out.println("4. Adding string features ... \n");
		predeccessorSuccessor("POSTAG");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		if (!cposEqPos || !lemmaBlank || !featsBlank) {
		System.out.println("5. Adding CPOSTAG, FEATS, and LEMMA features ... ");
		if (!cposEqPos) {
			//System.out.println("CPostag and Postag are distinct in your corpus.");
			//System.out.println("Adding CPOSTAG Features ... ");
			addNewFeaturesCpostagFeatsLemma("CPOSTAG");
		}
		
		if (!lemmaBlank) {
			//System.out.println("\nBest feature model: "+featureModel);
			//System.out.println("-----------------------------------------------------------------------------");
			//System.out.println("Lemma column is used in your training set.");
			//System.out.println("Adding LEMMA Features ... ");
			addNewFeaturesCpostagFeatsLemma("LEMMA");
		}
		if (!featsBlank) {
			//System.out.println("\nBest feature model: "+featureModel);
			//System.out.println("-----------------------------------------------------------------------------");
			//System.out.println("Feats column is used in your training set.");
			//System.out.println("Adding FEATS Features ... ");
			addSplitFeaturesFeats("FEATS");
			
		}

		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		}
		
		
		System.out.println("6. Adding conjunctions of POSTAG and FORM features... \n");
		addConjunctionFeatures("POSTAG","FORM");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has concluded feature selection and is going to tune the SVM cost parameter.\n");
		this.runPhase4SimplifiedVersion();
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
		OptionsGenerator og=new OptionsGenerator(language, this.trainingCorpus);
		String optionsCat=og.generateIncOptionsTestingsPhases(language, this.bestAlgorithm, this.trainingCorpus, Optimizer.optionMenosR, this.libraryValue, Optimizer.optionGRL, Optimizer.pcrOption);
		String optionsNivreEager="finalOptionsFile.xml";
		BufferedWriter bwOptionsNivreEager;
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase3_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase3_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		String s=""+this.bestResult;
		if (s.length()==4) s+="0";
		
		System.out.println("Incremental improvement over the baseline at the end of Phase 3: + "+sDifferenceLabel+"% ("+s+")");
	
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the feature model testing phase using your training set,");
		System.out.println("it saved the results for future use in phase3_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in phase3_optFile.txt.");
		System.out.println("");
		//System.out.println("To proceed with Phase 4 (Library Parameters) run the following command:");
		//System.out.println("java -jar MaltOptimizer.jar -p 4 -m <malt_path> -c <trainingCorpus>");
		
	}
	
	
	private void runPhase35Fold() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 3: FEATURE SELECTION - 5-FOLD CROSS-VALIDATION\n");

		
		System.out.println("MaltOptimizer is going to perform the following feature selection experiments:");
		System.out.println("1. Tune the window of POSTAG n-grams over the parser state.");
		System.out.println("2. Tune the window of FORM features over the parser state.");
		System.out.println("3. Tune DEPREL and POSTAG features over the partially built dependency tree.");
		System.out.println("4. Add POSTAG and FORM features over the input string.");
		System.out.println("5. Add CPOSTAG, FEATS, and LEMMA features if available.");
		System.out.println("6. Add conjunctions of POSTAG and FORM features.");
		System.out.println("-----------------------------------------------------------------------------");
		
		if (bestAlgorithm.equals("nivreeager")) {
			featureModel="NivreEager.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			featureModel="NivreStandard.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			featureModel="CovingtonNonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			featureModel="CovingtonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			featureModel="StackProjective.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			featureModel="StackSwap.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			featureModel="StackSwap.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Lookahead";
			//InputLookAhead="Input";
			
		}
		
		CoNLLHandler ch=new CoNLLHandler(this.trainingCorpus);
		//ch.generate5FoldCrossCorporaPseudo();
		
		System.out.println("1. Tuning the window of POSTAG n-grams ... \n");
		//postagTuning();
		
		postagTuning5Fold();
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("2. Tuning the window of FORM features ... \n");
		formTuning5Fold();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");

		
		System.out.println("3. Tuning dependency tree features ... \n");
		deprelTuning5Fold();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		
		System.out.println("4. Adding string features ... \n");
		predeccessorSuccessor5Fold("POSTAG");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		if (!cposEqPos || !lemmaBlank || !featsBlank) {
		System.out.println("5. Adding CPOSTAG, FEATS, and LEMMA features ... ");
		if (!cposEqPos) {
			//System.out.println("CPostag and Postag are distinct in your corpus.");
			//System.out.println("Adding CPOSTAG Features ... ");
			addNewFeaturesCpostagFeatsLemma5Fold("CPOSTAG");
		}
		
		if (!lemmaBlank) {
			//System.out.println("\nBest feature model: "+featureModel);
			//System.out.println("-----------------------------------------------------------------------------");
			//System.out.println("Lemma column is used in your training set.");
			//System.out.println("Adding LEMMA Features ... ");
			addNewFeaturesCpostagFeatsLemma5Fold("LEMMA");
		}
		if (!featsBlank) {
			//System.out.println("\nBest feature model: "+featureModel);
			//System.out.println("-----------------------------------------------------------------------------");
			//System.out.println("Feats column is used in your training set.");
			//System.out.println("Adding FEATS Features ... ");
			addSplitFeaturesFeats5Fold("FEATS");
			
		}

		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		}
		
		
		System.out.println("6. Adding conjunctions of POSTAG and FORM features... \n");
		addConjunctionFeatures5Fold("POSTAG","FORM");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has concluded feature selection and is going to tune the SVM cost parameter.\n");
		this.runPhase4SimplifiedVersion();
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
		OptionsGenerator og=new OptionsGenerator(language, this.trainingCorpus);
		String optionsCat=og.generateIncOptionsTestingsPhases(language, this.bestAlgorithm, this.trainingCorpus, Optimizer.optionMenosR, this.libraryValue, Optimizer.optionGRL, Optimizer.pcrOption);
		String optionsNivreEager="finalOptionsFile.xml";
		BufferedWriter bwOptionsNivreEager;
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase3_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase3_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		
		String s=""+this.bestResult;
		if (s.length()==4) s+="0";
				
		System.out.println("Incremental improvement over the baseline at the end of Phase 3: + "+sDifferenceLabel+"% ("+s+")");
	
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the feature model testing phase using your training set,");
		System.out.println("it saved the results for future use in phase3_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in phase3_optFile.txt.");
		System.out.println("");
		//System.out.println("To proceed with Phase 4 (Library Parameters) run the following command:");
		//System.out.println("java -jar MaltOptimizer.jar -p 4 -m <malt_path> -c <trainingCorpus>");
		
	}
	
	
	private void runPhase3AlternativeOrder1() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 3: FEATURE MODEL TUNING. ALTERNATIVE ORDER 1");

		
		System.out.println("MaltOptimizer is going to test the following:");
		System.out.println("1. Tune the window of POSTAG (n-gram) features over the stack and buffer.");
		System.out.println("2. Tune the window of (lexical) FORM features over the stack and buffer.");
		System.out.println("3. Tune dependency tree features using DEPREL and POSTAG features.");
		System.out.println("4. Add predecessor and successor features for salient tokens using POSTAG and FORM features.");
		System.out.println("5. Add CPOSTAG, FEATS,  and LEMMA features if available.");
		System.out.println("6. Add conjunctions of POSTAG and FORM features.");
		System.out.println("-----------------------------------------------------------------------------");
		
		if (bestAlgorithm.equals("nivreeager")) {
			featureModel="NivreEager.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			featureModel="NivreStandard.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			featureModel="CovingtonNonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			featureModel="CovingtonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			featureModel="StackProjective.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			featureModel="StackSwap.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			featureModel="StackSwap.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Lookahead";
			//InputLookAhead="Input";
			
		}
		
		System.out.println("6. Adding conjunctions of POSTAG and FORM features. ... ");
		addConjunctionFeatures("POSTAG","FORM");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("5. Add CPOSTAG, FEATS,  and LEMMA features if available. ... ");
		if (!cposEqPos) {
			System.out.println("CPostag and Postag are distinct in your corpus.");
			System.out.println("Adding Cpostag Features ...");
			addNewFeaturesCpostagFeatsLemma("CPOSTAG");
		}
		
		if (!lemmaBlank) {
			System.out.println("\nBest feature model: "+featureModel);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Lemma column is used in your training set.");
			System.out.println("Adding Lemma Features ...");
			addNewFeaturesCpostagFeatsLemma("LEMMA");
		}
		if (!featsBlank) {
			System.out.println("\nBest feature model: "+featureModel);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Feats column is used in your training set.");
			System.out.println("Adding Feats Features ...");
			addSplitFeaturesFeats("FEATS");
			
		}

		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("4. Adding predecessor and successor features using POSTAG and FORM features ... ");
		predeccessorSuccessor("POSTAG");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("3. Tuning dependency tree features (DEPREL) ... ");
		deprelTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("2. Tuning the window of FORM ... ");
		formTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("1. Tuning the window of POSTAG ... ");
		postagTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		
		this.runPhase4SimplifiedVersion();
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
		OptionsGenerator og=new OptionsGenerator(language, this.trainingCorpus);
		String optionsCat=og.generateIncOptionsTestingsPhases(language, this.bestAlgorithm, this.trainingCorpus, Optimizer.optionMenosR, this.libraryValue, Optimizer.optionGRL, Optimizer.pcrOption);
		String optionsNivreEager="finalOptionsFile.xml";
		BufferedWriter bwOptionsNivreEager;
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase3_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase3_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		System.out.println("At the end of Phase 3 MaltOptimizer achieves an increment of: + "+sDifferenceLabel+"% \ncompared with the default settings baseline.");
	
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the feature model testing phase using your training set,");
		System.out.println("it saved the results for future use in phase3_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in phase3_optFile.txt. If you want to change any of these options");
		System.out.println("you should edit phase3_optFile.txt before you start the next optimization phase.");
		System.out.println("");
		System.out.println("To proceed with Phase 4 (Library Parameters) run the following command:");
		System.out.println("java -jar MaltOptimizer.jar -p 4 -m <malt_path> -c <trainingCorpus>");
		
	}
	
	
	private void runPhase3AlternativeOrder2() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 3: FEATURE MODEL TUNING. ALTERNATIVE ORDER 2");

		
		System.out.println("MaltOptimizer is going to test the following:");
		System.out.println("1. Tune the window of POSTAG (n-gram) features over the stack and buffer.");
		System.out.println("2. Tune the window of (lexical) FORM features over the stack and buffer.");
		System.out.println("3. Tune dependency tree features using DEPREL and POSTAG features.");
		System.out.println("4. Add predecessor and successor features for salient tokens using POSTAG and FORM features.");
		System.out.println("5. Add CPOSTAG, FEATS,  and LEMMA features if available.");
		System.out.println("6. Add conjunctions of POSTAG and FORM features.");
		System.out.println("-----------------------------------------------------------------------------");
		
		if (bestAlgorithm.equals("nivreeager")) {
			featureModel="NivreEager.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			featureModel="NivreStandard.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			featureModel="CovingtonNonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			featureModel="CovingtonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			featureModel="StackProjective.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			featureModel="StackSwap.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			featureModel="StackSwap.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Lookahead";
			//InputLookAhead="Input";
			
		}
		
		
		
		
		System.out.println("4. Adding predecessor and successor features using POSTAG and FORM features ... ");
		predeccessorSuccessor("POSTAG");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("5. Add CPOSTAG, FEATS,  and LEMMA features if available. ... ");
		if (!cposEqPos) {
			System.out.println("CPostag and Postag are distinct in your corpus.");
			System.out.println("Adding Cpostag Features ...");
			addNewFeaturesCpostagFeatsLemma("CPOSTAG");
		}
		
		if (!lemmaBlank) {
			System.out.println("\nBest feature model: "+featureModel);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Lemma column is used in your training set.");
			System.out.println("Adding Lemma Features ...");
			addNewFeaturesCpostagFeatsLemma("LEMMA");
		}
		if (!featsBlank) {
			System.out.println("\nBest feature model: "+featureModel);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Feats column is used in your training set.");
			System.out.println("Adding Feats Features ...");
			addSplitFeaturesFeats("FEATS");
			
		}

		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		
		System.out.println("6. Adding conjunctions of POSTAG and FORM features. ... ");
		addConjunctionFeatures("POSTAG","FORM");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("1. Tuning the window of POSTAG ... ");
		postagTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("2. Tuning the window of FORM ... ");
		formTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");

		
		System.out.println("3. Tuning dependency tree features (DEPREL) ... ");
		deprelTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		this.runPhase4SimplifiedVersion();
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
		OptionsGenerator og=new OptionsGenerator(language, this.trainingCorpus);
		String optionsCat=og.generateIncOptionsTestingsPhases(language, this.bestAlgorithm, this.trainingCorpus, Optimizer.optionMenosR, this.libraryValue, Optimizer.optionGRL, Optimizer.pcrOption);
		String optionsNivreEager="finalOptionsFile.xml";
		BufferedWriter bwOptionsNivreEager;
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase3_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase3_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		System.out.println("At the end of Phase 3 MaltOptimizer achieves an increment of: + "+sDifferenceLabel+"% \ncompared with the default settings baseline.");
	
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the feature model testing phase using your training set,");
		System.out.println("it saved the results for future use in phase3_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in phase3_optFile.txt. If you want to change any of these options");
		System.out.println("you should edit phase3_optFile.txt before you start the next optimization phase.");
		System.out.println("");
		System.out.println("To proceed with Phase 4 (Library Parameters) run the following command:");
		System.out.println("java -jar MaltOptimizer.jar -p 4 -m <malt_path> -c <trainingCorpus>");
		
	}
	
	private void runPhase3AlternativeOrder3() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 3: FEATURE MODEL TUNING. ALTERNATIVE ORDER 3");

		
		System.out.println("MaltOptimizer is going to test the following:");
		System.out.println("1. Tune the window of POSTAG (n-gram) features over the stack and buffer.");
		System.out.println("2. Tune the window of (lexical) FORM features over the stack and buffer.");
		System.out.println("3. Tune dependency tree features using DEPREL and POSTAG features.");
		System.out.println("4. Add predecessor and successor features for salient tokens using POSTAG and FORM features.");
		System.out.println("5. Add CPOSTAG, FEATS,  and LEMMA features if available.");
		System.out.println("6. Add conjunctions of POSTAG and FORM features.");
		System.out.println("-----------------------------------------------------------------------------");
		
		if (bestAlgorithm.equals("nivreeager")) {
			featureModel="NivreEager.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			featureModel="NivreStandard.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			featureModel="CovingtonNonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			featureModel="CovingtonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			featureModel="StackProjective.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			featureModel="StackSwap.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			featureModel="StackSwap.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Lookahead";
			//InputLookAhead="Input";
			
		}
		
		System.out.println("1. Tuning the window of POSTAG ... ");
		postagTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("3. Tuning dependency tree features (DEPREL) ... ");
		deprelTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("2. Tuning the window of FORM ... ");
		formTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");

		System.out.println("5. Add CPOSTAG, FEATS,  and LEMMA features if available. ... ");
		if (!cposEqPos) {
			System.out.println("CPostag and Postag are distinct in your corpus.");
			System.out.println("Adding Cpostag Features ...");
			addNewFeaturesCpostagFeatsLemma("CPOSTAG");
		}
		
		if (!lemmaBlank) {
			System.out.println("\nBest feature model: "+featureModel);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Lemma column is used in your training set.");
			System.out.println("Adding Lemma Features ...");
			addNewFeaturesCpostagFeatsLemma("LEMMA");
		}
		if (!featsBlank) {
			System.out.println("\nBest feature model: "+featureModel);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Feats column is used in your training set.");
			System.out.println("Adding Feats Features ...");
			addSplitFeaturesFeats("FEATS");
			
		}

		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		
		System.out.println("4. Adding predecessor and successor features using POSTAG and FORM features ... ");
		predeccessorSuccessor("POSTAG");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		
		
		
		System.out.println("6. Adding conjunctions of POSTAG and FORM features. ... ");
		addConjunctionFeatures("POSTAG","FORM");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		this.runPhase4SimplifiedVersion();
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
		OptionsGenerator og=new OptionsGenerator(language, this.trainingCorpus);
		String optionsCat=og.generateIncOptionsTestingsPhases(language, this.bestAlgorithm, this.trainingCorpus, Optimizer.optionMenosR, this.libraryValue, Optimizer.optionGRL, Optimizer.pcrOption);
		String optionsNivreEager="finalOptionsFile.xml";
		BufferedWriter bwOptionsNivreEager;
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase3_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase3_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		System.out.println("At the end of Phase 3 MaltOptimizer achieves an increment of: + "+sDifferenceLabel+"% \ncompared with the default settings baseline.");
	
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the feature model testing phase using your training set,");
		System.out.println("it saved the results for future use in phase3_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in phase3_optFile.txt. If you want to change any of these options");
		System.out.println("you should edit phase3_optFile.txt before you start the next optimization phase.");
		System.out.println("");
		System.out.println("To proceed with Phase 4 (Library Parameters) run the following command:");
		System.out.println("java -jar MaltOptimizer.jar -p 4 -m <malt_path> -c <trainingCorpus>");
		
	}
	
	
	private void runPhase3AlternativeOrder4() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 3: FEATURE MODEL TUNING. ALTERNATIVE ORDER 4");

		
		System.out.println("MaltOptimizer is going to test the following:");
		System.out.println("1. Tune the window of POSTAG (n-gram) features over the stack and buffer.");
		System.out.println("2. Tune the window of (lexical) FORM features over the stack and buffer.");
		System.out.println("3. Tune dependency tree features using DEPREL and POSTAG features.");
		System.out.println("4. Add predecessor and successor features for salient tokens using POSTAG and FORM features.");
		System.out.println("5. Add CPOSTAG, FEATS,  and LEMMA features if available.");
		System.out.println("6. Add conjunctions of POSTAG and FORM features.");
		System.out.println("-----------------------------------------------------------------------------");
		
		if (bestAlgorithm.equals("nivreeager")) {
			featureModel="NivreEager.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			featureModel="NivreStandard.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			featureModel="CovingtonNonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			featureModel="CovingtonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			featureModel="StackProjective.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			featureModel="StackSwap.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			featureModel="StackSwap.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Lookahead";
			//InputLookAhead="Input";
			
		}
		
		System.out.println("6. Adding conjunctions of POSTAG and FORM features. ... ");
		addConjunctionFeatures("POSTAG","FORM");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("1. Tuning the window of POSTAG ... ");
		postagTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		
		System.out.println("5. Add CPOSTAG, FEATS,  and LEMMA features if available. ... ");
		if (!cposEqPos) {
			System.out.println("CPostag and Postag are distinct in your corpus.");
			System.out.println("Adding Cpostag Features ...");
			addNewFeaturesCpostagFeatsLemma("CPOSTAG");
		}
		
		if (!lemmaBlank) {
			System.out.println("\nBest feature model: "+featureModel);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Lemma column is used in your training set.");
			System.out.println("Adding Lemma Features ...");
			addNewFeaturesCpostagFeatsLemma("LEMMA");
		}
		if (!featsBlank) {
			System.out.println("\nBest feature model: "+featureModel);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Feats column is used in your training set.");
			System.out.println("Adding Feats Features ...");
			addSplitFeaturesFeats("FEATS");
			
		}

		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		
		System.out.println("4. Adding predecessor and successor features using POSTAG and FORM features ... ");
		predeccessorSuccessor("POSTAG");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("3. Tuning dependency tree features (DEPREL) ... ");
		deprelTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("2. Tuning the window of FORM ... ");
		formTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");

		
		
		
		
		
		
		this.runPhase4SimplifiedVersion();
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
		OptionsGenerator og=new OptionsGenerator(language, this.trainingCorpus);
		String optionsCat=og.generateIncOptionsTestingsPhases(language, this.bestAlgorithm, this.trainingCorpus, Optimizer.optionMenosR, this.libraryValue, Optimizer.optionGRL, Optimizer.pcrOption);
		String optionsNivreEager="finalOptionsFile.xml";
		BufferedWriter bwOptionsNivreEager;
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase3_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase3_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		System.out.println("At the end of Phase 3 MaltOptimizer achieves an increment of: + "+sDifferenceLabel+"% \ncompared with the default settings baseline.");
	
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the feature model testing phase using your training set,");
		System.out.println("it saved the results for future use in phase3_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in phase3_optFile.txt. If you want to change any of these options");
		System.out.println("you should edit phase3_optFile.txt before you start the next optimization phase.");
		System.out.println("");
		System.out.println("To proceed with Phase 4 (Library Parameters) run the following command:");
		System.out.println("java -jar MaltOptimizer.jar -p 4 -m <malt_path> -c <trainingCorpus>");
		
	}
	
	private void runPhase3AlternativeOrder5() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 3: FEATURE MODEL TUNING. ALTERNATIVE ORDER 5");

		
		System.out.println("MaltOptimizer is going to test the following:");
		System.out.println("1. Tune the window of POSTAG (n-gram) features over the stack and buffer.");
		System.out.println("2. Tune the window of (lexical) FORM features over the stack and buffer.");
		System.out.println("3. Tune dependency tree features using DEPREL and POSTAG features.");
		System.out.println("4. Add predecessor and successor features for salient tokens using POSTAG and FORM features.");
		System.out.println("5. Add CPOSTAG, FEATS,  and LEMMA features if available.");
		System.out.println("6. Add conjunctions of POSTAG and FORM features.");
		System.out.println("-----------------------------------------------------------------------------");
		
		if (bestAlgorithm.equals("nivreeager")) {
			featureModel="NivreEager.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			featureModel="NivreStandard.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			featureModel="CovingtonNonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			featureModel="CovingtonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			featureModel="StackProjective.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			featureModel="StackSwap.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			featureModel="StackSwap.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Lookahead";
			//InputLookAhead="Input";
			
		}
		
		System.out.println("5. Add CPOSTAG, FEATS,  and LEMMA features if available. ... ");
		if (!cposEqPos) {
			System.out.println("CPostag and Postag are distinct in your corpus.");
			System.out.println("Adding Cpostag Features ...");
			addNewFeaturesCpostagFeatsLemma("CPOSTAG");
		}
		
		if (!lemmaBlank) {
			System.out.println("\nBest feature model: "+featureModel);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Lemma column is used in your training set.");
			System.out.println("Adding Lemma Features ...");
			addNewFeaturesCpostagFeatsLemma("LEMMA");
		}
		if (!featsBlank) {
			System.out.println("\nBest feature model: "+featureModel);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Feats column is used in your training set.");
			System.out.println("Adding Feats Features ...");
			addSplitFeaturesFeats("FEATS");
			
		}
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("6. Adding conjunctions of POSTAG and FORM features. ... ");
		addConjunctionFeatures("POSTAG","FORM");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("4. Adding predecessor and successor features using POSTAG and FORM features ... ");
		predeccessorSuccessor("POSTAG");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
			
		System.out.println("3. Tuning dependency tree features (DEPREL) ... ");
		deprelTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("1. Tuning the window of POSTAG ... ");
		postagTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("2. Tuning the window of FORM ... ");
		formTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");

		
		
		this.runPhase4SimplifiedVersion();
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
		OptionsGenerator og=new OptionsGenerator(language, this.trainingCorpus);
		String optionsCat=og.generateIncOptionsTestingsPhases(language, this.bestAlgorithm, this.trainingCorpus, Optimizer.optionMenosR, this.libraryValue, Optimizer.optionGRL, Optimizer.pcrOption);
		String optionsNivreEager="finalOptionsFile.xml";
		BufferedWriter bwOptionsNivreEager;
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase3_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase3_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		System.out.println("At the end of Phase 3 MaltOptimizer achieves an increment of: + "+sDifferenceLabel+"% \ncompared with the default settings baseline.");
	
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the feature model testing phase using your training set,");
		System.out.println("it saved the results for future use in phase3_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in phase3_optFile.txt. If you want to change any of these options");
		System.out.println("you should edit phase3_optFile.txt before you start the next optimization phase.");
		System.out.println("");
		System.out.println("To proceed with Phase 4 (Library Parameters) run the following command:");
		System.out.println("java -jar MaltOptimizer.jar -p 4 -m <malt_path> -c <trainingCorpus>");
		
	}
	
	
	
	
	private void runPhase3AlternativeOrder6() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 3: FEATURE MODEL TUNING. ALTERNATIVE ORDER 6");

		
		System.out.println("MaltOptimizer is going to test the following:");
		System.out.println("1. Tune the window of POSTAG (n-gram) features over the stack and buffer.");
		System.out.println("2. Tune the window of (lexical) FORM features over the stack and buffer.");
		System.out.println("3. Tune dependency tree features using DEPREL and POSTAG features.");
		System.out.println("4. Add predecessor and successor features for salient tokens using POSTAG and FORM features.");
		System.out.println("5. Add CPOSTAG, FEATS,  and LEMMA features if available.");
		System.out.println("6. Add conjunctions of POSTAG and FORM features.");
		System.out.println("-----------------------------------------------------------------------------");
		
		if (bestAlgorithm.equals("nivreeager")) {
			featureModel="NivreEager.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			featureModel="NivreStandard.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			featureModel="CovingtonNonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			featureModel="CovingtonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			featureModel="StackProjective.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			featureModel="StackSwap.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			featureModel="StackSwap.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Lookahead";
			//InputLookAhead="Input";
			
		}
		
		System.out.println("2. Tuning the window of FORM ... ");
		formTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("6. Adding conjunctions of POSTAG and FORM features. ... ");
		addConjunctionFeatures("POSTAG","FORM");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		
		
		System.out.println("5. Add CPOSTAG, FEATS,  and LEMMA features if available. ... ");
		if (!cposEqPos) {
			System.out.println("CPostag and Postag are distinct in your corpus.");
			System.out.println("Adding Cpostag Features ...");
			addNewFeaturesCpostagFeatsLemma("CPOSTAG");
		}
		
		if (!lemmaBlank) {
			System.out.println("\nBest feature model: "+featureModel);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Lemma column is used in your training set.");
			System.out.println("Adding Lemma Features ...");
			addNewFeaturesCpostagFeatsLemma("LEMMA");
		}
		if (!featsBlank) {
			System.out.println("\nBest feature model: "+featureModel);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Feats column is used in your training set.");
			System.out.println("Adding Feats Features ...");
			addSplitFeaturesFeats("FEATS");
			
		}
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("4. Adding predecessor and successor features using POSTAG and FORM features ... ");
		predeccessorSuccessor("POSTAG");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("3. Tuning dependency tree features (DEPREL) ... ");
		deprelTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("1. Tuning the window of POSTAG ... ");
		postagTuning();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		this.runPhase4SimplifiedVersion();
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
		OptionsGenerator og=new OptionsGenerator(language, this.trainingCorpus);
		String optionsCat=og.generateIncOptionsTestingsPhases(language, this.bestAlgorithm, this.trainingCorpus, Optimizer.optionMenosR, this.libraryValue, Optimizer.optionGRL, Optimizer.pcrOption);
		String optionsNivreEager="finalOptionsFile.xml";
		BufferedWriter bwOptionsNivreEager;
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase3_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase3_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		System.out.println("At the end of Phase 3 MaltOptimizer achieves an increment of: + "+sDifferenceLabel+"% \ncompared with the default settings baseline.");
	
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the feature model testing phase using your training set,");
		System.out.println("it saved the results for future use in phase3_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in phase3_optFile.txt. If you want to change any of these options");
		System.out.println("you should edit phase3_optFile.txt before you start the next optimization phase.");
		System.out.println("");
		System.out.println("To proceed with Phase 4 (Library Parameters) run the following command:");
		System.out.println("java -jar MaltOptimizer.jar -p 4 -m <malt_path> -c <trainingCorpus>");
		
	}
	
	private void runPhase3BruteForce() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 3: FEATURE SELECTION\n");

		
		System.out.println("MaltOptimizer is going to perform the following feature selection experiments:");
		System.out.println("1. Tune the window of POSTAG n-grams over the parser state.");
		System.out.println("2. Tune the window of FORM features over the parser state.");
		System.out.println("3. Tune DEPREL and POSTAG features over the partially built dependency tree.");
		System.out.println("4. Add POSTAG and FORM features over the input string.");
		System.out.println("5. Add CPOSTAG, FEATS, and LEMMA features if available.");
		System.out.println("6. Add conjunctions of POSTAG and FORM features.");
		System.out.println("-----------------------------------------------------------------------------");
		
		if (bestAlgorithm.equals("nivreeager")) {
			featureModel="NivreEager.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			featureModel="NivreStandard.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			featureModel="CovingtonNonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			featureModel="CovingtonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			featureModel="StackProjective.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			featureModel="StackSwap.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			featureModel="StackSwap.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Lookahead";
			//InputLookAhead="Input";
			
		}
		
		FeatureGenerator fg=new FeatureGenerator();
		fg.emptyFeatureModel(featureModel,featureModelBruteForce);
		//featureModelBruteForce=featureModel;
		
		System.out.println("1. Tuning the window of POSTAG n-grams ... \n");
		postagTuningBruteForce();
		System.out.println("\nBest feature model Bf: "+featureModelBruteForce);
		System.out.println("-----------------------------------------------------------------------------");
		featureModel=featureModelBruteForce;
		
		System.out.println("2 (3). Tuning dependency tree features ... \n");
		deprelTuningBruteForce();
		System.out.println("\nBest feature model: "+featureModelBruteForce);
		System.out.println("-----------------------------------------------------------------------------");
		featureModel=featureModelBruteForce;
		
		System.out.println("3 (2). Tuning the window of FORM features ... \n");
		formTuningBruteForce();
		System.out.println("\nBest feature model: "+featureModelBruteForce);
		System.out.println("-----------------------------------------------------------------------------");
		featureModel=featureModelBruteForce;
		
		System.out.println("R1 (2). Reviewing some features ... \n");
		backTrackingAfter3();
		System.out.println("\nBest feature model: "+featureModelBruteForce);
		System.out.println("-----------------------------------------------------------------------------");
		featureModel=featureModelBruteForce;

		System.out.println("R1 (1). Reviewing the window of POSTAG n-grams ... \n");
		this.postagTuning();
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("R2 (2). Reviewing the window of FORM features ... \n");
		this.formTuning();
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("R3 (3). Reviewing dependency tree features ... \n");
		this.deprelTuning();
		System.out.println("-----------------------------------------------------------------------------");
		
		featureModelBruteForce=featureModel;
		
		System.out.println("4. Adding string features ... \n");
		predeccessorSuccessorBruteForce("POSTAG");
		System.out.println("\nBest feature model: "+featureModelBruteForce);
		System.out.println("-----------------------------------------------------------------------------");
		
		if (!cposEqPos || !lemmaBlank || !featsBlank) {
		System.out.println("5. Adding CPOSTAG, FEATS, and LEMMA features ... ");
		if (!cposEqPos) {
			//System.out.println("CPostag and Postag are distinct in your corpus.");
			//System.out.println("Adding CPOSTAG Features ... ");
			addNewFeaturesCpostagFeatsLemma("CPOSTAG");
		}
		
		if (!lemmaBlank) {
			//System.out.println("\nBest feature model: "+featureModel);
			//System.out.println("-----------------------------------------------------------------------------");
			//System.out.println("Lemma column is used in your training set.");
			//System.out.println("Adding LEMMA Features ... ");
			addNewFeaturesCpostagFeatsLemma("LEMMA");
		}
		if (!featsBlank) {
			//System.out.println("\nBest feature model: "+featureModel);
			//System.out.println("-----------------------------------------------------------------------------");
			//System.out.println("Feats column is used in your training set.");
			//System.out.println("Adding FEATS Features ... ");
			addSplitFeaturesFeats("FEATS");
			
		}

		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		}
		
		
		System.out.println("6. Adding conjunctions of POSTAG and FORM features... \n");
		addConjunctionFeatures("POSTAG","FORM");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has concluded feature selection and is going to tune the SVM cost parameter.\n");
		this.runPhase4SimplifiedVersion();
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
		OptionsGenerator og=new OptionsGenerator(language, this.trainingCorpus);
		String optionsCat=og.generateIncOptionsTestingsPhases(language, this.bestAlgorithm, this.trainingCorpus, Optimizer.optionMenosR, this.libraryValue, Optimizer.optionGRL, Optimizer.pcrOption);
		String optionsNivreEager="finalOptionsFile.xml";
		BufferedWriter bwOptionsNivreEager;
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase3_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase3_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		
		String s=""+this.bestResult;
		if (s.length()==4) s+="0";
		
		System.out.println("Incremental improvement over the baseline at the end of Phase 3: + "+sDifferenceLabel+"% ("+s+")");
	
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the feature model testing phase using your training set,");
		System.out.println("it saved the results for future use in phase3_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in phase3_optFile.txt.");
		System.out.println("");
		//System.out.println("To proceed with Phase 4 (Library Parameters) run the following command:");
		//System.out.println("java -jar MaltOptimizer.jar -p 4 -m <malt_path> -c <trainingCorpus>");
		
	}
	
	
	private void runPhase3RelaxedGreedy() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 3: FEATURE SELECTION DEEP GREEDY\n");

		
		System.out.println("MaltOptimizer is going to perform the following feature selection experiments:");
		System.out.println("1. Tune the window of POSTAG n-grams over the parser state.");
		System.out.println("2. Tune the window of FORM features over the parser state.");
		System.out.println("3. Tune DEPREL and POSTAG features over the partially built dependency tree.");
		System.out.println("4. Add POSTAG and FORM features over the input string.");
		System.out.println("5. Add CPOSTAG, FEATS, and LEMMA features if available.");
		System.out.println("6. Add conjunctions of POSTAG and FORM features.");
		System.out.println("-----------------------------------------------------------------------------");
		
		if (bestAlgorithm.equals("nivreeager")) {
			featureModel="NivreEager.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			featureModel="NivreStandard.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			featureModel="CovingtonNonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			featureModel="CovingtonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			featureModel="StackProjective.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			featureModel="StackSwap.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			featureModel="StackSwap.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Lookahead";
			//InputLookAhead="Input";
			
		}
		
		FeatureGenerator fg=new FeatureGenerator();
		fg.emptyFeatureModel(featureModel,featureModelBruteForce);
		
		System.out.println("1. Tuning the window of POSTAG n-grams ... \n");
		postagTuningRelaxedGreedy();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("2. Tuning the window of FORM features ... \n");
		formTuningRelaxedGreedy();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");

		
		System.out.println("3. Tuning dependency tree features ... \n");
		deprelTuningRelaxedGreedy();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		
		System.out.println("4. Adding string features ... \n");
		predeccessorSuccessorRelaxedGreedy("POSTAG");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		if (!cposEqPos || !lemmaBlank || !featsBlank) {
		System.out.println("5. Adding CPOSTAG, FEATS, and LEMMA features ... ");
		if (!cposEqPos) {
			//System.out.println("CPostag and Postag are distinct in your corpus.");
			//System.out.println("Adding CPOSTAG Features ... ");
			addNewFeaturesCpostagFeatsLemmaRelaxedGreedy("CPOSTAG");
			predeccessorSuccessorRelaxedGreedy("CPOSTAG");
		}
		
		if (!lemmaBlank) {
			//System.out.println("\nBest feature model: "+featureModel);
			//System.out.println("-----------------------------------------------------------------------------");
			//System.out.println("Lemma column is used in your training set.");
			//System.out.println("Adding LEMMA Features ... ");
			addNewFeaturesCpostagFeatsLemmaRelaxedGreedy("LEMMA");
			predeccessorSuccessorRelaxedGreedy("LEMMA");
		}
		if (!featsBlank) {
			//System.out.println("\nBest feature model: "+featureModel);
			//System.out.println("-----------------------------------------------------------------------------");
			//System.out.println("Feats column is used in your training set.");
			//System.out.println("Adding FEATS Features ... ");
			addSplitFeaturesFeatsRelaxedGreedy("FEATS");
			
		}

		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		}
		
		
		System.out.println("6. Adding conjunctions of POSTAG and FORM features... \n");
		addConjunctionFeatures("POSTAG","FORM");
		addConjunctionFeatures("POSTAG","LEMMA");
		addConjunctionFeatures("CPOSTAG","LEMMA");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has concluded feature selection and is going to tune the SVM cost parameter.\n");
		this.runPhase4SimplifiedVersion();
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
		OptionsGenerator og=new OptionsGenerator(language, this.trainingCorpus);
		String optionsCat=og.generateIncOptionsTestingsPhases(language, this.bestAlgorithm, this.trainingCorpus, Optimizer.optionMenosR, this.libraryValue, Optimizer.optionGRL, Optimizer.pcrOption);
		String optionsNivreEager="finalOptionsFile.xml";
		BufferedWriter bwOptionsNivreEager;
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase3_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase3_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		
		String s=""+this.bestResult;
		if (s.length()==4) s+="0";
		
		System.out.println("Incremental improvement over the baseline at the end of Phase 3: + "+sDifferenceLabel+"% ("+s+")");
	
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the feature model testing phase using your training set,");
		System.out.println("it saved the results for future use in phase3_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in phase3_optFile.txt.");
		System.out.println("");
		//System.out.println("To proceed with Phase 4 (Library Parameters) run the following command:");
		//System.out.println("java -jar MaltOptimizer.jar -p 4 -m <malt_path> -c <trainingCorpus>");
		
	}
	
	
	
	
	
	
	
	private void runPhase3OnlyBackward(String featureModelSoFar) {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 3: FEATURE SELECTION ONLY BACKWARD SELECTION\n");

		
		System.out.println("MaltOptimizer is going to perform the following feature selection experiments:");
		System.out.println("1. Tune the window of POSTAG n-grams over the parser state.");
		System.out.println("2. Tune the window of FORM features over the parser state.");
		System.out.println("3. Tune DEPREL and POSTAG features over the partially built dependency tree.");
		System.out.println("4. Add POSTAG and FORM features over the input string.");
		System.out.println("5. Add CPOSTAG, FEATS, and LEMMA features if available.");
		System.out.println("6. Add conjunctions of POSTAG and FORM features.");
		System.out.println("-----------------------------------------------------------------------------");
		
		if (bestAlgorithm.equals("nivreeager")) {
			featureModel="NivreEager.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			featureModel="NivreStandard.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			featureModel="CovingtonNonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			featureModel="CovingtonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			featureModel="StackProjective.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			featureModel="StackSwap.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			featureModel="StackSwap.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Lookahead";
			//InputLookAhead="Input";
			
		}
		if (!featureModelSoFar.equals("featureModel")) {
			if (!featureModelSoFar.equals("")) {
				featureModel=featureModelSoFar;
			}
		}
		
		FeatureGenerator fg=new FeatureGenerator();
		
		System.out.println("1. Tuning the window of POSTAG n-grams ... \n");
		postagTuningOnlyBackward();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("2. Tuning the window of FORM features ... \n");
		formTuningOnlyBackward();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");

		
		System.out.println("3. Tuning dependency tree features ... \n");
		deprelTuningOnlyBackward();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		/*
		System.out.println("4. Adding string features ... \n");
		predeccessorSuccessorOnlyBackward("POSTAG");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		/*if (!cposEqPos || !lemmaBlank || !featsBlank) {
		System.out.println("5. Adding CPOSTAG, FEATS, and LEMMA features ... ");
		if (!cposEqPos) {
			//System.out.println("CPostag and Postag are distinct in your corpus.");
			//System.out.println("Adding CPOSTAG Features ... ");
			addNewFeaturesCpostagFeatsLemmaOnlyBackward("CPOSTAG");
			//predeccessorSuccessorRelaxedGreedy("CPOSTAG");
		}
		
		if (!lemmaBlank) {
			//System.out.println("\nBest feature model: "+featureModel);
			//System.out.println("-----------------------------------------------------------------------------");
			//System.out.println("Lemma column is used in your training set.");
			//System.out.println("Adding LEMMA Features ... ");
			addNewFeaturesCpostagFeatsLemmaOnlyBackward("LEMMA");
			predeccessorSuccessorOnlyBackward("LEMMA");
		}
		if (!featsBlank) {
			//System.out.println("\nBest feature model: "+featureModel);
			//System.out.println("-----------------------------------------------------------------------------");
			//System.out.println("Feats column is used in your training set.");
			//System.out.println("Adding FEATS Features ... ");
			addSplitFeaturesFeatsOnlyBackward("FEATS");
			
		}

		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		}
		
		
		/*System.out.println("6. Adding conjunctions of POSTAG and FORM features... \n");
		addConjunctionFeatures("POSTAG","FORM");
		addConjunctionFeatures("POSTAG","LEMMA");
		addConjunctionFeatures("CPOSTAG","LEMMA");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");*/
		System.out.println("MaltOptimizer has concluded feature selection and is going to tune the SVM cost parameter.\n");
		this.runPhase4SimplifiedVersion();
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
		OptionsGenerator og=new OptionsGenerator(language, this.trainingCorpus);
		String optionsCat=og.generateIncOptionsTestingsPhases(language, this.bestAlgorithm, this.trainingCorpus, Optimizer.optionMenosR, this.libraryValue, Optimizer.optionGRL, Optimizer.pcrOption);
		String optionsNivreEager="finalOptionsFile.xml";
		BufferedWriter bwOptionsNivreEager;
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase3_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase3_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		
		String s=""+this.bestResult;
		if (s.length()==4) s+="0";
		
		System.out.println("Incremental improvement over the baseline at the end of Phase 3: + "+sDifferenceLabel+"% ("+s+")");
	
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the feature model testing phase using your training set,");
		System.out.println("it saved the results for future use in phase3_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in phase3_optFile.txt.");
		System.out.println("");
		//System.out.println("To proceed with Phase 4 (Library Parameters) run the following command:");
		//System.out.println("java -jar MaltOptimizer.jar -p 4 -m <malt_path> -c <trainingCorpus>");
		
	}
	
	
	
	private void runPhase3OnlyForward(String featureModelSoFar) {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 3: FEATURE SELECTION ONLY BACKWARD SELECTION\n");

		
		System.out.println("MaltOptimizer is going to perform the following feature selection experiments:");
		System.out.println("1. Tune the window of POSTAG n-grams over the parser state.");
		System.out.println("2. Tune the window of FORM features over the parser state.");
		System.out.println("3. Tune DEPREL and POSTAG features over the partially built dependency tree.");
		System.out.println("4. Add POSTAG and FORM features over the input string.");
		System.out.println("5. Add CPOSTAG, FEATS, and LEMMA features if available.");
		System.out.println("6. Add conjunctions of POSTAG and FORM features.");
		System.out.println("-----------------------------------------------------------------------------");
		
		if (bestAlgorithm.equals("nivreeager")) {
			featureModel="NivreEager.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			featureModel="NivreStandard.xml";
			InputLookAhead="Input";
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			featureModel="CovingtonNonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("covproj")) {
			featureModel="CovingtonProjective.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Right";
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			featureModel="StackProjective.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			featureModel="StackSwap.xml";
			
			InputLookAhead="Lookahead";
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			featureModel="StackSwap.xml";
			//featureModel="NivreEager.xml";
			InputLookAhead="Lookahead";
			//InputLookAhead="Input";
			
		}
		if (!featureModelSoFar.equals("featureModel")) {
			if (!featureModelSoFar.equals("")) {
				featureModel=featureModelSoFar;
			}
		}
		
		FeatureGenerator fg=new FeatureGenerator();
		
		System.out.println("1. Tuning the window of POSTAG n-grams ... \n");
		postagTuningOnlyForward();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		System.out.println("2. Tuning the window of FORM features ... \n");
		formTuningOnlyForward();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");

		
		System.out.println("3. Tuning dependency tree features ... \n");
		deprelTuningOnlyForward();
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		
		System.out.println("4. Adding string features ... \n");
		predeccessorSuccessorRelaxedGreedy("POSTAG");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		
		if (!cposEqPos || !lemmaBlank || !featsBlank) {
		System.out.println("5. Adding CPOSTAG, FEATS, and LEMMA features ... ");
		if (!cposEqPos) {
			//System.out.println("CPostag and Postag are distinct in your corpus.");
			//System.out.println("Adding CPOSTAG Features ... ");
			addNewFeaturesCpostagFeatsLemmaRelaxedGreedy("CPOSTAG");
			predeccessorSuccessorRelaxedGreedy("CPOSTAG");
		}
		
		if (!lemmaBlank) {
			//System.out.println("\nBest feature model: "+featureModel);
			//System.out.println("-----------------------------------------------------------------------------");
			//System.out.println("Lemma column is used in your training set.");
			//System.out.println("Adding LEMMA Features ... ");
			addNewFeaturesCpostagFeatsLemmaRelaxedGreedy("LEMMA");
			predeccessorSuccessorRelaxedGreedy("LEMMA");
		}
		if (!featsBlank) {
			//System.out.println("\nBest feature model: "+featureModel);
			//System.out.println("-----------------------------------------------------------------------------");
			//System.out.println("Feats column is used in your training set.");
			//System.out.println("Adding FEATS Features ... ");
			addSplitFeaturesFeatsRelaxedGreedy("FEATS");
			
		}

		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");
		}
		
		
		/*System.out.println("6. Adding conjunctions of POSTAG and FORM features... \n");
		addConjunctionFeatures("POSTAG","FORM");
		addConjunctionFeatures("POSTAG","LEMMA");
		addConjunctionFeatures("CPOSTAG","LEMMA");
		
		System.out.println("\nBest feature model: "+featureModel);
		System.out.println("-----------------------------------------------------------------------------");*/
		System.out.println("MaltOptimizer has concluded feature selection and is going to tune the SVM cost parameter.\n");
		this.runPhase4SimplifiedVersion();
		
		LibraryOptionsSetter los=LibraryOptionsSetter.getSingleton();
		//(String lang, String algorithm, String training80, String rootHandling, String libOptions, String rootLabel, String pcr) {
		OptionsGenerator og=new OptionsGenerator(language, this.trainingCorpus);
		String optionsCat=og.generateIncOptionsTestingsPhases(language, this.bestAlgorithm, this.trainingCorpus, Optimizer.optionMenosR, this.libraryValue, Optimizer.optionGRL, Optimizer.pcrOption);
		String optionsNivreEager="finalOptionsFile.xml";
		BufferedWriter bwOptionsNivreEager;
		try {
			bwOptionsNivreEager = new BufferedWriter(new FileWriter(optionsNivreEager));
			bwOptionsNivreEager.write(optionsCat);
			bwOptionsNivreEager.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase3_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase3_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		Double diff=bestResult-defaultBaseline;
		String sDifferenceLabel=""+diff;
		if (sDifferenceLabel.length()>5)
			sDifferenceLabel=sDifferenceLabel.substring(0, 5);
		
		String s=""+this.bestResult;
		if (s.length()==4) s+="0";
		
		System.out.println("Incremental improvement over the baseline at the end of Phase 3: + "+sDifferenceLabel+"% ("+s+")");
	
		
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the feature model testing phase using your training set,");
		System.out.println("it saved the results for future use in phase3_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in phase3_optFile.txt.");
		System.out.println("");
		//System.out.println("To proceed with Phase 4 (Library Parameters) run the following command:");
		//System.out.println("java -jar MaltOptimizer.jar -p 4 -m <malt_path> -c <trainingCorpus>");
		
	}
	

	private void postagTuning() {
		
		
		FeatureGenerator fg=new FeatureGenerator();
		
		/////////////////////////////////////////////////////////////////
		//STEP 1
		/////////////////////////////////////////////////////////////////
		String newFeature="backwardStackPostag.xml";
		//
		fg.removeStackWindow(featureModel,newFeature,"POSTAG");
		double result=runBestAlgorithm(newFeature);
		//System.out.println(result);
		//
		double difference=0.0;
		if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature;
			difference=result-bestResult;
			bestResult=result;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		else { //Expanding
			//
			String newFeature2Abs="forwardStackPostag";
			String anterior=featureModel;
			boolean keepGoing=true;
			for(int i=1;i<4;i++) {
				if (keepGoing) {
				String newFeature2=newFeature2Abs+i+".xml";
				fg.addStackWindow(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
				anterior=newFeature2;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature2);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature2;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
				else {
					keepGoing=false;
				}
				}
			}
		}
		
		/////////////////////////////////////////////////////////////////
		//STEP 2
		/////////////////////////////////////////////////////////////////
		
		String newFeature3="backwardInputPostag.xml";
		//
		fg.removeInputWindow(featureModel,newFeature3,"POSTAG",InputLookAhead);
		double result3=runBestAlgorithm(newFeature3);
		//System.out.println(result);
		//
		double difference3=0.0;
		if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature3;
			difference3=result3-bestResult;
			bestResult=result3;
			String sDifferenceLabel=""+difference3;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		else { //Expanding
			//
			String newFeature4Abs="forwardInputPostag";
			String anterior=featureModel;
			boolean keepGoing=true;
			for(int i=1;i<4;i++) {
				if (keepGoing){
				String newFeature4=newFeature4Abs+i+".xml";
				fg.addInputWindow(anterior,newFeature4,"POSTAG",InputLookAhead,"InputColumn");
				anterior=newFeature4;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature4);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature4;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
				else {
					keepGoing=false;
				}
				}
			}
		}
		
		String newFeature2Abs="";
		String anterior="";
		boolean keepGoing=false;
		if (bestAlgorithm.equals("stackeager") || bestAlgorithm.equals("stacklazy")) {
			//REMOVE INPUT[0] (no lookahead) (shrinking)
			//si no mejora al quitarlo add Input[1], Input[2].
			//si se mantuvo INPUT[0] add trigram Stack[0], Input[0], LookAhead[0]
			newFeature="backwardINPUT.xml";
			//
			fg.removeInputWindowSpecial(featureModel,newFeature,"POSTAG");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			boolean input0Esta=true;
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				input0Esta=false;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
			else { //Expanding
				//
				newFeature2Abs="forwardINPUT";
				anterior=featureModel;
				keepGoing=true;
				for(int i=1;i<3;i++) {
					if (keepGoing) {
					String newFeature2=newFeature2Abs+i+".xml";
					fg.addInputWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
					anterior=newFeature2;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm(newFeature2);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature2;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
						if (s.length()==4) s+="0";
						
						System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
					}
					else {
						keepGoing=false;
					}
				}
				}
			}
			if (input0Esta) {
				//add trigram stack[0], input[0], lookahead[0]
				newFeature="specialTrigramINPUT.xml";
				//
				fg.addMergeFeaturesMerge3SpecialCase(featureModel,newFeature,"POSTAG",0,"InputColumn");
				result=runBestAlgorithm(newFeature);
				//System.out.println(result);
				//
				difference=0.0;
				if (result>(this.bestResult+threshold)) { //Shrinking
					input0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				}
			}
		}
			
			if (bestAlgorithm.equals("covnonproj")) {
				
				newFeature="backwardLeftContext1.xml";
				//
				fg.removeLeftContextWindowSpecial(featureModel,newFeature,"POSTAG");
				result=runBestAlgorithm(newFeature);
				//System.out.println(result);
				boolean leftContext0Esta=true;
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					leftContext0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}
				
				else { //Expanding
					//
					newFeature2Abs="forwardRightContext";
					anterior=featureModel;
					keepGoing=true;
					for(int i=1;i<3;i++) {
						if (keepGoing) {
						String newFeature2=newFeature2Abs+i+".xml";
						fg.addLeftContextWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
						anterior=newFeature2;
						//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
						double result2=runBestAlgorithm(newFeature2);
						//System.out.println(result2);
						//System.out.println("best:"+bestResult);
						//
						double difference2=0.0;
						if (result2>(this.bestResult+threshold)) { //Shrinking
							featureModel=newFeature2;
							difference2=result2-bestResult;
							bestResult=result2;
							String sDifferenceLabel=""+difference2;
							if (sDifferenceLabel.length()>5)
								sDifferenceLabel=sDifferenceLabel.substring(0, 5);
							System.out.println("New best feature model: "+featureModel);
							String s=""+this.bestResult;
							if (s.length()==4) s+="0";
							
							System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
						
						}
						else {
							keepGoing=false;
						}
						}
					}
				}
				
				
				newFeature="backwardLeftContext2.xml";
				//
				fg.removeRightContextWindowSpecial(featureModel,newFeature,"POSTAG");
				result=runBestAlgorithm(newFeature);
				//System.out.println(result);
				boolean rightContext0Esta=true;
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					leftContext0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}
				
				else { //Expanding
					//
					newFeature2Abs="forwardRightContext";
					anterior=featureModel;
					keepGoing=true;
					for(int i=1;i<3;i++) {
						if (keepGoing) {
						String newFeature2=newFeature2Abs+i+".xml";
						fg.addRightContextWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
						anterior=newFeature2;
						//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
						double result2=runBestAlgorithm(newFeature2);
						//System.out.println(result2);
						//System.out.println("best:"+bestResult);
						//
						double difference2=0.0;
						if (result2>(this.bestResult+threshold)) { //Shrinking
							featureModel=newFeature2;
							difference2=result2-bestResult;
							bestResult=result2;
							String sDifferenceLabel=""+difference2;
							if (sDifferenceLabel.length()>5)
								sDifferenceLabel=sDifferenceLabel.substring(0, 5);
							System.out.println("New best feature model: "+featureModel);
							String s=""+this.bestResult;
							if (s.length()==4) s+="0";
							
							System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
						
						}
						else {
							keepGoing=false;
						}
						}
					}
				}
			
			
			
		}
		
	}
	
	private void postagTuning5Fold() {
		
		
		FeatureGenerator fg=new FeatureGenerator();
		
		/////////////////////////////////////////////////////////////////
		//STEP 1
		/////////////////////////////////////////////////////////////////
		String newFeature="backwardStackPostag.xml";
		//
		fg.removeStackWindow(featureModel,newFeature,"POSTAG");
		double result=runBestAlgorithm5Fold(newFeature);
		//System.out.println(result);
		//
		double difference=0.0;
		if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature;
			difference=result-bestResult;
			bestResult=result;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		else { //Expanding
			//
			String newFeature2Abs="forwardStackPostag";
			String anterior=featureModel;
			boolean keepGoing=true;
			for(int i=1;i<4;i++) {
				if (keepGoing) {
				String newFeature2=newFeature2Abs+i+".xml";
				fg.addStackWindow(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
				anterior=newFeature2;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm5Fold(newFeature2);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature2;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
				else {
					keepGoing=false;
				}
				}
			}
		}
		
		/////////////////////////////////////////////////////////////////
		//STEP 2
		/////////////////////////////////////////////////////////////////
		
		String newFeature3="backwardInputPostag.xml";
		//
		fg.removeInputWindow(featureModel,newFeature3,"POSTAG",InputLookAhead);
		double result3=runBestAlgorithm5Fold(newFeature3);
		//System.out.println(result);
		//
		double difference3=0.0;
		if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature3;
			difference3=result3-bestResult;
			bestResult=result3;
			String sDifferenceLabel=""+difference3;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		else { //Expanding
			//
			String newFeature4Abs="forwardInputPostag";
			String anterior=featureModel;
			boolean keepGoing=true;
			for(int i=1;i<4;i++) {
				if (keepGoing){
				String newFeature4=newFeature4Abs+i+".xml";
				fg.addInputWindow(anterior,newFeature4,"POSTAG",InputLookAhead,"InputColumn");
				anterior=newFeature4;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm5Fold(newFeature4);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature4;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
				else {
					keepGoing=false;
				}
				}
			}
		}
		
		String newFeature2Abs="";
		String anterior="";
		boolean keepGoing=false;
		if (bestAlgorithm.equals("stackeager") || bestAlgorithm.equals("stacklazy")) {
			//REMOVE INPUT[0] (no lookahead) (shrinking)
			//si no mejora al quitarlo add Input[1], Input[2].
			//si se mantuvo INPUT[0] add trigram Stack[0], Input[0], LookAhead[0]
			newFeature="backwardINPUT.xml";
			//
			fg.removeInputWindowSpecial(featureModel,newFeature,"POSTAG");
			result=runBestAlgorithm5Fold(newFeature);
			//System.out.println(result);
			boolean input0Esta=true;
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				input0Esta=false;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
				if (s.length()==4) s+="0";
				
				System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
			else { //Expanding
				//
				newFeature2Abs="forwardINPUT";
				anterior=featureModel;
				keepGoing=true;
				for(int i=1;i<3;i++) {
					if (keepGoing) {
					String newFeature2=newFeature2Abs+i+".xml";
					fg.addInputWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
					anterior=newFeature2;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm5Fold(newFeature2);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature2;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
						if (s.length()==4) s+="0";
						
						System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
					}
					else {
						keepGoing=false;
					}
				}
				}
			}
			if (input0Esta) {
				//add trigram stack[0], input[0], lookahead[0]
				newFeature="specialTrigramINPUT.xml";
				//
				fg.addMergeFeaturesMerge3SpecialCase(featureModel,newFeature,"POSTAG",0,"InputColumn");
				result=runBestAlgorithm5Fold(newFeature);
				//System.out.println(result);
				//
				difference=0.0;
				if (result>(this.bestResult+threshold)) { //Shrinking
					input0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				}
			}
		}
			
			if (bestAlgorithm.equals("covnonproj")) {
				
				newFeature="backwardLeftContext3.xml";
				//
				fg.removeLeftContextWindowSpecial(featureModel,newFeature,"POSTAG");
				result=runBestAlgorithm5Fold(newFeature);
				//System.out.println(result);
				boolean leftContext0Esta=true;
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					leftContext0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}
				
				else { //Expanding
					//
					newFeature2Abs="forwardRightContext";
					anterior=featureModel;
					keepGoing=true;
					for(int i=1;i<3;i++) {
						if (keepGoing) {
						String newFeature2=newFeature2Abs+i+".xml";
						fg.addLeftContextWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
						anterior=newFeature2;
						//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
						double result2=runBestAlgorithm5Fold(newFeature2);
						//System.out.println(result2);
						//System.out.println("best:"+bestResult);
						//
						double difference2=0.0;
						if (result2>(this.bestResult+threshold)) { //Shrinking
							featureModel=newFeature2;
							difference2=result2-bestResult;
							bestResult=result2;
							String sDifferenceLabel=""+difference2;
							if (sDifferenceLabel.length()>5)
								sDifferenceLabel=sDifferenceLabel.substring(0, 5);
							System.out.println("New best feature model: "+featureModel);
							String s=""+this.bestResult;
							if (s.length()==4) s+="0";
							
							System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
						
						}
						else {
							keepGoing=false;
						}
						}
					}
				}
				
				
				newFeature="backwardLeftContext4.xml";
				//
				fg.removeRightContextWindowSpecial(featureModel,newFeature,"POSTAG");
				result=runBestAlgorithm5Fold(newFeature);
				//System.out.println(result);
				boolean rightContext0Esta=true;
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					leftContext0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}
				
				else { //Expanding
					//
					newFeature2Abs="forwardRightContext";
					anterior=featureModel;
					keepGoing=true;
					for(int i=1;i<3;i++) {
						if (keepGoing) {
						String newFeature2=newFeature2Abs+i+".xml";
						fg.addRightContextWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
						anterior=newFeature2;
						//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
						double result2=runBestAlgorithm5Fold(newFeature2);
						//System.out.println(result2);
						//System.out.println("best:"+bestResult);
						//
						double difference2=0.0;
						if (result2>(this.bestResult+threshold)) { //Shrinking
							featureModel=newFeature2;
							difference2=result2-bestResult;
							bestResult=result2;
							String sDifferenceLabel=""+difference2;
							if (sDifferenceLabel.length()>5)
								sDifferenceLabel=sDifferenceLabel.substring(0, 5);
							System.out.println("New best feature model: "+featureModel);
							String s=""+this.bestResult;
							if (s.length()==4) s+="0";
							
							System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
						
						}
						else {
							keepGoing=false;
						}
						}
					}
				}
			
			
			
		}
		
	}
	
	private void postagTuningRelaxedGreedy() {
		
		
		FeatureGenerator fg=new FeatureGenerator();
		/*String newFeature2="prueba.xml";
		fg.addStackWindow(featureModel,newFeature2,"POSTAG",InputLookAhead);*/ 
		
		String oldFeatureModel=featureModel;
		String newFeature="backwardStack.xml";
		
		//
		fg.removeStackWindow(featureModel,newFeature,"POSTAG");
		double result=runBestAlgorithm(newFeature);
		//System.out.println(result);
		//
		double difference=0.0;
		if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature;
			difference=result-bestResult;
			bestResult=result;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			String newFeature2="backwardStackTwice.xml";
			fg.removeStackWindow(newFeature,newFeature2,"POSTAG");
			result=runBestAlgorithm(newFeature2);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature2;
				difference=result-bestResult;
				bestResult=result;
				sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		}
		else {
			String newFeature2="backwardStackTwice.xml";
			fg.removeStackWindow(newFeature,newFeature2,"POSTAG");
			result=runBestAlgorithm(newFeature2);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature2;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
			
			//Expanding
			//
			String newFeature2Abs="forwardStack";
			String anterior=oldFeatureModel;
			boolean keepGoing=true;
			for(int i=1;i<4;i++) {
				String newFeature2=newFeature2Abs+i+".xml";
				//System.out.println(featureModel);
				//System.out.println(newFeature2);
				fg.addStackWindow(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
				anterior=newFeature2;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature2);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { 
					featureModel=newFeature2;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}

			/*String newFeature2="prueba.xml";
			fg.addStackWindow(featureModel,newFeature2,"POSTAG",InputLookAhead);*/ 
			oldFeatureModel=featureModel;
			newFeature="backwardInput.xml";
			//
			fg.removeInputWindow(featureModel,newFeature,"POSTAG");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				String newFeature2="backwardInputTwice.xml";
				fg.removeInputWindow(newFeature,newFeature2,"POSTAG");
				result=runBestAlgorithm(newFeature2);
				//System.out.println(result);
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					featureModel=newFeature2;
					difference=result-bestResult;
					bestResult=result;
					sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}
				
			}
			else {
				String newFeature2="backwardInputTwice.xml";
				fg.removeInputWindow(newFeature,newFeature2,"POSTAG");
				result=runBestAlgorithm(newFeature2);
				//System.out.println(result);
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					featureModel=newFeature2;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}
			}
				
				//Expanding
				//
				newFeature2Abs="forwardInput";
				anterior=oldFeatureModel;
				for(int i=1;i<4;i++) {
					String newFeature2=newFeature2Abs+i+".xml";
					//System.out.println(featureModel);
					//System.out.println(newFeature2);
					fg.addInputWindow(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
					anterior=newFeature2;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm(newFeature2);

					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { 
						featureModel=newFeature2;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
					}
				}
		
		if (bestAlgorithm.equals("stackeager") || bestAlgorithm.equals("stacklazy")) {
			//REMOVE INPUT[0] (no lookahead) (shrinking)
			//si no mejora al quitarlo add Input[1], Input[2].
			//si se mantuvo INPUT[0] add trigram Stack[0], Input[0], LookAhead[0]
			oldFeatureModel=featureModel;
			newFeature="backwardINPUT.xml";
			//
			fg.removeInputWindowSpecial(featureModel,newFeature,"POSTAG");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			boolean input0Esta=true;
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				input0Esta=false;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
				//
				newFeature2Abs="forwardINPUT";
				anterior=oldFeatureModel;
				for(int i=1;i<3;i++) {
					String newFeature2=newFeature2Abs+i+".xml";
					fg.addInputWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
					anterior=newFeature2;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm(newFeature2);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature2;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
					}

				}
		
			if (input0Esta) {
				//add trigram stack[0], input[0], lookahead[0]
				newFeature="specialTrigramINPUT.xml";
				//
				fg.addMergeFeaturesMerge3SpecialCase(featureModel,newFeature,"POSTAG",0,"InputColumn");
				result=runBestAlgorithm(newFeature);
				//System.out.println(result);
				//
				difference=0.0;
				if (result>(this.bestResult+threshold)) { //Shrinking
					input0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				}
			}
		}
			
			if (bestAlgorithm.equals("covnonproj")) {
				oldFeatureModel=featureModel;
				newFeature="backwardLeftContext5.xml";
				//
				fg.removeLeftContextWindowSpecial(featureModel,newFeature,"POSTAG");
				result=runBestAlgorithm(newFeature);
				//System.out.println(result);
				boolean leftContext0Esta=true;
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					leftContext0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}

					//
					newFeature2Abs="forwardRightContext";
					anterior=oldFeatureModel;
					for(int i=1;i<3;i++) {
						String newFeature2=newFeature2Abs+i+".xml";
						fg.addLeftContextWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
						anterior=newFeature2;
						//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
						double result2=runBestAlgorithm(newFeature2);
						//System.out.println(result2);
						//System.out.println("best:"+bestResult);
						//
						double difference2=0.0;
						if (result2>(this.bestResult+threshold)) { //Shrinking
							featureModel=newFeature2;
							difference2=result2-bestResult;
							bestResult=result2;
							String sDifferenceLabel=""+difference2;
							if (sDifferenceLabel.length()>5)
								sDifferenceLabel=sDifferenceLabel.substring(0, 5);
							System.out.println("New best feature model: "+featureModel);
							String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
						
						}
					}
				
				oldFeatureModel=featureModel;
				newFeature="backwardLeftContext6.xml";
				//
				fg.removeRightContextWindowSpecial(featureModel,newFeature,"POSTAG");
				result=runBestAlgorithm(newFeature);
				//System.out.println(result);
				boolean rightContext0Esta=true;
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					leftContext0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}
					//
					newFeature2Abs="forwardRightContext";
					anterior=oldFeatureModel;
					for(int i=1;i<3;i++) {
						String newFeature2=newFeature2Abs+i+".xml";
						fg.addRightContextWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
						anterior=newFeature2;
						//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
						double result2=runBestAlgorithm(newFeature2);
						//System.out.println(result2);
						//System.out.println("best:"+bestResult);
						//
						double difference2=0.0;
						if (result2>(this.bestResult+threshold)) { //Shrinking
							featureModel=newFeature2;
							difference2=result2-bestResult;
							bestResult=result2;
							String sDifferenceLabel=""+difference2;
							if (sDifferenceLabel.length()>5)
								sDifferenceLabel=sDifferenceLabel.substring(0, 5);
							System.out.println("New best feature model: "+featureModel);
							String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
						
						}
					}
				}
			}
	
	private void postagTuningOnlyBackward() {
		
		
		FeatureGenerator fg=new FeatureGenerator();
		/*String newFeature2="prueba.xml";
		fg.addStackWindow(featureModel,newFeature2,"POSTAG",InputLookAhead);*/ 
		
		String oldFeatureModel=featureModel;
		String newFeature="backwardStack.xml";
		
		//
		fg.removeStackWindow(featureModel,newFeature,"POSTAG");
		double result=runBestAlgorithm(newFeature);
		//System.out.println(result);
		//
		double difference=0.0;
		if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature;
			difference=result-bestResult;
			bestResult=result;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			String newFeature2="backwardStackTwice.xml";
			fg.removeStackWindow(newFeature,newFeature2,"POSTAG");
			result=runBestAlgorithm(newFeature2);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature2;
				difference=result-bestResult;
				bestResult=result;
				sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		}
		else {
			String newFeature2="backwardStackTwice.xml";
			fg.removeStackWindow(newFeature,newFeature2,"POSTAG");
			result=runBestAlgorithm(newFeature2);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature2;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
			
		
			oldFeatureModel=featureModel;
			newFeature="backwardInput.xml";
			//
			fg.removeInputWindow(featureModel,newFeature,"POSTAG");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				String newFeature2="backwardInputTwice.xml";
				fg.removeInputWindow(newFeature,newFeature2,"POSTAG");
				result=runBestAlgorithm(newFeature2);
				//System.out.println(result);
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					featureModel=newFeature2;
					difference=result-bestResult;
					bestResult=result;
					sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}
				
			}
			else {
				String newFeature2="backwardInputTwice.xml";
				fg.removeInputWindow(newFeature,newFeature2,"POSTAG");
				result=runBestAlgorithm(newFeature2);
				//System.out.println(result);
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					featureModel=newFeature2;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}
			}
				
			
		
		if (bestAlgorithm.equals("stackeager") || bestAlgorithm.equals("stacklazy")) {
			//REMOVE INPUT[0] (no lookahead) (shrinking)
			//si no mejora al quitarlo add Input[1], Input[2].
			//si se mantuvo INPUT[0] add trigram Stack[0], Input[0], LookAhead[0]
			oldFeatureModel=featureModel;
			newFeature="backwardINPUT.xml";
			//
			fg.removeInputWindowSpecial(featureModel,newFeature,"POSTAG");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			boolean input0Esta=true;
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				input0Esta=false;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
			
		
			
			if (bestAlgorithm.equals("covnonproj")) {
				oldFeatureModel=featureModel;
				newFeature="backwardLeftContext7.xml";
				//
				fg.removeLeftContextWindowSpecial(featureModel,newFeature,"POSTAG");
				result=runBestAlgorithm(newFeature);
				//System.out.println(result);
				boolean leftContext0Esta=true;
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					leftContext0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}

					//
			
				
				oldFeatureModel=featureModel;
				newFeature="backwardLeftContext8.xml";
				//
				fg.removeRightContextWindowSpecial(featureModel,newFeature,"POSTAG");
				result=runBestAlgorithm(newFeature);
				//System.out.println(result);
				boolean rightContext0Esta=true;
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					leftContext0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}

			}
		}
	
	
	
private void postagTuningOnlyForward() {
		
		
		FeatureGenerator fg=new FeatureGenerator();
		/*String newFeature2="prueba.xml";
		fg.addStackWindow(featureModel,newFeature2,"POSTAG",InputLookAhead);*/ 
		
		String oldFeatureModel=featureModel;
		
			
			//Expanding
			//
			String newFeature2Abs="forwardStack";
			String anterior=oldFeatureModel;
			boolean keepGoing=true;
			for(int i=1;i<4;i++) {
				String newFeature2=newFeature2Abs+i+".xml";
				//System.out.println(featureModel);
				//System.out.println(newFeature2);
				fg.addStackWindow(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
				anterior=newFeature2;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature2);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { 
					featureModel=newFeature2;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}

		
				
				//Expanding
				//
				newFeature2Abs="forwardInput";
				anterior=oldFeatureModel;
				for(int i=1;i<4;i++) {
					String newFeature2=newFeature2Abs+i+".xml";
					//System.out.println(featureModel);
					//System.out.println(newFeature2);
					fg.addInputWindow(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
					anterior=newFeature2;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm(newFeature2);

					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { 
						featureModel=newFeature2;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
					}
				}
		
		if (bestAlgorithm.equals("stackeager") || bestAlgorithm.equals("stacklazy")) {
			//REMOVE INPUT[0] (no lookahead) (shrinking)
			//si no mejora al quitarlo add Input[1], Input[2].
			//si se mantuvo INPUT[0] add trigram Stack[0], Input[0], LookAhead[0]
			boolean input0Esta=false;
				//
				newFeature2Abs="forwardINPUT";
				anterior=oldFeatureModel;
				for(int i=1;i<3;i++) {
					String newFeature2=newFeature2Abs+i+".xml";
					fg.addInputWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
					anterior=newFeature2;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm(newFeature2);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature2;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
						if (i==1) input0Esta=true;
					
					}

				}
		
			if (input0Esta) {
				//add trigram stack[0], input[0], lookahead[0]
				String newFeature="specialTrigramINPUT.xml";
				//
				fg.addMergeFeaturesMerge3SpecialCase(featureModel,newFeature,"POSTAG",0,"InputColumn");
				double result=runBestAlgorithm(newFeature);
				//System.out.println(result);
				//
				double difference=0.0;
				if (result>(this.bestResult+threshold)) { //Shrinking
					input0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				}
			}
		}
			
			if (bestAlgorithm.equals("covnonproj")) {
				oldFeatureModel=featureModel;

					//
					newFeature2Abs="forwardRightContext";
					anterior=oldFeatureModel;
					for(int i=1;i<3;i++) {
						String newFeature2=newFeature2Abs+i+".xml";
						fg.addLeftContextWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
						anterior=newFeature2;
						//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
						double result2=runBestAlgorithm(newFeature2);
						//System.out.println(result2);
						//System.out.println("best:"+bestResult);
						//
						double difference2=0.0;
						if (result2>(this.bestResult+threshold)) { //Shrinking
							featureModel=newFeature2;
							difference2=result2-bestResult;
							bestResult=result2;
							String sDifferenceLabel=""+difference2;
							if (sDifferenceLabel.length()>5)
								sDifferenceLabel=sDifferenceLabel.substring(0, 5);
							System.out.println("New best feature model: "+featureModel);
							String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
						
						}
					}
				
				oldFeatureModel=featureModel;
				
					//
					newFeature2Abs="forwardRightContext";
					anterior=oldFeatureModel;
					for(int i=1;i<3;i++) {
						String newFeature2=newFeature2Abs+i+".xml";
						fg.addRightContextWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
						anterior=newFeature2;
						//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
						double result2=runBestAlgorithm(newFeature2);
						//System.out.println(result2);
						//System.out.println("best:"+bestResult);
						//
						double difference2=0.0;
						if (result2>(this.bestResult+threshold)) { //Shrinking
							featureModel=newFeature2;
							difference2=result2-bestResult;
							bestResult=result2;
							String sDifferenceLabel=""+difference2;
							if (sDifferenceLabel.length()>5)
								sDifferenceLabel=sDifferenceLabel.substring(0, 5);
							System.out.println("New best feature model: "+featureModel);
							String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
						
						}
					}
				}
			}
		
	
	private void postagTuningBruteForce() {
		
		
		FeatureGenerator fg=new FeatureGenerator();
		
		/*double result=runBestAlgorithm(featureModelBruteForce);
		System.out.println(result);
		System.out.println("Best: "+bestResult);
		//
		 * 
		 */
		double result=0.0;
		String antFeature=featureModelBruteForce;
		double difference=0.0;
		/*if (result>=(this.bestResultBruteForce)) { //Shrinking //NO THRESHOLD because we are removing
			this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
			difference=result-bestResultBruteForce;
			bestResultBruteForce=result;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			/*System.out.println("New best feature model: "+featureModelBruteForce);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
			
		//}
		//ADD FEATURES TO THE POOL. 
		//If we have 3...	
		//  {f1,f2,f3} => { {f1},{f2},{f3},{f1,f2},{f1,f3},{f2,f3},{f1,f2,f3} } 
		//
		ArrayList<String> pool = new ArrayList<String>();
		String structure="Stack";
		if (bestAlgorithm.contains("cov")) 
			structure="Left";
		
		String structureI="Input";
		if (bestAlgorithm.contains("cov")) 
			structureI="Right";
		if (bestAlgorithm.contains("stack")) 
			structureI="Lookahead";
		//for(int i=0;i<6;i++){
		int i=0;
			pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>");
			pool.add("\t\t<feature>InputColumn(POSTAG, "+structureI+"["+i+"])</feature>");
			if (i<5) {
				int j=i+1;
				pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+j+"])</feature>");
				pool.add("\t\t<feature>InputColumn(POSTAG, "+structureI+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+j+"])</feature>");
				if (i<4) {
					int k=j+1;
					pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+j+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+k+"])</feature>");
					pool.add("\t\t<feature>InputColumn(POSTAG, "+structureI+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+j+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+k+"])</feature>");
					if (i<3) {
						int l=k+1;
						pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+j+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+k+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+l+"])</feature>");
						pool.add("\t\t<feature>InputColumn(POSTAG, "+structureI+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+j+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+k+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+l+"])</feature>");
						if (i<2) {
							int m=l+1;
							pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+j+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+k+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+l+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+m+"])</feature>");
							pool.add("\t\t<feature>InputColumn(POSTAG, "+structureI+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+j+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+k+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+l+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+m+"])</feature>");
							if (i<1) {
								int n=m+1;
								pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+j+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+k+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+l+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+m+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+n+"])</feature>");
								pool.add("\t\t<feature>InputColumn(POSTAG, "+structureI+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+j+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+k+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+l+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+m+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"["+n+"])</feature>");
							}
						}
					}
				}
			}
		//}
	
				
		Iterator<String> it=pool.iterator();
		String newFeature="postag"+structure+structureI;
		int a=0;
		while(it.hasNext()){
			newFeature="postag"+structure+structureI+a+".xml";
			fg.addFeatureLine("bruteForce1.xml",newFeature,it.next());
			a++;
			antFeature=newFeature;
			System.out.println("Testing "+newFeature +" ...");
			result=runBestAlgorithm(newFeature);
			difference=0.0;
			System.out.println("  "+result);
			System.out.println("  Default: "+bestResult);
			if (result>=(this.bestResultBruteForce+threshold)) { 
				this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
				difference=result-bestResultBruteForce;
				bestResultBruteForce=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				/*System.out.println("New best feature model: "+featureModelBruteForce);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
				
			}
		}
		
		
		
		//ADD INPUT FEATURES
		//TEST WITH THE NEW FEATURES
		
		//ADD FEATURES TO THE POOL. 
		//If we have 3...	
		//  {f1,f2,f3} => { {f1},{f2},{f3},{f1,f2},{f1,f3},{f2,f3},{f1,f2,f3} } 
		//
		/*
		pool = new ArrayList<String>();
		structure="Input";
		if (bestAlgorithm.contains("cov")) 
			structure="Right";
		if (bestAlgorithm.contains("stack")) 
			structure="Lookahead";
		//for(int i=0;i<6;i++){
		 i=0;
			pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>");
			if (i<5) {
				int j=i+1;
				pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+j+"])</feature>");
				if (i<4) {
					int k=j+1;
					pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+j+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+k+"])</feature>");
					if (i<3) {
						int l=k+1;
						pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+j+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+k+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+l+"])</feature>");
						if (i<2) {
							int m=l+1;
							pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+j+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+k+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+l+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+m+"])</feature>");
							if (i<1) {
								int n=m+1;
								pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+j+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+k+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+l+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+m+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"["+n+"])</feature>");
							}
						}
					}
				}
			}
		//}
	
				
		it=pool.iterator();
		newFeature="postag"+structure;
		a=0;
		String currentFeature=featureModelBruteForce;
		while(it.hasNext()){
			newFeature="postag"+structure+a+".xml";
			fg.addFeatureLine(currentFeature,newFeature,it.next());
			a++;
			antFeature=newFeature;
			System.out.println("Testing "+newFeature +" ...");
			result=runBestAlgorithm(newFeature);
			difference=0.0;
			System.out.println("  "+result);
			System.out.println("  Default: "+bestResult);
			if (result>=(this.bestResultBruteForce+threshold)) { //Shrinking //NO THRESHOLD because we are removing
				this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
				difference=result-bestResultBruteForce;
				bestResultBruteForce=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				/*System.out.println("New best feature model: "+featureModelBruteForce);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
				
			/*}
		}*/
		String currentFeature=featureModelBruteForce;
		int maxS=fg.findMaxStack("POSTAG", currentFeature);
		int maxI=fg.findMaxInput("POSTAG", currentFeature, structureI);
		pool=new ArrayList<String>();
		if ((maxS>=0) && (maxI==-1)) {
			pool = new ArrayList<String>();
			structure="Input";
			if (bestAlgorithm.contains("cov")) 
				structure="Right";
			if (bestAlgorithm.contains("stack")) 
				structure="Lookahead";
			//for(int i=0;i<6;i++){
			 i=0;
			pool.add("\t\t<feature>InputColumn(POSTAG, "+structureI+"["+i+"])</feature>");
			pool.add("\t\t<feature>InputColumn(POSTAG, "+structureI+"[1])</feature>");
			pool.add("\t\t<feature>InputColumn(POSTAG, "+structureI+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structureI+"[1])</feature>");		
			}
			else {
				if ((maxS==-1) && (maxI>=0)) {
					structure="Stack";
					if (bestAlgorithm.contains("cov")) 
						structure="Left";
					i=0;
					pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>");
					pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"[1])</feature>");
					pool.add("\t\t<feature>InputColumn(POSTAG, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(POSTAG, "+structure+"[1])</feature>");	
					}
				}
			it=pool.iterator();
			newFeature="postag"+structure;
			a=0;
			
			while(it.hasNext()){
				newFeature="postagBackTracking"+a+".xml";
				fg.addFeatureLine(currentFeature,newFeature,it.next());
				a++;
				antFeature=newFeature;
				System.out.println("Testing "+newFeature +" ...");
				result=runBestAlgorithm(newFeature);
				difference=0.0;
				System.out.println("  "+result);
				System.out.println("  Default: "+bestResult);
				if (result>=(this.bestResultBruteForce+threshold)) { //Shrinking //NO THRESHOLD because we are removing
					this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
					difference=result-bestResultBruteForce;
					bestResultBruteForce=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					/*System.out.println("New best feature model: "+featureModelBruteForce);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
				}
			}
		if (bestAlgorithm.equals("stackeager") || bestAlgorithm.equals("stacklazy")) {
			
			String concat="";
			pool=new ArrayList<String>();
			pool.add("\t\t<feature>InputColumn(POSTAG, Input[0])</feature>");
			concat+="\t\t<feature>InputColumn(POSTAG, Input[0])</feature>";
			pool.add("\t\t<feature>InputColumn(POSTAG, Input[1])</feature>");
			concat+="\n\t\t<feature>InputColumn(POSTAG, Input[1])</feature>";
			pool.add(concat);
			pool.add("\t\t<feature>InputColumn(POSTAG, Input[2])</feature>");
			concat+="\n\t\t<feature>InputColumn(POSTAG, Input[2])</feature>";
			pool.add(concat);
			pool.add("\t\t<feature>InputColumn(POSTAG, Input[3])</feature>");
			concat+="\n\t\t<feature>InputColumn(POSTAG, Input[3])</feature>";
			pool.add(concat);
			
			it=pool.iterator();
			concat="";
			newFeature="postagMerge";
			a=0;
			
			boolean input0Esta=false;
			while(it.hasNext()){
				newFeature="postagLazyInput"+a+".xml";
				fg.addFeatureLine(currentFeature,newFeature,it.next());
				a++;
				antFeature=newFeature;
				System.out.println("Testing "+newFeature +" ...");
				result=runBestAlgorithm(newFeature);
				difference=0.0;
				System.out.println("  "+result);
				System.out.println("  Default: "+bestResult);
				if (result>=(this.bestResultBruteForce+threshold)) { //Shrinking //NO THRESHOLD because we are removing
					this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
					difference=result-bestResultBruteForce;
					bestResultBruteForce=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					if (a==0) {
						input0Esta=true;
					}
					/*System.out.println("New best feature model: "+featureModelBruteForce);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
					
				}
			}
			if (input0Esta) {
				//add trigram stack[0], input[0], lookahead[0]
				newFeature="specialTrigramINPUT.xml";
				//
				fg.addMergeFeaturesMerge3SpecialCase(featureModelBruteForce,newFeature,"POSTAG",0,"InputColumn");
				result=runBestAlgorithm(newFeature);
				//System.out.println(result);
				//
				difference=0.0;
				if (result>=(this.bestResultBruteForce+threshold)) { //Shrinking //NO THRESHOLD because we are removing
					this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
					difference=result-bestResultBruteForce;
					bestResultBruteForce=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					/*System.out.println("New best feature model: "+featureModelBruteForce);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
					
				}
			}
		}
		
		
		
		
		//ADD MERGE FEATURES to the POOL
		pool = new ArrayList<String>();
		maxS=fg.findMaxStack("POSTAG", featureModelBruteForce);
		maxI=fg.findMaxInput("POSTAG", featureModelBruteForce, structureI);
		//ADD MERGE FEATURES
		//TEST WITH THE NEW FEATURES
		/*String newFeature2="prueba.xml";
		fg.addStackWindow(featureModel,newFeature2,"POSTAG",InputLookAhead);*/
		//ArrayList<String> poolOfActions=new ArrayList<String>();
		
		String structureS="Stack";
		structureI=InputLookAhead;
		if (bestAlgorithm.contains("cov")){
			structureS="Left";
		}
		//<feature>Merge(InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Input[0]))</feature>
		if ((maxS>=0) && (maxI>=0)) {
			if (bestAlgorithm.contains("stack")) {
				//pool.add("\t\t<feature>Merge(InputColumn(POSTAG, "+structureS+"[1]), InputColumn(POSTAG, "+structureS+"[0]))</feature>");
				/*<feature>Merge3(InputColumn(POSTAG, Stack[2]), InputColumn(POSTAG, Stack[1]), InputColumn(POSTAG, Stack[0]))</feature>
				<feature>Merge3(InputColumn(POSTAG, Stack[1]), InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Lookahead[0]))</feature>
				<feature>Merge3(InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Lookahead[0]), InputColumn(POSTAG, Lookahead[1]))</feature>
				<feature>Merge3(InputColumn(POSTAG, Lookahead[0]), InputColumn(POSTAG, Lookahead[1]), InputColumn(POSTAG, Lookahead[2]))</feature>*/
				pool=new ArrayList<String>();
				String concat="";
				int h=-1;
				for (int j=1;j<=maxS;j++){
					if (h==-1) {
							concat+="\t\t<feature>Merge(InputColumn(POSTAG, "+structureS+"["+j+"]), InputColumn(POSTAG, "+structureS+"[0]))</feature>";
					}
					else {
						concat+="\n\t\t<feature>Merge(InputColumn(POSTAG, "+structureS+"["+j+"]), InputColumn(POSTAG, "+structureI+"["+h+"]))</feature>";
					}
					pool.add(concat);	
					h++;
				}
				it=pool.iterator();
				concat="";
				newFeature="postagMerge";
				a=0;
				currentFeature=featureModelBruteForce;
				while(it.hasNext()){
					newFeature="postagMerge"+a+".xml";
					fg.addFeatureLine(currentFeature,newFeature,it.next());
					a++;
					antFeature=newFeature;
					System.out.println("Testing "+newFeature +" ...");
					result=runBestAlgorithm(newFeature);
					difference=0.0;
					System.out.println("  "+result);
					System.out.println("  Default: "+bestResult);
					if (result>=(this.bestResultBruteForce+threshold)) { //Shrinking //NO THRESHOLD because we are removing
						this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
						difference=result-bestResultBruteForce;
						bestResultBruteForce=result;
						String sDifferenceLabel=""+difference;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						/*System.out.println("New best feature model: "+featureModelBruteForce);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
						
					}
				}
				pool=new ArrayList<String>();
				concat="";
				/*
				<feature>Merge3(InputColumn(POSTAG, Stack[2]), InputColumn(POSTAG, Stack[1]), InputColumn(POSTAG, Stack[0]))</feature>
				<feature>Merge3(InputColumn(POSTAG, Stack[1]), InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Lookahead[0]))</feature>
				<feature>Merge3(InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Lookahead[0]), InputColumn(POSTAG, Lookahead[1]))</feature>
				<feature>Merge3(InputColumn(POSTAG, Lookahead[0]), InputColumn(POSTAG, Lookahead[1]), InputColumn(POSTAG, Lookahead[2]))</feature>
				*/
				pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Stack[2]), InputColumn(POSTAG, Stack[1]), InputColumn(POSTAG, Stack[0]))</feature>");
				concat+="\t\t<feature>Merge3(InputColumn(POSTAG, Stack[2]), InputColumn(POSTAG, Stack[1]), InputColumn(POSTAG, Stack[0]))</feature>";

				pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Stack[1]), InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Lookahead[0]))</feature>");
				concat+="\n\t\t<feature>Merge3(InputColumn(POSTAG, Stack[1]), InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Lookahead[0]))</feature>";
				pool.add(concat);
				pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Lookahead[0]), InputColumn(POSTAG, Lookahead[1]))</feature>");
				concat+="\n\t\t<feature>Merge3(InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Lookahead[0]), InputColumn(POSTAG, Lookahead[1]))</feature>";
				pool.add(concat);
				
				pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Lookahead[0]), InputColumn(POSTAG, Lookahead[1]), InputColumn(POSTAG, Lookahead[2]))</feature>");
				concat+="\n\t\t<feature>Merge3(InputColumn(POSTAG, Lookahead[0]), InputColumn(POSTAG, Lookahead[1]), InputColumn(POSTAG, Lookahead[2]))</feature>";
				pool.add(concat);
				
				if (maxI>=3) {
					pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Lookahead[1]), InputColumn(POSTAG, Lookahead[2]), InputColumn(POSTAG, Lookahead[3]))</feature>");
					concat+="\n\t\t<feature>Merge3(InputColumn(POSTAG, Lookahead[1]), InputColumn(POSTAG, Lookahead[2]), InputColumn(POSTAG, Lookahead[3]))</feature>";
					pool.add(concat);
				}
				
				if (maxS>=3) {
					pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Stack[3]), InputColumn(POSTAG, Stack[2]), InputColumn(POSTAG, Stack[1]))</feature>");
					concat+="\n\t\t<feature>Merge3(InputColumn(POSTAG, Stack[3]), InputColumn(POSTAG, Stack[2]), InputColumn(POSTAG, Stack[1]))</feature>";
					pool.add(concat);
				}
				
				it=pool.iterator();
				concat="";
				newFeature="postagMerge";
				a=0;
				currentFeature=featureModelBruteForce;
				while(it.hasNext()){
					newFeature="postagMerge3"+a+".xml";
					fg.addFeatureLine(currentFeature,newFeature,it.next());
					a++;
					antFeature=newFeature;
					System.out.println("Testing "+newFeature +" ...");
					result=runBestAlgorithm(newFeature);
					difference=0.0;
					System.out.println("  "+result);
					System.out.println("  Default: "+bestResult);
					if (result>=(this.bestResultBruteForce+threshold)) { //Shrinking //NO THRESHOLD because we are removing
						this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
						difference=result-bestResultBruteForce;
						bestResultBruteForce=result;
						String sDifferenceLabel=""+difference;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						/*System.out.println("New best feature model: "+featureModelBruteForce);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
						
					}
				}
			}
			else {
				pool=new ArrayList<String>();
				String concat="";
				for (int j=0;j<=maxS;j++){
					if (j<=maxI) {
						if (j==0)
							concat+="\t\t<feature>Merge(InputColumn(POSTAG, "+structureS+"["+j+"]), InputColumn(POSTAG, "+structureI+"["+j+"]))</feature>";
						else 
							concat+="\n\t\t<feature>Merge(InputColumn(POSTAG, "+structureS+"["+j+"]), InputColumn(POSTAG, "+structureI+"["+j+"]))</feature>";
						pool.add(concat);
					}
				}
				it=pool.iterator();
				concat="";
				newFeature="postagMerge";
				a=0;
				currentFeature=featureModelBruteForce;
				while(it.hasNext()){
					newFeature="postagMerge"+a+".xml";
					fg.addFeatureLine(currentFeature,newFeature,it.next());
					a++;
					antFeature=newFeature;
					System.out.println("Testing "+newFeature +" ...");
					result=runBestAlgorithm(newFeature);
					difference=0.0;
					System.out.println("  "+result);
					System.out.println("  Default: "+bestResult);
					if (result>=(this.bestResultBruteForce+threshold)) { //Shrinking //NO THRESHOLD because we are removing
						this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
						difference=result-bestResultBruteForce;
						bestResultBruteForce=result;
						String sDifferenceLabel=""+difference;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						/*System.out.println("New best feature model: "+featureModelBruteForce);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
						
					}
				}
				
				/*<feature>Merge3(InputColumn(POSTAG, Stack[1]), InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Input[0]))</feature>
				<feature>Merge3(InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Input[0]), InputColumn(POSTAG, Input[1]))</feature>
				<feature>Merge3(InputColumn(POSTAG, Input[0]), InputColumn(POSTAG, Input[1]), InputColumn(POSTAG, Input[2]))</feature>
				<feature>Merge3(InputColumn(POSTAG, Input[1]), InputColumn(POSTAG, Input[2]), InputColumn(POSTAG, Input[3]))</feature>*/
				concat="";
				pool=new ArrayList<String>();
				pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, "+structureS+"[1]), InputColumn(POSTAG, "+structureS+"[0]), InputColumn(POSTAG, "+structureI+"[0]))</feature>");
				concat+="\t\t<feature>Merge3(InputColumn(POSTAG, "+structureS+"[1]), InputColumn(POSTAG, "+structureS+"[0]), InputColumn(POSTAG, "+structureI+"[0]))</feature>";
				pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, "+structureS+"[0]), InputColumn(POSTAG, "+structureI+"[0]), InputColumn(POSTAG, "+structureI+"[1]))</feature>");
				concat+="\n\t\t<feature>Merge3(InputColumn(POSTAG, "+structureS+"[0]), InputColumn(POSTAG, "+structureI+"[0]), InputColumn(POSTAG, "+structureI+"[1]))</feature>";
				pool.add(concat);
				pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, "+structureI+"[0]), InputColumn(POSTAG, "+structureI+"[1]), InputColumn(POSTAG, "+structureI+"[2]))</feature>");
				concat+="\n\t\t<feature>Merge3(InputColumn(POSTAG, "+structureI+"[0]), InputColumn(POSTAG, "+structureI+"[1]), InputColumn(POSTAG, "+structureI+"[2]))</feature>";
				pool.add(concat);
				
				pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, "+structureI+"[1]), InputColumn(POSTAG, "+structureI+"[2]), InputColumn(POSTAG, "+structureI+"[3]))</feature>");
				concat+="\n\t\t<feature>Merge3(InputColumn(POSTAG, "+structureI+"[1]), InputColumn(POSTAG, "+structureI+"[2]), InputColumn(POSTAG, "+structureI+"[3]))</feature>";
				
				if (maxI>3) {
					pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, "+structureI+"[2]), InputColumn(POSTAG, "+structureI+"[3]), InputColumn(POSTAG, "+structureI+"[4]))</feature>");
					concat+="\n\t\t<feature>Merge3(InputColumn(POSTAG, "+structureI+"[2]), InputColumn(POSTAG, "+structureI+"[3]), InputColumn(POSTAG, "+structureI+"[4]))</feature>";
				}
				if (maxS>1) {
					pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, "+structureS+"[2]), InputColumn(POSTAG, "+structureS+"[1]), InputColumn(POSTAG, "+structureS+"[0]))</feature>");
					concat+="\n\t\t<feature>Merge3(InputColumn(POSTAG, "+structureS+"[2]), InputColumn(POSTAG, "+structureS+"[1]), InputColumn(POSTAG, "+structureS+"[0]))</feature>";
				}
				
				it=pool.iterator();
				concat="";
				newFeature="postagMerge";
				a=0;
				currentFeature=featureModelBruteForce;
				while(it.hasNext()){
					newFeature="postagMerge3"+a+".xml";
					fg.addFeatureLine(currentFeature,newFeature,it.next());
					a++;
					antFeature=newFeature;
					System.out.println("Testing "+newFeature +" ...");
					result=runBestAlgorithm(newFeature);
					difference=0.0;
					System.out.println("  "+result);
					System.out.println("  Default: "+bestResult);
					if (result>=(this.bestResultBruteForce+threshold)) { //Shrinking //NO THRESHOLD because we are removing
						this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
						difference=result-bestResultBruteForce;
						bestResultBruteForce=result;
						String sDifferenceLabel=""+difference;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						/*System.out.println("New best feature model: "+featureModelBruteForce);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
						
					}
				}
				
				/*<feature>Merge3(InputColumn(POSTAG, Stack[1]), InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Input[0]))</feature>
				<feature>Merge3(InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Input[0]), InputColumn(POSTAG, Input[1]))</feature>
				<feature>Merge3(InputColumn(POSTAG, Input[0]), InputColumn(POSTAG, Input[1]), InputColumn(POSTAG, Input[2]))</feature>
				<feature>Merge3(InputColumn(POSTAG, Input[1]), InputColumn(POSTAG, Input[2]), InputColumn(POSTAG, Input[3]))</feature>*/
			}
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			
				//
			String concat="";
			pool=new ArrayList<String>();
			pool.add("\t\t<feature>InputColumn(POSTAG, LeftContext[0])</feature>");
			concat+="\t\t<feature>InputColumn(POSTAG, LeftContext[0])</feature>";
			pool.add("\t\t<feature>InputColumn(POSTAG, RightContext[0])</feature>");
			concat+="\n\t\t<feature>InputColumn(POSTAG, RightContext[0])</feature>";
			pool.add(concat);
			pool.add("\t\t<feature>InputColumn(POSTAG, LeftContext[1])</feature>");
			concat+="\n\t\t<feature>InputColumn(POSTAG, LeftContext[1])</feature>";
			pool.add("\t\t<feature>InputColumn(POSTAG, RightContext[1])</feature>");
			concat+="\n\t\t<feature>InputColumn(POSTAG, RightContext[1])</feature>";
			pool.add(concat);
			pool.add("\t\t<feature>InputColumn(POSTAG, LeftContext[2])</feature>");
			concat+="\n\t\t<feature>InputColumn(POSTAG, LeftContext[2])</feature>";
			pool.add("\t\t<feature>InputColumn(POSTAG, RightContext[2])</feature>");
			concat+="\n\t\t<feature>InputColumn(POSTAG, RightContext[2])</feature>";
			pool.add(concat);
			
			
			
			it=pool.iterator();
			concat="";
			newFeature="postagRightLeftContext";
			a=0;
			currentFeature=featureModelBruteForce;
			while(it.hasNext()){
				newFeature="postagRightLeftContext"+a+".xml";
				fg.addFeatureLine(currentFeature,newFeature,it.next());
				a++;
				antFeature=newFeature;
				System.out.println("Testing "+newFeature +" ...");
				result=runBestAlgorithm(newFeature);
				difference=0.0;
				System.out.println("  "+result);
				System.out.println("  Default: "+bestResult);
				if (result>=(this.bestResultBruteForce+threshold)) {
					this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
					difference=result-bestResultBruteForce;
					bestResultBruteForce=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					/*System.out.println("New best feature model: "+featureModelBruteForce);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
					
				}
			}
			}
		
		
		
		
		/*
			
			if (bestAlgorithm.equals("covnonproj")) {
				
				newFeature="backwardLeftContext.xml";
				//
				fg.removeLeftContextWindowSpecial(featureModel,newFeature,"POSTAG");
				result=runBestAlgorithm(newFeature);
				//System.out.println(result);
				boolean leftContext0Esta=true;
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					leftContext0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}
				
				else { //Expanding
					//
					String newFeature2Abs="forwardRightContext";
					String anterior=featureModel;
					boolean keepGoing=true;
					for(int i=1;i<3;i++) {
						if (keepGoing) {
						String newFeature2=newFeature2Abs+i+".xml";
						fg.addLeftContextWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
						anterior=newFeature2;
						//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
						double result2=runBestAlgorithm(newFeature2);
						//System.out.println(result2);
						//System.out.println("best:"+bestResult);
						//
						double difference2=0.0;
						if (result2>(this.bestResult+threshold)) { //Shrinking
							featureModel=newFeature2;
							difference2=result2-bestResult;
							bestResult=result2;
							String sDifferenceLabel=""+difference2;
							if (sDifferenceLabel.length()>5)
								sDifferenceLabel=sDifferenceLabel.substring(0, 5);
							System.out.println("New best feature model: "+featureModel);
							String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
						
						}
						else {
							keepGoing=false;
						}
						}
					}
				}
				
				
				newFeature="backwardLeftContext.xml";
				//
				fg.removeRightContextWindowSpecial(featureModel,newFeature,"POSTAG");
				result=runBestAlgorithm(newFeature);
				//System.out.println(result);
				boolean rightContext0Esta=true;
				//
				difference=0.0;
				if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
					leftContext0Esta=false;
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel=""+difference;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
				}
				
				else { //Expanding
					//
					String newFeature2Abs="forwardRightContext";
					String anterior=featureModel;
					boolean keepGoing=true;
					for(int i=1;i<3;i++) {
						if (keepGoing) {
						String newFeature2=newFeature2Abs+i+".xml";
						fg.addRightContextWindowSpecialCase(anterior,newFeature2,"POSTAG",InputLookAhead,"InputColumn");
						anterior=newFeature2;
						//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
						double result2=runBestAlgorithm(newFeature2);
						//System.out.println(result2);
						//System.out.println("best:"+bestResult);
						//
						double difference2=0.0;
						if (result2>(this.bestResult+threshold)) { //Shrinking
							featureModel=newFeature2;
							difference2=result2-bestResult;
							bestResult=result2;
							String sDifferenceLabel=""+difference2;
							if (sDifferenceLabel.length()>5)
								sDifferenceLabel=sDifferenceLabel.substring(0, 5);
							System.out.println("New best feature model: "+featureModel);
							String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
						
						}
						else {
							keepGoing=false;
						}
						}
					}
				}
			}*/
		
	}
	

	private void formTuning() {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		/////////////////////////////////////////////////////////////////
		//STEP 1
		/////////////////////////////////////////////////////////////////
		String newFeature="backwardStackForm.xml";
		//
		fg.removeStackWindow(featureModel,newFeature,"FORM");
		double result=runBestAlgorithm(newFeature);
		//System.out.println(result);
		//
		double difference=0.0;
		if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature;
			difference=result-bestResult;
			bestResult=result;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		else { //Expanding
			//
			String newFeature2Abs="forwardStackForm";
			String anterior=featureModel;
			boolean keepGoing=true;
			for(int i=1;i<4;i++) {
				if (keepGoing) {
				String newFeature2=newFeature2Abs+i+".xml";
				fg.addStackWindow(anterior,newFeature2,"FORM",InputLookAhead,"InputColumn");
				anterior=newFeature2;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature2);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature2;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
				else {
					keepGoing=false;
				}
				}
			}
		}
		
		/////////////////////////////////////////////////////////////////
		//STEP 2
		/////////////////////////////////////////////////////////////////
		
		String newFeature3="backwardInputForm.xml";
		//
		fg.removeInputWindow(featureModel,newFeature3,"FORM",InputLookAhead);
		double result3=runBestAlgorithm(newFeature3);
		//System.out.println(result);
		//
		double difference3=0.0;
		if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature3;
			difference3=result3-bestResult;
			bestResult=result3;
			String sDifferenceLabel=""+difference3;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		else { //Expanding
			//
			String newFeature4Abs="forwardInputForm";
			String anterior=featureModel;
			boolean keepGoing=true;
			for(int i=1;i<4;i++) {
				if (keepGoing){
				String newFeature4=newFeature4Abs+i+".xml";
				fg.addInputWindow(anterior,newFeature4,"FORM",InputLookAhead,"InputColumn");
				anterior=newFeature4;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature4);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature4;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
				else {
					keepGoing=false;
				}
				}
			}
		}
		/////////////////////////////////////////////////////////////////
		//STEP 3
		/////////////////////////////////////////////////////////////////
		if (!bestAlgorithm.equals("nivrestandard")) {
			String newFeature5="backwardHeadIterative.xml";
			//
			boolean generar=fg.removeIterativeWindow(featureModel,newFeature5,"FORM","InputLookAhead","Input");
			double result5=0.0;
			if (generar) {
				result5=runBestAlgorithm(newFeature5);
			//System.out.println(result);
			//
			}
			double difference5=0.0;
			if (result5>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature5;
				difference5=result5-bestResult;
				bestResult=result5;
				String sDifferenceLabel=""+difference5;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			else { //Expanding
				//
				String newFeature4Abs="headIterativeForm";
				String anterior=featureModel;
				boolean keepGoing=true;
				for(int i=1;i<4;i++) {
					if (keepGoing) {
					String newFeature4=newFeature4Abs+i+".xml";
					fg.addHeadIterativeWindow(anterior,newFeature4,"FORM",InputLookAhead,"InputColumn");
					anterior=newFeature4;
				
					double result2=runBestAlgorithm(newFeature4);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature4;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
					}
					else {
						keepGoing=false;
					}
					}
				}
			}
		}
		
		if (bestAlgorithm.equals("stackeager") || bestAlgorithm.equals("stacklazy")) {
			String newFeature2Abs="forwardINPUTsc";
			String anterior=featureModel;
			
				String newFeature2=newFeature2Abs+"0.xml";
				boolean generar=fg.addInputWindowSpecialCase(anterior,newFeature2,"FORM",InputLookAhead,"InputColumn");
				if (generar) {
				anterior=newFeature2;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm5Fold(newFeature2);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature2;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}
		}
	}
	
	private void formTuning5Fold() {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		/////////////////////////////////////////////////////////////////
		//STEP 1
		/////////////////////////////////////////////////////////////////
		String newFeature="backwardStackForm.xml";
		//
		fg.removeStackWindow(featureModel,newFeature,"FORM");
		double result=runBestAlgorithm5Fold(newFeature);
		//double result=0.0;
		//System.out.println(result);
		//
		double difference=0.0;
		if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature;
			difference=result-bestResult;
			bestResult=result;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		else { //Expanding
			//
			String newFeature2Abs="forwardStackForm";
			String anterior=featureModel;
			boolean keepGoing=true;
			for(int i=1;i<4;i++) {
				if (keepGoing) {
				String newFeature2=newFeature2Abs+i+".xml";
				fg.addStackWindow(anterior,newFeature2,"FORM",InputLookAhead,"InputColumn");
				anterior=newFeature2;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm5Fold(newFeature2);
				//System.out.println(result2);
				//double result2=0.0;
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature2;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
				else {
					keepGoing=false;
				}
				}
			}
		}
		
		/////////////////////////////////////////////////////////////////
		//STEP 2
		/////////////////////////////////////////////////////////////////
		
		String newFeature3="backwardInputForm.xml";
		//
		fg.removeInputWindow(featureModel,newFeature3,"FORM",InputLookAhead);
		double result3=runBestAlgorithm5Fold(newFeature3);
		//double result3=0.0;
		//System.out.println(result);
		//
		double difference3=0.0;
		if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature3;
			difference3=result3-bestResult;
			bestResult=result3;
			String sDifferenceLabel=""+difference3;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
		}
		else { //Expanding
			//
			String newFeature4Abs="forwardInputForm";
			String anterior=featureModel;
			boolean keepGoing=true;
			for(int i=1;i<4;i++) {
				if (keepGoing){
				String newFeature4=newFeature4Abs+i+".xml";
				fg.addInputWindow(anterior,newFeature4,"FORM",InputLookAhead,"InputColumn");
				anterior=newFeature4;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm5Fold(newFeature4);
				//double result2=0.0;
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature4;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
				else {
					keepGoing=false;
				}
				}
			}
		}
		/////////////////////////////////////////////////////////////////
		//STEP 3
		/////////////////////////////////////////////////////////////////
		if (!bestAlgorithm.equals("nivrestandard")) {
			String newFeature5="backwardHeadIterative.xml";
			//
			boolean generar=fg.removeIterativeWindow(featureModel,newFeature5,"FORM","InputLookAhead","Input");
			double result5=0.0;
			if (generar) {
				result5=runBestAlgorithm5Fold(newFeature5);
			//System.out.println(result);
			//
			}
			double difference5=0.0;
			if (result5>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature5;
				difference5=result5-bestResult;
				bestResult=result5;
				String sDifferenceLabel=""+difference5;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			else { //Expanding
				//
				
				String newFeature4Abs="headIterativeForm";
				String anterior=featureModel;
				boolean keepGoing=true;
				for(int i=1;i<4;i++) {
					if (keepGoing) {
					String newFeature4=newFeature4Abs+i+".xml";
					fg.addHeadIterativeWindow(anterior,newFeature4,"FORM",InputLookAhead,"InputColumn");
					anterior=newFeature4;
				
					double result2=runBestAlgorithm5Fold(newFeature4);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature4;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
					}
					else {
						keepGoing=false;
					}
					}
				}
			}
		}
		
		if (bestAlgorithm.equals("stackeager") || bestAlgorithm.equals("stacklazy")) {
			
				String newFeature2Abs="forwardINPUTsc";
				String anterior=featureModel;
				
					String newFeature2=newFeature2Abs+"0.xml";
					boolean generar=fg.addInputWindowSpecialCase(anterior,newFeature2,"FORM",InputLookAhead,"InputColumn");
					System.out.println("Generar:"+generar);
					if (generar) {
					anterior=newFeature2;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm5Fold(newFeature2);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature2;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
					}
				}
		}
	}
	
	private void formTuningRelaxedGreedy() {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		/////////////////////////////////////////////////////////////////
		//STEP 1
		/////////////////////////////////////////////////////////////////
		String oldFeatureModel=featureModel;
		String newFeature="backwardStackForm.xml";
		//
		fg.removeStackWindow(featureModel,newFeature,"FORM");
		double result=runBestAlgorithm(newFeature);
		//System.out.println(result);
		//
		double difference=0.0;
		if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature;
			difference=result-bestResult;
			bestResult=result;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			String newFeature2="backwardStackFormTwice.xml";
			//
			fg.removeStackWindow(newFeature,newFeature2,"FORM");
			result=runBestAlgorithm(newFeature2);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature2;
				difference=result-bestResult;
				bestResult=result;
				sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		}
		else { 
			String newFeature2="backwardStackFormTwice.xml";
			//
			fg.removeStackWindow(newFeature,newFeature2,"FORM");
			result=runBestAlgorithm(newFeature2);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature2;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
			
			//
			String newFeature2Abs="forwardStackForm";
			String anterior=oldFeatureModel;
			boolean keepGoing=true;
			for(int i=1;i<4;i++) {
				String newFeature2=newFeature2Abs+i+".xml";
				fg.addStackWindow(anterior,newFeature2,"FORM",InputLookAhead,"InputColumn");
				anterior=newFeature2;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature2);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature2;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}
		
		/////////////////////////////////////////////////////////////////
		//STEP 2
		/////////////////////////////////////////////////////////////////
		oldFeatureModel=featureModel;
		String newFeature3="backwardInputForm.xml";
		//
		fg.removeInputWindow(featureModel,newFeature3,"FORM",InputLookAhead);
		double result3=runBestAlgorithm(newFeature3);
		//System.out.println(result);
		//
		double difference3=0.0;
		if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature3;
			difference3=result3-bestResult;
			bestResult=result3;
			String sDifferenceLabel=""+difference3;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			String newFeature4="backwardInputFormTwice.xml";
			//
			fg.removeInputWindow(newFeature3, newFeature4, "FORM",InputLookAhead);
			result3=runBestAlgorithm(newFeature4);
			//System.out.println(result);
			//
			difference3=0.0;
			if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature4;
				difference3=result3-bestResult;
				bestResult=result3;
				sDifferenceLabel=""+difference3;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		}
		else {
			String newFeature4="backwardInputFormTwice.xml";
			//
			fg.removeInputWindow(newFeature3, newFeature4, "FORM",InputLookAhead);
			result3=runBestAlgorithm(newFeature4);
			//System.out.println(result);
			//
			difference3=0.0;
			if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature4;
				difference3=result3-bestResult;
				bestResult=result3;
				String sDifferenceLabel=""+difference3;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
			
			
			//Expanding
			//
			String newFeature4Abs="forwardInputForm";
			anterior=oldFeatureModel;
			for(int i=1;i<4;i++) {
				String newFeature4=newFeature4Abs+i+".xml";
				fg.addInputWindow(anterior,newFeature4,"FORM",InputLookAhead,"InputColumn");
				anterior=newFeature4;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature4);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature4;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}

		/////////////////////////////////////////////////////////////////
		//STEP 3
		/////////////////////////////////////////////////////////////////
		if (!bestAlgorithm.equals("nivrestandard")) {
			oldFeatureModel=featureModel;
			String newFeature5="backwardHeadIterative.xml";
			//
			fg.removeIterativeWindow(featureModel,newFeature5,"FORM","InputLookAhead","Input");
			double result5=runBestAlgorithm(newFeature5);
			//System.out.println(result);
			//
			double difference5=0.0;
			if (result5>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature5;
				difference5=result5-bestResult;
				bestResult=result5;
				String sDifferenceLabel=""+difference5;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			//else { //Expanding
				//
				newFeature4Abs="headIterativeForm";
				anterior=oldFeatureModel;
				for(int i=1;i<4;i++) {
					String newFeature4=newFeature4Abs+i+".xml";
					fg.addHeadIterativeWindow(anterior,newFeature4,"FORM",InputLookAhead,"InputColumn");
					anterior=newFeature4;
				
					double result2=runBestAlgorithm(newFeature4);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature4;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
					}
				}
			}
		
		if (bestAlgorithm.equals("stackeager") || bestAlgorithm.equals("stacklazy")) {
				newFeature2Abs="forwardINPUT";
				anterior=featureModel;
					String newFeature2=newFeature2Abs+"0.xml";
					fg.addInputWindowSpecialCase(anterior,newFeature2,"FORM",InputLookAhead,"InputColumn");
					anterior=newFeature2;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm(newFeature2);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature2;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
					}
				}
	}
	
	private void formTuningOnlyBackward() {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		/////////////////////////////////////////////////////////////////
		//STEP 1
		/////////////////////////////////////////////////////////////////
		String oldFeatureModel=featureModel;
		String newFeature="backwardStackForm.xml";
		//
		fg.removeStackWindow(featureModel,newFeature,"FORM");
		double result=runBestAlgorithm(newFeature);
		//System.out.println(result);
		//
		double difference=0.0;
		if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature;
			difference=result-bestResult;
			bestResult=result;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			String newFeature2="backwardStackFormTwice.xml";
			//
			fg.removeStackWindow(newFeature,newFeature2,"FORM");
			result=runBestAlgorithm(newFeature2);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature2;
				difference=result-bestResult;
				bestResult=result;
				sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		}
		else { 
			String newFeature2="backwardStackFormTwice.xml";
			//
			fg.removeStackWindow(newFeature,newFeature2,"FORM");
			result=runBestAlgorithm(newFeature2);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature2;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
		
		/////////////////////////////////////////////////////////////////
		//STEP 2
		/////////////////////////////////////////////////////////////////
		oldFeatureModel=featureModel;
		String newFeature3="backwardInputForm.xml";
		//
		fg.removeInputWindow(featureModel,newFeature3,"FORM",InputLookAhead);
		double result3=runBestAlgorithm(newFeature3);
		//System.out.println(result);
		//
		double difference3=0.0;
		if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature3;
			difference3=result3-bestResult;
			bestResult=result3;
			String sDifferenceLabel=""+difference3;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			String newFeature4="backwardInputFormTwice.xml";
			//
			fg.removeInputWindow(newFeature3, newFeature4, "FORM",InputLookAhead);
			result3=runBestAlgorithm(newFeature4);
			//System.out.println(result);
			//
			difference3=0.0;
			if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature4;
				difference3=result3-bestResult;
				bestResult=result3;
				sDifferenceLabel=""+difference3;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		}
		else {
			String newFeature4="backwardInputFormTwice.xml";
			//
			fg.removeInputWindow(newFeature3, newFeature4, "FORM",InputLookAhead);
			result3=runBestAlgorithm(newFeature4);
			//System.out.println(result);
			//
			difference3=0.0;
			if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature4;
				difference3=result3-bestResult;
				bestResult=result3;
				String sDifferenceLabel=""+difference3;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
			


		/////////////////////////////////////////////////////////////////
		//STEP 3
		/////////////////////////////////////////////////////////////////
		if (!bestAlgorithm.equals("nivrestandard")) {
			oldFeatureModel=featureModel;
			String newFeature5="backwardHeadIterative.xml";
			//
			fg.removeIterativeWindow(featureModel,newFeature5,"FORM","InputLookAhead","Input");
			double result5=runBestAlgorithm(newFeature5);
			//System.out.println(result);
			//
			double difference5=0.0;
			if (result5>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature5;
				difference5=result5-bestResult;
				bestResult=result5;
				String sDifferenceLabel=""+difference5;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
	
	}
	
	private void formTuningOnlyForward() {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		/////////////////////////////////////////////////////////////////
		//STEP 1
		/////////////////////////////////////////////////////////////////
		String oldFeatureModel=featureModel;
		
			
			//
			String newFeature2Abs="forwardStackForm";
			String anterior=oldFeatureModel;
			boolean keepGoing=true;
			for(int i=1;i<4;i++) {
				String newFeature2=newFeature2Abs+i+".xml";
				fg.addStackWindow(anterior,newFeature2,"FORM",InputLookAhead,"InputColumn");
				anterior=newFeature2;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature2);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature2;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}
		
		/////////////////////////////////////////////////////////////////
		//STEP 2
		/////////////////////////////////////////////////////////////////
		oldFeatureModel=featureModel;
		
			
			
			//Expanding
			//
			String newFeature4Abs="forwardInputForm";
			anterior=oldFeatureModel;
			for(int i=1;i<4;i++) {
				String newFeature4=newFeature4Abs+i+".xml";
				fg.addInputWindow(anterior,newFeature4,"FORM",InputLookAhead,"InputColumn");
				anterior=newFeature4;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature4);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature4;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}

		/////////////////////////////////////////////////////////////////
		//STEP 3
		/////////////////////////////////////////////////////////////////
		if (!bestAlgorithm.equals("nivrestandard")) {
			oldFeatureModel=featureModel;
			
			//else { //Expanding
				//
				newFeature4Abs="headIterativeForm";
				anterior=oldFeatureModel;
				for(int i=1;i<4;i++) {
					String newFeature4=newFeature4Abs+i+".xml";
					fg.addHeadIterativeWindow(anterior,newFeature4,"FORM",InputLookAhead,"InputColumn");
					anterior=newFeature4;
				
					double result2=runBestAlgorithm(newFeature4);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature4;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
					}
				}
			}
		
		if (bestAlgorithm.equals("stackeager") || bestAlgorithm.equals("stacklazy")) {
				newFeature2Abs="forwardINPUT";
				anterior=featureModel;
					String newFeature2=newFeature2Abs+"0.xml";
					fg.addInputWindowSpecialCase(anterior,newFeature2,"FORM",InputLookAhead,"InputColumn");
					anterior=newFeature2;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm(newFeature2);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature2;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
					
					}
				}
	}
	
	
	private void formTuningBruteForce() {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		ArrayList<String> pool = new ArrayList<String>();
		String structure="Stack";
		if (bestAlgorithm.contains("cov")) 
			structure="Left";
		
		String structureI="Input";
		if (bestAlgorithm.contains("cov")) 
			structureI="Right";
		if (bestAlgorithm.contains("stack")) 
			structureI="Lookahead";
		//for(int i=0;i<6;i++){
		int i=0;
		pool.add("\t\t<feature>InputColumn(FORM, "+structure+"["+i+"])</feature>");
		pool.add("\t\t<feature>InputColumn(FORM, "+structureI+"["+i+"])</feature>");
		if (i<5) {
			int j=i+1;
			pool.add("\t\t<feature>InputColumn(FORM, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+j+"])</feature>");
			pool.add("\t\t<feature>InputColumn(FORM, "+structureI+"["+i+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+j+"])</feature>");
			if (i<4) {
				int k=j+1;
				pool.add("\t\t<feature>InputColumn(FORM, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+j+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+k+"])</feature>");
				pool.add("\t\t<feature>InputColumn(FORM, "+structureI+"["+i+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+j+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+k+"])</feature>");
				if (i<3) {
					int l=k+1;
					pool.add("\t\t<feature>InputColumn(FORM, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+j+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+k+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+l+"])</feature>");
					pool.add("\t\t<feature>InputColumn(FORM, "+structureI+"["+i+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+j+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+k+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+l+"])</feature>");
					if (i<2) {
						int m=l+1;
						pool.add("\t\t<feature>InputColumn(FORM, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+j+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+k+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+l+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+m+"])</feature>");
						pool.add("\t\t<feature>InputColumn(FORM, "+structureI+"["+i+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+j+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+k+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+l+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+m+"])</feature>");
						if (i<1) {
							int n=m+1;
							pool.add("\t\t<feature>InputColumn(FORM, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+j+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+k+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+l+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+m+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"["+n+"])</feature>");
							pool.add("\t\t<feature>InputColumn(FORM, "+structureI+"["+i+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+j+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+k+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+l+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+m+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"["+n+"])</feature>");
						}
					}
				}
			}
		}
		//}
	
				
		Iterator<String> it=pool.iterator();
		String newFeature="postag"+structure+structureI;
		String currentFeature=featureModelBruteForce;
		int a=0;
		while(it.hasNext()){
			newFeature="form"+structure+structureI+a+".xml";
			fg.addFeatureLine(currentFeature,newFeature,it.next());
			a++;
			String antFeature=newFeature;
			System.out.println("Testing "+newFeature +" ...");
			double result=runBestAlgorithm(newFeature);
			System.out.println("  "+result);
			System.out.println("  Default: "+bestResult);
			if (result>=(this.bestResultBruteForce+threshold)) { 
				this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
				bestResultBruteForce=result;
				
				
			}
		}
		
		currentFeature=featureModelBruteForce;
		int maxS=fg.findMaxStack("FORM", currentFeature);
		int maxI=fg.findMaxInput("FORM", currentFeature, structureI);
		pool=new ArrayList<String>();
		if ((maxS>=0) && (maxI==-1)) {
			pool = new ArrayList<String>();
			structure="Input";
			if (bestAlgorithm.contains("cov")) 
				structure="Right";
			if (bestAlgorithm.contains("stack")) 
				structure="Lookahead";
			//for(int i=0;i<6;i++){
			 i=0;
			pool.add("\t\t<feature>InputColumn(FORM, "+structureI+"["+i+"])</feature>");
			pool.add("\t\t<feature>InputColumn(FORM, "+structureI+"[1])</feature>");
			pool.add("\t\t<feature>InputColumn(FORM, "+structureI+"["+i+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structureI+"[1])</feature>");		
			}
			else {
				if ((maxS==-1) && (maxI>=0)) {
					structure="Stack";
					if (bestAlgorithm.contains("cov")) 
						structure="Left";
					i=0;
					pool.add("\t\t<feature>InputColumn(FORM, "+structure+"["+i+"])</feature>");
					pool.add("\t\t<feature>InputColumn(FORM, "+structure+"[1])</feature>");
					pool.add("\t\t<feature>InputColumn(FORM, "+structure+"["+i+"])</feature>\n\t\t<feature>InputColumn(FORM, "+structure+"[1])</feature>");	
					}
				}
			it=pool.iterator();
			newFeature="FORM"+structure;
			a=0;
			
			while(it.hasNext()){
				newFeature="formBackTracking"+a+".xml";
				fg.addFeatureLine(currentFeature,newFeature,it.next());
				a++;
				String antFeature=newFeature;
				System.out.println("Testing "+newFeature +" ...");
				double result=runBestAlgorithm(newFeature);
				
				System.out.println("  "+result);
				System.out.println("  Default: "+bestResult);
				if (result>=(this.bestResultBruteForce+threshold)) { //Shrinking //NO THRESHOLD because we are removing
					this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
					bestResultBruteForce=result;
				}
			}
		
		/////////////////////////////////////////////////////////////////
		//STEP 3
		/////////////////////////////////////////////////////////////////
		//
			String newFeature4Abs="headIterativeForm";
			String anterior=featureModelBruteForce;
			for(int j=1;j<5;j++) {
				
				String newFeature4=newFeature4Abs+j+".xml";
				fg.addHeadIterativeWindow(anterior,newFeature4,"FORM",InputLookAhead,"InputColumn");
				anterior=newFeature4;
				System.out.println("Testing "+newFeature4 +" ...");
				double result2=runBestAlgorithm(newFeature4);
				System.out.println("  "+result2);
				System.out.println("  Default: "+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResultBruteForce+threshold)) { //Shrinking
					featureModelBruteForce=newFeature4;
					this.bestResultBruteForce=result2;
				}
				
			}
		
		if (bestAlgorithm.equals("stackeager") || bestAlgorithm.equals("stacklazy")) {
				String newFeature2Abs="forwardINPUT";
				anterior=featureModel;
					String newFeature2=newFeature2Abs+"0.xml";
					fg.addInputWindowSpecialCase(anterior,newFeature2,"FORM",InputLookAhead,"InputColumn");
					anterior=newFeature2;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					System.out.println("Testing "+newFeature2 +" ...");
					double result2=runBestAlgorithm(newFeature2);

					System.out.println("  "+result2);
					System.out.println("  Default: "+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResultBruteForce+threshold)) { //Shrinking
						featureModelBruteForce=newFeature2;
						this.bestResultBruteForce=result2;
					}
				}
		
	}
	
	
	
	private void deprelTuning() {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		/////////////////////////////////////////////////////////////////
		//STEP 1_ Subtracting single DEPREL one by one.
		/////////////////////////////////////////////////////////////////
		
		String antFeature=featureModel;
		for (int i=1;i<=4;i++){
			String newFeature="deprelSubtractingFeature";
			newFeature+=i+".xml";
			//System.out.println("hola?");
			boolean lanzar=fg.removeDeprelWindow(featureModel, newFeature, "DEPREL", "",i);
			if (lanzar) {
				antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			}
		}
		///////////////////////////////////////////
		//NIVRESTANDARD EXCEPTION
		//ADD RDEP(Stack[0]) if it works add(Postag, RDEP(Stack[0]))
		//////////////////////////////////////////
		if (bestAlgorithm.equals("nivrestandard")) {
			
			String newFeature="rdepAddFeature.xml";
			fg.addRdepWindow(featureModel, newFeature, "DEPREL");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				String newFeature2="rdepAddFeature.xml";
				fg.addRdepWindow(featureModel, newFeature, "POSTAG");
				antFeature=newFeature;
				//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
				double result2=runBestAlgorithm(newFeature);
				//System.out.println(result);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel2=""+difference;
					if (sDifferenceLabel2.length()>5)
						sDifferenceLabel2=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel2+"% ("+s+"%)");
					
				}
			}
		}
		
		/////////////////////////////////////////////////////////////////
		//STEP 2_ Subtracting merges one by one.
		/////////////////////////////////////////////////////////////////

		antFeature=featureModel;
		for (int i=1;i<=3;i++){
			
			//System.out.println("es aqui");
			
			String newFeature="deprelMergeSubtractingFeature";
			newFeature+=i+".xml";
			boolean generar=fg.removeMergeDeprelWindow(featureModel, newFeature, "DEPREL", "",i);
			if (generar) {
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}
			}
		}
		
		/////////////////////////////////////////////////////////////////
		//STEP 3_ REPLICATING DEPREL--POSTAG
		/////////////////////////////////////////////////////////////////

		antFeature=featureModel;
		for (int i=1;i<=3;i++){
			String newFeature="replicateDeprelPostagSubtractingFeature";
			newFeature+=i+".xml";
			boolean generar=fg.replicatePostagDeprel(featureModel, newFeature, "DEPREL", "",i);
			if (generar) {
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}
			}
		}
		
	}
	
	private void deprelTuning5Fold() {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		/////////////////////////////////////////////////////////////////
		//STEP 1_ Subtracting single DEPREL one by one.
		/////////////////////////////////////////////////////////////////
		
		String antFeature=featureModel;
		for (int i=1;i<=4;i++){
			String newFeature="deprelSubtractingFeature";
			newFeature+=i+".xml";
			//System.out.println("hola?");
			boolean lanzar=fg.removeDeprelWindow(featureModel, newFeature, "DEPREL", "",i);
			if (lanzar) {
				antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm5Fold(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			}
		}
		///////////////////////////////////////////
		//NIVRESTANDARD EXCEPTION
		//ADD RDEP(Stack[0]) if it works add(Postag, RDEP(Stack[0]))
		//////////////////////////////////////////
		if (bestAlgorithm.equals("nivrestandard")) {
			
			String newFeature="rdepAddFeature.xml";
			fg.addRdepWindow(featureModel, newFeature, "DEPREL");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm5Fold(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				String newFeature2="rdepAddFeature.xml";
				fg.addRdepWindow(featureModel, newFeature, "POSTAG");
				antFeature=newFeature;
				//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
				double result2=runBestAlgorithm5Fold(newFeature);
				//System.out.println(result);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel2=""+difference;
					if (sDifferenceLabel2.length()>5)
						sDifferenceLabel2=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel2+"% ("+s+"%)");
					
				}
			}
		}
		
		/////////////////////////////////////////////////////////////////
		//STEP 2_ Subtracting merges one by one.
		/////////////////////////////////////////////////////////////////

		antFeature=featureModel;
		for (int i=1;i<=3;i++){
			
			//System.out.println("es aqui");
			
			String newFeature="deprelMergeSubtractingFeature";
			newFeature+=i+".xml";
			boolean generar=fg.removeMergeDeprelWindow(featureModel, newFeature, "DEPREL", "",i);
			if (generar) {
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm5Fold(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}
			}
		}
		
		/////////////////////////////////////////////////////////////////
		//STEP 3_ REPLICATING DEPREL--POSTAG
		/////////////////////////////////////////////////////////////////

		antFeature=featureModel;
		for (int i=1;i<=3;i++){
			String newFeature="replicateDeprelPostagSubtractingFeature";
			newFeature+=i+".xml";
			boolean generar=fg.replicatePostagDeprel(featureModel, newFeature, "DEPREL", "",i);
			if (generar) {
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm5Fold(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}
			}
		}
		
	}
	
	private void deprelTuningRelaxedGreedy() {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		/////////////////////////////////////////////////////////////////
		//STEP 1_ Subtracting single DEPREL one by one.
		/////////////////////////////////////////////////////////////////
		
		String antFeature=featureModel;
		for (int i=1;i<=4;i++){
			String newFeature="deprelSubtractingFeature";
			newFeature+=i+".xml";
			fg.removeDeprelWindow(featureModel, newFeature, "DEPREL", "",i);
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
		///////////////////////////////////////////
		//NIVRESTANDARD EXCEPTION
		//ADD RDEP(Stack[0]) if it works add(Postag, RDEP(Stack[0]))
		//////////////////////////////////////////
		if (bestAlgorithm.equals("nivrestandard")) {
			
			String newFeature="rdepAddFeature.xml";
			fg.addRdepWindow(featureModel, newFeature, "DEPREL");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				String newFeature2="rdepAddFeature.xml";
				fg.addRdepWindow(featureModel, newFeature, "POSTAG");
				antFeature=newFeature;
				//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
				double result2=runBestAlgorithm(newFeature);
				//System.out.println(result);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel2=""+difference;
					if (sDifferenceLabel2.length()>5)
						sDifferenceLabel2=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					
					
					s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel2+"% ("+s+"%)");
					
				}
			}
		}
		
		/////////////////////////////////////////////////////////////////
		//STEP 2_ Subtracting merges one by one.
		/////////////////////////////////////////////////////////////////

		antFeature=featureModel;
		for (int i=1;i<=3;i++){
			String newFeature="deprelMergeSubtractingFeature";
			newFeature+=i+".xml";
			fg.removeMergeDeprelWindow(featureModel, newFeature, "DEPREL", "",i);
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}
		}
		
		/////////////////////////////////////////////////////////////////
		//STEP 3_ REPLICATING DEPREL--POSTAG
		/////////////////////////////////////////////////////////////////

		antFeature=featureModel;
		for (int i=1;i<=3;i++){
			String newFeature="replicateDeprelPostagSubtractingFeature";
			newFeature+=i+".xml";
			fg.replicatePostagDeprel(featureModel, newFeature, "DEPREL", "",i);
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}
		}
		
		/////////////////////////////////////////////////////////////////
		//ADDING SINGLE FEATURES
		/////////////////////////////////////////////////////////////////
		/*String newFeature="backwardDeprelForm.xml";
		//
		fg.removeStackWindow(featureModel,newFeature,"DEPREL");
		double result=runBestAlgorithm(newFeature);
		//System.out.println(result);
		//
		double difference=0.0;
		if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature;
			difference=result-bestResult;
			bestResult=result;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			String newFeature2="backwardStackFormTwice.xml";
			//
			fg.removeStackWindow(newFeature,newFeature2,"DEPREL");
			result=runBestAlgorithm(newFeature2);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature2;
				difference=result-bestResult;
				bestResult=result;
				sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}

		}
		else { 
			String newFeature2="backwardDeprelFormTwice.xml";
			//
			fg.removeStackWindow(newFeature,newFeature2,"DEPREL");
			result=runBestAlgorithm(newFeature2);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature2;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}

			else {//Expanding
				//
				String newFeature2Abs="forwardDeprelStack";
				String anterior=featureModel;
				boolean keepGoing=true;
				for(int i=1;i<4;i++) {
					newFeature2=newFeature2Abs+i+".xml";
					fg.addStackWindow(anterior,newFeature2,"DEPREL",InputLookAhead,"OutputColumn");
					anterior=newFeature2;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm(newFeature2);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature2;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

					}
				}
			}
		}

		/////////////////////////////////////////////////////////////////
		//STEP 2
		/////////////////////////////////////////////////////////////////

		String newFeature3="backwardInputDeprel.xml";
		//
		fg.removeInputWindow(featureModel,newFeature3,"FORM",InputLookAhead);
		double result3=runBestAlgorithm(newFeature3);
		//System.out.println(result);
		//
		double difference3=0.0;
		if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature3;
			difference3=result3-bestResult;
			bestResult=result3;
			String sDifferenceLabel=""+difference3;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			String newFeature4="backwardInputDeprelTwice.xml";
			//
			fg.removeInputWindow(newFeature3, newFeature4, "DEPREL",InputLookAhead);
			result3=runBestAlgorithm(newFeature4);
			//System.out.println(result);
			//
			difference3=0.0;
			if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature4;
				difference3=result3-bestResult;
				bestResult=result3;
				sDifferenceLabel=""+difference3;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}

		}
		else {
			String newFeature4="backwardInputDeprelTwice.xml";
			//
			fg.removeInputWindow(newFeature3, newFeature4, "DEPREL",InputLookAhead);
			result3=runBestAlgorithm(newFeature4);
			//System.out.println(result);
			//
			difference3=0.0;
			if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature4;
				difference3=result3-bestResult;
				bestResult=result3;
				String sDifferenceLabel=""+difference3;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}
			else {


				//Expanding
				//
				String newFeature4Abs="forwardInputDeprel";
				String anterior=featureModel;
				boolean keepGoing=true;
				for(int i=1;i<4;i++) {
					newFeature4=newFeature4Abs+i+".xml";
					fg.addInputWindow(anterior,newFeature4,"DEPREL",InputLookAhead,"OutputColumn");
					anterior=newFeature4;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm(newFeature4);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature4;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

					}
				}
			}
		}*/
		
	}
	
	
	private void deprelTuningOnlyBackward() {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		/////////////////////////////////////////////////////////////////
		//STEP 1_ Subtracting single DEPREL one by one.
		/////////////////////////////////////////////////////////////////
		
		String antFeature=featureModel;
		for (int i=1;i<=4;i++){
			String newFeature="deprelSubtractingFeature";
			newFeature+=i+".xml";
			fg.removeDeprelWindow(featureModel, newFeature, "DEPREL", "",i);
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}
		
		
		/////////////////////////////////////////////////////////////////
		//STEP 2_ Subtracting merges one by one.
		/////////////////////////////////////////////////////////////////

		antFeature=featureModel;
		for (int i=1;i<=3;i++){
			String newFeature="deprelMergeSubtractingFeature";
			newFeature+=i+".xml";
			fg.removeMergeDeprelWindow(featureModel, newFeature, "DEPREL", "",i);
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}
		}
	}
	
	
	private void deprelTuningOnlyForward() {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		String antFeature=featureModel;
		///////////////////////////////////////////
		//NIVRESTANDARD EXCEPTION
		//ADD RDEP(Stack[0]) if it works add(Postag, RDEP(Stack[0]))
		//////////////////////////////////////////
		if (bestAlgorithm.equals("nivrestandard")) {
			
			String newFeature="rdepAddFeature.xml";
			fg.addRdepWindow(featureModel, newFeature, "DEPREL");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				String newFeature2="rdepAddFeature.xml";
				fg.addRdepWindow(featureModel, newFeature, "POSTAG");
				antFeature=newFeature;
				//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
				double result2=runBestAlgorithm(newFeature);
				//System.out.println(result);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature;
					difference=result-bestResult;
					bestResult=result;
					String sDifferenceLabel2=""+difference;
					if (sDifferenceLabel2.length()>5)
						sDifferenceLabel2=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					s=""+this.bestResult;
					if (s.length()==4) s+="0";
					
					System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel2+"% ("+s+"%)");
					
				}
			}
		}
		
		/////////////////////////////////////////////////////////////////
		//STEP 3_ REPLICATING DEPREL--POSTAG
		/////////////////////////////////////////////////////////////////

		antFeature=featureModel;
		for (int i=1;i<=3;i++){
			String newFeature="replicateDeprelPostagSubtractingFeature";
			newFeature+=i+".xml";
			fg.replicatePostagDeprel(featureModel, newFeature, "DEPREL", "",i);
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}
		}
		
		/////////////////////////////////////////////////////////////////
		//ADDING SINGLE FEATURES
		/////////////////////////////////////////////////////////////////
		/*String newFeature="backwardDeprelForm.xml";
		//
		fg.removeStackWindow(featureModel,newFeature,"DEPREL");
		double result=runBestAlgorithm(newFeature);
		//System.out.println(result);
		//
		double difference=0.0;
		if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature;
			difference=result-bestResult;
			bestResult=result;
			String sDifferenceLabel=""+difference;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			String newFeature2="backwardStackFormTwice.xml";
			//
			fg.removeStackWindow(newFeature,newFeature2,"DEPREL");
			result=runBestAlgorithm(newFeature2);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature2;
				difference=result-bestResult;
				bestResult=result;
				sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}

		}
		else { 
			String newFeature2="backwardDeprelFormTwice.xml";
			//
			fg.removeStackWindow(newFeature,newFeature2,"DEPREL");
			result=runBestAlgorithm(newFeature2);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature2;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}

			else {//Expanding
				//
				String newFeature2Abs="forwardDeprelStack";
				String anterior=featureModel;
				boolean keepGoing=true;
				for(int i=1;i<4;i++) {
					newFeature2=newFeature2Abs+i+".xml";
					fg.addStackWindow(anterior,newFeature2,"DEPREL",InputLookAhead,"OutputColumn");
					anterior=newFeature2;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm(newFeature2);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature2;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

					}
				}
			}
		}

		/////////////////////////////////////////////////////////////////
		//STEP 2
		/////////////////////////////////////////////////////////////////

		String newFeature3="backwardInputDeprel.xml";
		//
		fg.removeInputWindow(featureModel,newFeature3,"FORM",InputLookAhead);
		double result3=runBestAlgorithm(newFeature3);
		//System.out.println(result);
		//
		double difference3=0.0;
		if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
			featureModel=newFeature3;
			difference3=result3-bestResult;
			bestResult=result3;
			String sDifferenceLabel=""+difference3;
			if (sDifferenceLabel.length()>5)
				sDifferenceLabel=sDifferenceLabel.substring(0, 5);
			System.out.println("New best feature model: "+featureModel);
			String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			String newFeature4="backwardInputDeprelTwice.xml";
			//
			fg.removeInputWindow(newFeature3, newFeature4, "DEPREL",InputLookAhead);
			result3=runBestAlgorithm(newFeature4);
			//System.out.println(result);
			//
			difference3=0.0;
			if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature4;
				difference3=result3-bestResult;
				bestResult=result3;
				sDifferenceLabel=""+difference3;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}

		}
		else {
			String newFeature4="backwardInputDeprelTwice.xml";
			//
			fg.removeInputWindow(newFeature3, newFeature4, "DEPREL",InputLookAhead);
			result3=runBestAlgorithm(newFeature4);
			//System.out.println(result);
			//
			difference3=0.0;
			if (result3>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature4;
				difference3=result3-bestResult;
				bestResult=result3;
				String sDifferenceLabel=""+difference3;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

			}
			else {


				//Expanding
				//
				String newFeature4Abs="forwardInputDeprel";
				String anterior=featureModel;
				boolean keepGoing=true;
				for(int i=1;i<4;i++) {
					newFeature4=newFeature4Abs+i+".xml";
					fg.addInputWindow(anterior,newFeature4,"DEPREL",InputLookAhead,"OutputColumn");
					anterior=newFeature4;
					//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
					double result2=runBestAlgorithm(newFeature4);
					//System.out.println(result2);
					//System.out.println("best:"+bestResult);
					//
					double difference2=0.0;
					if (result2>(this.bestResult+threshold)) { //Shrinking
						featureModel=newFeature4;
						difference2=result2-bestResult;
						bestResult=result2;
						String sDifferenceLabel=""+difference2;
						if (sDifferenceLabel.length()>5)
							sDifferenceLabel=sDifferenceLabel.substring(0, 5);
						System.out.println("New best feature model: "+featureModel);
						String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");

					}
				}
			}
		}*/
		
	}
	
	private void deprelTuningBruteForce() {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		double result=0.0;
		String antFeature=featureModelBruteForce;
		double difference=0.0;
		//ADD FEATURES TO THE POOL. 
		//If we have 3...	
		//  {f1,f2,f3} => { {f1},{f2},{f3},{f1,f2},{f1,f3},{f2,f3},{f1,f2,f3} } 
		//
		ArrayList<String> pool = new ArrayList<String>();
		String structure="Stack";
		if (bestAlgorithm.contains("cov")) 
			structure="Left";
		
		String structureI="Input";
		if (bestAlgorithm.contains("cov")) 
			structureI="Right";
		if (bestAlgorithm.contains("stack")) 
			structureI="Lookahead";
		//for(int i=0;i<6;i++){
		int i=0;
		pool.add("\t\t<feature>OutputColumn(DEPREL, "+structure+"["+i+"])</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, "+structureI+"["+i+"])</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, "+structure+"["+i+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structureI+"["+i+"])</feature>");
		if (i<5) {
			int j=i+1;
			pool.add("\t\t<feature>OutputColumn(DEPREL, "+structure+"["+i+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structure+"["+j+"])</feature>");
			pool.add("\t\t<feature>OutputColumn(DEPREL, "+structureI+"["+i+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structureI+"["+j+"])</feature>");
			pool.add("\t\t<feature>OutputColumn(DEPREL, "+structure+"["+i+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structure+"["+j+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structureI+"["+i+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structureI+"["+j+"])</feature>");
			if (i<4) {
				int k=j+1;
				pool.add("\t\t<feature>OutputColumn(DEPREL, "+structure+"["+i+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structure+"["+j+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structure+"["+k+"])</feature>");
				pool.add("\t\t<feature>OutputColumn(DEPREL, "+structureI+"["+i+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structureI+"["+j+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structureI+"["+k+"])</feature>");
				pool.add("\t\t<feature>OutputColumn(DEPREL, "+structure+"["+i+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structure+"["+j+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structure+"["+k+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structureI+"["+i+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structureI+"["+j+"])</feature>\n\t\t<feature>OutputColumn(DEPREL, "+structureI+"["+k+"])</feature>");
			}
		}
		//}
	
				
		Iterator<String> it=pool.iterator();
		String newFeature="deprel"+structure+structureI;
		int a=0;
		String currentFeature=featureModelBruteForce;
		while(it.hasNext()){
			newFeature="deprel"+structure+structureI+a+".xml";
			fg.addFeatureLine(currentFeature,newFeature,it.next());
			a++;
			antFeature=newFeature;
			System.out.println("Testing "+newFeature +" ...");
			result=runBestAlgorithm(newFeature);
			difference=0.0;
			System.out.println("  "+result);
			System.out.println("  Default: "+bestResult);
			if (result>=(this.bestResultBruteForce+threshold)) { 
				this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
				difference=result-bestResultBruteForce;
				bestResultBruteForce=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				/*System.out.println("New best feature model: "+featureModelBruteForce);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
				
			}
		}
		
		pool=new ArrayList<String>();
		pool.add("\t\t<feature>OutputColumn(DEPREL, ldep("+structure+"[0]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, rdep("+structure+"[0]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, rdep("+structureI+"[0]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, ldep("+structureI+"[0]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, ldep("+structure+"[1]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, rdep("+structure+"[1]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, rdep("+structureI+"[1]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, ldep("+structureI+"[1]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, ldep("+structure+"[2]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, rdep("+structure+"[2]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, rdep("+structureI+"[2]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, ldep("+structureI+"[2]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, ldep("+structure+"[3]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, rdep("+structure+"[3]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, rdep("+structureI+"[3]))</feature>");
		pool.add("\t\t<feature>OutputColumn(DEPREL, ldep("+structureI+"[3]))</feature>");
				
		it=pool.iterator();
		newFeature="deprel"+structure+structureI;
		a=0;
		while(it.hasNext()){
			newFeature="deprelLdepRdep"+structure+structureI+a+".xml";
			fg.addFeatureLine(featureModelBruteForce,newFeature,it.next());
			a++;
			antFeature=newFeature;
			System.out.println("Testing "+newFeature +" ...");
			result=runBestAlgorithm(newFeature);
			difference=0.0;
			System.out.println("  "+result);
			System.out.println("  Default: "+bestResult);
			if (result>=(this.bestResultBruteForce+threshold)) { 
				this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
				difference=result-bestResultBruteForce;
				bestResultBruteForce=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				/*System.out.println("New best feature model: "+featureModelBruteForce);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
				
			}
		}
		
		
		
		/*
		 * <feature>Merge3(InputColumn(POSTAG, Stack[0]), OutputColumn(DEPREL, ldep(Stack[0])), OutputColumn(DEPREL, rdep(Stack[0])))</feature>
		<feature>Merge3(InputColumn(POSTAG, Stack[1]), OutputColumn(DEPREL, ldep(Stack[1])), OutputColumn(DEPREL, rdep(Stack[1])))</feature>
		 */
		/*<feature>Merge3(InputColumn(POSTAG, Stack[0]), OutputColumn(DEPREL, ldep(Stack[0])), OutputColumn(DEPREL, rdep(Stack[0])))</feature>
		<feature>Merge(InputColumn(POSTAG, Stack[0]), OutputColumn(DEPREL, Stack[0]))</feature>
		<feature>Merge(InputColumn(POSTAG, Input[0]), OutputColumn(DEPREL, ldep(Input[0])))</feature>
		*/
		/*<feature>Merge3(InputColumn(POSTAG, Left[0]), OutputColumn(DEPREL, ldep(Left[0])), OutputColumn(DEPREL, rdep(Left[0])))</feature>
		<feature>Merge(InputColumn(POSTAG, Left[0]), OutputColumn(DEPREL, Left[0]))</feature>
		<feature>Merge(InputColumn(POSTAG, Right[0]), OutputColumn(DEPREL, ldep(Right[0])))</feature>
		<feature>Merge(InputColumn(POSTAG, Right[0]), OutputColumn(DEPREL, Right[0]))</feature>*/
		
		pool=new ArrayList<String>();
		if (bestAlgorithm.contains("stack")) {
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, "+structure+"[0]), OutputColumn(DEPREL, ldep("+structure+"[0])), OutputColumn(DEPREL, rdep("+structure+"[0])))</feature>");
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, "+structure+"[1]), OutputColumn(DEPREL, ldep("+structure+"[1])), OutputColumn(DEPREL, rdep("+structure+"[1])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Stack[1]), OutputColumn(DEPREL, ldep(Stack[1])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Stack[1]), OutputColumn(DEPREL, ldep(Stack[0])))</feature>");
		}
		if (bestAlgorithm.contains("nivre")) {
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Stack[0]), OutputColumn(DEPREL, ldep(Stack[0])), OutputColumn(DEPREL, rdep(Stack[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Stack[0]), OutputColumn(DEPREL, Stack[0]))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Input[0]), OutputColumn(DEPREL, ldep(Input[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Input[0]), OutputColumn(DEPREL, rdep(Input[0])))</feature>");
		}
		if (bestAlgorithm.contains("cov")) {
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Left[0]), OutputColumn(DEPREL, ldep(Left[0])), OutputColumn(DEPREL, rdep(Left[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Left[0]), OutputColumn(DEPREL, Left[0]))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Right[0]), OutputColumn(DEPREL, ldep(Right[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Right[0]), OutputColumn(DEPREL, Right[0]))</feature>");
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Left[1]), InputColumn(POSTAG, Left[0]), InputColumn(POSTAG, Right[0]))</feature>");
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Left[0]), InputColumn(POSTAG, Right[0]), InputColumn(POSTAG, Right[1]))</feature>");
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Right[0]), InputColumn(POSTAG, Right[1]), InputColumn(POSTAG, Right[2]))</feature>");
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Right[1]), InputColumn(POSTAG, Right[2]), InputColumn(POSTAG, Right[3]))</feature>");
		}
				
		it=pool.iterator();
		newFeature="deprel"+structure+structureI;
		a=0;
		while(it.hasNext()){
			newFeature="deprelMerge"+structure+structureI+a+".xml";
			fg.addFeatureLine(featureModelBruteForce,newFeature,it.next());
			a++;
			antFeature=newFeature;
			System.out.println("Testing "+newFeature +" ...");
			result=runBestAlgorithm(newFeature);
			difference=0.0;
			System.out.println("  "+result);
			System.out.println("  Default: "+bestResult);
			if (result>=(this.bestResultBruteForce+threshold)) { 
				this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
				difference=result-bestResultBruteForce;
				bestResultBruteForce=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				/*System.out.println("New best feature model: "+featureModelBruteForce);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
				
			}
		}
		/*
		/////////////////////////////////////////////////////////////////
		//STEP 1_ Subtracting single DEPREL one by one.
		/////////////////////////////////////////////////////////////////
		
		String antFeature=featureModel;
		for (int i=1;i<=4;i++){
			String newFeature="deprelSubtractingFeature";
			newFeature+=i+".xml";
			fg.removeDeprelWindow(featureModel, newFeature, "DEPREL", "",i);
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>=(this.bestResult)) { //Shrinking //NO THRESHOLD because we are removing
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
		}*/
		///////////////////////////////////////////
		//NIVRESTANDARD EXCEPTION
		//ADD RDEP(Stack[0]) if it works add(Postag, RDEP(Stack[0]))
		//////////////////////////////////////////
		if (bestAlgorithm.equals("nivrestandard")) {
			
			newFeature="rdepAddFeature.xml";
			fg.addRdepWindow(featureModelBruteForce, newFeature, "DEPREL");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			System.out.println("Testing "+newFeature +" ...");
			result=runBestAlgorithm(newFeature);
			System.out.println("  "+result);
			System.out.println("  Default: "+bestResult);
			
			//
			difference=0.0;
			if (result>=(this.bestResultBruteForce+threshold)) { 
				this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
				difference=result-bestResultBruteForce;
				bestResultBruteForce=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				/*System.out.println("New best feature model: "+featureModelBruteForce);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
				String newFeature2="rdepAddFeature2.xml";
				fg.addRdepWindow(featureModel, newFeature, "POSTAG");
				antFeature=newFeature;
				//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
				System.out.println("Testing "+newFeature2 +" ...");
				double result2=runBestAlgorithm(newFeature);
				System.out.println("  "+result2);
				System.out.println("  Default: "+bestResult);
				//System.out.println(result);
				double difference2=0.0;
				if (result2>(this.bestResultBruteForce+threshold)) { //Shrinking
					featureModelBruteForce=newFeature2;
					bestResultBruteForce=result2;
					
				}
				
			}

		}
		
		
		/////////////////////////////////////////////////////////////////
		//STEP 3_ REPLICATING DEPREL--POSTAG
		/////////////////////////////////////////////////////////////////

		antFeature=featureModel;
		for (int j=1;j<=3;j++){
			newFeature="replicateDeprelPostagSubtractingFeature";
			newFeature+=j+".xml";
			fg.replicatePostagDeprel(featureModelBruteForce, newFeature, "DEPREL", "",i);
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			System.out.println("Testing "+newFeature +" ...");
			result=runBestAlgorithm(newFeature);
			System.out.println("  "+result);
			System.out.println("  Default: "+bestResult);
			
			difference=0.0;
			if (result>(this.bestResultBruteForce+threshold)) { //Shrinking
				featureModelBruteForce=newFeature;
				bestResultBruteForce=result;
			}
		}
		
	}
	
	
	private void backTrackingAfter3() {
		FeatureGenerator fg=new FeatureGenerator();
		
		double result=0.0;
		String antFeature=featureModelBruteForce;
		double difference=0.0;
		//ADD FEATURES TO THE POOL. 
		//If we have 3...	
		//  {f1,f2,f3} => { {f1},{f2},{f3},{f1,f2},{f1,f3},{f2,f3},{f1,f2,f3} } 
		//
		ArrayList<String> pool = new ArrayList<String>();
		String structure="Stack";
		if (bestAlgorithm.contains("cov")) 
			structure="Left";
		
		String structureI="Input";
		if (bestAlgorithm.contains("cov")) 
			structureI="Right";
		if (bestAlgorithm.contains("stack")) 
			structureI="Lookahead";
		pool=new ArrayList<String>();
		if (bestAlgorithm.contains("stack")) {
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, "+structure+"[0]), OutputColumn(DEPREL, ldep("+structure+"[0])), OutputColumn(DEPREL, rdep("+structure+"[0])))</feature>");
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, "+structure+"[1]), OutputColumn(DEPREL, ldep("+structure+"[1])), OutputColumn(DEPREL, rdep("+structure+"[1])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Stack[1]), OutputColumn(DEPREL, ldep(Stack[1])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Stack[1]), OutputColumn(DEPREL, ldep(Stack[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Stack[2]), OutputColumn(DEPREL, ldep(Stack[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Lookahead[0]), OutputColumn(DEPREL, ldep(Stack[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Lookahead[0]), OutputColumn(DEPREL, rdep(Stack[0])))</feature>");
		}
		if (bestAlgorithm.contains("nivre")) {
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Stack[0]), OutputColumn(DEPREL, ldep(Stack[0])), OutputColumn(DEPREL, rdep(Stack[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Stack[0]), OutputColumn(DEPREL, Stack[0]))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Input[0]), OutputColumn(DEPREL, ldep(Input[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Input[0]), OutputColumn(DEPREL, rdep(Input[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Input[1]), OutputColumn(DEPREL, rdep(Input[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Input[2]), OutputColumn(DEPREL, rdep(Input[0])))</feature>");
		}
		if (bestAlgorithm.contains("cov")) {
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Left[0]), OutputColumn(DEPREL, ldep(Left[0])), OutputColumn(DEPREL, rdep(Left[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Left[0]), OutputColumn(DEPREL, Left[0]))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Right[0]), OutputColumn(DEPREL, ldep(Right[0])))</feature>");
			pool.add("\t\t<feature>Merge(InputColumn(POSTAG, Right[0]), OutputColumn(DEPREL, Right[0]))</feature>");
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Left[1]), InputColumn(POSTAG, Left[0]), InputColumn(POSTAG, Right[0]))</feature>");
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Left[0]), InputColumn(POSTAG, Right[0]), InputColumn(POSTAG, Right[1]))</feature>");
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Right[0]), InputColumn(POSTAG, Right[1]), InputColumn(POSTAG, Right[2]))</feature>");
			pool.add("\t\t<feature>Merge3(InputColumn(POSTAG, Right[1]), InputColumn(POSTAG, Right[2]), InputColumn(POSTAG, Right[3]))</feature>");
			
		}
				
		Iterator<String> it=pool.iterator();
		String newFeature="deprelBT"+structure+structureI;
		int a=0;
		while(it.hasNext()){
			newFeature="deprelMergeBT"+structure+structureI+a+".xml";
			fg.addFeatureLineBefore(featureModelBruteForce,newFeature,it.next(),"Merge","DEPREL");
			a++;
			antFeature=newFeature;
			System.out.println("Testing "+newFeature +" ...");
			result=runBestAlgorithm(newFeature);
			difference=0.0;
			System.out.println("  "+result);
			System.out.println("  Default: "+bestResult);
			if (result>=(this.bestResultBruteForce+threshold)) { 
				this.featureModelBruteForce=antFeature; //dummy (just a matter of completity)
				difference=result-bestResultBruteForce;
				bestResultBruteForce=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				/*System.out.println("New best feature model: "+featureModelBruteForce);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");*/
				
			}
		}
	}
	
	private void predeccessorSuccessor(String window) {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		boolean anyOfThemImprove=false;
		
		String antFeature=featureModel;
			String newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			String value="Stack[0]";
			if (bestAlgorithm.contains("cov")) {
				value="Left[0]";
			}
			fg.addPredSucc(featureModel, newFeature, window, value, "pred");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		antFeature=featureModel;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			fg.addPredSucc(featureModel, newFeature, window, value, "succ");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		antFeature=featureModel;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			String val="Input[0]";
			if (bestAlgorithm.contains("stack")) {
				val="Stack[1]"; //Input[0] corresponds to Stack[0] but Stack[0] is already tested above!
			}
			if (bestAlgorithm.contains("cov")) {
				val="Right[0]";
			}
			fg.addPredSucc(featureModel, newFeature, window, val, "pred");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		antFeature=featureModel;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			String val2="Input[0]";
			if (bestAlgorithm.contains("stack")) {
				val2="Stack[1]"; //Input[0] corresponds to Stack[0] but Stack[0] is already tested above!
			}
			if (bestAlgorithm.contains("cov")) {
				val2="Right[0]";
			}
			fg.addPredSucc(featureModel, newFeature, window, val2, "succ");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		if (anyOfThemImprove && window.equals("POSTAG")){
			predeccessorSuccessor("FORM");
		}
		
		
		
	}
	
	private void predeccessorSuccessor5Fold(String window) {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		boolean anyOfThemImprove=false;
		
		String antFeature=featureModel;
			String newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			String value="Stack[0]";
			if (bestAlgorithm.contains("cov")) {
				value="Left[0]";
			}
			fg.addPredSucc(featureModel, newFeature, window, value, "pred");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm5Fold(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		antFeature=featureModel;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			fg.addPredSucc(featureModel, newFeature, window, value, "succ");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm5Fold(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		antFeature=featureModel;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			String val="Input[0]";
			if (bestAlgorithm.contains("stack")) {
				val="Stack[1]"; //Input[0] corresponds to Stack[0] but Stack[0] is already tested above!
			}
			if (bestAlgorithm.contains("cov")) {
				val="Right[0]";
			}
			fg.addPredSucc(featureModel, newFeature, window, val, "pred");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm5Fold(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		antFeature=featureModel;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			String val2="Input[0]";
			if (bestAlgorithm.contains("stack")) {
				val2="Stack[1]"; //Input[0] corresponds to Stack[0] but Stack[0] is already tested above!
			}
			if (bestAlgorithm.contains("cov")) {
				val2="Right[0]";
			}
			fg.addPredSucc(featureModel, newFeature, window, val2, "succ");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm5Fold(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		if (anyOfThemImprove && window.equals("POSTAG")){
			predeccessorSuccessor("FORM");
		}
		
		
		
	}
	
	private void predeccessorSuccessorRelaxedGreedy(String window) {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		boolean anyOfThemImprove=false;
		
		String antFeature=featureModel;
			String newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			String value="Stack[0]";
			if (bestAlgorithm.contains("cov")) {
				value="Left[0]";
			}
			fg.addPredSucc(featureModel, newFeature, window, value, "pred");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		antFeature=featureModel;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			fg.addPredSucc(featureModel, newFeature, window, value, "succ");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		antFeature=featureModel;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			String val="Input[0]";
			if (bestAlgorithm.contains("stack")) {
				val="Stack[1]"; //Input[0] corresponds to Stack[0] but Stack[0] is already tested above!
			}
			if (bestAlgorithm.contains("cov")) {
				val="Right[0]";
			}
			fg.addPredSucc(featureModel, newFeature, window, val, "pred");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		antFeature=featureModel;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			String val2="Input[0]";
			if (bestAlgorithm.contains("stack")) {
				val2="Stack[1]"; //Input[0] corresponds to Stack[0] but Stack[0] is already tested above!
			}
			if (bestAlgorithm.contains("cov")) {
				val2="Right[0]";
			}
			fg.addPredSucc(featureModel, newFeature, window, val2, "succ");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResult+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModel=newFeature;
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
			}
			
		if (window.equals("POSTAG")){
			predeccessorSuccessor("FORM");
		}
		
		
		
	}

	private void predeccessorSuccessorBruteForce(String window) {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		
		boolean anyOfThemImprove=false;
		
		String antFeature=featureModelBruteForce;
			String newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			String value="Stack[0]";
			if (bestAlgorithm.contains("cov")) {
				value="Left[0]";
			}
			fg.addPredSucc(featureModelBruteForce, newFeature, window, value, "pred");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			double result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			double difference=0.0;
			if (result>(this.bestResultBruteForce+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModelBruteForce=newFeature;
				difference=result-bestResult;
				bestResultBruteForce=result;
				
				
			}
			
		antFeature=featureModelBruteForce;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			fg.addPredSucc(featureModelBruteForce, newFeature, window, value, "succ");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResultBruteForce+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModelBruteForce=newFeature;
				difference=result-bestResult;
				bestResultBruteForce=result;
				
				
			}
			
			antFeature=featureModelBruteForce;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			value="Stack[1]";
			if (bestAlgorithm.contains("cov")) {
				value="Left[1]";
			}
			fg.addPredSucc(featureModelBruteForce, newFeature, window, value, "pred");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			if (result>(this.bestResultBruteForce+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModelBruteForce=newFeature;
				difference=result-bestResult;
				bestResultBruteForce=result;
				
				
			}
			
		antFeature=featureModelBruteForce;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			fg.addPredSucc(featureModelBruteForce, newFeature, window, value, "succ");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResultBruteForce+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModelBruteForce=newFeature;
				difference=result-bestResult;
				bestResultBruteForce=result;
				
				
			}
			
		antFeature=featureModelBruteForce;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			String val="Input[0]";
			if (bestAlgorithm.contains("stack")) {
				val="Stack[2]"; //Input[0] corresponds to Stack[0] but Stack[0] is already tested above!
			}
			if (bestAlgorithm.contains("cov")) {
				val="Right[0]";
			}
			fg.addPredSucc(featureModelBruteForce, newFeature, window, val, "pred");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResultBruteForce+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModelBruteForce=newFeature;
				difference=result-bestResult;
				bestResultBruteForce=result;
				
				
			}
			
		antFeature=featureModelBruteForce;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			String val2="Input[0]";
			if (bestAlgorithm.contains("stack")) {
				val2="Stack[2]"; //Input[0] corresponds to Stack[0] but Stack[0] is already tested above!
			}
			if (bestAlgorithm.contains("cov")) {
				val2="Right[0]";
			}
			fg.addPredSucc(featureModelBruteForce, newFeature, window, val2, "succ");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResultBruteForce+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModelBruteForce=newFeature;
				difference=result-bestResult;
				bestResultBruteForce=result;
				
			}
			
			antFeature=featureModelBruteForce;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			val="Input[1]";
			if (bestAlgorithm.contains("stack")) {
				val="Lookahead[0]"; //Input[0] corresponds to Stack[0] but Stack[0] is already tested above!
			}
			if (bestAlgorithm.contains("cov")) {
				val="Right[1]";
			}
			fg.addPredSucc(featureModelBruteForce, newFeature, window, val, "pred");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResultBruteForce+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModelBruteForce=newFeature;
				difference=result-bestResult;
				bestResultBruteForce=result;
				
				
			}
			
		antFeature=featureModelBruteForce;
			newFeature="predSuccFeature";
			newFeature+="predStack"+".xml";
			val2="Input[1]";
			if (bestAlgorithm.contains("stack")) {
				val2="Lookahead[0]"; //Input[0] corresponds to Stack[0] but Stack[0] is already tested above!
			}
			if (bestAlgorithm.contains("cov")) {
				val2="Right[1]";
			}
			fg.addPredSucc(featureModelBruteForce, newFeature, window, val2, "succ");
			antFeature=newFeature;
			//fg.removeInputWindow(newFeature, newFeature, window, newFeature)(featureModel,newFeature,"FORM");
			result=runBestAlgorithm(newFeature);
			//System.out.println(result);
			//
			difference=0.0;
			if (result>(this.bestResultBruteForce+threshold)) { //Shrinking
				anyOfThemImprove=true;
				featureModelBruteForce=newFeature;
				difference=result-bestResult;
				bestResultBruteForce=result;
				
			}
			
			
		if (window.equals("POSTAG")){
			predeccessorSuccessorBruteForce("FORM");
		}
	}
	
	
	private void addNewFeaturesCpostagFeatsLemma(String window) {
		// TODO Auto-generated method stub
		if (!window.equals("FEATS")) {
			System.out.println("\nAdding "+window+" features ...");
		}
		FeatureGenerator fg=new FeatureGenerator();
		
		boolean anyOfThem=false;
		ArrayList<Integer> stackValues=new ArrayList<Integer>();
		ArrayList<Integer> inputLookValues=new ArrayList<Integer>();
		
		String value="";
		String anterior="";
		boolean keepGoing=true;
		for(int i=0;i<3;i++) {
			if (keepGoing) {
			String newFeature4="add"+InputLookAhead+window+i+".xml";
			value=InputLookAhead+"["+i+"]";
			if (bestAlgorithm.contains("stack")){
				if (i==0) {
					value="Stack[0]";
				}
				else {
					int j=i-1;
					value=InputLookAhead+"["+j+"]";
				}
			}
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addFeature(featureModel,newFeature4,window,value);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				anyOfThem=true;
				inputLookValues.add(i);
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			else {
				keepGoing=false;
			}
			}
		}
		
		value="";
		anterior="";
		keepGoing=true;
		for(int i=0;i<3;i++) {
			if (keepGoing){
			String newFeature4="addStack"+window+i+".xml";
			value="Stack["+i+"]";
			if (bestAlgorithm.contains("stack")){
				int j=i+1;
				value="Stack["+j+"]";
			}
			if (bestAlgorithm.contains("cov")){
				value="Left["+i+"]";
			}
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addFeature(featureModel,newFeature4,window,value);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				anyOfThem=true;
				stackValues.add(i);
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			else {
				keepGoing=false;
			}
			}
		}
		
		if (window.equals("FEATS") && anyOfThem) {
			//Merge Unsplit with POSTAG
			Iterator<Integer> it = inputLookValues.iterator();
			while(it.hasNext()){
				Integer i=it.next();
				String newFeature4="addMergePostagFeats"+InputLookAhead+i+".xml";
				//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
				fg.addMergeFeatures(featureModel, newFeature4, "POSTAG", "FEATS", InputLookAhead, "InputColumn", i);
				anterior=newFeature4;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature4);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature4;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}
			
			Iterator<Integer> it2 = stackValues.iterator();
			while(it2.hasNext()){
				Integer i=it2.next();
				String newFeature4="addMergePostagFeatsStack"+i+".xml";
				//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
				fg.addMergeFeatures(featureModel, newFeature4, "POSTAG", "FEATS", "Stack", "InputColumn", i);
				anterior=newFeature4;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature4);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature4;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}
		}
	}
	
	private void addNewFeaturesCpostagFeatsLemma5Fold(String window) {
		// TODO Auto-generated method stub
		if (!window.equals("FEATS")) {
			System.out.println("\nAdding "+window+" features ...");
		}
		FeatureGenerator fg=new FeatureGenerator();
		
		boolean anyOfThem=false;
		ArrayList<Integer> stackValues=new ArrayList<Integer>();
		ArrayList<Integer> inputLookValues=new ArrayList<Integer>();
		
		String value="";
		String anterior="";
		boolean keepGoing=true;
		for(int i=0;i<3;i++) {
			if (keepGoing) {
			String newFeature4="add"+InputLookAhead+window+i+".xml";
			value=InputLookAhead+"["+i+"]";
			if (bestAlgorithm.contains("stack")){
				if (i==0) {
					value="Stack[0]";
				}
				else {
					int j=i-1;
					value=InputLookAhead+"["+j+"]";
				}
			}
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addFeature(featureModel,newFeature4,window,value);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm5Fold(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				anyOfThem=true;
				inputLookValues.add(i);
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			else {
				keepGoing=false;
			}
			}
		}
		
		value="";
		anterior="";
		keepGoing=true;
		for(int i=0;i<3;i++) {
			if (keepGoing){
			String newFeature4="addStack"+window+i+".xml";
			value="Stack["+i+"]";
			if (bestAlgorithm.contains("stack")){
				int j=i+1;
				value="Stack["+j+"]";
			}
			if (bestAlgorithm.contains("cov")){
				value="Left["+i+"]";
			}
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addFeature(featureModel,newFeature4,window,value);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm5Fold(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				anyOfThem=true;
				stackValues.add(i);
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			else {
				keepGoing=false;
			}
			}
		}
		
		if (window.equals("FEATS") && anyOfThem) {
			//Merge Unsplit with POSTAG
			Iterator<Integer> it = inputLookValues.iterator();
			while(it.hasNext()){
				Integer i=it.next();
				String newFeature4="addMergePostagFeats"+InputLookAhead+i+".xml";
				//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
				fg.addMergeFeatures(featureModel, newFeature4, "POSTAG", "FEATS", InputLookAhead, "InputColumn", i);
				anterior=newFeature4;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm5Fold(newFeature4);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature4;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}
			
			Iterator<Integer> it2 = stackValues.iterator();
			while(it2.hasNext()){
				Integer i=it2.next();
				String newFeature4="addMergePostagFeatsStack"+i+".xml";
				//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
				fg.addMergeFeatures(featureModel, newFeature4, "POSTAG", "FEATS", "Stack", "InputColumn", i);
				anterior=newFeature4;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm5Fold(newFeature4);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature4;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}
		}
	}
	
	private void addNewFeaturesCpostagFeatsLemmaRelaxedGreedy(String window) {
		// TODO Auto-generated method stub
		if (!window.equals("FEATS")) 
			System.out.println("\nAdding "+window+" features ... ");
		
		FeatureGenerator fg=new FeatureGenerator();
		
		boolean anyOfThem=false;
		ArrayList<Integer> stackValues=new ArrayList<Integer>();
		ArrayList<Integer> inputLookValues=new ArrayList<Integer>();
		
		String value="";
		String anterior=featureModel;
		boolean keepGoing=true;
		for(int i=0;i<4;i++) {
			String newFeature4="add"+InputLookAhead+window+i+".xml";
			value=InputLookAhead+"["+i+"]";
			if (bestAlgorithm.contains("stack")){
				if (i==0) {
					value="Stack[0]";
				}
				else {
					int j=i-1;
					value=InputLookAhead+"["+j+"]";
				}
			}
			//anterior=featureModel;
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addFeature(anterior,newFeature4,window,value);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				anyOfThem=true;
				inputLookValues.add(i);
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		value="";
		anterior=featureModel;
		keepGoing=true;
		for(int i=0;i<4;i++) {
			String newFeature4="addStack"+window+i+".xml";
			value="Stack["+i+"]";
			if (bestAlgorithm.contains("stack")){
				int j=i+1;
				value="Stack["+j+"]";
			}
			if (bestAlgorithm.contains("cov")){
				value="Left["+i+"]";
			}
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			//anterior=featureModel;
			fg.addFeature(anterior,newFeature4,window,value);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				anyOfThem=true;
				stackValues.add(i);
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		if (window.equals("FEATS")) {
			//Merge Unsplit with POSTAG
			Iterator<Integer> it = inputLookValues.iterator();
			anterior=featureModel;
			while(it.hasNext()){
				Integer i=it.next();
				String newFeature4="addMergePostagFeats"+InputLookAhead+i+".xml";
				//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
				
				fg.addMergeFeatures(anterior, newFeature4, "POSTAG", "FEATS", InputLookAhead, "InputColumn", i);
				anterior=newFeature4;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature4);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature4;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}
			anterior=featureModel;
			Iterator<Integer> it2 = stackValues.iterator();
			while(it2.hasNext()){
				Integer i=it2.next();
				String newFeature4="addMergePostagFeatsStack"+i+".xml";
				//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
				fg.addMergeFeatures(anterior, newFeature4, "POSTAG", "FEATS", "Stack", "InputColumn", i);
				anterior=newFeature4;
				//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
				double result2=runBestAlgorithm(newFeature4);
				//System.out.println(result2);
				//System.out.println("best:"+bestResult);
				//
				double difference2=0.0;
				if (result2>(this.bestResult+threshold)) { //Shrinking
					featureModel=newFeature4;
					difference2=result2-bestResult;
					bestResult=result2;
					String sDifferenceLabel=""+difference2;
					if (sDifferenceLabel.length()>5)
						sDifferenceLabel=sDifferenceLabel.substring(0, 5);
					System.out.println("New best feature model: "+featureModel);
					String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
				
				}
			}
		}
	}
	
	
	private void addConjunctionFeatures(String window1, String window2) {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		String anterior="";
		
		ArrayList<Integer> inputLookValues=fg.getListOfValuesFeatures(featureModel, window2, InputLookAhead);
		ArrayList<Integer> stackValues=fg.getListOfValuesFeatures(featureModel, window2, "Stack");
		/*System.out.println(inputLookValues);
		System.out.println(stackValues);*/
		
		////////////////////////////////////////////////////////////////////////////////////
		//MERGE WITH OWN POSTAG
		/////////////////////////////////////////////////////////////////////////////////
		Iterator<Integer> it = inputLookValues.iterator();
		
		while(it.hasNext()){
			Integer i=it.next();
			String newFeature4="addMerg"+window1+window2+""+InputLookAhead+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeatures(featureModel, newFeature4, "POSTAG", "FORM", InputLookAhead, "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		Iterator<Integer> it2 = stackValues.iterator();
		
		/*while(it2.hasNext()){
			Integer i=it2.next();
			String newFeature4="addMerg"+window1+window2+"Stack"+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeatures(featureModel, newFeature4, "POSTAG", "FORM", "Stack", "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}*/
		
		
		////////////////////////////////////////////////////////////////////////////////////
		//MERGE WITH POSTAG STACK[0]
		/////////////////////////////////////////////////////////////////////////////////
		
		it = inputLookValues.iterator();
		while(it.hasNext()){
			Integer i=it.next();
			String newFeature4="addMerg"+window1+"S0"+window2+""+InputLookAhead+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeaturesS0(featureModel, newFeature4, "POSTAG", "FORM", InputLookAhead, "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		//featureModel="addMergePostagS0FeatsInput0.xml";
		it2 = stackValues.iterator();
		while(it2.hasNext()){
			Integer i=it2.next();
			String newFeature4="addMerg"+window1+"S0"+window2+"Stack"+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeaturesS0(featureModel, newFeature4, "POSTAG", "FORM", "Stack", "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			
		}
		//System.exit(0);
		
		////////////////////////////////////////////////////////////////////////////////////
		//MERGE WITH POSTAG INPUT[0]
		/////////////////////////////////////////////////////////////////////////////////
		
		it = inputLookValues.iterator();
		while(it.hasNext()){
			Integer i=it.next();
			String newFeature4="addMerg"+window1+"I0"+window2+""+InputLookAhead+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeaturesI0(featureModel, newFeature4, "POSTAG", "FORM", InputLookAhead, "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		it2 = stackValues.iterator();
		while(it2.hasNext()){
			Integer i=it2.next();
			String newFeature4="addMerg"+window1+"I0"+window2+"Stack"+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeaturesI0(featureModel, newFeature4, "POSTAG", "FORM", "Stack", "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		
		
		////////////////////////////////////////////////////////////////////////////////////
		//MERGE WITH MERGE(P(S[0],P(I[0]))
		/////////////////////////////////////////////////////////////////////////////////
		
		it = inputLookValues.iterator();
		while(it.hasNext()){
			Integer i=it.next();
			String newFeature4="addMerg"+window1+"S0I0"+window2+""+InputLookAhead+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeaturesMerge3(featureModel, newFeature4, "POSTAG", "FORM", InputLookAhead, "InputColumn", i);
			//System.out.println(newFeature4);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		it2 = stackValues.iterator();
		while(it2.hasNext()){
			Integer i=it2.next();
			String newFeature4="addMerg"+window1+"S0I0"+window2+"Stack"+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeaturesMerge3(featureModel, newFeature4, "POSTAG", "FORM", "Stack", "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		
	}
	
	private void addConjunctionFeatures5Fold(String window1, String window2) {
		// TODO Auto-generated method stub
		FeatureGenerator fg=new FeatureGenerator();
		String anterior="";
		
		ArrayList<Integer> inputLookValues=fg.getListOfValuesFeatures(featureModel, window2, InputLookAhead);
		ArrayList<Integer> stackValues=fg.getListOfValuesFeatures(featureModel, window2, "Stack");
		/*System.out.println(inputLookValues);
		System.out.println(stackValues);*/
		
		////////////////////////////////////////////////////////////////////////////////////
		//MERGE WITH OWN POSTAG
		/////////////////////////////////////////////////////////////////////////////////
		Iterator<Integer> it = inputLookValues.iterator();
		
		while(it.hasNext()){
			Integer i=it.next();
			String newFeature4="addMerg"+window1+window2+""+InputLookAhead+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeatures(featureModel, newFeature4, "POSTAG", "FORM", InputLookAhead, "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm5Fold(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		Iterator<Integer> it2 = stackValues.iterator();
		
		/*while(it2.hasNext()){
			Integer i=it2.next();
			String newFeature4="addMerg"+window1+window2+"Stack"+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeatures(featureModel, newFeature4, "POSTAG", "FORM", "Stack", "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm5Fold(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}*/
		
		
		////////////////////////////////////////////////////////////////////////////////////
		//MERGE WITH POSTAG STACK[0]
		/////////////////////////////////////////////////////////////////////////////////
		
		it = inputLookValues.iterator();
		while(it.hasNext()){
			Integer i=it.next();
			String newFeature4="addMerg"+window1+"S0"+window2+""+InputLookAhead+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeaturesS0(featureModel, newFeature4, "POSTAG", "FORM", InputLookAhead, "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm5Fold(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		//featureModel="addMergePostagS0FeatsInput0.xml";
		it2 = stackValues.iterator();
		while(it2.hasNext()){
			Integer i=it2.next();
			String newFeature4="addMerg"+window1+"S0"+window2+"Stack"+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeaturesS0(featureModel, newFeature4, "POSTAG", "FORM", "Stack", "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			
			double result2=runBestAlgorithm5Fold(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			
		}
		//System.exit(0);
		
		////////////////////////////////////////////////////////////////////////////////////
		//MERGE WITH POSTAG INPUT[0]
		/////////////////////////////////////////////////////////////////////////////////
		
		it = inputLookValues.iterator();
		while(it.hasNext()){
			Integer i=it.next();
			String newFeature4="addMerg"+window1+"I0"+window2+""+InputLookAhead+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeaturesI0(featureModel, newFeature4, "POSTAG", "FORM", InputLookAhead, "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm5Fold(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		it2 = stackValues.iterator();
		while(it2.hasNext()){
			Integer i=it2.next();
			String newFeature4="addMerg"+window1+"I0"+window2+"Stack"+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeaturesI0(featureModel, newFeature4, "POSTAG", "FORM", "Stack", "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm5Fold(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		
		
		////////////////////////////////////////////////////////////////////////////////////
		//MERGE WITH MERGE(P(S[0],P(I[0]))
		/////////////////////////////////////////////////////////////////////////////////
		
		it = inputLookValues.iterator();
		while(it.hasNext()){
			Integer i=it.next();
			String newFeature4="addMerg"+window1+"S0I0"+window2+""+InputLookAhead+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeaturesMerge3(featureModel, newFeature4, "POSTAG", "FORM", InputLookAhead, "InputColumn", i);
			//System.out.println(newFeature4);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm5Fold(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		it2 = stackValues.iterator();
		while(it2.hasNext()){
			Integer i=it2.next();
			String newFeature4="addMerg"+window1+"S0I0"+window2+"Stack"+i+".xml";
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addMergeFeaturesMerge3(featureModel, newFeature4, "POSTAG", "FORM", "Stack", "InputColumn", i);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm5Fold(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		
	}
	
	private void addSplitFeaturesFeatsRelaxedGreedy(String window) {
		// TODO Auto-generated method stub
		System.out.println("\nAdding "+window+" features ... ");
		FeatureGenerator fg=new FeatureGenerator();
		
		boolean anyOfThem=false;
		
		String value="";
		String anterior=featureModel;
		boolean keepGoing=true;
		for(int i=0;i<5;i++) {
			String newFeature4="addSplit"+InputLookAhead+window+i+".xml";
			value=InputLookAhead+"["+i+"]";
			if (bestAlgorithm.contains("stack")){
				if (i==0) {
					value="Stack[0]";
				}
				else {
					int j=i-1;
					value=InputLookAhead+"["+j+"]";
				}
				
			}
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addSplitFeature(anterior,newFeature4,window,value);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				anyOfThem=true;
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		value="";
		anterior=featureModel;
		for(int i=0;i<5;i++) {
			String newFeature4="addSplitStack"+window+i+".xml";
			value="Stack["+i+"]";
			if (bestAlgorithm.contains("stack")){
				int j=i+1;
				value="Stack["+j+"]";
			}
			if (bestAlgorithm.contains("cov")){
				value="Left["+i+"]";
			}
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addSplitFeature(anterior,newFeature4,window,value);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				anyOfThem=true;
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
		}
		
		//if (anyOfThem) {
		addNewFeaturesCpostagFeatsLemmaRelaxedGreedy("FEATS");
			//MERGE
		//}
	}
	
	private void addSplitFeaturesFeats(String window) {
		// TODO Auto-generated method stub
		System.out.println("\nAdding "+window+" features ... ");
		FeatureGenerator fg=new FeatureGenerator();
		
		boolean anyOfThem=false;
		
		String value="";
		String anterior="";
		boolean keepGoing=true;
		for(int i=0;i<4;i++) {
			if (keepGoing) {
			String newFeature4="addSplit"+InputLookAhead+window+i+".xml";
			value=InputLookAhead+"["+i+"]";
			if (bestAlgorithm.contains("stack")){
				if (i==0) {
					value="Stack[0]";
				}
				else {
					int j=i-1;
					value=InputLookAhead+"["+j+"]";
				}
				
			}
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addSplitFeature(featureModel,newFeature4,window,value);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				anyOfThem=true;
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			else {
				keepGoing=false;
			}
			}
		}
		
		value="";
		anterior="";
		keepGoing=true;
		for(int i=0;i<4;i++) {
			if (keepGoing) {
			String newFeature4="addSplitStack"+window+i+".xml";
			value="Stack["+i+"]";
			if (bestAlgorithm.contains("stack")){
				int j=i+1;
				value="Stack["+j+"]";
			}
			if (bestAlgorithm.contains("cov")){
				value="Left["+i+"]";
			}
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addSplitFeature(featureModel,newFeature4,window,value);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				anyOfThem=true;
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			else {
				keepGoing=false;
			}
			}
		}
		
		if (anyOfThem) {
			this.addNewFeaturesCpostagFeatsLemma("FEATS");
			//MERGE
		}
	}
	
	
	private void addSplitFeaturesFeats5Fold(String window) {
		// TODO Auto-generated method stub
		System.out.println("\nAdding "+window+" features ... ");
		FeatureGenerator fg=new FeatureGenerator();
		
		boolean anyOfThem=false;
		
		String value="";
		String anterior="";
		boolean keepGoing=true;
		for(int i=0;i<4;i++) {
			if (keepGoing) {
			String newFeature4="addSplit"+InputLookAhead+window+i+".xml";
			value=InputLookAhead+"["+i+"]";
			if (bestAlgorithm.contains("stack")){
				if (i==0) {
					value="Stack[0]";
				}
				else {
					int j=i-1;
					value=InputLookAhead+"["+j+"]";
				}
				
			}
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addSplitFeature(featureModel,newFeature4,window,value);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm5Fold(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				anyOfThem=true;
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			else {
				keepGoing=false;
			}
			}
		}
		
		value="";
		anterior="";
		keepGoing=true;
		for(int i=0;i<4;i++) {
			if (keepGoing) {
			String newFeature4="addSplitStack"+window+i+".xml";
			value="Stack["+i+"]";
			if (bestAlgorithm.contains("stack")){
				int j=i+1;
				value="Stack["+j+"]";
			}
			if (bestAlgorithm.contains("cov")){
				value="Left["+i+"]";
			}
			//(String featureModel, String newFeature, String window, String inputStack, String predSucc)
			fg.addSplitFeature(featureModel,newFeature4,window,value);
			anterior=newFeature4;
			//OJO!!! En este caso es Input porque para el primer test es con NIVREEAGER. Habrá que usar LOOKAHEAD EN OTROS
			double result2=runBestAlgorithm5Fold(newFeature4);
			//System.out.println(result2);
			//System.out.println("best:"+bestResult);
			//
			double difference2=0.0;
			if (result2>(this.bestResult+threshold)) { //Shrinking
				anyOfThem=true;
				featureModel=newFeature4;
				difference2=result2-bestResult;
				bestResult=result2;
				String sDifferenceLabel=""+difference2;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best feature model: "+featureModel);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			else {
				keepGoing=false;
			}
			}
		}
		
		if (anyOfThem) {
			this.addNewFeaturesCpostagFeatsLemma("FEATS");
			//MERGE
		}
	}
	

	private double runBestAlgorithm(String feature) {
		// TODO Auto-generated method stub
		//String language, double np, CoNLLHandler ch, String trainingCorpus, String rootHandling
		File f=new File(feature);
		if (f.exists()) {
		CoNLLHandler ch=new CoNLLHandler(trainingCorpus);
		AlgorithmTester at=new AlgorithmTester("lang",this.percentage,ch,trainingCorpus,optionMenosR);
		double result=0.0;
		try {
		if (bestAlgorithm.equals("nivreeager")) {
			result=at.executeNivreEager(feature);
		}
		
		if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
			//result=at.executeNivreEager(feature);
			result=at.executeNivreStandard(feature);
		}
		
		if (bestAlgorithm.equals("covnonproj")) {
			//result=at.executeNivreEager(feature);
			result=at.executeCovingtonNonProjective(feature);
		}
		
		if (bestAlgorithm.equals("covproj")) {
			//result=at.executeNivreEager(feature);
			result=at.executeCovingtonProjective(feature);
		}
		
		if (bestAlgorithm.equals("stackproj")) {
			//result=at.executeNivreEager(feature);
			result=at.executeStackProjective(feature);
		}
		
		if (bestAlgorithm.equals("stackeager")) {
			//result=at.feature);
			result=at.executestackEager(feature);
		}
		
		if (bestAlgorithm.equals("stacklazy")) {
			//result=at.executeNivreEager(feature);
			result=at.executeStackLazy(feature);
		}
		}catch(Exception e) {
			System.out.println("Feature not valid.");
		}
		//System.out.println(result);
		return result;
		}
		return 0.0;
	}
	
	private double runBestAlgorithm5Fold(String feature) { 
		// TODO Auto-generated method stub
		//String language, double np, CoNLLHandler ch, String trainingCorpus, String rootHandling
		
		int contExitos=0;
		
		File f=new File(feature);
		if (f.exists()) {
		CoNLLHandler ch=new CoNLLHandler(trainingCorpus);
		AlgorithmTester at=new AlgorithmTester("lang",this.percentage,ch,trainingCorpus,optionMenosR);
		double result=0.0;
		double max=bestResult;
		double sum=0.0;
		for (int i=1;i<6;i++) {
			try {
				if (bestAlgorithm.equals("nivreeager")) {
					result=at.executeNivreEagerTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (bestAlgorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
					//result=at.executeNivreEager(feature);
					result=at.executeNivreStandardTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (bestAlgorithm.equals("covnonproj")) {
					//result=at.executeNivreEager(feature);
					result=at.executeCovingtonNonProjectiveTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (bestAlgorithm.equals("covproj")) {
					//result=at.executeNivreEager(feature);
					result=at.executeCovingtonProjectiveTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (bestAlgorithm.equals("stackproj")) {
					//result=at.executeNivreEager(feature);
					result=at.executeStackProjectiveTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (bestAlgorithm.equals("stackeager")) {
					//result=at.feature);
					result=at.executestackEagerTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (bestAlgorithm.equals("stacklazy")) {
					//result=at.executeNivreEager(feature);
					result=at.executeStackLazyTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
				}catch(Exception e) {
					System.out.println("Feature not valid.");
				}
				String s=""+result;
				if (s.length()==4) s+="0";
				
				System.out.println("    Fold "+i+": "+s+"%");
				sum+=result;
				if (result>=bestResult) {
					contExitos++;
					if (max<result) max=result;
				}
			}
		sum=sum/5;
		String cad=String.valueOf(sum);
		if (cad.length()>4) {
		int a=Integer.parseInt(new String(""+cad.charAt(4)));
		int b=Integer.parseInt(new String(""+cad.charAt(3)));
		
		if ((cad.length()>5) && (a==9)) {
			cad=cad.substring(0,5);
		}
		else if ((cad.length()>5)) {
			if (cad.charAt(5)>5) {
				char c=cad.charAt(4);
				Integer in=Integer.parseInt(new String(""+c));
				in++;
				cad=cad.substring(0,4);
				
				cad=cad+""+in;
			}
			else {
				cad=cad.substring(0, 5);
			}
		}
		}
			if (cad.length()==4){
				cad+="0";
			}
			//if (cad.length()>=5) cad=cad.substring(0,5);
			sum=Double.parseDouble(cad);
			System.out.println("    Average: "+cad+"%");
			if (Optimizer.chooseAverage) {
				
				if (sum>=Optimizer.bestResult) {
					
					return sum;
				}
			}
			else if (Optimizer.chooseMajority) {
				if (contExitos>=3) {
					return sum;
				}
			}
			else if (Optimizer.chooseAllOfThem) {
				if (contExitos>=5) {
					return sum;
				}
			}
			return 0.0;
		}
		return 0.0;
	}
	
	private double runAlgorithm5Fold(String feature, String algorithm) { 
		// TODO Auto-generated method stub
		//String language, double np, CoNLLHandler ch, String trainingCorpus, String rootHandling
		
		int contExitos=0;
		
		File f=new File(feature);
		if (f.exists()) {
		CoNLLHandler ch=new CoNLLHandler(trainingCorpus);
		AlgorithmTester at=new AlgorithmTester("lang",this.percentage,ch,trainingCorpus,optionMenosR);
		double result=0.0;
		double max=bestResult;
		double sum=0.0;
		for (int i=1;i<6;i++) {
			try {
				if (algorithm.equals("nivreeager")) {
					result=at.executeNivreEagerTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (algorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
					//result=at.executeNivreEager(feature);
					result=at.executeNivreStandardTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (algorithm.equals("covnonproj")) {
					//result=at.executeNivreEager(feature);
					result=at.executeCovingtonNonProjectiveTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (algorithm.equals("covproj")) {
					//result=at.executeNivreEager(feature);
					result=at.executeCovingtonProjectiveTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (algorithm.equals("stackproj")) {
					//result=at.executeNivreEager(feature);
					result=at.executeStackProjectiveTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (algorithm.equals("stackeager")) {
					//result=at.feature);
					result=at.executestackEagerTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (algorithm.equals("stacklazy")) {
					//result=at.executeNivreEager(feature);
					result=at.executeStackLazyTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
				}catch(Exception e) {
					System.out.println("Feature not valid.");
				}
				String s=""+result;
				if (s.length()==4) s+="0";
			
				System.out.println("    Fold "+i+": "+s+"%");
				sum+=result;
				if (result>=bestResult) {
					contExitos++;
					if (max<result) max=result;
				}
			}
		sum=sum/5;
		String cad=String.valueOf(sum);
		if (cad.length()>4) {
		int a=Integer.parseInt(new String(""+cad.charAt(4)));
		int b=Integer.parseInt(new String(""+cad.charAt(3)));
		
		if ((cad.length()>5) && (a==9)) {
			cad=cad.substring(0,5);
		}
		else if ((cad.length()>5)) {
			if (cad.charAt(5)>5) {
				char c=cad.charAt(4);
				Integer in=Integer.parseInt(new String(""+c));
				in++;
				cad=cad.substring(0,4);
				
				cad=cad+""+in;
			}
			else {
				cad=cad.substring(0, 5);
			}
		}
		}
			if (cad.length()==4){
				cad+="0";
			}
			//if (cad.length()>=5) cad=cad.substring(0,5);
			sum=Double.parseDouble(cad);
			System.out.println("    Average: "+cad+"%");
			if (Optimizer.chooseAverage) {
				
				if (sum>=Optimizer.bestResult) {		
					return sum;
				}
			}
			else if (Optimizer.chooseMajority) {
				if (contExitos>=3) {
					return sum;
				}
			}
			else if (Optimizer.chooseAllOfThem) {
				if (contExitos>=5) {
					return sum;
				}
			}
			return 0.0;
		}
		return 0.0;
	}
	
	private double runCovingtonNonProjectivePPAllowShiftAllowRoot5Fold(String featureModel, String head, boolean allow_shift, boolean allow_root) {
		int contExitos=0;
		
		//File f=new File(feature);
		//if (f.exists()) {
		CoNLLHandler ch=new CoNLLHandler(trainingCorpus);
		AlgorithmTester at=new AlgorithmTester("lang",this.percentage,ch,trainingCorpus,optionMenosR);
		double result=0.0;
		double max=bestResult;
		double sum=0.0;
		for (int i=1;i<6;i++) {
			try {
				result=at.executeCovingtonNonProjectivePPAllowShiftAllowRootTestTrain(featureModel,head,allow_shift,allow_root,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}catch(Exception e) {
					System.out.println("Feature not valid.");
				}
				String s=""+result;
				if (s.length()==4) s+="0";
		
				System.out.println("    Fold "+i+": "+s+"%");
				sum+=result;
				if (result>=bestResult) {
					contExitos++;
					if (max<result) max=result;
				}
			}
		sum=sum/5;
		String cad=String.valueOf(sum);
		if (cad.length()>4) {
		int a=Integer.parseInt(new String(""+cad.charAt(4)));
		int b=Integer.parseInt(new String(""+cad.charAt(3)));
		
		if ((cad.length()>5) && (a==9)) {
			cad=cad.substring(0,5);
		}
		else if ((cad.length()>5)) {
			if (cad.charAt(5)>5) {
				char c=cad.charAt(4);
				Integer in=Integer.parseInt(new String(""+c));
				in++;
				cad=cad.substring(0,4);
				
				cad=cad+""+in;
			}
			else {
				cad=cad.substring(0, 5);
			}
		}
		}
			if (cad.length()==4){
				cad+="0";
			}
			//if (cad.length()>=5) cad=cad.substring(0,5);
			sum=Double.parseDouble(cad);
			System.out.println("    Average: "+cad+"%");
			if (Optimizer.chooseAverage) {
				
				if (sum>=Optimizer.bestResult) {
					return sum;
				}
			}
			else if (Optimizer.chooseMajority) {
				if (contExitos>=3) {
					return sum;
				}
			}
			else if (Optimizer.chooseAllOfThem) {
				if (contExitos>=5) {
					return sum;
				}
			}
			return 0.0;
		//}
		//return 0.0;
	}
	
	private double runCovingtonProjectivePPAllowShiftAllowRoot5Fold(String featureModel, String head, boolean allow_shift, boolean allow_root) {
		int contExitos=0;
		
		//File f=new File(feature);
		//if (f.exists()) {
		CoNLLHandler ch=new CoNLLHandler(trainingCorpus);
		AlgorithmTester at=new AlgorithmTester("lang",this.percentage,ch,trainingCorpus,optionMenosR);
		double result=0.0;
		double max=bestResult;
		double sum=0.0;
		for (int i=1;i<6;i++) {
			try {
				result=at.executeCovingtonProjectivePPAllowShiftAllowRootTestTrain(featureModel,head,allow_shift,allow_root,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}catch(Exception e) {
					System.out.println("Feature not valid.");
				}
			String s=""+result;
			if (s.length()==4) s+="0";
	
			System.out.println("    Fold "+i+": "+s+"%");
				sum+=result;
				if (result>=bestResult) {
					contExitos++;
					if (max<result) max=result;
				}
			}
		sum=sum/5;
		String cad=String.valueOf(sum);
		if (cad.length()>4) {
		int a=Integer.parseInt(new String(""+cad.charAt(4)));
		int b=Integer.parseInt(new String(""+cad.charAt(3)));
		
		if ((cad.length()>5) && (a==9)) {
			cad=cad.substring(0,5);
		}
		else if ((cad.length()>5)) {
			if (cad.charAt(5)>5) {
				char c=cad.charAt(4);
				Integer in=Integer.parseInt(new String(""+c));
				in++;
				cad=cad.substring(0,4);
				
				cad=cad+""+in;
			}
			else {
				cad=cad.substring(0, 5);
			}
		}
		}
			if (cad.length()==4){
				cad+="0";
			}
			//if (cad.length()>=5) cad=cad.substring(0,5);
			sum=Double.parseDouble(cad);
			System.out.println("    Average: "+cad+"%");
			if (Optimizer.chooseAverage) {
				
				if (sum>=Optimizer.bestResult) {
					
					return sum;
				}
			}
			else if (Optimizer.chooseMajority) {
				if (contExitos>=3) {
					return sum;
				}
			}
			else if (Optimizer.chooseAllOfThem) {
				if (contExitos>=5) {
					return sum;
				}
			}
			return 0.0;
		//}
		//return 0.0;
	}
	
	private double runCovingtonNonProjectiveAllowShiftAllowRoot5Fold(String featureModel, boolean allow_shift, boolean allow_root) {
		int contExitos=0;
		
		//File f=new File(feature);
		//if (f.exists()) {
		CoNLLHandler ch=new CoNLLHandler(trainingCorpus);
		AlgorithmTester at=new AlgorithmTester("lang",this.percentage,ch,trainingCorpus,optionMenosR);
		double result=0.0;
		double max=bestResult;
		double sum=0.0;
		for (int i=1;i<6;i++) {
			try {
				//result=at.executeCovingtonNonProjectivePPAllowShiftAllowRootTestTrain(featureModel,head,allow_shift,allow_root,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				result=at.executeCovingtonNonProjectiveAllowShiftAllowRootTestTrain(featureModel,allow_shift,allow_root,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}catch(Exception e) {
					System.out.println("Feature not valid.");
				}
			String s=""+result;
			if (s.length()==4) s+="0";
	
			System.out.println("    Fold "+i+": "+s+"%");
				sum+=result;
				if (result>=bestResult) {
					contExitos++;
					if (max<result) max=result;
				}
			}
		sum=sum/5;
		String cad=String.valueOf(sum);
		if (cad.length()>4) {
		int a=Integer.parseInt(new String(""+cad.charAt(4)));
		int b=Integer.parseInt(new String(""+cad.charAt(3)));
		
		if ((cad.length()>5) && (a==9)) {
			cad=cad.substring(0,5);
		}
		else if ((cad.length()>5)) {
			if (cad.charAt(5)>5) {
				char c=cad.charAt(4);
				Integer in=Integer.parseInt(new String(""+c));
				in++;
				cad=cad.substring(0,4);
				
				cad=cad+""+in;
			}
			else {
				cad=cad.substring(0, 5);
			}
		}
		}
			if (cad.length()==4){
				cad+="0";
			}
			//if (cad.length()>=5) cad=cad.substring(0,5);
			sum=Double.parseDouble(cad);
			System.out.println("    Average: "+cad+"%");
			if (Optimizer.chooseAverage) {
				
				if (sum>=Optimizer.bestResult) {
					//System.out.println("    (Av:"+sum+")");
					return sum;
				}
			}
			else if (Optimizer.chooseMajority) {
				if (contExitos>=3) {
					return sum;
				}
			}
			else if (Optimizer.chooseAllOfThem) {
				if (contExitos>=5) {
					return sum;
				}
			}
			return 0.0;
		//}
		//return 0.0;
	}
	
	private double runCovingtonProjectiveAllowShiftAllowRoot5Fold(String featureModel, boolean allow_shift, boolean allow_root) {
		int contExitos=0;
		
		//File f=new File(feature);
		//if (f.exists()) {
		CoNLLHandler ch=new CoNLLHandler(trainingCorpus);
		AlgorithmTester at=new AlgorithmTester("lang",this.percentage,ch,trainingCorpus,optionMenosR);
		double result=0.0;
		double max=bestResult;
		double sum=0.0;
		for (int i=1;i<6;i++) {
			try {
				//result=at.executeCovingtonNonProjectivePPAllowShiftAllowRootTestTrain(featureModel,head,allow_shift,allow_root,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				result=at.executeCovingtonProjectiveAllowShiftAllowRootTestTrain(featureModel,allow_shift,allow_root,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}catch(Exception e) {
					System.out.println("Feature not valid.");
				}
			String s=""+result;
			if (s.length()==4) s+="0";
	
			System.out.println("    Fold "+i+": "+s+"%");
				sum+=result;
				if (result>=bestResult) {
					contExitos++;
					if (max<result) max=result;
				}
			}
		sum=sum/5;
		String cad=String.valueOf(sum);
		if (cad.length()>4) {
		int a=Integer.parseInt(new String(""+cad.charAt(4)));
		int b=Integer.parseInt(new String(""+cad.charAt(3)));
		
		if ((cad.length()>5) && (a==9)) {
			cad=cad.substring(0,5);
		}
		else if ((cad.length()>5)) {
			if (cad.charAt(5)>5) {
				char c=cad.charAt(4);
				Integer in=Integer.parseInt(new String(""+c));
				in++;
				cad=cad.substring(0,4);
				
				cad=cad+""+in;
			}
			else {
				cad=cad.substring(0, 5);
			}
		}
		}
			if (cad.length()==4){
				cad+="0";
			}
			//if (cad.length()>=5) cad=cad.substring(0,5);
			sum=Double.parseDouble(cad);
			System.out.println("    Average: "+cad+"%");
			if (Optimizer.chooseAverage) {
				
				if (sum>=Optimizer.bestResult) {
					return sum;
				}
			}
			else if (Optimizer.chooseMajority) {
				if (contExitos>=3) {
					return sum;
				}
			}
			else if (Optimizer.chooseAllOfThem) {
				if (contExitos>=5) {
					return sum;
				}
			}
			return 0.0;
		//}
		//return 0.0;
	}
	
	
	/*private double runAlgorithm5Fold(String feature, String algorithm) { 
		// TODO Auto-generated method stub
		//String language, double np, CoNLLHandler ch, String trainingCorpus, String rootHandling
		
		int contExitos=0;
		
		File f=new File(feature);
		if (f.exists()) {
		CoNLLHandler ch=new CoNLLHandler(trainingCorpus);
		AlgorithmTester at=new AlgorithmTester("lang",this.percentage,ch,trainingCorpus,optionMenosR);
		double result=0.0;
		double max=bestResult;
		double sum=0.0;
		for (int i=1;i<6;i++) {
			try {
				if (algorithm.equals("nivreeager")) {
					//result=at.executeNivreEagerTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
					result=at.executeNivreEagerTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (algorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
					//result=at.executeNivreEager(feature);
					result=at.executeNivreStandardTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (algorithm.equals("covnonproj")) {
					//result=at.executeNivreEager(feature);
					result=at.executeCovingtonNonProjectiveTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (algorithm.equals("covproj")) {
					//result=at.executeNivreEager(feature);
					result=at.executeCovingtonProjectiveTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (algorithm.equals("stackproj")) {
					//result=at.executeNivreEager(feature);
					result=at.executeStackProjectiveTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (algorithm.equals("stackeager")) {
					//result=at.feature);
					result=at.executestackEagerTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
		
				if (algorithm.equals("stacklazy")) {
					//result=at.executeNivreEager(feature);
					result=at.executeStackLazyTestTrain(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}
				}catch(Exception e) {
					System.out.println("Feature not valid.");
				}
				System.out.println("    Fold "+i+":"+result);
				sum+=result;
				if (result>=bestResult) {
					contExitos++;
					if (max<result) max=result;
				}
			}
			sum=sum/5;
			if (Optimizer.chooseAverage) {
				
				if (sum>=Optimizer.bestResult) {
					System.out.println("    (Av:"+sum+")");
					return sum;
				}
			}
			else if (Optimizer.chooseMajority) {
				if (contExitos>=3) {
					return sum;
				}
			}
			else if (Optimizer.chooseAllOfThem) {
				if (contExitos>=5) {
					return sum;
				}
			}
			return 0.0;
		}
		return 0.0;
	}*/
	
	
	private double runAlgorithm5FoldPPOption(String feature, String algorithm) { 
		// TODO Auto-generated method stub
		//String language, double np, CoNLLHandler ch, String trainingCorpus, String rootHandling
		
		int contExitos=0;
		
		File f=new File(feature);
		if (f.exists()) {
		CoNLLHandler ch=new CoNLLHandler(trainingCorpus);
		AlgorithmTester at=new AlgorithmTester("lang",this.percentage,ch,trainingCorpus,optionMenosR);
		double result=0.0;
		double max=bestResult;
		double sum=0.0;
		for (int i=1;i<6;i++) {
			try {
				if (algorithm.equals("nivreeager")) {
					result=at.executeNivreEagerTestTrainPPOption(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll",Optimizer.ppOption);
				}
		
				if (algorithm.equals("nivrestandard")) {  //A PARTIR DE LA PROXIMA VERSION PONER EL FEATURE CORRESPONDIENTE!!!
					//result=at.executeNivreEager(feature);
					result=at.executeNivreStandardTestTrainPPOption(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll",Optimizer.ppOption);
				}
		
				/*if (bestAlgorithm.equals("covnonproj")) {
					//result=at.executeNivreEager(feature);
					result=at.executeCovingtonNonProjectiveTestTrainPPOption(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll");
				}*/
		
				if (algorithm.equals("covproj")) {
					//result=at.executeNivreEager(feature);
					result=at.executeCovingtonProjectiveTestTrainPPOption(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll",Optimizer.ppOption);
				}
		
				if (algorithm.equals("stackproj")) {
					//result=at.executeNivreEager(feature);
					result=at.executeStackProjectiveTestTrainPPOption(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll",Optimizer.ppOption);
				}
		
				if (algorithm.equals("stackeager")) {
					//result=at.feature);
					result=at.executestackEagerTestTrainPPOption(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll",Optimizer.ppOption);
				}
		
				if (algorithm.equals("stacklazy")) {
					//result=at.executeNivreEager(feature);
					result=at.executeStackLazyTestTrainPPOption(feature,"fold_train_"+i+".conll","fold_test_"+i+".conll",Optimizer.ppOption);
				}
				}catch(Exception e) {
					System.out.println("Feature not valid.");
				}
			String s=""+result;
			if (s.length()==4) s+="0";
	
			System.out.println("    Fold "+i+": "+s+"%");
				sum+=result;
				if (result>=bestResult) {
					contExitos++;
					if (max<result) max=result;
				}
			}
		sum=sum/5;
		String cad=String.valueOf(sum);
		if (cad.length()>4) {
		int a=Integer.parseInt(new String(""+cad.charAt(4)));
		int b=Integer.parseInt(new String(""+cad.charAt(3)));
		
		if ((cad.length()>5) && (a==9)) {
			cad=cad.substring(0,5);
		}
		else if ((cad.length()>5)) {
			if (cad.charAt(5)>5) {
				char c=cad.charAt(4);
				Integer in=Integer.parseInt(new String(""+c));
				in++;
				cad=cad.substring(0,4);
				
				cad=cad+""+in;
			}
			else {
				cad=cad.substring(0, 5);
			}
		}
		}
			if (cad.length()==4){
				cad+="0";
			}
			//if (cad.length()>=5) cad=cad.substring(0,5);
			sum=Double.parseDouble(cad);
			System.out.println("    Average: "+cad+"%");
			if (Optimizer.chooseAverage) {
				
				if (sum>=Optimizer.bestResult) {
					return sum;
				}
			}
			else if (Optimizer.chooseMajority) {
				if (contExitos>=3) {
					return sum;
				}
			}
			else if (Optimizer.chooseAllOfThem) {
				if (contExitos>=5) {
					return sum;
				}
			}
			return 0.0;
		}
		return 0.0;
	}

	private void loadPhase3Results(String pathTrainingSet) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("phase3_logFile.txt"));
		try {
			int contador=0;
			while(br.ready()){
				String line;
				try {
					line = br.readLine();
					StringTokenizer st=new StringTokenizer(line,":");
					String tok="";
					while(st.hasMoreTokens()){
						tok=st.nextToken();
					}
					contador++;
					if (contador==1) {
						if (pathTrainingSet.equals(tok)) {
							this.setTrainingCorpus(tok);
							//System.out.println(tok);
						}
						else {
							try {
								throw new PathNotFoundException();
							} catch (PathNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					if (contador==2) {
						Integer nt=Integer.parseInt(tok);
						this.setNumbTokens(nt);
						//System.out.println(nt);
					}
					if (contador==3) {
						Integer nt=Integer.parseInt(tok);
						this.setNumbSentences(nt);
						//System.out.println(nt);
					}
					if (contador==4) {
						Double nt=Double.parseDouble(tok);
						this.setPercentage(nt);
						//System.out.println(nt);
						if (nt==0.0){
							this.setNoNonProjective(true);
						}
						else {
							if (nt>15) {
								this.setSubstantialNonProjective(true);
							}
							else {
								this.setSmallCaseBothThings(true);
							}
						}
						
						
					}
					if (contador==5) {
						Integer it=Integer.parseInt(tok);
						if (it>0) this.setDanglingPunctuation(true);
						this.setNumbDanglingCases(it);
						//System.out.println(it);
					}
					if (contador==6) {
						Double nt=Double.parseDouble(tok);
						this.setBestResult(nt);
						//System.out.println(nt);
					}
					if (contador==7) {
						Double nt=Double.parseDouble(tok);
						this.setDefaultBaseline(nt);
						//System.out.println(nt);
					}
					if (contador==8) {
						Integer nt=Integer.parseInt(tok);
						this.numRootLabels=nt;
						//System.out.println(nt);
					}
					if (contador==9) {
						this.javaHeapValue=tok;
						//System.out.println(nt);
					}
					
					if (contador==10) {
						if (tok.equals("true"))
							cposEqPos=true;
						else
							cposEqPos=false;
						//System.out.println(nt);
					}
					if (contador==11) {
						if (tok.equals("true"))
							lemmaBlank=true;
						else
							lemmaBlank=false;
					}
					if (contador==12) {
						if (tok.equals("true"))
							featsBlank=true;
						else
							featsBlank=false;
						//System.out.println(nt);
					}
					
					
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		BufferedReader brOpt;
		try {
			brOpt = new BufferedReader(new FileReader("phase3_optFile.txt"));
		try {
			int contador=0;
			boolean grl=false;
			boolean pcr=false;
			boolean algo=false;
			boolean pp=false;
			boolean cs=false;
			boolean cr=false;
			boolean menosr=false;
			boolean feature=false;
			while(brOpt.ready()){
				String line;
				try {
					line = brOpt.readLine();
					StringTokenizer st=new StringTokenizer(line,":");
					grl=false;
					pcr=false;
					algo=false;
					pp=false;
					cr=false;
					cs=false;
					menosr=false;
					feature=false;
					if (line.contains("-grl")) grl=true;
					if (line.contains("-pcr")) pcr=true;
					if (line.contains("-a")) algo=true;
					if (line.contains("-pp")) pp=true;
					if (line.contains("-cs")) cs=true;
					if (line.contains("-cr")) cr=true;
					if (line.contains("-r")) menosr=true;
					if (line.contains("-F")) feature=true;
					String tok="";
					while(st.hasMoreTokens()){
						tok=st.nextToken();
					}
					contador++;

					
					if (grl) {
						this.setOptionGRL(tok);
						//System.out.println(line);
						grl=false;
					}
					if (pcr) {
						this.setPcrOption(tok);
						//System.out.println(line);
						pcr=false;
					}
					if (algo) {
						this.setBestAlgorithm(tok);
						//System.out.println(line);
						algo=false;
					}
					if (pp) {
						this.setPpOption(tok);
						//System.out.println(line);
						pp=false;
					}
					if (cs) {
						allow_shift=true;
						//System.out.println(line);
						cs=false;
					}
					if (cr) {
						allow_root=true;
						//System.out.println(line);
						cr=false;
					}
					if (menosr) {
						optionMenosR=tok;
						//System.out.println(line);
						menosr=false;
					}
					if (feature){
						featureModel=tok;
						feature=false;
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
					
	}
	

	private void runPhase4() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
            
		System.out.println("PHASE 4: LIBRARY CONFIGURATION PARAMETERS TESTING");
		//System.out.println(this.featureModel);
		System.out.println("MaltOptimizer is going to test the best training library configuration:\n");
		/**
		 * 
		 */
		int cParameter=1;
		LibraryOptionsSetter lo=LibraryOptionsSetter.getSingleton();
		lo.setLibraryOptions("-s_4_-c_0.01");
		while (cParameter<10) {
			cParameter++;
			String anterior=lo.getLibraryOptions();
			
			System.out.println("Testing the: "+lo.getLibraryOptions());
			//Double d=cParameter;
			
			//System.out.println(cParameter);

			//System.out.println(cParameter);
			double result=runBestAlgorithm(featureModel);
			System.out.println(result);
			System.out.println("best:"+bestResult);
			//
			double difference=0.0;
			if (result>(this.bestResult)) { 
				this.libraryValue=lo.getLibraryOptions();
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best Library Configuration Parameter: "+libraryValue);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			if (lo.getLibraryOptions().equals("-s_4_-c_0.01"))
				lo.setLibraryOptions("-s_4_-c_0.1");
			else lo.incrementC(0.1);
			
		}
		
BufferedWriter bwPhase3HiddenLogFile;
		
		try {
			bwPhase3HiddenLogFile = new BufferedWriter(new FileWriter("phase4_logFile.txt"));
			bwPhase3HiddenLogFile.write("Training set path:"+trainingCorpus+"\n");
			bwPhase3HiddenLogFile.write("Size (tokens):"+getNumbTokens()+"\n");
			bwPhase3HiddenLogFile.write("Size (sentences):"+getNumbSentences()+"\n");
			bwPhase3HiddenLogFile.write("Non projective:"+percentage+"\n");
			bwPhase3HiddenLogFile.write("Dangling Punctuation:"+numbDanglingCases+"\n");
			bwPhase3HiddenLogFile.write("LAS:"+bestResult+"\n");
			bwPhase3HiddenLogFile.write("Default:"+defaultBaseline+"\n");
			bwPhase3HiddenLogFile.write("NumRootLabels:"+numRootLabels+"\n");
			//bwPhase2HiddenLogFile.write("BestAlgorithm:"+bestAlgorithm+"\n");
			bwPhase3HiddenLogFile.write("JavaHeap:"+javaHeapValue+"\n");
			bwPhase3HiddenLogFile.write("MaxTokens:"+nMaxTokens+"\n");
			bwPhase3HiddenLogFile.write("CposEqPos:"+cposEqPos+"\n");
			bwPhase3HiddenLogFile.write("LemmaBlank:"+lemmaBlank+"\n");
			bwPhase3HiddenLogFile.write("FeatsBlank:"+featsBlank+"\n");
			if (bestAlgorithm.contains("nivre")) {
				bwPhase3HiddenLogFile.write("allow_root:"+Optimizer.allow_rootNiv+"\n");
				bwPhase3HiddenLogFile.write("allow_reduce:"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3HiddenLogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		BufferedWriter bwPhase3LogFile;
		
		try {
			bwPhase3LogFile = new BufferedWriter(new FileWriter("phase4_optFile.txt"));
			bwPhase3LogFile.write("1. root_label (-grl):"+optionGRL+"\n");
			bwPhase3LogFile.write("2. covered_root (-pcr):"+pcrOption+"\n");
			bwPhase3LogFile.write("3. parsing_algorithm (-a):"+bestAlgorithm+"\n");
			if (usePPOption) {
				bwPhase3LogFile.write("4. marking_strategy (-pp):"+ppOption+"\n");
			}
			if (bestAlgorithm.contains("cov")){
				bwPhase3LogFile.write("5. allow_shift (-cs):"+allow_shift+"\n");
				bwPhase3LogFile.write("6. allow_root (-cr):"+allow_root+"\n");
			}
			if (bestAlgorithm.contains("nivre")){
				bwPhase3LogFile.write("7. allow_root (-nr):"+Optimizer.allow_rootNiv+"\n");
				bwPhase3LogFile.write("8. allow_reduce (-ne):"+Optimizer.allow_reduceNiv+"\n");
			}
			bwPhase3LogFile.write("8. feature_model (-F):"+this.featureModel+"\n");
			bwPhase3LogFile.write("9. library (-lo):"+this.libraryValue+"\n");
			bwPhase3LogFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		System.out.println("\nThe best Library configuration is: "+libraryValue);
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the library parameter configuration phase using your training set,");
		System.out.println("it saved the results for future use in /phase4_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in /phase4_optFile.txt. ");
		System.out.println("");
	}
	
	
	private void runPhase4SimplifiedVersion() {
		// TODO Auto-generated method stub
		/*System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");*/
            
		//System.out.println("PHASE 4: LIBRARY CONFIGURATION PARAMETERS TESTING");
		//System.out.println(this.featureModel);
		//System.out.println("MaltOptimizer is going to test the best training library configuration:\n");
		/**
		 * 
		 */
		int cParameter=1;
		LibraryOptionsSetter lo=LibraryOptionsSetter.getSingleton();
		lo.setLibraryOptions("-s_4_-c_0.01");
		while (cParameter<6) {
			cParameter++;
			String anterior=lo.getLibraryOptions();
			
			System.out.println("Testing: C="+lo.getC());
			//Double d=cParameter;
			
			//System.out.println(cParameter);

			//System.out.println(cParameter);
			double result=runBestAlgorithm(featureModel);
			System.out.print(result);
			System.out.println("(Best:"+bestResult+")");
			//
			double difference=0.0;
			if (result>(this.bestResult)) { 
				this.libraryValue=lo.getLibraryOptions();
				difference=result-bestResult;
				bestResult=result;
				String sDifferenceLabel=""+difference;
				if (sDifferenceLabel.length()>5)
					sDifferenceLabel=sDifferenceLabel.substring(0, 5);
				System.out.println("New best Library Configuration Parameter: "+libraryValue);
				String s=""+this.bestResult;
			if (s.length()==4) s+="0";
			
			System.out.println("Incremental "+evaluationMeasure+" improvement: + "+sDifferenceLabel+"% ("+s+"%)");
			
			}
			if (lo.getLibraryOptions().equals("-s_4_-c_0.01"))
				lo.setLibraryOptions("-s_4_-c_0.2");
			else lo.incrementC(0.2);
			
		}
		lo.setLibraryOptions(this.libraryValue);		
		System.out.println("\nBest C value: "+lo.getC());
		/*System.out.println("-----------------------------------------------------------------------------");
		System.out.println("MaltOptimizer has completed the library parameter configuration phase using your training set,");
		System.out.println("it saved the results for future use in /phase4_logFile.txt. Updated MaltParser "); 
		System.out.println("options can be found in /phase4_optFile.txt. ");
		System.out.println("");*/
	}
	
	
	
	
	

	public ArrayList<String> getThreeFrequent(HashMap<String,Double> roots) {
		Set<String> set=roots.keySet();
		String max="";
		Double maxD=0.0;
		String sec="";
		Double secD=0.0;
		String third="";
		Double thirdD=0.0;
		
		Iterator<String> it=set.iterator();
		while(it.hasNext()){
			String r=it.next();
			Double d=roots.get(r)*100;
			if (d>maxD) {
				if (maxD>secD) {
					third=sec;
					thirdD=secD;
					sec=max;
					secD=maxD;
				}
				max=r;
				maxD=d;
			}
			else {
				if (d>secD) {
					if (secD>thirdD) {
						third=sec;
						thirdD=secD;
					}
					sec=r;
					secD=d;
				}
				else {
					if (d>thirdD) {
						third=r;
						thirdD=d;
					}
				}
			}
		}
		ArrayList<String> out=new ArrayList<String>();
		out.add(max);
		out.add(sec);
		if (third!=null && !third.equals(""))
			out.add(third);
		return out;
	}
	

	



	private void showDefault() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("Usage:");
		System.out.println("java -jar MaltOptimizer.jar -p <phase number> -m <path to MaltParser> -c <path to training corpus> [-v <validation method>]");
		System.out.println("java -jar malt.jar -h for more help and options");
	}

	private void showHelp() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                   MaltOptimizer 1.0");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("         Miguel Ballesteros* and Joakim Nivre**\n");
		System.out.println("          *Complutense University of Madrid (Spain)  ");
		System.out.println("                **Uppsala University (Sweden)   ");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("Usage:");
		System.out.println("java -jar MaltOptimizer.jar -p <int> -m <path to MaltParser> -c <path to training corpus> [-v <validation method>]");
		System.out.println("java -jar malt.jar -h for more help and options");
		
		System.out.println("-----------------------------------------------------------------------------\n");
		System.out.println("PHASE 1: DATA CHARACTERISTICS");

		System.out.println("In the data analysis, MaltOptimizer gather information about the following properties of the training set:");
		System.out.println("Number of words/sentences");
		System.out.println("Percentage of non-projective arcs/trees");
		System.out.println("Existence of ''covered roots'' (arcs spanning tokens with HEAD = 0)");
		System.out.println("Frequency of labels used for tokens with HEAD = 0");
		System.out.println("Existence of non-empty feature values in the LEMMA and FEATS columns");
		System.out.println("Identity (or not) of feature values in the CPOSTAG and POSTAG columns\n");

		System.out.println("Usage:");
		System.out.println("java -jar MaltOptimizer.jar -p 1 -m <-MaltParser jar path-> -c <path to training corpus> [-v <validation method>]");

		System.out.println("-----------------------------------------------------------------------------\n");
		System.out.println("PHASE 2: PARSING ALGORITHM SELECTION");
		System.out.println("MaltOptimizer selects the best algorithm implemented in MaltParser for the input training set.\n");

		System.out.println("Usage:");
		System.out.println("java -jar MaltOptimizer.jar -p 2 -m <-MaltParser jar path-> -c <path to training corpus> [-v <validation method>]");

		System.out.println("------------------------------------------------------------------------------\n");

		System.out.println("PHASE 3: FEATURE SELECTION");

		System.out.println("MaltOptimizer tests the following feature selection experiments:");
		System.out.println("1. Tune the window of POSTAG n-grams over the parser state");
		System.out.println("2. Tune the window of FORM features over the parser state");
		System.out.println("3. Tune DEPREL and POSTAG features over the partially built dependency tree");
		System.out.println("4. Add POSTAG and FORM features over the input string");
		System.out.println("5. Add CPOSTAG, FEATS, and LEMMA features if available");
		System.out.println("6. Add conjunctions of POSTAG and FORM features\n");

		System.out.println("Usage:");
		System.out.println("java -jar MaltOptimizer.jar -p 3 -m <-MaltParser jar path-> -c <path to training corpus> [-v <validation method>]");

		System.out.println("--------------------------------------------------------------------------------\n");

		System.out.println("EXTRA OPTIONS ");

		System.out.println("evaluation_measure	 -e	las	Labeled Attachment Score (DEFAULT)");
		System.out.println("\t\tuas	Unlabeled Attachment Score");
		System.out.println("\t\tlcm	Labeled Complete Match");
		System.out.println("\t\tucm	Unlabeled Complete Match\n");

		System.out.println("Usage:");
		System.out.println("java -jar MaltOptimizer.jar -p <-phase number-> -m <-MaltParser jar path-> -c <path to training corpus> [-v <validation method>] -e uas");

		System.out.println("punctuation_symbols	 -s	true	Include punctuaton symbols (DEFAULT)");
		System.out.println("\t\tfalse	Exclude punctuation symbols");

		System.out.println("Usage:");
		System.out.println("java -jar MaltOptimizer.jar -p <-phase number-> -m <-MaltParser jar path-> -c <path to training corpus> [-v <validation method>] -s false");
	}
	
	private void setMalt(String malt) {
		// TODO Auto-generated method stub
		maltPath=malt;
	}
	
	public static String getMaltPath() {
		return maltPath;
	}

	public static void setMaltPath(String maltPath) {
		Optimizer.maltPath = maltPath;
	}

	public String getTrainingCorpus() {
		return trainingCorpus;
	}

	public void setTrainingCorpus(String trainingCorpus) {
		this.trainingCorpus = trainingCorpus;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isProjective() {
		return projective;
	}

	public void setProjective(boolean projective) {
		this.projective = projective;
	}

	public boolean isStrictRoot() {
		return strictRoot;
	}

	public void setStrictRoot(boolean strictRoot) {
		this.strictRoot = strictRoot;
	}

	public boolean isCoveredRoots() {
		return coveredRoots;
	}

	public void setCoveredRoots(boolean coveredRoots) {
		this.coveredRoots = coveredRoots;
	}

	public boolean isCoveredRootsWithoutChildren() {
		return coveredRootsWithoutChildren;
	}

	public void setCoveredRootsWithoutChildren(boolean coveredRootsWithoutChildren) {
		this.coveredRootsWithoutChildren = coveredRootsWithoutChildren;
	}

	public String getBestAlgorithm() {
		return bestAlgorithm;
	}

	public void setBestAlgorithm(String bestAlgorithm) {
		this.bestAlgorithm = bestAlgorithm;
	}

	public boolean isRootGRL() {
		return rootGRL;
	}

	public void setRootGRL(boolean rootGRL) {
		this.rootGRL = rootGRL;
	}

	public boolean isDanglingPunctuation() {
		return danglingPunctuation;
	}

	public void setDanglingPunctuation(boolean danglingPunctuation) {
		this.danglingPunctuation = danglingPunctuation;
	}

	public static String getPcrOption() {
		return pcrOption;
	}

	public static void setPcrOption(String pcrOption) {
		Optimizer.pcrOption = pcrOption;
	}

	public boolean isPcr() {
		return pcr;
	}

	public void setPcr(boolean pcr) {
		this.pcr = pcr;
	}

	public static String getOptionGRL() {
		return optionGRL;
	}

	public static void setOptionGRL(String optionGRL) {
		Optimizer.optionGRL = optionGRL;
	}

	public static String getOptionMenosR() {
		return optionMenosR;
	}

	public static void setOptionMenosR(String optionMenosR) {
		Optimizer.optionMenosR = optionMenosR;
	}

	public static Double getBestResult() {
		return bestResult;
	}

	public static void setBestResult(Double bestResult) {
		Optimizer.bestResult = bestResult;
	}


	public static void main(String[] args) {
		
		Optimizer d=new Optimizer();
		String phase="";
		String maltPath="";
		String pathTrainingSet="";
		if (args.length==0) {
			d.showDefault();
		}
		else {
			if (args[0].equals("-h")) {
				d.showHelp();
			}
			else {
				//java -jar MaltOptimizer.jar -p <int> -m <path to MaltParser> -c <path to training corpus> [-v <validation method>]
				if (args[0].equals("-p")&& (args.length>5)) {
					phase= args[1];
					if (args[2].equals("-m")) {
						maltPath=args[3];
						if (args[4].equals("-c")) {
							pathTrainingSet=args[5];
							//System.out.println(pathTrainingSet);
							if (phase.equals("1")) {
								///////////////////////
								d.setMalt(maltPath);
								d.setCorpus(pathTrainingSet);
								if (args.length>=8) { 
									if (args[6]!=null) {
										if (args[6].equals("-s")) {
											if (args[7].equals("true")) {
												Optimizer.includePunctuation=true;
											}
											else {
												Optimizer.includePunctuation=false;
											}
										}
									}
									if (args[6]!=null) {
										if (args[6].equals("-e")) {
											if (args[7].equals("las")) {
												Optimizer.evaluationMeasure="LAS";
											}
											if (args[7].equals("uas")) {
												Optimizer.evaluationMeasure="UAS";
											}
											if (args[7].equals("lcm")) {
												Optimizer.evaluationMeasure="LCM";
											}
											if (args[7].equals("ucm")) {
												Optimizer.evaluationMeasure="UCM";
											}
										}
									}
									
									if (args[6]!=null) {
										if (args[6].equals("-t")) {
											Optimizer.testCorpus=args[7];
										}
									}
									
									if (args.length>=10) {
									if (args[8]!=null) {
										if (args[8].equals("-s")) {
											if (args[9].equals("true")) {
												Optimizer.includePunctuation=true;
											}
											else {
												Optimizer.includePunctuation=false;
											}
										}
									}
									if (args[8]!=null) {
										if (args[8].equals("-e")) {
											if (args[9].equals("las")) {
												Optimizer.evaluationMeasure="LAS";
											}
											else if (args[9].equals("uas")) {
												Optimizer.evaluationMeasure="UAS";
											}
											else if (args[9].equals("lcm")) {
												Optimizer.evaluationMeasure="LCM";
											}
											else if (args[9].equals("ucm")) {
												Optimizer.evaluationMeasure="UCM";
											}
											else d.showDefault();
										}
									}
									
									if (args[8]!=null) {
										if (args[8].equals("-t")) {
											Optimizer.testCorpus=args[9];
										}
									}
									
									
									if (args.length>=11) {
									if (args[10]!=null) {
										if (args[19].equals("-s")) {
											if (args[11].equals("true")) {
												Optimizer.includePunctuation=true;
											}
											else {
												Optimizer.includePunctuation=false;
											}
										}
									}
									if (args[10]!=null) {
										if (args[10].equals("-e")) {
											if (args[11].equals("las")) {
												Optimizer.evaluationMeasure="LAS";
											}
											else if (args[11].equals("uas")) {
												Optimizer.evaluationMeasure="UAS";
											}
											else if (args[11].equals("lcm")) {
												Optimizer.evaluationMeasure="LCM";
											}
											else if (args[11].equals("ucm")) {
												Optimizer.evaluationMeasure="UCM";
											}
											else d.showDefault();
										}
									}
									
									if (args[10]!=null) {
										if (args[10].equals("-t")) {
											Optimizer.testCorpus=args[11];
										}
									}
									
									
									}//>=7
									}//>=9
									}//>=11
								d.runPhase1();
								
								//////////////////////
								
							}
							else if (phase.equals("2")) {
								
								//////////////////////
								d.setMalt(maltPath);
								d.loadPhase1Results(pathTrainingSet);
								d.setCorpus(pathTrainingSet);
								if (args.length>=7) { 
									if (args[6]!=null) {
										if (args[6].equals("-s")) {
											if (args[7].equals("true")) {
												Optimizer.includePunctuation=true;
											}
											else {
												Optimizer.includePunctuation=false;
											}
										}
									}
									if (args[6]!=null) {
										if (args[6].equals("-e")) {
											if (args[7].equals("las")) {
												Optimizer.evaluationMeasure="LAS";
											}
											if (args[7].equals("uas")) {
												Optimizer.evaluationMeasure="UAS";
											}
											if (args[7].equals("lcm")) {
												Optimizer.evaluationMeasure="LCM";
											}
											if (args[7].equals("ucm")) {
												Optimizer.evaluationMeasure="UCM";
											}
										}
									}
									if (args[6]!=null) {
										if (args[6].equals("-t")) {
											Optimizer.testCorpus=args[7];
										}
									}
									
									if (args[6]!=null) {
										if (args[6].equals("-v")) {
											if (args[7].equals("cv")) {
												Optimizer.chooseAverage=true;
												Optimizer.crossValidation=true;
											}
										}
									}
									
									if (args.length>=9) {
									if (args[8]!=null) {
										if (args[8].equals("-s")) {
											if (args[9].equals("true")) {
												Optimizer.includePunctuation=true;
											}
											else {
												Optimizer.includePunctuation=false;
											}
										}
									}
									if (args[8]!=null) {
										if (args[8].equals("-e")) {
											if (args[9].equals("las")) {
												Optimizer.evaluationMeasure="LAS";
											}
											else if (args[9].equals("uas")) {
												Optimizer.evaluationMeasure="UAS";
											}
											else if (args[9].equals("lcm")) {
												Optimizer.evaluationMeasure="LCM";
											}
											else if (args[9].equals("ucm")) {
												Optimizer.evaluationMeasure="UCM";
											}
											else d.showDefault();
										}
									}
									if (args[8]!=null) {
										if (args[8].equals("-t")) {
											Optimizer.testCorpus=args[9];
										}
									}
									
									if (args[8]!=null) {
										if (args[8].equals("-v")) {
											if (args[9].equals("cv")) {
												Optimizer.chooseAverage=true;
												Optimizer.crossValidation=true;
											}
										}
									}
									
									
									if (args.length>=11) {
									if (args[10]!=null) {
										if (args[10].equals("-s")) {
											if (args[11].equals("true")) {
												Optimizer.includePunctuation=true;
											}
											else {
												Optimizer.includePunctuation=false;
											}
										}
									}
									if (args[10]!=null) {
										if (args[10].equals("-e")) {
											if (args[11].equals("las")) {
												Optimizer.evaluationMeasure="LAS";
											}
											else if (args[11].equals("uas")) {
												Optimizer.evaluationMeasure="UAS";
											}
											else if (args[11].equals("lcm")) {
												Optimizer.evaluationMeasure="LCM";
											}
											else if (args[11].equals("ucm")) {
												Optimizer.evaluationMeasure="UCM";
											}
											else d.showDefault();
										}
									}
									if (args[10]!=null) {
										if (args[11].equals("-t")) {
											Optimizer.testCorpus=args[13];
										}
									}
									if (args[10]!=null) {
										if (args[10].equals("-v")) {
											if (args[11].equals("cv")) {
												Optimizer.chooseAverage=true;
												Optimizer.crossValidation=true;
											}
										}
									}
									
									if (args[10]!=null) {
										if (args[10].equals("-e")) {
											if (args[11].equals("las")) {
												Optimizer.evaluationMeasure="LAS";
											}
											else if (args[11].equals("uas")) {
												Optimizer.evaluationMeasure="UAS";
											}
											else if (args[11].equals("lcm")) {
												Optimizer.evaluationMeasure="LCM";
											}
											else if (args[11].equals("ucm")) {
												Optimizer.evaluationMeasure="UCM";
											}
											else d.showDefault();
										}
									}
									if (args[10]!=null) {
										if (args[10].equals("-t")) {
											Optimizer.testCorpus=args[11];
										}
									}
									if (args[10]!=null) {
										if (args[10].equals("-v")) {
											if (args[11].equals("cv")) {
												Optimizer.chooseAverage=true;
												Optimizer.crossValidation=true;
											}
										}
									}
									
									}//>=7
									}//>=9
									}//>=11
									//}//>=13
								if (Optimizer.crossValidation) {
									d.runPhase25Fold();
								}
								else d.runPhase2();
								//////////////////////
							}
							else if (phase.equals("3")) {
								d.setMalt(maltPath);
								d.loadPhase2Results(pathTrainingSet);
								d.setCorpus(pathTrainingSet);
								//d.runPhase3();
								if (args.length>=8) { 
								if (args[6]!=null) {
									if (args[6].equals("-s")) {
										if (args[7].equals("true")) {
											Optimizer.includePunctuation=true;
										}
										else {
											Optimizer.includePunctuation=false;
										}
									}
								}
								if (args[6]!=null) {
									if (args[6].equals("-e")) {
										if (args[7].equals("las")) {
											Optimizer.evaluationMeasure="LAS";
										}
										if (args[7].equals("uas")) {
											Optimizer.evaluationMeasure="UAS";
										}
										if (args[7].equals("lcm")) {
											Optimizer.evaluationMeasure="LCM";
										}
										if (args[7].equals("ucm")) {
											Optimizer.evaluationMeasure="UCM";
										}
									}
								}
								/*if (args[6]!=null) {
									if (args[6].equals("-o")) {
										String s=args[7];
										int ord=Integer.parseInt(s);
										if (ord<7)
											Optimizer.order=ord;
										else d.showDefault();
									}
								}*/
								if (args[6]!=null) {
									if (args[6].equals("-a")) { //FEATURE ALGORITHM
										String s=args[7];
										if (s.equals("Greedy")){
											Optimizer.featureAlgorithm=s;
										}
										else {
											if (s.equals("RelaxedGreedy")){
												Optimizer.featureAlgorithm=s;
											}
											else {
												if (s.equals("BruteForce")){
													Optimizer.featureAlgorithm=s;
												}
												else if (s.equals("OnlyBackward")) {
													Optimizer.featureAlgorithm=s;
												}
												else if (s.equals("OnlyForward")) {
													Optimizer.featureAlgorithm=s;
												}
												else if (s.contains("5Fold")) {
													Optimizer.featureAlgorithm=s;
												}
												else {
													Optimizer.featureAlgorithm="Greedy";
												}
											}
										}
										
									}
								}
								if (args[6]!=null) {
									if (args[6].equals("-t")) {
										Optimizer.testCorpus=args[7];
									}
								}
								
								if (args[6]!=null) {
									if (args[6].equals("-v")) {
										if (args[7].equals("cv")) {
											//System.out.println("hoola");
											Optimizer.chooseAverage=true;
											Optimizer.crossValidation=true;
										}
									}
								}
								
								if (args.length>=10) {
								if (args[8]!=null) {
									if (args[8].equals("-s")) {
										if (args[9].equals("true")) {
											Optimizer.includePunctuation=true;
										}
										else {
											Optimizer.includePunctuation=false;
										}
									}
								}
								if (args[8]!=null) {
									if (args[8].equals("-e")) {
										if (args[9].equals("las")) {
											Optimizer.evaluationMeasure="LAS";
										}
										else if (args[9].equals("uas")) {
											Optimizer.evaluationMeasure="UAS";
										}
										else if (args[9].equals("lcm")) {
											Optimizer.evaluationMeasure="LCM";
										}
										else if (args[9].equals("ucm")) {
											Optimizer.evaluationMeasure="UCM";
										}
										else d.showDefault();
									}
								}
								
								/*if (args[8]!=null) {
									if (args[8].equals("-o")) {
										String s=args[9];
										int ord=Integer.parseInt(s);
										if (ord<7)
											Optimizer.order=ord;
										else d.showDefault();
									}
								}*/
								if (args[8]!=null) {
									if (args[8].equals("-a")) { //FEATURE ALGORITHM
										String s=args[9];
										if (s.equals("Greedy")){
											Optimizer.featureAlgorithm=s;
										}
										else {
											if (s.equals("RelaxedGreedy")){
												Optimizer.featureAlgorithm=s;
											}
											else {
												if (s.equals("BruteForce")){
													Optimizer.featureAlgorithm=s;
												}
												else if (s.equals("OnlyBackward")) {
													Optimizer.featureAlgorithm=s;
												}
												else if (s.equals("OnlyForward")) {
													Optimizer.featureAlgorithm=s;
												}
												else if (s.contains("5Fold")) {
													Optimizer.featureAlgorithm=s;
												}
												else {
													Optimizer.featureAlgorithm="Greedy";
												}
											}
										}
										
									}
								}
								if (args[8]!=null) {
									if (args[8].equals("-t")) {
										Optimizer.testCorpus=args[9];
									}
								}
								if (args[8]!=null) {
									if (args[8].equals("-v")) {
										if (args[9].equals("cv")) {
											Optimizer.chooseAverage=true;
											Optimizer.crossValidation=true;
										}
									}
								}
								
								if (args.length>=11) {
								if (args[10]!=null) {
									if (args[10].equals("-s")) {
										if (args[11].equals("true")) {
											Optimizer.includePunctuation=true;
										}
										else {
											Optimizer.includePunctuation=false;
										}
									}
								}
								if (args[10]!=null) {
									if (args[10].equals("-e")) {
										if (args[11].equals("las")) {
											Optimizer.evaluationMeasure="LAS";
										}
										else if (args[11].equals("uas")) {
											Optimizer.evaluationMeasure="UAS";
										}
										else if (args[11].equals("lcm")) {
											Optimizer.evaluationMeasure="LCM";
										}
										else if (args[11].equals("ucm")) {
											Optimizer.evaluationMeasure="UCM";
										}
										else d.showDefault();
									}
								}
								
								/*if (args[10]!=null) {
									if (args[10].equals("-o")) {
										String s=args[11];
										int ord=Integer.parseInt(s);
										if (ord<7)
											Optimizer.order=ord;
										else d.showDefault();
									}
								}*/
								if (args[10]!=null) {
									if (args[10].equals("-a")) { //FEATURE ALGORITHM
										String s=args[11];
										if (s.equals("Greedy")){
											Optimizer.featureAlgorithm=s;
										}
										else {
											if (s.equals("RelaxedGreedy")){
												Optimizer.featureAlgorithm=s;
											}
											else {
												if (s.equals("BruteForce")){
													Optimizer.featureAlgorithm=s;
												}
												else if (s.equals("OnlyBackward")) {
													Optimizer.featureAlgorithm=s;
												}
												else if (s.equals("OnlyForward")) {
													Optimizer.featureAlgorithm=s;
												}
												else if (s.contains("5Fold")) {
													Optimizer.featureAlgorithm=s;
												}
												else {
													Optimizer.featureAlgorithm="Greedy";
												}
											}
										}
										
									}
								}
								if (args[10]!=null) {
									if (args[10].equals("-t")) {
										Optimizer.testCorpus=args[11];
									}
								}
								
								if (args[10]!=null) {
									if (args[10].equals("-v")) {
										if (args[11].equals("cv")) {
											Optimizer.chooseAverage=true;
											Optimizer.crossValidation=true;
										}
									}
								}
								
								if (args.length>=13) {
									if (args[12]!=null) {
										if (args[12].equals("-s")) {
											if (args[13].equals("true")) {
												Optimizer.includePunctuation=true;
											}
											else {
												Optimizer.includePunctuation=false;
											}
										}
									}
									if (args[12]!=null) {
										if (args[12].equals("-e")) {
											if (args[13].equals("las")) {
												Optimizer.evaluationMeasure="LAS";
											}
											else if (args[13].equals("uas")) {
												Optimizer.evaluationMeasure="UAS";
											}
											else if (args[13].equals("lcm")) {
												Optimizer.evaluationMeasure="LCM";
											}
											else if (args[13].equals("ucm")) {
												Optimizer.evaluationMeasure="UCM";
											}
											else d.showDefault();
										}
									}
									
									/*if (args[12]!=null) {
										if (args[12].equals("-o")) {
											String s=args[13];
											int ord=Integer.parseInt(s);
											if (ord<7)
												Optimizer.order=ord;
											else d.showDefault();
										}
									}*/
									if (args[12]!=null) {
										if (args[12].equals("-a")) { //FEATURE ALGORITHM
											String s=args[13];
											if (s.equals("Greedy")){
												Optimizer.featureAlgorithm=s;
											}
											else {
												if (s.equals("RelaxedGreedy")){
													Optimizer.featureAlgorithm=s;
												}
												else {
													if (s.equals("BruteForce")){
														Optimizer.featureAlgorithm=s;
													}
													else if (s.equals("OnlyBackward")) {
														Optimizer.featureAlgorithm=s;
													}
													else if (s.equals("OnlyForward")) {
														Optimizer.featureAlgorithm=s;
													}
													else if (s.contains("5Fold")) {
														Optimizer.featureAlgorithm=s;
													}
													else {
														Optimizer.featureAlgorithm="Greedy";
													}
												}
											}
											
										}
									}
									if (args[12]!=null) {
										if (args[12].equals("-t")) {
											Optimizer.testCorpus=args[13];
										}
									}
									
									if (args[12]!=null) {
										if (args[12].equals("-v")) {
											if (args[13].equals("cv")) {
												Optimizer.chooseAverage=true;
												Optimizer.crossValidation=true;
											}
										}
									}
									if (args.length>=15) {
										if (args[14]!=null) {
											if (args[14].equals("-s")) {
												if (args[15].equals("true")) {
													Optimizer.includePunctuation=true;
												}
												else {
													Optimizer.includePunctuation=false;
												}
											}
										}
										if (args[14]!=null) {
											if (args[14].equals("-e")) {
												if (args[15].equals("las")) {
													Optimizer.evaluationMeasure="LAS";
												}
												else if (args[15].equals("uas")) {
													Optimizer.evaluationMeasure="UAS";
												}
												else if (args[15].equals("lcm")) {
													Optimizer.evaluationMeasure="LCM";
												}
												else if (args[15].equals("ucm")) {
													Optimizer.evaluationMeasure="UCM";
												}
												else d.showDefault();
											}
										}
										
										/*if (args[14]!=null) {
											if (args[14].equals("-o")) {
												String s=args[15];
												int ord=Integer.parseInt(s);
												if (ord<7)
													Optimizer.order=ord;
												else d.showDefault();
											}
										}*/
										if (args[14]!=null) {
											if (args[14].equals("-a")) { //FEATURE ALGORITHM
												String s=args[15];
												if (s.equals("Greedy")){
													Optimizer.featureAlgorithm=s;
												}
												else {
													if (s.equals("RelaxedGreedy")){
														Optimizer.featureAlgorithm=s;
													}
													else {
														if (s.equals("BruteForce")){
															Optimizer.featureAlgorithm=s;
														}
														else if (s.equals("OnlyBackward")) {
															Optimizer.featureAlgorithm=s;
														}
														else if (s.equals("OnlyForward")) {
															Optimizer.featureAlgorithm=s;
														}
														else if (s.contains("5Fold")) {
															Optimizer.featureAlgorithm=s;
														}
														else {
															Optimizer.featureAlgorithm="Greedy";
														}
													}
												}
												
											}
										}
										if (args[14]!=null) {
											if (args[14].equals("-t")) {
												Optimizer.testCorpus=args[15];
											}
										}
										
										if (args[14]!=null) {
											if (args[14].equals("-v")) {
												if (args[15].equals("cv")) {
													Optimizer.chooseAverage=true;
													Optimizer.crossValidation=true;
												}
											}
										}
								}//>=7
								}//>=9
								}//>=11
								}//>=13
								}//>=15
								
								/*if (Optimizer.order==0)
									d.runPhase3();
								else if (Optimizer.order==1)
									d.runPhase3AlternativeOrder1();
								else if (Optimizer.order==2)
									d.runPhase3AlternativeOrder2();
								else if (Optimizer.order==3)
									d.runPhase3AlternativeOrder3();
								else if (Optimizer.order==4)
									d.runPhase3AlternativeOrder4();
								else if (Optimizer.order==5)
									d.runPhase3AlternativeOrder5();
								else if (Optimizer.order==6)
									d.runPhase3AlternativeOrder6();
								else d.runPhase3();
								//////////////////////
								 * 
								 */
								if (Optimizer.featureAlgorithm.equals("Greedy")) {
									if (Optimizer.crossValidation==true) {
										Optimizer.pseudoRandomizeSelection=true;
										d.runPhase35Fold();
									}
									else 
										d.runPhase3(); //defatult is Greedy
								}
								else if (Optimizer.featureAlgorithm.equals("BruteForce"))
									d.runPhase3BruteForce();
								else if (Optimizer.featureAlgorithm.equals("RelaxedGreedy"))
									d.runPhase3RelaxedGreedy();
								else if (Optimizer.featureAlgorithm.equals("OnlyBackward"))
									d.runPhase3OnlyBackward("");
								else if (Optimizer.featureAlgorithm.equals("OnlyForward"))
									d.runPhase3OnlyForward("");
								else if (Optimizer.featureAlgorithm.equals("5FoldMajorityPS")){
									Optimizer.pseudoRandomizeSelection=true;
									Optimizer.chooseMajority=true;
									d.runPhase35Fold();
								}
								else if (Optimizer.featureAlgorithm.equals("5FoldAveragePS")){
									Optimizer.pseudoRandomizeSelection=true;
									Optimizer.chooseAverage=true;
									d.runPhase35Fold();
								}
								else if (Optimizer.featureAlgorithm.equals("5FoldAllPS")){
									Optimizer.pseudoRandomizeSelection=true;
									Optimizer.chooseAllOfThem=true;
									d.runPhase35Fold();
								}
								else if (Optimizer.featureAlgorithm.equals("5FoldMajorityNoPS")){
									Optimizer.pseudoRandomizeSelection=false;
									Optimizer.chooseMajority=true;
									d.runPhase35Fold();
								}
								else if (Optimizer.featureAlgorithm.equals("5FoldAverageNoPS")){
									Optimizer.pseudoRandomizeSelection=false;
									Optimizer.chooseAverage=true;
									d.runPhase35Fold();
								}
								else if (Optimizer.featureAlgorithm.equals("5FoldAllNoPS")){
									Optimizer.pseudoRandomizeSelection=false;
									Optimizer.chooseAllOfThem=true;
									d.runPhase35Fold();
								}
								/*else if (Optimizer.featureAlgorithm.equals("5FoldRelaxed"))
									d.runPhase35FoldRelaxed();*/
								else d.runPhase3();
								
							}
							/*else if (phase.equals("4")) {
								
								d.setMalt(maltPath);
								d.loadPhase3Results(pathTrainingSet);
								d.setCorpus(pathTrainingSet);
								d.runPhase4();
								//////////////////////
							}*/
						}
						else d.showDefault();
					}
					else {
						//System.out.println(args[2]);
						d.showDefault();
					}
				}
				else {
					d.showDefault();
				}
			}
		}
		//d.setCorpus("english_ptb_test.conll");
		//String corpus=args[0];
		//String corpus="czech_pdt_train.conll";
		//String corpus="english_ptb_train_prueba.conll";
		//String corpus="prueba.conll";
		//String corpus="spanish_cast3lb_train.conll";
		//String corpus="german_tiger_train.conll";
		//String corpus="czech_pdt_train.conll";
		//String corpus="small_czech_pdt_train.conll";
		//String corpus="slovene_sdt_train.conll";
		//String corpus="swedish_talbanken05_train.conll";
		//String corpus="basque_3lbBasque_train.conll";
		//String corpus="dutch_alpino_train.conll";
		//d.setCorpus(corpus);
		//d.run();
	}


	

}
