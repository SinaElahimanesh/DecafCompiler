package code_generator.stack;

import code_review.symbol_table.Variable;

import java.util.ArrayList;

public class Scope {
	ArrayList<Variable> variables = new ArrayList<>();
	Integer size = 0;

	public void addVariable(Variable variable) {
		variables.add(variable);
		size += variable.getSize();
	}

	public boolean hasVariable(String name) {
		for (Variable variable: variables) {
			if (variable.getName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	public Variable getVariable(String name) throws NoSuchFieldException {
		System.out.println("name: "+ name);
		for (Variable variable: variables) {
			System.out.println("Variables: " + variable.getName());
			if (variable.getName().equals(name)) {
				return variable;
			}
		}

		throw new NoSuchFieldException("Scope::getVariable: Variable " + name + " not found.");
	}

	public Integer getVariableAddress(String name) throws NoSuchFieldException {
		Integer currentAddress = -size;
		System.out.println("name: "+ name);
		for (Variable variable: variables) {
			System.out.println("Variables: " + variable.getName());
			if (variable.getName().equals(name)) {
				return currentAddress;
			}

			currentAddress += variable.getSize();
		}

		throw new NoSuchFieldException("Scope::getVariableAddress: Variable " + name + " not found.");
	}

	public Integer getSize() {
		return size;
	}
}
