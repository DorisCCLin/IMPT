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