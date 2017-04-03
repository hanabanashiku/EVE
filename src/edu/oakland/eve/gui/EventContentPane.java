package edu.oakland.eve.gui;

import com.google.api.services.calendar.model.Calendar;
import edu.oakland.eve.api.CalendarAPI;

import javax.swing.*;
import javax.swing.border.Border;
import com.google.api.services.calendar.model.Event;
import edu.oakland.eve.core.Program;

import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Shows the contents of a Google Event
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class EventContentPane extends JPanel {
    JLabel label;
    JPanel buttons;
    JButton edit;
    JButton remove;
    Event event;
    Calendar cal;

    public EventContentPane(Calendar c){
        cal = c;
        setLayout(new BorderLayout());
        label = new JLabel();
        edit = new JButton("Edit");
        edit.setIcon(new ImageIcon(EventContentPane.class.getResource("resources/pencil.png")));
        remove = new JButton("Delete");
        remove.setIcon(new ImageIcon(EventContentPane.class.getResource("resources/trash.png")));
        remove.addActionListener(e -> deleteEvent());
        buttons = new JPanel();
        buttons.setLayout(new BorderLayout());
        buttons.add(edit, BorderLayout.WEST);
        buttons.add(remove, BorderLayout.EAST);
        add(label, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        setVisible(false);
    }

    public void update(Event e){
        event = e;
        String text = "<html>";
        text += "<center>" + e.getSummary() + "</center>";
        text += "Description: " + ((e.getDescription() == null) ? "[none]" : e.getDescription()) + "<br />";
        String start, end;
        if(CalendarAPI.getDateTime(e.getStart().getDateTime()) == null) {
            start = CalendarAPI.getDate(e.getStart().getDate()).format(DateTimeFormatter.ofPattern("EEE. MMMM dd, yyyy"));
            end = CalendarAPI.getDate(e.getEnd().getDate()).format(DateTimeFormatter.ofPattern("EEE. MMMM dd, yyyy"));
        }
        else {
            start = CalendarAPI.getDateTime(e.getStart().getDateTime()).format(DateTimeFormatter.ofPattern("EEE. MMMM dd, yyyy h:m a"));
            end = CalendarAPI.getDateTime(e.getEnd().getDateTime()).format(DateTimeFormatter.ofPattern("EEE. MMMM dd, yyyy h:m a"));
        }
        text += "Start: " + start + "<br/>";
        text += "End: " + end + "<br/>";
        text += "</html>";

        label.setText(text);
        setVisible(true);
    }

    /**
     * Delete the current event
     */
    public void deleteEvent(){
        int delete = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + event.getSummary() + "?", "EVE Calendars", JOptionPane.YES_NO_OPTION);
        if(delete == JOptionPane.YES_OPTION){
            try {
                Program.calendars.removeEvent(cal, event);
                setVisible(false);
                ((JCalendar)this.getParent()).populate();
            }
            catch(IOException e){
                JOptionPane.showMessageDialog(this, "Error removing event: " + e.getMessage(), "EVE Calendars", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


}
