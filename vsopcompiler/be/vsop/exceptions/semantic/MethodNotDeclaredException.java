package be.vsop.exceptions.semantic;

/**
 * This class represents a semantic exception which occurs when a method is used but no declared
 */
public class MethodNotDeclaredException extends SemanticException{
    public MethodNotDeclaredException(String methodName, int line, int column) {
        super(line, column);

        this.message = "Method " + methodName + " not declared";
    }
}
