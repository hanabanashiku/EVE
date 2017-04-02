package edu.oakland.eve.gui;

import com.google.api.services.calendar.model.Event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Represents a single sell of a calendar
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 * TODO: Add event handlers to edit and display the event
 */
public class CalendarCell extends JPanel {
    private int Day;
    private ArrayList<Event> events = new ArrayList<>();

    public int getDay() { return Day; }
    public ArrayList<Event> getEvents() { return events; }

    public CalendarCell(int day){
        Day = day;
        setLayout(new BorderLayout());
        add(new JLabel(Integer.toString(day)), BorderLayout.NORTH);
    }

    /**
     * Display a new event
     * @param event the event to display
     */
    public void addEvent(Event event){
        events.add(event);
        JLabel label = new JLabel(event.getSummary());
        this.setToolTipText(event.getSummary());
        add(label, BorderLayout.CENTER);
    }
}
