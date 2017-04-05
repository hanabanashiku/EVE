package edu.oakland.eve.gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import edu.oakland.eve.api.CalendarAPI;

public class EventCreator extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField name;
    private JTextField desc;
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

    Event event = null;

    public EventCreator(JFrame parent) {
        super(parent, "New Event", true);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        pack();

        // set defaults
        Calendar cal = Calendar.getInstance();
        startmonth.setSelectedIndex(cal.get(Calendar.MONTH));
        endmonth.setSelectedIndex(cal.get(Calendar.MONTH));
        startday.setSelectedIndex(cal.get(Calendar.DAY_OF_MONTH) - 1);
        endday.setSelectedIndex(cal.get(Calendar.DAY_OF_MONTH) - 1);
        startyear.setText(Integer.toString(cal.get(Calendar.YEAR)));
        endyear.setText(Integer.toString(cal.get(Calendar.YEAR)));
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        allDayBox.addChangeListener(new boxListener());

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
    }

    private void onOK() {
        Event e = new Event();
        if(name.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "The event must have a title.", "EVE Calendars", JOptionPane.ERROR_MESSAGE);
            return;
        }
        e.setSummary(name.getText());
        if(!desc.getText().isEmpty()) e.setDescription(desc.getText());
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
        e.setStart(start);
        e.setEnd(end);
        event = e;
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    // disable or enable hour based on allday box
    private class boxListener implements ChangeListener{
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
        return getTime(startmonth, startday, startyear, starth, startap, allDayBox);
    }

    private EventDateTime getEndTime(){
        return getTime(endmonth, endday, endyear, endhour, endap, allDayBox);
    }

    static EventDateTime getTime(JComboBox monthf, JComboBox dayf, JTextField yearf, JComboBox hourf, JComboBox apf, JCheckBox allday){
        int month;
        // convert the month name to the number
        java.util.Calendar c = java.util.Calendar.getInstance();
        try{ c.setTime(new SimpleDateFormat("MMMM").parse((String)monthf.getSelectedItem())); } catch(Exception e){}
        month = c.get(java.util.Calendar.MONTH) + 1;
        int day = Integer.parseInt((String)dayf.getSelectedItem());
        int year;
        try{
            year = Integer.parseInt(yearf.getText());
        } catch(NumberFormatException e){
            JOptionPane.showMessageDialog(monthf.getRootPane(), "Invalid year format", "EVE Calendars", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if(year < 1970 || year > 2999){
            JOptionPane.showMessageDialog(monthf.getRootPane(), "Invalid year specified", "EVE Calendars", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if(!allday.isSelected()){
            int hour = Integer.parseInt((String)hourf.getSelectedItem());
            boolean pm = apf.getSelectedItem().equals("PM");
            if(pm && hour != 12) hour -= 12;
            else if(!pm && hour == 12) hour = 0;
            LocalDateTime ldt = LocalDateTime.of(year, month, day, hour, 0);
            return new EventDateTime()
                    .setDateTime(new DateTime(
                            ZonedDateTime.of(ldt, ZoneId.systemDefault())
                                    .format(DateTimeFormatter
                                            .ofPattern(CalendarAPI.DATE_TIME_PARSER))));
        }
        else{
            LocalDate ld = LocalDate.of(year, month, day);
            return new EventDateTime()
                    .setDate(new DateTime(ld.format(DateTimeFormatter.
                    ofPattern(CalendarAPI.DATE_PARSER))));
        }
    }


    public Event run(){
        setVisible(true);
        return event;
    }

}
