package impt.common;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.io.*;

public class ImptLogger {
    public void printLog(String source, String message) {
        DateTimeFormatter dateTimeFormmatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss.SSS a");
        LocalDateTime now = LocalDateTime.now();
        String timeStamp = dateTimeFormmatter.format(now);
        String[] sourceDissected = source.split("\\.");
        String shortSource = sourceDissected.length > 1 ? sourceDissected[sourceDissected.length - 1] : source;

        String formattedMessage = "[" + timeStamp + "] [" + shortSource + "] " + message;

        System.out.println(formattedMessage);
        printToFile(formattedMessage);
    }

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
}