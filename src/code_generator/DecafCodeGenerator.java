package code_generator;

import code_generator.instructions.Directive;
import code_generator.instructions.Instruction;
import code_generator.instructions.MipsLine;
import code_generator.instructions.SystemCall;
import code_generator.operand.Immediate;
import code_generator.operand.Indirect;
import code_generator.operand.Register;
import code_generator.operand.RegisterBank;
import code_generator.stack.Display;
import code_generator.stack.Scope;
import code_generator.stack.TemporaryMemoryBank;
import code_generator.symbol_table.SymbolType;
import code_generator.symbol_table.Variable;
import code_generator.symbol_table.symbols.IntSymbol;
import code_generator.symbol_table.symbols.Primitive;
import code_generator.symbol_table.symbols.Symbol;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import parser.Action;
import parser.CodeGenerator;
import scanner.CompilerScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Stack;

public class DecafCodeGenerator implements CodeGenerator {
	CompilerScanner scanner;

	public static ArrayList<MipsLine> mipsLines = new ArrayList<>();
	Display display = new Display(mipsLines);
	TemporaryMemoryBank temporaryMemoryBank = new TemporaryMemoryBank();

	Stack<Symbol> symbols = new Stack<>();

	Stack<Variable> variables = new Stack<>();
	Stack<Indirect> addresses = new Stack<>();

	AssignmentType assignmentType = AssignmentType.NONE;

	int call_number = 0;
	int current_call = 0;

	public DecafCodeGenerator(CompilerScanner scanner) {
		this.scanner = scanner;
		mipsLines.add(new Directive("text"));
	}

	@Override
	public void doSemantic(String sem, Action action) throws SemanticException {
		if (action != Action.SHIFT)
			return;
		current_call++;
		if (sem.equals("")) {
			return;
		}

		try {
			Method semanticMethod = this.getClass().getMethod(sem);
			semanticMethod.invoke(this);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new SemanticException("Unknown semantic " + sem + " has been used");
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			throw new SemanticException("Semantic " + sem + " can't be called");
		}
	}

	public void is_lvalue_friendly() {
		call_number = current_call;
	}

	public void assignment_operator() {
		if (current_call - call_number > 2) {
			throw new SyntaxException("wrong assignment");
		}
	}

	public void Type_int() {
		symbols.add(new IntSymbol());
	}

	public void Variable_name() {
		String name = scanner.getToken();
		display.allocateVariable(symbols.get(symbols.size() - 1), name);
		symbols.remove(symbols.get(symbols.size() - 1));
	}

	public void endLine() {
		temporaryMemoryBank.resetMemory();
	}

	public void readInteger() {
		Integer intSymbolSize = 4;
		Indirect temporary = temporaryMemoryBank.allocateTemporaryMemory(intSymbolSize);

		variables.push(new Variable(new IntSymbol(), "____"));
		addresses.push(temporary);

		mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(SystemCall.read_int)));
		mipsLines.add(new Instruction("syscall"));
		mipsLines.add(new Instruction("sw", new Register("v0"), temporary));
	}

	public void recordIdent() throws SemanticException {
		String ident = scanner.getToken();
		try {
			variables.push(display.getVariable(ident));
			addresses.push(display.getVariableAddress(ident));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			throw new SemanticException("recordIdent: No varibale with name " + ident + " found in display.");
		}
	}

	public void assign() {
		assignmentType = AssignmentType.ASSIGN;
	}

	public void doAssignment() throws SemanticException, ClassNotFoundException {
		if (!variables.get(variables.size() - 1).getSymbol().equals(variables.get(variables.size() - 2).getSymbol())) {
			throw new SemanticException("doAssignment: Incompatible types");
		}

		Symbol lhsSymbol = variables.get(variables.size() - 2).getSymbol();
		Symbol rhsSymbol = variables.get(variables.size() - 1).getSymbol();

		if (!(lhsSymbol instanceof Primitive)) {
			throw new SemanticException("doAssignment: Incompatible ");
		}

		Indirect lhsAddress = addresses.get(addresses.size() - 2);
		Indirect rhsAddress = addresses.get(addresses.size() - 1);

		Register rhs = RegisterBank.allocateRegister(rhsSymbol);

		mipsLines.add(new Instruction("lw", rhs, rhsAddress));
		mipsLines.add(new Instruction("sw", rhs, lhsAddress));

		RegisterBank.freeRegister(rhs);

		addresses.pop();		// Removes the rhs to make other assignments possible;
		variables.pop();
	}

	public void print() throws SemanticException, ClassNotFoundException {
		Symbol symbol = variables.pop().getSymbol();
		if (!(symbol instanceof Primitive)) {
			throw new SemanticException("print: Incompatible type to print");
		}

		Indirect address = addresses.pop();
		Register value = RegisterBank.allocateRegister(symbol);

		mipsLines.add(new Instruction("lw", value, address));

		((Primitive) symbol).print(value);

		RegisterBank.freeRegister(value);
	}

	public String get_result() {
		StringBuilder result = new StringBuilder();
		for (MipsLine line : mipsLines) {
			result.append(line).append('\n');
		}

		return result.toString();
	}
}
