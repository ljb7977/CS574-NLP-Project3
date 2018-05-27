package optimizer;

import java.util.ArrayList;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

/**
 * 
 * @author Miguel Ballesteros
 *
 */
public class LanguageDetector {
	
	String frase;
	

	public LanguageDetector(String frase) {
		// TODO Auto-generated constructor stub
		this.frase=frase;
	}

	 public void init(String profileDirectory) throws LangDetectException {
         DetectorFactory.loadProfile(profileDirectory);
     }
     public String detect(String text) throws LangDetectException {
         Detector detector = DetectorFactory.create();
         detector.append(text);
         return detector.detect();
     }
     public ArrayList detectLangs(String text) throws LangDetectException {
         Detector detector = DetectorFactory.create();
         detector.append(text);
         return detector.getProbabilities();
     }

	public String getLanguage() {
		// TODO Auto-generated method stub
		try {
			init("profiles");
			return detect(frase);
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		
	}
}