package org.ioopm.calculator.ast;

public abstract class Operator extends Binary{ 

    public Operator(SymbolicExpression a, SymbolicExpression b){
        super(a,b);
    }
    
}