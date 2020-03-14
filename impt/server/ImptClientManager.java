package impt.server;

import java.io.*;
import java.util.*;
import java.net.*;

import impt.server.*;
// import impt.common.*;

class ImptClientManager implements Runnable {
    Scanner scn = new Scanner(System.in);
    // private String name;
    final DataInputStream _dataInputStream;
    final DataOutputStream _dataOutputStream;
    Socket _socket;
    private String _outputMessage;

    // constructor
    public ImptClientManager(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this._dataInputStream = dataInputStream;
        this._dataOutputStream = dataOutputStream;
        // this.name = name;
        this._socket = socket;
        // this._isMessageValid = false;
        // this._isUserLoggedIn = false;
    }

    @Override
    public void run() {

        String receivedMessage;
        while (true) {
            try {
                // receive the string
                receivedMessage = _dataInputStream.readUTF();
                System.out.println(receivedMessage);

                ImptMessageManger imptMessageManger = new ImptMessageManger();
                imptMessageManger.receiveHandler(receivedMessage);

                ImptMessageManger.ClientMessageObject clientMessageObject = new ImptMessageManger.ClientMessageObject();
                clientMessageObject = imptMessageManger.getClientMessageObject();
                _outputMessage = clientMessageObject.message;

                System.out.println(_outputMessage);

                // Output message to client

                switch (clientMessageObject.command) {
                    case "AUTH":
                        if (clientMessageObject.isUserLoggedIn) {
                            ImptServer.activeSockets.put(clientMessageObject.userIdToken, this);
                            System.out.println(ImptServer.activeUsers);
                            this._dataOutputStream.writeUTF(_outputMessage);

                            if (ImptServer.activeUsers.size() == 1) {
                                this._dataOutputStream.writeUTF(clientMessageObject.initNoneUserMessage);
                            } else {
                                // send to current user
                                this._dataOutputStream.writeUTF(clientMessageObject.initCurrentUserMessage);
                                ImptClientManager recipientImptClientManager = ImptServer.activeSockets
                                        .get(clientMessageObject.prevUserIdToken);
                                recipientImptClientManager._dataOutputStream
                                        .writeUTF(clientMessageObject.initExistingUserMessage);
                            }
                        } else {

                            this._dataOutputStream.writeUTF(_outputMessage);
                        }

                        break;
                    case "PAYSND":
                        this._dataOutputStream.writeUTF(_outputMessage);
                        break;

                    case "DISCONNECT":
                        if (ImptServer.activeUsers.size() > 0) {
                            this._dataOutputStream.writeUTF(clientMessageObject.message);
                            ImptClientManager recipientImptClientManager = ImptServer.activeSockets
                                    .get(clientMessageObject.prevUserIdToken);

                            recipientImptClientManager._dataOutputStream
                                    .writeUTF(clientMessageObject.initExistingUserMessage);
                        } else {
                            this._dataOutputStream.writeUTF(clientMessageObject.message);

                        }

                        break;

                }

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

            } catch (IOException e) {
                e.printStackTrace();
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
        // this._dataOutputStream.close();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }
}