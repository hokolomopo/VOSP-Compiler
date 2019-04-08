package be.vsop.exceptions.semantic;

public class MethodAlreadyDeclaredException extends SemanticException {
    public MethodAlreadyDeclaredException(String methodName, int line, int column, int prevDecLine, int prevDecColumn) {
        super(line, column);

        this.message = "Method " + methodName + " already declared (at position " + prevDecLine + ":" + prevDecColumn + ")";
    }
}
