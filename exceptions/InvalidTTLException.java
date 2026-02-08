package exceptions;

public class InvalidTTLException extends InvalidCommandException {
    public InvalidTTLException(String message) {
        super(message);
    }
}
