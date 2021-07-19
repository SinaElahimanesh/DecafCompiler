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
import code_generator.stack.TemporaryMemoryBank;
import code_generator.symbol_table.SymbolTable;
import code_generator.symbol_table.Variable;
import code_generator.symbol_table.symbols.Primitive;
import code_generator.symbol_table.symbols.Symbol;
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
	SymbolTable symbolTable = new SymbolTable();

	Stack<Symbol> symbols = new Stack<>();

	Stack<Variable> variables = new Stack<>();
	Stack<Indirect> addresses = new Stack<>();

	AssignmentType assignmentType = AssignmentType.NONE;

	int lastLValue = 0;
	int currentCall = 0;
	int lastRecordedIdent = 0;
	String waitingIdent = null;

	public DecafCodeGenerator(CompilerScanner scanner) {
		this.scanner = scanner;
		mipsLines.add(new Directive("text"));
	}

	@Override
	public void doSemantic(String sem, Action action) throws Throwable {
		if (action == Action.SHIFT) {
			currentCall++;
		}

		if (sem.equals("")) {
			return;
		}
		System.out.println(sem);
		try {
			Method semanticMethod = this.getClass().getMethod(sem);
			semanticMethod.invoke(this);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new SemanticException("Unknown semantic " + sem + " has been used");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new SemanticException("Semantic " + sem + " can't be called");
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	public void is_lvalue_friendly() {
		lastLValue = currentCall;
	}

	public void assignment_operator() throws SyntaxException {
		if (currentCall - lastLValue > 2) {
			throw new SyntaxException("wrong assignment");
		}
	}

	public void type() {
		symbols.add(symbolTable.getSymbol(scanner.getToken()));
	}

	public void declareVariable() {
		//FIXME make declarations for ident types right.
		String name = scanner.getToken();
		if (currentCall < lastRecordedIdent + 3 && waitingIdent != null) {
			symbols.push(symbolTable.getSymbol(waitingIdent));
			waitingIdent = null;
		}

		display.allocateVariable(symbols.peek(), name);
		symbols.remove(symbols.pop());
	}

	public void endLine() {
		temporaryMemoryBank.resetMemory();
	}

	public void readInteger() {
		Integer intSymbolSize = 4;
		Indirect temporary = temporaryMemoryBank.allocateTemporaryMemory(intSymbolSize);

		variables.push(new Variable(symbolTable.getSymbol("int"), "____"));
		addresses.push(temporary);

		mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(SystemCall.read_int)));
		mipsLines.add(new Instruction("syscall"));
		mipsLines.add(new Instruction("sw", new Register("v0"), temporary));
	}

	public void recordIdent() throws SemanticException {
		waitingIdent = scanner.getToken();
		lastRecordedIdent = currentCall;
	}

	public void assign() throws NoSuchFieldException {
		loadWaitingIdent();
		assignmentType = AssignmentType.ASSIGN;
	}

	public void loadWaitingIdent() throws NoSuchFieldException {
		if (waitingIdent != null && lastRecordedIdent > currentCall - 10) {
			variables.push(display.getVariable(waitingIdent));
			addresses.push(display.getVariableAddress(waitingIdent));
			waitingIdent = null;
		}
	}

	public void doAssignment() throws SemanticException, ClassNotFoundException, NoSuchFieldException {
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

	public void print() throws SemanticException, ClassNotFoundException, NoSuchFieldException {
		loadWaitingIdent();
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

	public void startScope() {
		display.addNewScope();
	}

	public void endScope() {
		display.popScope();
	}

	public String getResult() {
		StringBuilder result = new StringBuilder();
		for (MipsLine line : mipsLines) {
			result.append(line).append('\n');
		}

		return result.toString();
	}
}
