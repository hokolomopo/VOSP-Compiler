package be.vsop.exceptions.semantic;

public class CyclicInheritanceException extends SemanticException {
    public CyclicInheritanceException(String className, int line, int column) {
        super(line, column);

        this.message = "Cyclic inheritance in class " + className;
    }
}
