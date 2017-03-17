package edu.oakland.eve.rss;

import java.util.LinkedList;

/**
 * Provides an organizational structure for newsfeeds.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Category {
    private String name;
    private String description;
    private LinkedList<Feed> feeds;
    private RSSClient cli;

    /**
     * @param feedName The name of the feed category.
     * @param rcli The RSS client instance
     */
    protected Category(String feedName, RSSClient rcli){
        name = feedName;
        cli = rcli;
        feeds = new LinkedList<>();
    }

    /**
     * @param feedName The name of the feed category
     * @param desc The feed category description
     * @param rcli The RSS client instance
     */
    protected Category(String feedName, String desc, RSSClient rcli){
        name = feedName;
        description = desc;
        cli = rcli;
        feeds = new LinkedList<>();
    }

    /**
     * Add a feed to the category, and to the client if it does not exist
     * @param f The feed to add
     */
    public void addFeed(Feed f){
        if(!cli.contains(f)) cli.addFeed(f);
        feeds.add(f);
    }


}

