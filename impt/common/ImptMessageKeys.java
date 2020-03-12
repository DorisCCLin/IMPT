package impt.common;

import java.util.*;
import static java.util.Map.entry;

public class ImptMessageKeys {
    private static Map<String, String> imptCommands = Map.ofEntries(entry("auth", "AUTH"), entry("init", "INIT"),
            entry("poke", "POKE"));
    private static Map<String, String> imptSentBy = Map.ofEntries(entry("client", "C"), entry("server", "S"));
    private static Map<String, String> imptMessageStatus = Map.ofEntries(entry("begin", "BEGIN"),
            entry("respond", "RES"), entry("final", "FIN"));
    // private static Map<String, String> imptMessageStatus =
    // Map.ofEntries(entry("begin", "BEGIN"),
    // entry("respond", "RES"), entry("final", "FIN"));
    private static String userIdToken;

    public Map<String, String> getImptCommands() {
        return imptCommands;
    }

    public Map<String, String> getImptSentBy() {
        return imptSentBy;
    }

    public Map<String, String> getImptMessageStatus() {
        return imptMessageStatus;
    }

    public String getuserIdToken() {
        return UUID.randomUUID().toString();
    }
}
