package org.ioopm.calculator.ast;

public class Node{
    private SymbolicExpression Value;
    private Node next;
    public Node(SymbolicExpression v){
        this.Value = v;
    }

    public SymbolicExpression getValue(){
        return this.Value;
    }

    public Node getNext(){
        return this.next;
    }

    public void setNext(Node n){
        this.next = n;
    }
    
}