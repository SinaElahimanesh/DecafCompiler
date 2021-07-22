package code_review.symbol_table.symbols;

public final class VoidSymbol extends Symbol {
	private static VoidSymbol instance = null;

	private VoidSymbol() {
		super("void");
		size = 4;
	}

	public static VoidSymbol get() {
		if (instance == null)
			instance = new VoidSymbol();
		return instance;
	}
}
