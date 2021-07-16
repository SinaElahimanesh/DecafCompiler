package code_generator.symbol_table.symbols;

public final class BlankSymbol extends Symbol {
	private static BlankSymbol instance = null;

	private BlankSymbol() {
		super("_blank_");
	}

	public static BlankSymbol get() {
		if (instance == null) {
			return new BlankSymbol();
		} else {
			return instance;
		}
	}
}
