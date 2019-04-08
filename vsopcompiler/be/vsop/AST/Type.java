package be.vsop.AST;

import be.vsop.exceptions.semantic.ClassNotDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotValidException;

import java.util.ArrayList;

public class Type extends ASTNode{
	private String name;

	public Type(String name) {
		this.name = name;
	}

	@Override
	public void checkScope(ArrayList<SemanticException> errorList){
		if(Character.isUpperCase(name.charAt(0))) {
			if (!classTable.containsKey(name))
				errorList.add(new ClassNotDeclaredException(name, line, column));
		}
		else{
			//TODO isn't it useless ? a parser error will be raised before getting here
			if (scopeTable.lookupType(name) == null)
				errorList.add(new TypeNotValidException(name, line, column));
		}
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print(name);
	}

	public String getName() {
		return name;
	}
}