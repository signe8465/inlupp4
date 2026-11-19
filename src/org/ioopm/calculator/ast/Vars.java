package org.ioopm.calculator.ast;

public class Vars extends Command{
    private static final Vars theInstance = new Vars();
        private Vars() {}
        public static Vars instance() {
            return theInstance;
        }

        public boolean run(Environment vars){
            vars.toString();
            return true;
        } 
}