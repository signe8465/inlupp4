package org.ioopm.calculator.ast;

public class Sin extends Unary{
    public Sin(SymbolicExpression a){
        super(a);
    }

    public String getName() {
        return "sin";
    }

    public boolean equals(Object other) {
        if (other instanceof Sin) {
            return this.equals((Sin) other);
        } else {
            return false;
        }
    }

    public boolean equals(Sin other) {
        return this.getArgument().equals(other.getArgument());
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
/*
    public SymbolicExpression eval(Environment vars) {
        SymbolicExpression arg = this.getArgument().eval(vars);
        if (arg.isConstant()) {
            return new Constant(Math.sin(arg.getValue()));
        } else {
            return new Sin(arg);
        }
    }
*/
}