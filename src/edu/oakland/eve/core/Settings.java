package edu.oakland.eve.core;

import java.io.*;

/**
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 * To do: resolve paths based on OS
 */
public class Settings implements Serializable {

	private static final String filename = "settings.bin";

	// put default values here
	private Settings(){

	}

	/**
	 * Load user settings
	 * @return The settings class saved to disk, or a new settings class, or null on failure.
	 */
	public static Settings load(){
		try{
			FileInputStream fin = new FileInputStream(filename);
			ObjectInputStream oin = new ObjectInputStream(fin);
			Settings settings = (Settings)oin.readObject();
			oin.close();
			fin.close();
			return settings;
		}
		catch(FileNotFoundException e)  { return new Settings(); }
		catch(IOException|ClassNotFoundException e){ return null;}
	}

	/***
	* Close the instance and save to disk
	*/
	public void close(){
		try{
			FileOutputStream fout = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeObject(this);
			out.close();
			fout.close();
		}
		catch(IOException e){ }
	}
	
	// called by the garbage collector
	protected void finalize() { close(); }

}
