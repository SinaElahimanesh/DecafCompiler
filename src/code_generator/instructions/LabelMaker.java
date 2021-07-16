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
}
