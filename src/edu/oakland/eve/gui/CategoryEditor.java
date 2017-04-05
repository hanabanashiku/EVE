package edu.oakland.eve.gui;

import edu.oakland.eve.rss.Category;

import javax.swing.*;
import java.awt.event.*;

public class CategoryEditor extends JDialog {
    private JPanel contentPane;
    private JTextField name;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton deleteButton;
    Category category;

    public CategoryEditor(Category cat) {
        category = cat;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Edit Category");

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        deleteButton.addActionListener(e -> delete());

        name.setText(cat.getName());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e ->onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        pack();
    }

    private void onOK() {
        // add your code here
        try{
            category.setName(name.getText());
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this, "Error updating category: " + e.getMessage(), "EVE RSS", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();
    }

    private void onCancel() {
        category = null;
        dispose();
    }

    public boolean run(){
        setVisible(true);
        return category == null;
    }

    private void delete(){
        new CategoryDeleter(category);
        dispose();
    }
}
