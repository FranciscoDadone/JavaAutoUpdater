package com.franciscodadone.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.swing.JFrame;

import org.json.JSONObject;
import java.io.Reader;
import org.json.JSONException;

import com.franciscodadone.vistas.DownloadingScreen;
import com.franciscodadone.vistas.MainFrame;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class MainApp {
	// CONSTANTES

	private final static String APP_NAME = "Chatty";
	private final static String JSON_URL = "https://api.github.com/repos/FranciscoDadone/Chatty/commits/update"; // github api, replace this params in your case. "https://api.github.com/repos/:githubUsername/:githubRepo/commits/:githubBranch"
	private final static String REPO_URL = "https://github.com/FranciscoDadone/Chatty/archive/update.zip";
	private final static String ZIP_DIRECTORY = "./Update.zip"; // this is a temp file for later decompression
	private final static String FINAL_APP_DECOMPRESSION_DIRECTORY = "."; //this is where the final app will be decompressed
	private final static String FINAL_APP_EXECUTABLE = "java -jar Chatty-update/App.jar";
	private final static String FILE_TO_DETECT_UPDATES = "lastCommit.txt";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		frame = new MainFrame();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setTitle(APP_NAME + " Updater");
		
		
		// ------------------- //
		// Get the last commit ID
		
		JSONObject json;
		try {
			json = readJsonFromUrl(JSON_URL);
			lastCommitID = (String) json.get("sha");
		    System.out.println("Github last commit ID: " + lastCommitID);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
		if(!getCommitID().trim().equals(lastCommitID)) {
			//DOWNLOADING FILES...
	    	System.out.println("Update found!");
	    	ds = new DownloadingScreen();
	    	ds.setVisible(true);
	    	ds.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	ds.setResizable(false);
	    	ds.setTitle(APP_NAME + " Updater");
	    	frame.dispose();
	    	try (BufferedInputStream inputStream = new BufferedInputStream(new URL(REPO_URL).openStream());
	    			  FileOutputStream fileOS = new FileOutputStream(ZIP_DIRECTORY)) {
	    			    byte data[] = new byte[1024];
	    			    int byteContent;
	    			    while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
	    			        fileOS.write(data, 0, byteContent);
	    			    }
	    			    System.out.println("Download finished");
	    			} catch (IOException e) {
	    			    e.printStackTrace();
	    	}
	    	
	    	try {
				unzip(ZIP_DIRECTORY, FINAL_APP_DECOMPRESSION_DIRECTORY, "");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	try {
				Runtime.getRuntime().exec(FINAL_APP_EXECUTABLE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	File updateFile = new File(ZIP_DIRECTORY);
	    	updateFile.delete();
	    	
	    	newCommitID(lastCommitID);
	    	
		} else {
			System.out.println("No updates found!");
			try {
				Runtime.getRuntime().exec(FINAL_APP_EXECUTABLE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			frame.dispose();
		}
    	
	}
	
	// READING JSON FROM GITHUB API ------------- //
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }

	  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }
	  // ------------------------- //
	  
	  
	  private static void newCommitID(String commitID) {
		  try {
	            FileWriter fileWriter = new FileWriter(FILE_TO_DETECT_UPDATES);

	            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	            
	            bufferedWriter.write(commitID);
	            System.out.println("CommitID updated '" + commitID + "'");
	            bufferedWriter.close();
	        }
	        catch(Exception ex) {
	            System.out.println(
	                "Error updating the commit ID '" + FILE_TO_DETECT_UPDATES + "'");
	        } finally {
	        	
	        	ds.dispose();	//kills the downloading screen and boots the actual app
	        
	        }
		  
	  }
	  
	  private static String getCommitID() {
		  
		  try {
			  BufferedReader br = new BufferedReader(new FileReader(FILE_TO_DETECT_UPDATES));
		      StringBuilder sb = new StringBuilder();
		      String line = br.readLine();

		      while (line != null) {
		          sb.append(line);
		          sb.append(System.lineSeparator());
		          line = br.readLine();
		      }
		      String everything = sb.toString();
		      br.close();
		      return everything;
		  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  return "";
	  }
	  
	  public static void unzip(String source, String destination, String password){

		    try {
		         ZipFile zipFile = new ZipFile(source);
		         if (zipFile.isEncrypted()) {
		            zipFile.setPassword(password);
		         }
		         zipFile.extractAll(destination);
		    } catch (ZipException e) {
		        e.printStackTrace();
		    }
		}
	  
	  private static String lastCommitID;
	  private static MainFrame frame;
	  private static DownloadingScreen ds;
	  
}
