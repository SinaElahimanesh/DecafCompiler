package code_review.symbol_table;

import code_review.symbol_table.symbols.VoidSymbol;
import code_review.symbol_table.symbols.Symbol;

public class Variable {
	Symbol symbol;
	AccessMode accessMode;
	String name;

	public Variable() {
		symbol = VoidSymbol.get();
		accessMode = AccessMode.NONE;
	}

	public Variable(Symbol type, String name) {
		symbol = type;
		this.name = name;
		accessMode = AccessMode.NONE;
	}

	public Variable setSymbol(Symbol symbol) {
		this.symbol = symbol;
		return this;
	}

	public Variable setAccessMode(AccessMode accessMode) {
		this.accessMode = accessMode;
		return this;
	}

	public Variable setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getSize() {
		return symbol.getSize();
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public AccessMode getAccessMode() {
		return accessMode;
	}

	public String getName() {
		return name;
	}
}
