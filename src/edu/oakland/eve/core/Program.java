package edu.oakland.eve.core;

import edu.oakland.eve.api.CalendarAPI;
import edu.oakland.eve.gui.MainWindow;
import edu.oakland.eve.rss.RSSClient;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import java.io.IOException;

/**
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Program {
    public static final String APP_NAME = "EVE";
    public static Settings settings;
    private static MainWindow mainw;
    public static CalendarAPI calendars;
    public static RSSClient rss;
    
	public static void main(String[] args) {
		settings = Settings.load();
		try{
			calendars = new CalendarAPI();
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(null,
					"Could not load calendars: " + e.getMessage(),
					"EVE Calendars", JOptionPane.ERROR_MESSAGE);
			return; // TODO: Decide how to handle execution without RSS/Calendars
		}
		try{
			rss = RSSClient.load();
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(null,
					"Could not load RSS feeds: " + e.getMessage(),
					"EVE RSS", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if(!System.getProperty("java.runtime.name").equalsIgnoreCase("android runtime")){
			mainw = new MainWindow();
		}

		/* ********************* *
		 *   ***  ANDROID  ***   *
		 * ********************* */
		else{
			// TODO: Android code here
			throw new NotImplementedException();
		}
	}

	/**
		Exit the application
	 */
	public void exit(){
		settings.save();

	}
}
