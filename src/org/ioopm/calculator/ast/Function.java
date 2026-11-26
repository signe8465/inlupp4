package org.ioopm.calculator.ast; 

public class Function extends SymbolicExpression{
    List FunctionRowList;
    SymbolicExpression name;
    List ParamList;

    public Function(SymbolicExpression name, List ParamList){
        this.FunctionRowList = new List();
        this.ParamList = ParamList;
        this.name = name;
    }

    public void addRow(SymbolicExpression row){
        FunctionRowList.addNode(row);
    }

    public List getParamList(){
        return this.ParamList;
    }

    public List getFunctionRowList(){
        return this.FunctionRowList;
    }
    
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }


}