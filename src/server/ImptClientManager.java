package impt_server;

import java.io.*;
import java.util.*;
import java.net.*;

class ImptClientManager implements Runnable {
    Scanner scn = new Scanner(System.in);
    // private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    private boolean _isloggedin = false;
    private boolean _isMessageValid = false;

    private enum _command {
        LOGIN, LOGOUT, CHAT, AUTH, PAYMENT
    };

    // constructor
    public ImptClientManager(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        // this.name = name;
        this.s = s;
        this._isMessageValid = false;
        this._isloggedin = false;
    }

    @Override
    public void run() {

        String received;
        while (true) {
            try {
                // receive the string
                received = dis.readUTF();

                System.out.println(received);

                if (received.equals("logout")) {
                    this._isloggedin = false;
                    this.s.close();
                    break;
                }

                // break the string into message and recipient part
                StringTokenizer st = new StringTokenizer(received, "#");
                String MsgToSend = st.nextToken();
                String recipient = st.nextToken();

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
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}