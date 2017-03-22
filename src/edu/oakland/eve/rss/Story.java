package edu.oakland.eve.rss;

import java.time.*;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.format.DateTimeParseException;
import javax.swing.*;

import org.w3c.dom.*;

import edu.oakland.eve.error.RSSFormatException;

/**
 * Represents a story from an RSS feed
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 * TODO: Provide a way to retrieve content (either description tag, or rendering of website)
 */
public class Story implements Serializable {
    private String title;
    private URL link;
    private String description;
    private String author;
    private LocalDateTime pubDate;
    private boolean read;
    private boolean saved;

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
    public LocalDateTime getPubDate() { return pubDate; }

    /**
     * @return true if the user has read the story already.
     */
    public boolean wasRead() { return read; }

    /**
     * Set the story as ready if unread, and vice versa.
     */
    public void toggleRead() { read = !read; }

    /**
     * Returns a GUI component containing the HTML rendering of the feed url
     * @return a GUI component
     *
    public JComponent fullHTMLDisplay(){
    }*/

    /**
     * Print the story contents to the commandline. Mostly for testing purposes.
     * For GUI applications, we prefer fullHTMLDisplay()
     */
    public void print(){
        System.out.println(title + " : " + link + " : " + pubDate);
        System.out.println(author);
        System.out.println(description);
        System.out.println("Read: " + read);
    }

    /**
     * Generate a new story object based on a feed entry.
     * @param item an entry from a feed
     * @throws RSSFormatException if an entry does not meet specifications.
     */
    protected Story(Node item, boolean isAtomNode) throws RSSFormatException{
        NodeList nl = item.getChildNodes();
        if(isAtomNode){
            for(int i = 0; i < nl.getLength(); i++){
                Node n = nl.item(i);
                switch(n.getNodeName()){ // deal with tags as we find them
                    case "title":
                        title = n.getTextContent();
                        break;
                    case "id":
                        try {
                            link = new URL(n.getTextContent());
                        } catch(MalformedURLException e) { throw new RSSFormatException(e.getMessage()); }
                        break;
                    case "updated":
                        try { pubDate = LocalDateTime.parse(n.getTextContent()); }
                        catch(DateTimeParseException e) { throw new RSSFormatException(e.getMessage()); }
                        break;
                    case "summary":
                        description = n.getTextContent();
                        break;
                    case "author": // <author><name>Name</name></author>
                        author = n.getChildNodes().item(0).getTextContent();
                        break;
                }
            }
            if(title == null || link == null || pubDate == null) throw new RSSFormatException("[Atom] One or more require sub-elements are missing from the story.");
        }

        else{
           for(int i = 0; i < nl.getLength(); i++){
               Node n = nl.item(i);
               switch(n.getNodeName()){
                   case "title":
                       title = n.getTextContent();
                       break;
                   case "guid":
                       try { link = new URL(n.getTextContent()); }
                       catch(MalformedURLException e) { throw new RSSFormatException(e.getMessage()); }
                       break;
                   case "description":
                       description = n.getTextContent();
                       break;
                   case "author":
                       author = n.getTextContent();
                       break;
                   case "pubDate":
                       try{ pubDate = LocalDateTime.parse(n.getTextContent()); }
                       catch(DateTimeParseException e) { throw new RSSFormatException(e.getMessage()); }
                       break;
               }
               if(title == null && description == null) throw new RSSFormatException("[RSS] Feed must have either a title or a description.");
               else if(title == null) title = description;
           }
        }

        read = false;
        saved = false;
    }
}
