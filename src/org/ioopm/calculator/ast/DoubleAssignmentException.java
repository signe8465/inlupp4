package org.ioopm.calculator.ast;

public class DoubleAssignmentException extends RuntimeException {
    public DoubleAssignmentException() {
        super("Error! Du kan inte assigna samma variabel två gånger i ett anrop!");
    }
}