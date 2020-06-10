package exceptions;

/**
 * This class represents an Exception thrown when an invalid ROM / Save if loaded
 */
public class InvalidFileException extends Exception {

    /**
     * Create a new Exception
     *
     * @param message a description of the exception
     */
    public InvalidFileException(String message) {
        super(message);
    }
}
