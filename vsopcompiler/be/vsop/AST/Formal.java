package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.LanguageSpecs;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;
import java.util.Objects;

public class Formal extends ASTNode{
    private Id id;
    private Type type;

    // Set to true if the formal is a field of a class
    private boolean isClassField = false;
    private int classFieldId = -1;//Index in the llvm structure
    private String parentClass;

    //For formals with same name, we add a number to uniquely identify them
    private int number = 1;

    String llvmId = null;

    public Formal(Id id, Type type) {
        this.id = id;
        this.type = type;

        this.children = new ArrayList<>();
        this.children.add(id);
        this.children.add(type);
    }

    public Formal(String id, String typeName){
        this(new Id(id), new Type(typeName));
    }

    public Formal(Formal formal){
        this.id = formal.id;
        this.type = formal.type;

        this.children = formal.children;
        this.isClassField = formal.isClassField;
        this.parentClass = formal.parentClass;
    }

    /**
     * See ASTNode
     */
    @Override
    public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
        this.scopeTable = scopeTable;
        scopeTable.addVariable(this);
        this.buildLlvmId();
        if(children != null)
            for(ASTNode node : children)
                node.fillScopeTable(scopeTable, errorList);
    }

    /**
     * See ASTNode, a Formal is printed as id : type
     */
    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        if(doTab)
            System.out.print(getTab(tabLevel));
        System.out.print(id.getName() + ":");
        type.print(tabLevel, false, withTypes);
    }

    public String getName() {
        return id.getName();
    }

    public Type getType() {
        return type;
    }

    private void buildLlvmId(){
        Formal twin = scopeTable.lookupVariable(id.getName(), ScopeTable.Scope.OUTER);
        if(twin != null && !twin.isClassField())
            number = twin.getNumber() + 1;
    }

    public String getLlvmId(){
        if(number > 1)
            return "%" + id.getName() + number;
        return "%" + id.getName();
    }

    public void setLlvmId(String llvmId) {
        this.llvmId = llvmId;
    }

    public String getLlvmPtr(){
        if(llvmId != null)
            return llvmId;

        return getLlvmId()  + ".ptr";
    }

    public int getNumber() {
        return number;
    }

    /**
     * See ASTNode
     */
    @Override
    public String getLlvm(InstrCounter counter) {
        return type.getLlvmName(true) + " " + getLlvmId();
    }

    public boolean isClassField() {
        return isClassField;
    }

    public void setClassField(boolean classField) {
        isClassField = classField;
    }

    /**
     * Return the llvm code to allocate this formal
     *
     * @return the llvm code
     */
    public String llvmAllocate(){
        return String.format("%s = alloca %s \n", getLlvmPtr(), type.getLlvmName(true));
    }

    /**
     * Get the llvm code to load this formal
     *
     * @param parentClassId the llvm id of the parent of the formal
     * @param counter an InstrCounter
     * @return the evaluation of the load
     */
    public ExprEval llvmLoad(String parentClassId, InstrCounter counter){
        String llvm = "", id;

        if(isClassField){
            ExprEval getFieldPtrEval = getFieldPtr(parentClassId, counter);
            id = counter.getNextLlvmId();
            llvm += getFieldPtrEval.llvmCode + String.format("%s = load %s, %s %s \n", id, type.getLlvmName(true), type.getLlvmPtr(true), getFieldPtrEval.llvmId);
            return new ExprEval(id, llvm);
        }
        id = counter.getNextLlvmId();
        llvm = String.format("%s = load %s, %s %s \n", id, type.getLlvmName(true), type.getLlvmPtr(true), getLlvmPtr());
        return new ExprEval(id, llvm);
    }

    /**
     * Get the llvm code to load this formal
     *
     * @param counter an InstrCounter
     * @return the evaluation of the load
     */
    public ExprEval llvmLoad(InstrCounter counter){
        return llvmLoad("%" + LanguageSpecs.SELF, counter);
    }

    /**
     * Get a pointer to the field, used if the formal is a class field
     *
     * @param parentClassId the llvm id of the parent of the formal
     * @param counter an InstrCounter
     * @return the code to get the pointer and the pointer llvm id
     */
    private ExprEval getFieldPtr(String parentClassId, InstrCounter counter){
        String id = counter.getNextLlvmId();
        String llvm = String.format("%s = getelementptr %s, %s* %s, i32 0, i32 %d \n", id, parentClass, parentClass, parentClassId, classFieldId);

        return new ExprEval(id, llvm);
    }

    /**
     * Get the llvm code to store the formal
     *
     * @param toStore the llvm id of the register to store
     * @param parentClassId the llvm id of the parent of the formal
     * @param counter an InstrCounter
     * @return the code to store the formal
     */
    public String llvmStore(String toStore, String parentClassId, InstrCounter counter){
        if(isClassField){
            ExprEval eval = getFieldPtr(parentClassId, counter);
            String llvm = eval.llvmCode;
            llvm += String.format("store %s %s, %s %s \n", type.getLlvmName(true), toStore, type.getLlvmPtr(true), eval.llvmId);
            return llvm;
        }
        return String.format("store %s %s, %s %s \n", type.getLlvmName(true), toStore, type.getLlvmPtr(true), getLlvmPtr());
    }

    /**
     * Get the llvm code to store the formal
     *
     * @param toStore the llvm id of the register to store
     * @param counter an InstrCounter
     * @return the code to store the formal
     */
    public String llvmStore(String toStore, InstrCounter counter){
        return llvmStore(toStore, "%" + LanguageSpecs.SELF, counter);
    }


    public boolean isPointer(){
        return type.isPointer();
    }

    public void setClassFieldId(int classFieldId) {
        this.classFieldId = classFieldId;
    }


    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Formal formal = (Formal) o;
        return this.id.getName().equals(((Formal) o).id.getName())
                && this.type.getName().equals(formal.type.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}