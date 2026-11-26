package org.ioopm.calculator.ast;

public class Listan{
    private Node first = null;
    private Node last = null;
    public Listan(){

    }

    public void addNode(SymbolicExpression s){
        Node n = new Node(s);
        if(this.first == null){
            this.first = n;
            this.last = n;
        } else{
            Node current = this.first;
            while(current != null){
                if(n.getValue().equals(current.getValue())){
                    throw new DoubleAssignmentException();
                }
                current = current.getNext();
            }
            this.last.setNext(n);
            this.last = n;
        }
    }

    public void clear(){
        this.first = null;
        this.last = null;
    }

    public SymbolicExpression getValue(){
        return this.first.getValue();
    }

    public Node getFirstNode(){
        return this.first;
    }

    public void prepend(SymbolicExpression a){
        Node n = new Node(a);
        if(this.first == null){
            this.first = n;
            this.last = n;
        } else{
            Node current = this.first;
            while(current != null){
                if(n.getValue().equals(current.getValue())){
                    throw new DoubleAssignmentException();
                }
                current = current.getNext();
            }
            n.setNext(this.first);
            this.first = n;
        }
    }
}