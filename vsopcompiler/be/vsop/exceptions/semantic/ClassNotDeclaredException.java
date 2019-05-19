package be.vsop.exceptions.semantic;

/**
 * This class represents a semantic exception which occurs when a class is used but not declared
 */
public class ClassNotDeclaredException extends SemanticException{
    public ClassNotDeclaredException(String className, int line, int column) {
        super(line, column);

        this.message = "Class " + className + " not declared";
    }

}
