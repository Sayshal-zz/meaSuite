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
package mea.External;

import java.io.*;
import java.net.*;

import mea.Logger.MeaLogger;

public class Download {
	public static double downloadSize = 0;
	public static double downloadAmount = 0;
	public static int lastDownloadPercent = 0;
	private File log = new File(System.getProperty("user.dir")+"/plugins/meaSuite/meaLogger/data_transfer.txt");
	public static String getDownloadStatus(){
		double pert = (downloadAmount/downloadSize)*100;
		int percent = (int) pert;
		return "Downloaded "+downloadAmount+" bytes of "+downloadSize+" bytes. "+percent+"%";
	}
	public static int getDownloadPercent(){
		return (int) ((downloadAmount/downloadSize)*100);
	}
	public int getDownloadStatusInt(){
		if(downloadAmount==downloadSize){
			return 1;
		}else{
			return 0;
		}
	}
	public static int getFileSize() throws Exception{
	    FileInputStream fstream = new FileInputStream("filesize.txt");
	    DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    int retValue = 0;
	    while ((strLine = br.readLine()) != null)   {
	     retValue = Integer.parseInt(strLine);
	    }
	    in.close();
	    return retValue;
	}
	
	 public Download(URL url, String filename, boolean println) throws Exception {
	        URLConnection con;  // represents a connection to the url we want to dl.
	        DataInputStream dis;  // input stream that will read data from the file.
	        FileOutputStream fos; //used to write data from inut stream to file.
	        byte[] fileData;  //byte aray used to hold data from downloaded file.
	        try {
	            con = url.openConnection(); // open the url connection.
	            dis = new DataInputStream(con.getInputStream()); // get a data stream from the url connection.
	            fileData = new byte[con.getContentLength()]; // determine how many byes the file size is and make array big enough to hold the data
	            //System.out.println("Getting URL and file information...");
	            downloadSize = fileData.length;
	            int t1 = 0;
	           // Thread.sleep(2000);
	            for (int x = 0; x < fileData.length; x++) { // fill byte array with bytes from the data input stream
	                fileData[x] = dis.readByte();
	                if(t1==0){
	                	t1=1;
	                	//System.out.println("Downloading File. This may take a while.");
	                }
	                downloadAmount = x+1;
	                if(getDownloadPercent()== 100){
	                	String parts[] = filename.split("\\/");
	                	String file = parts[parts.length-2];
	                	if(println){
	                		System.out.println(file+" :: "+getDownloadStatus());
	                		MeaLogger.log(file+" :: "+getDownloadStatus(), log);
	                	}
	                	lastDownloadPercent = getDownloadPercent();
	                }
	            }
	            dis.close(); // close the data input stream
	          //  System.out.println("Finishing download....");
	            //Thread.sleep(900);
	            fos = new FileOutputStream(new File(filename));  //create an object representing the file we want to save
	            fos.write(fileData);  // write out the file we want to save.
	            fos.close(); // close the output stream writer
	            //System.out.println("Download Complete.");
	        }
	        catch(MalformedURLException m) {
	            System.out.println(m);
	        }
	        catch(IOException io) {
	            System.out.println(io);
	        }
	    }
}
