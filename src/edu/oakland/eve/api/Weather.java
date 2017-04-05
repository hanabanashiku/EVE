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
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;
import javax.swing.ImageIcon;

/**
 * @author Murilo Delgado
 * @version 1.5
 * @since 1.3
 * Gives weather info
 * Currently returns max temp, min temp, if it will rain, condition code, and an icon code for GUI
 * based off api.openweather.org
 */

    public class Weather {
        private double tempMax, tempMin;
        public String city;
        private String icon;
        private int condition;
        private boolean rain = false;
        private char measurement = 'F';
        private PrintStream file = new PrintStream(new FileOutputStream("Forecast.txt"));
        private PrintStream old = System.out;
        
        // API Key
        private final String APIKey = "18c88925203d14822e71a822f7cf107a";

        // constructor creates a file "Forecast.txt" that has a one line string input
        // with all the information
        public Weather(String city, char c) throws IOException, SAXException, TransformerException, ParserConfigurationException{          
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
            city = city;
            
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
                
                // grab icon for weather
                if (nextWord.contains("icon=")){
                    icon = nextWord;
                    
                    // clean up the info
                    icon = icon.replace("icon=", "");
                    icon = icon.replace('"', ' ');   
                    icon = icon.replace(" ", "");
                }
                
                // grab the weather condition code
                if (nextWord.contains("number=")){
                    String condNumber = nextWord;
                    
                    // clean up the info
                    condNumber = condNumber.replace("number=", "");
                    condNumber = condNumber.replace('"', ' ');
                    condNumber = condNumber.replace(" ", "");
                    condition = Integer.parseInt(condNumber);
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
        
        // method that returns a string condition for the UI
        private String conditionManager(int n){
            String answer = "";
                      
            if (n >= 200 && n <= 299)
                answer += "thunderstoms";
            
            if (n >= 300 && n <= 399)
                answer += "light drizzles";
            
            if (n >= 500 && n <= 599)
                answer += "rain showers";
            
            if (n >= 600 & n <= 699)
                answer += "snowing";
            
            if (n == 800)
                answer += "clear sky";
            
            if (n >= 800 && n <= 899)
                answer += "overcast";
            
            return answer;
        }
        
        // convert to ImageIcon
        private ImageIcon createImageIcon(String path, String description){          
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL, description);
            } else {
                System.err.println("Couldn't find file: " + path);
                return null;
            }
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
        public boolean willItRain(){
            return rain;
        }
        
        // getter method that returns icon ID
        public ImageIcon getIcon(){
            return createImageIcon("resources/" + icon + ".png", "weather icon");
        }
        
        // getter method that returns the condition code
        public String getCondition(){                     
            return conditionManager(condition);
        }
             
    }
