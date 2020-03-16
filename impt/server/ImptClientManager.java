/**
 * ImptClientManager is the manage the message to be delivered through different sockets.
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.server;

import java.io.*;
import java.util.*;
import java.net.*;

import impt.common.*;

class ImptClientManager implements Runnable {
    Scanner scn = new Scanner(System.in);

    private DataInputStream _inputStream;
    private DataOutputStream _outputStream;
    private ImptLogger _logger = new ImptLogger();

    private ClientMessageObject _clientMessageObject = new ClientMessageObject();

    private String _clientUserName;

    // constructor
    public ImptClientManager(DataInputStream inputStream, DataOutputStream outputStream) {
        _inputStream = inputStream;
        _outputStream = outputStream;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // receive the string
                String receivedMessage = _inputStream.readUTF();
                _logger.printLog(this.getClass().toString(), receivedMessage, ImptLoggerConfig.Level.DEBUG);

                // sending incoming messager to message manager
                ImptMessageManager imptMessageManager = new ImptMessageManager();
                ClientMessageObject newClientMessageObject = imptMessageManager.handleClientMessage(receivedMessage);

                if (newClientMessageObject != null) {
                    _clientMessageObject = newClientMessageObject;
                }

                // check if otherUserIdToken is null
                if (_clientMessageObject.otherUserIdToken == null) {
                    if (ImptServer.activeUsers.size() > 1) {
                        // get other user's id token
                        String otherUserName = ImptServer.activeUsers.keySet().stream()
                                .filter(s -> !s.contains(_clientUserName)).toString();
                        _clientMessageObject.otherUserIdToken = ImptServer.activeUsers.get(otherUserName);
                    }
                }

                String outputMessage = _clientMessageObject.message;

                _logger.printLog(this.getClass().toString(), outputMessage, ImptLoggerConfig.Level.DEBUG);

                // dispatch messages to different user socket
                switch (_clientMessageObject.command) {
                    case "AUTH":
                        if (_clientMessageObject.isUserLoggedIn) {
                            ImptServer.activeSockets.put(_clientMessageObject.userIdToken, this);
                            _logger.printLog(this.getClass().toString(), ImptServer.activeUsers.toString(),
                                    ImptLoggerConfig.Level.DEBUG);
                            _outputStream.writeUTF(outputMessage);

                            // handle INIT message delivery
                            if (ImptServer.activeUsers.size() == 1) {
                                _outputStream.writeUTF(_clientMessageObject.initNoneUserMessage);
                            } else {
                                // send to current user
                                _outputStream.writeUTF(_clientMessageObject.initCurrentUserMessage);

                                ImptServer.activeSockets.get(_clientMessageObject.otherUserIdToken)._outputStream
                                        .writeUTF(_clientMessageObject.initExistingUserMessage);

                                // send payment message to current user
                                _outputStream.writeUTF(_clientMessageObject.payInfoMessage);

                                // send payment message to existing user
                                ImptServer.activeSockets.get(_clientMessageObject.otherUserIdToken)._outputStream
                                        .writeUTF(_clientMessageObject.payInfoMessage);
                            }

                        } else {
                            _outputStream.writeUTF(outputMessage);
                        }

                        break;

                    case "PAYSND":
                        // send payment request to the other user
                        ImptServer.activeSockets.get(_clientMessageObject.otherUserIdToken)._outputStream
                                .writeUTF(_clientMessageObject.message);

                        break;
                    case "PAYACCEPT":
                        _outputStream.writeUTF(outputMessage);

                        break;
                    case "DISCONNECT":
                        if (ImptServer.activeUsers.size() > 0) {
                            this._outputStream.writeUTF(_clientMessageObject.message);

                            // Send the other user that the current user has disconnected
                            ImptServer.activeSockets.get(_clientMessageObject.otherUserIdToken)._outputStream
                                    .writeUTF(_clientMessageObject.initExistingUserMessage);
                        } else {
                            this._outputStream.writeUTF(_clientMessageObject.message);
                        }

                        break;
                }
            } catch (SocketException socketEx) {
                _logger.printToFile("** SocketException below normally indicates loss of connection from a Client **");
                _logger.printToFile(_logger.getExceptionMessage(socketEx));
                return;
            } catch (Exception e) {
                _logger.printLog(this.getClass().toString(), " Error Encountered: " + _logger.getExceptionMessage(e),
                        ImptLoggerConfig.Level.ERROR);
                break;
            }
        }
    }
}