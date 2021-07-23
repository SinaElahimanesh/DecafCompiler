package code_generator.nodes;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.Instruction;
import code_generator.instructions.Label;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_generator.operand.LabelOperand;
import code_generator.operand.Register;
import code_generator.operand.RegisterBank;
import code_generator.stack.TemporaryMemoryBank;
import code_review.symbol_table.Function;
import code_review.symbol_table.symbols.*;

import java.util.ArrayList;

public class ParenthesisNode implements Node {
	DecafCodeGenerator codeGenerator;

	ParenthesisNodeType parenthesisNodeType;
	OrNode assignmentNode;
	Label constantValue;
	String ident;

	Indirect address;
	Symbol symbol;
	Function function;

	boolean lValue;
	boolean complete = false;
	private Symbol type;

	ParenthesisNode(DecafCodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
	}

	@Override
	public Indirect getAddress() {
		return address;
	}

	@Override
	public Symbol getSymbol() {
		return symbol;
	}

	public Function getFunction() {
		return function;
	}

	@Override
	public void implement(ArrayList<MipsLine> mipsLines) throws SyntaxException, SemanticException {
		lValue = false;
		if (type != null)
			return;
		switch (parenthesisNodeType) {
			case THIS:
				try {
					address = codeGenerator.display.getVariableAddress("this");
				} catch (NoSuchFieldException e) {
					throw new SemanticException("this not found in display, have you used it out of a class?");
				}
				symbol = codeGenerator.currentClassSymbol;
				break;
			case CONSTANT:
				address = new Indirect(constantValue, new Register("zero"));
				switch (constantValue.getType()) {
					case "integer":
						symbol = IntSymbol.get();
						break;
					case "double":
						symbol = DoubleSymbol.get();
						break;
					case "bool":
						symbol = BoolSymbol.get();
						break;
					case "string":
						symbol = StringSymbol.get();
						address = TemporaryMemoryBank.allocateTemporaryMemory(4);
						Register register = RegisterBank.allocateRegister(StringSymbol.get());
						mipsLines.add(new Instruction("la", register, new LabelOperand(constantValue)));
						mipsLines.add(new Instruction("sw", register, address));
						RegisterBank.freeRegister(register);
						break;
					default:
						throw new SyntaxException("Constant with type: " + constantValue.getType());
				}
				break;
			case EXPRESSION:
				assert assignmentNode != null;
				assignmentNode.implement(mipsLines);
				address = assignmentNode.getAddress();
				symbol = assignmentNode.getSymbol();
				break;
			case IDENT:
				try {
					address = codeGenerator.display.getVariableAddress(ident);
					symbol = codeGenerator.display.getVariable(ident).getSymbol();
					lValue = true;
				} catch (NoSuchFieldException | NullPointerException e) {
					try {
						function = codeGenerator.codeReviewer.getGlobalScope().getFunction(ident);
					} catch (NoSuchFieldException noSuchFieldException) {
						throw new SemanticException("Ident not found: " + ident);
					}
				}
				break;
		}
	}

	@Override
	public void addIdent(String token) throws SyntaxException, SemanticException {
		if (parenthesisNodeType == ParenthesisNodeType.EXPRESSION) {
			if (!complete)
				assignmentNode.addIdent(token);
			else
				throw new SemanticException("Add ident to complete parenthesis.");
		} else if (token.equals("this")) {
			if (parenthesisNodeType != null)
				throw new SyntaxException("Add `this` to non-empty parenthesis.");

			parenthesisNodeType = ParenthesisNodeType.THIS;
			complete = true;
		} else if (token.indexOf(':') != -1) {
			if (parenthesisNodeType != null)
				throw new SyntaxException("Add " + token + " to non-empty parenthesis.");
			parenthesisNodeType = ParenthesisNodeType.CONSTANT;
			constantValue = new Label(token.substring(0, token.length()-1));
			complete = true;
		} else {
			if (parenthesisNodeType != null)
				throw new SyntaxException("Add ident " + token + " to non-empty parenthesis.");
			if (codeGenerator.display.getVariable(token) == null && codeGenerator.symbolTable.getSymbol(token) != null) {
				this.type = codeGenerator.symbolTable.getSymbol(token);
				parenthesisNodeType = ParenthesisNodeType.IDENT;
			}
			this.ident = token;
			parenthesisNodeType = ParenthesisNodeType.IDENT;
			lValue = true;
			complete = true;
		}
	}

	@Override
	public void addOperator(String operator) throws SyntaxException, SemanticException {
		switch (operator)
		{
			case "(":
				if (complete)
					throw new SyntaxException("Passing " + operator + " to complete parenthesis.");

				if (parenthesisNodeType == null) {
					parenthesisNodeType = ParenthesisNodeType.EXPRESSION;
					assignmentNode = new OrNode(codeGenerator);
				}
				else {
					assignmentNode.addOperator(operator);
				}
				complete = false;
				break;
			case ")":
				if (complete)
					throw new SyntaxException("Passing " + operator + " to complete parenthesis.");

				if (parenthesisNodeType != ParenthesisNodeType.EXPRESSION)
					throw new SyntaxException("Passing ) to parenthesis with type " + parenthesisNodeType);
				if (!assignmentNode.isComplete()) {
					assignmentNode.addOperator(operator);
				} else {
					complete = true;
				}
				break;
			case "[":
				if (type != null) {
					type = new ArraySymbol(type);
					break;
				}
			case "]":
				if (type != null) {
					break;
				}
			default:
				if (complete)
					throw new SyntaxException("Passing " + operator + " to complete parenthesis.");

				if (parenthesisNodeType != ParenthesisNodeType.EXPRESSION)
					throw new SyntaxException("Passing operator to not expression parenthesis.");
				assignmentNode.addOperator(operator);
		}
	}

	@Override
	public boolean isLValue() {
		return lValue;
	}

	@Override
	public boolean isComplete() throws SyntaxException, SemanticException {
		return complete;
	}

	@Override
	public Symbol getType() {
		return type;
	}

	public OrNode getExpressionNode() {
		return assignmentNode;
	}

	public void setExpressionNode(OrNode assignmentNode) {
		this.assignmentNode = assignmentNode;
	}

	public Label getConstantValue() {
		return constantValue;
	}

	public void setConstantValue(Label constantValue) {
		this.constantValue = constantValue;
	}
}
