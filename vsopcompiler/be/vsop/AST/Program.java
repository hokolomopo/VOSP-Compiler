package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.codegenutil.LLVMTypes;
import be.vsop.codegenutil.LlvmWrappers;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

/**
 * This class represents a VSOP program, which is not more than a list of files (which are themselves lists of classes)
 */
public class Program extends ASTNode{
    private ArrayList<ClassList> classLists;

    /**
     * Construct a new program with only one file (the given classList)
     *
     * @param classList the list of classes
     */
    public Program(ClassList classList) {
        this.classLists = new ArrayList<>();
        this.classLists.add(classList);

        this.children = new ArrayList<>(classLists);
    }

    /**
     * See ASTNode, a program is printed as ClassList1\nClassList2\n...ClassListN\n
     */
    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        for(ClassList c : classLists) {
            c.print(0, false, withTypes);
            System.out.println();
        }
    }

    /**
     * See ASTNode
     */
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

    /**
     * Generates the llvm code that declares all the strings present in the program (declared as llvm global constants)
     *
     * @param literalStrings the strings, may be updated
     *
     * @return the llvm code that declares the string
     */
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

    /**
     * Generates the code of the main function (that simply calls the VSOP Main.main function)
     *
     * @return the llvm code that declares the main function
     */
    private String createMain(){
        String llvm = "";

        //Begin main function
        llvm += "define " + VSOPTypes.INT32.getLlvmName() + " @main () { " + endLine;

        //Create a new Main class
        String mainTypeName = "Main";
        ExprEval newMain = new New(new Type(mainTypeName)).evalExpr(new InstrCounter(), mainTypeName);

        //Call main function of Main class
        String result = "%returned";
        String mainFuncName = "@Main.main";

        ArrayList<String> argumentsIds = new ArrayList<>();
        argumentsIds.add(newMain.llvmId);

        ArrayList<String> argumentsTypes = new ArrayList<>();
        argumentsTypes.add(VSOPTypes.getLlvmTypeName(mainTypeName));

        llvm += newMain.llvmCode + LlvmWrappers.call(result, LLVMTypes.INT32.getLlvmName(), mainFuncName,
                argumentsIds, argumentsTypes);

        //Return the value returned by the main function
        llvm += "ret " + VSOPTypes.INT32.getLlvmName() + " " + result + endLine + "}";

        return llvm;
    }
}
