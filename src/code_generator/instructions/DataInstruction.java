package code_generator.instructions;

public class DataInstruction implements MipsLine{

    private String directive;
    private int numberOfBytes;

    // Declaring .data instructions
    public DataInstruction(String directive, int numberOfBytes) {
        this.directive = directive;
        this.numberOfBytes = numberOfBytes;
    }

    @Override
    public String toString() {
        return directive + " " + numberOfBytes;
    }
}
