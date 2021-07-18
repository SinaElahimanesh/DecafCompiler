package code_generator.stack;

import code_generator.operand.Indirect;
import code_generator.operand.Register;

public class TemporaryMemoryBank {
	public Integer size = 0;

	public Indirect allocateTemporaryMemory(Integer size) {
		this.size += size;
		return new Indirect(this.size - size, new Register("sp"));
	}

	public void resetMemory() {
		size = 0;
	}
}
