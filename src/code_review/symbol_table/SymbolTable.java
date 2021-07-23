package code_review.symbol_table;

import code_generator.SemanticException;
import code_review.symbol_table.symbols.*;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    Map<String, Symbol> symbols = new HashMap<>();

    public SymbolTable() throws SemanticException {
        addSymbol(IntSymbol.get());
        addSymbol(BoolSymbol.get());
        addSymbol(DoubleSymbol.get());
        addSymbol(VoidSymbol.get());
        addSymbol(StringSymbol.get());
    }

    public void addSymbol(Symbol symbol) throws SemanticException {
        if (symbols.containsKey(symbol.getName()))
            throw new SemanticException("Redeclaration of  type " + symbol.getName());

        symbols.put(symbol.getName(), symbol);
    }

    public Symbol getSymbol(String symbolName) {
        return symbols.get(symbolName);
    }
}