/**
 * ClientAuth The client authentication module for the initial handshake
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

import java.util.Scanner;

public class ImptClientAuth {

    // Authentication information
    private String _username;
    private String _password;
    private String _version = "IMPT 1.0";

    public String getAuthInfo() {
        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        System.out.println("Enter username");
        _username = myObj.nextLine(); // Read user input

        System.out.println("Enter password:"); // Output user input
        _password = myObj.nextLine();

        return buildOutputMessage(_username, _password);
    }

    /**
     * Build the client authentication message
     */
    private String buildOutputMessage(String username, String password) {
        // need encrypted password and username
        return "AUTH BEGIN " + username + " " + password;
    }

    public void handleInputMessage(String message) {
        String[] messageArr = message.split(" ");
        // need encrypted password and username
        if (messageArr[0] == "ERR_AUTH") {
            System.out.println("Opps, the username and password don't match, try again?");
            getAuthInfo();
        } else if (messageArr[0] == "AUTH") {
            ImptClient._myUserIdToken = messageArr[2];
            System.out.println("Logged in successfully");
            ImptClient._isLoggedIn = true;
        }
    }
}