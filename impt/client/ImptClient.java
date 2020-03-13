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
import static java.util.Map.entry;

public class ImptClient {
    final static int ServerPort = 1234;

    public static boolean _isLoggedIn = false;
    public static boolean _isAwaitingResponseFromServer = false;
    public static boolean _isConnectedToOther = false;
    public static String _recipientUserName;
    public static String _recipientUserIdToken;
    public static String _myUserIdToken;
    public static String _myUsername;

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
                                System.out.println("logging in...");
                            } else {
                                System.out.println(_myUsername + ", what's on your mind?");
                                String message = scanner.nextLine();

                                if (message != null && message.isEmpty()) {
                                    if (message.equals("logout")) {
                                        System.out.println("in logout block");
                                        ImptClientInit clientInit = new ImptClientInit();
                                        clientInit.handleDisconnect();
                                        String disconnectMessage = clientInit.getDisconnectMessage();
                                        outputStream.writeUTF(disconnectMessage);
                                    }
                                    // } else {

                                    // write on the output stream
                                    // outputStream.writeUTF(message);
                                }
                            }
                            _isAwaitingResponseFromServer = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            /**
             * Build the client authentication message
             */
            String buildAuthOutputMessage(String loginCredential) {
                // need encrypted password and username
                return "AUTH BEGIN " + loginCredential;
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
                                    clientAuth.getAuthInfo();
                                    _isAwaitingResponseFromServer = true;
                                }
                            } else {
                                System.out.println("SERVER MSG");
                                String[] messageArr = message.split(" ");
                                switch (messageArr[0]) {
                                    case "INIT":
                                        System.out.println("SERVER INIT");
                                        ImptClientInit clientInit = new ImptClientInit();
                                        clientInit.handleIncomingConnect(messageArr[2], messageArr[3]);
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

        // if user types EXIT or LOGOUT
        // trigger scanner.close();
        // trigger socket.close();
    }
}
