/**
 * ImptClientInit The client connection module for establish connection with other user
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

import java.util.Scanner;
import impt.common.*;

class ImptClientInit {
    // Init information
    private String _recipientUsername;
    private String _recipientUserIdToken;
    private String _disconnectMessage;
    private ImptLogger _logger = new ImptLogger();

    public Boolean handleIncomingConnect(String initUsername, String initUserIdToken) {
        Boolean success = false;
        switch (initUsername) {
            case "none":
                _logger.printLog(this.getClass().toString(), "You are the only one Online. Waiting for other user...",
                        ImptLoggerConfig.Level.PROMPT);
                break;
            default:
                _recipientUsername = initUsername;
                _recipientUserIdToken = initUserIdToken;
                _logger.printLog(this.getClass().toString(), initUsername + " is now connected with you!",
                        ImptLoggerConfig.Level.INFO);
                _logger.printLog(this.getClass().toString(),
                        "\n** Type '#logout' anytime to disconnect **\n** Type '#payment' anytime to initiate payment **\n** Type '#help' anytime to view help on commands **",
                        ImptLoggerConfig.Level.PROMPT);
                _logger.printLog(this.getClass().toString(), ImptClient._myUsername + ", what's on your mind? :)",
                        ImptLoggerConfig.Level.PROMPT);
                success = true;
                break;
        }

        return success;
    }

    public boolean handleDisconnect() {
        Scanner disconnectConfirmScanner = new Scanner(System.in); // Create a Scanner object
        String response = "o";

        _logger.printLog(this.getClass().toString(), ">> Are you sure you want to log out? (y/n) **",
                ImptLoggerConfig.Level.PROMPT);

        while (response.toLowerCase().equals("y") || response.toLowerCase().equals("n")) {
            if (disconnectConfirmScanner.hasNextLine()) {
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