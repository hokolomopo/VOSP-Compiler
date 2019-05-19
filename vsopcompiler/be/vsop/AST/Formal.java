package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.LanguageSpecs;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This class represents a VSOP Formal, which is not more than a name and a type
 */
public class Formal extends ASTNode{
    private Id id;
    private Type type;

    // Set to true if the formal is a field of a class
    private boolean isClassField = false;
    private int classFieldId = -1;//Index in the llvm structure (used for the class fields)
    private String parentClass;

    //For formals with same name, we add a number to uniquely identify them
    private int number = 1;

    String llvmId = null;

    /**
     * Creates a new formal with the given id and type
     *
     * @param id the name of the formal
     * @param type the type of the formal
     */
    public Formal(Id id, Type type) {
        this.id = id;
        this.type = type;

        this.children = new ArrayList<>();
        this.children.add(id);
        this.children.add(type);
    }

    /**
     * Creates a new formal with the given id and type, convenience constructor equivalent to
     * Formal(new Id(id), new Type(typeName))
     *
     * @param id the name of the formal
     * @param typeName the name of the type of the formal
     */
    public Formal(String id, String typeName){
        this(new Id(id), new Type(typeName));
    }

    /**
     * Copy constructor
     *
     * @param formal the formal to copy
     */
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
        this.buildNumber();
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

    /**
     * Getter for the name of this formal
     *
     * @return the name
     */
    public String getName() {
        return id.getName();
    }

    /**
     * Getter for the Type of this formal
     *
     * @return the Type
     */
    public Type getType() {
        return type;
    }

    /**
     * Computes and store (in an instance variable) the number of this Formal, used to differentiate
     * variables with the same name in different scopes (but whose scopes are linked by a parent relationship)
     */
    private void buildNumber(){
        Formal twin = scopeTable.lookupVariable(id.getName(), ScopeTable.Scope.OUTER);
        if(twin != null && !twin.isClassField())
            number = twin.getNumber() + 1;
    }

    /**
     * Returns the llvm id (variable containing the value) of this Formal
     *
     * @return the llvm id
     */
    public String getLlvmId(){
        if(number > 1)
            return "%" + id.getName() + number;
        return "%" + id.getName();
    }

    /**
     * Set the llvm id (variable containing the value) of this Formal
     *
     * @param llvmId the new llvm id
     */
    public void setLlvmId(String llvmId) {
        this.llvmId = llvmId;
    }

    /**
     * Returns the llvm id of the pointer pointing to this formal
     *
     * @return the llvm id of the pointer
     */
    private String getLlvmPtr(){
        if(llvmId != null)
            return llvmId;

        return getLlvmId()  + ".ptr";
    }

    /**
     * Returns the number used to differentiate variable with the same name
     *
     * @return the number of this Formal
     */
    public int getNumber() {
        return number;
    }

    /**
     * See ASTNode
     */
    @Override
    public String getLlvm(InstrCounter counter) {
        return type.getLlvmName() + " " + getLlvmId();
    }

    /**
     * Whether this Formal is a class field or not
     *
     * @return true if this Formal is a class field, false otherwise
     */
    private boolean isClassField() {
        return isClassField;
    }

    /**
     * Tells to this formal that it is a class field
     */
    void toClassField() {
        isClassField = true;
    }

    /**
     * Return the llvm code that allocates this formal
     *
     * @return the llvm code
     */
    String llvmAllocate(){
        return String.format("%s = alloca %s \n", getLlvmPtr(), type.getLlvmName());
    }

    /**
     * Get the llvm code that loads this formal
     *
     * @param parentClassId the llvm id of the parent of the formal
     * @param counter an InstrCounter
     *
     * @return the ExprEval containing the code needed to evaluate the load and the llvm id in which the result is stored
     */
    ExprEval llvmLoad(String parentClassId, InstrCounter counter){
        String llvm = "", id;

        if(isClassField){
            ExprEval getFieldPtrEval = getFieldPtr(parentClassId, counter);
            id = counter.getNextLlvmId();
            llvm += getFieldPtrEval.llvmCode + String.format("%s = load %s, %s %s \n", id,
                    type.getLlvmName(), type.getLlvmPtr(),
                    getFieldPtrEval.llvmId);

            return new ExprEval(id, llvm);
        }
        id = counter.getNextLlvmId();
        llvm = String.format("%s = load %s, %s %s \n", id, type.getLlvmName(),
                type.getLlvmPtr(), getLlvmPtr());

        return new ExprEval(id, llvm);
    }

    /**
     * Get the llvm code that loads this formal, convenience method for loading a Formal of self
     *
     * @param counter an InstrCounter
     *
     * @return the ExprEval containing the code needed to evaluate the load and the llvm id in which the result is stored
     */
    ExprEval llvmLoad(InstrCounter counter){
        return llvmLoad("%" + LanguageSpecs.SELF, counter);
    }

    /**
     * Get a pointer to the field, used if the formal is a class field
     *
     * @param parentClassId the llvm id of the parent of the formal
     * @param counter an InstrCounter
     *
     * @return the code to get the pointer and the pointer llvm id
     */
    private ExprEval getFieldPtr(String parentClassId, InstrCounter counter){
        String id = counter.getNextLlvmId();
        String llvm = String.format("%s = getelementptr %s, %s* %s, i32 0, i32 %d \n", id, parentClass, parentClass,
                parentClassId, classFieldId);

        return new ExprEval(id, llvm);
    }

    /**
     * Get the llvm code that stores this Formal
     *
     * @param toStore the llvm id of the register to store
     * @param parentClassId the llvm id of the parent of the formal
     * @param counter an InstrCounter
     *
     * @return the code to store the formal
     */
    String llvmStore(String toStore, String parentClassId, InstrCounter counter){
        if(isClassField){
            ExprEval eval = getFieldPtr(parentClassId, counter);
            String llvm = eval.llvmCode;
            llvm += String.format("store %s %s, %s %s \n", type.getLlvmName(), toStore,
                    type.getLlvmPtr(), eval.llvmId);

            return llvm;
        }
        return String.format("store %s %s, %s %s \n", type.getLlvmName(), toStore,
                type.getLlvmPtr(), getLlvmPtr());
    }

    /**
     * Get the llvm code that stores this Formal, convenience method for storing a Formal of self
     *
     * @param toStore the llvm id of the register to store
     * @param counter an InstrCounter
     *
     * @return the code to store the formal
     */
    String llvmStore(String toStore, InstrCounter counter){
        return llvmStore(toStore, "%" + LanguageSpecs.SELF, counter);
    }

    /**
     * Tells to this Formal, representing a class field, at which position in the structure defining the class it is
     *
     * @param classFieldId the position of the formal in the class structure
     */
    void setClassFieldId(int classFieldId) {
        this.classFieldId = classFieldId;
    }

    /**
     * Tells to this Formal to which class it belongs to
     *
     * @param parentClass the name of the parent class of this Formal
     */
    void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    /**
     * Equality of 2 formals is defined as equality only between Id and Type
     * @param o the other formal to compare this object to
     *
     * @return true if this object is equal to o, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Formal formal = (Formal) o;
        return this.id.getName().equals(formal.id.getName())
                && this.type.getName().equals(formal.type.getName());
    }

    /**
     * Computes a hash code for this Formal
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}