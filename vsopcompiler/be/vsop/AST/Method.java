package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.*;
import be.vsop.semantic.LLVMKeywords;
import be.vsop.semantic.ScopeTable;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

public class Method extends ASTNode {
    private Id id;
    private FormalList formals;
    private Type retType;
    private ExprList block;

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
    public String getLlvm(InstrCounter counter) {
        //Add self to formals
        Formal self = new Formal(new Id("self"), new Type(scopeTable.getScopeClassType().getName()));
        formals.addFormal(self, 0);

        //Method header
        String llvm =  LLVMKeywords.DEFINE.getLlvmName() + " " + retType.getLlvmName(true) +
                " @" + scopeTable.getScopeClassType().getName() + "." + id.getName() +
                "(";
        if(formals.getLength() > 0) {
            llvm +=  formals.getLlvm(counter);
        }
        llvm += ") {\n";

        //Method body

        //Allocate and store all arguments into pointers
        llvm += formals.llvmAllocate();
        llvm += formals.llvmStore(counter);

        ExprEval bodyEval = block.evalExpr(new InstrCounter());
        llvm += bodyEval.llvmCode;

        llvm += "ret " + VSOPTypes.getLlvmTypeName(block.typeName, true) + " " + bodyEval.llvmId + " " + endLine + "}";

        return llvm;
    }
}