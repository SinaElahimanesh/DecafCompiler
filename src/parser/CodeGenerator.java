package parser;

import code_generator.SemanticException;

public interface CodeGenerator {
    void doSemantic(String sem, Action action) throws Throwable;
}