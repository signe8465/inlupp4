package org.ioopm.calculator.ast;

public class Stack{
    private Scope topScope;
    private Scope baseScope;
    public Stack(Environment vars){
        Scope tmp = new Scope();
        tmp.setEnvironment(vars);
        baseScope = tmp;
        topScope = baseScope;
    }

    public Environment pop(){
        if(topScope != null && topScope != baseScope){
            topScope = topScope.nextScope();
        }
        return getEnvironment();
    }

    public Environment push(Environment en){
        Scope tmp = new Scope();
        tmp.setEnvironment(en);
        tmp.setNext(topScope);
        this.topScope = tmp;
        return getEnvironment();
    }

    public Environment getEnvironment(){
        return this.topScope.getEnvironment();
    }
}