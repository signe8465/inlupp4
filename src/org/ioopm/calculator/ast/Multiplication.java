package org.ioopm.calculator.ast;

public class Multiplication extends Binary{
    public Multiplication(SymbolicExpression a, SymbolicExpression b){
        super(a, b);
    }

    public String getName() {
        return "*";
    }

    public int getPriority(){
        return 40;
    }

    public boolean equals(Object other) {
        if (other instanceof Multiplication) {
            return this.equals((Multiplication) other);
        } else {
            return false;
        }
    }

    public boolean equals(Multiplication other) {
        return (this.getLhs().equals(other.getLhs()) || this.getLhs().equals(other.getRhs())) &&  (this.getRhs().equals(other.getLhs()) || this.getRhs().equals(other.getRhs()));
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

/*
    public SymbolicExpression eval(Environment vars) {
        SymbolicExpression lhs = this.getLhs().eval(vars);
        SymbolicExpression rhs = this.getRhs().eval(vars);
        if (lhs.isConstant() && rhs.isConstant()) {
            return new Constant(lhs.getValue() * rhs.getValue());
        } else {
            return new Multiplication(lhs, rhs);
        }
    }
*/
}