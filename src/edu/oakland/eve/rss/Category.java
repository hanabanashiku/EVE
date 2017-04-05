package edu.oakland.eve.rss;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import edu.oakland.eve.error.RSSClientException;

/**
 * Provides an organizational structure for newsfeeds.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Category implements Serializable, Iterable<Feed>{
    private String name;
    private LinkedList<Feed> feeds;
    private RSSClient cli;

    /**
     * @param catName The name of the feed category.
     * @param rcli The RSS client instance
     */
    protected Category(String catName, RSSClient rcli) throws RSSClientException{
        cli = rcli;
        setName(catName);
        feeds = new LinkedList<>();
    }

    public String getName() { return name; }
    public void setName(String value) throws RSSClientException{
        if(cli.getCategory(value) != null)
            throw new RSSClientException("A category already exists with the given name.");
        name = value;
    }

    /**
     * Determine whether or not the feed exists within the RSS Client.
     * @param f The feed object to check
     * @return true if the feed is contained in the category.
     * note: uses .equals() for comparisons
     */
    public boolean contains(Feed f){
        return feeds.contains(f);
    }
    /**
     * Add a feed to the category, and to the client if it does not exist
     * @param f The feed to add
     * @returns true on success
     */
    public boolean add(Feed f){
        if(contains(f)) return false;
        if(!cli.contains(f)) cli.add(f);
        feeds.add(f);

        f.setCategory(this);
        return true;
    }

    /**
     * Removes a feed from the category.
     * @param f The feed to remove
     * @return true if the feed was removed successfully.
     */

    public boolean remove(Feed f){
        if(feeds.remove(f)){
            f.setCategory(null);
            return true;
        }
        return false;
    }

    public Iterator<Feed> iterator() { return feeds.iterator(); }

    public boolean equals(Object o) {
        return o instanceof Category && ((Category) o).getName().equals(name);
    }
}

