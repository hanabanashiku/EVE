package edu.oakland.eve.api;

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
/**
 * @author Murilo Delgado
 * @version 1.0
 * @since 1.0
 * Generates a XML File with weather info
 * based off api.openweather.org
 */

    public class Weather {

        // generateWeather takes in one parameter (city) and spits back
        // a XML formatted response with all weather information (temp in K)
        // interface will deal with converting temp and parsing in useful info only
        public static void generateWeather(String city) throws IOException, SAXException, TransformerException, ParserConfigurationException{
            // API Key does not change
            final String APIKey = "18c88925203d14822e71a822f7cf107a";

            // creating the URL
            String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&mode=xml&appid=" + APIKey;
            
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