package code_generator.stack;

import code_generator.instructions.Instruction;
import code_generator.instructions.MipsLine;
import code_generator.operand.Immediate;
import code_generator.operand.Indirect;
import code_generator.operand.Register;
import code_review.symbol_table.Variable;
import code_review.symbol_table.symbols.Symbol;

import java.util.ArrayList;
import java.util.Stack;

public class Display {
	ArrayList<MipsLine> mipsLines;
	Stack<Scope> scopes = new Stack<>();

	public Display(ArrayList<MipsLine> mipsLines) {
		this.mipsLines = mipsLines;
	}

	public Scope addNewScope() {
		mipsLines.add(new Instruction("addi",
				new Register("sp"),
				new Register("sp"),
				new Immediate(scopes.get(scopes.size() - 1).getSize())));

		Scope scope = new Scope();
		scopes.add(scope);
		return scope;
	}

	public void popScope() {
		mipsLines.add(new Instruction("addi", new Register("sp"), new Register("sp"), new Immediate(-scopes.pop().getSize())));
	}

	public Scope getLastScope() {
		return scopes.get(scopes.size() - 1);
	}

	public Variable getVariable(String name) {
		for (int i = scopes.size() - 1; i > -1; i--) {
			if (scopes.get(i).getVariable(name) != null) {
				return scopes.get(i).getVariable(name);
			}
		}

		return null;
	}

	public Indirect getVariableAddress(String name) throws NoSuchFieldException {
		Integer currentAddress = 0;
		for (int i = scopes.size() - 1; i > -1; i--) {
			if (scopes.get(i).hasVariable(name)) {
				return new Indirect(currentAddress + scopes.get(i).getVariableAddress(name), new Register("sp"));
			}
			currentAddress -= scopes.get(i).size;
		}

		return null;
	}

	public Variable allocateVariable(Symbol type, String name) {
		Scope scope = getLastScope();
		Variable variable = new Variable(type, name);
		scope.addVariable(variable);
		return variable;
	}

	public void addScope(Scope globalScope) {
		scopes.add(globalScope);
	}

	public int findScopeDepth(Scope scope) {
		int result = 0;
		for (int i = scopes.size()-1; i >= 0; i --) {
			result += scopes.get(i).size;
			if (scopes.get(i) == scope) {
				return result;
			}
		}

		return result;
	}
}
