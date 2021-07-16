package code_generator.symbol_table.symbols;

import code_generator.symbol_table.Field;
import code_generator.symbol_table.Function;
import code_generator.symbol_table.Variable;

import java.util.ArrayList;

public class Symbol {
    String name;
    ArrayList<Field> fields;
    Integer size;

    public Symbol(String name) {
        this.name = name;
    }

    public void addField(Function method) throws NoSuchFieldException {
        Field field = new Field(method);
        fields.add(field);
        size += field.getSize();
    }

    public void addField(Variable variable) throws NoSuchFieldException {
        Field field = new Field(variable);
        fields.add(field);
        size += field.getSize();
    }

    public String getName() {
        return name;
    }

    /**
     * These fields should not be modified (neither adding or removing element is prohibietd,
     * but I'm tired to implement it, just DON'T).
     * @return List of symbol's fields
     */
    public ArrayList<Field> getFields() {
        return fields;
    }

    public Integer getSize() {
        return size;
    }
}