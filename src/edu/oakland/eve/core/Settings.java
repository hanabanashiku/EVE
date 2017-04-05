package edu.oakland.eve.core;

import java.io.*;

/**
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 * //TODO: resolve paths based on OS
 */
public class Settings implements Serializable {

	private String city;
	private char tempUnit;
	private static final String filename = "settings.bin";

	public String getCity() { return city; }

	public void setCity(String city) {
		this.city = city;
	}

	public char getTempUnit() { return tempUnit; }

	public void setTempUnit(char tempUnit) throws IllegalArgumentException{
		if(tempUnit != 'F' && tempUnit != 'C')
			throw new IllegalArgumentException("Invalid temperature unit.");
		this.tempUnit = tempUnit;
	}

	// put default values here
	private Settings(){
		city = "Detroit";
		tempUnit = 'F';
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
	* Save the settings instance to disk
	*/
	public void save(){
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
	protected void finalize() { save(); }
}
