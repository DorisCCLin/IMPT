/**
 * ClientAuth The client authentication module for the initial handshake
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

import java.util.Scanner;
import impt.common.*;

public class ImptClientInit {
    // Init information
    private String _recipientUsername;
    private String _recipientUserIdToken;
    private String _disconnectMessage;
    private ImptLogger _logger = new ImptLogger();

    public Boolean handleIncomingConnect(String initUsername, String initUserIdToken) {
        Boolean success = false;

        switch (initUsername) {
            case "none":
                _logger.printLog(this.getClass().toString(), ">> You are the only one Online. Waiting for other user...");
                break;
            default:
                _recipientUsername = initUsername;
                _recipientUserIdToken = initUserIdToken;
                _logger.printLog(this.getClass().toString(), initUsername + " is now connected with you!");
                success = true;
                break;
        }
        
        _logger.printLog(this.getClass().toString(), "\n** Type '#logout' or '#exit' anytime to disconnet **\n** Type '#payment' anytime to initiate payment **\n** Type '#help' anytime to view help on commands **");
        
        return success;
    }

    public boolean handleDisconnect() {
        Scanner disconnectConfirmScanner = new Scanner(System.in); // Create a Scanner object
        String response = "o";

        while(response.toLowerCase().equals("y") || response.toLowerCase().equals("n"))
        {
            _logger.printLog(this.getClass().toString(), ">> Are you sure you want to log out? (y/n) **");
            response = disconnectConfirmScanner.nextLine();

            if (response.toLowerCase().equals("y")) {
                _disconnectMessage = "DISCONNECT BEGIN " + ImptClient._myUserIdToken;
                disconnectConfirmScanner.close();

                return true;
            } else if (response.toLowerCase().equals("n")) {
                disconnectConfirmScanner.close();

                return false;
            }
        }
        
        disconnectConfirmScanner.close();
        return false;
    }

    public String getRecipientUsername() {
        return _recipientUsername;
    }

    public String getRecipientUserIdToken() {
        return _recipientUserIdToken;
    }

    public String getDisconnectMessage() {
        return _disconnectMessage;
    }
}