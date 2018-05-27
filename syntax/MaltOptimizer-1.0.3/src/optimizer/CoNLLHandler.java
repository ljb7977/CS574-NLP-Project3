package optimizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.StringTokenizer;

import algorithmTester.AlgorithmTester;

/**
 * 
 * @author Miguel Ballesteros
 *
 */
public class CoNLLHandler {
	BufferedReader br;
	String trainingCorpus;
	String head0;
	int numberOfTrees;
	boolean cposEqPos;
	boolean lemmaBlank;
	boolean featsBlank;
	String danglingFreq="";
	
	
	String fold1Test="";
	String fold2Test="";
	String fold3Test="";
	String fold4Test="";
	String fold5Test="";
	
	String fold1Train="";
	String fold2Train="";
	String fold3Train="";
	String fold4Train="";
	String fold5Train="";
	
	
	
	public String getDanglingFreq() {
		return danglingFreq;
	}

	public void setDanglingFreq(String danglingFreq) {
		this.danglingFreq = danglingFreq;
	}


	String messageDivision="";
	
	public String getMessageDivision() {
		return messageDivision;
	}

	public void setMessageDivision(String messageDivision) {
		this.messageDivision = messageDivision;
	}


	int numbSentences;
	public static int numSentences;
	public int getNumbSentences() {
		return numbSentences;
	}

	public void setNumbSentences(int numbSentences) {
		this.numbSentences = numbSentences;
	}

	public int getNumbTokens() {
		return numbTokens;
	}

	public void setNumbTokens(int numbTokens) {
		this.numbTokens = numbTokens;
	}


	public static int numbTokens;
	
	public boolean isFeatsBlank() {
		return featsBlank;
	}

	public HashMap<String, Double> getRootlabels() {
		return rootlabels;
	}

	public void setRootlabels(HashMap<String, Double> rootlabels) {
		this.rootlabels = rootlabels;
	}


	String training80;
	String testing20;
	HashMap<String,Double> rootlabels;
	
	TreeMap<String,String> tree;
	TreeMap<Integer,ArrayList<String>> invtree;

	public CoNLLHandler(String trainingCorpus) {
		// TODO Auto-generated constructor stub
		rootlabels=new HashMap<String,Double>();
		this.trainingCorpus=trainingCorpus;
		if (trainingCorpus.contains("/")) {
			StringTokenizer st=new StringTokenizer(trainingCorpus,"/");
			String relPath="";
			while (st.hasMoreTokens()){
				relPath=st.nextToken("/");
			}
			//System.out.println(relPath);
			training80=relPath.replaceAll(".conll","");
			testing20=relPath.replaceAll(".conll","");
			training80+="_train80.conll";
			testing20+="_test20.conll";
		}
		else {
			training80=trainingCorpus.replaceAll(".conll","");
			testing20=trainingCorpus.replaceAll(".conll","");
			training80+="_train80.conll";
			testing20+="_test20.conll";
		}
		 try {
			br = new BufferedReader(new FileReader(trainingCorpus));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getNumberOfTrees() {
		return numberOfTrees;
	}

	public void setNumberOfTrees(int numberOfTrees) {
		this.numberOfTrees = numberOfTrees;
	}

	public int getNumberOfSentences(){
		return numberOfTrees;
	}

	/**
	 * 
	 * @return A simple Text plain random sentence from the corpus.
	 */
	public String getSamplePlainText() {
		// TODO Auto-generated method stub
		Random r =new Random();
		int nrand = r.nextInt(15);
		//int nrand=0;
		boolean buscando=true;
		int i=0;
		try {
			br = new BufferedReader(new FileReader(trainingCorpus));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(buscando) {
			String line;
			try {
				line = br.readLine();
				if (line!=null && line.equals("")){
					i++;
					if (i==nrand) buscando=false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		boolean nofinfrase=true;
		String cat="";
		while(nofinfrase){
			String line;
			try {
				line = br.readLine();
				//System.out.println(line);
				StringTokenizer st=new StringTokenizer(line);
				if (st.hasMoreTokens())
					st.nextToken();
				if (st.hasMoreTokens())
					cat+=st.nextToken()+" ";
				if (line!=null && line.equals("")) {
					nofinfrase=false;
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.println(cat);
		return cat;
	}
	
	
	/**
	 * 
	 * @return true if the root labels are for the word ROOT, otherwise false.
	 *
	 */
	public boolean rootLabels(){
		int numberRoots=0;
		boolean diferentes=false;
		try {
			br = new BufferedReader(new FileReader(trainingCorpus));
			while(br.ready()){
				String line;
				try {
					line = br.readLine();
					//System.out.println(line);
						String head=getColumn(line,7);
						if (head.equals("0")){
							numberRoots++;
							String root=getColumn(line,8);
							Double d=rootlabels.get(root);
							if (d==null) d=new Double(0);
							d=d+1.0;
							rootlabels.put(root, d);
							head0=root;
							if (root.equals("ROOT"))
								diferentes=true;
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
		int numb=0;
		Set<String> set=rootlabels.keySet();
		Iterator<String> it=set.iterator();
		while(it.hasNext()){
			numb++;
			String s=it.next();
			Double nr=new Double(numberRoots);
			rootlabels.put(s, rootlabels.get(s)/nr);
		}
		if (numb>1){
			return true;
		}
		return false;
	}
	
	public String getTraining80() {
		return training80;
	}

	public void setTraining80(String training80) {
		this.training80 = training80;
	}

	public String getTesting20() {
		return testing20;
	}

	public void setTesting20(String testing20) {
		this.testing20 = testing20;
	}

	/**
	 * 
	 * @return true if the HEAD=0 for punctuation tokens is covered by another arc, otherwise false.
	 *
	 */
	public boolean danglingPunctuation(){
		int contNonProjLocal=0;
		int numberOfCovered=0;
		int numbSentences=0;
		boolean dangling=false;
		int contCoveredRoots=0;
		TreeMap<String,String> treeDangling=new TreeMap<String,String>();
		TreeMap<String,String> treeForms=new TreeMap<String,String>();
		try {
			br = new BufferedReader(new FileReader(trainingCorpus));
			treeDangling=new TreeMap<String,String>();
			treeForms=new TreeMap<String,String>();
			while(br.ready()){
				String line;
				try {
					line = br.readLine();
					//System.out.println(line);
					if (line==null || line.equals("")) {
						numbSentences++;
						treeDangling=new TreeMap<String,String>();
						treeForms=new TreeMap<String,String>();
						if (dangling) contCoveredRoots++;
						dangling=false;
					}
					if (line!=null && !line.equals("")) {

							String id=getColumn(line,1);
							String head=getColumn(line,7);
							treeDangling.put(id,head);
							String form =getColumn(line,2);
							treeForms.put(id,form);
							int contador=0;
							//if (head.equals("0")){
								int intid = Integer.parseInt(id);
								int inthead=Integer.parseInt(head);
								contNonProjLocal=0;
								if (inthead<intid) {
								//COVERED ROOTS!!
								for(int i=inthead+1;i<intid;i++){
									String numb=""+i;
									String formN=treeForms.get(numb);
									if (formN.equals(",")||formN.equals(".")||formN.equals(";")||formN.equals("-")||formN.equals('"')||formN.equals("'")) {
									//System.out.println(numb);
									String headOld=treeDangling.get(numb);
									//System.out.println(headOld);
									if (headOld!=null) {
										int intHeadOld=Integer.parseInt(headOld);
										if (intHeadOld==0) {
										if (headOld!=null) {
											int hold=Integer.parseInt(headOld);
											if (hold<inthead) {
												contador++;
												contNonProjLocal++;
											}
											else if (hold>intid) {
												contador++;
												contNonProjLocal++;
											}
										}
										}
									}
									}
								}
							
							}
						}
						if (contNonProjLocal>0) {
							//System.out.println(line);
							dangling=true;
							numberOfCovered+=contNonProjLocal;
							contNonProjLocal=0;
							
							//return true;
							
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
		//System.out.println(contCoveredRoots);
		double frequency=((double)contCoveredRoots/(double)numbSentences)*100;
		String freq=""+frequency;
		if (freq.length()>5) freq=freq.substring(0, 5);
		//System.out.println("Frequency of Sentences with Covered Roots (dangling punctuation): "+freq);
		//System.out.println("Number of Covered Roots (punctuation): "+numberOfCovered);
		danglingFreq=""+numberOfCovered;
		if (contCoveredRoots>0) return true;
		return false;
	}
	
	/**
	 * 
	 * @return true if the HEAD=0 covered by another Arc, otherwise false.
	 *
	 */
	public boolean coveredRoots(){
		int contNonProjLocal=0;
		int numberOfCovered=0;
		int numbSentences=0;
		boolean dangling=false;
		int contCoveredRoots=0;
		TreeMap<String,String> treeDangling=new TreeMap<String,String>();
		try {
			br = new BufferedReader(new FileReader(trainingCorpus));
			treeDangling=new TreeMap<String,String>();
			while(br.ready()){
				String line;
				try {
					line = br.readLine();
					//System.out.println(line);
					if (line==null || line.equals("")) {
						numbSentences++;
						treeDangling=new TreeMap<String,String>();
						if (dangling) contCoveredRoots++;
						dangling=false;
					}
					if (line!=null && !line.equals("")) {

							String id=getColumn(line,1);
							String head=getColumn(line,7);
							treeDangling.put(id,head);
							String form =getColumn(line,2);
							int contador=0;
							//if (head.equals("0")){
								int intid = Integer.parseInt(id);
								int inthead=Integer.parseInt(head);
								contNonProjLocal=0;
								if (inthead<intid) {
								//COVERED ROOTS!!
								for(int i=inthead+1;i<intid;i++){
									String numb=""+i;
									//System.out.println(numb);
									String headOld=treeDangling.get(numb);
									//System.out.println(headOld);
									if (headOld!=null) {
										int intHeadOld=Integer.parseInt(headOld);
										if (intHeadOld==0) {
										if (headOld!=null) {
											int hold=Integer.parseInt(headOld);
											if (hold<inthead) {
												contador++;
												contNonProjLocal++;
											}
											else if (hold>intid) {
												contador++;
												contNonProjLocal++;
											}
										}
										}
									}
								}
							
							}
						}
						if (contNonProjLocal>0) {
							//System.out.println(line);
							dangling=true;
							numberOfCovered+=contNonProjLocal;
							contNonProjLocal=0;
							//return true;
							
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
		double frequency=(double)contCoveredRoots/(double)numbSentences;
		String freq=""+frequency;
		if (freq.length()>6) freq=freq.substring(0, 6);
		System.out.println("Frequency of Sentences with Covered Roots: "+freq);
		System.out.println("Number of Covered Roots: "+numberOfCovered);
		if (contCoveredRoots>0) return true;
		return false;
	}
	
	
	/**
	 * 
	 * @return true if the HEAD=0 covered by another Arc, otherwise false.
	 *
	 */
	public boolean coveredRootsWithoutChildren(){
		String concat="";
		int contNonProjLocal=0;
		int numberOfCovered=0;
		int numbSentences=0;
		boolean dangling=false;
		int contCoveredRoots=0;
		ArrayList<String> candidates=new ArrayList<String>();
		TreeMap<String,String> treeDangling=new TreeMap<String,String>();
		try {
			br = new BufferedReader(new FileReader(trainingCorpus));
			treeDangling=new TreeMap<String,String>();
			while(br.ready()){
				String line;
				try {
					line = br.readLine();
					//System.out.println(line);
					if (line==null || line.equals("")) {
						numbSentences++;
						Iterator<String> it =candidates.iterator();
						boolean covered=true;
						if (!it.hasNext()) covered=false;
						while(it.hasNext()){
							String c=it.next();
							///int candidato=Integer.parseInt(c);
							boolean esCovered=true;
							Set<String> idSet=treeDangling.keySet();
							Iterator<String> ids=idSet.iterator();
							while(ids.hasNext()) {
								String id=ids.next();
								String headN=treeDangling.get(id);
								if (headN.equals(c)) {
									covered=false;
									esCovered=false;
								}
							}
							if (esCovered) numberOfCovered++;
						}
						//if (covered) System.out.println(concat);
						//concat="";
						
						treeDangling=new TreeMap<String,String>();
						candidates=new ArrayList<String>();
						if (covered) {
							contCoveredRoots++;
						}
						dangling=false;
					}
					if (line!=null && !line.equals("")) {

							//concat+=line+"\n";
							String id=getColumn(line,1);
							String head=getColumn(line,7);
							treeDangling.put(id,head);
							String form =getColumn(line,2);
							int contador=0;
							//if (head.equals("0")){
								int intid = Integer.parseInt(id);
								int inthead=Integer.parseInt(head);
								contNonProjLocal=0;
								if (inthead<intid) {
								//COVERED ROOTS!!
								for(int i=inthead+1;i<intid;i++){
									String numb=""+i;
									//System.out.println(numb);
									String headOld=treeDangling.get(numb);
									//System.out.println(headOld);
									if (headOld!=null) {
										int intHeadOld=Integer.parseInt(headOld);
										if (intHeadOld==0) {
										if (headOld!=null) {
											int hold=Integer.parseInt(headOld);
											if (hold<inthead) {
												contador++;
												contNonProjLocal++;
												candidates.add(numb);
											}
											else if (hold>intid) {
												contador++;
												contNonProjLocal++;
												candidates.add(numb);
											}
										}
										}
									}
								}
							
							}
						}
						if (contNonProjLocal>0) {
							//System.out.println(line);
							dangling=true;
							contNonProjLocal=0;
							//return true;
							
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
		//System.out.println(contCoveredRoots);
		//System.out.println(numbSentences);
		double frequency=(double)contCoveredRoots/(double)numbSentences;
		
		//System.out.println(frequency);
		String freq=""+frequency;
		if (freq.length()>6) freq=freq.substring(0, 6);
		System.out.println("Frequency of Sentences with Covered Roots (without children): "+freq);
		System.out.println("Number of Covered Roots (without children): "+numberOfCovered);
		if (contCoveredRoots>0) return true;
		return false;
	}
	
	/**
	 * 
	 * @return 
	 *
	 */
	public String extraDataCharacteristics(){
		String characteristics="";
		int numTokens=0;
		int numbSentences=0;
		cposEqPos=true;
		lemmaBlank=true;
		featsBlank=true;
		try {
			br = new BufferedReader(new FileReader(trainingCorpus));
			while(br.ready()){
				String line;
				try {
					line = br.readLine();
					if ((line==null) || (line.equals(""))) numbSentences++;
					else if (line!=null && (!line.equals(""))){
						numTokens++;
						String cpos=getColumn(line, 4);
						String pos=getColumn(line, 5);
						if (!pos.equals(cpos)) {
							cposEqPos=false;
						}
						String lemma=getColumn(line,3);
						if (!lemma.equals("_")) {
							lemmaBlank=false;
						}
						String feats=getColumn(line,6);
						if (!feats.equals("_")) {
							//System.out.println(feats);
							featsBlank=false;
						}
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
		System.out.println("Your training set consists of "+numbSentences +" sentences and "+numTokens +" tokens.");
		System.out.println("Testing Java Heap ... ");
		//JAVA HEAP HANDLING
		boolean validHeap=false;
		String javaHeap="";
		AlgorithmTester at=new AlgorithmTester("lang",this,this.trainingCorpus);
		int val=numTokens+1;
		int nMaxTokens=val;
		//System.out.println(nMaxTokens);
		while(!validHeap) {
			javaHeap=calculateJavaHeapValue(nMaxTokens);
			this.generateDivision8020();
			/*System.out.println("Trying with "+Optimizer.javaHeapValue);
			System.out.println(Optimizer.nMaxTokens);*/
			validHeap=at.executeCovNonProjEagerDefaultJavaHeapTesting("CovingtonNonProjective.xml");
			//validHeap=true;
			if (!validHeap){
				if (nMaxTokens>700000)
					nMaxTokens=700000;
				else if (nMaxTokens>650000)
					nMaxTokens=650000;
				else if (nMaxTokens>500000)
					nMaxTokens=500000;
				else nMaxTokens-=20000;
				Optimizer.nMaxTokens=nMaxTokens;
			}
			//System.out.println(nMaxTokens);
		}
		characteristics+=javaHeap;
		if (nMaxTokens!=val){
			System.out.println("MaltOptimizer inferred that your system cannot allocate enough memory to run experiments with the whole corpus.");
			//System.out.println("MaltOptimizer will reduce the size of the training set.");
			double percentage=(double)nMaxTokens*100/numTokens;
			String perc=""+percentage;
			if (perc.length()>5) perc=perc.substring(0,5);
			//System.out.println("The performance is going to be affected by this fact.");
			//System.out.println("We recommend the use of a system with higher memory allocation.");
			System.out.println("MaltOptimizer will reduce the size of the training set and use only "+nMaxTokens+" tokens ("+perc+"%).");
			Optimizer.nMaxTokens=nMaxTokens;	
		}
		//System.out.println(Optimizer.nMaxTokens);
				//calculateJavaHeapValue(numTokens);
		//
		
		
		if (cposEqPos) {
			characteristics+="CPOSTAG and POSTAG are identical in your training set.\n";
			Optimizer.cposEqPos=true;
		}
		else {
			characteristics+="CPOSTAG and POSTAG are distinct in your training set.\n";
			Optimizer.cposEqPos=false;
		}
		if (lemmaBlank) {
			characteristics+="The LEMMA column is not used in your training set.\n";
			Optimizer.lemmaBlank=true;
		}
		else {
			characteristics+="The LEMMA column is used in your training set.\n";
			Optimizer.lemmaBlank=false;
		}
		if (featsBlank) {
			characteristics+="The FEATS column is not used in your training set.";
			Optimizer.featsBlank=true;
		}
		else {
			characteristics+="The FEATS column is used in your training set.";
			Optimizer.featsBlank=false;
		}
		this.numbTokens=numTokens;
		this.numSentences=numbSentences;
		return characteristics;
	}
	
	private String calculateJavaHeapValue(int numTokens) {
		// TODO Auto-generated method stub
		if (numTokens<=70000) {
			//Optimizer.javaHeapValue="-Xmx2048m";
			Optimizer.javaHeapValue="-Xmx2048m";
			return "MaltOptimizer has inferred that MaltParser needs at least 2 Gb of free memory.\n";
		}
		else if (numTokens<=100000) {
			//Optimizer.javaHeapValue="-Xmx2560m";
			Optimizer.javaHeapValue="-Xmx4096m";
			return "MaltOptimizer has inferred that MaltParser needs at least 4Gb of free memory.\n";
		}
		else if (numTokens<=150000) {
			Optimizer.javaHeapValue="-Xmx5120m";
			return "MaltOptimizer has inferred that MaltParser needs at least 5Gb of free memory.\n";
		}
		/*else if (numTokens<=250000) {
			Optimizer.javaHeapValue="-Xmx3072m";
			return "MaltOptimizer has inferred that MaltParser needs at least 3Gb of free memory.\n";
		}*/
		else if (numTokens<=350000) {
			Optimizer.javaHeapValue="-Xmx5120m";
			return "MaltOptimizer has inferred that MaltParser needs at least 5Gb of free memory.\n";
		}
		else if (numTokens<=400000) {
			Optimizer.javaHeapValue="-Xmx6144m";
			return "MaltOptimizer has inferred that MaltParser needs at least 6Gb of free memory.\n";
		}
		else if (numTokens<=450000) {
			Optimizer.javaHeapValue="-Xmx7168m";
			return "MaltOptimizer has inferred that MaltParser needs at least 7Gb of free memory.\n";
		}
		else if (numTokens<=500000) {
			Optimizer.javaHeapValue="-Xmx8192m";
			return "MaltOptimizer has inferred that MaltParser needs at least 8Gb of free memory.\n";
		}
		else if (numTokens<=600000) {
			Optimizer.javaHeapValue="-Xmx10240m";
			return "MaltOptimizer has inferred that MaltParser needs at least 10Gb of free memory.\n";
		}
		/*else if (numTokens<=6500000) {
			Optimizer.javaHeapValue="-Xmx20480m";
			return "MaltOptimizer has inferred that MaltParser needs at least 9Gb of free memory.\n";
		}*/
		else if (numTokens<=700000) {
			Optimizer.javaHeapValue="-Xmx16384m";
			return "MaltOptimizer has inferred that MaltParser needs at least 16Gb of free memory.\n";
		}/*
		else  if (numTokens<=1200000){
			Optimizer.javaHeapValue="-Xmx20480m";
			return "MaltOptimizer has inferred that MaltParser needs at least 16Gb of free memory.\n";
		}*/
		else {
			Optimizer.javaHeapValue="-Xmx20480m";
			return "MaltOptimizer has inferred that MaltParser needs at least 20Gb of free memory.\n";
		}
		
		//AlgorithmTester at=new AlgorithmTester();
		
		
		
	}

	/**
	 * 
	 * @return the percentage of non-projective trees in the training set
	 *
	 */
	public double projectiveOrNonProjective(){
		//CROSSING EDGES--->contador++
		int numberOfNonProjectives=0;
		int numberOfTrees=0;
		int contador=0;
		int anteriorHead=-1;
		int anteriorId=-1;
		int contProjectivities=0;
		int numberOfArcs=0;
		String cat="";
		int cont=0;
		tree=new TreeMap<String,String>();
		invtree=new TreeMap<Integer,ArrayList<String>>();
		try {
			br = new BufferedReader(new FileReader(trainingCorpus));
		try {
				while(br.ready()){
					String line;
						line = br.readLine();
						if (line!=null && line.equals("")){
							cont++;
							numberOfTrees++;
							//System.out.println(cat);
							cat="";
							boolean nonprojective=false;
							if (cont>0) 
								nonprojective=inorder(invtree);
						
							
							
							//if (contador>0){
							if (nonprojective) {
								//System.out.println("Non Projective");
								numberOfNonProjectives++; //number of non-projectivities in the previous sentence
								contador=0; 
							}
							tree=new TreeMap<String,String>();
							invtree=new TreeMap<Integer,ArrayList<String>>();
						}
						if (line!=null && (!line.equals(""))){
							cat+=getColumn(line,2)+" ";
							numberOfArcs++;
							String head=getColumn(line,7);
							String id=getColumn(line,1);
							tree.put(id,head);
							Integer ihead=Integer.parseInt(head);
							
							ArrayList<String> children=invtree.get(ihead);
							if (children==null) children = new ArrayList<String>();
							children.add(id);
							
							
							
							invtree.put(ihead,children);
						}
				}
							
							/*int intid = Integer.parseInt(id);
							int inthead=Integer.parseInt(head);
							int contNonProjLocal=0;
							if (inthead<intid) {
								//Se trata de encontrar una dependencia que vaya por delante de donde depende este.
								//Es decir,
								//John saw a dog yesterday which was a Yorkshire terrier.
								//Estamos en WAS (7)
								//Was tiene como HEAD DOG (4) (4<7)
								//Pero Yesterday (5) que está por delante de dog (4) tiene como HEAD Saw (2)
								//Los arcos de was a dog y de yesterday a Saw se CRUZAN...
								//Esto es lo que hay que detectar
								for(int i=inthead+1;i<intid;i++){
									String numb=""+i;
									String headOld=tree.get(numb);
									if (headOld!=null) {
										int intHeadOld=Integer.parseInt(headOld);
										
										if (headOld!=null) {
											int hold=Integer.parseInt(headOld);
											if (hold<inthead) {
												contador++;
												contNonProjLocal++;
											}
											else if (hold>intid) {
												contador++;
												contNonProjLocal++;
											}
										}
									}
								}
								//if (contNonProjLocal==0){
								for(int i=inthead+1;i<intid;i++){
									String numb=""+i;
									String headOld=tree.get(numb);
									if (headOld!=null) {
										int intHeadOld=Integer.parseInt(headOld);
										/*System.out.print("("+numb+",");
										System.out.println(headOld+")");
										if (!head.equals("0") && (!(headOld.equals(head)))){//&& (!(headOld.equals(id)))) {
											//System.out.println(line);
												boolean out=expandNodeRoot(intHeadOld,inthead); //is I "dominated" by head? (in a transitive way)
											//if i is dominated by head return false, else return true and it is a non projective arc
											//(numb is i)
												if (out) {
													//System.out.println("GOOD!");
													contador++;
													contNonProjLocal++;
													}
												}
											}
										}
									//}
										
								
										
									//}//
									
									
									/*if (headOld!=null) {
										int hold=Integer.parseInt(headOld);
										if (hold<inthead) {
											contador++;
											contNonProjLocal++;
										}
										else if (hold>intid) {
											contador++;
											contNonProjLocal++;
										}
										/*else {
											String headOldS=tree.get(headOld); //father of headOld
											if (headOldS!=null){
												Integer holds=Integer.parseInt(headOldS);
												if (holds<inthead) {
													contador++;
													contNonProjLocal++;
												}
												/*else if (holds>intid) {
													contador++;
													contNonProjLocal++;
												
											}
										}
										
									}
								}
								
								if (contNonProjLocal>0) {
									contProjectivities++;
									//System.out.println("NonProjective Arc:"+getColumn(line,2) +" head:"+head);
								}
								
							//}
							/*else
								if (inthead>intid)
							{
								for(int i=inthead-1;i>1;i--){
									String numb=""+i;
									String headOld=tree.get(numb);
									if (headOld!=null) {
										int hold=Integer.parseInt(headOld);
										if (hold<inthead) {
											contador++;
										}
									}
									
								}
							}
							anteriorHead=inthead;
							anteriorId=intid;
							}*/
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		double value=0;
		/*this.numberOfTrees=numberOfTrees;
		if (this.numberOfTrees>0){
			value=((double)numberOfNonProjectives/(double)numberOfTrees)*100;
		}*/
		double k=((double)numberOfNonProjectives/(double)numberOfTrees)*100;
		String ntrees=""+k;
		//System.out.println(k);
		if (ntrees.length()>5) ntrees=ntrees.substring(0,5);
		//System.out.println(ntrees+"% of the trees contain non-projective arcs");
		this.numberOfTrees=numberOfTrees;
		this.numbSentences=numberOfTrees;
		/*if (this.numberOfTrees>0){
			value=((double)contProjectivities/(double)numberOfArcs)*100;
		}*/
		//System.out.println(value);
		
		return k;//(numberOfNonProjectives/numberOfTrees)*100;
	}
	
	private boolean inorder(TreeMap<Integer, ArrayList<String>> tree2) {
		//leftmost child
		// TODO Auto-generated method stub
		String cat="";
		Set<Integer> keySet=tree2.keySet();
		//System.out.println(keySet);
		Integer head=0;
		ArrayList<String> children=tree2.get(head);
		cat+=getSubtree(tree2,head,children);
		
		//System.out.println(cat);
		StringTokenizer st=new StringTokenizer(cat);
		Integer anterior=0;
		while(st.hasMoreTokens()){
			String s=st.nextToken();
			Integer is=Integer.parseInt(s);
			Integer shouldBe=anterior+1;
			if ((is!=(shouldBe))) {
				if (cat.contains(shouldBe.toString()))
					return true;
				//else 
				//	anterior=shouldBe;
			}
			
			anterior=is;
		}
		
		//System.out.println(cat);
		return false;
	}
	
	private String getSubtree(TreeMap<Integer, ArrayList<String>> tree2, Integer head, ArrayList<String> children) {
		String cat="";
		
		//System.out.println("("+head+","+children+")");
		Iterator<String> itch=children.iterator();
			if (!itch.hasNext()){
				cat+=" "+head;
			}
			else {
				while(itch.hasNext()) {
					String child=itch.next();
					Integer intChild=Integer.parseInt(child);
			
					ArrayList<String> nietos=tree2.get(intChild);
					if (head<intChild && (!cat.contains(head.toString())) && (head!=0)){
						cat+=" "+ head;
					}
					if (nietos==null){
						cat+=" "+child;
						//if ((intChild==head-1))
						if ((intChild==head-1)&& (!cat.contains(head.toString())))
							cat+=" "+head;
					}
					else {
						/*if (head!=0)
						cat+=getSubtree(tree2,intChild,nietos) + head.toString();
						else */
						cat+=getSubtree(tree2,intChild,nietos);
					}
				}
			}
			if (!cat.contains(head.toString())) {
				cat+=" "+head;
			}
			return cat;
		}

	private boolean expandNodeRoot(int head, int originalHead) {
		// TODO Auto-generated method stub
		if (head==0) return true; //si llegas a ROOT antes que a originalHead el arco original es non-projective	
		if (head==originalHead) return false;
		/*String strHead=""+head;
		String nhead=tree.get(strHead);*/
		
		int nIntHead=head;
		while(nIntHead!=0) {
			String strHeadN=""+nIntHead;
			String nheadN=tree.get(strHeadN);
			//System.out.println("("+originalHead+","+nheadN+")");
			if (nheadN==null) return true;
			nIntHead=Integer.parseInt(nheadN);
			if (nIntHead==0) return true; //si llegas a ROOT antes que a originalHead el arco original es non-projective	
			if (nIntHead==originalHead) return false;
		}
		/*if (nhead!=null) {
			//System.out.println(nhead);
			int nIntHead=Integer.parseInt(nhead);
			return expandNodeRoot(nIntHead, originalHead);
		}*/
		return true;
		
	}

	private String getColumn(String line, int columna) {
		StringTokenizer st=new StringTokenizer(line);
		String ret="";
		for(int i=0;i<columna;i++){
			if (st.hasMoreTokens())
				ret=st.nextToken();
		}
		return ret;
	}
	
	public String getHead0(){
		return head0;
	}
	
	
	public void generateDivision8020() {
		//CROSSING EDGES--->contador++
		//System.out.println("Generating training and test corpus");
		double percent80=0.8*numberOfTrees;
		int numbLinesTrain=0;
		int numbSentencesTest=0;
		int numbSentencesTrain=0;
		double percent20=0.2*numberOfTrees;
		String concatTrain="";
		String concatTest="";
		int trainTimes=0;
		String lastTest="";
		String line;
		boolean trainTurn=false;
		
		int ntokens=0;
		int ntokensTrain=0;
		int ntokensTest=0;
		//4 for train, 1 for test
		try {
			br = new BufferedReader(new FileReader(trainingCorpus));
			BufferedWriter bwTrain=new BufferedWriter(new FileWriter(training80));
			BufferedWriter bwTest=new BufferedWriter(new FileWriter(testing20));
			boolean metido=false;
			try {
				while(br.ready()){
					line = br.readLine();
					ntokens++;
					if (line!=null && line.equals("")){
						ntokens++;
						if (trainTimes<4){
							trainTimes++;
							trainTurn=true;
							numbSentencesTrain++;
						}
						else {
							trainTimes=0;
							trainTurn=false;
							numbSentencesTest++;
						}
						int total=numbSentencesTrain+numbSentencesTest;
						/*if (total%150==0)
							System.out.print(".");*/
					}
					if (ntokens<Optimizer.nMaxTokens-10000) {
						if (trainTurn) {
							if (numbLinesTrain>0) {
							//concatTrain+=line+"\n";
								//System.out.println(line+"\n");
								bwTrain.write(line+"\n");
								ntokensTrain++;
							}
							numbLinesTrain++;
						}
						else {
							bwTest.write(line+"\n");
							ntokensTest++;
							lastTest=line;
							//concatTest+=line+"\n";
						}
					}
					else {
						if (!metido && !line.equals("")) {
							if (trainTurn) {
								if (numbLinesTrain>0) {
								//concatTrain+=line+"\n";
									//System.out.println(line+"\n");
									bwTrain.write(line+"\n");
									ntokensTrain++;
								}
								numbLinesTrain++;
							}
							else {
								bwTest.write(line+"\n");
								ntokensTrain++;
								lastTest=line;
								//concatTest+=line+"\n";
							}
						}
						else {
							if (line.equals("")) {
								metido=true;
							}
						}
					}
				}
				
				//System.out.println("");
				
				//bwTrain.write(concatTrain);
				bwTrain.close();
				//bwTest.write(concatTest);
				//bwTest.write("\n");
				//bwTest.write("\n");
				if (!lastTest.equals(""))
					bwTest.write("\n");
				bwTest.close();
				numbSentencesTest++;
				numbSentencesTrain--;
				/*System.out.println("Testing Set of "+numbSentencesTest +" sentences generated");
				System.out.println("Training Set of "+numbSentencesTrain +" sentences generated");*/
				messageDivision="Generated training set ("+ntokensTrain +" tokens) and devtest set ("+ntokensTest +" tokens).";
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
			//System.out.println("\nCorpora generated");
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void generate5FoldCrossCorpora() {
		
		if (!Optimizer.pseudoRandomizeSelection) this.generate5FoldCrossCorporaNoPseudo();
		else this.generate5FoldCrossCorporaPseudo();
	}
	
	
	public void generate5FoldCrossCorporaNoPseudo() {
		
		//Generate 5 Small Folds and 5 corresponding big folds. Save the names as follows.
		//fold_train1
		//fold_test1
		//fold_train2
		//fold_test2
		//fold_train3
		//...
		
		//CROSSING EDGES--->contador++
		//System.out.println("Generating training and test corpus");
		int numbLinesTrain=0;
		int numbSentencesTest=0;
		int numbSentencesTrain=0;
		//this.n
		
		int numberOfLines=Optimizer.numbTokens+Optimizer.numbSentences;
		/*System.out.println(Optimizer.numbTokens);
		System.out.println(Optimizer.numbSentences);
		System.out.println(numberOfLines);
		/*System.out.println(numberOfLines);
		System.out.println(numSentences);*/
		double percent20=0.2*(numberOfLines);
		double percent40=0.4*(numberOfLines);
		double percent60=0.6*(numberOfLines);
		double percent80=0.8*(numberOfLines);
		
		/*System.out.println(percent20);
		System.out.println(percent40);
		System.out.println(percent60);
		System.out.println(percent80);
		System.out.println(numberOfLines);*/
		
		//1 empieza en 0 para test
		//2 empieza en 20% para test
		//3 empieza en 40% para test
		//4 empieza en 60% para test
		//5 empieza en 60% para test
		
		double limitInfTest=0;
		double limitSupTest=percent20;
		for (int i=1;i<6;i++) {
			String training="fold_train_"+i+".conll";
			String test="fold_test_"+i+".conll";
			
			int trainTimes=0;
			String lastTest="";
			String line;
			boolean trainTurn=false;
		
			int ntokens=0;
			//4 for train, 1 for test
			try {
				br = new BufferedReader(new FileReader(trainingCorpus));
				BufferedWriter bwTrain=new BufferedWriter(new FileWriter(training));
				BufferedWriter bwTest=new BufferedWriter(new FileWriter(test));
				try {
					
					boolean testTurn=false;
					if (i==1) {
						limitInfTest=0;
						limitSupTest=percent20;
						testTurn=true;
					}
					if (i==2) {
						limitInfTest=percent20;
						limitSupTest=percent40;
					}
					if (i==3) {
						limitInfTest=percent40;
						limitSupTest=percent60;
					}
					if (i==4) {
						limitInfTest=percent60;
						limitSupTest=percent80;
					}
					if (i==5) {
						limitInfTest=percent80;
						limitSupTest=numberOfLines;
					}
					int nSentencesTest=0;
					String concat="";
					while(br.ready()){
						line = br.readLine();
						ntokens++;
						if (!line.equals("")){
							concat+=line+"\n";
						}
						else {
							if (ntokens>limitInfTest && ntokens<=limitSupTest) {
								bwTest.write(concat+"\n");
								concat="";
							}
							else {
								bwTrain.write(concat+"\n");
								concat="";
							}
						}
					}
					//System.out.println("");
				
				//bwTrain.write(concatTrain);
				bwTrain.close();
				//bwTest.write(concatTest);
				//bwTest.write("\n");
				//bwTest.write("\n");
				if (!lastTest.equals(""))
					bwTest.write("\n");
				bwTest.close();
				numbSentencesTest++;
				numbSentencesTrain--;
				//System.out.println("Test Fold "+i +"; Train Fold "+i+" generated");
				messageDivision="Test Fold "+i +"; Train Fold "+i+" generated";
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			//System.out.println("\nCorpora generated");
			catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
		
	}
	
	public void generate5FoldCrossCorporaPseudo() {

		
		int numbSentences=0;

		String line;
		//4 for train, 1 for test
			try {
				br = new BufferedReader(new FileReader(trainingCorpus));
				
				ArrayList<BufferedWriter> listBuffersTest=new ArrayList<BufferedWriter>();
				ArrayList<BufferedWriter> listBuffersTrain=new ArrayList<BufferedWriter>();
				for (int i=1;i<6;i++){ //could be extended if needed to an N-fold cross validation
					String training="fold_train_"+i+".conll";
					String test="fold_test_"+i+".conll";
					
					listBuffersTest.add(new BufferedWriter(new FileWriter(test)));
					listBuffersTrain.add(new BufferedWriter(new FileWriter(training)));
				}

			String concat="";
			int module=1;
			while(br.ready()){
					line = br.readLine();
					if (concat.equals("")) {
							concat=line;
						}
						else {
							concat=concat+"\n"+line;
						}
						if (line!=null && line.equals("")){
							numbSentences++;
							
							int k=module-1;
							listBuffersTest.get(k).write(concat+"\n");
		
							for (int i=0;i<listBuffersTrain.size();i++) {
								if (i!=k) {
									listBuffersTrain.get(i).write(concat+"\n");
								}
							}
							if (module<5) {
								module++;
							}
							else if (module==5) {
								module=1;
							}
							concat="";
						}
						
					}
			
				for(int i=0;i<listBuffersTest.size();i++){
					listBuffersTest.get(i).close();
					listBuffersTrain.get(i).close();
				}
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		
		//}
		//System.out.println("#Sentences:"+numbSentences);
		System.out.println("Five cross-validation folds generated.");
		
	}


public void generate5FoldCrossCorporaPseudoOld() {
	//CROSSING EDGES--->contador++
	//System.out.println("Generating training and test corpus");
	int numbLinesTrain=0;
	int numbSentencesTest=0;
	int numbSentencesTrain=0;
	
	int numbSentences=0;

	int trainTimes=0;
	String lastTest="";
	String line;
	boolean trainTurn=false;
	boolean firstTime=true;
	
	int ntokens=0;
	//4 for train, 1 for test
	for (int i=1;i<6;i++) {
		String training="fold_train_"+i+".conll";
		String test="fold_test_"+i+".conll";
		int contLinesTest=0;
	try {
		br = new BufferedReader(new FileReader(trainingCorpus));
		BufferedWriter bwTrain=new BufferedWriter(new FileWriter(training));
		BufferedWriter bwTest=new BufferedWriter(new FileWriter(test));
		try {
			while(br.ready()){
				line = br.readLine();
				ntokens++;
				if (line!=null && line.equals("")){
					ntokens++;
					if ((trainTimes<i)||(trainTimes>i)){
						trainTurn=true;
						numbSentencesTrain++;
						numbSentences++;
						if (trainTimes==5) trainTimes=0;
						else trainTimes++;
					}
					else { //trainTimes==i
						trainTurn=false;
						firstTime=true;
						numbSentencesTest++;
						numbSentences++;
						if (trainTimes==5) trainTimes=0;
						else trainTimes++;
					}
				}
				//if (ntokens<Optimizer.nMaxTokens) {
					if (trainTurn) {
						if (numbLinesTrain>0) {
						//concatTrain+=line+"\n";
							bwTrain.write(line+"\n");
						}
						numbLinesTrain++;
					}
					else {
						if (contLinesTest==0 && line.equals("")) {
							firstTime=false;
						}
						else {
							bwTest.write(line+"\n");
							lastTest=line;
							contLinesTest++;
						}
						//concatTest+=line+"\n";
					}
				//}
			}
			//System.out.println("");
			
			//bwTrain.write(concatTrain);
			bwTrain.close();

			if (!lastTest.equals(""))
				bwTest.write("\n");
			
			bwTest.close();
			numbSentencesTest++;
			numbSentencesTrain--;
			/*System.out.println("Testing Set of "+numbSentencesTest +" sentences generated");
			System.out.println("Training Set of "+numbSentencesTrain +" sentences generated");*/
			//System.out.println("FOLD: "+i+" generated.");
			
			//messageDivision="Generated training set ("+numbSentencesTrain +" sentences) and test set ("+numbSentencesTest +" sentences).";
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
		//System.out.println("\nCorpora generated");
	catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	System.out.println("#Sentences:"+numbSentences);
	System.out.println("Five cross-validation folds generated.");
	
}
	
	public double evaluator(String testCorpus, String goldStandard) {
		
		if (Optimizer.evaluationMeasure.equals("LAS"))
			return evalLAS(testCorpus,goldStandard);
		else if (Optimizer.evaluationMeasure.equals("UAS"))
			return evalUAS(testCorpus,goldStandard);
		else if (Optimizer.evaluationMeasure.equals("LCM"))
			return evalLCM(testCorpus,goldStandard);
		else if (Optimizer.evaluationMeasure.equals("UCM"))
			return evalUCM(testCorpus,goldStandard);
		return evalLAS(testCorpus,goldStandard);
		
	}
	
	
public double evalLAS(String testCorpus, String goldStandard) {
	//System.out.println(testCorpus);
	//System.out.println(goldStandard);
	Double v=0.0;
	String script="eval07.pl";
	if (!Optimizer.includePunctuation) {
		script="eval.pl";	
	}
	Process p;
	try {
		//System.out.println("perl "+script+" -g "+goldStandard+" -s "+testCorpus+" -q");
		p = Runtime.getRuntime().exec("perl "+script+" -g "+goldStandard+" -s "+testCorpus+" -q");
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
	               p.getInputStream()));
		/*BufferedReader stdOutput = new BufferedReader(new InputStreamReader(
	               p.getOutputStream()));*/
		BufferedReader stdError = new BufferedReader(new InputStreamReader(
	               p.getErrorStream()));
		String s="";
		int cont=0;
		String val="";
		while ((s = stdError.readLine()) != null) {
			System.out.println(s);
		}
		while ((s = stdInput.readLine()) != null) {
			//System.out.println(s);
			
			if (cont==0) {
				StringTokenizer st=new StringTokenizer(s,"=");
				st.nextToken();
				val=st.nextToken();
				val=val.replaceAll(" ","");
				val=val.replaceAll("%","");
			}
			cont++;
		}
		//if (val.length()==4) val=val+"0";
		v=Double.parseDouble(val);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	// Leemos la salida del comando
	//System.out.println("Ésta es la salida standard del comando:\n");
	return v;
}

public double evalUAS(String testCorpus, String goldStandard) {
	
	
	Double v=0.0;
	String script="eval07.pl";
	if (!Optimizer.includePunctuation) {
		script="eval.pl";	
	}
	Process p;
	try {
		//System.out.println("perl "+script+" -g "+goldStandard+" -s "+testCorpus+" -q");
		p = Runtime.getRuntime().exec("perl "+script+" -g "+goldStandard+" -s "+testCorpus+" -q");
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
	               p.getInputStream()));
		/*BufferedReader stdOutput = new BufferedReader(new InputStreamReader(
	               p.getOutputStream()));*/
		BufferedReader stdError = new BufferedReader(new InputStreamReader(
	               p.getErrorStream()));
		String s="";
		int cont=0;
		String val="";
		while ((s = stdInput.readLine()) != null) {
	//		System.out.println(s);
			
			if (cont==1) {
				StringTokenizer st=new StringTokenizer(s,"=");
				st.nextToken();
				val=st.nextToken();
				val=val.replaceAll(" ","");
				val=val.replaceAll("%","");
			}
			cont++;
		}
		//if (val.length()==4) val=val+"0";
		v=Double.parseDouble(val);
	} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
		
		
		// Leemos la salida del comando
		//System.out.println("Ésta es la salida standard del comando:\n");
	return v;
}


public double evalLCM(String testCorpus, String goldStandard) {
	
	
	BufferedReader tc;
	BufferedReader gs;
	
	double correctNodes=0;
	double totNodes=0;
	
	double totSentences=0;
	double correctSentences=0;
	
	//4 for train, 1 for test
	try {
		tc = new BufferedReader(new FileReader(testCorpus));
		gs = new BufferedReader(new FileReader(goldStandard));
		
		try {
			String lineTc;
			String lineGs;
			while(tc.ready() && gs.ready()){
				lineTc = tc.readLine();
				lineGs = gs.readLine();
				if ((lineTc!=null && lineGs!=null) && (!lineTc.equals("") && !lineGs.equals(""))){
					String tok=getColumn(lineTc,2);
					String headTc=getColumn(lineTc,7);
					String headGs=getColumn(lineGs,7);
					String deprelTc=getColumn(lineTc,8);
					String deprelGs=getColumn(lineGs,8);
					//if (!((tok.equals(","))||(tok.equals("."))||(tok.equals(":"))||(tok.equals("-"))||(tok.equals(";"))||(tok.equals("'"))||(tok.equals('"'))||(tok.equals("^"))||(tok.equals("-"))||(tok.equals("..."))||(tok.equals("_")))) {
					if (!Optimizer.includePunctuation) {
					if (tok.length()==1){
						if (Character.getType(tok.charAt(0))!=20 && Character.getType(tok.charAt(0))!=21 && Character.getType(tok.charAt(0))!=22 && Character.getType(tok.charAt(0))!=23 && Character.getType(tok.charAt(0))!=24 && Character.getType(tok.charAt(0))!=29 && Character.getType(tok.charAt(0))!=30)
						{
							/*totNodes=totNodes+1.0;
							if (headTc.equals(headGs) && deprelTc.equals(deprelGs)) {
								correctNodes=correctNodes+1.0;
							}*/
						}
						else {
							totNodes=totNodes+1.0;
							if (headTc.equals(headGs) && deprelTc.equals(deprelGs)) {
								correctNodes=correctNodes+1.0;
							}
						}
					}
						else {
							totNodes=totNodes+1.0;
							if (headTc.equals(headGs) && deprelTc.equals(deprelGs)) {
								correctNodes=correctNodes+1.0;
							}
						}
					}
					else {
						totNodes=totNodes+1.0;
						if (headTc.equals(headGs) && deprelTc.equals(deprelGs)) {
							correctNodes=correctNodes+1.0;
						}
					}
					//}
				}
				else {
					totSentences++;
					if (totNodes==correctNodes){
						correctSentences++;
					}
					totNodes=0;
					correctNodes=0;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	/*System.out.println(correctNodes);
	System.out.println(totNodes);*/
	System.out.println("\tLAS:"+evalLAS(testCorpus,goldStandard));
	return (correctSentences/totSentences)*100;
}


public double evalUCM(String testCorpus, String goldStandard) {
	
	
	BufferedReader tc;
	BufferedReader gs;
	
	double correctNodes=0;
	double totNodes=0;
	
	double totSentences=0;
	double correctSentences=0;
	
	//4 for train, 1 for test
	try {
		tc = new BufferedReader(new FileReader(testCorpus));
		gs = new BufferedReader(new FileReader(goldStandard));
		
		try {
			String lineTc;
			String lineGs;
			while(tc.ready() && gs.ready()){
				lineTc = tc.readLine();
				lineGs = gs.readLine();
				if ((lineTc!=null && lineGs!=null) && (!lineTc.equals("") && !lineGs.equals(""))){
					String tok=getColumn(lineTc,2);
					String headTc=getColumn(lineTc,7);
					String headGs=getColumn(lineGs,7);
					String deprelTc=getColumn(lineTc,8);
					String deprelGs=getColumn(lineGs,8);
					//if (!((tok.equals(","))||(tok.equals("."))||(tok.equals(":"))||(tok.equals("-"))||(tok.equals(";"))||(tok.equals("'"))||(tok.equals('"'))||(tok.equals("^"))||(tok.equals("-"))||(tok.equals("..."))||(tok.equals("_")))) {
					if (!Optimizer.includePunctuation) {
					if (tok.length()==1){
						if (Character.getType(tok.charAt(0))!=20 && Character.getType(tok.charAt(0))!=21 && Character.getType(tok.charAt(0))!=22 && Character.getType(tok.charAt(0))!=23 && Character.getType(tok.charAt(0))!=24 && Character.getType(tok.charAt(0))!=29 && Character.getType(tok.charAt(0))!=30)
						{
							/*totNodes=totNodes+1.0;
							if (headTc.equals(headGs)) {
								correctNodes=correctNodes+1.0;
							}*/
						}
						else {
							totNodes=totNodes+1.0;
							if (headTc.equals(headGs)) {
								correctNodes=correctNodes+1.0;
							}
						}
					}
						else {
							totNodes=totNodes+1.0;
							if (headTc.equals(headGs)) {
								correctNodes=correctNodes+1.0;
							}
						}
					}
					else {
						totNodes=totNodes+1.0;
						if (headTc.equals(headGs)) {
							correctNodes=correctNodes+1.0;
						}
					}
					//}
				}
				else {
					totSentences++;
					if (totNodes==correctNodes){
						correctSentences++;
					}
					totNodes=0;
					correctNodes=0;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	/*System.out.println(correctNodes);
	System.out.println(totNodes);*/
	System.out.println("\tLAS:"+evalLAS(testCorpus,goldStandard));
	return (correctSentences/totSentences)*100;
}
	
	
	public static void main(String[] args) {
		CoNLLHandler ch=new CoNLLHandler("/home/miguel/Descargas/swedish_dep_train_tagged.conll");
		//ch.extraDataCharacteristics();
		//ch.generate5FoldCrossCorporaPseudo();
		//System.out.println(ch.projectiveOrNonProjective());
		ch.generate5FoldCrossCorporaPseudo();
		//Optimizer.includePunctuation=false;
		//double eval=ch.evaluator("spanish_cast3lb_train_test20.conll","outNivreEager.conll");
		//System.out.println(eval);
		
	}
	

}
