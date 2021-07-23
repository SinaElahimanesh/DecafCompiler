package code_generator.instructions;

import code_generator.operand.Operand;

public class Label implements MipsLine {
	String name;

	/**
	 * This method should only be used in LabelMaker in order to prevent bad labeling.
	 * @param name The name of the label.
	 */
	public Label(String name) {
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

	public String getType() {
		return name.substring(0, name.indexOf('_'));
	}

	public int getSize() {
		return Integer.parseInt(name.substring(name.indexOf('_'), name.indexOf("_constant")));
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Label))
			return false;
		return ((Label) obj).getName().equals(getName());
	}
}
