package org.ioopm.calculator.ast;

public class ScopeStop extends Function{
    public ScopeStop(){

    }
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

    public String getName() {
        return "}";
    }

    public boolean equals(Object other) {
        if (other instanceof ScopeStop) {
            return true;
        } else {
            return false;
        }
    }

}