package org.ioopm.calculator.ast;

public class EvaluationVisitor implements Visitor {
    private Environment env = null;

    public SymbolicExpression evaluate(SymbolicExpression topLevel, Environment env) {
        this.env = env;
        return topLevel.accept(this);
    }

    // This method gets called from Addition.accept(Visitor v) -- you should
    // be able to see from the eval() methods how these should behave (i.e., 
    // compare this method with your Addition::eval() and Symbolic.addition) 
    @Override
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

    @Override
    public SymbolicExpression visit(Subtraction n) {

        SymbolicExpression left = n.getLhs().accept(this);
        SymbolicExpression right = n.getRhs().accept(this);

        if (left.isConstant() && right.isConstant()) {
            return new Constant(left.getValue() - right.getValue());
        } else {
            return new Subtraction(left, right);
        }
    }

    @Override
    public SymbolicExpression visit(Multiplication n) {

        SymbolicExpression left = n.getLhs().accept(this);
        SymbolicExpression right = n.getRhs().accept(this);

        if (left.isConstant() && right.isConstant()) {
            return new Constant(left.getValue() * right.getValue());
        } else {
            return new Multiplication(left, right);
        }
    }

    @Override
    public SymbolicExpression visit(Division n) {

        SymbolicExpression left = n.getLhs().accept(this);
        SymbolicExpression right = n.getRhs().accept(this);

        if (left.isConstant() && right.isConstant()) {
            return new Constant(left.getValue() / right.getValue());
        } else {
            return new Division(left, right);
        }
    }

    @Override
    public SymbolicExpression visit(Cos n) {

        SymbolicExpression val = n.getArgument().accept(this);

        if (val.isConstant()) {
            return new Constant(Math.cos(val.getValue));
        } else {
            return new Cos(val);
        }
    }

    @Override
    public SymbolicExpression visit(Sin n) {
        SymbolicExpression val = n.getArgument().accept(this);

        if (val.isConstant()) {
            return new Constant(Math.sin(val));
        } else {
            return new Sin(val);
        }
    }

    @Override
    public SymbolicExpression visit(Exp n) {

        SymbolicExpression val = n.getArgument().accept(this);

        if (val.isConstant()) {
            return new Constant(Math.exp(val));
        } else {
            return new Exp(val);
        }
    }

    @Override
    public SymbolicExpression visit(Log n) {

        SymbolicExpression val = n.getArgument().accept(this);

        if (val.isConstant()) {
            return new Constant(Math.log(val.getValue()));
        } else {
            return new Log(val);
        }
    }

    @Override
    public SymbolicExpression visit(Neg n) {

        SymbolicExpression val = n.getArgument().accept(this);

        if (val.isConstant()) {
            return new Constant(-1 * (val.getValue()));;
        } else {
            return new Neg(val);
        }
    }

    @Override
    public SymbolicExpression visit(Variable n){

        SymbolicExpression var = n.accept(this);
        SymbolicExpression result = this.env.get(new Variable(n.toString()));

        if(result != null){
            return result;
        } else {
            return new Variable(n.toString()); 
        }
    }

    @Override
    public SymbolicExpression visit(Constant n) {

        SymbolicExpression val = n.accept(this);

        if (val.isConstant()) {
            return new Constant(n.getValue());
        } else {
            return new Neg(val);
        }
    }

    @Override
    public SymbolicExpression visit(Assignment n){
        SymbolicExpression rhs = n.getRhs().accept(this);
        SymbolicExpression lhs = n.getLhs().accept(this);
        if(!(rhs instanceof Variable)){
            throw new IllegalAssignmentException();
        }
        this.env.put((Variable) rhs, lhs);
        return lhs;

    }
    // We have not implemented Clear, Quit and Vars - README why
}