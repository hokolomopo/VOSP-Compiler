package be.vsop.exceptions.semantic;

/**
 * This class represents a semantic exception which occurs when an override is not valid (several reasons are possible)
 */
public class InvalidOverrideException extends SemanticException {
    public InvalidOverrideException(String methodName, int line, int column, int prevDecLine, int prevDecColumn, String appendToMessage) {
        super(line, column);
        this.message = "Invalid overriding of method " + methodName + " (first defined at position " + prevDecLine + ":" + prevDecColumn + "): ";
        this.message += appendToMessage;
    }
}
