package org.ioopm.calculator.ast;

public class FunctionError extends RuntimeException {
    public FunctionError(String a) {
        super(a);
    }
}