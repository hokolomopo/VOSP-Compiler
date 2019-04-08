package be.vsop.AST;

import java.util.ArrayList;

public class Program extends ASTNode{
    private ArrayList<ClassList> classLists;

    public Program(ClassList classList) {
        this.classLists = new ArrayList<>();
        this.classLists.add(classList);

        this.children = new ArrayList<>(classLists);
    }

    @Override
    public void print(int tabLevel, boolean doTab) {
        for(ClassList c : classLists) {
            c.print(0, false);
            System.out.println();
        }
    }

}
