package code_generator.instructions;

import java.util.ArrayList;
import java.util.List;

public class Directive implements MipsLine {
	String name;
	List<String> parameters;

	public Directive(String name, List<String> parameters) {
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
		Integer startLength = directive_string.length();
		for (String parameter: parameters) {
			if (directive_string.length() != startLength) {
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

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<String> parameters) {
		this.parameters = parameters;
	}
}
