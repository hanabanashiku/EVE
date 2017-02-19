package edu.oakland.eve.rss;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.*;
import javax.xml.parsers.ParserConfigurationException;
import java.util.LinkedList;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import edu.oakland.eve.error.RSSFormatException;

/***
 * Represents an RSS or Atom syndicated news feed
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Feed implements Serializable{
    private String title;
    private String link;
    private URL url;
    private String description;
    private String imageurl;
    private boolean isAtomFeed;
    private LinkedList<Story> stories;

    /**
     * @return The feed's title
     */
    public String getTitle() { return title; }

    /**
     * @return The feed's parent website URL, or null if missing
     */
    public String getLink() { if(link.equals("")) return null; return link; }

    /**
     * @return The feed description
     */
    public String getDescription() { return description; }

    /**
     * @return The feed image URL
     */
    public String getImageurl() { return imageurl; }

    /***
     * Fetch the image from the feed's specified image URL and return it
     * @return AWT image file, or null if the image URL is not defined or could not be loaded
     * TODO: Implement image caching if performance is an issue
     */
    public Image getImage(){
        try{ return ImageIO.read(new URL(imageurl)); }
        catch(Exception e){ return null; }
    }

    /**
     * Initialize a feed object
     * @param feedURL The URL to the RSS feed
     * @throws RSSFormatException if the given file is not an RSS or Atom feed
     * @throws IOException if the URL could not be opened properly or if it was malformed.
     * TODO: Refactor try/catch blocks when error handling solutions are decided on
     */
    public Feed(String feedURL) throws RSSFormatException, IOException{
        try{
            this.url = new URL(feedURL);
            Document doc = parse();
            if(doc == null) throw new RSSFormatException("Parse error");

            Node channel = doc.getDocumentElement();
            if(channel.getNodeName().equalsIgnoreCase("feed")) isAtomFeed = true;
            else if(channel.getNodeName().equalsIgnoreCase("rss")) isAtomFeed = false;
            else throw new RSSFormatException("Stream is of type: " + doc.getDoctype().getName());

            // Atom feeds use the parent tag, <feed>, while RSS feeds utilize the <channel> tag,
            // which is always the first and only child off the <rss> tag.
            if(!isAtomFeed){
                channel = channel.getFirstChild();
                if(!channel.getNodeName().equalsIgnoreCase("channel"))
                    throw new RSSFormatException("Channel tag was misnamed or does not exist.");
            }

            updateMetaData(doc);
            pullLatestStories(doc);
        }
        catch(SAXException e) { throw new RSSFormatException(e.getMessage()); }

    }

    // use this to parse the file
    private Document parse() throws IOException, SAXException{
        try{
            DocumentBuilder db = DocumentBuilderFactory.
                    newInstance().
                    newDocumentBuilder();
            Document doc = db.parse(url.openStream()); // will throw SAXException upon failure
            doc.normalizeDocument(); // make sure everything is lined up properly; probably not an issue
            return doc;
        }
        catch(ParserConfigurationException e) { e.printStackTrace(); return null; }
    }

    /**
     * Grabs the latest version of the metadata from the feed.
     * @throws RSSFormatException if the feed does not follow the correct standards.
     * @throws IOException if the feed cannot be opened.
     */
    public void updateMetadata() throws RSSFormatException, IOException{
        try { updateMetaData(parse()); }
        catch(SAXException e) { throw new RSSFormatException(e.getMessage()); }
    }
    private void updateMetaData(Document doc) throws RSSFormatException{
        NodeList nl;

        // Title tag
        nl = doc.getElementsByTagName("title");
        if(nl.getLength() == 0) throw new RSSFormatException("Title tag is missing.");
        title = nl.item(0).getTextContent();

        // link tag
        nl = doc.getElementsByTagName("link");
        if(nl.getLength() == 0){ // RSS feeds require this, Atom does not
            if(!isAtomFeed) throw new RSSFormatException("[RSS] Link tag is missing.");
            else link = "";
        }
        else // for all intents and purposes, we will assume the first link is the one we want in an atom feed
            link = (isAtomFeed) ?
                   // Atom feeds use <link href="url" />
                   ((Element)nl.item(0)).getAttribute("href") :
                   // RSS feeds use <link>url</link>
                   nl.item(0).getTextContent();

        // description tag
        if(isAtomFeed){
            nl = doc.getElementsByTagName("subtitle");
            if(nl.getLength() == 0) description = "";
            else description = nl.item(0).getTextContent();
        }
        else{ // RSS requires the description tag
            nl = doc.getElementsByTagName("description");
            if(nl.getLength() == 0) throw new RSSFormatException("[RSS] Description tag not found.");
            else description = nl.item(0).getTextContent();
        }

        // image tag
        if(isAtomFeed){
            nl = doc.getElementsByTagName("logo");
            if(nl.getLength() == 0) imageurl = "";
            else imageurl = nl.item(0).getTextContent();
        }
        else{
            nl = doc.getElementsByTagName("image");
            imageurl = "";
            if(nl.getLength() != 0) {
                nl = nl.item(0).getChildNodes();
                for(int i = 0; i < nl.getLength(); i++){
                    Node n = nl.item(i);
                    if(n.getNodeName().equals("link")){
                        imageurl = n.getTextContent();
                        break;
                    }
                } // for
            } // if
        }
    } // updateMetadata()

    /**
     * Update the feed's stories
     * @throws RSSFormatException
     * @throws IOException
     */
    public void pullLatestStories() throws RSSFormatException, IOException{
        try { pullLatestStories(parse()); }
        catch(SAXException e) { throw new RSSFormatException(e.getMessage()); }
    }
    private void pullLatestStories(Document doc){

    }
}
