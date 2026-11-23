package org.ioopm.calculator.ast;

public class Vars extends Command{
    private static final Vars theInstance = new Vars();
        private Vars() {}
        public static Vars instance() {
            return theInstance;
        }

        public boolean run(Environment vars){
            System.out.println(vars.toString());
            return true;
        } 

        public SymbolicExpression accept(Visitor v) {
            return v.visit(this);
        }
}