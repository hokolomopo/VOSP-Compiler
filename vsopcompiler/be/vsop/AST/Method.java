package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.*;
import be.vsop.semantic.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Method extends ASTNode {
    private Id id;
    private FormalList formals;
    private Type retType;
    private ExprList block;

    //Fields for llvm generation
    private int llvmNumber = -1;
    private Method overriddenMethod = null;

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

    @Override
    public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
        this.scopeTable.setParent(scopeTable);
        // If two methods are defined in different scopes, it may not yet be added in the tables,
        // thus we only check local scope for now.
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
            if (curParam.getName().equals("self")) {
                errorList.add(new VariableAlreadyDeclaredException("self", curParam.line, curParam.column, 0, 0));
            }
        }
        if(children != null)
            for(ASTNode node : children)
                node.fillScopeTable(this.scopeTable, errorList);
    }

    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        if (block.typeName != null && isNotChild(block.typeName, retType.getName())) {
            errorList.add(new TypeNotExpectedException(block, retType.getName()));
        }
    }

    @Override
    public void checkScope(ArrayList<SemanticException> errorList){
        // getParent() because the scopeTable of this object is a fresh one containing local variables, its parent
        // is the scopeTable of the VSOP class implementing the method.
        // outer is used to avoid getting the current method through the scope table.
        Method previousDeclaration = scopeTable.getParent().lookupMethod(getName(), ScopeTable.Scope.OUTER);
        if (previousDeclaration != null) {
            if (previousDeclaration.formals.size() != this.formals.size()) {
                errorList.add(new InvalidOverrideException(getName(),
                        line, column, previousDeclaration.line, previousDeclaration.column, "different number of arguments"));
            } else {
                StringBuilder messageEnd = new StringBuilder("argument(s) ");
                boolean invalid = false;
                for (int i = 0; i < formals.size(); i++) {
                    String shouldBeChild = formals.get(i).getType().getName();
                    String shouldBeParent = previousDeclaration.formals.get(i).getType().getName();
                    if (isNotChild(shouldBeChild, shouldBeParent)) {
                        invalid = true;
                        messageEnd.append((i+1)).append(", ");
                    }
                }
                if (!retType.getName().equals(previousDeclaration.retType.getName())) {
                    invalid = true;
                    messageEnd.append("return, ");
                }
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

    public String getName() {
        return id.getName();
    }

    int nbArguments() {
        return formals.size();
    }

    Formal getArgument(int index) {
        return formals.get(index);
    }

    String returnType() {
        return retType.getName();
    }

    @Override
    public void prepareForLlvm() {
        //Add self to formals
        Formal self = new Formal(new Id("self"), new Type(scopeTable.getScopeClassType().getName()));
        formals.addFormal(self, 0);

        super.prepareForLlvm();
    }

    @Override
    public String getLlvm(InstrCounter counter) {
        //The body of the function is a new scope, it needs a new counter
        InstrCounter bodyCounter = new InstrCounter();

        //Method header
        String llvm =  LLVMKeywords.DEFINE.getLlvmName() + " " + retType.getLlvmName(true) + " " +
                LlvmWrappers.getMethodName(id.getName(), this.getParentClassName()) +
                "(";
        if(formals.getLength() > 0) {
            llvm +=  formals.getLlvm(counter);
        }
        llvm += ") {\n";

        //Method body
        //First check that self is not null
        String isNullId = bodyCounter.getNextLlvmId();
        HashMap<String, String> condLabels = bodyCounter.getNextCondLabels();
        llvm += LlvmWrappers.binOp(isNullId, "null", "%" + LanguageSpecs.SELF, LLVMKeywords.EQ,
                VSOPTypes.getLlvmTypeName(scopeTable.getScopeClassType().getName(), true));
        llvm += LlvmWrappers.branch(isNullId, condLabels.get(InstrCounter.COND_IF_LABEL),
                condLabels.get(InstrCounter.COND_ELSE_LABEL));

        //Will be executed if self is null
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

        llvm += "ret " + VSOPTypes.getLlvmTypeName(retType.getName(), true) + " " + bodyEval.llvmId + " " + endLine + "}";

        return llvm;
    }

    public String getLlvmSignature(boolean pointer){
        StringBuilder llvm = new StringBuilder();

        llvm.append(retType.getLlvmName(true)).append(" (");

        for(int i = 0;i < formals.getLength();i++){
            Formal f = formals.get(i);

            llvm.append(f.getType().getLlvmName(true));

            if(i != formals.getLength() - 1)
                llvm.append(", ");
        }

        llvm.append(")");

        if(pointer)
            llvm.append("*");

        return llvm.toString();
    }

    public String getParentClassName(){
        return scopeTable.getScopeClassType().getName();
    }

    public int getLlvmNumber() {
        return llvmNumber;
    }

    public void setLlvmNumber(int llvmNumber) {
        this.llvmNumber = llvmNumber;
    }

    public String getLlvmName(){
        return LlvmWrappers.getMethodName(id.getName(), getParentClassName());
    }

    private Formal getMethodFormal(String vtableName){
        Formal methodFormal = new Formal(getLlvmName(), getLlvmSignature(false));
        methodFormal.setClassField(true);
        methodFormal.setClassFieldId(this.llvmNumber);
        methodFormal.setParentClass(vtableName);

        return methodFormal;
    }

    public String storeInVtable(Formal vtable, String vtableId, InstrCounter counter) {
        String llvm = "";

//        //Method is an override of another, we need to cast the first argument (self) to correspond to the original type
//        if(isOverride()){
//
//        }

        Formal methodFormal = getMethodFormal(vtable.getType().getName());

        llvm += methodFormal.llvmStore(getLlvmName(), vtableId, counter);
        ;
        return llvm;
    }

    public ExprEval loadMethod(String parentClassId, InstrCounter counter){
        Formal vtable = classTable.get(getParentClassName()).getVtable();
        Formal methodFormal = getMethodFormal(vtable.getType().getName());

        ExprEval loadVtable =  vtable.llvmLoad(parentClassId, counter);
        ExprEval loadMethod = methodFormal.llvmLoad(loadVtable.llvmId, counter);


        return new ExprEval(loadMethod.llvmId, loadVtable.llvmCode + loadMethod.llvmCode);
    }

    public void setOverriddenMethod(Method overriddenMethod) {
        this.overriddenMethod = overriddenMethod;
    }

    public boolean isOverride(){
        return this.overriddenMethod != null;
    }

    private Method getOriginalMethod(){
        Method current = this;

        while (current.overriddenMethod != null)
            current = current.overriddenMethod;

        return current;
    }
}