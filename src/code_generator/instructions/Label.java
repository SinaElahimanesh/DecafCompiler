package code_generator.instructions;

public class Label implements MipsLine {
	String name;

	/**
	 * This method should only be used in LabelMaker in order to prevent bad labeling.
	 * @param name The name of the label.
	 */
	protected Label(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name + ':';
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
