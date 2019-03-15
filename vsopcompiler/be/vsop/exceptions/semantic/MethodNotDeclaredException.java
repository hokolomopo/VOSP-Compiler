package be.vsop.exceptions.semantic;

public class MethodNotDeclaredException extends SemanticException{
    public MethodNotDeclaredException(String methodName, int line, int column) {
        super(line, column);

        this.message = "Method " + methodName + " not declared";
    }
}
