package org.ioopm.calculator.ast;

public class IllegalComparisonException extends RuntimeException {
    public IllegalComparisonException() {
        super("Error! Illegal use of comparison!");
    }
}