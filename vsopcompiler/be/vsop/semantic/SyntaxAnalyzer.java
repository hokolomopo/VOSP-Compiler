package be.vsop.semantic;

import be.vsop.AST.ClassItem;
import be.vsop.AST.Program;
import be.vsop.exceptions.semantic.ClassNotDeclaredException;
import be.vsop.exceptions.semantic.CyclicInheritanceException;
import be.vsop.exceptions.semantic.MainException;
import be.vsop.exceptions.semantic.SemanticException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SyntaxAnalyzer {
    private Program program;
    private String languageDirPath;
    private HashMap<String, ClassItem> classTable;
    private ArrayList<SemanticException> errors = new ArrayList<>();

    public SyntaxAnalyzer(Program program, String languageDirPath) {
        this.program = program;
        this.languageDirPath = languageDirPath;
    }

    public void analyze(){
        //Create the class table
        LanguageSpecs languageSpecs = new LanguageSpecs(languageDirPath);
        classTable = languageSpecs.getLanguageClassTable();
        program.updateClassTable(classTable, errors);

        //Check for cycles and extended but not declared classes
        HashSet<ClassItem> involvedInCycle = new HashSet<>();
        HashSet<String> extendedButNotDeclared = new HashSet<>();
        classTable.forEach((name, classItem) -> {
            if (! involvedInCycle.contains(classItem)) {
                // ArrayList to retain order : error message easier to read
                checkForCycle(classItem, new ArrayList<>(), involvedInCycle, extendedButNotDeclared);
            }
        });

        if (!classTable.containsKey("Main")) {
            errors.add(new MainException("Input file should contain a Main class", 0, 0));
        }
        if (errors.size() == 0) {
            program.fillScopeTable(null, errors);
        }
        if (errors.size() == 0) {
            program.checkTypes(errors);
        }
        if (errors.size() == 0) {
            program.checkScope(errors);
        }

//        program.fillScopeTable(null, errors);
//        program.checkTypes(errors);
//        program.checkScope(errors);

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
            // Stop if current class is a default one, otherwise pointer exception because object has no parent
            if (LanguageSpecs.isDefaultClass(current.getName())) {
                return;
            }
            //Add the current class to the visited one
            visited.add(current);

            //Check for cycle in the parent of the class
            ClassItem parent = classTable.get(current.getParentName());

            if(parent == null) {
                if (! extendedButNotDeclared.contains(current.getParentName())) {
                    this.errors.add(new ClassNotDeclaredException(current.getParentName(), current.line, current.parentNameColumn()));
                    extendedButNotDeclared.add(current.getParentName());
                }
            }
            else
                checkForCycle(parent, visited, involvedInCycle, extendedButNotDeclared);
        }
    }

    public boolean hasError(){
        return errors.size() > 0;
    }

    public ArrayList<SemanticException> getErrors() {
        return errors;
    }
}
