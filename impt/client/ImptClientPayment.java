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
    private String _chosenService;
    private String[] _paymentServices;

    private static ImptLogger _logger = new ImptLogger();

    public ImptClientPayment(String recipientUsername, String recipientUserIdToken, String[] paymentServices) {
        _recipientUsername = recipientUsername;
        _recipientUserIdToken = recipientUserIdToken;
        _paymentServices = paymentServices;
    }

    private String renderPaymentServices(String[] paymentServices) {
        String list = "";
        for (String service : paymentServices) {
            int i = 1;
            list.concat(i + "." + service + " ");
            i++;
        }

        return list;
    }

    // fetch User's choice of payment service
    private void getPaymentServiceChoice(String[] paymentServices) {
        String matchedPaymentServices = "** ( " + renderPaymentServices(paymentServices) + ") **";

        Scanner paymentScanner = new Scanner(System.in); // Create a Scanner object
        _logger.printLog(this.getClass().toString(), "** what payment service, please enter the number? **",
                ImptLoggerConfig.Level.PROMPT);
        _logger.printLog(this.getClass().toString(), matchedPaymentServices, ImptLoggerConfig.Level.PROMPT);

        String entryNumber = paymentScanner.nextLine();

        for (String service : paymentServices) {
            int i = 0;
            if (entryNumber.equals(Integer.toString(i + 1))) {
                _chosenService = service;
                _logger.printLog(this.getClass().toString(), "You picked " + _chosenService,
                        ImptLoggerConfig.Level.INFO);
            }
            i++;
        }

        paymentScanner.close();
    }

    // fetch User's info to send a payment request
    public String initialPaymentSend() {
        Scanner paymentScanner = new Scanner(System.in); // Create a Scanner object
        _logger.printLog(this.getClass().toString(), "Are you sending payment to " + _recipientUsername + "? (y/n):",
                ImptLoggerConfig.Level.PROMPT);
        String response = paymentScanner.nextLine();

        if (response.equals("y") || response.equals("n")) {
            if (response.equals("y")) {
                _logger.printLog(this.getClass().toString(), "How much would you like to send?",
                        ImptLoggerConfig.Level.PROMPT);
                _paymentAmount = paymentScanner.nextLine();
                getPaymentServiceChoice(_paymentServices);
            }
        } else {
            initialPaymentSend();
        }

        paymentScanner.close();

        return "PAYSND BEGIN " + _recipientUserIdToken + " " + _paymentAmount + " " + _chosenService;
    }

    // handle when Server resturns the payment transaction result
    public void handlePaymentResponse(String[] response) {
        if (response.length == 4 && response[3].equals("success")) {
            _logger.printLog(this.getClass().toString(),
                    "You have paid " + _recipientUsername + " " + response[2] + ".", ImptLoggerConfig.Level.INFO);
        } else {
            switch (response[2]) {
                case ("fail"):
                    _logger.printLog(this.getClass().toString(), "Opps, transaction failed",
                            ImptLoggerConfig.Level.INFO);
                    break;
                default:
                    _logger.printLog(this.getClass().toString(), "Opps, something went wrong",
                            ImptLoggerConfig.Level.INFO);
                    break;
            }
        }
    }
}