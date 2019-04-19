package be.vsop.AST;

import be.vsop.codegenutil.InstrCounter;

import java.util.ArrayList;

public class Program extends ASTNode{
    private ArrayList<ClassList> classLists;

    public Program(ClassList classList) {
        this.classLists = new ArrayList<>();
        this.classLists.add(classList);

        this.children = new ArrayList<>(classLists);
    }

    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        for(ClassList c : classLists) {
            c.print(0, false, withTypes);
            System.out.println();
        }
    }

    @Override
    public String getLlvm(InstrCounter counter) {
        ArrayList<LiteralString> stringsLiteral = new ArrayList<>();
        this.getStringLiteral(stringsLiteral);

        String strDeclarations = llvmDeclareStrings(stringsLiteral);

        return strDeclarations + endLine + super.getLlvm(counter);
    }

    private String llvmDeclareStrings(ArrayList<LiteralString> literalStrings){
        StringBuilder declarations = new StringBuilder();
        int i = 0;
        for(LiteralString str : literalStrings){
            str.setStringId(i);

            declarations.append(str.getStringId()).append(" = ");
            declarations.append("private unnamed_addr constant ");
            declarations.append(String.format("[%d : i8] c\"%s\\00\"", str.getRawValue().length() + 1, str.getRawValue()));
            declarations.append(endLine);
            i++;
        }

        return declarations.toString();
    }
}
