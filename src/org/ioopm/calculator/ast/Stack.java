package org.ioopm.calculator.ast;

public class Stack{
    private Scope topScope;
    private Scope baseScope;

    public Stack(Environment vars, Listan l){
        Scope tmp = new Scope();
        tmp.setEnvironment(vars);
        tmp.setListan(l);
        baseScope = tmp;
        topScope = baseScope;
    }

    public Environment pop(){
        if(topScope != baseScope){
            topScope = topScope.nextScope();
        }
        return this.getEnvironment();
    }

    public Environment push(Environment en){
        Scope tmp = new Scope();
        tmp.setEnvironment(en);
        tmp.setNext(topScope);
        tmp.setListan(new Listan());
        this.topScope = tmp;
        return this.getEnvironment();
    }

    public Environment getEnvironment(){
        return this.topScope.getEnvironment();
    }

    public Listan getVariableListan(){
        return this.topScope.getVariableListan();
    }

    public Scope getTopScope(){
        return this.topScope;
    }

}