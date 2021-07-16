package code_generator.instructions;

public class Label implements MipsLine {
	String name;

	public Label(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name + ':';
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
