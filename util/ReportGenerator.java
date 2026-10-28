package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {

    public static void generate(Path logsDir, Path reportFile) {
        long totalDeposits    = 0;
        long totalWithdrawals = 0;
        StringBuilder report  = new StringBuilder();

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        report.append("===== END-OF-DAY SUMMARY REPORT =====\n");
        report.append("Generated : ").append(timestamp).append("\n\n");

        // Walk logs directory using Files.newDirectoryStream
        try (DirectoryStream<Path> stream =
                Files.newDirectoryStream(logsDir, "*.log")) {

            for (Path logFile : stream) {

                // Read file metadata with Files.readAttributes
                BasicFileAttributes attrs =
                        Files.readAttributes(logFile, BasicFileAttributes.class);

                report.append("--- ").append(logFile.getFileName()).append(" ---\n");
                report.append("  Size          : ")
                        .append(attrs.size()).append(" bytes\n");
                report.append("  Last modified : ")
                        .append(attrs.lastModifiedTime()).append("\n");

                // Read each line with BufferedReader
                try (BufferedReader reader = Files.newBufferedReader(logFile)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("DEPOSIT:")) {
                            totalDeposits += Long.parseLong(
                                    line.substring("DEPOSIT:".length()).trim());
                        } else if (line.startsWith("WITHDRAW:")) {
                            totalWithdrawals += Long.parseLong(
                                    line.substring("WITHDRAW:".length()).trim());
                        }
                    }
                }
            }

        } catch (IOException e) {
            report.append("Error reading logs: ").append(e.getMessage()).append("\n");
        }

        report.append("\n===== TOTALS =====\n");
        report.append("Total Deposits   : Rs. ").append(totalDeposits).append("\n");
        report.append("Total Withdrawals: Rs. ").append(totalWithdrawals).append("\n");
        report.append("Net Change       : Rs. ")
                .append(totalDeposits - totalWithdrawals).append("\n");
        report.append("=====================================\n");

        // Write summary to report file
        try {
            Files.writeString(reportFile, report.toString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Report written to: " + reportFile);
        } catch (IOException e) {
            System.out.println("Report write failed: " + e.getMessage());
        }

        System.out.println(report);
    }
}
