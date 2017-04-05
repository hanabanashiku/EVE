package edu.oakland.eve.gui;

import edu.oakland.eve.core.Program;
import edu.oakland.eve.rss.Category;
import edu.oakland.eve.rss.Feed;

import javax.swing.*;
import java.awt.event.*;
import java.util.Iterator;

public class FeedCreator extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField url;
    private JTextField name;
    private JPanel propertiesPanel;
    private JComboBox<String> category;
    private JButton newCategoryButton;
    private Exception error = null;
    private Feed f;

    public FeedCreator() {
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Add Feed");
        populateCategories();

        propertiesPanel.setVisible(false);

       buttonOK.addActionListener(e -> onOK());
       buttonCancel.addActionListener(e -> onCancel());
       url.addActionListener(e -> resolveURL());
       newCategoryButton.addActionListener(e -> createCategory());

        // call onCancel() when cross is clicked
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel()
                , KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        pack();
    }

    private void onOK() {
        if(error != null) {
            JOptionPane.showMessageDialog(this, "Error creating feed: " + error.getMessage(), "EVE RSS", JOptionPane.ERROR_MESSAGE);
            return;
        }
        else if(f == null) {
            JOptionPane.showMessageDialog(this, "Error creating feed: please enter a URL.", "EVE RSS", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!name.getText().isEmpty())
            f.setName(name.getText());
        Program.rss.add(f);
        if(category.getSelectedIndex() != 0){ // index 0 is (none)
            Category cat = Program.rss.getCategory((String)category.getSelectedItem());
            if(cat != null)
                cat.add(f);
        }
        dispose();
    }

    private void onCancel() {
        f = null;
        dispose();
    }

    private void resolveURL(){
        try{
            f = new Feed(url.getText());
            error = null;
            name.setText(f.getTitle());
            propertiesPanel.setVisible(true);
            pack();
        }
        catch(Exception e){ error = e; f = null; }
    }

    private void populateCategories(){
        for(Iterator<Category> it = Program.rss.categoryIterator(); it.hasNext(); ){
            category.addItem(it.next().getName());
        }
    }

    private void createCategory(){
        CategoryCreator dialog = new CategoryCreator();
        Category c = dialog.run();
        if(c != null){
            category.addItem(c.getName());
            category.setSelectedIndex(category.getItemCount() - 1);
        }
    }

    public Feed run(){
        setVisible(true);
        return f;
    }
}
