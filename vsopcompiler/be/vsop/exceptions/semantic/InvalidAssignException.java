package be.vsop.exceptions.semantic;

public class InvalidAssignException extends  SemanticException {
    public InvalidAssignException(int line, int column) {
        super(line, column);
        this.message = "Assigning to self is forbidden";
    }
}
