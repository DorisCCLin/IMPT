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
    // private String name;
    private Socket _socket;
    private DataInputStream _inputStream;
    private DataOutputStream _outputStream;
    // private String _outputMessage;
    private ImptLogger _logger = new ImptLogger();
    private ClientMessageObject _clientMessageObject = new ClientMessageObject();

    // constructor
    public ImptClientManager(Socket socket, DataInputStream inputStream, DataOutputStream outputStream) {
        this._socket = socket;
        this._inputStream = inputStream;
        this._outputStream = outputStream;
    }

    @Override
    public void run() {

        String receivedMessage = null;

        while (true) {
            try {
                // receive the string
                receivedMessage = this._inputStream.readUTF();
                _logger.printLog(this.getClass().toString(), receivedMessage, ImptLoggerConfig.Level.DEBUG);

                // sending incoming messager to message manager
                ImptMessageManager ImptMessageManager = new ImptMessageManager();
                ImptMessageManager.handleClientMessage(receivedMessage);

                _clientMessageObject = ImptMessageManager.getClientMessageObject();
                String outputMessage = _clientMessageObject.message;

                _logger.printLog(this.getClass().toString(), outputMessage, ImptLoggerConfig.Level.DEBUG);

                // dispatch messages to different user socket
                switch (_clientMessageObject.command) {
                    case "AUTH":
                        if (_clientMessageObject.isUserLoggedIn) {
                            ImptServer.activeSockets.put(_clientMessageObject.userIdToken, this);
                            _logger.printLog(this.getClass().toString(), ImptServer.activeUsers.toString(),
                                    ImptLoggerConfig.Level.DEBUG);
                            this._outputStream.writeUTF(outputMessage);

                            // handle INIT message delivery
                            if (ImptServer.activeUsers.size() == 1) {
                                this._outputStream.writeUTF(_clientMessageObject.initNoneUserMessage);
                            } else {
                                // send connection message to current user
                                // this._outputStream.writeUTF(_clientMessageObject.initCurrentUserMessage);
                                // send connection message to existing user
                                ImptClientManager recipientImptClientManager = ImptServer.activeSockets
                                        .get(_clientMessageObject.prevUserIdToken);
                                // recipientImptClientManager._outputStream
                                // .writeUTF(_clientMessageObject.initExistingUserMessage);

                                // send payment message to current user
                                this._outputStream.writeUTF(_clientMessageObject.payInfoMessage);
                                // send payment message to existing user
                                recipientImptClientManager._outputStream.writeUTF(_clientMessageObject.payInfoMessage);
                            }

                        } else {
                            _outputStream.writeUTF(outputMessage);
                        }

                        break;
                    case "PAYSND":
                        _outputStream.writeUTF(outputMessage);
                        break;

                    case "DISCONNECT":
                        if (ImptServer.activeUsers.size() > 0) {
                            this._outputStream.writeUTF(_clientMessageObject.message);

                            ImptClientManager recipientImptClientManager = ImptServer.activeSockets
                                    .get(_clientMessageObject.prevUserIdToken);

                            recipientImptClientManager._outputStream
                                    .writeUTF(_clientMessageObject.initExistingUserMessage);
                        } else {
                            this._outputStream.writeUTF(_clientMessageObject.message);
                        }

                        break;
                }

                receivedMessage = null;

            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                _logger.printLog(this.getClass().toString(), " Error Encountered: " + errors.toString(),
                        ImptLoggerConfig.Level.ERROR);
                break;
            }
        }
    }
}