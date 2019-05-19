package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
import be.vsop.exceptions.semantic.VariableAlreadyDeclaredException;
import be.vsop.semantic.ScopeTable;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

/**
 * This class represents a VSOP class field
 */
public class Field extends ASTNode {
    private Id id;
    private Type type;
    private Expr initExpr;

    /**
     * Creates a new Field with the given id and type, without initialisation expression
     *
     * @param id the name of the field
     * @param type the type of the field
     */
    public Field(Id id, Type type) {
        this(id, type, null);
    }

    /**
     * Creates a new Field with the given id, type and initialisation expression.
     *
     * @param id the name of the field
     * @param type the type of the field
     * @param initExpr the value to assign to the field when creating a new instance of the class
     */
    public Field(Id id, Type type, Expr initExpr) {
        this.id = id;
        this.type = type;
        this.initExpr = initExpr;

        this.children = new ArrayList<>();
        this.children.add(id);
        this.children.add(type);

        if(initExpr != null)
            this.children.add(initExpr);

    }

    /**
     * See ASTNode
     */
    @Override
    public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
        this.scopeTable = scopeTable;

        // We need to check here if the variable is already defined in local scope, even if we will
        // later do the same check with the outer scope, because we can't do a lookup in local scope after
        // having added the Field : the Field would find itself.
        Formal previousDeclaration = scopeTable.lookupVariable(id.getName(), ScopeTable.Scope.LOCAL);
        if(previousDeclaration != null) {
            errorList.add(new VariableAlreadyDeclaredException(id.getName(), line, column,
                    previousDeclaration.line, previousDeclaration.column));
        } else {
            Formal newDeclaration = new Formal(id, type);
            newDeclaration.line = line;
            newDeclaration.column = column;
            newDeclaration.toClassField();
            this.scopeTable.addVariable(newDeclaration);
        }

        id.fillScopeTable(scopeTable, errorList);
        type.fillScopeTable(scopeTable, errorList);
        if (initExpr != null) {
            initExpr.fillScopeTable(new ScopeTable(), errorList);
        }
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        if (initExpr != null && initExpr.typeName != null && isNotChild(initExpr.typeName, type.getName())) {
            errorList.add(new TypeNotExpectedException(initExpr, type.getName()));
        }
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkScope(ArrayList<SemanticException> errorList) {
        Formal previousDeclaration = scopeTable.lookupVariable(id.getName(), ScopeTable.Scope.OUTER);
        if (previousDeclaration != null) {
            errorList.add(new VariableAlreadyDeclaredException(id.getName(), line, column,
                    previousDeclaration.line, previousDeclaration.column));
        }
        super.checkScope(errorList);
    }

    /**
     * See ASTNode, a Field is printed as Field(id, type) or Field(id, type, initExpr) depending on
     * whether or not there is an expression for initialisation
     */
    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        if(doTab)
            System.out.print(getTab(tabLevel));
        System.out.print("Field(" + id.getName() + ", ");

        type.print(tabLevel, false, withTypes);
        if (initExpr != null) {
            System.out.println(", ");
            initExpr.print(tabLevel + 1, true, withTypes);
        }
        System.out.print(")");
    }

    /**
     * Getter for the type of this Field
     *
     * @return the Type object of this field
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns an ExprEval initialising this field
     *
     * @param counter the InstCounter
     *
     * @return the ExprEval containing the llvm id of the result and the llvm code initialising the field
     */
    ExprEval getInitLlvm(InstrCounter counter) {
        if (initExpr == null) {
            return new ExprEval(VSOPTypes.getLlvmDefaultInit(type.getName()), "");
        }
        return initExpr.evalExpr(counter, type.getName());
    }

    /**
     * Returns a Formal representing this field
     *
     * @return the formal
     */
    public Formal getFormal(){
        return new Formal(id, type);
    }
}