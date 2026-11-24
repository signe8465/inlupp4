package org.ioopm.calculator.ast;

public class Scope{
    private Environment vars;
    private Scope nextScope = null;
    private List list = null;
    public Scope(){
        vars = new Environment();
    }

    public Environment getEnvironment(){
        return this.vars;
    }

    public Scope nextScope(){
        return this.nextScope;
    }

    public void setNext(Scope s){
        this.nextScope = s;
    }
    
    public void setEnvironment(Environment en){
        this.vars = en;
    }

    public void setList(List l){
        this.list = l;
    }

    public List getVariableList(){
        return this.list;
    }
}