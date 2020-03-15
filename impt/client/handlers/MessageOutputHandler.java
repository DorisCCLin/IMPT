package impt.client.handlers;

import java.util.*;
import java.io.*;
import java.net.*;
import impt.common.*;
import impt.client.*;

public class MessageOutputHandler implements Runnable {
    private static ImptLogger _logger = new ImptLogger();
    private Socket _clientSocket;
    private String _version;

    public MessageOutputHandler(Socket clientSocket, String version) {
        _clientSocket = clientSocket;
        _version = version;
    }

    @Override
    public void run() {
        Scanner userInputScanner = new Scanner(System.in);
        DataOutputStream outputStream = null;
        _logger.printLog(this.getClass().toString(),
                "**** MessageOutputHandler Thread Name: " + Thread.currentThread().getName(), ImptLoggerConfig.Level.DEBUG);

        try {
            outputStream = new DataOutputStream(_clientSocket.getOutputStream());
        } catch (Exception ex) {
            _logger.printLog(this.getClass().toString(),
                    "** Could not establish output stream connection with the server.", ImptLoggerConfig.Level.INFO);
            ex.printStackTrace();
        }

        try {
            while (true) {
                if (!ImptClient._isAwaitingResponseFromServer) {
                    if (!ImptClient._isLoggedIn) { // LOGIN
                        ImptClientAuth clientAuth = new ImptClientAuth();
                        String credential = clientAuth.getAuthInfo(userInputScanner);
                        outputStream.writeUTF(buildAuthOutputMessage(credential));
                        ImptClient._myUsername = credential.split(" ")[0];
                        ImptClient._isAwaitingResponseFromServer = true;

                        _logger.printLog(this.getClass().toString(), "** Logging in...", ImptLoggerConfig.Level.INFO);
                    } else {
                        if (ImptClient._isConnectedToOther) {
                            String message = userInputScanner.nextLine();

                            if (!message.isEmpty()) {
                                // handleGeneralUserInput(message.toLowerCase(), outputStream);

                                switch (message.toLowerCase()) {
                                    // LOGOUT
                                    case "#logout":
                                    case "#exit":
                                        ImptClientInit clientInit = new ImptClientInit();
                                        Boolean disconnectConfirmed = clientInit.handleDisconnect();

                                        if (disconnectConfirmed) {
                                            String disconnectMessage = clientInit.getDisconnectMessage();
                                            outputStream.writeUTF(disconnectMessage);
                                            ImptClient._isAwaitingResponseFromServer = true;
                                            ImptClient.disconnect();
                                            return;
                                            // _clientSocket.close();
                                        }
                                        break;
                                    // PAYMENT
                                    case "#payment":
                                        ImptClientPayment imptClientPayment = new ImptClientPayment(
                                                ImptClient._recipientUserName, ImptClient._recipientUserIdToken);
                                        String paymentSendMessage = imptClientPayment.initialPaymentSend();
                                        outputStream.writeUTF(paymentSendMessage);
                                        ImptClient._isAwaitPaymentSendAccept = true;
                                        break;
                                    // CHAT
                                    default:
                                        break;
                                }
                            }
                        }
                    }

                    // _logger.printLog(this.getClass().toString(), "** Awaiting server response...",
                    //         ImptLoggerConfig.Level.INFO);
                }
            }
        }
        catch (SocketException socketEx) {
            _logger.printToFile("** SocketException below normally indicates loss of connection from the Server **");
            _logger.printToFile(_logger.getExceptionMessage(socketEx));
        }
        catch (Exception e) {
            _logger.printLog(this.getClass().toString(),
                    " Error Encountered: " + _logger.getExceptionMessage(e), ImptLoggerConfig.Level.ERROR);
        }
    }

    /**
     * Build the client authentication message and add version#
     */
    private String buildAuthOutputMessage(String loginCredential) {
        // need encrypted password and username
        return "AUTH BEGIN " + loginCredential + " " + _version;
    }
}