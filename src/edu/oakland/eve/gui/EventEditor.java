package edu.oakland.eve.gui;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import edu.oakland.eve.api.CalendarAPI;
import edu.oakland.eve.core.Program;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventEditor extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField name;
    private JTextField desc;
    private JTextField location;
    private JButton buttonDelete;
    private JComboBox startmonth;
    private JComboBox startday;
    private JTextField startyear;
    private JComboBox starth;
    private JComboBox startap;
    private JComboBox endmonth;
    private JComboBox endday;
    private JTextField endyear;
    private JComboBox endhour;
    private JComboBox endap;
    private JCheckBox allDayBox;
    private JCalendar jCalendar;
    private Event event;

    public EventEditor(JCalendar jcal, Event ev) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Edit event");
        jCalendar = jcal;
        event = ev;

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        buttonDelete.addActionListener(e -> deleteEvent());
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        allDayBox.addChangeListener(e -> new boxListener());
        pack();

        // fill text boxes
        if(ev.getSummary() != null) name.setText(ev.getSummary());
        if(ev.getDescription() != null) desc.setText(ev.getDescription());
        if(ev.getLocation() != null) location.setText(ev.getLocation());
        //TODO: Fix dates not properly resolving
        if(ev.getStart().getDateTime() == null){
            allDayBox.setSelected(false);
            LocalDateTime start = CalendarAPI.getDateTime(ev.getStart().getDateTime());
            LocalDateTime end = CalendarAPI.getDateTime(ev.getEnd().getDateTime());
            if(start == null || end == null) return; // shouldn't be called
            startmonth.setSelectedIndex(start.getMonthValue() - 1);
            startday.setSelectedIndex(start.getDayOfMonth() - 1);
            startyear.setText(Integer.toString(start.getYear()));
            if(start.getHour() < 12){
                if(start.getHour() == 0) starth.setSelectedIndex(0);
                else starth.setSelectedIndex(start.getHour() - 1);
                startap.setSelectedIndex(0);
            }
            else{
                starth.setSelectedIndex(start.getHour() - 12 - 1);
                startap.setSelectedIndex(1);
            }
            endmonth.setSelectedIndex(end.getMonthValue() - 1);
            endday.setSelectedIndex(end.getDayOfMonth() - 1);
            endyear.setText(Integer.toString(end.getYear()));
            if(end.getHour() < 12){
                if(end.getHour() == 0) endhour.setSelectedIndex(0);
                else endhour.setSelectedIndex(start.getHour() - 1);
                startap.setSelectedIndex(0);
            }
            else{
                endhour.setSelectedIndex(end.getHour() - 12 - 1);
                endhour.setSelectedIndex(1);
            }
        }
        else {
            allDayBox.setSelected(true);
            LocalDate start = CalendarAPI.getDate(ev.getStart().getDate());
            LocalDate end = CalendarAPI.getDate(ev.getEnd().getDate());
            startmonth.setSelectedIndex(start.getMonthValue() - 1);
            startday.setSelectedIndex(start.getDayOfMonth() - 1);
            startyear.setText(Integer.toString(start.getYear()));
            endmonth.setSelectedIndex(end.getMonthValue() - 1);
            endday.setSelectedIndex(end.getDayOfMonth() - 1);
            endyear.setText(Integer.toString(end.getYear()));
        }
    }

    private void onOK() {
        if(!name.getText().isEmpty()) event.setSummary(name.getText());
        if(!desc.getText().isEmpty()) event.setDescription(desc.getText());
        if(!location.getText().isEmpty()) event.setLocation(location.getText());
        EventDateTime start = getStartTime();
        EventDateTime end = getEndTime();
        if(start == null || end == null) return;
        if(allDayBox.isSelected()){
            if(end.getDate().getValue() < start.getDate().getValue()){
                JOptionPane.showMessageDialog(this, "The end date is before the start date.", "EVE Calendars", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        else{
            if(end.getDateTime().getValue() <= start.getDateTime().getValue()){
                JOptionPane.showMessageDialog(this, "The end date is before the start date.", "EVE Calendars", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        event.setStart(start);
        event.setEnd(end);

        try{
            Program.calendars.updateEvent(jCalendar.getCalender(), event);
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(this, "Error updating event: " + e.getMessage(), "EVE Calendars", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public Event run(){
        setVisible(true);
        return event;
    }

    // disable or enable hour based on allday box
    private class boxListener implements ChangeListener {
        public void stateChanged(ChangeEvent e){
            if(allDayBox.isSelected()){
                starth.setEnabled(false);
                endhour.setEnabled(false);
                startap.setEnabled(false);
                endap.setEnabled(false);
            }
            else{
                starth.setEnabled(true);
                endhour.setEnabled(true);
                startap.setEnabled(true);
                endap.setEnabled(true);
            }
        }
    }

    private EventDateTime getStartTime(){
        return EventCreator.getTime(startmonth, startday, startyear, starth, startap, allDayBox);
    }

    private EventDateTime getEndTime(){
        return EventCreator.getTime(endmonth, endday, endyear, endhour, endap, allDayBox);
    }

    private void deleteEvent(){
        int delete = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + event.getSummary() + "?", "EVE Calendars", JOptionPane.YES_NO_OPTION);
        if(delete == JOptionPane.YES_OPTION){
            try {
                Program.calendars.removeEvent(jCalendar.getCalender(), event);
                jCalendar.populate();
                dispose();
            }
            catch(IOException e){
                JOptionPane.showMessageDialog(this, "Error removing event: " + e.getMessage(), "EVE Calendars", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
