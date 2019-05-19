package be.vsop.exceptions.semantic;

/**
 * This class represents a semantic exception which occurs when a variable is declared twice
 */
public class VariableAlreadyDeclaredException extends SemanticException {
    public VariableAlreadyDeclaredException(String variableName, int line, int column, int prevDecLine, int prevDecColumn) {
        super(line, column);

        this.message = "Variable " + variableName + " already declared (at position " + prevDecLine + ":" + prevDecColumn + ")";
    }
}
