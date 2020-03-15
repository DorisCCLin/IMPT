package impt.client.handlers;

import java.io.*;
import java.net.*;
import impt.common.*;
import impt.client.*;

public class MessageInputHandler implements Runnable {
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
                String message = inputStream.readUTF();
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
                                    ImptClient._recipientUserName = clientInit.getRecipientUsername();
                                    ImptClient._recipientUserIdToken = clientInit.getRecipientUserIdToken();
                                    ImptClient._isConnectedToOther = true;
                                } else {
                                    ImptClient._recipientUserName = ImptClient._recipientUserIdToken = null;
                                }

                                ImptClient._isAwaitingResponseFromServer = false;
                                break;
                            case "PAYINFO":
                                _logger.printLog(this.getClass().toString(), "SERVER PAY",
                                        ImptLoggerConfig.Level.DEBUG);

                                ImptClient._matchedPaymentServices = messageArr[2].split(",");
                                System.out.println(ImptClient._matchedPaymentServices[0]);

                                ImptClient._isAwaitingResponseFromServer = false;
                                break;
                            case "PAYSND":
                                _logger.printLog(this.getClass().toString(), "SERVER PAY",
                                        ImptLoggerConfig.Level.DEBUG);

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
                                    ImptClient._isAwaitingResponseFromServer = false;
                                } else {
                                    ImptClient._recipientUserName = null;
                                    ImptClient._recipientUserIdToken = null;
                                    _logger.printLog(this.getClass().toString(), "someone got disconnected",
                                            ImptLoggerConfig.Level.INFO);
                                }
                            case "CHAT":
                                break;
                        }
                    }

                    message = null;
                }

            } catch (SocketException socketEx) {
                _logger.printToFile(
                        "** SocketException below normally indicates loss of connection from the Server **");
                _logger.printToFile(_logger.getExceptionMessage(socketEx));
                break;
            } catch (Exception e) {
                _logger.printLog(this.getClass().toString(), " Error Encountered: " + _logger.getExceptionMessage(e),
                        ImptLoggerConfig.Level.ERROR);
            }
        }
    }
}