/**
 * ClientMessageObject is the returned object from ImptMessageManager to ImptClientManager.
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.server;

public class ClientMessageObject {
    public boolean isUserLoggedIn = false;
    public String userIdToken = "";
    public String prevUserIdToken = "";
    public String initNoneUserMessage = "";
    public String initCurrentUserMessage = "";
    public String initExistingUserMessage = "";
    public String message = "";
    public String command = "";
}