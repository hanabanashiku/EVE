package edu.oakland.eve.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import edu.oakland.eve.api.CalendarAPI;
import edu.oakland.eve.core.Program;

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
    private java.util.GregorianCalendar javaCal;
    private JLabel title;
    private JButton backButton;
    private JButton forwardButton;
    private JButton addButton;
    private JPanel buttons;
    private JTable table;
    private JScrollPane pane;
    private DefaultTableModel model;
    private EventContentPane eventPanel;

    private final String[] columns = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

    public Calendar getCalender() { return cal; }

    public JCalendar(Calendar c){
        cal = c; // give an instance of the calendar
        javaCal = new GregorianCalendar(); // get a Calendar with today's date.
        javaCal.set(java.util.Calendar.DAY_OF_MONTH, 1); // always use the first

        // set the name of the tab
        if(c.getSummary() == null) setName("Untitled");
        else setName(c.getSummary());

        // set padding: bottom, left, right, top
        setBorder(new EmptyBorder(10, 15, 15, 25));

        backButton = new JButton();
        backButton.setIcon(new ImageIcon(JCalendar.class.getResource("resources/arrow-thick-left.png")));
        backButton.addActionListener(e -> flipBack());

        forwardButton = new JButton();
        forwardButton.setIcon(new ImageIcon(JCalendar.class.getResource("resources/arrow-thick-right.png")));
        forwardButton.addActionListener(e -> flipForward());

        buttons = new JPanel();
        addButton = new JButton();
        addButton.setIcon(new ImageIcon(JCalendar.class.getResource("resources/plus.png")));
        addButton.addActionListener(e -> addEvent());

        title = new JLabel();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        setHeader();

        eventPanel = new EventContentPane(cal, this);

        model = new CalendarModel(null, columns);
        table = new JTable(model);
        table.setDefaultRenderer(Object.class, new CalendarRenderer());
        table.setRowHeight(50);
        table.setRowSelectionAllowed(false);
        table.addMouseListener(new mouseHandler());
        pane = new JScrollPane(table);


        //pack the elements
        this.setLayout(new BorderLayout());
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.add(backButton, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        header.add(forwardButton, BorderLayout.EAST);
        buttons.setLayout(new BorderLayout());
        buttons.add(addButton, BorderLayout.EAST);
        this.add(header, BorderLayout.NORTH);
        this.add(pane, BorderLayout.CENTER);
        this.add(buttons, BorderLayout.SOUTH);
        buttons.add(eventPanel, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(325, -1));

        // pull from the calendar
        populate();
    }

    // set the label of the header to the current java.util.Calendar date.
    private void setHeader(){
        title.setText(
                new SimpleDateFormat("MMMM yyy").format(javaCal.getTime())
        );
    }

    // back button handler
    private void flipBack(){
        javaCal.add(MONTH, -1);
        setHeader();
        populate();
    }

    // forward button handler
    private void flipForward(){
        javaCal.add(MONTH, +1);
        setHeader();
        populate();
    }

    // populate the table using events from the google calendar instance
    // http://www.javacodex.com/Swing/Swing-Calendar
    void populate(){
        model.setRowCount(0); // reset the rows
        model.setRowCount(javaCal.getActualMaximum(java.util.Calendar.WEEK_OF_MONTH));
        int start = javaCal.get(DAY_OF_WEEK);
        int max = javaCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

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
                // All day event
                if(e.getStart().getDateTime() == null){
                    LocalDate dt = CalendarAPI.getDate(e.getStart().getDate());
                    if(dt.getMonth().getValue() - 1 == javaCal.get(MONTH) && dt.getYear() == javaCal.get(YEAR)){
                        CalendarCell cell = (CalendarCell)table.getValueAt(week(dt.getDayOfMonth()), dayofweek(dt.getDayOfWeek()));
                        cell.addEvent(e);
                    }
                }
                // has date and time
                else{
                    LocalDateTime dt = CalendarAPI.getDateTime(e.getStart().getDateTime());
                    if(dt.getMonth().getValue() - 1 == javaCal.get(MONTH) && dt.getYear() == javaCal.get(YEAR)){
                        CalendarCell cell = (CalendarCell)table.getValueAt(week(dt.getDayOfMonth()), dayofweek(dt.getDayOfWeek()));
                        cell.addEvent(e);
                    }
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
        int first = javaCal.get(DAY_OF_WEEK);
        // what day is the first saturday?
        for(int i = 7 - first; i < day; i+= 7){
            ret++;
        }
        return ret;
    }

    void addEvent(){
        Event event = new EventCreator((JFrame)SwingUtilities.getRoot(this)).run();
        if(event == null) return;
        try{
            Program.calendars.addEvent(cal, event);
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(SwingUtilities.getRoot(this), "Error creating event: " + e.getMessage(), "EVE Calendars", JOptionPane.ERROR_MESSAGE);
        }
        populate();
    }

    class CalendarRenderer implements TableCellRenderer{
        @Override
        public Component getTableCellRendererComponent(JTable jTable, Object o, boolean isSelected, boolean hasFocus, int r, int c) {
            // If the cell is empty or not a CalendarCell, we will default to a blank JPanel.
            // Setting this to return null instead will cause the window not to render properly
            if(!(o instanceof CalendarCell)) return new JPanel();
            else return (CalendarCell)o;
        }
    }

    class CalendarModel extends DefaultTableModel{
        private CalendarModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }
        @Override
        public boolean isCellEditable(int row, int column){ return false; }
    }

    class mouseHandler extends MouseAdapter{
        @Override
        public void mousePressed(MouseEvent e){
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            // make sure it's in the table and the cell isn't a default JPanel
            if(row >= 0 && col >= 0 && table.getValueAt(row, col) instanceof CalendarCell){
                CalendarCell cell = (CalendarCell)table.getValueAt(row, col);
                //TODO: support for more than one event
                eventPanel.update(cell.getEvents().get(0));
            }
        }
    }
}
