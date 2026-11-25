package org.ioopm.calculator.ast; 

public class FunctionDeclaration extends SymbolicExpression{
    private SymbolicExpression key;
    private List ParameterList; 
    private SymbolicExpression FunctionRows;

    public SymbolicExpression FunctionDeclaration(SymbolicExpression key, List ParameterList){
        this.key = key;
        this.ParameterList = ParameterList;
        this.FunctionRows = new Function(ParameterList);
    }

    public void addRow(SymbolicExpression r){
        FunctionRows.addRow(r);
    }

    public SymbolicExpression getKey(){
        return this.key;
    }
}