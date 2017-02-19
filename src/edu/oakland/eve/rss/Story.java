package edu.oakland.eve.rss;

import com.google.api.client.util.DateTime;

import java.io.Serializable;
import java.net.URL;

import org.w3c.dom.*;

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
    private DateTime pubDate;
    private boolean read;

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

    /**
     * @return The date the story was published
     */
    public DateTime getPubDate() { return pubDate; }

    /**
     * Generate a new story object based on a feed entry.
     * @param item an entry from a feed
     */
    protected Story(Node item){

    }
}
