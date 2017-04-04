package edu.oakland.eve.gui;

import com.google.api.services.calendar.model.Event;

import javax.swing.*;
import java.awt.event.*;

public class EventEditor extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField name;
    private JTextField desc;
    private JTextField location;
    private JButton buttonDelete;
    private JPanel formpanel;
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
    JCalendar jCalendar;
    Event event;

    public EventEditor(JCalendar jcal, Event ev) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Edit event");
        jCalendar = jcal;
        event = ev;

        //TODO: fill text boxes

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
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
        pack();
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public Event run(){
        setVisible(true);
        return event;
    }
}
