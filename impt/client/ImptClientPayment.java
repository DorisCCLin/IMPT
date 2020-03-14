/**
 * ImptClientPayment The client payment module for payment inquiry
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

import java.util.Scanner;
import impt.common.*;

public class ImptClientPayment {
    // Init information
    private String _recipientUsername;
    private String _recipientUserIdToken;
    private String _paymentAmount;
    private String _paymentService;

    private static ImptLogger _logger = new ImptLogger();

    public ImptClientPayment(String recipientUsername, String recipientUserIdToken) {
        _recipientUsername = recipientUsername;
        _recipientUserIdToken = recipientUserIdToken;
    }

    private void getPaymentServiceChoice() {
        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        _logger.printLog(this.getClass().toString(), "what payment service, please enter the number?");
        _logger.printLog(this.getClass().toString(), "(1.PayPal 2.Venmo 3.Cash)");

        String service = myObj.nextLine();

        switch (service) {
            case "1":
                _paymentService = "PayPay";
                _logger.printLog(this.getClass().toString(), "You picked " + _paymentService);
                break;
            case "2":
                _paymentService = "Venmo";
                _logger.printLog(this.getClass().toString(), "You picked " + _paymentService);
            case "3":
                _paymentService = "Cash";
                _logger.printLog(this.getClass().toString(), "You picked " + _paymentService);
            default:
                getPaymentServiceChoice();
        }

    }

    public String initialPaymentSend() {
        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        _logger.printLog(this.getClass().toString(), "You are sending payment to " + _recipientUsername + "? (y/n):");
        String response = myObj.nextLine();

        if (response.equals("y") || response.equals("n")) {
            if (response.equals("y")) {
                _logger.printLog(this.getClass().toString(), "How much would you like to send?");
                _paymentAmount = myObj.nextLine();
                getPaymentServiceChoice();
            }

        } else {
            initialPaymentSend();
        }

        return "PAYSND BEGIN " + _recipientUserIdToken + " " + _paymentAmount + " " + _paymentService;
    }

    public void handlePaymentResponse(String[] response) {
        if (response.length == 4 && response[3].equals("success")) {
            _logger.printLog(this.getClass().toString(),
                    "You have paid " + _recipientUsername + " " + response[2] + ".");
        } else
            switch (response[2]) {
                case ("fail"):
                    _logger.printLog(this.getClass().toString(), "Opps, transaction failed");
                    break;
                default:
                    _logger.printLog(this.getClass().toString(), "Opps, something went wrong");

            }
    }

}