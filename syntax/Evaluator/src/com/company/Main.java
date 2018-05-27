package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {

    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("Error: Invalid argumnet number");
            return;
        }
        Double acc = evalLAS(args[0], args[1]);
        System.out.println("Accuracy: "+(acc+""));
    }

    public static double evalLAS(String testCorpus, String goldStandard) {
        //System.out.println(testCorpus);
        //System.out.println(goldStandard);
        Double v=0.0;
        String script = "eval.pl";
//        String script="eval07.pl";
//        if (!Optimizer.includePunctuation) {
//            script="eval.pl";
//        }

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
}
