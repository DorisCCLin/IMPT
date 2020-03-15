/**
 * PaymentHandler is to handle payment inquiry related tasks.
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.server.handlers;

import java.util.*;
import impt.common.*;

public class PaymentHandler {
    // mock user payment data
    private Map<String, List<String>> _userPaymentServices = Map.ofEntries(
            Map.entry("Doris",
                    List.of(ImptPaymentConfig.PaymentService.PAYPAL, ImptPaymentConfig.PaymentService.VENMO,
                            ImptPaymentConfig.PaymentService.CASH)),
            Map.entry("Calvin",
                    List.of(ImptPaymentConfig.PaymentService.PAYPAL, ImptPaymentConfig.PaymentService.CASH)),
            Map.entry("Brian", List.of(ImptPaymentConfig.PaymentService.VENMO)),
            Map.entry("Emily", Collections.emptyList()));

    private PaymentObject _paymentObject = new PaymentObject();;

    // Prepare payment info to send to both users when connection is established
    public void preparePaymentInfo(String usernameOne, String usernameTwo) {
        List<String> usernameOneServices = _userPaymentServices.get(usernameOne);
        List<String> usernameTwoServices = _userPaymentServices.get(usernameTwo);

        System.out.println("usernameOneServices:" + usernameOneServices);
        System.out.println("usernameTwoServices:" + usernameTwoServices);

        if (!usernameOneServices.isEmpty() && !usernameOneServices.isEmpty()) {
            List<String> commonServices = new ArrayList<String>(usernameOneServices);

            commonServices.retainAll(usernameTwoServices);
            _paymentObject.matchedPaymentServices = String.join(",", commonServices);
        } else {
            _paymentObject.hasPaymentError = true;
        }
    }

    public PaymentObject getPaymentObject() {
        return _paymentObject;
    }

    public PaymentObject sendPayment() {
        // mock payment service api response
        Random sendToPaymentServiceAPI = new Random();
        _paymentObject.isPaymentSuccess = sendToPaymentServiceAPI.nextBoolean();

        return _paymentObject;
    }

    // the object return to ImptMessageManager
    public class PaymentObject {
        public boolean isPaymentSuccess = false;
        public String matchedPaymentServices = "";
        public boolean hasPaymentError = false;
    }
}