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

public class ImptMessageManager {
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
                    authHandler.authenticate(messageArr[2], messageArr[3]);
                    AuthenticationHandler.AuthenticationObject authObject = authHandler.getAuthenticationObject();
                    clientMessageObject.isUserLoggedIn = authObject.isUserIdLoggedIn;
                    clientMessageObject.userIdToken = authObject.userIdToken;

                    if (authObject.isUserIdLoggedIn) {
                        switch (ImptServer.activeUsers.size()) {
                            // If currently no other users on the server, add the user to server and send
                            // none init message.
                            case 0:
                                clientMessageObject.message = "AUTH RES " + authObject.userIdToken;
                                clientMessageObject.initNoneUserMessage = "INIT BEGIN none none";
                                ImptServer.activeUsers.put(authObject.userName, authObject.userIdToken);
                                break;

                            // If currently one other user on the server, add the user to server and send
                            // init-begin message to both users.
                            case 1:
                                // If it's the same user login again
                                if (ImptServer.activeUsers.containsKey(authObject.userName)) {
                                    clientMessageObject.message = "ERR_AUTH BEGIN sameUser";

                                } else {
                                    clientMessageObject.message = "AUTH RES " + authObject.userIdToken;
                                    String otherUsername = ImptServer.activeUsers.keySet().iterator().next();
                                    String otherUserIdToken = ImptServer.activeUsers.get(otherUsername);
                                    clientMessageObject.otherUserIdToken = otherUserIdToken;

                                    // INIT Message handling
                                    clientMessageObject.initCurrentUserMessage = "INIT BEGIN " + otherUsername + " "
                                            + otherUserIdToken;
                                    clientMessageObject.initExistingUserMessage = "INIT BEGIN " + authObject.userName
                                            + " " + authObject.userIdToken;

                                    ImptServer.activeUsers.put(authObject.userName, authObject.userIdToken);

                                    // PAYMENT Message handling - prepare payment info
                                    PaymentHandler authPaymentHandler = new PaymentHandler();
                                    authPaymentHandler.preparePaymentInfo(authObject.userName, otherUsername);
                                    PaymentHandler.PaymentObject authPaymentObject = authPaymentHandler
                                            .getPaymentObject();

                                    if (authPaymentObject.hasPaymentError) {
                                        clientMessageObject.payInfoMessage = "ERR_PAY BEGIN noServices";

                                    } else {
                                        clientMessageObject.payInfoMessage = "PAYINFO BEGIN "
                                                + authPaymentObject.matchedPaymentServices;
                                    }

                                }
                                break;

                            // If currently one other user on the server, add the user to server and send
                            // init-begin message to both users.
                            case 2:
                                clientMessageObject.message = "ERR_AUTH BEGIN serverFull";
                                break;

                            default:
                                clientMessageObject.message = "ERR_AUTH BEGIN unknown";
                                break;
                        }
                    }

                    // user login error
                    if (authObject.hasAuthError) {
                        clientMessageObject.message = "ERR_AUTH BEGIN authError";
                    }
                    break;

                case "PAYSND":
                    clientMessageObject.otherUserIdToken = messageArr[2];
                    clientMessageObject.message = "PAYSND RES " + messageArr[3] + messageArr[4];

                    break;

                case "PAYACCEPT":
                    clientMessageObject.message = "PAYSND RES fail";
                    PaymentHandler paymentHandler = new PaymentHandler();
                    paymentHandler.sendPayment();
                    PaymentHandler.PaymentObject paymentObject = paymentHandler.getPaymentObject();

                    if (paymentObject.isPaymentSuccess) {
                        clientMessageObject.message = "PAYACCEPT RES " + messageArr[3] + " success";
                    } else {
                        clientMessageObject.message = "PAYACCEPT RES fail";
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

                        while (ImptServer.activeUsers.values().remove(messageArr[2]))
                            ;
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