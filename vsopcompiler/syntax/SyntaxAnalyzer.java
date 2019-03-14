package syntax;

import AST.ASTNode;
import AST.ClassItem;
import exceptions.SemanticError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SyntaxAnalyzer {

    ASTNode program;
    HashMap<String, ClassItem> classTable = new HashMap<>();
    ArrayList<SemanticError> errors = new ArrayList<>();

    public SyntaxAnalyzer(ASTNode program) {
        this.program = program;
    }

    public void analyze(){
        this.buildClassTable();
    }

    private void buildClassTable(){
        //Create the class table
        ArrayList<ClassItem> classList = new ArrayList<>(LanguageSpecs.getLanguageClasses());
        program.updateClassTable(classList);

        //Check for double declarations
        for(ClassItem item : classList){
            if(this.classTable.containsKey(item.getName()))
                reportError("Class " + item.getName() + " already declared",
                        item.getLine(), item.getColumn());
            else
                this.classTable.put(item.getName(), item);
        }

        //Check for cycles
        classTable.forEach((name,classItem) -> {
            checkForCycle(classItem, new HashSet<>());
        });
    }

    private void checkForCycle(ClassItem current, HashSet<String> visited){
        if(visited.contains(current.getName()))
            reportError("Cycle in declaration of class " + current.getName(), current.getLine(), current.getColumn());
        else {
            //Stop if the class is a class defined by the language itself (like Object)
            if(LanguageSpecs.isDefaultClass(current.getName()))
                return;

            visited.add(current.getName());
            checkForCycle(this.classTable.get(current.getParentName()), visited);
        }
    }

    private void reportError(String message, int line, int column){
        this.errors.add(new SemanticError(message, line, column));
    }

    public boolean hasError(){
        return errors.size() > 0;
    }

    public ArrayList<SemanticError> getErrors() {
        return errors;
    }
}
