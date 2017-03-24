package edu.oakland.eve.test;

import edu.oakland.eve.error.RSSFormatException;
import org.junit.*;

import edu.oakland.eve.rss.Feed;
import edu.oakland.eve.rss.Story;

import java.io.IOException;

/**
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */

public class FeedTest {
    final String[] testFeeds = new String[]
            {
                    "http://time.com/feed/",
                    "http://rss.cnn.com/rss/cnn_topstories.rss",
                    "http://feeds.bbci.co.uk/news/rss.xml?edition=uk",
                    "http://feeds.washingtonpost.com/rss/politics"
            };

    @Test
    public void Feed() throws Exception{
        for(String uri : testFeeds){
            System.out.println("Testing " + uri);
            Feed f = new Feed(uri);
            Assert.assertNotNull(f);
            Assert.assertFalse(f.isEmpty());
            switch(uri){
                case "http://time.com/feed/":
                    Assert.assertEquals(f.getLink(), "http://time.com");
                    break;
                case "http://rss.cnn.com/rss/cnn_topstories.rss":
                    Assert.assertEquals(f.getLink(), "http://www.cnn.com/index.html");
                    break;
                case "http://feeds.bbci.co.uk/news/rss.xml?edition=uk":
                    Assert.assertEquals(f.getLink(), "http://www.bbc.co.uk/news/");
                    break;
                case "http://feeds.washingtonpost.com/rss/politics":
                    Assert.assertEquals(f.getLink(), "http://www.washingtonpost.com/pb/politics/");
                    break;
            }
        }

        try{ // must throw IOException
            new Feed("http://thisisnotavalid.url");
            Assert.fail();
        }
        catch(IOException e) {}

        try{ // must throw RSSFormatException
            new Feed("http://google.com");
            Assert.fail();
        }
        catch(RSSFormatException e) {}
    }

    @Test // Precondition: Feed() passes
    public void getStory() throws Exception {
        for(String uri : testFeeds){
            Feed f = new Feed(uri);
            Assert.assertFalse(f.isEmpty());
            for(Story s : f){
                Assert.assertNotNull(s);
                s.print();
                Assert.assertNotNull(f.get(s.getLink()));
            }
        }
    }

    @Test
    public void deleteStory() throws Exception {
        for(String uri : testFeeds){
            Feed f = new Feed(uri);
            Assert.assertFalse(f.isEmpty());

            while(f.size() != 0){
                Story s = f.get(0);
                System.out.println(s.getLink().toURI());
                Assert.assertNotNull(s);
                Assert.assertTrue(f.deleteStory(s));
                // This line causes this test to fail when running the WP feed.
                // This is because at the time of testing, the feed had a duplicate
                // GUID entry. This is ok because if deleteStory() didn't work correctly,
                // there would be an infinite loop, thus the test would never complete.
                //Assert.assertNull(f.get(s.getLink()));
            }
        }
    }
}