package impt.server;

import java.util.*;

import impt.server.handlers.*;

class ImptMessageManger {
    private ClientMessageObject _clientMessageObject = new ClientMessageObject();

    public void receiveHandler(String message) {
        boolean isMessageValid = isMessageValid(message);
        String[] messageArr = message.split(" ");

        if (isMessageValid) {
            _clientMessageObject.command = messageArr[0];
            switch (messageArr[0]) {
                case "AUTH":
                    AuthenticationHandler authHandler = new AuthenticationHandler();
                    AuthenticationHandler.AuthenticationObject authObject = new AuthenticationHandler.AuthenticationObject();
                    authObject = authHandler.authenticate(messageArr[2], messageArr[3]);
                    _clientMessageObject.isUserLoggedIn = authObject.isUserIdLoggedIn;
                    _clientMessageObject.userIdToken = authObject.userIdToken;

                    if (authObject.isUserIdLoggedIn) {

                        _clientMessageObject.message = "AUTH RES " + authObject.userIdToken;

                        if (ImptServer.activeUsers.size() == 0) {
                            _clientMessageObject.initNoneUserMessage = "INIT BEGIN none";
                        } else {
                            String prevUsername = ImptServer.activeUsers.keySet().iterator().next();
                            String prevUserIdToken = ImptServer.activeUsers.get(prevUsername);
                            _clientMessageObject.prevUserIdToken = prevUserIdToken;

                            _clientMessageObject.initCurrentUserMessage = "INIT BEGIN " + prevUsername;
                            _clientMessageObject.initExistingUserMessage = "INIT BEGIN " + authObject.userName;
                        }

                        ImptServer.activeUsers.put(authObject.userName, authObject.userIdToken);

                    }

                    if (authObject.hasAuthError) {
                        _clientMessageObject.message = "ERR_AUTH BEGIN authError";
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
        public boolean isUserLoggedIn = false;
        public String userIdToken = "";
        public String prevUserIdToken = "";
        public String initNoneUserMessage = "";
        public String initCurrentUserMessage = "";
        public String initExistingUserMessage = "";
        public String message = "";
        public String command = "";

    }
}