package impt.client;

import java.io.*;
import java.net.*;
import impt.common.*;
// import impt.client.*;

class MessageInputHandler implements Runnable {
    private static ImptLogger _logger = new ImptLogger();
    private Socket _clientSocket;

    public MessageInputHandler(Socket clientSocket) {
        _clientSocket = clientSocket;
    }

    @Override
    public void run() {
        DataInputStream inputStream = null;
        _logger.printLog(this.getClass().toString(),
                "**** MessageInputHandler Thread Name: " + Thread.currentThread().getName(),
                ImptLoggerConfig.Level.DEBUG);

        try {
            inputStream = new DataInputStream(_clientSocket.getInputStream());
        } catch (Exception ex) {
            _logger.printLog(this.getClass().toString(),
                    "** Could not establish input stream connection with the server.", ImptLoggerConfig.Level.INFO);
        }

        while (true) {
            try {
                // read the message sent to this client
                String message = null;
                message = inputStream.readUTF();
                _logger.printLog(this.getClass().toString(), "<< " + message);

                if (message != null && !message.isEmpty()) {
                    if (!ImptClient._isLoggedIn) {
                        ImptClientAuth clientAuth = new ImptClientAuth();

                        clientAuth.handleServerAuthResponse(message);
                        ImptClient._myUserIdToken = clientAuth.getUserToken();

                        if (ImptClient._myUserIdToken != null && !ImptClient._myUserIdToken.isEmpty()) {
                            ImptClient._isLoggedIn = true;
                        } else {
                            ImptClient.disconnect();
                        }

                        ImptClient._isAwaitingResponseFromServer = false;
                    } else {
                        String[] messageArr = message.split(" ");
                        String incomingCommand = messageArr[0];

                        switch (incomingCommand) {
                            case "INIT":
                                ImptClientInit clientInit = new ImptClientInit();
                                Boolean connected = clientInit.handleIncomingConnect(messageArr[2], messageArr[3]);

                                if (connected) {
                                    System.out.print("CONNECTED TO OTHER USER");
                                    ImptClient._recipientUserName = clientInit.getRecipientUsername();
                                    ImptClient._recipientUserIdToken = clientInit.getRecipientUserIdToken();
                                    ImptClient._isConnectedToOther = true;

                                    // ImptClient._isAwaitingResponseFromServer = false; will be set after receiving
                                    // the PAYINFO
                                } else {
                                    System.out.print("NOT CONNECTED TO OTHER USER");
                                    ImptClient._recipientUserName = ImptClient._recipientUserIdToken = null;
                                    ImptClient._isConnectedToOther = false;
                                    ImptClient._isAwaitingResponseFromServer = false; 
                                }

                                break;
                            case "PAYINFO":
                                // _logger.printLog(this.getClass().toString(), "SERVER PAY",
                                // ImptLoggerConfig.Level.DEBUG);

                                ImptClient._matchedPaymentServices = messageArr[2].split(",");
                                System.out.println(ImptClient._matchedPaymentServices[0]);

                                ImptClient._isAwaitingResponseFromServer = false;
                                break;
                            case "PAYSND":
                                // _logger.printLog(this.getClass().toString(), "SERVER PAY",
                                // ImptLoggerConfig.Level.DEBUG);

                                ImptClientPayment imptClientPayment = new ImptClientPayment(
                                        ImptClient._recipientUserName, ImptClient._recipientUserIdToken,
                                        ImptClient._matchedPaymentServices);
                                imptClientPayment.handlePaymentResponse(messageArr);

                                ImptClient._isAwaitingResponseFromServer = false;
                                break;
                            case "DISCONNECT":
                                if (messageArr.length == 2) {
                                    _logger.printLog(this.getClass().toString(), "you are disconnected",
                                            ImptLoggerConfig.Level.INFO);
                                    ImptClient.disconnect();
                                    ImptClient._isConnectedToServer = false;
                                } else {
                                    _logger.printLog(this.getClass().toString(),
                                            ImptClient._recipientUserName + " disconnected",
                                            ImptLoggerConfig.Level.INFO);
                                    ImptClient._recipientUserName = ImptClient._recipientUserIdToken = null;
                                    ImptClient._isConnectedToOther = false;
                                }

                                ImptClient._isAwaitingResponseFromServer = false;
                                break;
                            case "CHAT":
                            default:
                                _logger.printLog(this.getClass().toString(), "Unhandled message: " + message,
                                        ImptLoggerConfig.Level.PROMPT);
                                break;
                        }
                    }

                    message = null;
                }
            } catch (SocketException socketEx) {
                _logger.printToFile(
                        "** SocketException below normally indicates loss of connection from the Server **");
                _logger.printToFile(_logger.getExceptionMessage(socketEx));
                _logger.printLog(this.getClass().toString(), "Disconnected from the server...");
                break;
            } catch (Exception e) {
                _logger.printLog(this.getClass().toString(), " Error Encountered: " + _logger.getExceptionMessage(e),
                        ImptLoggerConfig.Level.ERROR);
            }
        }
    }
}