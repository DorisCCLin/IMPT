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

    private static boolean _isLoggedIn = false;

    // private int _currentSequenceId;
    // private int _nextSequenceId;

    public static void main(String args[]) throws UnknownHostException, IOException {
        Scanner scanner = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket socket = new Socket(ip, ServerPort);

        // obtaining input and out streams
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        // ImptClientAuth clientAuth = new ImptClientAuth();
        // String message = clientAuth.getAuthMessage();

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    if (!_isLoggedIn) {
                        ImptClientAuth clientAuth = new ImptClientAuth();
                        String message = clientAuth.getAuthMessage();
                        dos.writeUTF(message);
                        _isLoggedIn = true;

                    } else {
                        // read the message to deliver.
                        while (true) {
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
                            dos.writeUTF(message);
                        }
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
                        String msg = dis.readUTF();
                        System.out.println(msg);
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
