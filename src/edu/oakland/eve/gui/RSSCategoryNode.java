package edu.oakland.eve.gui;

import edu.oakland.eve.core.Program;
import edu.oakland.eve.rss.Category;
import edu.oakland.eve.rss.Feed;
import edu.oakland.eve.rss.Story;

import javax.swing.*;
import javax.swing.tree.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Michael MacLean
 */
public class RSSCategoryNode extends DefaultMutableTreeNode {
    // A null category means it is the root node
    private Category category;
    private final boolean root;
    private LinkedList<Feed> feeds = new LinkedList<>();
    private LinkedList<Category> cats = new LinkedList<>();
    private DefaultTreeModel model;

    public boolean isRoot() { return root; }
    public Category getCategory() { return category; }

    public RSSCategoryNode(Category cat, DefaultTreeModel m){
        category = cat;
        model = m;
        if(cat == null){
            setUserObject("All feeds");
            root = true;
        }
        else{
            setUserObject(category.getName());
            root = false;
        }
        setAllowsChildren(true);
        fill();
    }

    public RSSCategoryNode(DefaultTreeModel m) { this(null, m); }

    // fill up with nodes
    void fill(){
        if(root){
            //Update nodes already there or delete ones that aren't
            for(int i = 0; i < getChildCount(); i++){
                TreeNode n = getChildAt(i);
                if(n instanceof RSSCategoryNode) {
                    if(Program.rss.getCategory(((RSSCategoryNode) n).getCategory().getName()) == null)
                        model.removeNodeFromParent((MutableTreeNode)n);
                    else
                        ((RSSCategoryNode) n).fill();
                }
                else{
                    if(!Program.rss.contains(((RSSFeedNode)n).getFeed()))
                        model.removeNodeFromParent((MutableTreeNode)n);
                    else if(((RSSFeedNode)n).getFeed().getCategory() != null)
                        model.removeNodeFromParent((MutableTreeNode)n);
                }
            }
            for(Iterator<Category>it = Program.rss.categoryIterator(); it.hasNext(); ){
                Category c = it.next();
                if(!cats.contains(c)){
                    cats.add(c);
                    add(new RSSCategoryNode(c, model));

                }
            }
            for(Feed f : Program.rss) {
                if (f.getCategory() == null && !feeds.contains(f)) {
                    feeds.add(f);
                    add(new RSSFeedNode(f));
                }
            }
        }
        else{
            for(int i = 0; i < getChildCount(); i++){
                RSSFeedNode n = (RSSFeedNode)getChildAt(i);
                if(!Program.rss.contains(n.getFeed()))
                    model.removeNodeFromParent(n);
                else if(n.getFeed().getCategory() == null)
                    model.removeNodeFromParent(n);
            }
            for(Feed f : Program.rss)
                if(f.getCategory() == category && !feeds.contains(f)){
                    feeds.add(f);
                    add(new RSSFeedNode(f));
                }
        }

        for(int i = 0; i < feeds.size(); i++){
            boolean exists = false;
            for(int j = 0; j < getChildCount(); j++)
                if( getChildAt(j) instanceof RSSFeedNode && ((RSSFeedNode)getChildAt(j)).getFeed().equals(feeds.get(i)))
                    exists = true;
            if(!exists) feeds.remove(feeds.get(i));
        }

        model.reload(this);
    }

    RSSContentPanel generatePanel(){
        RSSContentPanel panel = new RSSContentPanel();
        if(root){
            for(Feed f : Program.rss)
                for(Story s : f)
                    panel.add(s);
        }
        else{
            for(Feed f : category)
                for(Story s : f)
                    panel.add(s);
        }
        return panel;
    }
}
