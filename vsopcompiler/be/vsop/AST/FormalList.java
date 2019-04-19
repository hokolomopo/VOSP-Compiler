package be.vsop.AST;

import be.vsop.codegenutil.InstrCounter;
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

    public void addFormal(Formal formal, int index){
        formals.add(index, formal);
    }

    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        if(doTab)
            System.out.print(getTab(tabLevel));
        System.out.print("[");


        int i;
        if (formals.size() > 0) {
            for (i = 0; i < formals.size() - 1; i++) {
                formals.get(i).print(tabLevel, false, withTypes);
                System.out.print(",");
            }
            formals.get(i).print(tabLevel, false, withTypes);
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

    @Override
    public String getLlvm(InstrCounter counter) {
        StringBuilder builder = new StringBuilder();

        for(int i = 0;i < formals.size();i++){
            builder.append(formals.get(i).getLlvm(counter));

            if(i < formals.size() - 1)
                builder.append(", ");

        }

        return builder.toString();

    }

    public String llvmAllocate() {
        StringBuilder builder = new StringBuilder();

        for(Formal formal : formals)
            builder.append(formal.llvmAllocate());

        return builder.toString();

    }

    public String llvmStore() {
        StringBuilder builder = new StringBuilder();

        for(Formal formal : formals)
            builder.append(formal.llvmStore(formal.getLlvmId()));

        return builder.toString();

    }


    public void classesToPtr(){
        for(Formal formal : formals)
            if(!formal.isPrimitive() && !formal.isPointer())
                formal.toPointer();
    }

    public int getLength(){
        return formals.size();
    }
}