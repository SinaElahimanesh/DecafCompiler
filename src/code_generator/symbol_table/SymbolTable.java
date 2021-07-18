package code_generator.symbol_table;

import code_generator.symbol_table.symbols.IntSymbol;
import code_generator.symbol_table.symbols.Symbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SymbolTable {
    Map<String, Symbol> symbols = new HashMap<>();

    public SymbolTable() {
        symbols.put("int", new IntSymbol());
    }

    public Symbol getSymbol(String symbolName) {
        return symbols.get(symbolName);
    }
}