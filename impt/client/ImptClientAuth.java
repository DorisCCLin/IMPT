/**
 * ClientAuth The client authentication module for the initial handshake
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

import java.util.Scanner;
import impt.common.*;

public class ImptClientAuth {
    private ImptLogger _logger = new ImptLogger();

    public String getAuthInfo() {
        Scanner loginScanner = new Scanner(System.in); // Create a Scanner object

        _logger.printLog(this.getClass().toString(), "<< Enter username:");
        String username = loginScanner.nextLine(); // Read user input

        _logger.printLog(this.getClass().toString(), "<< Enter password:"); // Output user input
        String password = loginScanner.nextLine();

        loginScanner.close();

        return username + " " + password;
    }

    public String handleServerAuthResponse(String message) {
        String[] messageArr = message.split(" ");
        String userToken = null;
        String serverResponseCommand = messageArr[0];

        // need encrypted password and username
        if (serverResponseCommand.equals("ERR_AUTH")) {
            _logger.printLog(this.getClass().toString(),
                    "** Oops! The username and password do not match. Try again! :( **");
        } else if (serverResponseCommand.equals("AUTH")) {
            userToken = messageArr[2];
            _logger.printLog(this.getClass().toString(), "** Logged in successfully!");
        }

        return userToken;
    }
}