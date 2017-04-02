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
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;
/**
 * @author Murilo Delgado
 * @version 1.1
 * @since 1.0
 * Gives weather info
 * Currently returns max temp, min temp, and if it will rain
 * based off api.openweather.org
 */

    public class Weather {
        private double tempMax, tempMin;
        private boolean rain = false;
        private char measurement = 'F';
        private PrintStream file = new PrintStream(new FileOutputStream("Forecast.txt"));
        private PrintStream old = System.out;
        
        // API Key
        private final String APIKey = "18c88925203d14822e71a822f7cf107a";

        // constructor creates a file "Forecast.txt" that has a one line string input
        // with all the information
        private Weather(String city, char c) throws IOException, SAXException, TransformerException, ParserConfigurationException{          
            // saving to file
            System.setOut(file);
   
            generateWeather(city);
            measurement = c;
            
            // calls cleanUp method to only get information we want
            // information extracted can be modified         
            File forecast = new File("Forecast.txt");
            cleanUp(forecast);
            System.setOut(old);
        }

        // generateWeather takes in one parameter (city) and spits back
        // a XML formatted response with all weather information (temp in K)
        // another method will deal with converting temp and parsing in useful info only
        private void generateWeather(String city) throws IOException, SAXException, TransformerException, ParserConfigurationException{
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
        
        // method that takes in a File f and "cleans it up"
        // meaning it scrapes off useful information and spits it back
        private void cleanUp(File f) throws FileNotFoundException{             
            Scanner input = new Scanner(f);
            String tempMaxTemp = "", tempMinTemp = "";
        
            String nextWord;
        
            while (input.hasNext()){
                nextWord = input.next();

                // grab max temp
                if (nextWord.contains("max=")){
                    tempMaxTemp = nextWord;
                
                    // clean up the info
                    tempMaxTemp = tempMaxTemp.replace("max=", "");
                    tempMaxTemp = tempMaxTemp.replace('"', ' ');
                }
            
                // grab min temp
                if (nextWord.contains("min=")){
                    tempMinTemp = nextWord;
                
                    // clean up the info
                    tempMinTemp = tempMinTemp.replace("min=", "");
                    tempMinTemp = tempMinTemp.replace('"', ' ');
                }
            
                // grab precip
                if (nextWord.contains("mode=")){
                    String rainTemp = nextWord;
                
                    // clean up the info
                    rainTemp = rainTemp.replace("mode=", "");
                    rainTemp = rainTemp.replace('"', ' ');
                    rainTemp = rainTemp.replace("/><weather", "");
                    
                    if (rainTemp == "yes")
                        rain = true;
                    
                    if (rainTemp == "no")
                        rain = false;
                }
                
                // grab condition
                if (nextWord.contains("number=")){
                    
                }
                
                // grab icon for weather
                if (nextWord.contains("icon=")){
                    
                }
            }
            
            tempMax = convertTemp(tempMaxTemp);
            tempMin = convertTemp(tempMinTemp);
                   
        }
        
        // method that converts the temperature to whatever temperature the user desires
        private double convertTemp(String s){
            // parsing the string into a double
            double x = Double.parseDouble(s);
            
            // converting to Celsius
            if (measurement == 'C')
                x -= 273;
            
            // converting to Farenheit
            if (measurement == 'F'){
                x = 1.8 * (x - 273) + 32;
            }
            
            return x;
        }
        
        // getter method for max temp
        public String getMaxTemp(){
            String temp = "";
            
            DecimalFormat degree = new DecimalFormat("#.0");
            temp += degree.format(tempMax);
            temp += "\u00b0";
            
            return temp;
        }
        
        // getter method for min temp
        public String getMinTemp(){
            String temp = "";
            
            DecimalFormat degree = new DecimalFormat("#.0");
            temp += degree.format(tempMin);
            temp += "\u00b0";
            
            return temp;
        }
        
        // getter method that returns boolean for rain or not
        public String willItRain(){
            String answer = "";
            if (rain == true)
                answer += "Yes";
            
            else
                answer += "No";
            
            return answer;
        }
    }