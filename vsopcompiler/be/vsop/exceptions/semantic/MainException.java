package be.vsop.exceptions.semantic;

/**
 * This class represents a semantic exception which occurs when the main class is not defined properly
 */
public class MainException extends  SemanticException {
    public MainException(String message, int line, int column) {
        super(line, column);
        this.message = message;
    }
}
