package AST;

import java.util.ArrayList;

public class Call extends Expr {
	private Expr objExpr;
	private String methodName;
	private ArgList argList;

	public Call(Expr objExpr, String methodName, ArgList argList) {
		this.objExpr = objExpr;
		this.methodName = methodName;
		this.argList = argList;

		this.children = new ArrayList<>();
		this.children.add(objExpr);
		this.children.add(argList);
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("Call(");
		objExpr.print(tabLevel, false);
		System.out.print("," + methodName + ",");
		argList.print(tabLevel, false);
		System.out.print(")");
	}
}