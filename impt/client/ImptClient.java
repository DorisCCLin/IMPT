/**
 * ImptClient is The client module for enter client side
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import impt.common.*;
import impt.client.handlers.*;

public class ImptClient {
    final static int ServerPort = 1234;

    private static ImptLogger _logger = new ImptLogger();
    private static Socket _clientSocket = null;

    private static String _version = "IMPT1.0";
    private static int _connectionRetryLimit = 10;

    public static boolean _isLoggedIn = false;
    public static boolean _isAwaitingResponseFromServer = false;
    public static boolean _isAwaitPaymentSendAccept = false;
    public static boolean _isConnectedToOther = false;
    public static boolean _isConnectedToServer = false;

    public static String _recipientUserName;
    public static String _recipientUserIdToken;
    public static String _myUserIdToken;
    public static String _myUsername;

    // handle general request from user
    // public static void handleGeneralUserInput(String input, DataOutputStream
    // outputStream)
    // throws UnknownHostException, IOException {
    // switch (input) {
    // case "p":
    // ImptClientPayment imptClientPayment = new
    // ImptClientPayment(_recipientUserName, _recipientUserIdToken);
    // String paymentSendMessage = imptClientPayment.initialPaymentSend();
    // outputStream.writeUTF(paymentSendMessage);
    // _isAwaitPaymentSendAccept = true;
    // break;

    // case "logout":
    // ImptClientInit clientInit = new ImptClientInit();
    // Boolean disconnectConfirmed = clientInit.handleDisconnect();

    // if (disconnectConfirmed) {
    // String disconnectMessage = clientInit.getDisconnectMessage();
    // outputStream.writeUTF(disconnectMessage);
    // _isAwaitingResponseFromServer = true;
    // _clientSocket.close();
    // }
    // break;
    // }
    // }

    public static void disconnect() {
        _isLoggedIn = _isConnectedToOther = false;
        _myUsername = _myUserIdToken = _recipientUserName = _recipientUserIdToken = null;
    }

    // Start running main method
    public static void main(String args[]) throws UnknownHostException, IOException {
        _logger.printLog("ImptClient", "Welcome to IMPT Client! (> '')>");

        if (!connect()) {
            return;
        }

        _logger.printLog("ImptClient", "Connection established!");

        while (true) {
            if (!_isConnectedToServer) {
                MessageOutputHandler messageOutputHandler = new MessageOutputHandler(_clientSocket, _version);
                MessageInputHandler messageInputHandler = new MessageInputHandler(_clientSocket);

                Thread sendMessageThread = new Thread(messageOutputHandler);
                Thread readMessageThread = new Thread(messageInputHandler);

                sendMessageThread.start();
                readMessageThread.start();

                _isConnectedToServer = true;
            }
        }
    }

    public static Boolean connect() {
        int connectionRetries = 0;
        String connectionMessage = "Connecting";

        while (true) {
            if (connectionRetries < _connectionRetryLimit) {
                try {
                    _logger.printLog("ImptClient", connectionMessage + " to server...");
                    TimeUnit.SECONDS.sleep(1);
                    // getting localhost ip
                    InetAddress ip = InetAddress.getByName("localhost");

                    // establish the connection
                    _clientSocket = new Socket(ip, ServerPort);
                    return true;
                } catch (ConnectException connectEx) {
                    connectionMessage = "Retrying connection";
                    connectionRetries++;
                } catch (Exception ex) {
                    _logger.printLog("ImptClient", "Error Encountered: " + _logger.getExceptionMessage(ex),
                            ImptLoggerConfig.Level.ERROR);
                }
            } else {
                _logger.printLog("ImptClient", "Connection could not be established. Exiting...");
                return false;
            }
        }
    }
}