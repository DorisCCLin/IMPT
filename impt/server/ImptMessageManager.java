package impt.server;

import java.util.*;

import impt.server.handlers.*;

class ImptMessageManger {
    private ClientMessageObject _clientMessageObject = new ClientMessageObject();

    public void handleClientMessage(String message) {
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
                            _clientMessageObject.initNoneUserMessage = "INIT BEGIN none none";
                        } else {
                            String prevUsername = ImptServer.activeUsers.keySet().iterator().next();
                            String prevUserIdToken = ImptServer.activeUsers.get(prevUsername);
                            _clientMessageObject.prevUserIdToken = prevUserIdToken;

                            _clientMessageObject.initCurrentUserMessage = "INIT BEGIN " + prevUsername + " "
                                    + prevUserIdToken;
                            _clientMessageObject.initExistingUserMessage = "INIT BEGIN " + authObject.userName + " "
                                    + authObject.userIdToken;
                        }

                        ImptServer.activeUsers.put(authObject.userName, authObject.userIdToken);

                    }

                    if (authObject.hasAuthError) {
                        _clientMessageObject.message = "ERR_AUTH BEGIN authError";
                    }
                    break;
                case "DISCONNECT":
                    _clientMessageObject.userIdToken = messageArr[2];

                    if (ImptServer.activeUsers.size() > 1) {
                        String currentUsername = "";
                        for (Map.Entry<String, String> entry : ImptServer.activeUsers.entrySet()) {
                            if (entry.getValue().equals(messageArr[2])) {
                                currentUsername = entry.getKey();
                            }
                        }

                        String otherUserIdToken = messageArr[2];
                        _clientMessageObject.prevUserIdToken = otherUserIdToken;

                        _clientMessageObject.initExistingUserMessage = "DISCONNECT FIN " + currentUsername + " "
                                + messageArr[2];
                    }
                    while (ImptServer.activeUsers.values().remove(_clientMessageObject.userIdToken))
                        ;
                    String prevUsername = ImptServer.activeUsers.keySet().iterator().next();
                    String prevUserIdToken = ImptServer.activeUsers.get(prevUsername);
                    _clientMessageObject.prevUserIdToken = prevUserIdToken;

                    _clientMessageObject.message = "DISCONNECT FIN";

                    System.out.println("Active Users[MessageManager]: " + ImptServer.activeUsers);

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
}