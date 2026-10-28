package util;

import model.Account;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class StatePersister {

    public static void save(Account[] accounts, Path file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(file,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING))) {
            oos.writeObject(accounts);
            System.out.println("Accounts saved to: " + file);
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    public static Account[] load(Path file) {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(file))) {
            Account[] accounts = (Account[]) ois.readObject();
            System.out.println("Accounts loaded from: " + file);
            return accounts;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Load failed: " + e.getMessage());
            return new Account[0];
        }
    }
}
