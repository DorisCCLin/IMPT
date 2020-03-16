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

    // constructor
    public ImptClientManager(DataInputStream inputStream, DataOutputStream outputStream) {
        _inputStream = inputStream;
        _outputStream = outputStream;
    }

    private void updateClientMessageObject(ClientMessageObject newClientMessageObject) {
        _clientMessageObject.isUserLoggedIn = newClientMessageObject.isUserLoggedIn;
        _clientMessageObject.userName = newClientMessageObject.userName != null
                && !newClientMessageObject.userName.isEmpty() ? newClientMessageObject.userName
                        : _clientMessageObject.userName;
        _clientMessageObject.userIdToken = newClientMessageObject.userIdToken != null
                && !newClientMessageObject.userIdToken.isEmpty() ? newClientMessageObject.userIdToken
                        : _clientMessageObject.userIdToken;
        _clientMessageObject.otherUserIdToken = newClientMessageObject.otherUserIdToken != null
                && !newClientMessageObject.otherUserIdToken.isEmpty() ? newClientMessageObject.otherUserIdToken
                        : _clientMessageObject.otherUserIdToken;
        _clientMessageObject.initNoneUserMessage = newClientMessageObject.initNoneUserMessage != null
                && !newClientMessageObject.initNoneUserMessage.isEmpty() ? newClientMessageObject.initNoneUserMessage
                        : _clientMessageObject.initNoneUserMessage;
        _clientMessageObject.initCurrentUserMessage = newClientMessageObject.initCurrentUserMessage != null
                && !newClientMessageObject.initCurrentUserMessage.isEmpty()
                        ? newClientMessageObject.initCurrentUserMessage
                        : _clientMessageObject.initCurrentUserMessage;
        _clientMessageObject.initExistingUserMessage = newClientMessageObject.initExistingUserMessage != null
                && !newClientMessageObject.initExistingUserMessage.isEmpty()
                        ? newClientMessageObject.initExistingUserMessage
                        : _clientMessageObject.initExistingUserMessage;
        _clientMessageObject.payInfoMessage = newClientMessageObject.payInfoMessage != null
                && !newClientMessageObject.payInfoMessage.isEmpty() ? newClientMessageObject.payInfoMessage
                        : _clientMessageObject.payInfoMessage;
        _clientMessageObject.message = newClientMessageObject.message != null
                && !newClientMessageObject.message.isEmpty() ? newClientMessageObject.message
                        : _clientMessageObject.message;
        _clientMessageObject.command = newClientMessageObject.command != null
                && !newClientMessageObject.command.isEmpty() ? newClientMessageObject.command
                        : _clientMessageObject.command;
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
                    updateClientMessageObject(newClientMessageObject);
                }

                // check if otherUserIdToken is null
                if (_clientMessageObject.otherUserIdToken == null) {
                    System.out.println("Active Users[ImptClientManager:GET OTHER USER]: " + ImptServer.activeUsers);
                    if (ImptServer.activeUsers.size() > 1) {
                        // get other user's id token
                        String otherUserName = ImptServer.activeUsers.keySet().stream()
                                .filter(s -> !s.contains(_clientMessageObject.userName)).toString();
                        System.out.println("otherUserName[ImptClientManager]: " + otherUserName);
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

                            System.out.println("Active Users[ImptClientManager:AUTH]: " + ImptServer.activeUsers);

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
                        System.out.println("Active Users[ImptClientManager:DISCONNECT]: " + ImptServer.activeUsers);

                        if (ImptServer.activeUsers.size() > 0) {
                            this._outputStream.writeUTF(_clientMessageObject.message);

                            // Send the other user that the current user has disconnected
                            ImptServer.activeSockets.get(_clientMessageObject.otherUserIdToken)._outputStream
                                    .writeUTF(_clientMessageObject.initExistingUserMessage);
                        } else {
                            this._outputStream.writeUTF(_clientMessageObject.message);
                        }

                        ImptServer.activeSockets.remove(_clientMessageObject.userIdToken);
                        _logger.printLog(this.getClass().toString(), "Removed " + _clientMessageObject.userIdToken
                                + ": " + ImptServer.activeSockets.toString());
                        break;
                }
            } catch (SocketException socketEx) {
                _logger.printToFile("** SocketException below normally indicates loss of connection from a Client **");
                _logger.printToFile(_logger.getExceptionMessage(socketEx));
                _logger.printLog(this.getClass().toString(),
                        _clientMessageObject.userName + " disconnected. Terminating thread socket...",
                        ImptLoggerConfig.Level.INFO);

                try {
                    ImptServer.activeSockets.get(_clientMessageObject.otherUserIdToken)._outputStream
                            .writeUTF("DISCONNECT FIN " + _clientMessageObject.userName + " "
                                    + ImptServer.activeUsers.get(_clientMessageObject.userName));

                    ImptServer.activeSockets.get(_clientMessageObject.otherUserIdToken)._outputStream
                            .writeUTF("INIT BEGIN none none");

                    ImptServer.activeUsers.remove(_clientMessageObject.userName);

                    _logger.printLog(this.getClass().toString(),
                            "Removed " + _clientMessageObject.userName + ": " + ImptServer.activeUsers.toString());

                    ImptServer.activeSockets.remove(_clientMessageObject.userIdToken);

                    _logger.printLog(this.getClass().toString(),
                            "Removed " + _clientMessageObject.userIdToken + ": " + ImptServer.activeSockets.toString());
                } catch (Exception sendDisconnectMsgEx) {
                    // sendDisconnectMsgEx
                }

                return;
            } catch (Exception e) {
                _logger.printLog(this.getClass().toString(), " Error Encountered: " + _logger.getExceptionMessage(e),
                        ImptLoggerConfig.Level.ERROR);
                break;
            }
        }
    }
}