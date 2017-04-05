package edu.oakland.eve.gui;

import edu.oakland.eve.rss.Feed;
import edu.oakland.eve.rss.Story;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Michael MacLean
 */
public class RSSFeedNode extends DefaultMutableTreeNode {
    Feed feed;

    public Feed getFeed() { return feed; }

    public RSSFeedNode(Feed f){
        feed = f;
        setAllowsChildren(false);
        setUserObject(f.getName());
    }

    RSSContentPanel generatePanel(){
        System.out.println("Generating...");
        RSSContentPanel panel = new RSSContentPanel();
        for(Story s : feed){
            panel.add(s);
        }

        return panel;
    }
}
