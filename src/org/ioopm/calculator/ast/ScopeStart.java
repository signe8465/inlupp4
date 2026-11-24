package org.ioopm.calculator.ast;

public class ScopeStart extends Unary{
    public ScopeStart(SymbolicExpression a){
        super(a);
    }

    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

    public String getName() {
        return "{" + getArgument().toString() + "}";
    }

    public boolean equals(Object other) {
        if (other instanceof ScopeStart) {
            return this.equals((ScopeStart) other);
        } else {
            return false;
        }
    }

    public boolean equals(ScopeStart other) {
        return this.getArgument().equals(other.getArgument());
    }
}