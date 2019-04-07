package be.vsop.semantic;

import be.vsop.AST.ClassItem;
import be.vsop.AST.ClassList;
import be.vsop.AST.Program;
import be.vsop.exceptions.semantic.ClassNotDeclaredException;
import be.vsop.exceptions.semantic.CyclicInheritanceException;
import be.vsop.exceptions.semantic.SemanticException;

import java.util.ArrayList;
import java.util.HashSet;

public class SyntaxAnalyzer {

    private Program program;
    private ScopeTable scopeTable = new ScopeTable();
    private ArrayList<SemanticException> errors = new ArrayList<>();

    public SyntaxAnalyzer(Program program) {
        this.program = program;
    }

    public void analyze(){
        this.buildClassTable();
        program.updateClassItems(scopeTable, errors);
        program.checkScope(scopeTable, errors);
    }

    private void buildClassTable(){
        //Create the class table
        ClassList classList = LanguageSpecs.getLanguageClasses();

        program.addClassList(classList);
        program.updateClassTable(scopeTable, errors);

        //Check for cycles and extended but not declared classes
        HashSet<ClassItem> involvedInCycle = new HashSet<>();
        HashSet<String> extendedButNotDeclared = new HashSet<>();
        scopeTable.getClassTable().forEach((name, classItem) -> {
            if (! involvedInCycle.contains(classItem)) {
                // ArrayList to retain order : error message easier to read
                checkForCycle(classItem, new ArrayList<>(), involvedInCycle, extendedButNotDeclared);
            }
        });
    }

    /**
     * Check for inheritance cycles in class declaration in the ScopeTable field.
     *
     * @param current the class where we are checking for cycles
     * @param visited the already visited children of this class
     * @param involvedInCycle the classes that have already been discovered as being in a cycle
     * @param extendedButNotDeclared the classes that have already been discovered as being used (extended) but not declared
     */
    private void checkForCycle(ClassItem current, ArrayList<ClassItem> visited,
                               HashSet<ClassItem> involvedInCycle, HashSet<String> extendedButNotDeclared) {
        //Report error if we find a cycle
        if(visited.contains(current)) {
            // We want to only print the classes that are indeed involved in the cycle.
            // For instance if A extends B, B extends A and C extends B, we exclude C from the print.
            if (visited.get(0).equals(current)) {
                this.errors.add(new CyclicInheritanceException(visited));
                involvedInCycle.addAll(visited);
            }
        }

        else {
            //Stop if the class is a class defined by the language itself (like Object)
            if(LanguageSpecs.isDefaultClass(current.getName()))
                return;

            //Add the current class to the visited one
            visited.add(current);

            //Check for cycle in the parent of the class
            ClassItem parent = this.scopeTable.getClassTable().get(current.getParentName());

            if(parent == null) {
                if (! extendedButNotDeclared.contains(current.getParentName())) {
                    this.errors.add(new ClassNotDeclaredException(current.getParentName(), current.getLine(), current.getColumn()));
                    extendedButNotDeclared.add(current.getParentName());
                }
            }
            else
                checkForCycle(this.scopeTable.getClassTable().get(current.getParentName()), visited, involvedInCycle, extendedButNotDeclared);
        }
    }

    public boolean hasError(){
        return errors.size() > 0;
    }

    public ArrayList<SemanticException> getErrors() {
        return errors;
    }
}
