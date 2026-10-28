package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TransactionLog {

    public static void append(Path file, String line) {
        try {
            Files.writeString(file,
                    line + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Log write failed: " + e.getMessage());
        }
    }
}
