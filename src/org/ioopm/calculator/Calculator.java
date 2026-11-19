package org.ioopm.calculator;
import org.ioopm.calculator.ast.*;
import org.ioopm.calculator.parser.*;
import java.util.Scanner;

public class Calculator {
    public static void main(String [] args){
        Scanner sc = new Scanner(System.in);
        final CalculatorParser parser = new CalculatorParser();
        final EvaluationVisitor evaluator = new EvaluationVisitor();
        final Environment vars = new Environment();
        SymbolicExpression ans = new Variable("ans");
        boolean run = true;
        System.out.print("\033[2J\033[;H");
        System.out.println("Welcome to the Calculator!");
        while(run){
            System.out.print("Please enter an expression: ");
            String input = sc.nextLine();
            try{
                final SymbolicExpression expression = parser.parse(input, vars);
                if(expression.isCommand()){
                    run = expression.run(vars);
                } else{
                    final SymbolicExpression evaluationResult = evaluator.evaluate(topLevel, variables);
                    SymbolicExpression a = new Assignment(output, ans).eval(vars);
                    System.out.println(evaluationResult);
                }
            }catch(Exception e){
                System.out.println(e);
            }
        }

    }
}