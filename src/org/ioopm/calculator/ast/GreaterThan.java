package org.ioopm.calculator.ast;

public class GreaterThan extends Operator{
    public GreaterThan(SymbolicExpression a, SymbolicExpression b){
        super(a, b);
    }

    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

}