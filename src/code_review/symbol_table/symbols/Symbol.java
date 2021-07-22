package code_review.symbol_table.symbols;

import code_review.symbol_table.Field;
import code_review.symbol_table.Function;
import code_review.symbol_table.Variable;

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

    public Integer getVariableAddress(String name) throws NoSuchFieldException {
        Integer address = 0;

        for (Field field: fields) {
            if (field.getVariable() != null) {
                if (field.getVariable().getName().equals(name))
                    return address;

                address += field.getSize();
            }
        }

        throw new NoSuchFieldException("Variable " + name + " not found in class " + this.name);
    }

    public Variable getVariable(String name) throws NoSuchFieldException {
        for (Field field: fields)
            if (field.getVariable() != null)
                if (field.getVariable().getName().equals(name))
                    return field.getVariable();

        throw new NoSuchFieldException("Variable " + name + " not found in class " + this.name);
    }

    public Function getMethod(String name) throws NoSuchFieldException {
        for (Field field: fields)
            if (field.getFunction() != null)
                if (field.getFunction().getName().equals(name))
                    return field.getFunction();

        throw new NoSuchFieldException("Method " + name + " not found in class " + this.name);
    }

    public Integer getSize() {
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Symbol)) return false;
        return ((Symbol) obj).getName().equals(getName());
    }
}