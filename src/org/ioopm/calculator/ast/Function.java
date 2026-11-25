package org.ioopm.calculator.ast; 

public abstract class Function extends SymbolicExpression{
    List FunctionRowList;
    public Function(List ParamList){
        this.FunctionRowList = new List();
        prependParams(ParamList);
    }

    private void prependParams(List ParamList){
        SymbolicExpression row = ParamList.getValue();
        //TODO SÅ ATT DEN FAKTISKT GÖR NÅGOT
    }

    public void addRow(SymbolicExpression row){
        FunctionRowList.addNode(row);
    }
}