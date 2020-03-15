/**
 * ImptClient is The client module for enter client side
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

import java.io.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import impt.common.*;

public class ImptClient {
    final static int ServerPort = 1234;
    private static String _version = "IMPT1.0";

    private static ImptLogger _logger = new ImptLogger();
    private static Socket _clientSocket = null;

    public static boolean _isLoggedIn = false;
    public static boolean _isAwaitingResponseFromServer = false;
    public static boolean _isAwaitPaymentSendAccept = false;
    public static boolean _isConnectedToOther = false;
    public static String _recipientUserName;
    public static String _recipientUserIdToken;
    public static String _myUserIdToken;
    public static String _myUsername;

    // handle general request from user
    public static void handleGeneralUserInput(String input, DataOutputStream outputStream)
            throws UnknownHostException, IOException {
        switch (input) {
            case "#payment":
                ImptClientPayment imptClientPayment = new ImptClientPayment(_recipientUserName, _recipientUserIdToken);
                String paymentSendMessage = imptClientPayment.initialPaymentSend();
                outputStream.writeUTF(paymentSendMessage);
                _isAwaitPaymentSendAccept = true;
                break;

            case "#logout":
                ImptClientInit clientInit = new ImptClientInit();
                Boolean disconnectConfirmed = clientInit.handleDisconnect();

                if (disconnectConfirmed) {
                    String disconnectMessage = clientInit.getDisconnectMessage();
                    outputStream.writeUTF(disconnectMessage);
                    _isAwaitingResponseFromServer = true;
                    _clientSocket.close();
                }
                break;
            case "#help":
                printHelpCommands();
                break;
            default:
                _logger.printLog("ImptClient", "** Unknown command **", ImptLoggerConfig.Level.INFO);
        }
    }

    public static void printHelpCommands() {
        _logger.printLog("ImptClient",
                "\n** Type '#logout' anytime to disconnect **\n** Type '#payment' anytime to initiate payment **\n** Type '#help' anytime to view help on commands **",
                ImptLoggerConfig.Level.INFO);
    }

    public static void resetLoginStatus() {
        _isLoggedIn = _isConnectedToOther = false;
        _myUsername = _myUserIdToken = _recipientUserName = _recipientUserIdToken = null;
    }

    // Start running main method
    public static void main(String args[]) throws UnknownHostException, IOException {
        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        _clientSocket = new Socket(ip, ServerPort);

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner userInputScanner = new Scanner(System.in);
                DataOutputStream outputStream = null;
                _logger.printLog(this.getClass().toString(),
                        "**** sendMessage Thread Name: " + Thread.currentThread().getName(),
                        ImptLoggerConfig.Level.DEBUG);

                try {
                    outputStream = new DataOutputStream(_clientSocket.getOutputStream());
                } catch (Exception ex) {
                    _logger.printLog(this.getClass().toString(),
                            "** Could not establish output stream connection with the server.",
                            ImptLoggerConfig.Level.INFO);
                    ex.printStackTrace();
                }

                try {
                    while (true) {
                        if (!_isAwaitingResponseFromServer) {
                            if (!_isLoggedIn) { // LOGIN
                                ImptClientAuth clientAuth = new ImptClientAuth();
                                String credential = clientAuth.getAuthInfo(userInputScanner);
                                outputStream.writeUTF(buildAuthOutputMessage(credential));
                                _myUsername = credential.split(" ")[0];
                                _isAwaitingResponseFromServer = true;

                                _logger.printLog(this.getClass().toString(), "** Logging in...",
                                        ImptLoggerConfig.Level.INFO);
                            } else {
                                if (_isConnectedToOther) {

                                    String message = userInputScanner.nextLine();
                                    if (!message.isEmpty()) {

                                        handleGeneralUserInput(message.toLowerCase(), outputStream);

                                        // switch (message.toLowerCase()) {
                                        // // LOGOUT
                                        // case "#logout":
                                        // case "#exit":
                                        // ImptClientInit clientInit = new ImptClientInit();
                                        // Boolean disconnectConfirmed = clientInit.handleDisconnect();

                                        // if (disconnectConfirmed) {
                                        // String disconnectMessage = clientInit.getDisconnectMessage();
                                        // outputStream.writeUTF(disconnectMessage);
                                        // disconnect();
                                        // _isAwaitingResponseFromServer = true;
                                        // _clientSocket.close();
                                        // }
                                        // break;
                                        // // PAYMENT
                                        // case "#payment":
                                        // ImptClientPayment imptClientPayment = new ImptClientPayment(
                                        // _recipientUserName, _recipientUserIdToken);
                                        // String paymentSendMessage = imptClientPayment.initialPaymentSend();
                                        // outputStream.writeUTF(paymentSendMessage);
                                        // _isAwaitPaymentSendAccept = true;
                                        // break;
                                        // // CHAT
                                        // default:
                                        // break;
                                        // }
                                    }
                                }
                            }

                            _logger.printLog(this.getClass().toString(), "** Awaiting server response...",
                                    ImptLoggerConfig.Level.INFO);
                        }
                    }
                } catch (Exception e) {
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    _logger.printLog(this.getClass().toString(),
                            " Error Encountered in sendMessage Thread: " + errors.toString(),
                            ImptLoggerConfig.Level.ERROR);

                    try {
                        outputStream.close();
                        userInputScanner.close();
                    } catch (Exception exClose) {
                        exClose.printStackTrace();
                    }
                }
            }

            /**
             * Build the client authentication message and add version#
             */
            String buildAuthOutputMessage(String loginCredential) {
                // need encrypted password and username
                return "AUTH BEGIN " + loginCredential + " " + _version;
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream inputStream = null;
                _logger.printLog(this.getClass().toString(),
                        "**** readMessage Thread Name: " + Thread.currentThread().getName(),
                        ImptLoggerConfig.Level.DEBUG);

                try {
                    inputStream = new DataInputStream(_clientSocket.getInputStream());
                } catch (Exception ex) {
                    _logger.printLog(this.getClass().toString(),
                            "** Could not establish input stream connection with the server.",
                            ImptLoggerConfig.Level.INFO);
                }

                while (true) {
                    try {
                        // read the message sent to this client
                        String message = inputStream.readUTF();
                        _logger.printLog(this.getClass().toString(), "<< " + message);

                        if (message != null && !message.isEmpty()) {
                            if (!_isLoggedIn) {
                                ImptClientAuth clientAuth = new ImptClientAuth();
                                clientAuth.handleServerAuthResponse(message);
                                _myUserIdToken = clientAuth.getUserToken();
                                boolean _shouldCloseSocket = clientAuth.getShouldCloseSocket();

                                if (_myUserIdToken != null && !_myUserIdToken.isEmpty()) {
                                    _isLoggedIn = true;
                                } else {
                                    if (_shouldCloseSocket) {
                                        _clientSocket.close();
                                    } else {
                                        resetLoginStatus();
                                    }

                                }
                                _isAwaitingResponseFromServer = false;
                            } else {
                                String[] messageArr = message.split(" ");
                                String incomingCommand = messageArr[0];

                                switch (incomingCommand) {
                                    case "INIT":
                                        ImptClientInit clientInit = new ImptClientInit();
                                        Boolean connected = clientInit.handleIncomingConnect(messageArr[2],
                                                messageArr[3]);

                                        if (connected) {
                                            _recipientUserName = clientInit.getRecipientUsername();
                                            _recipientUserIdToken = clientInit.getRecipientUserIdToken();
                                            _isConnectedToOther = true;
                                        } else {
                                            _recipientUserName = _recipientUserIdToken = null;
                                        }

                                        _isAwaitingResponseFromServer = false;
                                        break;
                                    case "PAYSND":
                                        _logger.printLog(this.getClass().toString(), "SERVER PAY",
                                                ImptLoggerConfig.Level.DEBUG);

                                        ImptClientPayment imptClientPayment = new ImptClientPayment(_recipientUserName,
                                                _recipientUserIdToken);
                                        imptClientPayment.handlePaymentResponse(messageArr);

                                        _isAwaitingResponseFromServer = false;
                                        break;
                                    case "DISCONNECT":
                                        if (messageArr.length == 2) {
                                            _logger.printLog(this.getClass().toString(), "you are disconnected",
                                                    ImptLoggerConfig.Level.INFO);
                                            _isAwaitingResponseFromServer = false;
                                        } else {
                                            _recipientUserName = null;
                                            _recipientUserIdToken = null;
                                            _logger.printLog(this.getClass().toString(), "someone got disconnected",
                                                    ImptLoggerConfig.Level.INFO);
                                        }
                                    case "CHAT":
                                        break;
                                }
                            }

                            message = null;
                        }

                    } catch (Exception e) {
                        StringWriter errors = new StringWriter();
                        e.printStackTrace(new PrintWriter(errors));

                        _logger.printLog(this.getClass().toString(),
                                " Error Encountered in readMessage Thread: " + errors.toString(),
                                ImptLoggerConfig.Level.ERROR);
                        break;
                    }
                }

            }

        });

        sendMessage.start();
        readMessage.start();
    }
}