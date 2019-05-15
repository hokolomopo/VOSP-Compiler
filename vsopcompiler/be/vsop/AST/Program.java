package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.semantic.LLVMTypes;
import be.vsop.semantic.LlvmWrappers;
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

            declarations.append(str.getLlvmDeclaration());
            i++;
        }

        return declarations.toString();
    }

    private String createMain(){
        String llvm = "";

        //Begin main function
        llvm += "define " + VSOPTypes.INT32.getLlvmName() + " @main () { " + endLine;

        //Create a new Main class
        String mainTypeName = "Main";
        ExprEval newMain = new New(new Type(mainTypeName)).evalExpr(new InstrCounter());

        //Call main function of Main class
        String result = "%returned";
        String mainFuncName = "@Main.main";
        ArrayList<String> argumentsIds = new ArrayList<>();
        argumentsIds.add(newMain.llvmId);
        ArrayList<String> argumentsTypes = new ArrayList<>();
        argumentsTypes.add(VSOPTypes.getLlvmTypeName(mainTypeName, true));
        llvm += newMain.llvmCode + LlvmWrappers.call(result, LLVMTypes.INT32.getLlvmName(), mainFuncName,
                argumentsIds, argumentsTypes);

        //Return the value returned by the main function
        llvm += "ret " + VSOPTypes.INT32.getLlvmName() + " " + result + endLine + "}";

        return llvm;
    }
}
