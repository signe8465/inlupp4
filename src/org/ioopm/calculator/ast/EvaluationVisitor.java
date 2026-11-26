    package org.ioopm.calculator.ast;

    public class EvaluationVisitor implements Visitor {
        private Environment env = null;
        private Stack scopeStack;
        private String errorText = "";
        private boolean err = false;
        private Listan variableListan = null;
        public SymbolicExpression evaluate(SymbolicExpression expression, Environment vars) {
            variableListan = new Listan();
            this.scopeStack = new Stack(vars, variableListan);
            this.env = scopeStack.getEnvironment();
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
            throw new RuntimeException("Evaluation failed: " + e.getMessage(), e);
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
            Scope i = scopeStack.getTopScope();
            while(result == null && i.nextScope() != null){
                result = i.nextScope().getEnvironment().get(new Variable(n.toString()));
                i = i.nextScope();
            }
            // kolla om det är en konstant eller funktion
            if(result != null){
                if(result.isConstant()){
                    return result;
                }else{
                    return result.accept(this);
                }
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
                scopeStack.getVariableListan().addNode(rhs);
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

        public SymbolicExpression visit(End n){
            return n;
        }

        public SymbolicExpression visit(ScopeStart n){
            this.env = this.scopeStack.push(new Environment());
            SymbolicExpression result = n.getArgument().accept(this);
            this.env = this.scopeStack.pop();
            return result;
        }

        public SymbolicExpression visit(Equals n){
            SymbolicExpression lhs = n.getLhs();
            SymbolicExpression rhs = n.getRhs();
            if(!(n.getLhs().isConstant())){
                lhs = lhs.accept(this);
            }
            if(!(n.getRhs().isConstant())){
                rhs = rhs.accept(this);
            }
            
            if(lhs.equals(rhs)){
                return new Constant(1);
            } else{
                return new Constant(0);
            }
        }

        public SymbolicExpression visit( GreaterThanOrEqual n){
            SymbolicExpression lhs = n.getLhs();
            SymbolicExpression rhs = n.getRhs();
            if(!(n.getLhs().isConstant())){
                lhs = lhs.accept(this);
            }
            if(!(n.getRhs().isConstant())){
                rhs = rhs.accept(this);
            }
            if (lhs.isConstant() && rhs.isConstant()) {
                if(lhs.getValue() >= rhs.getValue()){
                    return new Constant(1);
                } else{
                    return new Constant(0);
            }
            } else {
                throw new IllegalComparisonException();
            }
            
        }

        public SymbolicExpression visit(LessThanOrEqual n){
            SymbolicExpression lhs = n.getLhs();
            SymbolicExpression rhs = n.getRhs();
            if(!(n.getLhs().isConstant())){
                lhs = lhs.accept(this);
            }
            if(!(n.getRhs().isConstant())){
                rhs = rhs.accept(this);
            }
            if (lhs.isConstant() && rhs.isConstant()) {
                if(lhs.getValue() <= rhs.getValue()){
                    return new Constant(1);
                } else{
                    return new Constant(0);
            }
            } else {
                throw new IllegalComparisonException();
            }
        }

        public SymbolicExpression visit(GreaterThan n){
            SymbolicExpression lhs = n.getLhs();
            SymbolicExpression rhs = n.getRhs();
            if(!(n.getLhs().isConstant())){
                lhs = lhs.accept(this);
            }
            if(!(n.getRhs().isConstant())){
                rhs = rhs.accept(this);
            }

            
            if (lhs.isConstant() && rhs.isConstant()) {
                if(lhs.getValue() > rhs.getValue()){
                    return new Constant(1);
                } else{
                    return new Constant(0);
            }
            } else {
                throw new IllegalComparisonException();
            }
        }

        public SymbolicExpression visit(LessThan n){
            SymbolicExpression lhs = n.getLhs();
            SymbolicExpression rhs = n.getRhs();
            if(!(n.getLhs().isConstant())){
                lhs = lhs.accept(this);
            }
            if(!(n.getRhs().isConstant())){
                rhs = rhs.accept(this);
            }
            
            if (lhs.isConstant() && rhs.isConstant()) {
                if(lhs.getValue() < rhs.getValue()){
                    return new Constant(1);
                } else{
                    return new Constant(0);
            }
            } else {
                throw new IllegalComparisonException();
            }
        }

        public SymbolicExpression visit(Conditional n){
            SymbolicExpression result = n.getOp().accept(this);
            if(result.getValue() == 1){
                return n.getIfScope().accept(this);
            }else{
                return n.getElseScope().accept(this);
            }
        }

        public SymbolicExpression visit(FunctionDeclaration n){ 
            SymbolicExpression key = n.getKey();
            Function func = n.getFunction();
            if(key.isNamedConstant()){
                errorText += "\n" + n;
                err = true;
            }
            if(!err){
                scopeStack.getVariableListan().addNode(key);//Kan finnas fel med lagringslogiken här, känns fel men tror det funkar?
                this.env.put((Variable) key, func); // stoppa in ett funktionsobjekt som innehåller allt
            }
            return new Variable(" ");
        }

        public SymbolicExpression visit(Function n){
            return n;
        }

        public SymbolicExpression visit(FunctionStart n){
            this.env = this.scopeStack.push(new Environment());
            SymbolicExpression Result = new Variable(" ");
            SymbolicExpression func = n.getKey().accept(this);
            Listan Functions = func.getFunctionRowListan();
            if(func.isConstant()){
                throw new FunctionError("Hittade en konstant istället för en funktion i hashtabellen");
            }
            DeclareVariables(func.getParamListan(), n.ConstantListan);

            Node Iterator = Functions.getFirstNode();
            while(Iterator != null){
                Result = Iterator.getValue().accept(this);
                Iterator = Iterator.getNext();
            }
            this.env = this.scopeStack.pop();
            return Result;
        }

        public void DeclareVariables(Listan ParamListan, Listan ConstantListan) throws FunctionError{
            Node iteratorP = ParamListan.getFirstNode();
            Node iteratorC = ConstantListan.getFirstNode();
            while(iteratorP != null && iteratorC != null){
                SymbolicExpression v = iteratorP.getValue();
                SymbolicExpression c = iteratorC.getValue();
                SymbolicExpression row = new Assignment(c, v);
                row.accept(this);
                iteratorP = iteratorP.getNext();
                iteratorC = iteratorC.getNext();
            }
            if(iteratorP != null){
                throw new FunctionError("För få argument");
            }
            if(iteratorC != null){
                throw new FunctionError("För många argument");
            }
        }
    }