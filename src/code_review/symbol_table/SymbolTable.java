package code_review.symbol_table;

import code_review.symbol_table.symbols.IntSymbol;
import code_review.symbol_table.symbols.Symbol;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    Map<String, Symbol> symbols = new HashMap<>();

    public SymbolTable() {
        symbols.put("int", IntSymbol.get());
    }

    public Symbol getSymbol(String symbolName) {
        return symbols.get(symbolName);
    }
}