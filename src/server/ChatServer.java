// Java implementation of Server side 
// It contains two classes : Server and ClientHandler 
// Save file as Server.java 
package src.server;

import java.io.*;
import java.util.*;
import java.net.*;
import static java.util.Map.entry;

// Server class 
public class ChatServer {

    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();

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
            ClientHandler clientHandler = new ClientHandler(socket, inputStream, outputStream);

            // Create a new Thread with this object.
            Thread t = new Thread(clientHandler);

            System.out.println("Adding this client to active client list");

            // add this client to active clients list
            ar.add(clientHandler);

            // start the thread.
            t.start();

            // increment i for new client.
            // i is used for naming only, and can be replaced
            // by any naming scheme
            // i++;

        }
    }
}

class MessageHandler {
    private ClientMessageObject _clientMessageObject;

    public MessageHandler(ClientMessageObject clientMessageObject) {
        _clientMessageObject = clientMessageObject;
    }

    public boolean isMessageValid(String message) {
        // validate correct json format
        // if no - throw invalid json message format error
        // try parsing into ClientMessageObject
        // if no - throw invalid message format error
        return true;
    }

    public String GetCommand() {
        return _clientMessageObject.command;
    }

    public String GetMessageStatus() {
        return _clientMessageObject.messageStatus;
    }

    public String GetMessage() {
        return _clientMessageObject.message;
    }

    public String GetToken() {
        return _clientMessageObject.token;
    }

    public String GetOptionalParam() {
        return _clientMessageObject.optionalParam;
    }

    class ClientMessageObject {
        private String command;
        private String messageStatus;
        private String message;
        private String token;
        private String optionalParam;
    }
}

class AuthenticationHandler {
    private static Map<String, String> _credentials = Map.ofEntries(entry("Doris", "12345"), entry("Calvin", "ca1vin"),
            entry("Brian", "password"));

    public AuthenticationHandler(ClientMessageObject clientMessageObject) {
        _clientMessageObject = clientMessageObject;
    }

    public static boolean authenticate(String userName, String password) {
        if ((_credentials.containsKey(userName))) {
            if (_credentials.get(userName) == password) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }
}

class ChatHandler {
}

class PaymentHandler {
}

// ClientHandler class
class ClientHandler implements Runnable {
    Scanner scn = new Scanner(System.in);
    // private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    // private boolean _isloggedin = false;
    // private boolean _isloggedin = false;

    private enum _command {
        LOGIN, LOGOUT, CHAT, AUTH, PAYMENT
    };

    // constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
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

                // use MessageHandler to validate Message format
                MessageHandler messageHandler = new MessageHandler();
                 = messageHandler.isMessageValid(received);


                System.out.println(received);

                if (received.equals("logout")) {
                    this.isloggedin = false;
                    this.s.close();
                    break;
                }

                // break the string into message and recipient part
                StringTokenizer st = new StringTokenizer(received, "#");
                String MsgToSend = st.nextToken();
                String recipient = st.nextToken();

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users
                for (ClientHandler mc : ChatServer.ar) {
                    // if the recipient is found, write on its
                    // output stream
                    if (mc.name.equals(recipient) && mc.isloggedin == true) {
                        mc.dos.writeUTF(this.name + " : " + MsgToSend);
                        break;
                    }
                }
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
