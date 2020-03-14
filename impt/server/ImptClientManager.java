package impt.server;

import java.io.*;
import java.util.*;
import java.net.*;

import impt.common.*;

class ImptClientManager implements Runnable {
    Scanner scn = new Scanner(System.in);
    // private String name;
    private Socket _socket;
    // private String _outputMessage;
    private ImptLogger _logger = new ImptLogger();
    private ClientMessageObject _clientMessageObject = new ClientMessageObject();

    // constructor
    public ImptClientManager(Socket socket) {
        _socket = socket;
    }

    @Override
    public void run() {

        String receivedMessage = null;
        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;

        try {
            inputStream = new DataInputStream(_socket.getInputStream());
            outputStream = new DataOutputStream(_socket.getOutputStream());
        } catch (Exception ex) {

        }

        while (true) {
            try {
                // receive the string
                receivedMessage = inputStream.readUTF();
                _logger.printLog(this.getClass().toString(), receivedMessage);

                ImptMessageManger imptMessageManger = new ImptMessageManger();
                imptMessageManger.handleClientMessage(receivedMessage);

                _clientMessageObject = imptMessageManger.getClientMessageObject();
                String outputMessage = _clientMessageObject.message;

                _logger.printLog(this.getClass().toString(), outputMessage);

                // Output message to client

                // switch (clientMessageObject.command) {
                // case "AUTH":
                // if (clientMessageObject.isUserLoggedIn) {
                // ImptServer.activeSockets.put(clientMessageObject.userIdToken, this);
                // System.out.println(ImptServer.activeUsers);
                // this._dataOutputStream.writeUTF(_outputMessage);

                // if (ImptServer.activeUsers.size() == 1) {
                // this._dataOutputStream.writeUTF(clientMessageObject.initNoneUserMessage);
                // } else {
                // // send to current user
                // this._dataOutputStream.writeUTF(clientMessageObject.initCurrentUserMessage);
                // ImptClientManager recipientImptClientManager = ImptServer.activeSockets
                // .get(clientMessageObject.prevUserIdToken);
                // recipientImptClientManager._dataOutputStream
                // .writeUTF(clientMessageObject.initExistingUserMessage);
                // }
                // } else {

                // this._dataOutputStream.writeUTF(_outputMessage);
                // }

                // break;
                // case "PAYSND":
                // this._dataOutputStream.writeUTF(_outputMessage);

                switch (_clientMessageObject.command) {
                    case "AUTH":
                        if (_clientMessageObject.isUserLoggedIn) {
                            ImptServer.activeSockets.put(_clientMessageObject.userIdToken, this);
                            System.out.println("Active Users[ClientManager]: " + ImptServer.activeUsers);
                        }

                        outputStream.writeUTF(outputMessage);

                        if (ImptServer.activeUsers.size() == 1) {
                            outputStream.writeUTF(_clientMessageObject.initNoneUserMessage);
                        } else {
                            // send to current user
                            outputStream.writeUTF(_clientMessageObject.initCurrentUserMessage);

                            // ImptClientManager recipientImptClientManager = ImptServer.activeSockets
                            // .get(_clientMessageObject.prevUserIdToken);
                            // recipientImptClientManager.outputStream
                            // .writeUTF(_clientMessageObject.initExistingUserMessage);
                        }
                        break;
                    case "PAYSND":
                        outputStream.writeUTF(outputMessage);
                        break;

                    case "DISCONNECT":
                        if (ImptServer.activeUsers.size() > 0) {
                            outputStream.writeUTF(_clientMessageObject.message);

                            // ImptClientManager recipientImptClientManager = ImptServer.activeSockets
                            // .get(_clientMessageObject.prevUserIdToken);

                            // recipientImptClientManager.outputStream
                            // .writeUTF(_clientMessageObject.initExistingUserMessage);
                        } else {
                            outputStream.writeUTF(_clientMessageObject.message);

                        }

                        break;

                }

                receivedMessage = null;

                // if (receivedMessage.equals("logout")) {
                // this._socket.close();
                // break;
                // }

                // for (ImptClientManager mc : ImptServer.ar) {
                // // if the recipient is found, write on its
                // // output stream
                // if (mc.name.equals(recipient) && mc._isloggedin == true) {
                // mc.dos.writeUTF(this.name + " : " + MsgToSend);
                // break;
                // }
                // }

            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                _logger.printLog(this.getClass().toString(), " !! Error Encountered: " + errors.toString());
                break;
                // } catch (Exception ex) {
                // try {
                // this._socket.close();
                // } catch (Exception ex2) {
                // ex2.printStackTrace();
                // }
            }
        }

        // try {
        // // closing resources
        // this._dataInputStream.close();
        // this.outputStream.close();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }
}