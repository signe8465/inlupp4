package org.ioopm.calculator.ast;

public class NamedConstant extends Atom{
    private String name;
    private double value;


    public NamedConstant(String name, double value){
        this.value = value;
        this.name = name;
    }
        
    public boolean isNamedConstant(){
        return true;
    }

    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
    
    public double getValue(){
        return this.value;
    }

    public String toString() {
        return this.name;
    }
/*
    public SymbolicExpression eval(Environment vars){
        return new Constant(this.value);
    }
*/
}