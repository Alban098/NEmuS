package exceptions;

/**
 * This class represents an Exception thrown when ROM with an unsupported mapper is loaded
 */
public class UnsupportedMapperException extends Exception {

    /**
     * Create a new Exception
     *
     * @param message a description of the exception
     */
    public UnsupportedMapperException(String message) {
        super(message);
    }
}
