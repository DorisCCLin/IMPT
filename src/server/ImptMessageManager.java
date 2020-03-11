package impt_server;

class ImptMessageManger {
    private ClientMessageObject _clientMessageObject;

    public ImptMessageManger(ClientMessageObject clientMessageObject) {
        _clientMessageObject = clientMessageObject;
    }

    public void Execute(String message) {
        boolean isMessageValid = isMessageValid(message);
        String[] messageArr = message.split(" ");

        if (isMessageValid) {
            switch (messageArr[2]) {
                case "AUTH":
                    // code block
                    break;
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