package edu.oakland.eve.gui;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import javax.swing.*;
import javax.swing.table.*;

import com.google.api.services.calendar.Calendar;



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
    private DefaultTableModel model;

    private String [] columns = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

    public JCalendar(Calendar c){
        cal = c; // give an instance of the calendar
        javacal = new GregorianCalendar(); // get a Calendar with today's date.
        javacal.set(java.util.Calendar.DAY_OF_MONTH, 1); // always use the first
        this.setLayout(new BorderLayout());
        backButton = new JButton();
        backButton.setIcon(new ImageIcon("resources/arrow-thick-left.png"));
        backButton.addActionListener(e -> flipBack());
        forwardButton = new JButton();
        forwardButton.setIcon(new ImageIcon("resources/arrow-thick-right.png"));
        forwardButton.addActionListener(e ->flipForward());
        title = new JLabel();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        setHeader();
        model = new DefaultTableModel(null, columns);
        table = new JTable(model);
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
        javacal.set(java.util.Calendar.MONTH, -1);
        setHeader();
    }

    // forward button handler
    private void flipForward(){
        javacal.set(java.util.Calendar.MONTH, +1);
        setHeader();
    }

    // populate the table using events from the google calendar instance
    private void populate(){
        //TODO: Remove this line if possible
        model.setRowCount(0); // reset the rows
        model.setRowCount(javacal.getActualMaximum(java.util.Calendar.WEEK_OF_MONTH));
        int start = javacal.get(java.util.Calendar.DAY_OF_WEEK);
        int max = javacal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        int i = start - 1;
        //TODO: Continue
        // http://www.javacodex.com/Swing/Swing-Calendar
        for()
    }

}
