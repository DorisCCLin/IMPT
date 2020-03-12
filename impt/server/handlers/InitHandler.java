package impt.server.handlers;

import impt.server.*;

public class InitHandler {
    private static InitObject _initObject;

    public InitObject initialize(String recipientUserName, String myUserName) {
        if ((ImptServer.activeUsers.containsKey(recipientUserName))) {
            _initObject.initUserName = myUserName;
            _initObject.userName = recipientUserName;
            _initObject.userIdToken = ImptServer.activeUsers.get(recipientUserName);
            return _initObject;

        } else {
            _initObject.hasInitError = true;

            return _initObject;
        }
    }

    public static class InitObject {
        public String initUserName;
        public String userName;
        public String userIdToken = "";
        public boolean areUsersConnected = false;
        public boolean hasInitError = false;

    }
}