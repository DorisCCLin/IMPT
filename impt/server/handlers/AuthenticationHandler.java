/**
 * AuthenticationHandler is to handle authentication related tasks.
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */
package impt.server.handlers;

import java.util.*;
import static java.util.Map.entry;

public class AuthenticationHandler {
    private static AuthenticationObject _authenticationObject;

    // mock user credential data
    private static Map<String, String> _credentials = Map.ofEntries(entry("Doris", "doris1"),
            entry("Calvin", "calvin1"), entry("Brian", "brian1"), entry("Emily", "emily1"));

    // authenticating user's credential
    public AuthenticationObject authenticate(String userName, String password) {
        _authenticationObject = new AuthenticationObject();

        if ((_credentials.containsKey(userName))) {

            if (_credentials.get(userName).equals(password)) {
                _authenticationObject.userName = userName;
                _authenticationObject.userIdToken = UUID.randomUUID().toString();
                _authenticationObject.isUserIdLoggedIn = true;
                return _authenticationObject;
            } else {
                _authenticationObject.hasAuthError = true;
                return _authenticationObject;
            }

        } else {
            _authenticationObject.hasAuthError = true;
            return _authenticationObject;
        }
    }

    // the object return to ImptMessageManager
    public static class AuthenticationObject {
        public String userName = "";
        public String userIdToken = "";
        public boolean isUserIdLoggedIn = false;
        public boolean hasAuthError = false;
    }
}