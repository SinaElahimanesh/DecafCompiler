package code_generator.nodes;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.Instruction;
import code_generator.instructions.Label;
import code_generator.instructions.MipsLine;
import code_generator.instructions.SystemCall;
import code_generator.operand.*;
import code_generator.stack.TemporaryMemoryBank;
import code_review.symbol_table.Function;
import code_review.symbol_table.symbols.IntSymbol;
import code_review.symbol_table.symbols.Primitive;
import code_review.symbol_table.symbols.StringSymbol;
import code_review.symbol_table.symbols.Symbol;

import java.util.ArrayList;

public class CallNode implements Node {
	DecafCodeGenerator codeGenerator;

	ParenthesisNode parenthesisNode;

	ArrayList<OrNode> arguments = new ArrayList<>();

	Indirect address;
	Symbol symbol;

	boolean complete = false;

	public CallNode(DecafCodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
		parenthesisNode = new ParenthesisNode(codeGenerator);

	}

	@Override
	public Indirect getAddress() {
		return address;
	}

	@Override
	public Symbol getSymbol() {
		return symbol;
	}

	@Override
	public void implement(ArrayList<MipsLine> mipsLines) throws SyntaxException, SemanticException {
		parenthesisNode.implement(mipsLines);
		if (arguments.size() == 0) {
			parenthesisNode.implement(mipsLines);
			symbol = parenthesisNode.symbol;
			address = parenthesisNode.address;
			return;
		}

		Function function = parenthesisNode.function;

		// Set arguments
		if (arguments.size() > 1 || arguments.get(0).isComplete()) {
			for (OrNode argument : arguments) {
				argument.implement(mipsLines);
			}

			if (!function.getName().equals("Print")) {
				if (function.getArguments().size() != arguments.size()) {
					throw new SemanticException("Incompatible arguments size " + function.getArguments().size() +
							" and " + arguments.size() + " for function " + function.getName());
				}
				for (int i = 0; i < arguments.size(); i++) {
					if (!arguments.get(i).getSymbol().equals(function.getArguments().get(i).getSymbol())) {
						throw new SemanticException("Incompatible argumnets with type " +
								arguments.get(i).getSymbol().getName() +
								" and " + function.getArguments().get(i).getSymbol().getName());
					}
				}
			} else {
				for (OrNode argument : arguments) {
					if (argument.getSymbol() instanceof Primitive) {
						try {
							Register register = RegisterBank.allocateRegister(argument.getSymbol());
							mipsLines.add(new Instruction("la", register, argument.getAddress()));
							((Primitive) argument.getSymbol()).print(register);
							RegisterBank.freeRegister(register);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							System.exit(0);
						}
					} else {
						throw new SemanticException("Print called for non-primitive type.");
					}
				}

				mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(SystemCall.print_string)));
				mipsLines.add(new Instruction("la", new Register("a0"), new LabelOperand(new Label("string__newline"))));
				mipsLines.add(new Instruction("syscall"));

				address = null;
				symbol = null;
				return;
			}

			int argumentMemory = TemporaryMemoryBank.size + 8;

			for (OrNode argument : arguments) {
				try {
					Register register = RegisterBank.allocateRegister(argument.getSymbol());
					mipsLines.add(new Instruction("lw", register, argument.getAddress()));
					mipsLines.add(new Instruction("sw", register, new Indirect(argumentMemory, new Register("sp"))));
					RegisterBank.freeRegister(register);
					argumentMemory += 4;
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}

		if (function.getName().equals("ReadInteger")) {
			mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(5)));
			mipsLines.add(new Instruction("syscall"));
			symbol = IntSymbol.get();
			address = TemporaryMemoryBank.allocateTemporaryMemory(4);
			mipsLines.add(new Instruction("sw", new Register("v0"), address));
			return;
		}

		if (function.getName().equals("ReadLine")) {
			symbol = StringSymbol.get();
			address = TemporaryMemoryBank.allocateTemporaryMemory(1024);
			mipsLines.add(new Instruction("add", new Register("a0"), new Register("sp"), new Register("zero")));
			mipsLines.add(new Instruction("addi", new Register("a0"), new Immediate(address.getImmediate())));
			mipsLines.add(new Instruction("li", new Register("a1"), new Immediate(1024)));
			mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(8)));
			mipsLines.add(new Instruction("syscall"));
			return;
		}

		Indirect returnAddress = TemporaryMemoryBank.allocateTemporaryMemory(4);
		mipsLines.add(new Instruction("sw", new Register("ra"), returnAddress));
		mipsLines.add(new Instruction("addi", new Register("sp"), new Register("sp"), new Immediate(TemporaryMemoryBank.size)));
		mipsLines.add(new Instruction("jal", new LabelOperand(function.getStartLabel())));
		mipsLines.add(new Instruction("addi", new Register("sp"), new Register("sp"), new Immediate(-TemporaryMemoryBank.size)));
		mipsLines.add(new Instruction("lw", new Register("ra"), returnAddress));
		address = TemporaryMemoryBank.allocateTemporaryMemory(4);
		symbol = function.getReturnType();
	}

	@Override
	public void addIdent(String token) throws SyntaxException, SemanticException {
		if (arguments.size() > 0)
			arguments.get(arguments.size() - 1).addIdent(token);
		else {
			parenthesisNode.addIdent(token);
			complete = parenthesisNode.isComplete();
		}
	}

	@Override
	public void addOperator(String operator) throws SyntaxException, SemanticException {
		if (complete && arguments.size() > 0)
			throw new SemanticException("Call node is complete, but received operator " + operator);
		switch (operator) {
			case ",":
				if (arguments.size() > 0)
					try {
						arguments.get(arguments.size() - 1).addOperator(operator);
					} catch (SyntaxException | SemanticException e) {
						arguments.add(new OrNode(codeGenerator));
					}
				else
					parenthesisNode.addOperator(operator);
				break;
			case "(":
				if (arguments.size() > 0)
					arguments.get(arguments.size() - 1).addOperator(operator);
				else if (!parenthesisNode.isComplete()) {
					parenthesisNode.addOperator(operator);
				} else {
					arguments.add(new OrNode(codeGenerator));
					complete = false;
				}
				break;
			case ")":
				if (arguments.size() > 0) {
					try {
						arguments.get(arguments.size() - 1).addOperator(operator);
					} catch (SyntaxException | SemanticException e) {
						complete = true;
					}
				} else {
					parenthesisNode.addOperator(operator);
					complete = parenthesisNode.isComplete();
				}
				break;
			default:
				if (arguments.isEmpty())
					parenthesisNode.addOperator(operator);
				else
					arguments.get(arguments.size() - 1).addOperator(operator);
				break;
		}
	}

	@Override
	public boolean isLValue() {
		return arguments.size() == 0 && parenthesisNode.isLValue();
	}

	@Override
	public boolean isComplete() throws SyntaxException, SemanticException {
		return complete;
	}
}
