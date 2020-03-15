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

    // fetch User's choice of payment service
    private void getPaymentServiceChoice() {
        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        _logger.printLog(this.getClass().toString(), "what payment service, please enter the number?",
                ImptLoggerConfig.Level.PROMPT);
        _logger.printLog(this.getClass().toString(), "(1.PayPal 2.Venmo 3.Cash)", ImptLoggerConfig.Level.PROMPT);

        String service = myObj.nextLine();

        switch (service) {
            case "1":
                _paymentService = "PayPay";
                _logger.printLog(this.getClass().toString(), "You picked " + _paymentService,
                        ImptLoggerConfig.Level.INFO);
                break;
            case "2":
                _paymentService = "Venmo";
                _logger.printLog(this.getClass().toString(), "You picked " + _paymentService,
                        ImptLoggerConfig.Level.INFO);
                break;
            case "3":
                _paymentService = "Cash";
                _logger.printLog(this.getClass().toString(), "You picked " + _paymentService,
                        ImptLoggerConfig.Level.INFO);
                break;
            default:
                getPaymentServiceChoice();
        }

    }

    // fetch User's info to send a payment request
    public String initialPaymentSend() {
        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        _logger.printLog(this.getClass().toString(), "Are you sending payment to " + _recipientUsername + "? (y/n):",
                ImptLoggerConfig.Level.PROMPT);
        String response = myObj.nextLine();

        if (response.equals("y") || response.equals("n")) {
            if (response.equals("y")) {
                _logger.printLog(this.getClass().toString(), "How much would you like to send?",
                        ImptLoggerConfig.Level.PROMPT);
                _paymentAmount = myObj.nextLine();
                getPaymentServiceChoice();
            }

        } else {
            initialPaymentSend();
        }

        return "PAYSND BEGIN " + _recipientUserIdToken + " " + _paymentAmount + " " + _paymentService;
    }

    // handle when Server resturns the payment transaction result
    public void handlePaymentResponse(String[] response) {
        if (response.length == 4 && response[3].equals("success")) {
            _logger.printLog(this.getClass().toString(),
                    "You have paid " + _recipientUsername + " " + response[2] + ".", ImptLoggerConfig.Level.INFO);
        } else
            switch (response[2]) {
                case ("fail"):
                    _logger.printLog(this.getClass().toString(), "Opps, transaction failed",
                            ImptLoggerConfig.Level.INFO);
                    break;
                default:
                    _logger.printLog(this.getClass().toString(), "Opps, something went wrong",
                            ImptLoggerConfig.Level.INFO);

            }
    }

}