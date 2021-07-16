package code_generator.instructions;

import java.util.ArrayList;

public class Directive implements MipsLine {
	String name;
	ArrayList<String> parameters;

	public Directive(String name, ArrayList<String> parameters) {
		this.name = name;
		this.parameters = parameters;
	}

	public Directive(String name) {
		this.name = name;
		this.parameters = new ArrayList<>();
	}

	@Override
	public String toString() {
		StringBuilder directive_string = new StringBuilder();
		directive_string.append('.');
		directive_string.append(name);
		directive_string.append(' ');
		for (String parameter: parameters) {
			if (directive_string.length() != 0) {
				directive_string.append(", ");
			}

			directive_string.append(parameter);
		}

		return directive_string.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<String> parameters) {
		this.parameters = parameters;
	}
}
