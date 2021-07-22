package code_generator.nodes;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.Label;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_generator.operand.Register;
import code_review.symbol_table.Function;
import code_review.symbol_table.symbols.BoolSymbol;
import code_review.symbol_table.symbols.DoubleSymbol;
import code_review.symbol_table.symbols.IntSymbol;
import code_review.symbol_table.symbols.Symbol;

import java.util.ArrayList;

public class ParenthesisNode implements Node {
	DecafCodeGenerator codeGenerator;

	ParenthesisNodeType parenthesisNodeType;
	AssignmentNode assignmentNode;
	Label constantValue;
	String ident;

	Indirect address;
	Symbol symbol;
	Function function;

	boolean lValue;
	boolean complete = false;

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
						// FIXME Create string symbol.
//						symbol = StringSymbol.get();
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
				throw new SyntaxException("Add `this` to non-empty parenthesis.");

			parenthesisNodeType = ParenthesisNodeType.CONSTANT;
			constantValue = new Label(token.substring(0, token.length()-1));
			complete = true;
		} else {
			this.ident = token;
			parenthesisNodeType = ParenthesisNodeType.IDENT;
			lValue = true;
			complete = true;
		}
	}

	@Override
	public void addOperator(String operator) throws SyntaxException {
		if (complete)
			throw new SyntaxException("Passing " + operator + " to complete parenthesis.");

		switch (operator)
		{
			case "(":
				if (parenthesisNodeType != null)
					throw new SyntaxException("Passing ( to parenthesis with type.");
				parenthesisNodeType = ParenthesisNodeType.EXPRESSION;
				assignmentNode = new AssignmentNode(codeGenerator);
			case ")":
				if (parenthesisNodeType != ParenthesisNodeType.EXPRESSION)
					throw new SyntaxException("Passing ) to parenthesis with type " + parenthesisNodeType);

				complete = true;
			default:
				if (parenthesisNodeType != ParenthesisNodeType.EXPRESSION)
					throw new SyntaxException("Passing operator to not expression parenthesis.");
		}
	}

	@Override
	public boolean isLValue() {
		return lValue;
	}

	public AssignmentNode getExpressionNode() {
		return assignmentNode;
	}

	public void setExpressionNode(AssignmentNode assignmentNode) {
		this.assignmentNode = assignmentNode;
	}

	public Label getConstantValue() {
		return constantValue;
	}

	public void setConstantValue(Label constantValue) {
		this.constantValue = constantValue;
	}
}
