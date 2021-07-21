package code_review.symbol_table;

/**
 * A union of Variable and Function.
 * @see Variable
 * @see Function
 */
public class Field {
	Variable variable;
	Function function;
	FieldType fieldType;

	public Field(Variable variable) {
		this.variable = variable;
		fieldType = FieldType.VARIABLE;
	}

	public Field(Function function) {
		this.function = function;
		fieldType = FieldType.FUNCTION;
	}

	public Variable getVariable() {
		return variable;
	}

	public Function getFunction() {
		return function;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public Integer getSize() throws NoSuchFieldException {
		switch (fieldType) {
			case FUNCTION:
				return 0;
			case VARIABLE:
				return variable.getSize();
			default:
				throw new NoSuchFieldException("Field.getSize: fieldType hasn't been declared.");
		}
	}
}
