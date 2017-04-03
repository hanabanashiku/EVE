package edu.oakland.eve.gui;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 * TODO: switch to box layout to fix layout
 */
public class EventCreator extends JDialog implements ActionListener{
    Calendar calendar;
    Event event;
    JPanel panel;
    JPanel buttons;
    JLabel namel;
    JTextField name;
    JLabel descl;
    JTextField description;
    //JDatePicker startdate;
    //JDatePicket enddate;
    JButton ok;
    JButton cancel;

    public EventCreator(JFrame parent, Calendar c){
        super(parent, "New Event", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        calendar = c;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        buttons = new JPanel();
        buttons.setLayout(new BorderLayout());

        namel = new JLabel("Name:");
        name = new JTextField();
        descl = new JLabel("Description:");
        description = new JTextField();

        JButton ok = new JButton("OK");
        ok.addActionListener(this);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(this);

        panel.add(namel, BorderLayout.WEST);
        panel.add(name, BorderLayout.CENTER);
        panel.add(descl, BorderLayout.WEST);
        panel.add(description, BorderLayout.CENTER);
        buttons.add(ok, BorderLayout.WEST);
        buttons.add(cancel, BorderLayout.EAST);
        panel.add(buttons, BorderLayout.SOUTH);
        add(panel);
        pack();
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == ok){
            // TODO: Create the event
        }
        else{ // the user hit cancel
            event = null;
        }
        dispose();
    }

    public Event run(){
        setVisible(true);
        return event;
    }
}
