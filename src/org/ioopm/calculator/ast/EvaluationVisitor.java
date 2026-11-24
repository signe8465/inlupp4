    package org.ioopm.calculator.ast;

    public class EvaluationVisitor implements Visitor {
        private Environment env = null;
        private Stack scopeStack;
        private String errorText = "";
        private boolean err = false;
        private List variableList = null;
        public SymbolicExpression evaluate(SymbolicExpression expression, Stack scopeStack) {
            this.scopeStack = scopeStack;
            this.env = this.scopeStack.getEnvironment();
            variableList = new List();
            SymbolicExpression tmp = null;
            try{
                tmp = expression.accept(this);
                if(err){
                    failed();
                }
            } catch(Exception e){
                errorCheck(e);
                err = false;
            }
            return tmp;
        }

        private void failed(){
            throw new IllegalAssignmentException();
        }
        private void errorCheck(Exception e){
            System.out.println( e + errorText );
            errorText = "";
        }
        // This method gets called from Addition.accept(Visitor v) -- you should
        // be able to see from the eval() methods how these should behave (i.e., 
        // compare this method with your Addition::eval() and Symbolic.addition) 
        
        public SymbolicExpression visit(Addition n) {
            // Visit the left hand side and right hand side subexpressions
            SymbolicExpression left = n.getLhs().accept(this);
            SymbolicExpression right = n.getRhs().accept(this);
            // When we come back here, the visitor has visited all subexpressions, 
            // meaning left and right point to newly created trees reduced to 
            // the extent possible (best case -- both are constants)

            // If subexpressions are fully evaluated, replace them in
            // the tree with a constant whose value is the sub of the
            // subexpressions, if not, simply construct a new addition
            // node from the new subexpressions
            if (left.isConstant() && right.isConstant()) {
                return new Constant(left.getValue() + right.getValue());
            } else {
                return new Addition(left, right);
            }
        }

        
        public SymbolicExpression visit(Subtraction n) {

            SymbolicExpression left = n.getLhs().accept(this);
            SymbolicExpression right = n.getRhs().accept(this);

            if (left.isConstant() && right.isConstant()) {
                return new Constant(left.getValue() - right.getValue());
            } else {
                return new Subtraction(left, right);
            }
        }

        
        public SymbolicExpression visit(Multiplication n) {

            SymbolicExpression left = n.getLhs().accept(this);
            SymbolicExpression right = n.getRhs().accept(this);

            if (left.isConstant() && right.isConstant()) {
                return new Constant(left.getValue() * right.getValue());
            } else {
                return new Multiplication(left, right);
            }
        }

        
        public SymbolicExpression visit(Division n) {

            SymbolicExpression left = n.getLhs().accept(this);
            SymbolicExpression right = n.getRhs().accept(this);

            if (left.isConstant() && right.isConstant()) {
                return new Constant(left.getValue() / right.getValue());
            } else {
                return new Division(left, right);
            }
        }

        
        public SymbolicExpression visit(Cos n) {

            SymbolicExpression val = n.getArgument().accept(this);

            if (val.isConstant()) {
                return new Constant(Math.cos(val.getValue()));
            } else {
                return new Cos(val);
            }
        }

        
        public SymbolicExpression visit(Sin n) {
            SymbolicExpression val = n.getArgument().accept(this);

            if (val.isConstant()) {
                return new Constant(Math.sin(val.getValue()));
            } else {
                return new Sin(val);
            }
        }

        
        public SymbolicExpression visit(Exp n) {

            SymbolicExpression val = n.getArgument().accept(this);

            if (val.isConstant()) {
                return new Constant(Math.exp(val.getValue()));
            } else {
                return new Exp(val);
            }
        }

        
        public SymbolicExpression visit(Log n) {

            SymbolicExpression val = n.getArgument().accept(this);

            if (val.isConstant()) {
                return new Constant(Math.log(val.getValue()));
            } else {
                return new Log(val);
            }
        }

        
        public SymbolicExpression visit(Negation n) {

            SymbolicExpression val = n.getArgument().accept(this);

            if (val.isConstant()) {
                return new Constant(-1 * (val.getValue()));
            } else {
                return new Negation(val);
            }
        }

        
        public SymbolicExpression visit(Variable n){

            SymbolicExpression result = this.env.get(new Variable(n.toString()));

            if(result != null){
                return result;
            } else {
                return new Variable(n.toString()); 
            }
        }

        public SymbolicExpression visit(Constant n) {
            if (n.isConstant()) {
                return new Constant(n.getValue());
            } else {
                return new Negation(n);
            }
        }

        
        public SymbolicExpression visit(Assignment n){ //Check inte implementerat under, tror vi har all logik i visit.
            SymbolicExpression rhs = n.getRhs();
            SymbolicExpression lhs = n.getLhs().accept(this);
            if(rhs.isNamedConstant()){
                errorText += "\n" + n;
                err = true;
            }
            if(!err){
                variableList.addNode(rhs);
                this.env.put((Variable) rhs, lhs);
            }
            return lhs;

        }

        public SymbolicExpression visit(NamedConstant n){
            return new Constant(n.getValue());
        }

        public SymbolicExpression visit(SymbolicExpression n){
            throw new RuntimeException();
        }

        public SymbolicExpression visit(Quit n){
            return n;
        }

        public SymbolicExpression visit(Vars n){
            return n;
        }

        public SymbolicExpression visit(Clear n){
            return n;
        }

        public SymbolicExpression visit(ScopeStart n){
            this.env = this.scopeStack.push(new Environment());
            SymbolicExpression result = n.getArgument().accept(this);
            return result;
        }

        public SymbolicExpression visit(ScopeStop n){
            this.env = this.scopeStack.pop();
            return n;
        }

    }