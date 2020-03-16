package impt.client;

import java.util.*;
import java.io.*;
import java.net.*;
import impt.common.*;
// import impt.client.*;

class MessageOutputHandler implements Runnable {
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
                "**** MessageOutputHandler Thread Name: " + Thread.currentThread().getName(),
                ImptLoggerConfig.Level.DEBUG);

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
                        userInputScanner = new Scanner(System.in);
                        if (ImptClient._isConnectedToOther && userInputScanner.hasNextLine()) {
                            String message = userInputScanner.nextLine();

                            System.out.println("User INPUT: " + message);

                            if (!message.isEmpty()) {
                                handleGeneralUserInput(message, outputStream);
                            }
                        }
                    }
                }
                else {
                    _logger.printLog(this.getClass().toString(), "** Awaiting server response...",
                            ImptLoggerConfig.Level.DEBUG);
                }
            }
        } catch (SocketException socketEx) {
            _logger.printToFile("** SocketException below normally indicates loss of connection from the Server **");
            _logger.printToFile(_logger.getExceptionMessage(socketEx));
        } catch (Exception e) {
            _logger.printLog(this.getClass().toString(), " Error Encountered: " + _logger.getExceptionMessage(e),
                    ImptLoggerConfig.Level.ERROR);
        }
    }

    /**
     * Build the client authentication message and add version#
     */
    private String buildAuthOutputMessage(String loginCredential) {
        // need encrypted password and username
        return "AUTH BEGIN " + loginCredential + " " + _version;
    }

    // handle general request from user
    public void handleGeneralUserInput(String input, DataOutputStream outputStream)
            throws UnknownHostException, IOException {
        switch (input.toLowerCase()) {
            case "#payment":
                if (ImptClient._matchedPaymentServices.length == 0 || ImptClient._matchedPaymentServices == null) {
                    ImptClientPayment imptClientPayment = new ImptClientPayment(ImptClient._recipientUserName,
                            ImptClient._recipientUserIdToken, ImptClient._matchedPaymentServices);
                    String paymentSendMessage = imptClientPayment.initialPaymentSend();
                    outputStream.writeUTF(paymentSendMessage);
                    ImptClient._isAwaitPaymentSendAccept = true;
                } else {
                    _logger.printLog("ImptClient", "Opps, there is no payment options", ImptLoggerConfig.Level.INFO);
                    ImptClient._isAwaitPaymentSendAccept = true;
                }

                break;
            case "#logout":
                System.out.print("User wants to log out");
                ImptClientInit clientInit = new ImptClientInit();
                Boolean disconnectConfirmed = clientInit.handleDisconnect();

                if (disconnectConfirmed) {
                    String disconnectMessage = clientInit.getDisconnectMessage();
                    outputStream.writeUTF(disconnectMessage);
                    ImptClient._isAwaitingResponseFromServer = true;
                    // ImptClient.disconnect();
                    return;
                    // _clientSocket.close();
                }

                break;
            case "#help":
                ImptClient.printHelpCommands();

                break;
            default:
                _logger.printLog("ImptClient", "** Unknown command ** " + input, ImptLoggerConfig.Level.INFO);
                break;
        }
    }
}