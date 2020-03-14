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
            case "p":
                ImptClientPayment imptClientPayment = new ImptClientPayment(_recipientUserName, _recipientUserIdToken);
                String paymentSendMessage = imptClientPayment.initialPaymentSend();
                outputStream.writeUTF(paymentSendMessage);
                _isAwaitPaymentSendAccept = true;
                break;

            case "logout":
                ImptClientInit clientInit = new ImptClientInit();
                Boolean disconnectConfirmed = clientInit.handleDisconnect();

                if (disconnectConfirmed) {
                    String disconnectMessage = clientInit.getDisconnectMessage();
                    outputStream.writeUTF(disconnectMessage);
                    _isAwaitingResponseFromServer = true;
                    _clientSocket.close();
                }
                break;
        }

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
                        "**** sendMessage Thread Name: " + Thread.currentThread().getName());

                try {
                    outputStream = new DataOutputStream(_clientSocket.getOutputStream());
                } catch (Exception ex) {
                    _logger.printLog(this.getClass().toString(),
                            "** Could not establish output stream connection with the server.");
                    ex.printStackTrace();
                }

                try {
                    while (true) {
                        if (!_isAwaitingResponseFromServer) {
                            if (!_isLoggedIn) { // LOGIN
                                ImptClientAuth clientAuth = new ImptClientAuth();
                                String credential = clientAuth.getAuthInfo();
                                outputStream.writeUTF(buildAuthOutputMessage(credential));
                                _myUsername = credential.split(" ")[0];
                                _isAwaitingResponseFromServer = true;

                                _logger.printLog(this.getClass().toString(), "** Logging in...");
                            } else {
                                if (_isConnectedToOther) {
                                    // Scanner newInputScanner = new Scanner(System.in);
                                    _logger.printLog(this.getClass().toString(),
                                            _myUsername + ", what's on your mind? :)");

                                    _logger.printLog(this.getClass().toString(),
                                            "\n** Type 'logout' anytime to disconnect **\n** Type 'p' anytime to initiate payment **\n** Type '#help' anytime to view help on commands **");
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
                                                    _isAwaitingResponseFromServer = true;
                                                    _clientSocket.close();
                                                }
                                                break;
                                            // PAYMENT
                                            case "#payment":
                                                break;
                                            // CHAT
                                            default:
                                                break;
                                        }
                                    }
                                }
                            }

                            _logger.printLog(this.getClass().toString(), "** Awaiting server response...");
                        }
                    }
                } catch (Exception e) {
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    _logger.printLog(this.getClass().toString(),
                            " !! Error Encountered in sendMessage Thread: " + errors.toString());

                    try {
                        outputStream.close();
                        // userInputScanner.close();
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
                        "**** readMessage Thread Name: " + Thread.currentThread().getName());

                try {
                    inputStream = new DataInputStream(_clientSocket.getInputStream());
                } catch (Exception ex) {
                    _logger.printLog(this.getClass().toString(),
                            "** Could not establish input stream connection with the server.");
                }

                while (true) {
                    try {
                        // read the message sent to this client
                        String message = inputStream.readUTF();
                        _logger.printLog(this.getClass().toString(), "<< " + message);

                        if (message != null && !message.isEmpty()) {
                            if (!_isLoggedIn) {
                                ImptClientAuth clientAuth = new ImptClientAuth();
                                _myUserIdToken = clientAuth.handleServerAuthResponse(message);

                                if (_myUserIdToken != null && !_myUserIdToken.isEmpty()) {
                                    _isLoggedIn = true;
                                } else {
                                    _isLoggedIn = false;
                                    _myUsername = null;
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

                                        break;
                                    case "PAYSND":
                                        _logger.printLog(this.getClass().toString(), "SERVER PAY");

                                        ImptClientPayment imptClientPayment = new ImptClientPayment(_recipientUserName,
                                                _recipientUserIdToken);
                                        imptClientPayment.handlePaymentResponse(messageArr);

                                        _isAwaitingResponseFromServer = false;
                                        break;
                                    case "DISCONNECT":
                                        if (messageArr.length == 2) {
                                            // socket.close();
                                            _logger.printLog(this.getClass().toString(), "you are disconnected");

                                        } else {
                                            _recipientUserName = null;
                                            _recipientUserIdToken = null;
                                            _logger.printLog(this.getClass().toString(), "someone got disconnected");
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
                                " !! Error Encountered in readMessage Thread: " + errors.toString());
                        break;
                    }
                }

            }

        });

        sendMessage.start();
        readMessage.start();
    }
}