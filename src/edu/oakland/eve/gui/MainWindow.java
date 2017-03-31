package edu.oakland.eve.gui;

import edu.oakland.eve.core.Program;

import javax.swing.*;
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

        // hold all the category panels here
        catpanes = new ArrayList<>();
    }

    private void packWidgets(){
        pack();
    }
}


