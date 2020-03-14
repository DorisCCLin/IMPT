/**
 * PaymentHandler is to handle payment inquiry related tasks.
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.server.handlers;

import java.util.*;

public class PaymentHandler {
    private PaymentObject _paymentObject = new PaymentObject();

    public PaymentObject sendPayment() {
        // mock payment service api response
        Random sendToPaymentServiceAPI = new Random();
        _paymentObject.isPaymentSuccess = sendToPaymentServiceAPI.nextBoolean();

        return _paymentObject;
    }

    public static class PaymentObject {
        public boolean isPaymentSuccess = false;

    }
}