/**
 * ClientAuth The client authentication module for the initial handshake
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */

package impt.client;

import java.util.Scanner;

public class ImptClientPayment {
    // Init information
    private String _recipientUsername;
    private String _recipientUserIdToken;
    private String _paymentAmount;
    private String _paymentService;

    public ImptClientPayment(String recipientUsername, String recipientUserIdToken) {
        _recipientUsername = recipientUsername;
        _recipientUserIdToken = recipientUserIdToken;
    }

    private void getPaymentServiceChoice() {
        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        System.out.println("what payment service, please enter the number?");
        System.out.println("(1.PayPal 2.Venmo 3.Cash)");

        String service = myObj.nextLine();

        switch (service) {
            case "1":
                _paymentService = "PayPay";
                System.out.println("You picked " + _paymentService);
                break;
            case "2":
                _paymentService = "Venmo";
                System.out.println("You picked " + _paymentService);
            case "3":
                _paymentService = "Cash";
                System.out.println("You picked " + _paymentService);
            default:
                getPaymentServiceChoice();
        }

    }

    public String initialPaymentSend() {
        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        System.out.println("You are sending payment to " + _recipientUsername + "? (y/n):");
        String response = myObj.nextLine();

        if (response.equals("y") || response.equals("n")) {
            if (response.equals("y")) {
                System.out.println("How much would you like to send?");
                _paymentAmount = myObj.nextLine();
                getPaymentServiceChoice();
            }

        } else {
            initialPaymentSend();
        }

        return "PAYSD BEGIN " + _recipientUserIdToken + " " + _paymentAmount + " " + _paymentService;
    }
}