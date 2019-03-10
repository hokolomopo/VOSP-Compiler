package AST;

public class Formal {
	private String name;
	private Type type;

	public Formal(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	public void print() {
		System.out.print(name + ":");
		type.print();
	}
}