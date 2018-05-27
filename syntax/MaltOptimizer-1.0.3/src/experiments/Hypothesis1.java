/**
 * 
 */
package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author miguel
 *
 */
public class Hypothesis1 {
	
	private String corpus;
	
	
	
	public Hypothesis1() {}
	
	
	public void setCorpus(String corpus) {
		this.corpus=corpus;
	}
	
	/**
	Given a sentence in conll format count the right arcs for the sentence
	Given a sentence in conll format count the right parents for the sentence
	*/
	public void generateStatistics() {
		
		try {
			BufferedReader br=new BufferedReader(new FileReader(corpus));
			try {
				
				/*int contRightArcs=0;
				int contRightParents=0;
				HashMap<String,ArrayList<String>> tree=new HashMap<String,ArrayList<String>>();*/
				int contStructures2=0;
				double totalProportion=0.0;
				double numbSentences=0.0;
				
				ArrayList<String> rightParents=new ArrayList<String>();
				ArrayList<String> rightArcs=new ArrayList<String>();
				
				while(br.ready()) {
					String line=br.readLine();
					double proportion=0.0;
					if (!line.equals("")) {
						StringTokenizer st=new StringTokenizer(line,"\t");
						
						String id="";
						String parent="";
						
						//double proportion=0.0;
						
						
						int cont=1;
						while(st.hasMoreTokens()) {
							String tok=st.nextToken();
							if (cont==1) {
								id=tok;
							}
							if (cont==7) {
								parent=tok;
								
								Integer parentInt=Integer.parseInt(parent);
								Integer idInt=Integer.parseInt(id);
								
								if (idInt>parentInt) {
									if (!rightParents.contains(parentInt.toString())) {
										if (parentInt!=0)
										rightParents.add(parentInt.toString());
									}
									if (!rightArcs.contains(idInt.toString())) rightArcs.add(idInt.toString());
								}
							}
							cont++;
						}
						
						
						
						
					}
					else {
						if (rightParents.size()>0)
							proportion=(double)rightParents.size()/(double)rightArcs.size();
						System.out.println("rightParents ("+rightParents.size()+")/#rightArcs ("+rightArcs.size()+")="+proportion);
						System.out.println(rightArcs);
						System.out.println(rightParents);
						if (proportion==1.0) contStructures2++;
						totalProportion+=proportion;
						proportion=0.0;
						numbSentences+=1.0;
						rightParents=new ArrayList<String>();
						rightArcs=new ArrayList<String>();
						
					}
					
					
				}
				System.out.println("Exact Structures 2:" +contStructures2);
				System.out.println("totalProp/numbSenteces="+totalProportion/numbSentences);
				
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	public static void main(String[] args) {
		Hypothesis1 h1=new Hypothesis1();
		h1.setCorpus("/home/miguel/Escritorio/NewMaltParserOptimization/MaltParser_Optimization/czech_pdt_train.conll");
		h1.generateStatistics();
	}
	
	

}
