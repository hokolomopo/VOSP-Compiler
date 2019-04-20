package be.vsop.AST;

import be.vsop.codegenutil.InstrCounter;
import be.vsop.semantic.LanguageSpecs;
import be.vsop.semantic.VSOPTypes;

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

        //Declare the String constants
        String llvm = llvmDeclareStrings(stringsLiteral) + endLine + endLine;

        //Create a main function
        llvm += createMain() + endLine;

        return llvm + endLine + super.getLlvm(counter);
    }

    private String llvmDeclareStrings(ArrayList<LiteralString> literalStrings){
        StringBuilder declarations = new StringBuilder();
        int i = 0;
        for(LiteralString str : literalStrings){
            str.setStringId(i);

            declarations.append(str.getLlvmDelaraction());
            i++;
        }

        return declarations.toString();
    }

    private String createMain(){
        String llvm = "";

        //Begin main function
        llvm += "define " + VSOPTypes.INT32.getLlvmName() + " @main () { " + endLine;

        //Create a new Main class
        Formal main = new Formal(new Id("Main"), new Type("Main"));
        Formal mainRet = new Formal(new Id("returned"), new Type(VSOPTypes.INT32.getName()));

        llvm += main.llvmAllocate();
        //TODO clean call
        llvm += mainRet.getLlvmId() + " = call i32 @Main.main(" + main.getType().getLlvmPtr() + " " + main.getLlvmPtr() + ")" + endLine;

        llvm += "ret " + VSOPTypes.INT32.getLlvmName() + " " + mainRet.getLlvmId() + endLine;

        llvm += "}" + endLine;
        return llvm;
    }
}
