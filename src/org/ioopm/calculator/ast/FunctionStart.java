package org.ioopm.calculator.ast; 

public class FunctionStart extends SymbolicExpression{
    SymbolicExpression key;
    List ConstantList;
    public FunctionStart(SymbolicExpression key, List ConstantList){
        this.key = key;
        this.ConstantList = ConstantList;
    }

    public List prependParams(List ParamList, List FunctionRowList) throws FunctionError{
        Node iteratorP = ParamList.getFirstNode();
        Node iteratorC = this.ConstantList.getFirstNode();
        while(iteratorP != null && iteratorC != null){
            SymbolicExpression v = iteratorP.getValue();
            SymbolicExpression c = iteratorC.getValue();
            SymbolicExpression row = new Assignment(c, v);
            FunctionRowList.prepend(row);

            iteratorP = iteratorP.getNext();
            iteratorC = iteratorC.getNext();
        }
        if(iteratorP != null){
            throw new FunctionError("För få argument");
        }
        if(iteratorC != null){
            throw new FunctionError("För många argument");
        }
        return FunctionRowList;
    }

    public SymbolicExpression getKey(){
        return this.key;
    }
    
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

}