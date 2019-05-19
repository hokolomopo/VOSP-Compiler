package be.vsop.exceptions.semantic;

/**
 * This class represents a semantic exception which occurs when an assign is not valid (several reasons are possible)
 */
public class InvalidAssignException extends  SemanticException {
    public InvalidAssignException(String message, int line, int column) {
        super(line, column);
        this.message = message;
    }
}
