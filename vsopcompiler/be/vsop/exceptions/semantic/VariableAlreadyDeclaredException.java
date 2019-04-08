package be.vsop.exceptions.semantic;

public class VariableAlreadyDeclaredException extends SemanticException {
    public VariableAlreadyDeclaredException(String variableName, int line, int column, int prevDecLine, int prevDecColumn) {
        super(line, column);

        this.message = "Variable " + variableName + " already declared (at position " + prevDecLine + ":" + prevDecColumn + ")";
    }
}
