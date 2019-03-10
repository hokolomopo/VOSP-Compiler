package AST;

public class Call extends Expr {
	private Expr objExpr;
	private String methodName;
	private ArgList argList;

	public Call(Expr objExpr, String methodName, ArgList argList) {
		this.objExpr = objExpr;
		this.methodName = methodName;
		this.argList = argList;
	}

	public void print() {
		System.out.print("Call(");
		objExpr.print();
		System.out.print("," + methodName + ",");
		argList.print();
	}
}