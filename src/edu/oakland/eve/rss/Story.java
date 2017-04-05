package edu.oakland.eve.rss;

import java.time.*;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.w3c.dom.*;


import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

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
    private String feed;
    private String description;
    private String author;
    private LocalDateTime pubDate;
    private boolean read;
    private boolean saved;

    private String[] datePatterns = {"E, d MMM y H:m:s Z", "E, d MMM y H:m:s z"};

    /**
     * @return The title of the story
     */
    public String getTitle() { return title;}
    /**
     * @return The website URL of the story
     */
    public URL getLink() { return link; }

    /**
     * @return The URL of the feed that the story was pulled from.
     */
    public String getFeed(){ return feed; }

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
     * Setting this property is up to the discretion of the EVE interface.
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
     * @param origfeed the original feed url
     * @throws RSSFormatException if an entry does not meet specifications.
     */
    protected Story(Node item, String origfeed) throws RSSFormatException{
        feed = origfeed;
        NodeList nl = item.getChildNodes();
        if(item.getNodeName().equalsIgnoreCase("entry")){
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

        else if(item.getNodeName().equalsIgnoreCase("item")){
            // this fixes a bug where all the sub-elements of <item> were being
            // interpreted as DOM#text elements instead of XML tags for some odd reason
            // Working around the limitations of the DOM implementation at this point
            Element n = (Element)item;
            // Format for pubDate: <pubDate>Sun, 19 May 2002 15:21:36 GMT</pubDate>
            DateTimeFormatter dform = DateTimeFormatter.ofPattern("");
            NodeList el;
            // look for title tag
            el = n.getElementsByTagName("title");
            if(el.getLength() != 0)
                title = el.item(0).getTextContent();
            // look for guid tag
            el = n.getElementsByTagName("guid");
            if(el.getLength() != 0) {
                try {
                    link = new URL(el.item(0).getTextContent());
                } catch (MalformedURLException e) {
                    throw new RSSFormatException("Error parsing URL: " + e.getMessage());
                }
            }
            // look for description tag
            el = n.getElementsByTagName("description");
            if(el.getLength() != 0){
                description = el.item(0).getTextContent();
            }
            // look for author tag - RSS only supports one tag
            // this usually returns an email address if it is defined.
            el = n.getElementsByTagName("author");
            if(el.getLength() != 0){
                author = el.item(0).getTextContent();
            }
            // look for pubdate tag
            el = n.getElementsByTagName("pubDate");
            if(el.getLength()!= 0){
                String datestr = el.item(0).getTextContent();
                for(String p : datePatterns){
                    try{
                        pubDate = LocalDateTime.from(DateTimeFormatter.ofPattern(p).parse(datestr));
                        break;
                    }
                    catch(DateTimeException e) {}
                }
                if(pubDate == null) throw new RSSFormatException("Could not parse pubDate " + datestr);
            }

            if(title == null && description == null) throw new RSSFormatException("[RSS] Feed must have either a title or a description.");
            else if(title == null) title = description;
        }
        else throw new RSSFormatException("Could not parse story in " + origfeed);

        read = false;
        saved = false;
    }

    public BrowserView getArticleView(){
       Browser b = new Browser();
       BrowserView bv = new BrowserView(b);
       b.loadURL(link.toString());
       return bv;
    }
}