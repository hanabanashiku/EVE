package edu.oakland.eve.test;

import com.google.api.services.calendar.model.*;
import org.junit.*;
import edu.oakland.eve.api.CalendarAPI;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class CalendarAPITest {

    CalendarAPI cal;
    @Before
    public void setUp() throws Exception {
        cal = new CalendarAPI();
    }

    @Test
    public void fetchCalendars() throws Exception {
        CalendarList list = cal.fetchCalendars();
        Assert.assertNotNull(list);
    }

    @Test
    public void addDeleteCalendar() throws Exception {
        Calendar testcal = cal.addCalendar("Test");
        Assert.assertNotNull(testcal);
        Assert.assertTrue(cal.fetchCalendar(testcal.getId()).getSummary().equals("Test"));
        cal.deleteCalendar(testcal.getId());
        Assert.assertNull(cal.fetchCalendar(testcal.getId()));
    }

    @Test
    public void updateCalendar() throws Exception {
        Calendar testcal = cal.addCalendar("Test");
        Assert.assertNotNull(testcal);
        Assert.assertFalse(testcal.getDescription().equals("A test of the calendar system."));
        testcal.setDescription("A test of the calendar system.");
        cal.updateCalendar(testcal.getId(), testcal);
        testcal = cal.fetchCalendar(testcal.getId());
        Assert.assertTrue(testcal.getDescription().equals("A test of the calendar system."));
        cal.deleteCalendar(testcal.getId());
    }

    @Test
    public void addEvent() throws Exception {
        Event e = CalendarAPI.newEvent("Test Event", new Date(2017, 03, 26, 13, 0, 0), new Date(2017, 03, 26, 14, 0, 0));
        Calendar test = cal.addCalendar("Test");
        Assert.assertNotNull(test);
        Assert.assertNull(cal.fetchEvent(test.getId(), "Test Event"));
        cal.addEvent(test, e);
        Assert.assertNotNull(cal.fetchEvent(test.getId(), "Test Event"));
        cal.deleteCalendar(test.getId());
    }

}