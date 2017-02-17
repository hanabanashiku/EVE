package edu.oakland.eve.rss;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.*;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

/***
 * Represents an RSS feed
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

    /**
     * @return The feed's title
     */
    public String getTitle() { return title; }

    /**
     * @return The feed's parent website URL
     */
    public String getLink() { return link; }

    /**
     * @return The feed description
     */
    public String getDescription() { return description; }

    /**
     * @return The feed image URL
     */
    public String getImageurl() { return imageurl; }

    /***
     * Fetch the image from the URL and return it
     * @return AWT image file
     * //TODO: Implement image caching if performance is an issue
     */
    public Image getImage(){
        try{
            return ImageIO.read(new URL(imageurl));
        }
        catch(Exception e){
            return null;
        }
    }

    /**
     * Initialize a feed object
     * @param feedURL The URL to the RSS feed
     * @throws MalformedURLException
     * //TODO: Refactor try/catch blocks when error handling solutions are decided on
     */
    public Feed(String feedURL) throws MalformedURLException{
        try{
            this.url = new URL(feedURL);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = DocumentBuilderFactory.
                    newInstance().
                    newDocumentBuilder();
            Document doc = db.parse(url.openStream());
            if(doc.getDoctype().getName() != "rss") throw new Exception("Not an RSS feed");
            doc.normalizeDocument(); // make sure everything is lined up properly; probably not an issue

            Node channel = doc.getDocumentElement();
            if(channel.getNodeName() == "feed") isAtomFeed = true;
            else if(channel.getNodeName() == "rss") isAttomFeed = false;
            else throw new Exception("Not a feed");
            

        }
        catch(Exception e){}

    }
}
