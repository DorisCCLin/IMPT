/**
 * ClientAuth The client authentication module for the initial handshake
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt_client;

// import java.util.List;
import java.util.Scanner;

// import rtce.RTCEMessageType;

public class ImptClientAuth {

    // Authentication information
    private String username;
    private String password;
    private String version = "IMPT 1.0";

    // involved messages
    // private RTCEClientMessage clientMessage;
    // private RTCEClientMessage serverMessage;
    // private RTCEClientMessage cack;

    public String getAuthMessage() {
        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        System.out.println("Enter username");
        username = myObj.nextLine(); // Read user input

        System.out.println("Enter password:"); // Output user input
        password = myObj.nextLine();

        return buildMessage(username, password);
    }

    /**
     * Build the client authentication message
     */
    private String buildMessage(String username, String password) {

        // need encrypted password and username

        return " C > AUTH [BEGIN] " + username + " " + password;

        // clientMessage = new RTCEClientMessage();
        // clientMessage.setRequest(RTCEMessageType.CUAUTH);
        // clientMessage.setDocumentOwner(documentOwner);
        // clientMessage.setDocumentTitle(documentTitle);
        // clientMessage.setPassword(password);
        // clientMessage.setUsername(username);
        // clientMessage.setEncryptOpts(getEncryptOpts());
        // clientMessage.setGenericOpts(getGenericOpts());
        // clientMessage.setVersion(RTCEClientConfig.getVersion());
        // clientMessage.setSessionId(0);
    }

    // /**
    // * Get the client authentication message
    // *
    // * @return the client authentication message
    // */
    // public RTCEClientMessage getClientMessage() {
    // return clientMessage;
    // }

    // /**
    // * Get the server connection response
    // *
    // * @return the server connection response
    // */
    // public RTCEClientMessage getServerMessage() {
    // return serverMessage;
    // }

    // /**
    // * Get the client acknowledgement message
    // *
    // * @return the client acknowledgement message
    // */
    // public RTCEClientMessage getCack() {
    // return cack;
    // }

    // /**
    // * Set the server connection response
    // *
    // * @param serverMessage = the server connection response
    // */
    // public void setServerMessage(RTCEClientMessage serverMessage) {
    // this.serverMessage = serverMessage;
    // }

    // /**
    // * Get the ordered array of desired encryption options
    // *
    // * @return an ordered string array
    // */
    // private String[] getEncryptOpts() {
    // List<String> encrypt = RTCEClientConfig.getDesiredEncrypts();
    // return encrypt.toArray(new String[encrypt.size()]);
    // }

    // /**
    // * Get the array of desired generic options
    // *
    // * @return a string array
    // */
    // private String[] getGenericOpts() {
    // List<String> opts = RTCEClientConfig.getDesiredOpts();
    // return opts.toArray(new String[opts.size()]);
    // }

    // /**
    // * Generate and return the client connection object
    // *
    // * @return the client connection object
    // */
    // public RTCEClientConnection getConnection() {
    // String encrypt = serverMessage.getEncryptOpts()[0];
    // String opts[] = serverMessage.getGenericOpts();
    // String sec[] = serverMessage.getSharedSecrets();
    // long session = serverMessage.getSessionId();
    // byte version[] = serverMessage.getVersion();
    // cack = new RTCEClientMessage();
    // cack.setSessionId(session);
    // cack.setRequest(RTCEMessageType.CACK);
    // return new RTCEClientConnection(encrypt, opts, sec, null, session, version);
    // }
}