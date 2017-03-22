package edu.oakland.eve.test;

import edu.oakland.eve.rss.Feed;
import edu.oakland.eve.rss.Story;
import org.junit.*;
import edu.oakland.eve.rss.RSSClient;

import java.io.File;

/**
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class RSSClientTest {
    final String[] testFeeds = new String[]
            {
                    "http://time.com/feed/",
                    "http://rss.cnn.com/rss/cnn_topstories.rss",
                    "http://feeds.bbci.co.uk/news/rss.xml?edition=uk",
                    "http://feeds.washingtonpost.com/rss/politics"
            };
    RSSClient cli;
    @Before
    public void setUp() throws Exception {
        new File("rss_cli.db").delete();
        cli = RSSClient.load();
    }

    @After
    public void tearDown() throws Exception {
        cli.save();
    }

    @Test // Precondition: isEmpty(), Feed() passes.
    public void loadSave() throws Exception{
        Assert.assertTrue(cli.isEmpty());
        for(String url : testFeeds)
            cli.add(new Feed(url));
        Assert.assertFalse(cli.isEmpty());
        cli.save();
        cli = RSSClient.load();
        Assert.assertFalse(cli.isEmpty());
    }

    @Test // Precondition: get(), Feed() passes.
    public void add() throws Exception {
        for(String url : testFeeds){
            cli.add(new Feed(url));
            Assert.assertNotNull(cli.get(url));
        }
    }

    @Test // Precondition: add(), Feed() passes
    public void contains() throws Exception {
        for(String url : testFeeds){
            Feed f = new Feed(url);
            Assert.assertFalse(cli.contains(f));
            cli.add(f);
            Assert.assertTrue(cli.contains(f));
        }
    }

    @Test // Precondition: add(), isEmpty(), Feed() passes
    public void remove() throws Exception{
        Feed[] feeds = new Feed[testFeeds.length];
        for(int i = 0; i < testFeeds.length; i++){
            feeds[i] = new Feed(testFeeds[i]);
            cli.add(feeds[i]);
        }
        for(Feed f : feeds)
            Assert.assertTrue(cli.remove(f));
        Assert.assertTrue(cli.isEmpty());
    }

    @Test // Precondition: getCategory() passes
    public void addCategory() throws Exception {
        Assert.assertNull(cli.getCategory("News"));
        Assert.assertNull(cli.getCategory("Sports"));
        cli.addCategory("News");
        cli.addCategory("Sports");
        Assert.assertNotNull(cli.getCategory("News"));
        Assert.assertNotNull(cli.getCategory("Sports"));
    }

    @Test // Precondition: addCategory(), getCategory() passes
    public void removeCategory() throws Exception {
        cli.addCategory("News");
        cli.addCategory("Sports");
        Assert.assertTrue(cli.removeCategory(cli.getCategory("Sports")));
        Assert.assertTrue(cli.removeCategory(cli.getCategory("News")));
        Assert.assertNull(cli.getCategory("News"));
        Assert.assertNull(cli.getCategory("Sports"));
    }

    @Test
    public void saveStory() throws Exception{
        Feed f = new Feed(testFeeds[0]);
        Assert.assertFalse(f.isEmpty());
        cli.add(f);
        Assert.assertTrue(cli.contains(f));
        for(Story s : f){
            cli.saveStory(s);
            Assert.assertTrue(cli.getSavedStories(f).contains(s));
        }
    }

    @Test
    public void removeSavedStory() throws Exception{
        Feed f = new Feed(testFeeds[0]);
        Assert.assertFalse(f.isEmpty());
        cli.add(f);
        Assert.assertTrue(cli.contains(f));
        for(Story s : f)
            cli.saveStory(s);

        for(Story s : f){
            Assert.assertTrue(cli.removeSavedStory(s));
            Assert.assertFalse(cli.getSavedStories(f).contains(s));
        }
    }
}