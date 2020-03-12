package impt.server;

import java.util.*;

import impt.server.handlers.*;

class ImptMessageManger {
    private ClientMessageObject _clientMessageObject;

    public void receiveHandler(String message) {
        boolean isMessageValid = isMessageValid(message);
        String[] messageArr = message.split(" ");

        if (isMessageValid) {
            _clientMessageObject.command = messageArr[1];
            switch (messageArr[1]) {
                case "AUTH":
                    AuthenticationHandler authHandler = new AuthenticationHandler();
                    AuthenticationHandler.AuthenticationObject authObject = new AuthenticationHandler.AuthenticationObject();
                    authObject = authHandler.authenticate(messageArr[2], messageArr[3]);
                    _clientMessageObject.isUserLoggedIn = authObject.isUserIdLoggedIn;
                    _clientMessageObject.userIdToken = authObject.userIdToken;

                    if (_isUserLoggedIn) {
                        ImptServer.activeUsers.put(authObject.userName, authObject.userIdToken);
                        _clientMessageObject.message = "AUTH RES " + authObject.userIdToken;
                    }

                    if (authObject.hasAuthError) {
                        _clientMessageObject.message = "ERR_AUTH BEGIN authError";
                    }
                    break;

                case "INIT":
                    InitHandler initHandler = new InitHandler();
                    InitHandler.InitObject initObject = new InitHandler.InitObject();
                    String initUsername;
                    for (Map.Entry<String, String> entry : ImptServer.activeUsers.entrySet()) {
                        if (messageArr[3].equals(entry.getValue())) {
                            initUsername = entry.getKey();
                            break;
                        }
                    }

                    initObject = initHandler.initialize(messageArr[2], initUsername);

                    _clientMessageObject.userIdToken = initObject.userIdToken;

                    if (initObject.hasInitError) {
                        _clientMessageObject.message = "ERR_INIT BEGIN initError";
                    } else {
                        _clientMessageObject.message = "INIT_RQST BEGIN " + initObject.userIdToken + " "
                                + initObject.initUserName;
                    }

                    break;
                default:
                    // code block
            }
        }
    }

    public boolean isMessageValid(String message) {
        // validate correct json format
        // if no - throw invalid json message format error
        // try parsing into ClientMessageObject
        // if no - throw invalid message format error
        return true;
    }

    public ClientMessageObject getClientMessageObject() {
        return _clientMessageObject;
    }

    public static class ClientMessageObject {
        public boolean isUserLoggedIn;
        public String userIdToken;
        // public String recipientIdToken;
        public String message;
        public String command;

    }
}