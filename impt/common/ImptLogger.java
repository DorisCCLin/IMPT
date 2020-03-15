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

public class ImptLogger {

    public void printLog(String source, String message) {
        printLog(source, message, ImptLoggerConfig.Level.DEFAULT);
    }

    // Print log on console
    public void printLog(String source, String message, String level) {
        DateTimeFormatter dateTimeFormmatter = DateTimeFormatter.ofPattern("hh:mm:ss.SSS a");
        LocalDateTime now = LocalDateTime.now();
        String timeStamp = dateTimeFormmatter.format(now);
        String[] sourceDissected = source.split("\\.");
        String shortSource = sourceDissected.length > 1 ? sourceDissected[sourceDissected.length - 1] : source;
        String formattedMessage = level.equals(ImptLoggerConfig.Level.PROMPT)
                ?  message : "[" + timeStamp + "] [" + level + "] [" + shortSource + "] " + message;

        System.out.println(formattedMessage);

        if (!level.equals(ImptLoggerConfig.Level.PROMPT)) {
            printToFile(formattedMessage);
        }
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

    public String getExceptionMessage(Exception ex) {
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }
}