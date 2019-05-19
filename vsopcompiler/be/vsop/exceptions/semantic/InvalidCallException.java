package be.vsop.exceptions.semantic;

/**
 * This class represents a semantic exception which occurs when a call is not valid (several reasons are possible)
 */
public class InvalidCallException extends SemanticException {
    public InvalidCallException(String methodName, int line, int column, int prevDecLine, int prevDecColumn, String appendToMessage) {
        super(line, column);
        this.message = "Invalid call to method " + methodName + " (defined at position " + prevDecLine + ":" + prevDecColumn + "): ";
        this.message += appendToMessage;
    }
}