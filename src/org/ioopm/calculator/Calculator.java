package org.ioopm.calculator;
import org.ioopm.calculator.ast.*;
import org.ioopm.calculator.parser.*;
import java.util.Scanner;

public class Calculator {
    public static void main(String [] args){
        Scanner sc = new Scanner(System.in);
        final CalculatorParser parser = new CalculatorParser();
        final EvaluationVisitor evaluator = new EvaluationVisitor();
        Environment vars = new Environment();
        SymbolicExpression ans = new Variable("ans");
        boolean run = true;
        System.out.print("\033[2J\033[;H");
        System.out.println("Welcome to the Calculator!");
        while(run){
            System.out.print("Please enter an expression: ");
            String input = sc.nextLine();
            try{
                final SymbolicExpression expression = parser.parse(input,vars);
                if(expression.isCommand()){
                    if(expression instanceof Quit){
                        run = false;
                    } else if(expression instanceof Clear){
                        vars.clear();
                    } else if(expression instanceof Vars){
                        System.out.println(vars.toString());
                    }
                } else{
                    final SymbolicExpression evaluationResult = evaluator.evaluate(expression, vars);
                    if(evaluationResult != null){
                        new Assignment(evaluationResult, ans);
                        System.out.println(evaluationResult);
                    }
                }
            }catch(Exception e){
                System.out.println(e);
            }
        }

    }
}