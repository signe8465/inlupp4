package org.ioopm.calculator.ast;

public class LessThan extends Operator{
    public LessThan(SymbolicExpression a, SymbolicExpression b){
        super(a, b);
    }
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
}