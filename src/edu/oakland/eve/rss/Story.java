package edu.oakland.eve.rss;

import com.google.api.client.util.DateTime;

import java.io.Serializable;
import java.net.URL;

/**
 * Represents a story from an RSS feed
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Story implements Serializable {
    private String title;
    private URL link;
    private String description;
    private String author;
    private String[] categories;
    private DateTime pubDate;

    /**
     * @return The title of the story
     */
    public String getTitle() { return title;}
    /**
     * @return The website URL of the story
     */
    public URL getLink() { return link; }

    /**
     * @return The story description
     */
    public String getDescription() { return description; }

    /**
     * @return The author (usually the email address)
     */
    public String getAuthor() { return author; }

    public String[] getCategories() { return categories; }

    public DateTime getPubDate() { return pubDate; }

    public Story(){

    }
}
