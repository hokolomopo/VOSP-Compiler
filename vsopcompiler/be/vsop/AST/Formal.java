package be.vsop.AST;

import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.LanguageSpecs;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;

public class Formal extends ASTNode{
	private Id id;
	private Type type;

	// Set to true if the formal is a field of a class
	private boolean isClassField = false;
	private int classFieldId = -1;
	private String parentClass;

	//For formals with same name, we add a number to uniquely identify them
	private int number = 1;

	public Formal(Id id, Type type) {
		this.id = id;
		this.type = type;

		this.children = new ArrayList<>();
		this.children.add(id);
		this.children.add(type);
	}

	@Override
	public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
		this.scopeTable = scopeTable;
		scopeTable.addVariable(this);
		this.buildLlvmId();
		if(children != null)
			for(ASTNode node : children)
				node.fillScopeTable(scopeTable, errorList);
	}

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

	public String getLlvmPtr(){
		return getLlvmId()  + "Ptr";
	}


	public int getNumber() {
		return number;
	}

	@Override
	public String getLlvm(InstrCounter counter) {
		return type.getLlvmName() + " " + getLlvmId();
	}

	public boolean isClassField() {
		return isClassField;
	}

	public void setClassField(boolean classField) {
		isClassField = classField;
	}

	public String llvmAllocate(){
		return String.format("%s = alloca %s \n", getLlvmPtr(), type.getLlvmName());
	}

	public String llvmLoad(String loadTo, String parentClassId){
		if(isClassField){
			String llvm = getFieldPtr(parentClassId);
			llvm += String.format("%s = load %s, %s %s \n", loadTo, type.getLlvmName(), type.getLlvmPtr(), getLlvmId());
			return llvm;
		}
		return String.format("%s = load %s, %s %s \n", loadTo, type.getLlvmName(), type.getLlvmPtr(), getLlvmPtr());
	}

	public String llvmLoad(String loadTo){
		return llvmLoad(loadTo, "%" + LanguageSpecs.SELF);
	}

	private String getFieldPtr(String parentClassId){
		return String.format("%s = getelementptr %s, %s* %s, i32 0, i32 %d \n", getLlvmId(), parentClass, parentClass, parentClassId, classFieldId);//TODO : inbound? always int32? wtf is this function
	}

	public String llvmLoad(){
		return llvmLoad(getLlvmId());
	}

	public String llvmStore(String toStore, String parentClassId){
		if(isClassField){
			String llvm = getFieldPtr(parentClassId);
			llvm += String.format("store %s %s, %s %s \n", type.getLlvmName(), toStore, type.getLlvmPtr(), getLlvmId());
			return llvm;
		}
		return String.format("store %s %s, %s %s \n", type.getLlvmName(), toStore, type.getLlvmPtr(), getLlvmPtr());
	}

	public String llvmStore(String toStore){
		return llvmStore(toStore, "%" + LanguageSpecs.SELF);
	}

	public boolean isPrimitive(){
		return type.isPrimitive();
	}

	public boolean isPointer(){
		return type.isPointer();
	}

	public void toPointer(){
		this.type.toPointer();
	}

	public int getClassFieldId() {
		return classFieldId;
	}

	public void setClassFieldId(int classFieldId) {
		this.classFieldId = classFieldId;
	}

	public String getParentClass() {
		return parentClass;
	}

	public void setParentClass(String parentClass) {
		this.parentClass = parentClass;
	}
}