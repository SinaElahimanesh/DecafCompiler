package code_review.symbol_table;

import code_generator.instructions.Label;
import code_generator.instructions.LabelMaker;
import code_review.symbol_table.symbols.Symbol;

import java.util.ArrayList;

public class Function {
	Label startLabel;
	String name;
	Symbol returnType;
	AccessMode accessMode;
	ArrayList<Variable> arguments = new ArrayList<>();

	/**
	 * This constructor should be used only when it's a function, not a method in a class.
	 * @param name name of the **FUNCTION**
	 */
	public Function(String name) {
		startLabel = LabelMaker.createFunctionLabel(name);
		this.name = name;
	}

	/**
	 * This constructor should be used only when it's a method in a class.
	 * @param className Class in where the method is declared
	 * @param methodName The method's name
	 */
	public Function(String className, String methodName) {
		startLabel = LabelMaker.createFunctionLabel(className, methodName);
		name = methodName;
	}

	public Label getStartLabel() {
		return startLabel;
	}

	public String getName() {
		return name;
	}

	public AccessMode getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(AccessMode accessMode) {
		this.accessMode = accessMode;
	}

	public Symbol getReturnType() {
		return returnType;
	}

	public Function setReturnType(Symbol returnType) {
		this.returnType = returnType;
		return this;
	}

	public ArrayList<Variable> getArguments() {
		return arguments;
	}

	public Function addArgument(Variable argument) {
		this.arguments.add(argument);
		return this;
	}
}
