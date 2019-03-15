package be.vsop.exceptions.semantic;

public class VariableAlreadyDeclaredException extends SemanticException {
    public VariableAlreadyDeclaredException(String variableName, int line, int column) {
        super(line, column);

        this.message = "Variable " + variableName + " already declared";
    }
}
