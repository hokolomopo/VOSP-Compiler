package be.vsop.exceptions.semantic;

/**
 * This class represents a semantic exception which occurs when a class is defined twice
 */
public class ClassAlreadyDeclaredException extends SemanticException {
    public ClassAlreadyDeclaredException(String className, int line, int column, int prevDecLine, int prevDecColumn) {
        super(line, column);

        this.message = "Class " + className + " already declared (at position " + prevDecLine + ":" + prevDecColumn + ")";
    }

}
