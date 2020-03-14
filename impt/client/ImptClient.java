/**
 * ClientAuth The client authentication module for the initial handshake
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class ImptClient {
    final static int ServerPort = 1234;
    private static String _version = "IMPT1.0";

    public static boolean _isLoggedIn = false;
    public static boolean _isLoggingOut = false;
    public static boolean _isAwaitingResponseFromServer = false;
    public static boolean _isAwaitPaymentSendAccept = false;
    public static boolean _isConnectedToOther = false;
    public static String _recipientUserName;
    public static String _recipientUserIdToken;
    public static String _myUserIdToken;
    public static String _myUsername;

    public boolean toggleIsAwaitingResponseFromServer() {
        return !_isAwaitingResponseFromServer;
    }

    public static void handleGeneralUserInput(String input, DataOutputStream outputStream)
            throws UnknownHostException, IOException {
        switch (input) {
            case ("p"):
                ImptClientPayment imptClientPayment = new ImptClientPayment(_recipientUserName, _recipientUserIdToken);
                String paymentSendMessage = imptClientPayment.initialPaymentSend();
                outputStream.writeUTF(paymentSendMessage);
                _isAwaitPaymentSendAccept = true;
                break;
            case ("logout"):
                ImptClientInit clientInit = new ImptClientInit();
                clientInit.handleDisconnect();
                String disconnectMessage = clientInit.getDisconnectMessage();
                outputStream.writeUTF(disconnectMessage);
                _isLoggingOut = true;
                _isAwaitingResponseFromServer = true;
                break;
        }

    }

    public static void main(String args[]) throws UnknownHostException, IOException {
        Scanner scanner = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket socket = new Socket(ip, ServerPort);

        // obtaining input and out streams
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {

                        if (!_isAwaitingResponseFromServer) {
                            if (!_isLoggedIn) {
                                ImptClientAuth clientAuth = new ImptClientAuth();
                                String credential = clientAuth.getAuthInfo();
                                outputStream.writeUTF(buildAuthOutputMessage(credential));
                                _myUsername = credential.split(" ")[0];
                                _isAwaitingResponseFromServer = true;
                                System.out.println("logging in...");
                            } else {
                                if (!_isLoggingOut) {
                                    if (!_isAwaitPaymentSendAccept) {
                                        if (_recipientUserName != null && !_recipientUserName.isEmpty()) {
                                            System.out.println(_myUsername + ", what's on your mind?");
                                            System.out.println("(p => pay, logout => exit)");

                                            String message = scanner.nextLine();

                                            handleGeneralUserInput(message, outputStream);

                                        } else {
                                            System.out.println("await for other user online...");
                                            System.out.println("(type 'logout' anytime to exit)");
                                        }

                                    } else {
                                        break;
                                    }

                                } else {
                                    break;
                                }
                                // break;
                            }
                        }
                        System.out.println("await for server");

                    } catch (IOException e) {
                        e.printStackTrace();
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

                while (true) {
                    try {
                        // read the message sent to this client
                        String message = inputStream.readUTF();
                        System.out.println("WAITING SERVER MSG");
                        System.out.println(message);

                        if (message != null && !message.isEmpty()) {
                            if (!_isLoggedIn) {
                                System.out.println("SERVER AUTH");
                                ImptClientAuth clientAuth = new ImptClientAuth();
                                _myUserIdToken = clientAuth.handleInputMessage(message);

                                if (_myUserIdToken != null && !_myUserIdToken.isEmpty()) {
                                    _isLoggedIn = true;
                                    _isAwaitingResponseFromServer = false;
                                } else {
                                    _isLoggedIn = false;
                                    _myUsername = null;
                                    clientAuth.getAuthInfo();
                                    _isAwaitingResponseFromServer = false;
                                }
                            } else {
                                System.out.println("SERVER MSG");
                                String[] messageArr = message.split(" ");
                                switch (messageArr[0]) {
                                    case "INIT":
                                        System.out.println("SERVER INIT");
                                        ImptClientInit clientInit = new ImptClientInit();
                                        clientInit.handleIncomingConnect(messageArr[2]);
                                        if (messageArr.length == 4) {
                                            ImptClient._recipientUserName = messageArr[2];
                                            ImptClient._recipientUserIdToken = messageArr[3];
                                            _isConnectedToOther = true;
                                        }

                                        _isAwaitingResponseFromServer = false;
                                        break;
                                    case "PAYSND":
                                        System.out.println("SERVER PAY");

                                        ImptClientPayment imptClientPayment = new ImptClientPayment(_recipientUserName,
                                                _recipientUserIdToken);
                                        imptClientPayment.handlePaymentResponse(messageArr);

                                        _isAwaitingResponseFromServer = false;
                                        break;
                                    case "DISCONNECT":
                                        if (messageArr.length == 2) {
                                            // socket.close();
                                            System.out.println("you are disconnected");

                                        } else {
                                            _recipientUserName = null;
                                            _recipientUserIdToken = null;
                                            System.out.println("someone got disconnected");
                                        }

                                        break;
                                }
                            }

                            message = null;
                        }
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }

            }

        });

        sendMessage.start();
        readMessage.start();
    }
}
