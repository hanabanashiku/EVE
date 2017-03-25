package edu.oakland.eve.rss;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.*;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Iterator;
import java.util.Stack;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import edu.oakland.eve.error.RSSFormatException;

/***
 * Represents an RSS or Atom syndicated news feed
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Feed implements Serializable, Iterable<Story>{
    private String name;
    private String title;
    private String link;
    private URL url;
    private String description;
    private String imageUrl;
    private boolean isAtomFeed;
    private Stack<Story> stories = new Stack<>();

    /**
     * @return The feed name provided by the user
     */
    public String getName() { return name; }

    /**
     * Set the feed's name
     * @param value What to cal the feed
     */
    public void setName(String value) { name = value; }
    /**
     * @return The feed's title
     */
    public String getTitle() { return title; }

    /**
     * @return The feed's parent website URL, or null if missing
     */
    public String getLink() { if(link.equals("")) return null; return link; }

    /**
     * @return The RSS feed url object.
     */
    public URL getURL() { return url; }

    /**
     * @return The feed description
     */
    public String getDescription() { return description; }

    /**
     * @return The feed image URL
     */
    public String getImageUrl() { return imageUrl; }

    /***
     * Fetch the image from the feed's specified image URL and return it
     * @return AWT image, or null if the image URL is not defined or could not be loaded
     * TODO: Implement image caching if performance is an issue
     */
    public Image getImage(){
        try{ return ImageIO.read(new URL(imageUrl)); }
        catch(Exception e){ return null; }
    }

    public String getSuggestedCategory() {
        try {
            Document doc = parse();
            NodeList cat = doc.getElementsByTagName("category");
            if(cat.getLength() == 0) return null;
            if(isAtomFeed) // <category term="news" />
                return ((Element)cat.item(0)).getAttribute("term");
            else // <category>news</category>
                return cat.item(0).getTextContent();
        }
        catch(Exception e) { return null; }
    }

    /**
     * @return The number of stories saved in the feed
     */
    public int count() { return stories.size(); }

    /**
     * @return true if there are no stories saved in the feed
     */
    public boolean isEmpty(){ return stories.isEmpty(); }
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
                if(!channel.getNodeName().equalsIgnoreCase("rss")) throw new RSSFormatException("RSS tag was misnamed or does not exist");
                channel = getElementByName(channel.getChildNodes(), "channel");
                if(channel == null)
                    throw new RSSFormatException("Channel tag was misnamed or does not exist.");
            }

            updateMetaData(doc);
            pull();
            name = title;
        }
        catch(SAXException e) { throw new RSSFormatException(e.getMessage()); }

    }

    /**
     * Initialize a feed object
     * @param feedURL The URL to the RSS feed
     * @param feedName The name to call the feed by.
     * @throws RSSFormatException if the given file is not an RSS or Atom feed
     * @throws IOException if the URL could not be opened properly or if it was malformed.
     */
    public Feed(String feedURL, String feedName) throws RSSFormatException, IOException{
        this(feedURL);
        name = feedName;
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
            if(nl.getLength() == 0) imageUrl = "";
            else imageUrl = nl.item(0).getTextContent();
        }
        else{
            nl = doc.getElementsByTagName("image");
            imageUrl = "";
            if(nl.getLength() != 0) {
                nl = nl.item(0).getChildNodes();
                for(int i = 0; i < nl.getLength(); i++){
                    Node n = nl.item(i);
                    if(n.getNodeName().equals("link")){
                        imageUrl = n.getTextContent();
                        break;
                    }
                } // for
            } // if
        }
    } // updateMetadata()

    /**
     * Update the feed's stories
     * @throws RSSFormatException on RSS parse error
     * @throws IOException if the feed could not be accessed
     */
    public void pull() throws RSSFormatException, IOException{
      NodeList nl;
      Document doc;
      try { doc = parse(); }
      catch(SAXException e) { throw new RSSFormatException(e.getMessage()); }
      if(isAtomFeed) nl = doc.getElementsByTagName("entry");
      else nl = doc.getElementsByTagName("item");

      Story first = stories.isEmpty() ? null : stories.peek();
      for(int i = 0; i < nl.getLength(); i++){
        Node n = nl.item(i);
        String link = (isAtomFeed) ? getElementByName(n.getChildNodes(), "id").getNodeValue() : getElementByName(n.getChildNodes(), "guid").getNodeValue();
        // we caught up to the previous story
        if(first != null && first.getLink().sameFile(new URL(link)))
          break;
        stories.push(new Story(n, url.toString()));
      }
    }

    /**
     * @return The number of stories in the feed
     */
    public int size(){ return stories.size(); }
    /**
     * Returns whether or not a story is contained in the current feed.
     * @param s The story to look for
     * @return true if the story is in the feed.
     */
    public boolean contains(Story s) { return stories.contains(s); }

    /**
     * Get a story by uri
     * @param uri The uri to look for
     * @return The story on success, or null on failure
     */
    public Story get(URL uri){
        for(Story s : this)
            if(s.getLink().sameFile(uri))
                return s;
        return null;
    }

    /**
     * Get a story by uri
     * @param uri The uri to look for
     * @return The story on success, or null on failure
     * @throws MalformedURLException
     */
    public Story get(String uri) throws MalformedURLException{
        return get(new URL(uri));
    }

    /**
     * Get a story by index number, starting from 0
     * @param i The 0-based index
     * @return The story
     * @throws IndexOutOfBoundsException
     */
    public Story get(int i) throws IndexOutOfBoundsException {
        return stories.get(i);
    }

    /**
     * Delete a story from the list of stories.
     * @param s The story to delete
     * @return true on success
     * TODO: If the first story was deleted, it will come back when new stories are pulled.
     */
    public boolean deleteStory(Story s){
        return stories.remove(s);
    }

    protected static Node getElementByName(NodeList nl, String name){
        for(int i = 0; i < nl.getLength(); i++)
            if(nl.item(i).getNodeName().equals(name)) return nl.item(i);
        return null;

    }

    public Iterator<Story> iterator(){ return stories.iterator(); }

    @Override
    public boolean equals(Object o){
        return (o instanceof Feed) && ((Feed) o).getLink().equalsIgnoreCase(getLink());
    }
}
