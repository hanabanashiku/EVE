package edu.oakland.eve.gui;

import edu.oakland.eve.rss.Story;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class RSSContentPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JScrollPane pane;
    //private JPanel article;
    private ArrayList<Story> stories;

    public RSSContentPanel(){
        setLayout(new BorderLayout());
        stories = new ArrayList<>();
        String[] columns = {"Title", "Author", "Description", "Date", "Feed"};
        model = new DefaultTableModel(null, columns);
        table = new JTable(model);
        table.addMouseListener(new Listener());
        pane = new JScrollPane(table);
        //article = new JPanel();
        add(pane, BorderLayout.NORTH);
        //add(article, BorderLayout.SOUTH);
    }

    public void add(Story s){
        int i = table.getRowCount();
        stories.add(i, s);
        String[] data = {s.getTitle(), s.getAuthor(), s.getDescription(),
                DateTimeFormatter.ofPattern("MMM dd, yy hh:mm").format(s.getPubDate()), s.getFeed()};

        ((DefaultTableModel)table.getModel()).addRow(data);
    }

    class Listener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int i = table.getSelectedRow();
            if(!stories.get(i).wasRead()) stories.get(i).toggleRead();
            /*if(e.getClickCount() == 1){
                JEditorPane text;
                try{
                text = new JEditorPane(stories.get(i).getLink().toString());}
                catch(Exception err) { return; }
                JScrollPane spane = new JScrollPane(text);
                article.add(pane, BorderLayout.SOUTH);
            }*/
            if(e.getClickCount() == 1){
                try {
                    Desktop.getDesktop().browse(new URI(stories.get(i).getLink().toString()));
                }
                catch(Exception err){
                    JOptionPane.showMessageDialog(null, "Error opening document: " + err.getMessage(), "EVE RSS", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
