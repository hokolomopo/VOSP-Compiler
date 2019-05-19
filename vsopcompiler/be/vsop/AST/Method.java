package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.*;
import be.vsop.semantic.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents a VSOP method definition
 */
public class Method extends ASTNode {
    private Id id;
    private FormalList formals;
    private Type retType;
    private ExprList block;

    //Fields for llvm generation
    private int llvmNumber = -1;
    private Method overriddenMethod = null;

    /**
     * Creates a new Method from the given name, arguments, return type and instructions block
     *
     * @param id the name of the Method
     * @param formals the arguments
     * @param retType the return type
     * @param block the instructions block
     */
    public Method(Id id, FormalList formals, Type retType, ExprList block) {
        this.scopeTable = new ScopeTable();
        this.id = id;
        this.formals = formals;
        this.retType = retType;
        this.block = block;

        this.children = new ArrayList<>();
        this.children.add(id);
        this.children.add(formals);
        this.children.add(retType);
        this.children.add(block);
    }

    /**
     * See ASTNode
     */
    @Override
    public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
        this.scopeTable.setParent(scopeTable);

        // If two methods are defined in different scopes, it may not yet be added in the tables,
        // thus we only check local scope for now. Moreover, it has to be done to avoid comparing a
        // Method with itself later (in the check scope pass).
        Method previousDeclaration = scopeTable.lookupMethod(getName(), ScopeTable.Scope.LOCAL);
        if (previousDeclaration != null) {
            errorList.add(new MethodAlreadyDeclaredException(getName(),
                    line, column, previousDeclaration.line, previousDeclaration.column));
        } else {
            scopeTable.addMethod(this);
        }
        Formal curParam;
        for (int i = 0; i < formals.size(); i++) {
            curParam = formals.get(i);

            // in VSOP we can never define or replace the self variable (here we print an error stating self is
            // declared at line and column 0, which is to indicate that it is defined out of the file)
            if (curParam.getName().equals("self")) {
                errorList.add(new VariableAlreadyDeclaredException("self", curParam.line, curParam.column, 0, 0));
            }
        }

        if(children != null)
            for(ASTNode node : children)
                node.fillScopeTable(this.scopeTable, errorList);
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        // the typeName of the block will be null is there is already a type error in this block, we don't want to
        // generate too much errors that would be irrelevant
        if (block.typeName != null && isNotChild(block.typeName, retType.getName())) {
            errorList.add(new TypeNotExpectedException(block, retType.getName()));
        }
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkScope(ArrayList<SemanticException> errorList){
        // getParent() because the scopeTable of this object is a fresh one containing local variables, its parent
        // is the scopeTable of the VSOP class implementing the method.
        // outer is used to avoid getting the current method through the scope table.
        Method previousDeclaration = scopeTable.getParent().lookupMethod(getName(), ScopeTable.Scope.OUTER);
        if (previousDeclaration != null) {

            // Overriding in VSOP has to respect some rules. The overriding function should have the same number
            // of arguments and they should all have the same type. Its return type must also be the same
            if (previousDeclaration.formals.size() != this.formals.size()) {
                errorList.add(new InvalidOverrideException(getName(),
                        line, column, previousDeclaration.line, previousDeclaration.column, "different number of arguments"));

            } else {

                // The overriding function has the same number of arguments than the previous one. Now we need to check
                // whether the types match
                StringBuilder messageEnd = new StringBuilder("argument(s) ");
                boolean invalid = false;
                for (int i = 0; i < formals.size(); i++) {
                    String shouldBeChild = formals.get(i).getType().getName();
                    String shouldBeParent = previousDeclaration.formals.get(i).getType().getName();

                    // The arguments types does not need to equal, we can put a child instead of a parent
                    if (isNotChild(shouldBeChild, shouldBeParent)) {
                        invalid = true;
                        messageEnd.append((i+1)).append(", ");
                    }
                }

                // The two functions should have the same return type
                if (!retType.getName().equals(previousDeclaration.retType.getName())) {
                    invalid = true;
                    messageEnd.append("return, ");
                }

                // Invalid is true if at least one error was found
                if (invalid) {
                    messageEnd.setLength(messageEnd.length() - 2);
                    messageEnd.append(" differ(s) in type");
                    errorList.add(new InvalidOverrideException(getName(),
                            line, column, previousDeclaration.line, previousDeclaration.column, messageEnd.toString()));
                }
            }
        }

        formals.checkAllDifferent(errorList);
        super.checkScope(errorList);
    }

    /**
     * See ASTNode, a Method is printed as Method(formals, retType, block)
     */
    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        if(doTab)
            System.out.print(getTab(tabLevel));

        System.out.print("Method(" + id.getName() + ", ");
        formals.print(tabLevel, false, withTypes);
        System.out.print(", ");
        retType.print(tabLevel, false, withTypes);
        System.out.print(", ");
        System.out.println();
        block.print(tabLevel + 1, true, withTypes);
        System.out.print(")");
    }

    /**
     * Getter for the name of the method
     *
     * @return the name
     */
    public String getName() {
        return id.getName();
    }

    /**
     * Getter for the number of arguments of the method
     *
     * @return the number of arguments
     */
    int nbArguments() {
        return formals.size();
    }

    /**
     * Returns the index'th argument of the method
     *
     * @param index the index of the argument to return
     *
     * @return the index'th argument
     */
    Formal getArgument(int index) {
        return formals.get(index);
    }

    /**
     * Getter for the return type of the function
     *
     * @return the return type
     */
    String returnType() {
        return retType.getName();
    }

    /**
     * See ASTNode
     */
    @Override
    public void prepareForLlvm() {
        //Add self to formals
        Formal self = new Formal(new Id("self"), new Type(scopeTable.getScopeClassType().getName()));
        formals.addFormalAtBeginning(self);

        super.prepareForLlvm();
    }

    /**
     * See ASTNode
     */
    @Override
    public String getLlvm(InstrCounter counter) {
        //The body of the function is a new scope, it needs a new counter
        InstrCounter bodyCounter = new InstrCounter();

        //Method header
        String llvm =  LLVMKeywords.DEFINE.getLlvmName() + " " + retType.getLlvmName() + " " +
                LlvmWrappers.getMethodName(id.getName(), this.getParentClassName()) +
                "(";
        if(formals.size() > 0) {
            llvm +=  formals.getLlvm(counter);
        }
        llvm += ") {\n";

        //Method body
        //First check that self is not null
        String isNullId = bodyCounter.getNextLlvmId();
        HashMap<String, String> condLabels = bodyCounter.getNextCondLabels();
        llvm += LlvmWrappers.binOp(isNullId, "null", "%" + LanguageSpecs.SELF, LLVMKeywords.EQ,
                VSOPTypes.getLlvmTypeName(scopeTable.getScopeClassType().getName()));
        llvm += LlvmWrappers.branch(isNullId, condLabels.get(InstrCounter.COND_IF_LABEL),
                condLabels.get(InstrCounter.COND_ELSE_LABEL));

        //Will be executed if self is null
        //TODO in fact this is never executed because of the addition of dynamic dispatch. We instead get a segFault
        // when accessing the vTable (before). This code should then be put somewhere else and modified a bit.
        llvm += LlvmWrappers.label(condLabels.get(InstrCounter.COND_IF_LABEL));
        String stringErrorContent = "Segmentation fault : dispatch on null when calling function " + id.getName() +
                " on class " + scopeTable.getScopeClassType().getName();
        llvm += LlvmWrappers.printErrorString(bodyCounter, stringErrorContent, true);
        llvm += LlvmWrappers.exit(-1);
        llvm += LlvmWrappers.returnDefault(retType);

        //Will be executed if self is not null
        llvm += LlvmWrappers.label(condLabels.get(InstrCounter.COND_ELSE_LABEL));

        //Allocate and store all arguments into pointers
        llvm += formals.llvmAllocate();
        llvm += formals.llvmStore(counter);

        ExprEval bodyEval = block.evalExpr(bodyCounter, retType.getName());
        llvm += bodyEval.llvmCode;

        llvm += "ret " + VSOPTypes.getLlvmTypeName(retType.getName()) + " " + bodyEval.llvmId + " " + endLine + "}";

        return llvm;
    }

    /**
     * Creates the llvm code corresponding to the signature of the method
     *
     * @param pointer true if we want a pointer on the method (which looks much like its signature)
     *
     * @return the signature
     */
    String getLlvmSignature(boolean pointer){
        StringBuilder llvm = new StringBuilder();

        // In llvm, the signature of a method is <retType> (<arg1Type>, <arg2Type>,...)
        llvm.append(retType.getLlvmName()).append(" (");

        for(int i = 0;i < formals.size();i++){
            Formal f = formals.get(i);
            llvm.append(f.getType().getLlvmName());

            if(i != formals.size() - 1)
                llvm.append(", ");
        }

        llvm.append(")");

        if(pointer)
            llvm.append("*");

        return llvm.toString();
    }

    /**
     * Getter for the name of the class implementing this method
     *
     * @return the name of the parent class
     */
    String getParentClassName(){
        return scopeTable.getScopeClassType().getName();
    }

    /**
     * Getter for the number of this function (in the order of the vTable, useful for calls)
     *
     * @return the number
     */
    public int getLlvmNumber() {
        return llvmNumber;
    }

    /**
     * Tells to this method its location in the order of the vTable, useful for calls
     *
     * @param llvmNumber the position of the method
     */
    public void setLlvmNumber(int llvmNumber) {
        this.llvmNumber = llvmNumber;
    }

    /**
     * Returns the llvm code corresponding to the name of this function
     *
     * @return the llvm code
     */
    public String getLlvmName(){
        return LlvmWrappers.getMethodName(id.getName(), getParentClassName());
    }

    /**
     * Get a formal representing the method that can be loaded from the vTable
     *
     * @param vTableName the name of the vTable
     *
     * @return the formal
     */
    private Formal getMethodFormal(String vTableName){
        Formal methodFormal = new Formal(getLlvmName(), getLlvmSignature(false));
        methodFormal.toClassField();
        methodFormal.setClassFieldId(this.llvmNumber);
        methodFormal.setParentClass(vTableName);

        return methodFormal;
    }

    /**
     * Store this method in the given vTable
     *
     * @param vTable the vTable
     * @param vTableId the llvm register where is the vTable
     * @param counter an InstrCounter
     * @return the llvm code that store the method in the vTable
     */
    String storeInVTable(Formal vTable, String vTableId, InstrCounter counter) {
        String llvm = "";

        Formal methodFormal = getMethodFormal(vTable.getType().getName());

        llvm += methodFormal.llvmStore(getLlvmName(), vTableId, counter);

        return llvm;
    }

    /**
     * Load a method from the vTable
     *
     * @param parentClassId the llvm id of the class of the parent
     * @param counter an InstrCounter
     * @return the eval loading the method
     */
    ExprEval loadMethod(String parentClassId, InstrCounter counter){
        Formal vTable = classTable.get(getParentClassName()).getVTable();
        Formal methodFormal = getMethodFormal(vTable.getType().getName());

        ExprEval loadVTable =  vTable.llvmLoad(parentClassId, counter);
        ExprEval loadMethod = methodFormal.llvmLoad(loadVTable.llvmId, counter);


        return new ExprEval(loadMethod.llvmId, loadVTable.llvmCode + loadMethod.llvmCode);
    }

    /**
     * Tells to this method that it overrides another method (useful for updating accordingly the vTable)
     *
     * @param overriddenMethod the overridden (not overriding) method
     */
    public void setOverriddenMethod(Method overriddenMethod) {
        this.overriddenMethod = overriddenMethod;
    }

    /**
     * Whether this methods overrides another method or is the first in the hierarchy
     *
     * @return true if this method is an overriding, false otherwise
     */
    public boolean isOverride(){
        return this.overriddenMethod != null;
    }
}