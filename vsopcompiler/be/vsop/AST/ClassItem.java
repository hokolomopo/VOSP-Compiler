package be.vsop.AST;

import be.vsop.exceptions.semantic.ClassAlreadyDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassItem extends ASTNode{
	private Type type;
	private Type parentType;
	private ClassElementList cel;

	public ClassItem(Type type, ClassElementList cel) {
		this(type, new Type("Object"), cel);
	}

	public ClassItem(Type type, Type parentType, ClassElementList cel) {
		this.scopeTable = new ScopeTable();
		this.type = type;
		if (type.getName().equals("Object")) {
			this.parentType = null;
		} else {
			this.parentType = parentType;
		}
		this.cel = cel;

		this.children = new ArrayList<>();
		this.children.add(cel);

		//Don't add Types as children because we already check for missing type of class declaration in SyntaxAnalyzer
		//when checking for cyclic inheritance
	}

	@Override
	public void updateClassTable(HashMap<String, ClassItem> classTable, ArrayList<SemanticException> errorList) {
		this.classTable = classTable;
		if(classTable.containsKey(type.getName())) {
			errorList.add(new ClassAlreadyDeclaredException(type.getName(), line, column));
		} else {
			classTable.put(type.getName(), this);
		}
		if(children != null)
			for(ASTNode node : children)
				node.updateClassTable(classTable, errorList);
	}

	@Override
	public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
		if (parentType != null) {
			ScopeTable parentTable = classTable.get(getParentName()).scopeTable;
			this.scopeTable.setParent(parentTable);
		}
		//Add self field
		Formal self = new Formal(new Id("self"), type);
		this.scopeTable.addVariable(self);

		if(children != null)
			for(ASTNode node : children)
				node.fillScopeTable(this.scopeTable, errorList);
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("Class(" + type.getName() + "," + parentType.getName() + ",");

		System.out.println();
		cel.print(tabLevel +1, true);
		System.out.print(")");
	}

	public String getParentName() {
		return parentType.getName();
	}

	public String getName() {
		return type.getName();
	}

}