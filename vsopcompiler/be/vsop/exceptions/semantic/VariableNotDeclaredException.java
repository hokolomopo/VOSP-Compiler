package be.vsop.exceptions.semantic;

/**
 * This class represents a semantic exception which occurs when a variable is used but not declared
 */
public class VariableNotDeclaredException extends SemanticException {
    public VariableNotDeclaredException(String variableName, int line, int column) {
        super(line, column);

        this.message = "Variable " +  variableName + " not declared";
    }
}
