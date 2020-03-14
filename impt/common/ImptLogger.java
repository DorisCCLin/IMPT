/**
 * ImptLogger is to print logs of communication between server and client
 * 
 * @author Doris Chia-ching Lin
 * @version 1
 */
package impt.common;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.io.*;
import java.awt.*;

public class ImptLogger {

    public void printLog(String source, String message) {
        printLog(source, message, ImptLoggerConfig.Level.DEFAULT);
    }

    // Print log on console
    public void printLog(String source, String message, String level) {
        DateTimeFormatter dateTimeFormmatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss.SSS a");
        LocalDateTime now = LocalDateTime.now();
        String timeStamp = dateTimeFormmatter.format(now);
        String[] sourceDissected = source.split("\\.");
        String shortSource = sourceDissected.length > 1 ? sourceDissected[sourceDissected.length - 1] : source;
        String formattedMessage = "[" + timeStamp + "] [" + shortSource + "] " + message;
        formatMessage(level);

        System.out.println(formattedMessage);
        printToFile(formattedMessage);
    }

    // Print log a output file
    public void printToFile(String message) {
        DateTimeFormatter dateTimeFormmatter = DateTimeFormatter.ofPattern("MM.dd.yyyy");
        LocalDateTime currentDate = LocalDateTime.now();
        String fileName = "log_" + dateTimeFormmatter.format(currentDate) + ".txt";

        try {
            FileWriter fileWriter = new FileWriter(fileName, true); // Set true for append mode
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(message);
            printWriter.close();
        } catch (Exception ex) {
            // Meh...
        }
    }

    private void formatMessage(String level)
    {
        // Color color = new Color(255, 0, 0);

        switch (level) {
            case ImptLoggerConfig.Level.DEFAULT:
                System.out.println(Color.LIGHT_GRAY);
                // formattedMessage = ImptLoggerConfig.Color.ANSI_RESET + formattedMessage
                //         + ImptLoggerConfig.Color.ANSI_RESET;
                break;
            case ImptLoggerConfig.Level.PROMPT:
            System.out.println(Color.LIGHT_GRAY);
                // formattedMessage = ImptLoggerConfig.Color.ANSI_WHITE + formattedMessage
                //         + ImptLoggerConfig.Color.ANSI_RESET;
                break;
            case ImptLoggerConfig.Level.INFO:
            System.out.println(Color.LIGHT_GRAY);
                // formattedMessage = ImptLoggerConfig.Color.ANSI_YELLOW + formattedMessage
                //         + ImptLoggerConfig.Color.ANSI_RESET;
                break;
            case ImptLoggerConfig.Level.ERROR:
            System.out.println(Color.LIGHT_GRAY);
                // formattedMessage = ImptLoggerConfig.Color.ANSI_RED + formattedMessage
                //         + ImptLoggerConfig.Color.ANSI_RESET;
                break;
            case ImptLoggerConfig.Level.DEBUG:
            System.out.println(Color.LIGHT_GRAY);
                // formattedMessage = ImptLoggerConfig.Color.ANSI_CYAN + formattedMessage
                //         + ImptLoggerConfig.Color.ANSI_RESET;
                break;
        }
    }
}