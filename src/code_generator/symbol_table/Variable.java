package code_generator.symbol_table;

import code_generator.symbol_table.symbols.Symbol;

public class Variable {
	Symbol symbol;
	AccessMode accessMode;
	String name;

	public Integer getSize() {
		return symbol.getSize();
	}
}
