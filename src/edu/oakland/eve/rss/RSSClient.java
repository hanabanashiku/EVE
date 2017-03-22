package edu.oakland.eve.rss;

import java.util.Iterator;
import java.util.LinkedList;
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
    private LinkedList<Story> saved;

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
     * Are there any feeds in the client?
     * @return true if there are no feeds
     */
    public boolean isEmpty() { return allFeeds.size() == 0; }
    /**
     * Add a feed to the client
     * @param f The feed to add
     * @return true on success
     */
    public boolean add(Feed f){
        if(!contains(f)) {
            allFeeds.add(f);
            return true;
        }
        return false;
    }

    /**
     * Remove a feed from the client and all categories
     * @param f The feed to remove
     * @return true if the feed was removed successfully
     */
    public boolean remove(Feed f){
        if(allFeeds.remove(f)){
            for(Category c : categories)
                c.remove(f);
            return true;
        }
        return false;
    }

    /**
     * Get a feed from the client
     * @param uri The feed URI
     * @return The feed, or null on failure
     */
    public Feed get(String uri){
        for(Feed f : this)
           if(f.getLink().equalsIgnoreCase(uri))
                return f;
        return null;
    }

    /**
     * Pulls all latest stories from all feeds
     * @throws RSSFormatException
     * @throws IOException
     */
    public void pull() throws RSSFormatException, IOException{
        for(Feed f : this)
            f.pull();
    }

    /**
     * Find a category by name
     * @param catName The name of the category
     * @return The category, or null on failure
     */
    public Category getCategory(String catName){
        for(Category c : categories)
            if(c.getName().equals(catName))
                return c;
        return null;
    }

    /**
     * Create a new empty category
     * @param catName The name of the category
     * @throws RSSClientException if the category already exists
     */
    public void addCategory(String catName) throws RSSClientException{
        if(getCategory(catName) != null) throw new RSSClientException("A category by the given name already exists.");
        categories.add(new Category(catName, this));
    }

    /**
     * Remove a category from the client
     * @param c The category to remove
     * @param removeAllFeeds If true, all instances of all the category's member feeds will be removed from the client.
     * @returns True on success.
     */
    public boolean removeCategory(Category c, boolean removeAllFeeds){
        if(!categories.contains(c)) return false;
        categories.remove(c);
        if(removeAllFeeds){
            for(Feed f : c)
                remove(f);
        }
        return true;
    }
    public boolean removeCategory(Category c) { return removeCategory(c, false); }

    /**
     * Load the client instance
     * @return The existing instance, or a new instance if there is none.
     * @throws IOException if the versions are incompaatible
     */
    public static RSSClient load() throws IOException{
        if(!new File(FNAME).exists()) return new RSSClient();
        try {
            FileInputStream fstream = new FileInputStream(FNAME);
            ObjectInputStream ostream = new ObjectInputStream(fstream);
            return (RSSClient) ostream.readObject();
        }
        catch(ClassNotFoundException e) { throw new IOException("Incompatible RSSClient version."); }
    }

    /**
     * Save a story for later viewing.
     * @param s The story to save
     */
    public void saveStory(Story s){
        saved.add(s);
    }

    /**
     * Remove a story from the list of saved stories.
     * @param s The story to remove
     * @return true on success
     */
    public boolean removeSavedStory(Story s){
        return saved.remove(s);
    }

    public LinkedList<Story> getSavedStories(Feed f){ return getSavedStories(f.getLink()); }
    /**
     * Get all saved stories that were from a feed
     * @param url The url of the feed to pull from
     * @return A linked list containing all pertinent feeds, or null if none exist.
     */
    public LinkedList<Story> getSavedStories(String url){
        LinkedList<Story> ret = new LinkedList<>();
        while(savedStoryIterator().hasNext()){
            Story s = savedStoryIterator().next();
            if(s.getFeed().equalsIgnoreCase(url)) ret.add(s);
        }
        if(ret.isEmpty()) return null;
        else return ret;
    }

    public Iterator<Feed> iterator() { return allFeeds.iterator(); }
    public Iterator<Category> categoryIterator() { return categories.iterator(); }
    public Iterator<Story> savedStoryIterator(){ return saved.iterator(); }

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
