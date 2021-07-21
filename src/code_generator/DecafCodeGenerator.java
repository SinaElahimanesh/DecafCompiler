package code_generator;

import code_generator.instructions.*;
import code_generator.operand.*;
import code_generator.stack.Display;
import code_generator.stack.TemporaryMemoryBank;
import code_review.DecafCodeReviewer;
import code_review.symbol_table.Function;
import code_review.symbol_table.SymbolTable;
import code_review.symbol_table.Variable;
import code_review.symbol_table.symbols.BoolSymbol;
import code_review.symbol_table.symbols.Primitive;
import code_review.symbol_table.symbols.Symbol;
import parser.Action;
import parser.CodeGenerator;
import scanner.CompilerScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Stack;

public class DecafCodeGenerator implements CodeGenerator {
	CompilerScanner scanner;
	DecafCodeReviewer codeReviewer;

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

	int lastLValue = 0;
	int currentCall = 0;
	int lastRecordedIdent = 0;
	String waitingIdent = null;

	public DecafCodeGenerator(CompilerScanner scanner, DecafCodeReviewer codeReviewer) {
		this.scanner = scanner;
		this.codeReviewer = codeReviewer;
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

	public void ifStatement() throws SemanticException, ClassNotFoundException {
		Symbol symbol = variables.pop().getSymbol();
		Indirect address = addresses.pop();

		if (!(symbol instanceof BoolSymbol)) {
			throw new SemanticException("Boolean expression: the condition of If statement is not Boolean Expression");
		}
		Register value = RegisterBank.allocateRegister(symbol);
		mipsLines.add(new Instruction("lw", value, address));

		Label jumpLabel = LabelMaker.createNonFunctionLabel();
		labels.push(jumpLabel);
		mipsLines.add(new Instruction("bez", value, new LabelOperand(jumpLabel)));

		RegisterBank.freeRegister(value);
	}

	public void elseStatement() {

		Label jumpLabel = LabelMaker.createNonFunctionLabel();
		labels.push(jumpLabel);
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

	public void whileLabel() {
		Label label = LabelMaker.createNonFunctionLabel();
		mipsLines.add(label);
		//
		labels.push(label);
	}

	public void whileLoop() throws SemanticException, ClassNotFoundException {
		Symbol symbol = variables.pop().getSymbol();
		Indirect address = addresses.pop();

		if (!(symbol instanceof BoolSymbol)) {
			throw new SemanticException("Boolean expression: the condition of If statement is not Boolean Expression");
		}
		Register value = RegisterBank.allocateRegister(symbol);
		mipsLines.add(new Instruction("lw", value, address));

		Label jumpLabel = LabelMaker.createNonFunctionLabel();
		labels.push(jumpLabel);
		mipsLines.add(new Instruction("bez", value, new LabelOperand(jumpLabel)));

		RegisterBank.freeRegister(value);
	}

	public void whileComplete() {
		//
		Label label = labels.pop();
		mipsLines.add(label);
	}

	public void integerConstant() {
		int integer_number = Integer.parseInt(scanner.nextToken());
		Label label = LabelMaker.createIntegerConstantLabel(integer_number);
		mipsLines.add(label);
		ArrayList<String> parameters = new ArrayList<>();
		parameters.add(String.valueOf(integer_number));
		mipsLines.add(new Directive(".word", parameters));
	}

	public void doubleConstant() {
		double double_number = Double.parseDouble(scanner.nextToken());
		Label label = LabelMaker.createDoubleConstantLabel(double_number);
		mipsLines.add(label);
		ArrayList<String> parameters = new ArrayList<>();
		parameters.add(String.valueOf(double_number));
		mipsLines.add(new Directive(".float", parameters));
	}

	public void booleanConstant() {
		boolean booli = Boolean.parseBoolean(scanner.nextToken());
		Label label = LabelMaker.createBooleanConstantLabel(booli);
		mipsLines.add(label);
		ArrayList<String> parameters = new ArrayList<>();
		if(booli) { // it is true
			parameters.add(String.valueOf(1));
			mipsLines.add(new Directive(".word", parameters));
		} else { // it is false
			parameters.add(String.valueOf(0));
			mipsLines.add(new Directive(".word", parameters));
		}

	}

	public void stringConstant() {

	}

	public void declareWaitingVariable() throws NoSuchFieldException {
		//FIXME make declarations for ident types right.
		String name = scanner.getToken();
		if (currentCall < lastRecordedIdent + 3 && waitingIdent != null) {
			symbols.push(symbolTable.getSymbol(waitingIdent));
			waitingIdent = null;
		}

		// FIXME add to declaring class symbol fields (if any) as well.
		Variable variable = display.allocateVariable(symbols.peek(), name);

		if (shouldDeclareArguments)
		{
			currentDeclaringFunction.addArgument(variable);
		}

		symbols.remove(symbols.pop());
	}

	Boolean shouldDeclareArguments;
	Function currentDeclaringFunction;

	public void startFunction() {
		shouldDeclareArguments = true;

		Boolean shouldDeclareArguments = true;

		String name = waitingIdent;
		waitingIdent = null;

		// FIXME add for classes as well.
		currentDeclaringFunction = new Function(name);

		currentDeclaringFunction.setReturnType(symbols.pop());

		mipsLines.add(LabelMaker.createFunctionLabel(name));

		startScope();
		//FIXME is this the best way?
		display.allocateVariable(currentDeclaringFunction.getReturnType(),
				"return");
	}

	public void endDeclareArguments() {
		shouldDeclareArguments = false;
	}

	public void endFunction() {
		mipsLines.add(new Instruction("jr", new Register("ra")));
	}
}
