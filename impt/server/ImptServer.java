/**
 * Server is where the service and concurrenting happens.
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */
package impt.server;

import java.io.*;
import java.util.*;
import java.net.*;
import impt.common.*;

// Server class 
public class ImptServer {

    // Map to store active clients
    public static Map<String, String> activeUsers = new HashMap<String, String>();
    public static Map<String, ImptClientManager> activeSockets = new HashMap<String, ImptClientManager>();

    private static ImptLogger _logger = new ImptLogger();

    // Entry Server point
    public static void main(String[] args) throws IOException {
        // server is listening on port 1234
        ServerSocket serverSocket = new ServerSocket(1234);
        Socket socket;

        _logger.printLog("ImptServer", "IMPT Server Started", ImptLoggerConfig.Level.INFO);

        try {
            // running infinite loop for getting
            // client request
            while (true) {
                // Accept the incoming request
                socket = serverSocket.accept();

                _logger.printLog("ImptServer", "New client request received : " + socket, ImptLoggerConfig.Level.INFO);

                // obtain input and output streams
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

                _logger.printLog("ImptServer", "Creating a new handler for this client...",
                        ImptLoggerConfig.Level.INFO);

                ImptClientManager imptClientManager = new ImptClientManager(inputStream, outputStream);

                // Create a new Thread with this object. CONCURRENT
                Thread thread = new Thread(imptClientManager);
                _logger.printLog("ImptServer", "ImptClientManager Thread Name: " + thread.getName(), 
                        ImptLoggerConfig.Level.DEBUG);

                // start the thread.
                thread.start();
            }
        } catch (Exception ex) {
            _logger.printLog("ImptServer", " Error Encountered in IMPT Server: " + _logger.getExceptionMessage(ex), 
                    ImptLoggerConfig.Level.ERROR);
        } finally {
            serverSocket.close();
        }
    }
}
