import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Murilo Delgado
 * @version 1.0
 * @since 1.0
 * To do: main interface for eve
 */
public class EVE {
    private String name, city, temp;
    
    public static void main(String[] args) throws InterruptedException{
        System.out.print("Booting");
        
        int x = 0;
        while (x < 5){
            TimeUnit.SECONDS.sleep(1);
            System.out.print(".");
            x++;
        }
        System.out.println(); System.out.println(); System.out.println(); System.out.println();
        
        EVE ai = new EVE();
        
        ai.Setup();
    }
    // method to run on startup to set up settings
    public void Setup() throws InterruptedException{     
        Scanner answer = new Scanner(System.in); // scanner object to collect answers
        
        // first response
        System.out.println("Welcome! I am E.V.E, the AI Interface. "
                + "In order to better assist you, please answer these basic questions.");
        
        TimeUnit.SECONDS.sleep(3); // sleep for 3 seconds
        
        // taking in name
        System.out.println("Let's start with your name. What can I call you? ");
        name = answer.nextLine();
        
        // taking in city
        System.out.println("Nice to meet you " + name + ". "
                + "To provide more accurate information, please tell me the city where you live. ");
        city = answer.nextLine();
        
        // taking in temp preference
        System.out.println("Is Fahrenheit okay with you, or would you prefer I use something else? ");
        String tempAnswer = answer.nextLine();
        
        if (tempAnswer.equalsIgnoreCase("okay") || tempAnswer.equalsIgnoreCase("yes")){
            temp = "Fahrenheit";
            System.out.println("Alright, I will display temperature in Fahrenheit then.");
        } else if (tempAnswer.equalsIgnoreCase("something else") || tempAnswer.equalsIgnoreCase("no")){
            System.out.println("Would you prefer Celsius then?");
            if (answer.nextLine().equalsIgnoreCase("yes")){
                temp = "Celsius";
                System.out.println("Alright, I will display temperature in Celsius then.");
            } else {
                System.out.println("What the fuck do you want then?"); // CHANGE LATER
                temp = answer.nextLine();
            }
        } else{
            System.out.println("I'm sorry, I don't understand. "
                    + "I will display temperature in Fahrenheit unless changed later.");
            temp = "Fahrenheit";
        }
        
        TimeUnit.SECONDS.sleep(2); // sleep for 2 seconds
        
        System.out.println("Okay " + name +". I understand you live in " + city + " and prefer me to display temperature in " + temp + ". "
                + "If everything looks correct type OK or type CHANGE to change something.");
        tempAnswer = answer.nextLine();
        
        // prompt to change any setting originally put it (MIGHT BE SMARTER TO MAKE THIS A METHOD)
        if (tempAnswer.equalsIgnoreCase("change")){
            System.out.println("What do you need to modify? NAME, CITY, or TEMP?");
            tempAnswer = answer.nextLine();
            
            int counter = 0; // counter to trigger answer for repeating loops
            // while loop to configure settings till to user's liking
            while (!tempAnswer.equalsIgnoreCase("no")){
                
                // question that only happens after first loop
                if (counter >= 1){
                    System.out.println("Which one do you need to change?");
                    tempAnswer = answer.nextLine();
                }
                
                switch (tempAnswer.toLowerCase()){
                    case "name":
                        changeName(); // runs changeName method to modify name variable
                        break;
                    
                    case "city":
                        changeCity(); // runs changeCity method to modify city
                        break;
                    
                    case "temp":
                        changeTemp(); // runs changeTemp method to modify unit of measurement
                        break;
                }
                
                System.out.println("Do you still need NAME, CITY, or TEMP modified?");
                tempAnswer = answer.nextLine();
                counter++;
                System.out.println();
            }
        }
        
        System.out.println("Thank You for running the basic setup " + name + ".");
    }
    
    // method to easily change Name setting
    public void changeName(){
        Scanner answer = new Scanner(System.in); // scanner object to collect answers
        
        System.out.println("What would you like me to call you?");
        name = answer.nextLine();
        System.out.println("No problem " + name + ". System has been updated.");
    }
    
    // method to easily change Location setting
    public void changeCity(){
        Scanner answer = new Scanner(System.in); // scanner object to collect answers
        
        System.out.println("What would you like me to change your location to?");
        city = answer.nextLine();
        System.out.println("No problem " + name + ". City changed to " + city + ".");
    }
    
    // method to easily change Temperature setting
    public void changeTemp(){
        Scanner answer = new Scanner(System.in); // scanner object to collect answers
        
        System.out.println("What unit would you like temperature displayed at?");
        temp = answer.nextLine();
        System.out.println("No problem " + name + ". System has been updated.");
    }
}
