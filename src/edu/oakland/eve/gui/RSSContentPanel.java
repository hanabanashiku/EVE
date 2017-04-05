package edu.oakland.eve.gui;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import edu.oakland.eve.rss.Story;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private JPanel article;
    private ArrayList<Story> stories;

    public RSSContentPanel(){
        setLayout(new BorderLayout());
        stories = new ArrayList<>();
        String[] columns = {"Title", "Author", "Description", "Date", "Feed"};
        model = new DefaultTableModel(null, columns);
        table = new JTable(model);
        table.getSelectionModel().addListSelectionListener(new Listener());
        pane = new JScrollPane(table);
        article = new JPanel();
        add(pane, BorderLayout.NORTH);
        add(article, BorderLayout.SOUTH);
    }

    public void add(Story s){
        int i = table.getRowCount();
        stories.add(i, s);
        String[] data = {s.getTitle(), s.getAuthor(), s.getDescription(),
                DateTimeFormatter.ofPattern("MMM dd, yy hh:mm").format(s.getPubDate()), s.getFeed()};

        ((DefaultTableModel)table.getModel()).addRow(data);
    }

    class Listener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            article.removeAll();
            int i = table.getSelectedRow();
            if(!stories.get(i).wasRead()) stories.get(i).toggleRead();
            Browser b = new Browser();
            BrowserView bv = new BrowserView(b);
            article.add(bv, BorderLayout.CENTER);
            b.loadURL(stories.get(i).getLink().toString());
        }
    }
}
