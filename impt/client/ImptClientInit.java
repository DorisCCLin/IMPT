/**
 * ImptClientInit The client connection module for establish connection with other user
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

import java.util.Scanner;

public class ImptClientInit {
    // Init information
    private String _recipientUsername;
    private String _disconnectMessage;

    public void handleIncomingConnect(String initUsername) {
        ImptClient imptClient = new ImptClient();
        switch (initUsername) {
            case "none":
                System.out.println("You are the only one online, idling...");

                break;
            default:
                System.out.println(initUsername + " is now connected with you.");

        }
        imptClient.toggleIsAwaitingResponseFromServer();
        return;
    }

    public void handleDisconnect() {
        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        System.out.println("Are you sure to log out?(y/n)");
        String response = myObj.nextLine();
        if (response.equals("y") || response.equals("n")) {
            if (response.equals("y")) {
                _disconnectMessage = "DISCONNECT BEGIN " + ImptClient._myUserIdToken;
            } else {
                return;
            }

        } else {
            handleDisconnect();
        }

    }

    public String getRecipientUsername() {
        return _recipientUsername;
    }

    public String getDisconnectMessage() {
        return _disconnectMessage;
    }
}