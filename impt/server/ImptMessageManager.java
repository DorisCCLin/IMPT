/**
 * ImptMessageManager is to handle incoming message and form the outgoing manage the message to be delivered.
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */
package impt.server;

import java.util.*;
import java.util.regex.*;

import impt.server.handlers.*;

class ImptMessageManger {
    // handle incoming message and form message and recipient info in
    // ClientMessageObject
    public ClientMessageObject handleClientMessage(String message) {
        boolean isMessageValid = isMessageValid(message);
        String[] messageArr = message.split(" ");
        ClientMessageObject clientMessageObject = null;

        if (isMessageValid) {
            clientMessageObject = new ClientMessageObject();
            clientMessageObject.command = messageArr[0];

            switch (messageArr[0]) {
                case "AUTH":
                    AuthenticationHandler authHandler = new AuthenticationHandler();
                    AuthenticationHandler.AuthenticationObject authObject = new AuthenticationHandler.AuthenticationObject();
                    authObject = authHandler.authenticate(messageArr[2], messageArr[3]);
                    clientMessageObject.isUserLoggedIn = authObject.isUserIdLoggedIn;
                    clientMessageObject.userIdToken = authObject.userIdToken;

                    if (authObject.isUserIdLoggedIn) {

                        clientMessageObject.message = "AUTH RES " + authObject.userIdToken;

                        if (ImptServer.activeUsers.size() == 0) {
                            clientMessageObject.initNoneUserMessage = "INIT BEGIN none none";
                        } else {
                            String otherUsername = ImptServer.activeUsers.keySet().iterator().next();
                            String otherUserIdToken = ImptServer.activeUsers.get(otherUsername);
                            clientMessageObject.otherUserIdToken = otherUserIdToken;

                            clientMessageObject.initCurrentUserMessage = "INIT BEGIN " + otherUsername + " "
                                    + otherUserIdToken;
                            clientMessageObject.initExistingUserMessage = "INIT BEGIN " + authObject.userName + " "
                                    + authObject.userIdToken;
                        }

                        ImptServer.activeUsers.put(authObject.userName, authObject.userIdToken);

                    }

                    if (authObject.hasAuthError) {
                        clientMessageObject.message = "ERR_AUTH BEGIN authError";
                    }
                    break;

                case "PAYSND":
                    PaymentHandler paymentHandler = new PaymentHandler();
                    PaymentHandler.PaymentObject paymentObject = new PaymentHandler.PaymentObject();
                    paymentObject = paymentHandler.sendPayment();
                    if (paymentObject.isPaymentSuccess) {
                        clientMessageObject.message = "PAYSND RES " + messageArr[3] + " success";
                    } else {
                        clientMessageObject.message = "PAYSND RES fail";
                    }

                    break;

                case "DISCONNECT":
                    clientMessageObject.userIdToken = messageArr[2];

                    if (ImptServer.activeUsers.size() > 1) {
                        String currentUsername = "";
                        for (Map.Entry<String, String> entry : ImptServer.activeUsers.entrySet()) {
                            if (entry.getValue().equals(messageArr[2])) {
                                currentUsername = entry.getKey();
                            }
                        }

                        String otherUserIdToken = messageArr[2];
                        clientMessageObject.otherUserIdToken = otherUserIdToken;
                        clientMessageObject.initExistingUserMessage = "DISCONNECT FIN " + currentUsername + " "
                                + messageArr[2];                          
                            
                        String otherUsername = ImptServer.activeUsers.keySet().iterator().next();
                        clientMessageObject.otherUserIdToken = ImptServer.activeUsers.get(otherUsername);
                    } else {
                            
                        while (ImptServer.activeUsers.values().remove(messageArr[2]));
                    }

                    clientMessageObject.message = "DISCONNECT FIN";

                    System.out.println("Active Users[MessageManager]: " + ImptServer.activeUsers);

                    break;
            }
        }
        
        return clientMessageObject;
    }

    // mockup message validation mechanism
    public boolean isMessageValid(String message) {
        String[] messageParts = message.split(" ");
        boolean hasCommand = Pattern.compile(".*[A-Z].*").matcher(messageParts[0]).matches();

        return hasCommand;
    }
}