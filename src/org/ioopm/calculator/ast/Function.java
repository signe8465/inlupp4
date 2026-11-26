package org.ioopm.calculator.ast; 

public class Function extends SymbolicExpression{
    Listan FunctionRowListan;
    SymbolicExpression name;
    Listan ParamListan;

    public Function(SymbolicExpression name, Listan ParamListan){
        this.FunctionRowListan = new Listan();
        this.ParamListan = ParamListan;
        this.name = name;
    }

    public void addRow(SymbolicExpression row){
        FunctionRowListan.addNode(row);
    }

    public Listan getParamListan(){
        return this.ParamListan;
    }

    public Listan getFunctionRowListan(){
        return this.FunctionRowListan;
    }
    
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }


}