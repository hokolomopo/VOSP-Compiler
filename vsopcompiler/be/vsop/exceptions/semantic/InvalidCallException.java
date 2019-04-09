package be.vsop.exceptions.semantic;

public class InvalidCallException extends SemanticException {
    public InvalidCallException(String methodName, int line, int column, int prevDecLine, int prevDecColumn, String appendToMessage) {
        super(line, column);
        this.message = "Invalid call to method " + methodName + " (defined at position " + prevDecLine + ":" + prevDecColumn + "): ";
        this.message += appendToMessage;
    }
}