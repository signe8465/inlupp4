package org.ioopm.calculator.parser;

import org.ioopm.calculator.ast.*;

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.IOException;

import java.util.*;

/**
 * Represents the parsing of strings into valid expressions defined in the AST.
 */
public class CalculatorParser {
    private StreamTokenizer st;
    private Environment vars;
    private Variable CurrentFunction = null;
    private boolean EndFunction = true;
    private boolean isDeclaringFunction = false;
    private static char MULTIPLY = '*';
    private static char ADDITION = '+';
    private static char SUBTRACTION = '-';
    private static char DIVISION = '/';
    private static String NEG = "Neg";
    private static char NEGATION = '-';
    private static String SIN = "Sin";
    private static String COS = "Cos";
    private static String LOG = "Log";
    private static String EXP = "Exp";
    private static char ASSIGNMENT = '=';
    private static char SCOPESTART = '{';
    private static String IF = "if";
    private static char LESSTHAN = '<';
    private static char GREATERTHAN = '>';
    private static String LESSTHANOREQUAL = "=";
    private static String GREATERTHANOREQUAL = ">=";
    private static String EQUALS = "==";
    private static String FUNCTION = "function";
    private static String END = "end";

    // unallowerdVars is used to check if variabel name that we
    // want to assign new meaning to is a valid name eg 3 = Quit
    // or 10 + x = L is not allowed
    private final ArrayList < String > unallowedVars = new ArrayList < String > (Arrays.asList("Quit",
        "Vars",
        "Clear", "end")); //FÖRBJUDNA HÄR

    /**
     * Used to parse the inputted string by the Calculator program
     * @param inputString the string used to parse
     * @param vars the Environment in which the variables exist
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     */
    public SymbolicExpression parse(String inputString, Environment vars) throws IOException {
        this.st = new StreamTokenizer(new StringReader(inputString)); // reads from inputString via stringreader.
        this.vars = vars;
        this.st.ordinaryChar('-');
        this.st.ordinaryChar('/');
        this.st.ordinaryChar('{');
        this.st.ordinaryChar('>');
        this.st.ordinaryChar('<');
        this.st.eolIsSignificant(true);
        if(EndFunction){
            SymbolicExpression result = statement(); // calls to statement
            return result; // the final result
        } else{
            this.EndFunction = buildFunction();
            return new Variable(" ");
        }
    }

    /**
     * Checks wether the token read is a command or an assignment
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     * @throws SyntaxErrorException if the token parsed cannot be turned into a valid expression
     */
    private SymbolicExpression statement() throws IOException {
        SymbolicExpression result;
        this.st.nextToken(); //kollar på nästa token som ligger på strömmen
        if (this.st.ttype == this.st.TT_EOF) {
            throw new SyntaxErrorException("Error: Expected an expression");
        }
        if (this.st.ttype == this.st.TT_WORD) { // vilken typ det senaste tecken vi lÃ¤ste in hade.
            if (this.st.sval.equals("Quit") || this.st.sval.equals("Vars") || this.st.sval.equals("Clear") || this.st.sval.equals("end")) { // sval = string Variable
                result = command();
            } else if(this.st.sval.equals(FUNCTION)){
                result = functionDeclaration();
            }else{
                result = assignment(); // går vidare med uttrycket.
            }
        } else {
            result = assignment(); // om inte == word, gå till assignment Ändå (kan vara tt_number)
        }

        if (this.st.nextToken() != this.st.TT_EOF) { // token should be an end of stream token if we are done
            if (this.st.ttype == this.st.TT_WORD) {
                throw new SyntaxErrorException("Error: Unexpected '" + this.st.sval + "'");
            } else {
                throw new SyntaxErrorException("Error: Unexpected '" + String.valueOf((char) this.st.ttype) + "'");
            }
        }
        return result;
    }


    /**
     * Checks what kind of command that should be returned
     * @return an instance of Quit, Clear or Vars depending on the token parsed
     * @throws IOException by nextToken() if it reads invalid input
     */
    private SymbolicExpression command() throws IOException {
        if (this.st.sval.equals("Quit")) {
            return Quit.instance();
        } else if (this.st.sval.equals("Clear")) {
            return Clear.instance();
        } else if (this.st.sval.equals("Vars")){
            return Vars.instance();
        } else{
            return End.instance();
        } 
    }


    /**
     * Checks wether the token read is an assignment between 2 expression and 
     * descend into the right hand side of '='
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     * @throws SyntaxErrorException if the token parsed cannot be turned into a valid expression,
     *         the variable on rhs of '=' is a number or invalid variable
     */
    private SymbolicExpression assignment() throws IOException {
        SymbolicExpression result = expression();
        this.st.nextToken();
        while (this.st.ttype == ASSIGNMENT) {
            this.st.nextToken();
            if (this.st.ttype == this.st.TT_NUMBER) {
                throw new SyntaxErrorException("Error: Numbers cannot be used as a variable name");
            } else if (this.st.ttype != this.st.TT_WORD) {
                throw new SyntaxErrorException("Error: Not a valid assignment of a variable"); //this handles faulty inputs after the equal sign eg. 1 = (x etc
            } else {
                if (this.st.sval.equals("ans")) { //LOGIK OM FÖRBJUDNA VARIABLER GÅR NOG IN HÄR
                    throw new SyntaxErrorException("Error: ans cannot be redefined");
                }
                SymbolicExpression key = identifier();
                result = new Assignment(result, key);
            }
            this.st.nextToken();
        }
        this.st.pushBack();
        return result;
    }

    /**
     * Check if valid identifier for variable and return that if so
     * @return a SymbolicExpression that is either a named constant or a new variable
     * @throws IOException by nextToken() if it reads invalid input
     * @throws IllegalExpressionException if you try to redefine a string that isn't allowed
     */
    private SymbolicExpression identifier() throws IOException {
        SymbolicExpression result;

        if (this.unallowedVars.contains(this.st.sval)) {
            throw new IllegalExpressionException("Error: cannot redefine " + this.st.sval);
        }
    
        if (Constants.namedConstants.containsKey(this.st.sval)) {
            result = new NamedConstant(st.sval, Constants.namedConstants.get(st.sval));
        } 
        else {
            result = functionStart();
        }
        return result;
    }


    /**
     * Checks wether the token read is an addition or subtraction
     * and then continue on with the right hand side of operator
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     */
    private SymbolicExpression expression() throws IOException {
        SymbolicExpression result = term();
        this.st.nextToken();
        while (this.st.ttype == ADDITION || this.st.ttype == SUBTRACTION) {
            int operation = st.ttype;
            this.st.nextToken();
            if (operation == ADDITION) {
                result = new Addition(result, term());
            } else {
                result = new Subtraction(result, term());
            }
            this.st.nextToken();
        }
        this.st.pushBack();
        return result;
    }

    /**
     * Checks wether the token read is an Multiplication or
     * Division and then continue on with the right hand side of
     * operator
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     */
    private SymbolicExpression term() throws IOException {
        SymbolicExpression result = primary();
        this.st.nextToken();
        while (this.st.ttype == MULTIPLY || this.st.ttype == DIVISION) {
            int operation = st.ttype;
            this.st.nextToken();

            if (operation == MULTIPLY) {
                result = new Multiplication(result, primary());
            } else {
                result = new Division(result, primary());
            }
            this.st.nextToken();
        }
        this.st.pushBack();
        return result;
    }

    /**
     * Checks wether the token read is a parantheses and then
     * continue on with the expression inside of it or if the
     * operation is an unary operation and then continue on with
     * the right hand side of that operator else if it's a
     * number/identifier
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     * @throws SyntaxErrorException if the token parsed cannot be turned into a valid expression,
     *         missing right parantheses
     */
    private SymbolicExpression primary() throws IOException {
        SymbolicExpression result ;
        if (this.st.ttype == '(') { 
            this.st.nextToken();
            result = assignment();
            /// This captures unbalanced parentheses!
            if (this.st.nextToken() != ')') {
                throw new SyntaxErrorException("expected ')'");
            }
        } else if (this.st.ttype == NEGATION || this.st.ttype == SCOPESTART) {
            result = unary();
        } else if (this.st.ttype == this.st.TT_WORD) {
            if (st.sval.equals(SIN) ||
                st.sval.equals(COS) ||
                st.sval.equals(EXP) ||
                st.sval.equals(NEG) ||
                st.sval.equals(LOG) ||
                st.sval.equals(SCOPESTART))
                {

                result = unary();
            } else if(this.st.sval.equals(IF)){
                result = conditional();
            } else {
                result = identifier();
            }
        } else {
            this.st.pushBack();
            result = number();
        }
        if (result == null){
            throw new SyntaxErrorException("Något gick fel i primary()");
        }
        return result;
    }

    /**
     * Checks what type of Unary operation the token read is and
     * then continues with the expression that the operator holds
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     */
    private SymbolicExpression unary() throws IOException {
        SymbolicExpression result;
        int charval = st.ttype;
        String operation = st.sval;
        this.st.nextToken();
        if (charval == SCOPESTART )
        {
            result = new ScopeStart(assignment());
            if (this.st.nextToken() != '}') {
                throw new SyntaxErrorException("expected '}'");
            }
            return result;
        } 
        else if (charval == NEGATION || operation.equals(NEG)) {
            result = new Negation(primary());
        } else if (operation.equals(SIN)) {
            result = new Sin(primary());
        } else if (operation.equals(COS)) {
            result = new Cos(primary());
        } else if (operation.equals(LOG)) {
            result = new Log(primary());
        } else{
            result = new Exp(primary());
        }  

        return result;
    }

       /**
     * Checks what type of conditional operation the token read is and
     * then continues with the expression that the operator holds
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     */
    private SymbolicExpression conditional() throws IOException {
        SymbolicExpression result = null;
        if(this.st.sval.equals(IF))
        {
            this.st.nextToken();
            SymbolicExpression op = operator();
            this.st.nextToken(); 
            SymbolicExpression IfScope = assignment();
            if(!(IfScope instanceof ScopeStart)){
                throw new SyntaxErrorException("inget start på if-scope");
            }
            this.st.nextToken();
            if (!this.st.sval.equals("else")) {
                throw new SyntaxErrorException("Else-sats saknas");
            }
            this.st.nextToken();
            SymbolicExpression ElseScope = assignment();
            if(!(ElseScope instanceof ScopeStart)){
                throw new SyntaxErrorException("inget start på else-scope");
            }
            result = new Conditional(op, IfScope, ElseScope);

        }
        if(result == null){
            throw new SyntaxErrorException("Något gick fel i conditional()");
        }
        return result;
    }

    /**
     * Checks if the token read is a number - should always be a number in this method
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     * @throws SyntaxErrorException if the token parsed cannot be turned into a valid expression,
     *         expected a number which is not present
     */
    private SymbolicExpression number() throws IOException {
        this.st.nextToken();
        if (this.st.ttype == this.st.TT_NUMBER) {
            return new Constant(this.st.nval);
        }
        else {
            throw new SyntaxErrorException("Error: Expected number");
        }
    }

    private SymbolicExpression operator() throws IOException{
        SymbolicExpression result = expression();
        this.st.nextToken();
        if(this.st.ttype == GREATERTHAN){
            this.st.nextToken();
            if(this.st.ttype == ASSIGNMENT){
                this.st.nextToken();
                return result = new GreaterThanOrEqual(result, expression());
            }
            return result = new GreaterThan(result, expression());
        } else if(this.st.ttype == LESSTHAN){
            this.st.nextToken();
            if(this.st.ttype == ASSIGNMENT){
                this.st.nextToken();
                return result = new LessThanOrEqual(result, expression());
            }
            return result = new LessThan(result, expression());
        } else{
            this.st.nextToken();            
            this.st.nextToken();
            return result = new Equals(result, expression());
        }
    }

    private SymbolicExpression functionDeclaration() throws IOException{
        this.st.nextToken();
        isDeclaringFunction = true;
        SymbolicExpression key = identifier();
        //check för om det är en variabel
        Listan paramListan = new Listan();
        this.st.nextToken();
        if (this.st.ttype == '(') { 
            this.st.nextToken(); 

            while(this.st.ttype != ')'){
                if(this.st.ttype != ','){
                    SymbolicExpression paramVariable = identifier();
                    paramListan.addNode(paramVariable);
                } 
                this.st.nextToken();
            }
        } 
        
        FunctionDeclaration f = new FunctionDeclaration((Variable) key, paramListan);
        CurrentFunction =(Variable) key;
        this.vars.put((Variable)key, f.getFunction());
        EndFunction = false;
        isDeclaringFunction = false;
        return new Variable("");
    }

    private boolean buildFunction() throws IOException{
        SymbolicExpression row = statement();
        if(row.isEnd()){
            return true;
        }
        SymbolicExpression f = this.vars.get(CurrentFunction);
        f.addRow(row);
        this.vars.put(CurrentFunction,(Function) f);
        return false;
    }

    private SymbolicExpression functionStart() throws IOException{
        SymbolicExpression key = new Variable(this.st.sval);
        if(isDeclaringFunction){
            return key;
        }
        Listan ConstantList = new Listan();
        this.st.nextToken();
        if (this.st.ttype == '(') { 
            this.st.nextToken(); 
            while(this.st.ttype != ')'){
                if(this.st.ttype != ','){
                    SymbolicExpression ConstantVariable = expression();
                    ConstantList.addNode(ConstantVariable);
                } 
                this.st.nextToken();
            }
        } else{
            this.st.pushBack();
            return key;
        }
        return new FunctionStart(key, ConstantList);
    }

}