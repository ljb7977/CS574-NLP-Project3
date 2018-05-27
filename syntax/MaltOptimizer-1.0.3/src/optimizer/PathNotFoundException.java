/**
 * 
 */
package optimizer;

/**
 * @author miguel
 *
 */
public class PathNotFoundException extends Exception {
	
	public PathNotFoundException(){
		System.out.println("The Training set path used in Phase 1 is not the same as you are trying to use in Phase 2");
	}

}
