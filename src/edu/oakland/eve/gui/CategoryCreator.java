package edu.oakland.eve.gui;

import edu.oakland.eve.core.Program;
import edu.oakland.eve.rss.Category;

import javax.swing.*;
import java.awt.event.*;

public class CategoryCreator extends JDialog {
    private JPanel contentPane;
    private JTextField name;
    private JButton buttonOK;
    private JButton buttonCancel;
    private Category cat = null;

    public CategoryCreator() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("New Category");

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
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
        try{
            if(name.getText().isEmpty()) throw new Exception("The category must have a name");
            Program.rss.addCategory(name.getText());
            cat = Program.rss.getCategory(name.getText());
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this, "Error creating category: " + e.getMessage(), "EVE Calendars", JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public Category run(){
        setVisible(true);
        return cat;
    }
}
