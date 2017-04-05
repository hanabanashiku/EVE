package edu.oakland.eve.gui;

import edu.oakland.eve.core.Program;
import edu.oakland.eve.rss.Category;
import edu.oakland.eve.rss.Feed;

import javax.swing.*;
import java.awt.event.*;
import java.util.Iterator;

public class FeedEditor extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<String> category;
    private JButton addCategoryButton;
    private JButton deleteButton;
    private JTextField name;
    private Feed feed;

    FeedEditor(Feed f) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Edit feed");
        feed = f;

        name.setText(f.getName());

        category.setSelectedIndex(0);
        for(Iterator<Category> it = Program.rss.categoryIterator(); it.hasNext();){
            Category cat = it.next();
            category.addItem(cat.getName());
            if(f.getCategory() != null && f.getCategory().equals(cat))
                category.setSelectedIndex(category.getItemCount() - 1);
        }

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        deleteButton.addActionListener(e -> delete());
        addCategoryButton.addActionListener(e -> addCategory());

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
        feed.setName(name.getText());
        if(category.getSelectedIndex() == 0 && feed.getCategory() == null)
            feed.getCategory().remove(feed);
        else if(category.getSelectedIndex() != 0){
            Category cat = Program.rss.getCategory(((String)category.getSelectedItem()));
            if(feed.getCategory() != cat){
                if(feed.getCategory() != null)
                    feed.getCategory().remove(feed);
                cat.add(feed);
            }
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public void run(){
        setVisible(true);
    }
    private void delete(){
        new FeedDeleter(feed).run();
        dispose();
    }

    private void addCategory(){
        CategoryCreator dialog = new CategoryCreator();
        Category cat = dialog.run();
        if(cat != null)
            category.addItem(cat.getName());
    }
}
