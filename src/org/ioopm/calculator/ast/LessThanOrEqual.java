package org.ioopm.calculator.ast;

public class LessThanOrEqual extends Operator{
    public LessThanOrEqual(SymbolicExpression a, SymbolicExpression b){
        super(a, b);
    }

    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
    
}