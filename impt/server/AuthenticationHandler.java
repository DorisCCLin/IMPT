/**
 * AuthenticationHandler is to handle authentication related tasks.
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */
package impt.server;

import java.util.*;

class AuthenticationHandler {
    private AuthenticationObject _authenticationObject = new AuthenticationObject();

    // mock user credential data
    private Map<String, String> _credentials = Map.ofEntries(Map.entry("Doris", "doris1"),
            Map.entry("Calvin", "calvin1"), Map.entry("Brian", "brian1"), Map.entry("Emily", "emily1"));

    // authenticating user's credential
    public void authenticate(String userName, String password) {

        if ((_credentials.containsKey(userName))) {

            if (_credentials.get(userName).equals(password)) {
                _authenticationObject.userName = userName;
                _authenticationObject.userIdToken = UUID.randomUUID().toString();
                _authenticationObject.isUserIdLoggedIn = true;
            } else {
                _authenticationObject.hasAuthError = true;
            }

        } else {
            _authenticationObject.hasAuthError = true;
        }
    }

    public AuthenticationObject getAuthenticationObject() {
        return _authenticationObject;
    }

    // the object return to ImptMessageManager
    public class AuthenticationObject {
        public String userName = "";
        public String userIdToken = "";
        public boolean isUserIdLoggedIn = false;
        public boolean hasAuthError = false;
    }
}