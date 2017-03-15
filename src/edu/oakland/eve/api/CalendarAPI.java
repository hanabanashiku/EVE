package edu.oakland.eve.api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import edu.oakland.eve.core.Program;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.TimeZone;

/***
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */

/***
 * A class for managing Google Calendar API calls
 */

public class CalendarAPI{
	private static final File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".credentials/eve-calendar");
	private static FileDataStoreFactory dataFactory;
	private static final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
	private static HttpTransport http;
	private static final List<String> scopes =
			Arrays.asList(CalendarScopes.CALENDAR);

	private com.google.api.services.calendar.Calendar client;

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

	/***
	 * Crates a Credential object authorized by the Google API.
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException{
		InputStream in = Calendar.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets secrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

		GoogleAuthorizationCodeFlow flow =
				new GoogleAuthorizationCodeFlow.Builder(
						http, jsonFactory, secrets, scopes)
						.setDataStoreFactory(dataFactory)
						.setAccessType("offline")
						.build();
		Credential cred = new AuthorizationCodeInstalledApp(
				flow, new LocalServerReceiver()).authorize("user");
		return cred;
	}

	/***
	 * Build and return the authorized Calendar client service
	 * @return an authorized Calendar client service
	 * @throws IOException
	 */
	public static com.google.api.services.calendar.Calendar getCalendarService() throws IOException{
		Credential cred = authorize();
		return new com.google.api.services.calendar.Calendar.Builder(
				http, jsonFactory, cred).setApplicationName(Program.APP_NAME)
				.build();
	}

	public CalendarAPI(){
		try {
			client = getCalendarService();
		}
		catch(IOException e) { e.printStackTrace(); }
	}

	/***
	 * Gets a lists of all calendars in the client instance and returns it.
	 * @return List of calendars
	 * @throws IOException
	 */
	public CalendarList fetchCalendars() throws IOException{
		return client.calendarList().list().execute();
	}

	/***
	 * Add a new calendar to the client instance
	 * @return The newly-created calendar
	 * @throws IOException
	 */
	public Calendar addCalendar(String summary) throws IOException {
		Calendar cal = new Calendar();
		cal.setSummary(summary);
		return client.calendars().insert(cal).execute();
	}

	/***
	 *
	 * @param original The original calendar from the client
	 * @param update The update calendar to replace with
	 * @return The updated caledar from the client
	 * @throws IOException
	 */
	public Calendar updateCalendar(Calendar original, Calendar update) throws IOException{
		return client.calendars().patch(original.getId(), update).execute();
	}

	/***
	 * Add an event to a calendar
	 * @param calendar The calendar to add the event to
	 * @param event The event to add to the calendar
	 * @throws IOException
	 */
	public void addEvent(Calendar calendar, Event event) throws IOException{
		client.events().insert(calendar.getId(), event).execute();
	}

	/**
	 * Create a new event
	 * @param summary The event's summary
	 * @param start The event's start time
	 * @param end The event's end time
	 * @param timeZone The applicable timezone
	 * @return The new event
	 */
	public Event newEvent(String summary, Date start, Date end, TimeZone timeZone){
		Event event = new Event();
		event.setSummary(summary);
		event.setStart(new EventDateTime().setDateTime(new DateTime(start, timeZone)));
		event.setEnd(new EventDateTime().setDateTime(new DateTime(end, timeZone)));
		return event;
	}

	/**
	 * Create a new event using the system's default timezone
	 * @param summary The event's summary
	 * @param start The event's start time
	 * @param end The event's end time
	 * @return The new event
	 */
	public Event newEvent(String summary, Date start, Date end){ return newEvent(summary, start, end, TimeZone.getDefault()); }

	/**
	 * List all events from a calendar
	 * @param calendar
	 * @return An iterable list of events
	 * @throws IOException
	 */
	public Events showEvents(Calendar calendar) throws IOException{
		return client.events().list(calendar.getId()).execute();
	}

	/***
	 * Delete a calendar from the client
	 * @param calendar The calendar to delete
	 * @throws IOException
	 */
	public void deleteCalendar(Calendar calendar) throws IOException{
		client.calendars().delete(calendar.getId()).execute();
	}


}