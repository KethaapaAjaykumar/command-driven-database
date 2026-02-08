package exceptions;

public class DatabaseStoppedException extends Exception {
    public DatabaseStoppedException(String message) {
        super(message);
    }
}