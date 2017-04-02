package edu.oakland.eve.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import edu.oakland.eve.api.CalendarAPI;
import edu.oakland.eve.core.Program;

import static java.lang.Math.ceil;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


/**
 * A widget for viewing a Google calendar
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class JCalendar extends JPanel{
    private Calendar cal;
    private java.util.GregorianCalendar javacal;
    private JLabel title;
    private JButton backButton;
    private JButton forwardButton;
    private JTable table;
    private JScrollPane pane;
    private DefaultTableModel model;

    private String[] columns = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

    public JCalendar(Calendar c){
        cal = c; // give an instance of the calendar
        javacal = new GregorianCalendar(); // get a Calendar with today's date.
        javacal.set(java.util.Calendar.DAY_OF_MONTH, 1); // always use the first

        // set the name of the tab
        if(c.getSummary() == null) setName("Untitled");
        else setName(c.getSummary());

        // set padding: bottom, left, right, top
        setBorder(new EmptyBorder(10, 15, 15, 25));

        backButton = new JButton();
        try {
            backButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("resources/arrow-thick-left.png"))));
        } catch(Exception e){}
        backButton.addActionListener(e -> flipBack());

        forwardButton = new JButton();
        try {
            forwardButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("resources/arrow-thick-right.png"))));
        } catch(Exception e){}
            forwardButton.addActionListener(e ->flipForward());

        title = new JLabel();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        setHeader();

        model = new DefaultTableModel(null, columns);
        table = new JTable(model);
        table.setDefaultRenderer(Object.class, new CalendarRenderer());
        table.setRowHeight(50);
        table.setRowSelectionAllowed(false);
        table.setEnabled(false); // no changing values
        pane = new JScrollPane(table);

        //pack the elements
        this.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(backButton, BorderLayout.WEST);
        panel.add(title, BorderLayout.CENTER);
        panel.add(forwardButton, BorderLayout.EAST);
        this.add(panel, BorderLayout.NORTH);
        this.add(pane, BorderLayout.CENTER);

        // pull from the calendar
        populate();
    }

    // set the label of the header to the current java.util.Calendar date.
    private void setHeader(){
        title.setText(
                new SimpleDateFormat("MMMM yyy").format(javacal.getTime())
        );
    }

    // back button handler
    private void flipBack(){
        javacal.add(MONTH, -1);
        setHeader();
        populate();
    }

    // forward button handler
    private void flipForward(){
        javacal.add(MONTH, +1);
        setHeader();
        populate();
    }

    // populate the table using events from the google calendar instance
    // http://www.javacodex.com/Swing/Swing-Calendar
    private void populate(){
        //TODO: Remove this line if possible
        model.setRowCount(0); // reset the rows
        model.setRowCount(javacal.getActualMaximum(java.util.Calendar.WEEK_OF_MONTH));
        int start = javacal.get(DAY_OF_WEEK);
        int max = javacal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        int j = start - 1; // day of the week
        for(int i = 1; i <= max; i++){
            //      value , day of the week, week
            model.setValueAt(new CalendarCell(i), j/7, j%7);
            j++;
        }

        // Let's put the events in the calendar
        try{
            Events el = Program.calendars.showEvents(cal);
            for(Event e : el.getItems()){
                LocalDate dt = CalendarAPI.getDate(e.getStart().getDate());
                if(dt.getMonth().getValue() - 1 == javacal.get(MONTH) && dt.getYear() == javacal.get(YEAR)){
                    CalendarCell cell = (CalendarCell)table.getValueAt(week(dt.getDayOfMonth()), dayofweek(dt.getDayOfWeek()));
                    cell.addEvent(e);
                }
            }
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(
                    this, "Error fetching events: " + e.getMessage(),
                    "EVE Calendars", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int dayofweek(DayOfWeek d){
        switch(d.getValue()){
            case 7: return 0;
            default: return d.getValue();
        }
    }

    // which week of the month is it (zero based)?
    private int week(int day){
        int ret = 0;
        // what day of the week does the first fall on?
        int first = javacal.get(DAY_OF_WEEK);
        // what day is the first saturday?
        for(int i = 7 - first; i < day; i+= 7){
            ret++;
        }
        return ret;
    }

    class CalendarRenderer implements TableCellRenderer{
        @Override
        public Component getTableCellRendererComponent(JTable jTable, Object o, boolean isSelected, boolean hasFocus, int r, int c) {
            if(!(o instanceof CalendarCell)) return new JPanel();
            else return (CalendarCell)o;
        }
    }
}
