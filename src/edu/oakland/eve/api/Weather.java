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
import java.text.SimpleDateFormat;


/**
 * @author Murilo Delgado
 * @version 1.5 FINAL
 * Gives weather info
 * Currently returns max temp, min temp, if it will rain, condition code, and an icon code for GUI
 * based off api.openweather.org
 */

public class Weather {
    // ****  VARIABLE DECLARATION BEGINNING  ****

    private double tempMax, tempMin;
    public String city;
    private String icon;
    private int condition;
    private boolean rain = false;
    private char measurement = 'F';
    private PrintStream file = new PrintStream(new FileOutputStream("Forecast.txt"));
    private PrintStream futureFile = new PrintStream(new FileOutputStream("Future.txt"));
    private PrintStream old = System.out;

    // getting tomorrow's date
    private Date today = new Date();
    public Date day1 = new Date(today.getTime() + (1000 * 60 * 60 * 24));
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private String D1 = format.format(day1);

    // date for Day 2
    public Date day2 = new Date(day1.getTime() + (1000 * 60 * 60 * 24));
    private String D2 = format.format(day2);

    // date for Day 3
    public Date day3 = new Date(day2.getTime() + (1000 * 60 * 60 * 24));
    private String D3 = format.format(day3);

    // date for Day 4
    public Date day4 = new Date(day3.getTime() + (1000 * 60 * 60 * 24));
    private String D4 = format.format(day4);

    // variables for future forecast days
    private double tempD1, tempD2, tempD3, tempD4;
    private String iconD1, iconD2, iconD3, iconD4;

    // API Key
    private final String APIKey = "18c88925203d14822e71a822f7cf107a";

    // ****  VARIABLE DECLARATION ENDING  ****

    // constructor creates a file "Forecast.txt" that has a one line string input
    // with all the information
    public Weather(String city, char c) throws IOException, SAXException, TransformerException, ParserConfigurationException {


        // saving to file
        System.setOut(file);
        generateWeather(city);

        measurement = c;


        // calls cleanUp method to only get information we want
        // information extracted can be modified
        File forecast = new File("Forecast.txt");
        cleanUp(forecast);

        // grabbing future forecast
        System.setOut(futureFile);
        generateForecast(city);
        File futureForecast = new File("Future.txt");
        cleanFuture(futureForecast);
        System.setOut(old);

    }

    // generateWeather takes in one parameter (city) and spits back
    // a XML formatted response with all weather information (temp in K)
    // another method will deal with converting temp and parsing in useful info only
    private void generateWeather(String c) throws IOException, SAXException, TransformerException, ParserConfigurationException {
        city = c;

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

    // method that generates future forecast
    private void generateForecast(String c) throws IOException, SAXException, TransformerException, ParserConfigurationException {
        city = c;

        // creating the URL
        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + city + ",us&mode=xml&cnt=6&appid=" + APIKey;

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
    private void cleanUp(File f) throws FileNotFoundException {
        Scanner input = new Scanner(f);
        String tempMaxTemp = "", tempMinTemp = "";

        String nextWord;

        while (input.hasNext()) {
            nextWord = input.next();

            // grab max temp
            if (nextWord.contains("max=")) {
                tempMaxTemp = nextWord;

                // clean up the info
                tempMaxTemp = tempMaxTemp.replace("max=", "");
                tempMaxTemp = tempMaxTemp.replace('"', ' ');
            }

            // grab min temp
            if (nextWord.contains("min=")) {
                tempMinTemp = nextWord;

                // clean up the info
                tempMinTemp = tempMinTemp.replace("min=", "");
                tempMinTemp = tempMinTemp.replace('"', ' ');
            }

            // grab precip
            if (nextWord.contains("mode=")) {
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
            if (nextWord.contains("icon=")) {
                icon = nextWord;

                // clean up the info
                icon = icon.replace("icon=", "");
                icon = icon.replace('"', ' ');
                icon = icon.replace(" ", "");
            }

            // grab the weather condition code
            if (nextWord.contains("number=")) {
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

    // method to clean up future forecast
    private void cleanFuture(File f) throws FileNotFoundException {
        Scanner input = new Scanner(f);
        String nextWord;
        String d1Temp = "", d2Temp = "", d3Temp = "", d4Temp = "";

        while (input.hasNext()) {
            nextWord = input.next();

            if (nextWord.contains(D1)) {
                while (!nextWord.contains("/time")) {
                    nextWord = input.next();

                    if (nextWord.contains("var=")) {
                        iconD1 = nextWord;

                        iconD1 = iconD1.replace("var=", "");
                        iconD1 = iconD1.replace('"', ' ');
                        iconD1 = iconD1.replace(" ", "");
                        iconD1 = iconD1.replace("/><precipitation", "");
                        iconD1 = iconD1.replace("/><windDirection", "");
                    }

                    if (nextWord.contains("day=")) {
                        d1Temp = nextWord;

                        d1Temp = d1Temp.replace("day=", "");
                        d1Temp = d1Temp.replace('"', ' ');
                        d1Temp = d1Temp.replace(" ", "");
                    }
                }
            }

            if (nextWord.contains(D2)) {
                while (!nextWord.contains("/time")) {
                    nextWord = input.next();

                    if (nextWord.contains("var=")) {
                        iconD2 = nextWord;

                        iconD2 = iconD2.replace("var=", "");
                        iconD2 = iconD2.replace('"', ' ');
                        iconD2 = iconD2.replace(" ", "");
                        iconD2 = iconD2.replace("/><precipitation", "");
                        iconD2 = iconD2.replace("/><windDirection", "");
                    }

                    if (nextWord.contains("day=")) {
                        d2Temp = nextWord;

                        d2Temp = d2Temp.replace("day=", "");
                        d2Temp = d2Temp.replace('"', ' ');
                        d2Temp = d2Temp.replace(" ", "");
                    }
                }
            }

            if (nextWord.contains(D3)) {
                while (!nextWord.contains("/time")) {
                    nextWord = input.next();

                    if (nextWord.contains("var=")) {
                        iconD3 = nextWord;

                        iconD3 = iconD3.replace("var=", "");
                        iconD3 = iconD3.replace('"', ' ');
                        iconD3 = iconD3.replace(" ", "");
                        iconD3 = iconD3.replace("/><precipitation", "");
                        iconD3 = iconD3.replace("/><windDirection", "");
                    }

                    if (nextWord.contains("day=")) {
                        d3Temp = nextWord;

                        d3Temp = d3Temp.replace("day=", "");
                        d3Temp = d3Temp.replace('"', ' ');
                        d3Temp = d3Temp.replace(" ", "");
                    }
                }
            }

            if (nextWord.contains(D4)) {
                while (!nextWord.contains("/time")) {
                    nextWord = input.next();

                    if (nextWord.contains("var=")) {
                        iconD4 = nextWord;

                        iconD4 = iconD4.replace("var=", "");
                        iconD4 = iconD4.replace('"', ' ');
                        iconD4 = iconD4.replace(" ", "");
                        iconD4 = iconD4.replace("/><precipitation", "");
                        iconD4 = iconD4.replace("/><windDirection", "");
                    }

                    if (nextWord.contains("day=")) {
                        d4Temp = nextWord;

                        d4Temp = d4Temp.replace("day=", "");
                        d4Temp = d4Temp.replace('"', ' ');
                        d4Temp = d4Temp.replace(" ", "");
                    }
                }
            }
        }

        tempD1 = convertTemp(d1Temp);
        tempD2 = convertTemp(d2Temp);
        tempD3 = convertTemp(d3Temp);
        tempD4 = convertTemp(d4Temp);
    }

    // method that converts the temperature to whatever temperature the user desires
    private double convertTemp(String s) {
        // parsing the string into a double
        double x = Double.parseDouble(s);

        // converting to Celsius
        if (measurement == 'C')
            x -= 273;

        // converting to Farenheit
        if (measurement == 'F') {
            x = 1.8 * (x - 273) + 32;
        }

        return x;
    }

    // method that returns a string condition for the UI
    private String conditionManager(int n) {
        String answer = "";

        if (n >= 200 && n <= 299)
            answer += "Thunderstoms";

        if (n >= 300 && n <= 399)
            answer += "Light Drizzles";

        if (n >= 500 && n <= 599)
            answer += "Rain Showers";

        if (n >= 600 & n <= 699)
            answer += "Snowing";

        if (n == 800)
            answer += "Clear Sky";

        if (n >= 800 && n <= 899)
            answer += "Overcast";

        return answer;
    }

    // getter method for max temp
    public String getMaxTemp() {
        String temp = "";

        DecimalFormat degree = new DecimalFormat("#.0");
        temp += degree.format(tempMax);
        temp += "\u00b0";
        temp += " " + measurement;

        return temp;
    }

    // getter method for min temp
    public String getMinTemp() {
        String temp = "";

        DecimalFormat degree = new DecimalFormat("#.0");
        temp += degree.format(tempMin);
        temp += "\u00b0";
        temp += " " + measurement;

        return temp;
    }

    // getter method for temp for Day 1
    public String getTempD1() {
        String temp = "";

        DecimalFormat degree = new DecimalFormat("#.0");
        temp += degree.format(tempD1);
        temp += "\u00b0";
        temp += " " + measurement;

        return temp;
    }

    // getter method for temp for Day 2
    public String getTempD2() {
        String temp = "";

        DecimalFormat degree = new DecimalFormat("#.0");
        temp += degree.format(tempD2);
        temp += "\u00b0";
        temp += " " + measurement;

        return temp;
    }

    // getter method for temp for Day 3
    public String getTempD3() {
        String temp = "";

        DecimalFormat degree = new DecimalFormat("#.0");
        temp += degree.format(tempD3);
        temp += "\u00b0";
        temp += " " + measurement;

        return temp;
    }

    // getter method for temp for Day 4
    public String getTempD4() {
        String temp = "";

        DecimalFormat degree = new DecimalFormat("#.0");
        temp += degree.format(tempD4);
        temp += "\u00b0";
        temp += " " + measurement;

        return temp;
    }

    // getter method that returns boolean for rain or not
    public boolean willItRain() {
        return rain;
    }

    // convert to ImageIcon
    private ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    // getter method that returns icon ID
    public ImageIcon getIcon() {
        return createImageIcon("resources/weather/" + icon + ".png", "weather icon");
    }

    // getter method that returns icon ID for Day 1
    public ImageIcon getIconD1() {
        return createImageIcon("resources/weather/" + iconD1 + ".png", "weather icon");
    }

    // getter method that returns icon ID for Day 2
    public ImageIcon getIconD2() {
        return createImageIcon("resources/weather/" + iconD2 + ".png", "weather icon");
    }

    // getter method that returns icon ID for Day 3
    public ImageIcon getIconD3() {
        return createImageIcon("resources/weather/" + iconD3 + ".png", "weather icon");
    }

    // getter method that returns icon ID for Day 4
    public ImageIcon getIconD4() {
        return createImageIcon("resources/weather/" + iconD4 + ".png", "weather icon");
    }

    // getter method that returns the condition code
    public String getCondition() {
        return conditionManager(condition);
    }

}
