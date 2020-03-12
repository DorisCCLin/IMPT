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
import java.util.Scanner;

public class ImptClient {
    final static int ServerPort = 1234;

    public static boolean _isLoggedIn = false;
    public static boolean _isConnectedToOther = false;
    public static String _recipientUserName;
    public static String _recipientUserIdToken;
    public static String _myUserIdToken;

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

                try {
                    while (true) {
                        if (!_isLoggedIn) {
                            ImptClientAuth clientAuth = new ImptClientAuth();
                            String message = clientAuth.getAuthInfo();
                            outputStream.writeUTF(message);
                            System.out.println("logging in...");
                        } else {
                            if (!_isConnectedToOther) {
                                ImptClientInit clientInit = new ImptClientInit();
                                String message = clientInit.getInitInfo();
                                _recipientUserName = clientInit.getRecipientUsername();
                                outputStream.writeUTF(message);
                                System.out.println("Checking if " + _recipientUserIdToken + " is online...");

                            } else {

                                Scanner generalInput = new Scanner(System.in);
                                String message = generalInput.nextLine();

                                if (message.equals("logout")) {
                                    _isLoggedIn = false;
                                    socket.close();
                                    scanner.close();
                                    System.out.println("Good-bye!");
                                    break;
                                }

                                // write on the output stream
                                outputStream.writeUTF(message);
                            }
                        }
                        scanner.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();

                }
                // while (true) {

                // // read the message to deliver.
                // String msg = scanner.nextLine();
                // // String msg = auth.getAuthMessage();

                // try {
                // // write on the output stream
                // dos.writeUTF(msg);
                // } catch (IOException e) {
                // e.printStackTrace();
                // }
                // }
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
                        System.out.println(message);

                        if (!_isLoggedIn) {
                            ImptClientAuth clientAuth = new ImptClientAuth();
                            clientAuth.handleInputMessage(message);
                        } else {
                            String[] messageArr = message.split(" ");

                            switch (messageArr[0]) {
                                case "INT_RQST":
                                    ImptClientInit clientInit = new ImptClientInit();
                                    clientInit.handleConnectRequest(messageArr[3]);
                                    break;
                                default:
                            }
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
