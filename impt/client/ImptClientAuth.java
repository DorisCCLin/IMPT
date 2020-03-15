/**
 * ClientAuth The client authentication module for the initial handshake
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

import java.util.Scanner;
import impt.common.*;

public class ImptClientAuth {
    private ImptLogger _logger = new ImptLogger();
    private String _userToken = null;
    private boolean _shouldCloseSocket = false;

    // fetch user log info
    public String getAuthInfo(Scanner loginScanner) {
        _logger.printLog(this.getClass().toString(), "<< ENTER USERNAME:", ImptLoggerConfig.Level.PROMPT);
        String username = loginScanner.nextLine(); // Read user input

        _logger.printLog(this.getClass().toString(), "<< ENTER PASSWORD:", ImptLoggerConfig.Level.PROMPT); // Output
                                                                                                           // user input
        String password = loginScanner.nextLine();

        return username + " " + password;
    }

    // when Authentication response returns, handle accordingly
    public void handleServerAuthResponse(String message) {
        String[] messageArr = message.split(" ");

        String serverResponseCommand = messageArr[0];

        switch (serverResponseCommand) {

            case "AUTH":
                _userToken = messageArr[2];
                _logger.printLog(this.getClass().toString(), "** Logged in successfully! **",
                        ImptLoggerConfig.Level.INFO);

                break;

            case "ERR_AUTH":
                if (messageArr[2].equals("serverFull")) {
                    _shouldCloseSocket = true;
                    _logger.printLog(this.getClass().toString(),
                            "** Oops! There are too many users online. :( Try later! **", ImptLoggerConfig.Level.INFO);
                }

                if (messageArr[2].equals("sameUser")) {
                    _logger.printLog(this.getClass().toString(),
                            "** It seems you have logged in somewhere. Are you sure you are you? **",
                            ImptLoggerConfig.Level.INFO);
                }

                if (messageArr[2].equals("authError")) {
                    _logger.printLog(this.getClass().toString(),
                            "** Oops! The username and password do not match. :( Try again! **",
                            ImptLoggerConfig.Level.INFO);
                }

                if (messageArr[2].equals("unknown")) {
                    _shouldCloseSocket = true;
                    _logger.printLog(this.getClass().toString(), "** Oops! Something went wrong, try later!**",
                            ImptLoggerConfig.Level.INFO);
                }
                break;
        }
    }

    public String getUserToken() {
        return _userToken;
    }

    public boolean getShouldCloseSocket() {
        return _shouldCloseSocket;
    }
}