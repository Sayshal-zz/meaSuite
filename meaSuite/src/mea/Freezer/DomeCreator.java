/* meaSuite is copyright 2011/2012 of Turt2Live Programming and Sayshal Productions
 * 
 * Modifications of the code, or any use of the code must be preauthorized by Travis
 * Ralston (Original author) before any modifications can be used. If any code is 
 * authorized for use, this header must retain it's original state. The authors (Travis
 * Ralston and Tyler Heuman) can request your code at any time. Upon code request you have
 * 24 hours to present code before we will ask you to not use our code.
 * 
 * Contact information:
 * Travis Ralston
 * email: minecraft@turt2live.com
 * 
 * Tyler Heuman
 * email: contact@sayshal.com
 */
package mea.Freezer;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class DomeCreator {
	
	public void writeCSV(String location){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(location, false));
			out.write("(4,0,-1)..(4,0,0)..(4,0,1)..(4,1,-1)..(4,1,0)..(4,1,1)..(3,0,-2)..(3,0,-1)..(3,0,0)..(3,0,1)..(3,0,2)..(3,1,-2)..(3,1,2)..(3,2,-1)..(3,2,0)..(3,2,1)..(2,0,-3)..(2,0,-2)..(2,0,-1)..(2,0,0)..(2,0,1)..(2,0,2)..(2,0,3)..(2,1,-3)..(2,1,3)..(2,2,-2)..(2,2,2)..(2,3,-1)..(2,3,0)..(2,3,1)..(1,0,-4)..(1,0,-3)..(1,0,-2)..(1,0,-1)..(1,0,0)..(1,0,1)..(1,0,2)..(1,0,3)..(1,0,4)..(1,1,-4)..(1,1,4)..(1,2,-3)..(1,2,3)..(1,3,-2)..(1,3,2)..(1,4,-1)..(1,4,0)..(1,4,1)..(0,0,-4)..(0,0,-3)..(0,0,-2)..(0,0,-1)..(0,0,0)..(0,0,1)..(0,0,2)..(0,0,3)..(0,0,4)..(0,1,-4)..(0,1,4)..(0,2,-3)..(0,2,3)..(0,3,-2)..(0,3,2)..(0,4,-1)..(0,4,0)..(0,4,1)..(-1,0,-4)..(-1,0,-3)..(-1,0,-2)..(-1,0,-1)..(-1,0,0)..(-1,0,1)..(-1,0,2)..(-1,0,3)..(-1,0,4)..(-1,1,-4)..(-1,1,4)..(-1,2,-3)..(-1,2,3)..(-1,3,-2)..(-1,3,2)..(-1,4,-1)..(-1,4,0)..(-1,4,1)..(-2,0,-3)..(-2,0,-2)..(-2,0,-1)..(-2,0,0)..(-2,0,1)..(-2,0,2)..(-2,0,3)..(-2,1,-3)..(-2,1,3)..(-2,2,-2)..(-2,2,2)..(-2,3,-1)..(-2,3,0)..(-2,3,1)..(-3,0,-2)..(-3,0,-1)..(-3,0,0)..(-3,0,1)..(-3,0,2)..(-3,1,-2)..(-3,1,2)..(-3,2,-1)..(-3,2,0)..(-3,2,1)..(-4,0,-1)..(-4,0,0)..(-4,0,1)..(-4,1,-1)..(-4,1,0)..(-4,1,1)");
			out.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
