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

        int x = 3;
    }

    private void buildClassTable(){
        //Create the class table
        ClassList classList = LanguageSpecs.getLanguageClasses();

        program.addClassList(classList);
        program.updateClassTable(scopeTable, errors);

        //Check for cycles
        scopeTable.getClassTable().forEach((name, classItem) -> {
            checkForCycle(classItem, new HashSet<>());
        });
    }

    /**
     * Check for inheritance cycles in class declaration in the ScopeTable field.
     *
     * @param current the class where we are checking for cycles
     * @param visited the already visoted children of this class
     */
    private void checkForCycle(ClassItem current, HashSet<String> visited){
        //Report error if we find a cycle
        if(visited.contains(current.getName()))
            this.errors.add(new CyclicInheritanceException(current.getName(), current.getLine(), current.getColumn()));

        else {
            //Stop if the class is a class defined by the language itself (like Object)
            if(LanguageSpecs.isDefaultClass(current.getName()))
                return;

            //Add the current class to the visited one
            visited.add(current.getName());

            //Check for cycle in the parent of the class
            ClassItem parent = this.scopeTable.getClassTable().get(current.getParentName());

            if(parent == null)
                this.errors.add(new ClassNotDeclaredException(current.getParentName(), current.getLine(), current.getColumn()));
            else
                checkForCycle(this.scopeTable.getClassTable().get(current.getParentName()), visited);
        }
    }

    public boolean hasError(){
        return errors.size() > 0;
    }

    public ArrayList<SemanticException> getErrors() {
        return errors;
    }
}
