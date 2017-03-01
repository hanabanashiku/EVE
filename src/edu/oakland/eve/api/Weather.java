import java.io.IOException;
import org.xml.sax.SAXException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import java.net.URLConnection;

    public class Weather {

        public static void main(String[] args) throws IOException, SAXException, TransformerException, ParserConfigurationException{
            generateWeather("Chicago","18c88925203d14822e71a822f7cf107a");
        }

        public static void generateWeather(String city, String APIkey) throws IOException, SAXException, TransformerException, ParserConfigurationException{
            // creating the URL
            String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&mode=xml&appid=" + APIkey;
            
            // printing out XML
            URL urlString = new URL(url);
            URLConnection conn = urlString.openConnection();
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(conn.getInputStream());

            TransformerFactory transformer = TransformerFactory.newInstance();
            Transformer xform = transformer.newTransformer();
            
            xform.transform(new DOMSource(doc), new StreamResult(System.out));
        }    
    }