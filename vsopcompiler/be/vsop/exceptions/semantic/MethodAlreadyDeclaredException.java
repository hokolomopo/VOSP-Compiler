package be.vsop.exceptions.semantic;

/**
 * This class represents a semantic exception which occurs when a method is declared twice
 */
public class MethodAlreadyDeclaredException extends SemanticException {
    public MethodAlreadyDeclaredException(String methodName, int line, int column, int prevDecLine, int prevDecColumn) {
        super(line, column);

        this.message = "Method " + methodName + " already declared (at position " + prevDecLine + ":" + prevDecColumn + ")";
    }
}
