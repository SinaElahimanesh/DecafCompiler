package code_generator;

public class Symbol {
    private String symbolName;
    private SymbolType symbolType;

    public Symbol(String symbolName, SymbolType symbolType) {
        this.symbolName = symbolName;
        this.symbolType = symbolType;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }
}