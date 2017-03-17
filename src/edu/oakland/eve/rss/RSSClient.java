package edu.oakland.eve.rss;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Dictionary;
import java.io.*;

import edu.oakland.eve.error.*;

/**
 * Aggregate and manage RSS and Atom feeds.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class RSSClient implements Serializable, Iterable<Feed>{
    private static final String FNAME = "rss_cli.db";
    private LinkedList<Feed> allFeeds;
    private LinkedList<Category> categories;

    private RSSClient(){
        allFeeds = new LinkedList<Feed>();
        categories = new LinkedList<>();
    }

    /**
     * Determine whether or not the feed exists within the RSS Client.
     * @param f The feed object to check
     * @return true if the feed is contained in the client.
     * note: uses .equals() for comparisons
     */
    public boolean contains(Feed f) { return allFeeds.contains(f); }

    /**
     * Add a feed to the client
     * @param f The feed to add
     */
    public void addFeed(Feed f){
        if(!contains(f))
            allFeeds.add(f);
    }

    /**
     * Add a feed to a category
     * @param f The feed to add
     * @param catName The category to add it to
     */
    public void addFeed(Feed f, String catName){

    }

    /**
     * Add a feed to a category
     * @param f The feed to add
     * @param c The category to add it to
     */
    public void addFeed(Feed f, Category c){

    }

    /**
     * Load the client instance
     * @return The existing instance, or a new instance if there is none.
     * @throws IOException if the versions are incompaatible
     */
    public RSSClient load() throws IOException{
        if(!new File(FNAME).exists()) return new RSSClient();
        try {
            FileInputStream fstream = new FileInputStream(FNAME);
            ObjectInputStream ostream = new ObjectInputStream(fstream);
            return (RSSClient) ostream.readObject();
        }
        catch(ClassNotFoundException e) { throw new IOException("Incompatible RSSClient version."); }
    }

    public Iterator<Feed> iterator() { return allFeeds.iterator(); }
    public Iterator<Category> categoryIterator() { return categories.iterator(); }

    /**
     * Save the client instance
     * @return 0 on success, -1 on failure
     */
    public int save(){
        try{
            FileOutputStream fstream = new FileOutputStream(FNAME);
            ObjectOutputStream ostream = new ObjectOutputStream(fstream);
            ostream.writeObject(this);
        }
        catch(Exception e) { return -1; }
        return 0;
    }
    protected void finalize() { save(); }
}
