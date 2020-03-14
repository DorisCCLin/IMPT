/**
 * ClientAuth The client authentication module for the initial handshake
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

import java.util.Scanner;

public class ImptClientAuth {

    public String getAuthInfo() {
        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        System.out.println("Enter username:");
        String username = myObj.nextLine(); // Read user input

        System.out.println("Enter password:"); // Output user input
        String password = myObj.nextLine();

        return username + " " + password;
    }

    public String handleInputMessage(String message) {
        String[] messageArr = message.split(" ");
        String userToken = null;

        // need encrypted password and username
        if (messageArr[0].equals("ERR_AUTH")) {
            System.out.println("Opps, the username and password don't match, try again?");
        } else if (messageArr[0].equals("AUTH")) {
            userToken = messageArr[2];
            System.out.println("Logged in successfully");
        }

        return userToken;
    }
}