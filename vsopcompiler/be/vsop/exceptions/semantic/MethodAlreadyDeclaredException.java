package be.vsop.exceptions.semantic;

public class MethodAlreadyDeclaredException extends SemanticException {
    public MethodAlreadyDeclaredException(String methodName, int line, int column) {
        super(line, column);

        this.message = "Method " + methodName + " already declared";
    }
}
