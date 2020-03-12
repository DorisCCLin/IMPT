package impt.server;

// import java.util.Map;

import impt.server.handlers.*;

class ImptMessageManger {
    private ClientMessageObject _clientMessageObject;
    private boolean _isUserLoggedIn = false;
    private String _outputMessage;

    // public ImptMessageManger(ClientMessageObject clientMessageObject) {
    // _clientMessageObject = clientMessageObject;
    // }
    // public ImptMessageManger(ClientMessageObject clientMessageObject) {
    // _clientMessageObject = clientMessageObject;
    // }

    // public ImptMessageManger(boolean isUserLoggedIn) {
    // _isUserLoggedIn = isUserLoggedIn;
    // }

    public void receiveHandler(String message) {
        boolean isMessageValid = isMessageValid(message);
        String[] messageArr = message.split(" ");

        if (isMessageValid) {
            switch (messageArr[1]) {
                case "AUTH":
                    AuthenticationHandler authHandler = new AuthenticationHandler();
                    AuthenticationHandler.AuthenticationObject authObject = new AuthenticationHandler.AuthenticationObject();
                    authObject = authHandler.authenticate(messageArr[2], messageArr[3]);
                    _clientMessageObject.isUserLoggedIn = authObject.isUserIdLoggedIn;
                    _clientMessageObject.userIdToken = authObject.userIdToken;
                    _clientMessageObject.message = "AUTH RES " + authObject.userIdToken;

                    if (_isUserLoggedIn) {
                        ImptServer.activeUsers.put(authObject.userName, authObject.userIdToken);
                    }

                    // for (Map.Entry<String, String> entry : ImptServer.activeUsers.entrySet()) {
                    // // if the recipient is found, write on its
                    // // output stream
                    // if (mc.name.equals(recipient) && mc._isloggedin == true) {
                    // mc.dos.writeUTF(this.name + " : " + MsgToSend);
                    // break;
                    // }
                    // }

                case "INIT":
                    // code block
                    break;
                default:
                    // code block
            }
        }
        // figure out what kind of message it is
        // authenticate -> invoke authenticationHandler
        // chat -> invoke chatHandler
        // payment -> invoke paymentHandler
    }

    public boolean isMessageValid(String message) {
        // validate correct json format
        // if no - throw invalid json message format error
        // try parsing into ClientMessageObject
        // if no - throw invalid message format error
        return true;
    }

    // public boolean getIsUserLoggedIn() {
    // return _isUserLoggedIn;
    // }

    // public String GetCommand() {
    // return _clientMessageObject.command;
    // }

    // public String GetMessageStatus() {
    // return _clientMessageObject.messageStatus;
    // }

    public ClientMessageObject getClientMessageObject() {
        return _clientMessageObject;
    }

    public String getMessage() {
        return _outputMessage;
    }

    public boolean getIsUserLoggedIn() {
        return _isUserLoggedIn;
    }

    // public String GetToken() {
    // return _clientMessageObject.token;
    // }

    // public String GetOptionalParam() {
    // return _clientMessageObject.optionalParam;
    // }

    public static class ClientMessageObject {
        public boolean isUserLoggedIn;
        public String userIdToken;
        public String message;

    }
}