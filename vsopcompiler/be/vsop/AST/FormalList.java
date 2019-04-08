package be.vsop.AST;

import be.vsop.exceptions.semantic.VariableAlreadyDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;

import java.util.ArrayList;
import java.util.HashSet;

public class FormalList extends ASTNode{
    private ArrayList<Formal> formals;

    public FormalList(FormalList fl, Formal f) {
        this.formals = fl.formals;
        this.formals.add(f);

        this.children = new ArrayList<>(this.formals);
    }

    public FormalList() {
        formals = new ArrayList<>();
    }

    @Override
    public void print(int tabLevel, boolean doTab) {
        if(doTab)
            System.out.print(getTab(tabLevel));
        System.out.print("[");


        int i;
        if (formals.size() > 0) {
            for (i = 0; i < formals.size() - 1; i++) {
                formals.get(i).print(tabLevel, false);
                System.out.print(",");
            }
            formals.get(i).print(tabLevel, false);
        }

        System.out.print("]");
    }

    void checkAllDifferent(ArrayList<SemanticException> errorList) {
        Formal formal;
        Formal other;
        HashSet<Integer> toIgnore = new HashSet<>();
        for (int i = 0; i < formals.size(); i++) {
            if (! toIgnore.contains(i)) {
                formal = formals.get(i);
                for (int j = i + 1; j < formals.size(); j++) {
                    other = formals.get(j);
                    if (formal.getName().equals(other.getName())) {
                        errorList.add(new VariableAlreadyDeclaredException(formal.getName(),
                                other.line, other.column, formal.line, formal.column));
                        toIgnore.add(j);
                    }
                }
            }
        }
    }

    int size() {
        return formals.size();
    }

    Formal get(int index) {
        return formals.get(index);
    }
}