package be.vsop.exceptions.semantic;

public class VariableNotDeclaredException extends SemanticException {
    public VariableNotDeclaredException(String varableName, int line, int column) {
        super(line, column);

        this.message = "Variable " +  varableName + " not declared";
    }
}
