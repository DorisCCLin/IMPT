/**
 * ClientAuth The client authentication module for the initial handshake
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

// import java.util.List;
import java.util.Scanner;

public class ImptClientInit {
    // Init information
    private String _recipientUsername;
    private String _acceptMessage;

    public String getInitInfo() {
        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        System.out.println("Hi there, who would you like to connect today?");
        System.out.println("Enter username");
        _recipientUsername = myObj.nextLine(); // Read user input

        return buildOutputMessage(_recipientUsername);
    }

    /**
     * Build the client authentication message
     */
    private String buildOutputMessage(String username) {
        System.out.println("checking if " + username + " is online...");
        return "INIT BEGIN " + username + " " + ImptClient._myUserIdToken;
    }

    public void handleConnectRequest(String initUsername) {

        System.out.println(initUsername + " wants to connect to you, okay? (y/n)");
        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        String response = myObj.nextLine();

        if (response == "y" || response == "n") {
            if (response == "y") {
                System.out.println("if you can get here" + initUsername);
            } else {
                System.out.println("if you can get here");
            }

        } else {
            System.out.println("if you can get here" + initUsername);
        }

        myObj.close();
    }

    public String getRecipientUsername() {
        return _recipientUsername;
    }
}