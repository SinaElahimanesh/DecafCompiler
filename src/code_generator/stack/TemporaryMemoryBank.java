package code_generator.stack;

import code_generator.operand.Indirect;
import code_generator.operand.Register;

public class TemporaryMemoryBank {
	public static Integer size = 0;

	public static Indirect allocateTemporaryMemory(Integer size) {
		TemporaryMemoryBank.size += size;
		return new Indirect(TemporaryMemoryBank.size - size, new Register("sp"));
	}

	public static void resetMemory() {
		size = 0;
	}
}
