package edu.oakland.eve.gui;

import edu.oakland.eve.core.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class MainWindow extends JFrame {
    private JMenuBar menuBar;
    private JMenu fileMenu, calendarMenu, rssMenu, helpMenu;
    private JMenuItem settingsItem, quitItem;
    private JMenuItem addCalendarItem, refreshItem;
    private JMenuItem newFeedItem, newCateoryItem, pullFeedsItem;
    private JMenuItem aboutItem;
    private CalendarTab calendarPanel;
    private RSSTab rsspanel;
    private WeatherUI weatherpanel;
    private JTabbedPane tabs;

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
            dispose();
            System.exit(-1);
        }

        //pack our elements
        createWidgets();
        packWidgets();

        //show our frame
        setVisible(true);
    }

    private void createWidgets(){
        tabs = new JTabbedPane();
        calendarPanel = new CalendarTab();
        rsspanel = new RSSTab();
        weatherpanel = new WeatherUI();
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        settingsItem = new JMenuItem("Settings");
        settingsItem.addActionListener(e -> new SettingsDialog(this, true));
        quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(e -> exit());
        calendarMenu = new JMenu("Calendars");
        addCalendarItem = new JMenuItem("Create Calendar");
        addCalendarItem.addActionListener(e ->createCalendar());
        refreshItem = new JMenuItem("Refresh Calendars");
        refreshItem.addActionListener(e->refreshCalendars());
        rssMenu = new JMenu("RSS");
        newFeedItem = new JMenuItem("Add New Feed");
        newFeedItem.addActionListener(e -> createFeed());
        newCateoryItem = new JMenuItem("Create new Category");
        newCateoryItem.addActionListener(e -> createCategory());
        pullFeedsItem = new JMenuItem("Update Feeds");
        pullFeedsItem.addActionListener(e -> pullFeeds());
        helpMenu = new JMenu("Help");
        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> new AboutDialog());
    }

    private void packWidgets(){
        // pack the menu bar
        fileMenu.add(settingsItem);
        fileMenu.add(quitItem);
        calendarMenu.add(refreshItem);
        calendarMenu.add(addCalendarItem);
        rssMenu.add(pullFeedsItem);
        rssMenu.addSeparator();
        rssMenu.add(newFeedItem);
        rssMenu.add(newCateoryItem);
        helpMenu.add(aboutItem);
        menuBar.add(fileMenu);
        menuBar.add(calendarMenu);
        menuBar.add(rssMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // pack the tabs
        tabs.add(calendarPanel);
        tabs.add(rsspanel);
        tabs.add(weatherpanel);
        tabs.setMnemonicAt(0, KeyEvent.VK_C);
        tabs.setMnemonicAt(1, KeyEvent.VK_R);
        tabs.setMnemonicAt(2, KeyEvent.VK_W);

        // pack the tabs
        add(tabs);

        // set the window size
        pack();
    }

    private void exit(){
        setVisible(false);
        Program.exit();
        dispose();
        System.exit(0);
    }

    private void createCalendar(){
        CalendarCreator dialog = new CalendarCreator();
        if(dialog.run())
            refreshCalendars();
    }

    private void createFeed(){
        FeedCreator dialog = new FeedCreator();
        if(dialog.run() != null){
            pullFeeds();
        }
    }

    private void createCategory(){
        CategoryCreator dialog = new CategoryCreator();
        if(dialog.run() != null)
            pullFeeds();
    }

    private void pullFeeds(){
        rsspanel.pull();
    }

    private void refreshCalendars(){
        calendarPanel.updateCalendars();
    }

}


