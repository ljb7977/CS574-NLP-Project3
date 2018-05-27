package optimizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 
 * @author Miguel Ballesteros
 *
 */
public class FeatureGenerator {
	
	String language="lang";
	private static String addAfterHeadIterative="";
	public FeatureGenerator(String language) {
		this.language=language;
	}
	
	public FeatureGenerator() {
	}
	
	public String generate(){
		String feature="";
		if (language.equals("en")) {
			feature+="<?xml version='1.0' encoding='UTF-8'?>";
			feature+="\n\t<featuremodels>";
			feature+="\n\t\t<featuremodel name="+language+"Model>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Stack[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Input[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Input[1])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Input[2])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Input[3])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Stack[1])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Stack[2])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, head(Stack[0]))</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, ldep(Stack[0]))</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, rdep(Stack[0]))</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, ldep(Input[0]))</feature>";
			feature+="\n\t\t\t<feature>OutputColumn(DEPREL, Stack[0])</feature>";
			feature+="\n\t\t\t<feature>OutputColumn(DEPREL, ldep(Stack[0]))</feature>";
			feature+="\n\t\t\t<feature>OutputColumn(DEPREL, rdep(Stack[0]))</feature>";
			feature+="\n\t\t\t<feature>OutputColumn(DEPREL, ldep(Input[0]))</feature>";
			feature+="\n\t\t\t<feature>InputColumn(FORM, Stack[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(FORM, Input[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(FORM, Input[1])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(FORM, head(Stack[0]))</feature>";
			feature+="\n\t\t</featuremodel>";
			feature+="\n\t</featuremodels>";
		}
		
		if (language.equals("es")) {
			feature+="<?xml version='1.0' encoding='UTF-8'?>";
			feature+="\n\t<featuremodels>";
			feature+="\n\t\t<featuremodel name="+language+"Model>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Stack[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Input[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Input[1])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Input[2])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Input[3])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Stack[1])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, head(Stack[0]))</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, ldep(Stack[0]))</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, rdep(Stack[0]))</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, ldep(Input[0]))</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, succ(Stack[0]))</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, pred(Stack[0]))</feature>";
			feature+="\n\t\t\t<feature>InputColumn(POSTAG, Stack[2])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(FEATS, Stack[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(FEATS, Input[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(FEATS, Input[1])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(FEATS, head(Stack[1]))</feature>";
			feature+="\n\t\t\t<feature>OutputColumn(DEPREL, rdep(Stack[0]))</feature>";
			feature+="\n\t\t\t<feature>OutputColumn(DEPREL, ldep(Input[0]))</feature>";
			feature+="\n\t\t\t<feature>InputColumn(FORM, Stack[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(FORM, Input[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(LEMMA, Stack[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(LEMMA, Input[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(LEMMA, ldep(Stack[0]))</feature>";
			feature+="\n\t\t\t<feature>InputColumn(CPOSTAG, Stack[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(CPOSTAG, Input[0])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(CPOSTAG, Input[1])</feature>";
			feature+="\n\t\t\t<feature>InputColumn(CPOSTAG, head(Stack[0]))</feature>";
			feature+="\n\t\t</featuremodel>";
			feature+="\n\t</featuremodels>";
		}
		
		return feature;
	}
	
	
	public void addInputNivreEager(String original, String newFeat){
		try {
			BufferedReader br = new BufferedReader(new FileReader(original));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeat));
			boolean inputFound=false;
			boolean inputClose=false;
			int inputCounter=0;
			while(br.ready()) {
				String line=br.readLine();
				if ((line.contains("POSTAG")) && line.contains("Input[0]")) {
					inputFound=true;
					inputCounter++;
				}
				else if (inputFound && (line.contains("POSTAG")) && line.contains("Input[")){
					inputCounter++;
				}
				else if (inputFound && (!line.contains("Input["))){
					inputClose=true;
					inputFound=false;
				}
				/*if (!(inputFound) && !(inputClose)) {
					bw.write(line+"\n");
				}*/
				if (inputClose) {
					inputClose=false;
					bw.write("\t\t<feature>InputColumn(POSTAG, Input["+inputCounter+"])</feature>\n");
				}
				bw.write(line+"\n");
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

}
	
	public void printFeature(String original){
		try {
			BufferedReader br = new BufferedReader(new FileReader(original));
			while(br.ready()) {
				String line=br.readLine();
				System.out.println(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

}
	
	public void removeInputNivreEager(String original, String newFeat){
		try {
			BufferedReader br = new BufferedReader(new FileReader(original));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeat));
			boolean inputFound=false;
			boolean inputClose=false;
			int inputCounter=0;
			String anterior="";
			int cont=0;
			while(br.ready()) {
				cont++;
				String line=br.readLine();
				if ((line.contains("POSTAG")) && line.contains("Input[0]")) {
					inputFound=true;
					inputCounter++;
				}
				else if (inputFound && (line.contains("POSTAG")) && line.contains("Input[")){
					inputCounter++;
				}
				else if (inputFound && (!line.contains("Input["))){
					inputClose=true;
					inputFound=false;
				}
				/*if (!(inputFound) && !(inputClose)) {
					bw.write(line+"\n");
				}*/
				if (inputClose) {
					inputClose=false;
					//bw.write("\t\t<feature>InputColumn(POSTAG, Input["+inputCounter+"])</feature>\n");
					anterior=line;
				}
				else {
					if (cont!=0){
						if (!anterior.equals(""))
							bw.write(anterior+"\n");
					}
					anterior=line;
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

}
	
	public void addStack(String original, String newFeat){
		String structure="Stack";
		if (Optimizer.bestAlgorithm.contains("cov")) {
			structure="Left";
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(original));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeat));
			boolean StackFound=false;
			boolean StackClose=false;
			int StackCounter=0;
			while(br.ready()) {
				String line=br.readLine();
				if ((line.contains("POSTAG")) && line.contains(structure+"[0]")) {
					StackFound=true;
					StackCounter++;
				}
				else if (StackFound && (line.contains("POSTAG")) && line.contains(structure+"[")){
					StackCounter++;
				}
				else if (StackFound && (!line.contains(structure+"["))){
					StackClose=true;
					StackFound=false;
				}
				/*if (!(StackFound) && !(StackClose)) {
					bw.write(line+"\n");
				}*/
				if (StackClose) {
					StackClose=false;
					bw.write("\t\t<feature>StackColumn(POSTAG, "+structure+"["+StackCounter+"])</feature>\n");
				}
				bw.write(line+"\n");
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

}
	
	public void removeStack(String original, String newFeat){
		String structure="Stack";
		if (Optimizer.bestAlgorithm.contains("cov")) {
			structure="Left";
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(original));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeat));
			boolean StackFound=false;
			boolean StackClose=false;
			int StackCounter=0;
			String anterior="";
			int cont=0;
			while(br.ready()) {
				cont++;
				String line=br.readLine();
				if ((line.contains("POSTAG")) && line.contains("Stack[0]")) {
					StackFound=true;
					StackCounter++;
				}
				else if (StackFound && (line.contains("POSTAG")) && line.contains(structure+"[")){
					StackCounter++;
				}
				else if (StackFound && (!line.contains(structure+"["))){
					StackClose=true;
					StackFound=false;
				}
				/*if (!(StackFound) && !(StackClose)) {
					bw.write(line+"\n");
				}*/
				if (StackClose) {
					StackClose=false;
					//bw.write("\t\t<feature>StackColumn(POSTAG, Stack["+StackCounter+"])</feature>\n");
					anterior=line;
				}
				else {
					if (cont!=0){
						if (!anterior.equals(""))
							bw.write(anterior+"\n");
					}
					anterior=line;
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

}
	
	public boolean addLookAheadStackLazy(String original, String newFeat){
		try {
			BufferedReader br = new BufferedReader(new FileReader(original));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeat));
			boolean inputFound=false;
			boolean inputClose=false;
			int inputCounter=0;
			while(br.ready()) {
				String line=br.readLine();
				if ((line.contains("POSTAG")) && line.contains("Lookahead[0]")) {
					inputFound=true;
					inputCounter++;
				}
				else if (inputFound && (line.contains("POSTAG")) && line.contains("Lookahead[")){
					inputCounter++;
				}
				else if (inputFound && (!line.contains("Lookahead["))){
					inputClose=true;
					inputFound=false;
				}
				/*if (!(inputFound) && !(inputClose)) {
					bw.write(line+"\n");
				}*/
				if (inputClose) {
					inputClose=false;
					bw.write("\t\t<feature>InputColumn(POSTAG, Lookahead["+inputCounter+"])</feature>\n");
				}
				bw.write(line+"\n");
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	return true;

}
	
	public boolean removeLookAheadStackLazy(String original, String newFeat){
		try {
			BufferedReader br = new BufferedReader(new FileReader(original));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeat));
			boolean inputFound=false;
			boolean inputClose=false;
			int inputCounter=0;
			String anterior="";
			int cont=0;
			while(br.ready()) {
				cont++;
				String line=br.readLine();
				if ((line.contains("POSTAG")) && line.contains("Lookahead[0]")) {
					inputFound=true;
					inputCounter++;
				}
				else if (inputFound && (line.contains("POSTAG")) && line.contains("Lookahead[")){
					inputCounter++;
				}
				else if (inputFound && (!line.contains("Lookahead["))){
					inputClose=true;
					inputFound=false;
				}
				/*if (!(inputFound) && !(inputClose)) {
					bw.write(line+"\n");
				}*/
				if (inputClose) {
					inputClose=false;
					//bw.write("\t\t<feature>InputColumn(POSTAG, Input["+inputCounter+"])</feature>\n");
					anterior=line;
				}
				else {
					if (cont!=0){
						if (!anterior.equals(""))
							bw.write(anterior+"\n");
					}
					anterior=line;
				}
			}
			bw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
		return true;

}





/*************************
 * 
 * @param featureModel
 * @param newFeature
 * @param window
 */

public void removeStackWindow(String featureModel, String newFeature, String window) {
	// TODO Auto-generated method stub
	String structure="Stack";
	if (Optimizer.bestAlgorithm.contains("cov")) {
		structure="Left";
	}
		int max=findMaxStack(window, featureModel);
		if (max>=0) {
			if (window.equals("DEPREL")) {
				System.out.println("  rm OutputColumn("+window+","+structure+"["+max+"])");
			}
			else {
				System.out.println("  rm InputColumn("+window+","+structure+"["+max+"])");
			}
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				if (!line.contains("("+window+", "+structure+"["+max)) {
					bw.write(line+"\n");
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
	}

public void removeInputWindow(String featureModel, String newFeature, String window) {
	// TODO Auto-generated method stub
		int max=findMaxInput(window, featureModel, Optimizer.InputLookAhead);
		if (max>=0) {
			
			if (window.equals("DEPREL")) {
				System.out.println("  rm OutputColumn("+window+","+Optimizer.InputLookAhead+"["+max+"])");
			}
			else {
				System.out.println("  rm InputColumn("+window+","+Optimizer.InputLookAhead+"["+max+"])");
			}
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				if (!line.contains("("+window+", "+Optimizer.InputLookAhead+"["+max)) {
					bw.write(line+"\n");
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
	}

public void removeInputWindowSpecial(String featureModel, String newFeature, String window) {
	// TODO Auto-generated method stub
		int max=findMaxInput(window, featureModel, "Input");
		if (max>=0) {
			System.out.println("  rm InputColumn("+window+",Input["+max+"])");
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				if (!line.contains("("+window+", Input["+max)) {
					bw.write(line+"\n");
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
	}

public void removeLeftContextWindowSpecial(String featureModel, String newFeature, String window) {
	// TODO Auto-generated method stub
		int max=findMaxInput(window, featureModel, "LeftContext");
		System.out.println("  rm InputColumn("+window+",LeftContext["+max+"])");
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				if (!line.contains("("+window+", LeftContext["+max)) {
					bw.write(line+"\n");
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

public void removeRightContextWindowSpecial(String featureModel, String newFeature, String window) {
	// TODO Auto-generated method stub
		int max=findMaxInput(window, featureModel, "RightContext");
		System.out.println("  rm InputColumn("+window+",RightContext["+max+"])");
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				if (!line.contains("("+window+", RightContext["+max)) {
					bw.write(line+"\n");
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

public void addStackWindow(String featureModel, String newFeature, String window, String inputLookAhead,String inOrOut) {
	// TODO Auto-generated method stub
	String structure="Stack";
	if (Optimizer.bestAlgorithm.contains("cov")) {
		structure="Left";
	}
		int max=findMaxStack(window, featureModel);
		int v=max+1;
		/*if (window.equals("DEPREL")) {
			System.out.println("  add OutputColumn("+window+","+structure+"["+v+"])");
		}
		else {
			System.out.println("  add InputColumn("+window+","+structure+"["+v+"])");
		}*/
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				bw.write(line+"\n");
				int val=max+1;
				if (line.contains("("+window+", "+structure+"["+max)) {
					if ((line.contains("Merge") && (!line.contains("Merge3")))) {
						//<feature>Merge(InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Input[0]))</feature>
						//System.out.println("  add Merge("+inOrOut+"("+window+", "+structure+"["+val+"]), InputColumn("+window+", "+inputLookAhead+"["+val+"]))");
						bw.write("\t\t<feature>Merge("+inOrOut+"("+window+", "+structure+"["+val+"]), InputColumn("+window+", "+inputLookAhead+"["+val+"]))</feature>\n");
					}
					else if (line.contains("Merge3")) {
						//<feature>Merge3(InputColumn(POSTAG, Stack[1]), InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Input[0]))</feature>
						//System.out.println("  add Merge3("+inOrOut+"("+window+", "+structure+"["+val+"]), InputColumn("+window+", "+structure+"["+max+"]), InputColumn("+window+", "+inputLookAhead+"["+max+"]))");
						bw.write("\t\t<feature>Merge3("+inOrOut+"("+window+", "+structure+"["+val+"]), InputColumn("+window+", "+structure+"["+max+"]), InputColumn("+window+", "+inputLookAhead+"["+max+"]))</feature>\n");
					}
					else {
						//<feature>InputColumn(POSTAG, Stack[1])</feature>
						System.out.println("  add "+inOrOut+"("+window+", "+structure+"["+val+"])");
						bw.write("\t\t<feature>"+inOrOut+"("+window+", "+structure+"["+val+"])</feature>\n");
					}
					
				}
				
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}




public void removeInputWindow(String featureModel, String newFeature, String window, String InputLookAhead) {
	// TODO Auto-generated method stub
		int max=findMaxInput(window, featureModel,InputLookAhead);
		if (max>=0) {
			if (window.equals("DEPREL")) {
				System.out.println("  rm OutputColumn("+window+","+InputLookAhead+"["+max+"])");
			}
			else {
				System.out.println("  rm InputColumn("+window+","+InputLookAhead+"["+max+"])");
			}
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				if (!line.contains("("+window+", "+InputLookAhead+"["+max)) {
					bw.write(line+"\n");
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
	}

public boolean removeDeprelWindow(String featureModel, String newFeature, String window, String InputLookAhead,int i) {
	// TODO Auto-generated method stub
		int max=findMaxInput(window, featureModel,InputLookAhead);
		//System.out.println("  rm OutputColumn("+window+","+InputLookAhead+"["+max+"])");
		if (max>=0) {
		//System.out.println("  rm "+window+" feature");
		//if (window.equals("DEPREL")) {
			
		//}
		//else {
	//		System.out.println("  rm InputColumn("+window+","+InputLookAhead+"["+max+"])");
	//	}
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			boolean removed=false;
			int count=0;
			while(br.ready()) {
				String line=br.readLine();
				if (line.contains("OutputColumn("+window) && !removed) {
					count++;
					if (count==i) {
						removed=true;
						//System.out.println(line);
						String s=line.replaceAll("<feature>","");
						s=s.replaceAll("</feature>","");
						s=s.replaceAll("\t","");
						System.out.println(" rm "+s);
					}
					else bw.write(line+"\n");
				}
				else bw.write(line+"\n");
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
		}
		return false;
	}

public boolean replicatePostagDeprel(String featureModel, String newFeature, String window, String InputLookAhead,int i) {
	// TODO Auto-generated method stub
	String structure="Stack";
	if (Optimizer.bestAlgorithm.contains("cov")) {
		structure="Left";
	}
		boolean usado=false;
		int max=findMaxInput(window, featureModel,InputLookAhead);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			boolean used=false;
			boolean toInclude=false;
			int count=0;
			while(br.ready()) {
				String line=br.readLine();
				if (line.contains("OutputColumn("+window) && !used) {
					count++;
					if (count==i) {
						used=true;
						bw.write(line+"\n");
						//<feature>OutputColumn(DEPREL, Stack[0])</feature>
						StringTokenizer st=new StringTokenizer(line,",");
						String s="";
						if (st.hasMoreTokens())
							s=st.nextToken();
						if (st.hasMoreTokens())
						s=st.nextToken();
						s=s.replaceAll("</feature>", "");
						s=s.substring(1,s.length());
						String newLine="";
						if (s.contains(structure+"[0]") && (!s.contains("ldep") || !s.contains("rdep")) && Optimizer.bestAlgorithm.equals("NivreEager")) {
							System.out.println("  add InputColumn(POSTAG, "+"head("+s+")");
							newLine="\t\t<feature>InputColumn(POSTAG, "+"head("+s+")</feature>\n";	
							toInclude=true;
						}
						else {
							int max2=findMaxInput("POSTAG", featureModel,InputLookAhead);
							//if (max2<max) {
							newLine="\t\t<feature>InputColumn(POSTAG, "+s+"</feature>\n";
							if (isNotIncluded(newLine,featureModel)) {
								System.out.println("  add InputColumn(POSTAG, "+s);
								toInclude=true;
							}
							//}
							
							//System.out.println(newLine);
						}
						
						if (toInclude) {
							bw.write(newLine);
							toInclude=false;
							usado=true;
						}
								
					}
					else bw.write(line+"\n");
				}
				else bw.write(line+"\n");
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return usado;
	}

private boolean isNotIncluded(String newLine, String featureModel) {
	// TODO Auto-generated method stub
		String n=newLine.replaceAll("\n", "");
		n=n.replaceAll("\t","");
		
		//System.out.println(n);
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			
			boolean include=false;
			int count=0;
			while(br.ready()) {
				String line=br.readLine();
				//System.out.println(line);
				if (line.contains(n)) 
					return false;
			}
			br.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	return true;
}

public void addPredSucc(String featureModel, String newFeature, String window, String inputStack, String predSucc) {
	// TODO Auto-generated method stub
	
	//fg.addPredSucc(featureModel, newFeature, window, "Stack[0]", "pred");
	System.out.println("  add InputColumn("+window+","+predSucc+"("+inputStack+"))");
	try {
		BufferedReader br = new BufferedReader(new FileReader(featureModel));
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
		boolean used=false;
		int count=0;
		while(br.ready()) {
			String line=br.readLine();
			if (line.contains("InputColumn("+window) && line.contains("inputStack")) {
					used=true;
					bw.write(line+"\n");
					//<feature>OutputColumn(DEPREL, Stack[0])</feature>
					StringTokenizer st=new StringTokenizer(line,",");
					String s="";
					if (st.hasMoreTokens())
						s=st.nextToken();
					if (st.hasMoreTokens())
					s=st.nextToken();
					s=s.replaceAll("</feature>", "");
					s=s.substring(1,s.length());
					String newLine="";
					newLine="\t\t<feature>InputColumn(POSTAG, "+predSucc+"("+s+")"+"</feature>\n";	
					bw.write(newLine);	
				}
				else bw.write(line+"\n");
		}
		bw.close();
	} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	catch (IOException e1) {
	// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
}

public boolean removeMergeDeprelWindow(String featureModel, String newFeature, String window, String InputLookAhead,int i) {
	// TODO Auto-generated method stub
		//int max=findMaxInput(window, featureModel,InputLookAhead);
		//System.out.println("step 2");
		//System.out.println("  rm merge "+window);
		int count=0;	
		boolean removed=false;
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			
			while(br.ready()) {
				String line=br.readLine();
				if (line.contains("OutputColumn("+window) && line.contains("Merge") && !removed) {
					count++;
					
					if (count==i) {
						removed=true;
						//System.out.println("lnea:"+line);
						String s=line.replaceAll("<feature>","");
						s=s.replaceAll("</feature>","");
						s=s.replaceAll("\t", "");
						if (s.equals(""))  return false;
						System.out.println("  rm "+s);
					}
					else bw.write(line+"\n");
				}
				else bw.write(line+"\n");
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (removed) return true;
		return false;
	}

public void addInputWindow(String featureModel, String newFeature, String window, String inputLookAhead,String inOrOut) {
	// TODO Auto-generated method stub
		int max=findMaxInput(window, featureModel,inputLookAhead);
		int v=max+1;
		//System.out.println("  add ("+window+","+inputLookAhead+"["+v+"])");
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				bw.write(line+"\n");
				int val=max+1;
				if (line.contains("("+window+", "+inputLookAhead+"["+max)) {
					if ((line.contains("Merge") && (!line.contains("Merge3")))) {
						//<feature>Merge(InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Input[0]))</feature>
						bw.write("\t\t<feature>Merge("+inOrOut+"("+window+", "+inputLookAhead+"["+val+"]), InputColumn("+window+", "+inputLookAhead+"["+val+"]))</feature>\n");
						//System.out.println("  add Merge("+inOrOut+"("+window+", "+inputLookAhead+"["+val+"]), InputColumn("+window+", "+inputLookAhead+"["+val+"]))");
					}
					else if (line.contains("Merge3")) {
						//<feature>Merge3(InputColumn(POSTAG, Stack[1]), InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Input[0]))</feature>
						bw.write("\t\t<feature>Merge3("+inOrOut+"("+window+", "+inputLookAhead+"["+val+"]), InputColumn("+window+", "+inputLookAhead+"["+max+"]), InputColumn("+window+", "+inputLookAhead+"["+max+"]))</feature>\n");
						//System.out.println("  add Merge3("+inOrOut+"("+window+", "+inputLookAhead+"["+val+"]), InputColumn("+window+", "+inputLookAhead+"["+max+"]), InputColumn("+window+", "+inputLookAhead+"["+max+"]))");
					}
					else {
						//<feature>InputColumn(POSTAG, Stack[1])</feature>
						bw.write("\t\t<feature>"+inOrOut+"("+window+", "+inputLookAhead+"["+val+"])</feature>\n");
						System.out.println("  add "+inOrOut+"("+window+", "+inputLookAhead+"["+val+"])");
					}
					
				}
				
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

public boolean addInputWindowSpecialCase(String featureModel, String newFeature, String window, String inputLookAhead,String inOrOut) {
	// TODO Auto-generated method stub
		int max=findMaxInput(window, featureModel,"Input");
		int v=max+1;
		//System.out.println("  add "+inOrOut+"("+window+", Input["+v+"])");
		boolean algunoEncaja=false;
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();		
				int val=max+1;
				bw.write(line+"\n");
				if (line.contains("("+window+", Input["+max)) {
						//<feature>InputColumn(POSTAG, Stack[1])</feature>
						bw.write("\t\t<feature>"+inOrOut+"("+window+", Input["+val+"])</feature>\n");
						System.out.println("  add "+inOrOut+"("+window+", Input["+val+"])");
						algunoEncaja=true;
						
					}
				//else return false;
				/*else {
					if (line.contains("</featuremodel>")) {
						//<feature>InputColumn(POSTAG, Stack[1])</feature>
						bw.write("\t\t<feature>"+inOrOut+"("+window+", Input["+val+"])</feature>\n");
					}*/ 
				
				
				}

			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("AlgunoEncaja:"+algunoEncaja);
		if (algunoEncaja) return true;
		return false;
	}

public void addLeftContextWindowSpecialCase(String featureModel, String newFeature, String window, String inputLookAhead,String inOrOut) {
	// TODO Auto-generated method stub
		int max=findMaxInput(window, featureModel,"LeftContext");
		int v=max+1;
		//System.out.println("  add ("+window+",LeftContext["+v+"])");
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				bw.write(line+"\n");
				int val=max+1;
				if (line.contains("("+window+", LeftContext["+max)) {
						//<feature>InputColumn(POSTAG, Stack[1])</feature>
						bw.write("\t\t<feature>"+inOrOut+"("+window+", LeftContext["+val+"])</feature>\n");
						System.out.println("  add "+inOrOut+"("+window+", LeftContext["+val+"])");
					}
				else {
					if (line.contains("</featuremodel>")) {
						//<feature>InputColumn(POSTAG, Stack[1])</feature>
						bw.write("\t\t<feature>"+inOrOut+"("+window+", LeftContext["+val+"])</feature>\n");
						System.out.println("  add "+inOrOut+"("+window+", LeftContext["+val+"])");
					} 
				}
					
				
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

public void addRightContextWindowSpecialCase(String featureModel, String newFeature, String window, String inputLookAhead,String inOrOut) {
	// TODO Auto-generated method stub
		int max=findMaxInput(window, featureModel,"RightContext");
		int v=max+1;
		//System.out.println("  add ("+window+",RightContext["+v+"])");
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				bw.write(line+"\n");
				int val=max+1;
				if (line.contains("("+window+", RightContext["+max)) {
						//<feature>InputColumn(POSTAG, Stack[1])</feature>
						bw.write("\t\t<feature>"+inOrOut+"("+window+", RightContext["+val+"])</feature>\n");
						System.out.println("  add "+inOrOut+"("+window+", RightContext["+val+"])");
					}
				else {
					if (line.contains("</featuremodel>")) {
						//<feature>InputColumn(POSTAG, Stack[1])</feature>
						bw.write("\t\t<feature>"+inOrOut+"("+window+", RightContext["+val+"])</feature>\n");
						System.out.println("  add "+inOrOut+"("+window+", RightContext["+val+"]");
								
					} 
				}
					
				
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

public ArrayList<Integer> getListOfValuesFeatures(String featureModel, String window, String stackInputLookAhead) {
	// TODO Auto-generated method stub
		ArrayList<Integer> alist=new ArrayList<Integer>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
		
			while(br.ready()) {
				String line=br.readLine();
			
				if (line.contains("("+window+", "+stackInputLookAhead+"[")) {
					//<feature>InputColumn(FORM, Stack[0])</feature>
					StringTokenizer st=new StringTokenizer(line, "[");
					String valString="";
					if (st.hasMoreTokens()) st.nextToken();
					if (st.hasMoreTokens()) valString=st.nextToken();
					valString=valString.substring(0,1);
					Integer i=Integer.parseInt(valString);
					alist.add(i);
				}
			}
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	return alist;
}

public void addMergeFeatures(String featureModel, String newFeature, String window1, String window2, String inputLookAheadStack, String inOrOut, int val) {
	// TODO Auto-generated method stubint val=max+1;
	String structure="Stack";
	if (Optimizer.bestAlgorithm.contains("cov")) {
		structure="Left";
	}
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				bw.write(line+"\n");
				if (line.contains("("+window2+", "+inputLookAheadStack+"["+val) && (!line.contains("Split"))) {
					String stackInput=inputLookAheadStack;
					int valAux=val;
					if (Optimizer.bestAlgorithm.contains("stack")){
						if (inputLookAheadStack.equals("LookAhead")) {
							if (val==0) {
								stackInput=structure;
								valAux=0;
							}
							else {
								valAux=val-1;
								stackInput="LookAhead";
							}
						}
						if (inputLookAheadStack.equals(structure)) {
							valAux=val+1;
						}
					}
					//System.out.println("  add merge("+window1+","+window2+")("+valAux+") ("+stackInput+")");
					//<feature>Merge(InputColumn(POSTAG, Stack[0]), InputColumn(POSTAG, Input[0]))</feature>
					if (isNotIncluded("\t\t<feature>Merge("+inOrOut+"("+window1+", "+stackInput+"["+valAux+"]), InputColumn("+window2+", "+stackInput+"["+valAux+"]))</feature>\n",featureModel)) {
						bw.write("\t\t<feature>Merge("+inOrOut+"("+window1+", "+stackInput+"["+valAux+"]), InputColumn("+window2+", "+stackInput+"["+valAux+"]))</feature>\n");
						System.out.println("  add Merge("+inOrOut+"("+window1+", "+stackInput+"["+valAux+"]), InputColumn("+window2+", "+stackInput+"["+valAux+"]))");
					}
					
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

public void addMergeFeaturesS0(String featureModel, String newFeature, String window1, String window2, String inputLookAheadStack, String inOrOut, int val) {
	// TODO Auto-generated method stub
	String structure="Stack";
	if (Optimizer.bestAlgorithm.contains("cov")) {
		structure="Left";
	}
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				bw.write(line+"\n");
				if (line.contains("("+window2+", "+inputLookAheadStack+"["+val) && (!line.contains("Split"))&& (!line.contains("Merge"))) {
					//System.out.println(line);
					String stackInput=inputLookAheadStack;
					int valAux=val;
					if (Optimizer.bestAlgorithm.contains("stack")){
						if (inputLookAheadStack.equals("LookAhead")) {
							if (val==0) {
								stackInput=structure;
								valAux=0;
							}
							else {
								valAux=val-1;
								stackInput="LookAhead";
							}
						}
						if (inputLookAheadStack.equals(structure)) {
							valAux=val+1;
						}
					}
					if (Optimizer.bestAlgorithm.contains("stack")) {
						//System.out.println("  add merge("+window1+" S(1),"+window2+") ("+valAux+")("+stackInput+")");
						bw.write("\t\t<feature>Merge("+inOrOut+"("+window1+", Stack[1]), "+inOrOut+"("+window2+", "+stackInput+"["+valAux+"]))</feature>\n");
						System.out.println("  add Merge("+inOrOut+"("+window1+", Stack[1]), "+inOrOut+"("+window2+", "+stackInput+"["+valAux+"])");
					}
					else {
						//System.out.println("  add merge("+window1+" S(0),"+window2+") ("+valAux+")("+stackInput+")");
						bw.write("\t\t<feature>Merge("+inOrOut+"("+window1+", "+structure+"[0]), "+inOrOut+"("+window2+", "+stackInput+"["+valAux+"]))</feature>\n");
						System.out.println("  add Merge("+inOrOut+"("+window1+", "+structure+"[0]), "+inOrOut+"("+window2+", "+stackInput+"["+valAux+"])");
					}
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
	}

public void addMergeFeaturesI0(String featureModel, String newFeature, String window1, String window2, String inputLookAheadStack, String inOrOut, int val) {
	// TODO Auto-generated method stub
	String structure="Stack";
	if (Optimizer.bestAlgorithm.contains("cov")) {
		structure="Left";
	}
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				bw.write(line+"\n");
				if (line.contains("("+window2+", "+inputLookAheadStack+"["+val) && (!line.contains("Split"))&& (!line.contains("Merge"))) {
					String stackInput=inputLookAheadStack;
					int valAux=val;
					if (Optimizer.bestAlgorithm.contains("stack")){
						if (inputLookAheadStack.equals("LookAhead")) {
							if (val==0) {
								stackInput=structure;
								valAux=0;
							}
							else {
								valAux=val-1;
								stackInput="LookAhead";
							}
						}
						if (inputLookAheadStack.equals("Stack")) {
							valAux=val+1;
						}
					}
					if (Optimizer.bestAlgorithm.contains("stack")) {
						//System.out.println("  add merge("+window1+" S(0),"+window2+") ("+valAux+")("+stackInput+")");
						bw.write("\t\t<feature>Merge("+inOrOut+"("+window1+", "+structure+"[0]), "+inOrOut+"("+window2+", "+stackInput+"["+valAux+"]))</feature>\n");
						System.out.println("  add Merge("+inOrOut+"("+window1+", "+structure+"[0]), "+inOrOut+"("+window2+", "+stackInput+"["+valAux+"]))");
					}
					else {
						//System.out.println("  add merge("+window1+" I(0),"+window2+") ("+valAux+") ("+stackInput+")");
						bw.write("\t\t<feature>Merge("+inOrOut+"("+window1+", "+Optimizer.InputLookAhead+"[0]), "+inOrOut+"("+window2+", "+stackInput+"["+valAux+"]))</feature>\n");
						System.out.println("  add Merge("+inOrOut+"("+window1+", "+Optimizer.InputLookAhead+"[0]), "+inOrOut+"("+window2+", "+stackInput+"["+valAux+"]))");
					}
				}
			}
			bw.close();
			//System.exit(0);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
	}

public void addMergeFeaturesMerge3(String featureModel, String newFeature, String window1, String window2, String inputLookAheadStack, String inOrOut, int val) {
	// TODO Auto-generated method stub
	String structure="Stack";
	if (Optimizer.bestAlgorithm.contains("cov")) {
		structure="Left";
	}
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				bw.write(line+"\n");
				if (line.contains("("+window2+", "+inputLookAheadStack+"["+val) && (!line.contains("Split"))&& (!line.contains("Merge"))) {
					
					String stackInput=inputLookAheadStack;
					int valAux=val;
					if (Optimizer.bestAlgorithm.contains("stack")){
						if (inputLookAheadStack.equals("LookAhead")) {
							if (val==0) {
								stackInput="Stack";
								valAux=0;
							}
							else {
								valAux=val-1;
								stackInput="LookAhead";
							}
						}
						if (inputLookAheadStack.equals(structure)) {
							valAux=val+1;
						}
					}
					if (Optimizer.bestAlgorithm.contains("stack")) {
						//System.out.println("  add merge3("+window1+" Merge(S(0),S(1)),"+window2+") ("+valAux+")");
						bw.write("\t\t<feature>Merge3("+inOrOut+"("+window1+", "+structure+"[0]), "+inOrOut+"("+window1+", "+structure+"[1]), InputColumn("+window2+", "+stackInput+"["+valAux+"]))</feature>\n");
						System.out.println("  add Merge3("+inOrOut+"("+window1+", "+structure+"[0]), "+inOrOut+"("+window1+", "+structure+"[1]), InputColumn("+window2+", "+stackInput+"["+valAux+"]))");
					}
					else {
						//System.out.println("  add merge3("+window1+" Merge(I(0),S(0)),"+window2+") ("+val+")");
						bw.write("\t\t<feature>Merge3("+inOrOut+"("+window1+", "+Optimizer.InputLookAhead+"[0]), "+inOrOut+"("+window1+", "+structure+"[0]), InputColumn("+window2+", "+inputLookAheadStack+"["+val+"]))</feature>\n");
						System.out.println("  add Merge3("+inOrOut+"("+window1+", "+Optimizer.InputLookAhead+"[0]), "+inOrOut+"("+window1+", "+structure+"[0]), InputColumn("+window2+", "+inputLookAheadStack+"["+val+"]))");
					}
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
	}

public void addMergeFeaturesMerge3SpecialCase(String featureModel, String newFeature, String window1,int val,String inOrOut) {
	// TODO Auto-generated method stub
		//System.out.println("...............");
		System.out.println("  add Merge3("+inOrOut+"("+window1+", Stack[0]), "+inOrOut+"("+window1+", Input[0]),"+inOrOut+"("+window1+", LookAhead[0]))");
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				if (line.contains("</featureModel>")) {
					bw.write("\t\t<feature>Merge3("+inOrOut+"("+window1+", Stack[0]), "+inOrOut+"("+window1+", Input[0]),"+inOrOut+"("+window1+", LookAhead[0]))</feature>\n");
					//System.out.println("  add Merge3("+inOrOut+"("+window1+", Stack[0]), "+inOrOut+"("+window1+", Input[0]),"+inOrOut+"("+window1+", LookAhead[0]))");
				}
				bw.write(line+"\n");
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
	}

public void addFeature(String featureModel, String newFeature, String window, String value) {
	// TODO Auto-generated method stub
	//System.out.println("  add ("+window+","+value+")");
	try {
		BufferedReader br = new BufferedReader(new FileReader(featureModel));
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
		while(br.ready()) {
			String line=br.readLine();
			

			if (line.contains("</featuremodel>")) {
				//<feature>InputColumn(FORM, Stack[0])</feature>
				String nLinea="\t\t<feature>InputColumn("+window+", "+value+")</feature>";
				System.out.println("  add InputColumn("+window+", "+value+")");
				bw.write(nLinea+"\n");
			}
			//bw.write(line+"\n");
			bw.write(line+"\n");
			
		}
		bw.close();
	} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	catch (IOException e1) {
	// TODO Auto-generated catch block
		e1.printStackTrace();
	}
}


public void addSplitFeature(String featureModel, String newFeature, String window, String value) {
	// TODO Auto-generated method stub
	//System.out.println("  add Split ("+window+","+value+")");
	try {
		BufferedReader br = new BufferedReader(new FileReader(featureModel));
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
		while(br.ready()) {
			String line=br.readLine();
			
			if (line.contains("</featuremodel>")) {
				//<feature>InputColumn(FORM, Stack[0])</feature>
				String nLinea="\t\t<feature>Split(InputColumn("+window+", "+value+"),"+"\\|"+")</feature>";
				System.out.println("  add Split(InputColumn("+window+", "+value+"),"+"|"+")");
				bw.write(nLinea+"\n");
			}
			//bw.write(line+"\n");
			bw.write(line+"\n");
			
		}
		bw.close();
	} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	catch (IOException e1) {
	// TODO Auto-generated catch block
		e1.printStackTrace();
	}
}




public void addHeadIterativeWindow(String featureModel, String newFeature, String window, String inputLookAhead,String inOrOut) {
	// TODO Auto-generated method stub
	//<feature>InputColumn(FORM, head(Stack[0]))</feature>
	String structure="Stack";
	if (Optimizer.bestAlgorithm.contains("cov")) {
		structure="Left";
	}
		//System.out.println("  add ("+window+",...head("+structure+"[0])");
	
		boolean existeAlguno=false;
		
		String toAddAfter=findMaxHeadIterativeStack(window, featureModel);
		//System.out.println("toAdd"+toAddAfter);
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {

				String line=br.readLine();
				bw.write(line+"\n");
				if (line.contains(window) && line.contains("head("+structure+"[")) {
					if (line.equals(toAddAfter)) {
						String newS="";
						int headCounter=0;
						boolean added=false;
						StringTokenizer st=new StringTokenizer(toAddAfter,"(");
						while(st.hasMoreTokens()) {
							String s=st.nextToken();

							if (s.contains("head")) {
								if (!(newS.charAt(newS.length()-1)=='(')) {
									newS+="("+s;
								}
								else {
									newS+=s;
								}
								if (!added) {
									if (s.equals("head")) newS+="head(";
									else newS+="(head(";
									added=true;
								}
								else {
									if (!(newS.charAt(newS.length()-1)=='(')) {
										newS+="(";
									}
								}
								headCounter++;
							}
							else { 

								newS+=s;
							}
						}
					
						newS=newS.replace("</feature>", "");
						newS+=")";
						newS+="</feature>";
						
						if (newS.equals("")) {
							newS="\t<feature>InputColumn(FORM, head("+structure+"[0]))</feature>\n";
						}
						String s=newS.replace("</feature>","");
						s=s.replace("<feature>","");
						s=s.replace("\t","");
						System.out.println("  add "+s);
						bw.write(newS+"\n");
						existeAlguno=true;
					}
					
				}
				
				
			}
			if (!existeAlguno) {
				
				String newS="\t\t<feature>InputColumn(FORM, head("+structure+"[0]))</feature>\n";
				
				String s=newS.replace("</feature>","");
				s=s.replace("<feature>","");
				s=s.replace("\t","");
				System.out.println("  add "+s);
				
				BufferedReader br2 = new BufferedReader(new FileReader(featureModel));
				BufferedWriter bw2 = new BufferedWriter(new FileWriter(newFeature));
				while(br2.ready()) {

					String line=br2.readLine();
					if (!line.contains("</featuremodel>")) {
						bw2.write(line+"\n");
					}
					else {
						bw2.write(newS);
						bw2.write(line+"\n");
					}
					
				}
			bw2.close();
			}
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

public boolean removeIterativeWindow(String featureModel, String newFeature, String window, String inputLookAhead,String inOrOut) {
	// TODO Auto-generated method stub
	//<feature>InputColumn(FORM, head(Stack[0]))</feature>
		String structure="Stack";
		if (Optimizer.bestAlgorithm.contains("cov")) {
			structure="Left";
		}
		//System.out.println("  rm  ("+window+" head("+structure+"))");
		String toRemove=findMaxHeadIterativeStack(window,featureModel);
		if (toRemove.equals("")) return false;
	
		String s=toRemove.replaceAll("<feature>","");
		s=s.replaceAll("</feature>","");
		s=s.replaceAll("\t","");
		if (!s.equals("")) System.out.println("  rm "+s);
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
			while(br.ready()) {
				String line=br.readLine();
				if (line.contains("("+window+", head("+structure+"[")) {
					if (!line.equals(toRemove)) {
						bw.write(line+"\n");
					}
					else {
						
					}
				}
				else {
					bw.write(line+"\n");
				}
				
			}
			bw.close();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
		// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}


private String findMaxHeadIterativeStack(String window, String featureModel) {
	// TODO Auto-generated method stub
	int max=0;
	String maxS="";
	String structure="Stack";
	if (Optimizer.bestAlgorithm.contains("cov")) {
		structure="Left";
	}
	try {
		BufferedReader br = new BufferedReader(new FileReader(featureModel));
		while(br.ready()) {
			String line=br.readLine();
			//System.out.println(line);
			if (line.contains(window) && line.contains("head("+structure+"[")) {
				StringTokenizer st=new StringTokenizer(line,"(");
				int val=0;
				while(st.hasMoreTokens()) {
					String sst=st.nextToken();
					//System.out.println("SST:"+sst);
					if (sst.contains("head"))
						val++;
				}
				if (val>max) { 
					max=val;
					maxS=line;
				}
			}
		}
	} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
		e.printStackTrace();
	}
	catch (IOException e1) {
		// TODO Auto-generated catch block
	e1.printStackTrace();
	}
	return maxS;
}






public int findMaxStack(String window, String featureModel) {
	// TODO Auto-generated method stub
		int max=-1;
		String structure="Stack";
		if (Optimizer.bestAlgorithm.contains("cov")) {
			structure="Left";
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			while(br.ready()) {
				String line=br.readLine();
				if (line.contains("("+window+", "+structure+"[") && (!line.contains("Merge"))) {
					String ss[]=line.split("\\[");
					String s=ss[1].substring(0,1);
					int val=Integer.parseInt(s);
					if (val>max) 
						max=val;
				}
			}
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
		e1.printStackTrace();
		}
		return max;
	}

public int findMaxInput(String window, String featureModel, String InputLookAhead) {
	// TODO Auto-generated method stub
		int max=-1;
		try {
			BufferedReader br = new BufferedReader(new FileReader(featureModel));
			while(br.ready()) {
				String line=br.readLine();
				if (line.contains("("+window+", "+InputLookAhead+"[") && (!line.contains("Merge"))) {
					String ss[]=line.split("\\[");
					String s=ss[1].substring(0,1);
					int val=Integer.parseInt(s);
					if (val>max) 
						max=val;
				}
			}
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
		e1.printStackTrace();
		}
		return max;
	}



public void addRdepWindow(String featureModel, String newFeature, String window) {
	// TODO Auto-generated method stub
	
	try {
		BufferedReader br = new BufferedReader(new FileReader(featureModel));
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
		while(br.ready()) {
			String line=br.readLine();
			
			if (line.contains("</featuremodel>")) {
				//<feature>InputColumn(FORM, Stack[0])</feature>
				String nLinea="";
				if (window.equals("DEPREL")) {
					nLinea="\t\t<feature>OutputColumn("+window+",rdep(Stack[0]))</feature>";
					System.out.println("  add OutputColumn("+window+",rdep(Stack[0]))");
				}
				else { 
					nLinea="\t\t<feature>InputColumn("+window+",rdep(Stack[0]))</feature>";
					System.out.println("  add InputColumn("+window+",rdep(Stack[0]))");
				}
				bw.write(nLinea+"\n");
			}
			//bw.write(line+"\n");
			bw.write(line+"\n");
			
		}
		bw.close();
	} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	catch (IOException e1) {
	// TODO Auto-generated catch block
		e1.printStackTrace();
	}
}


//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

public ArrayList<String> removeAllStack(String featureModel, String newFeature,
		String window) {
	// TODO Auto-generated method stub
	ArrayList<String> pool=new ArrayList<String>();
	String structure="Stack";
	if (Optimizer.bestAlgorithm.contains("cov")) {
		structure="Left";
	}
	
	try {
		BufferedReader br = new BufferedReader(new FileReader(featureModel));
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
		while(br.ready()) {
			String line=br.readLine();
			
			if (line.contains(structure) && line.contains(window)) {
				if (!line.contains("Merge")) {
					line=line.replace("\t","");
					pool.add(line);
				}
			}
			else {
			//bw.write(line+"\n");
				bw.write(line+"\n");
			}
			
		}
		bw.close();
	} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	catch (IOException e1) {
	// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	return pool;
}

public void emptyFeatureModel(String featureModel, String newFeature) {
	// TODO Auto-generated method stub
	
	try {
		BufferedReader br = new BufferedReader(new FileReader(featureModel));
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
		while(br.ready()) {
			String line=br.readLine();
			
			if (line.contains("featuremodel")) {
				bw.write(line+"\n");
			}
		}
		bw.close();
	} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	catch (IOException e1) {
	// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
}

public static void main(String[] args) {
	FeatureGenerator f=new FeatureGenerator();
	Optimizer.bestAlgorithm="nivreeager";
	System.out.println(f.removeAllStack("NivreEager.xml", "asndj1.xml","POSTAG"));
}

public void addFeatureLine(String featureModel, String newFeature, String newLines) {
	// TODO Auto-generated method stub
	try {
		BufferedReader br = new BufferedReader(new FileReader(featureModel));
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
		while(br.ready()) {
			String line=br.readLine();
			
			if (line.contains("</featuremodel>")) {
				bw.write(newLines+"\n");
				bw.write(line+"\n");
			}
			else {
				bw.write(line+"\n");
			}
		}
		bw.close();
	} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	catch (IOException e1) {
	// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
}

public void addFeatureLineBefore(String featureModel, String newFeature, String newLines, String cad1, String cad2) {
	// TODO Auto-generated method stub
	try {
		BufferedReader br = new BufferedReader(new FileReader(featureModel));
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFeature));
		boolean ant=false;
		while(br.ready()) {
			String line=br.readLine();
			
			if (line.contains(cad1) && line.contains(cad2)) {
				ant=true;
				bw.write(line+"\n");
			}
			else {
				if (ant) {
					bw.write(newLines+"\n");
					ant=false;
				}
				bw.write(line+"\n");
			}
		}
		bw.close();
	} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	catch (IOException e1) {
	// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
}

}
	