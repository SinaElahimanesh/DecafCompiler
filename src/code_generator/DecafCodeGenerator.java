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
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

public class DecafCodeGenerator implements CodeGenerator {
	CompilerScanner scanner;
	DecafCodeReviewer codeReviewer;

	public static ArrayList<MipsLine> mipsLines = new ArrayList<>();
	Display display = new Display(mipsLines);
	TemporaryMemoryBank temporaryMemoryBank = new TemporaryMemoryBank();
	SymbolTable symbolTable = new SymbolTable();

	Stack<Variable> variables = new Stack<>();
	Stack<Indirect> addresses = new Stack<>();

	Stack<Label> labels = new Stack<>();

	AssignmentType assignmentType = AssignmentType.NONE;

	int lastLValue = 0;
	int currentCallId = 0;
	int lastRecordedIdentCallId = 0;
	String latestRecordedIdent = null;
	Symbol latestRecordedSymbol = null;

	public DecafCodeGenerator(CompilerScanner scanner, DecafCodeReviewer codeReviewer) throws SemanticException {
		this.scanner = scanner;
		this.codeReviewer = codeReviewer;
		display.addScope(codeReviewer.getGlobalScope());
		mipsLines.add(new Directive("globl", Collections.singletonList("main")));
		mipsLines.addAll(codeReviewer.getMipsLines());
		mipsLines.add(new Directive("text"));
	}

	@Override
	public void doSemantic(String sem, Action action) throws Throwable {
		if (action == Action.SHIFT) {
			currentCallId++;
		}

		if (sem.equals("")) {
			return;
		}
		System.out.println(sem);
		try {
			Method semanticMethod = this.getClass().getMethod(sem);
			semanticMethod.invoke(this);
		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//			throw new SemanticException("Unknown semantic " + sem + " has been used");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new SemanticException("Semantic " + sem + " can't be called");
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	public String getResult() {
		StringBuilder result = new StringBuilder();
		for (MipsLine line : mipsLines) {
			result.append(line).append('\n');
		}

		return result.toString();
	}

	public void is_lvalue_friendly() {
		lastLValue = currentCallId;
	}

	public void assignment_operator() throws SyntaxException {
		if (currentCallId - lastLValue > 2) {
			throw new SyntaxException("wrong assignment");
		}
	}

	public void type() {
		latestRecordedSymbol = symbolTable.getSymbol(scanner.getToken());
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

	public void recordIdent() throws SemanticException, NoSuchFieldException {
		latestRecordedIdent = scanner.getToken();
		lastRecordedIdentCallId = currentCallId;
		Variable variable = display.getVariable(latestRecordedIdent);
		if (symbolTable.getSymbol(latestRecordedIdent) != null) {
			latestRecordedSymbol = symbolTable.getSymbol(latestRecordedIdent);
		}

		if (variable != null) {
			variables.add(variable);
			addresses.add(display.getVariableAddress(latestRecordedIdent));
		}
	}

	public void declareInstantVariable() throws SyntaxException {
		if (!variables.empty())
			throw new SyntaxException("Declaration while variables is not empty.");

		display.allocateVariable(latestRecordedSymbol, scanner.getToken());
	}

	public void assign() throws NoSuchFieldException {
		loadWaitingIdent();
		assignmentType = AssignmentType.ASSIGN;
	}

	public void loadWaitingIdent() throws NoSuchFieldException {
		if (latestRecordedIdent != null && lastRecordedIdentCallId > currentCallId - 10) {
			variables.push(display.getVariable(latestRecordedIdent));
			addresses.push(display.getVariableAddress(latestRecordedIdent));
			latestRecordedIdent = null;
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

	Function currentFunction;
	public void declareFunction() throws NoSuchFieldException {
		if (currentClassSymbol != null) {
			currentFunction = currentClassSymbol.getMethod(latestRecordedIdent);
			mipsLines.add(LabelMaker.createFunctionLabel(currentClassSymbol.getName(), latestRecordedIdent));
		} else {
			currentFunction = codeReviewer.getGlobalScope().getFunction(latestRecordedIdent);
			mipsLines.add(LabelMaker.createFunctionLabel(latestRecordedIdent));
		}
		latestRecordedIdent = null;
	}

	public void endFunctionBlock() {
		mipsLines.add(new Instruction("jr", new Register("ra")));
	}

	Symbol currentClassSymbol;
}
