package edu.oakland.eve.gui;

import com.google.api.services.calendar.model.Event;

import javax.swing.*;
import java.awt.*;

/**
 * Represents a single sell of a calendar
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 * TODO: Add event handlers to edit and display the event
 */
public class CalendarCell extends JPanel {
    private int Day;
    public int getDay() { return Day; }

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
        add(new JLabel(event.getSummary()), BorderLayout.CENTER);
    }
}
