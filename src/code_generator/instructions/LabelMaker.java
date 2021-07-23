package code_generator.instructions;

public final class LabelMaker {
	private LabelMaker() {}

	static Integer id = 0;

	/**
	 * Used to create labels for methods in classes. We should hope that no one will ever use ____ in their names.
	 * @param className Name of the class method is declared in
	 * @param methodName Name of the method
	 * @return A label appropriate for the input method
	 */
	public static Label createFunctionLabel(String className, String methodName) {
		return new Label(className + "____" + methodName);
	}

	/**
	 * Used to create labels for functions out of classes. We should hope that no one will ever use ____ in their names.
	 * @param functionName Name of the function
	 * @return A label appropriate for the input function
	 */
	public static Label createFunctionLabel(String functionName) {
		return new Label(functionName);
	}

	/**
	 * Used to create labels for non-functions. We should hope that no one will ever use ____ in their names.
	 * @return An unused label in the program.
	 */
	public static Label createNonFunctionLabel() {
		id ++;
		return new Label("label____" + id.toString());
	}

	private static int INTEGER_CONSTANT_ID = 0;
	private static int DOUBLE_CONSTANT_ID = 0;
	private static int BOOLEAN_CONSTANT_ID = 0;
	private static int STRING_CONSTANT_ID = 0;
	private static int ARRAY_ID = 0;

	public static Label createIntegerConstantLabel(int number) {
		String numberStr = String.valueOf(number);
		StringBuilder result = new StringBuilder("");
		for(int i = 0; i < numberStr.length() ; i++){   // while counting characters if less than the length add one
			char character = numberStr.charAt(i); // start on the first character
			result.append((int) character);
		}
		INTEGER_CONSTANT_ID++;
		return new Label("INTEGER_LABEL" + result + INTEGER_CONSTANT_ID);
	}

	public static Label createDoubleConstantLabel(double number) {
		String numberStr = String.valueOf(number);
		StringBuilder result = new StringBuilder("");
		for(int i = 0; i < numberStr.length() ; i++){   // while counting characters if less than the length add one
			char character = numberStr.charAt(i); // start on the first character
			result.append((int) character);
		}
		DOUBLE_CONSTANT_ID++;
		return new Label("DOUBLE_LABEL" + result + DOUBLE_CONSTANT_ID);
	}

	public static Label createBooleanConstantLabel(boolean booli) {
		BOOLEAN_CONSTANT_ID++;
		return new Label("BOOLEAN_LABEL" + booli + BOOLEAN_CONSTANT_ID);
	}

	public static Label createStringConstantLabel(String str) {
		StringBuilder result = new StringBuilder("");
		for(int i = 0; i < str.length() ; i++){   // while counting characters if less than the length add one
			char character = str.charAt(i); // start on the first character
			result.append((int) character);
		}
		STRING_CONSTANT_ID++;
		return new Label("STRING_LABEL" + result + STRING_CONSTANT_ID);
	}

	public static Label createArrayLabel() {
		ARRAY_ID++;
		return new Label("ARRAY_LABEL" + ARRAY_ID);
	}
}
