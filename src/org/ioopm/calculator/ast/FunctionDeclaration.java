package org.ioopm.calculator.ast; 

public class FunctionDeclaration extends SymbolicExpression{
    private SymbolicExpression key;
    private Function FunctionStorage;

    public FunctionDeclaration(SymbolicExpression key, Listan ParameterListan){
        this.key = key;
        this.FunctionStorage = new Function(key, ParameterListan);
    }

    public void addRow(SymbolicExpression r){
        FunctionStorage.addRow(r);
    }

    public SymbolicExpression getKey(){
        return this.key;
    }

    public Function getFunction(){
        return this.FunctionStorage;
    }

    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

}