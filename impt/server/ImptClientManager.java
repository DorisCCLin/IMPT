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
    // private boolean _isUserLoggedIn = false;
    // static Vector<ImptClientManager> activeUsers = new Vector<>();

    // private int SEQUENCE_MAX_DELTA = 20;

    // private int _currentSequenceId;
    // private int _nextSequenceId;

    // private enum _command {
    // LOGIN, LOGOUT, CHAT, AUTH, PAYMENT
    // };

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

                System.out.println(ImptServer.activeUsers);
                System.out.println(_outputMessage);

                switch (clientMessageObject.command) {
                    case "AUTH":
                        if (clientMessageObject.isUserLoggedIn) {
                            ImptServer.activeSockets.put(clientMessageObject.userIdToken, this);
                        }
                        this._dataOutputStream.writeUTF(_outputMessage);
                        break;
                    case "INIT":
                        ImptClientManager recipientImptClientManager = ImptServer.activeSockets
                                .get(clientMessageObject.userIdToken);
                        recipientImptClientManager._dataOutputStream.writeUTF(_outputMessage);
                        break;
                }

                if (receivedMessage.equals("logout")) {
                    this._socket.close();
                    break;
                }

                // break the string into message and recipient part
                // StringTokenizer st = new StringTokenizer(received, "#");
                // String MsgToSend = st.nextToken();
                // String recipient = st.nextToken();

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users

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
            }

        }
        try {
            // closing resources
            this._dataInputStream.close();
            this._dataOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}