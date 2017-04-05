package edu.oakland.eve.gui;

import edu.oakland.eve.core.Program;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
class RSSTab extends JPanel {
    private JTree tree;
    private RSSCategoryNode root;
    private JPanel contentPanel;

    RSSTab(){
        setName("RSS");
        setLayout(new BorderLayout());
        createWidgets();
        packWidgets();
    }

    private void createWidgets(){
        tree = new JTree();
        root = new RSSCategoryNode((DefaultTreeModel)tree.getModel());
        ((DefaultTreeModel)tree.getModel()).setRoot(root);
        tree.addMouseListener(new Listener());
        tree.getSelectionModel().addTreeSelectionListener(new SelectionListener());
        contentPanel = new JPanel();
    }

    private void packWidgets(){
        JScrollPane pane = new JScrollPane(tree);
        add(pane, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    void pull(){
        try {
            Program.rss.pull();
        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error pulling feeds: " + e.getMessage(), "EVE RSS", JOptionPane.ERROR_MESSAGE);
            return;
        }
        root.fill();
    }

    private void edit(){
        TreeNode node = (TreeNode)tree.getLastSelectedPathComponent();
        if(node instanceof RSSCategoryNode && !((RSSCategoryNode)node).isRoot()){
            CategoryEditor dialog = new CategoryEditor(((RSSCategoryNode) node).getCategory());
            dialog.run();
            pull();
        }
        else if(node instanceof RSSFeedNode){
            FeedEditor dialog = new FeedEditor(((RSSFeedNode)node).getFeed());
            dialog.run();
            pull();
        }
    }

    private void delete(){
        TreeNode node = (TreeNode)tree.getLastSelectedPathComponent();
        if(node instanceof RSSCategoryNode)
            new CategoryDeleter(((RSSCategoryNode)node).getCategory());
        else if(node instanceof RSSFeedNode)
            new FeedDeleter(((RSSFeedNode)node).getFeed()).run();
        pull();
    }

    private class Listener extends MouseAdapter{
        public void mouseClicked(MouseEvent e){
            if(e.getButton() == 3){
                int r = tree.getClosestRowForLocation(e.getX(), e.getY());
                tree.setSelectionRow(r);
                new ContextMenu(tree, e.getX(), e.getY());
            }
        }
    }

    private class SelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            contentPanel.removeAll();
            TreeNode n = (TreeNode)tree.getLastSelectedPathComponent();
            if(n instanceof RSSCategoryNode) contentPanel.add(((RSSCategoryNode)n).generatePanel(), BorderLayout.CENTER);
            else if(n instanceof  RSSFeedNode) contentPanel.add(((RSSFeedNode)n).generatePanel(), BorderLayout.CENTER);
        }
    }

    private class ContextMenu extends JPopupMenu{
        ContextMenu(JTree comp, int x, int y){
            JMenuItem editItem = new JMenuItem("Edit");
            editItem.addActionListener(e -> edit());
            JMenuItem deleteItem = new JMenuItem("Delete");
            deleteItem.addActionListener(e -> delete());
            add(editItem);
            add(deleteItem);
            show(comp, x, y);
        }
    }
}
