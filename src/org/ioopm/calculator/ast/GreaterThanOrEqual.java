package org.ioopm.calculator.ast;

public class  GreaterThanOrEqual extends Operator{
    public  GreaterThanOrEqual(SymbolicExpression a, SymbolicExpression b){
        super(a, b);
    }
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
}