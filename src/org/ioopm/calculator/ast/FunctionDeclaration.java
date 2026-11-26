package org.ioopm.calculator.ast; 

public class FunctionDeclaration extends SymbolicExpression{
    private SymbolicExpression key;
    private SymbolicExpression FunctionRows;

    public FunctionDeclaration(SymbolicExpression key, List ParameterList){
        this.key = key;
        this.FunctionRows = new Function(key, ParameterList);
    }

    public void addRow(SymbolicExpression r){
        FunctionRows.addRow(r);
    }

    public SymbolicExpression getKey(){
        return this.key;
    }

    public SymbolicExpression getFunction(){
        return this.FunctionRows;
    }

    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
}