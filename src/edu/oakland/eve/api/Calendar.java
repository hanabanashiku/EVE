package edu.oakland.eve.api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/***
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */

/***
 * A class for managing Google Calendar API calls
 */

public class Calendar{
	private static final File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".credentials/eve-calendar");
	private static FileDataStoreFactory dataFactory;
	private static final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
	private static HttpTransport http;
	private static final List<String> SCOPES =
			Arrays.asList(CalendarScopes.CALENDAR);

	static{
		try{
			http = GoogleNetHttpTransport.newTrustedTransport();
			dataFactory = new FileDataStoreFactory(DATA_STORE_DIR);

		}
		catch(Throwable t){ // can be modified later
			t.printStackTrace();
			System.exit(1);
		}
	}

	public static Crendential authorize() throws IOException{

	}
}