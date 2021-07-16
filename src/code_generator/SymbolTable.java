package code_generator;

import java.util.ArrayList;

public class SymbolTable {

    private static ArrayList<Symbol> allSymbols = new ArrayList<Symbol>();

    public void addSymbol(Symbol symbol) {
        allSymbols.add(symbol);
    }

    public boolean doesSymbolNameExists(String ident) {
        for (Symbol symbol : allSymbols) {
            if(symbol.getSymbolName().equals(ident)) {
                return true;
            }
        }
        return false;
    }

    public Symbol getSymbolById(String ident) {
        for (Symbol symbol : allSymbols) {
            if(symbol.getSymbolName().equals(ident)) {
                return symbol;
            }
        }
        return null; // if there is no symbol with this name returns NULL
    }
}