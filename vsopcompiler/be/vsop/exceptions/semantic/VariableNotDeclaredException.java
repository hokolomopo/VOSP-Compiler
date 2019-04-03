package be.vsop.exceptions.semantic;

public class VariableNotDeclaredException extends SemanticException {
    public VariableNotDeclaredException(String variableName, int line, int column) {
        super(line, column);

        this.message = "Variable " +  variableName + " not declared";
    }
}
