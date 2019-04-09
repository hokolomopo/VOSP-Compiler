package be.vsop.exceptions.semantic;

public class InvalidAssignException extends  SemanticException {
    public InvalidAssignException(String message, int line, int column) {
        super(line, column);
        this.message = message;
    }
}
