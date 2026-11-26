package org.ioopm.calculator.ast; 

public class FunctionStart extends SymbolicExpression{
    SymbolicExpression key;
    Listan ConstantListan;
    public FunctionStart(SymbolicExpression key, Listan ConstantListan){
        this.key = key;
        this.ConstantListan = ConstantListan;
    }

    public Listan prependParams(Listan ParamListan, Listan FunctionRowListan) throws FunctionError{
        Node iteratorP = ParamListan.getFirstNode();
        Node iteratorC = this.ConstantListan.getFirstNode();
        while(iteratorP != null && iteratorC != null){
            SymbolicExpression v = iteratorP.getValue();
            SymbolicExpression c = iteratorC.getValue();
            SymbolicExpression row = new Assignment(c, v);
            FunctionRowListan.prepend(row);

            iteratorP = iteratorP.getNext();
            iteratorC = iteratorC.getNext();
        }
        if(iteratorP != null){
            throw new FunctionError("För få argument");
        }
        if(iteratorC != null){
            throw new FunctionError("För många argument");
        }
        return FunctionRowListan;
    }

    public SymbolicExpression getKey(){
        return this.key;
    }
    
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

}