package edu.oakland.eve.gui;

import com.google.api.services.calendar.model.Calendar;
import edu.oakland.eve.core.Program;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class CalendarEditor extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField name;
    private JTextField desc;
    private JTextField location;
    private JButton buttonDelete;
    private Calendar calendar;
    private CalendarTab tab;

    CalendarEditor(CalendarTab t, Calendar c) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        calendar = c;
        tab = t;
        setTitle("Edit Calendar");

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        buttonDelete.addActionListener(e -> tab.deleteCalendar(calendar));
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel()
                , KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // fill text boxes
        name.setText(calendar.getSummary());
        desc.setText(calendar.getDescription());
        location.setText(calendar.getLocation());

        pack();
    }

    private void onOK() {
        if(!name.getText().isEmpty()) calendar.setSummary(name.getText());
        if(!desc.getText().isEmpty()) calendar.setDescription(desc.getText());
        if(!location.getText().isEmpty()) calendar.setLocation(location.getText());
        try{
            Program.calendars.updateCalendar(calendar.getId(), calendar);
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(this, "Error updating calendar: " + e.getMessage(), "EVE Calendars", JOptionPane.ERROR_MESSAGE);
            return; // don't dispose yet
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public Calendar run() {
        setVisible(true);
        return calendar;
    }
}
