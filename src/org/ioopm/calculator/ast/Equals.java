package org.ioopm.calculator.ast;

public class Equals extends Operator{
    public Equals(SymbolicExpression a, SymbolicExpression b){
        super(a, b);
    }
    
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
}