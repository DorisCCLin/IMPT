package impt_server.handlers;

import java.util.*;
import static java.util.Map.entry;

class AuthenticationHandler {
    private static Map<String, String> _credentials = Map.ofEntries(entry("Doris", "12345"), entry("Calvin", "ca1vin"),
            entry("Brian", "password"));

    public static boolean authenticate(String userName, String password) {
        if ((_credentials.containsKey(userName))) {
            if (_credentials.get(userName) == password) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }
}