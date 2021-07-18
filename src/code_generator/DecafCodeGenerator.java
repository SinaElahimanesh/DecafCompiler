package code_generator;

import code_generator.instructions.*;
import code_generator.operand.*;
import code_generator.stack.Display;
import code_generator.stack.TemporaryMemoryBank;
import code_generator.symbol_table.SymbolTable;
import code_generator.symbol_table.Variable;
import code_generator.symbol_table.symbols.BoolSymbol;
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

	Stack<Label> labels = new Stack<>();

	AssignmentType assignmentType = AssignmentType.NONE;
	OpType operand = OpType.NONE;

	int call_number = 0;
	int current_call = 0;
	String waitingIdent;

	public DecafCodeGenerator(CompilerScanner scanner) {
		this.scanner = scanner;
		mipsLines.add(new Directive("text"));
	}

	@Override
	public void doSemantic(String sem, Action action) throws Throwable {
		if (action == Action.SHIFT) {
			current_call++;
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
		call_number = current_call;
	}

	public void assignment_operator() throws SyntaxException {
		if (current_call - call_number > 2) {
			throw new SyntaxException("wrong assignment");
		}
	}

	public void type() {
		symbols.add(symbolTable.getSymbol(scanner.getToken()));
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

		variables.push(new Variable(symbolTable.getSymbol("int"), "____"));
		addresses.push(temporary);

		mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(SystemCall.read_int)));
		mipsLines.add(new Instruction("syscall"));
		mipsLines.add(new Instruction("sw", new Register("v0"), temporary));
	}

	public void recordIdent() throws SemanticException {
		String ident = scanner.getToken();
		waitingIdent = ident;
	}

	public void assign() {
		assignmentType = AssignmentType.ASSIGN;
	}

	public void subOpSet() {
		operand = OpType.SUBTRACT;
	}

	public void addOpSet() {
		operand = OpType.SUBTRACT;
	}

	public void addSubOp() {
		if (operand == OpType.SUBTRACT) {
			System.out.println("subbbbbb");		
		} else {
			System.out.println("addddddd");
		}
	}

	public void startScope() {

	}

	public void declareVariable() {
		
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

	public void ifStatement() throws SemanticException, ClassNotFoundException {
		Symbol symbol = variables.pop().getSymbol();
		Indirect address = addresses.pop();

		if (!(symbol instanceof BoolSymbol)) {
			throw new SemanticException("Boolean expression: the condition of If statement is not Boolean Expression");
		}
		Register value = RegisterBank.allocateRegister(symbol);
		mipsLines.add(new Instruction("lw", value, address));

		Label jumpLabel = LabelMaker.createNonFunctionLabel();
		labels.add(jumpLabel);
		mipsLines.add(new Instruction("bez", value, new LabelOperand(jumpLabel)));

		RegisterBank.freeRegister(value);
	}

	public void elseStatement() {

		Label jumpLabel = LabelMaker.createNonFunctionLabel();
		labels.add(jumpLabel);
		mipsLines.add(new Instruction("j", new LabelOperand(jumpLabel)));

		//
		Label label = labels.pop();
		mipsLines.add(label);
	}

	public void completeElse() {
		//
		Label label = labels.pop();
		mipsLines.add(label);
	}
}
