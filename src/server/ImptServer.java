// Java implementation of Server side 
// It contains two classes : Server and ClientHandler 
// Save file as Server.java 
package impt_server;

import java.io.*;
import java.util.*;
import java.net.*;

// Server class 
public class ImptServer {

    // Vector to store active clients
    static Vector<ImptClientManager> ar = new Vector<>();

    public static void main(String[] args) throws IOException {
        // server is listening on port 1234
        ServerSocket serverSocket = new ServerSocket(1255);
        Socket socket;

        // running infinite loop for getting
        // client request
        while (true) {
            // Accept the incoming request
            socket = serverSocket.accept();

            System.out.println("New client request received : " + socket);

            // obtain input and output streams
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            System.out.println("Creating a new handler for this client...");

            // Create a new handler object for handling this request.
            ImptClientManager imptClientManager = new ImptClientManager(socket, inputStream, outputStream);

            // Create a new Thread with this object.
            Thread t = new Thread(imptClientManager);

            System.out.println("Adding this client to active client list");

            // add this client to active clients list
            ar.add(imptClientManager);

            // start the thread.
            t.start();

        }
    }
}
