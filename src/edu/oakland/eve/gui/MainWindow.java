package edu.oakland.eve.gui;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import edu.oakland.eve.core.Program;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class MainWindow extends JFrame {
    private JPanel panel;
    private JTabbedPane calendartabs;
    private JPanel calendarpanel;
    private JPanel rsspanel;
    private JPanel weatherpanel;
    private JTabbedPane tabs;
    private JButton rsssettings;

    private ArrayList<categoryPanel> catpanes;

    public MainWindow(){
        setTitle(Program.APP_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(650, 450));
        setLayout(new BorderLayout());
        try{
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null,
                    "MainWindow: " + e.getMessage(),
                    "EVE Error",
                    JOptionPane.ERROR_MESSAGE);
            exit();
        }

        //pack our elements
        createWidgets();
        packWidgets();

        //show our frame
        setVisible(true);
    }

    public void exit(){
        setVisible(false);
        dispose();
    }

    private void createWidgets(){
        tabs = new JTabbedPane();
        calendarpanel = new JPanel();
        calendartabs = new JTabbedPane();
        rsspanel = new JPanel();
        weatherpanel = new JPanel();

        // hold all the RSS category panels here
        catpanes = new ArrayList<>();
    }

    private void packWidgets(){
        // pack the calendar tab
        calendarpanel.setName("Calendar");
        calendarpanel.setLayout(new BorderLayout());
        calendartabs.setTabPlacement(JTabbedPane.LEFT);
        calendartabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        calendarpanel.add(calendartabs, BorderLayout.CENTER);
        tabs.add(calendarpanel);
        updateCalendars();

        // pack the rss tab
        rsspanel.setName("RSS");
        tabs.add(rsspanel);
        weatherpanel.setName("Weather");
        tabs.add(weatherpanel);
        tabs.setMnemonicAt(0, KeyEvent.VK_C);
        tabs.setMnemonicAt(1, KeyEvent.VK_R);
        tabs.setMnemonicAt(2, KeyEvent.VK_W);

        // pack the tabs
        add(tabs);

        // set the window size
        pack();
    }

    private void updateCalendars(){
        calendartabs.removeAll();
        try {
            List<CalendarListEntry> cl = Program.calendars.fetchCalendars().getItems();
            for(CalendarListEntry c : cl){
                calendartabs.add(new JCalendar(Program.calendars.fetchCalendar(c.getId())));
            }
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this, "Failed to pull calendars: " + e.getMessage(), "EVE Calendars", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}


