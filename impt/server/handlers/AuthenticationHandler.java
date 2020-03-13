package impt.server.handlers;

import java.util.*;
import static java.util.Map.entry;

public class AuthenticationHandler {
    private static AuthenticationObject _authenticationObject;
    private static Map<String, String> _credentials = Map.ofEntries(entry("Doris", "Doris1"), entry("Calvin", "ca1vin"),
            entry("Brian", "password"));

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

    public static class AuthenticationObject {
        public String userName = "";
        public String userIdToken = "";
        public boolean isUserIdLoggedIn = false;
        public boolean hasAuthError = false;

    }
}