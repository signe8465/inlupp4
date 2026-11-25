package org.ioopm.calculator.ast;

public class Conditional extends SymbolicExpression{
    private SymbolicExpression op;
    private SymbolicExpression IfScope;
    private SymbolicExpression ElseScope;
    public Conditional(SymbolicExpression op, SymbolicExpression Scope1, SymbolicExpression Scope2){
        this.op = op;
        this.IfScope = Scope1;
        this.ElseScope = Scope2;
    }

    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

    public SymbolicExpression getIfScope(){
        return this.IfScope; 
    }

    public SymbolicExpression getElseScope(){
        return this.ElseScope; 
    }

    public SymbolicExpression getOp(){
        return this.op; 
    }

}