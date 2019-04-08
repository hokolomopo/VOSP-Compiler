package be.vsop.exceptions.semantic;

public class MainException extends  SemanticException {
    public MainException(String message, int line, int column) {
        super(line, column);
        this.message = message;
    }
}
