import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.ioopm.calculator.ast.*;
import org.ioopm.calculator.parser.*;

public class ParserTests {
    static CalculatorParser parser;
    static Environment vars;
    static EvaluationVisitor evaluator= new EvaluationVisitor();


    @BeforeAll
    public static void initParser() {
        parser = new CalculatorParser();
        vars = new Environment();
    }
    @BeforeEach
    void resetVars() {
        vars = new Environment();
    }

    static SymbolicExpression parse(String input) {
        try {
            return parser.parse(input, vars);
        } catch (SyntaxErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new SyntaxErrorException("FEL: " + e.getMessage());
        }
    }
    static void assertEqualsContract(SymbolicExpression a,
                                 SymbolicExpression same,
                                 SymbolicExpression different) {
        assertTrue(a.equals(a));                 // reflexiv
        assertTrue(a.equals(same));              // lika struktur
        assertTrue(same.equals(a));              // symmetrisk
        assertFalse(a.equals(different));
        assertFalse(different.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals("inte ett uttryck"));
    }

    static void assertNotConstantValue(SymbolicExpression e) {
        // GISSNING: getValue() på icke-konstanter kastar ett RuntimeException
        assertThrows(RuntimeException.class, () -> e.getValue());
    }

    static void runTest(SymbolicExpression e) {
        String input = e.toString();
        SymbolicExpression result = parse(input);
        assertEquals(e, result);
    }

    static SymbolicExpression con(double d) {
        return new Constant(d);
    }

    static SymbolicExpression var(String s) {
        return new Variable(s);
    }

    static SymbolicExpression add(SymbolicExpression l, SymbolicExpression r) {
        return new Addition(l, r);
    }

    @Test
    void testConstant() {
        runTest(con(42.0));
        runTest(con(0.0));
        runTest(con(3.14159));
        runTest(new Negation(con(3.14159)));
    }

    @Test
    void testVariable() {
        runTest(var("x"));
        runTest(var("xyz"));
        runTest(var("aVeryLongVariableNameThatNeverSeemsToEnd"));
    }

    @Test
    void testAddition() {
        runTest(add(con(1.0), con(2.0)));
        runTest(add(var("x"), con(2.0)));
        runTest(add(add(con(1.0), con(2.0)), 
                    add(var("Three"), var("Four"))));
    }

    @Test
    void testParentheses() throws SyntaxErrorException {
        assertEquals(parse("(1)"), new Constant(1.0));
        assertEquals(parse("((1))"), new Constant(1.0));
        assertEquals(parse("(((1)))"), new Constant(1.0));
        assertEquals(parse("(((1 + 2) + 3) + 4)"),
                     add(add(add(con(1), con(2)), con(3)), con(4)));
        assertEquals(parse("(1 + (2 + (3 + 4)))"),
                     add(con(1), add(con(2), add(con(3), con(4)))));
        assertThrows(SyntaxErrorException.class, () -> {parse("(1 + ");});
    }

    @Test
    void testAdditionAST () {
        Addition a = new Addition(new Constant(1), new Constant(2));
        assertFalse(a.isConstant());
        assertEquals(a.getName(), "+");
        assertFalse(a.isCommand());
        assertEquals(a.getPriority(), 20);
        assertEquals(a.toString(), "1.0 + 2.0");
        assertEquals(evaluator.evaluate(a, vars), new Constant(3));
    }
    // Förutsätter: Environment vars = new Environment(); (t.ex. i @BeforeEach)

    @Test
    void testSubtractionAST() {
        Subtraction s = new Subtraction(new Constant(5), new Constant(2));
        assertFalse(s.isConstant());
        assertEquals(s.getName(), "-");
        assertFalse(s.isCommand());
        assertEquals(s.getPriority(), 20);
        assertEquals(s.toString(), "5.0 - 2.0");
        assertEquals(evaluator.evaluate(s, vars), new Constant(3));
    }

    @Test
    void testMultiplicationAST() {
        Multiplication m = new Multiplication(new Constant(3), new Constant(4));
        assertFalse(m.isConstant());
        assertEquals(m.getName(), "*");
        assertFalse(m.isCommand());
        assertEquals(m.getPriority(), 40);
        assertEquals(m.toString(), "3.0 * 4.0");
        assertEquals(evaluator.evaluate(m, vars), new Constant(12));
    }

    @Test
    void testDivisionAST() {
        Division d = new Division(new Constant(8), new Constant(2));
        assertFalse(d.isConstant());
        assertEquals(d.getName(), "/");
        assertFalse(d.isCommand());
        assertEquals(d.getPriority(), 40);
        assertEquals(d.toString(), "8.0 / 2.0");
        assertEquals(evaluator.evaluate(d, vars), new Constant(4));
    }

    @Test
    void testNegationAST() {
        Negation n = new Negation(new Constant(3));
        assertFalse(n.isConstant());
        assertEquals(n.getName(), "-");
        assertFalse(n.isCommand());
        assertEquals(n.getPriority(), 80);
        assertEquals(n.toString(), "- 3.0");
        assertEquals(evaluator.evaluate(n, vars), new Constant(-3));
    }

    @Test
    void testSinAST() {
        Sin s = new Sin(new Constant(0));
        assertFalse(s.isConstant());
        assertEquals(s.getName(), "sin");
        assertFalse(s.isCommand());
        assertEquals(s.getPriority(), 80);
        assertEquals(s.toString(), "sin 0.0");
        assertEquals(evaluator.evaluate(s, vars), new Constant(0));
    }

    @Test
    void testCosAST() {
        Cos c = new Cos(new Constant(0));
        assertFalse(c.isConstant());
        assertEquals(c.getName(), "cos");
        assertFalse(c.isCommand());
        assertEquals(c.getPriority(), 80);
        assertEquals(c.toString(), "cos 0.0");
        assertEquals(evaluator.evaluate(c, vars), new Constant(1));
    }

    @Test
    void testExpAST() {
        Exp e = new Exp(new Constant(0));
        assertFalse(e.isConstant());
        assertEquals(e.getName(), "e^");
        assertFalse(e.isCommand());
        assertEquals(e.getPriority(), 80);
        assertEquals(e.toString(), "e^ 0.0");
        assertEquals(evaluator.evaluate(e, vars), new Constant(1));
    }

    @Test
    void testLogAST() {
        Log l = new Log(new Constant(1));
        assertFalse(l.isConstant());
        assertEquals(l.getName(), "log");
        assertFalse(l.isCommand());
        assertEquals(l.getPriority(), 80);
        assertEquals(l.toString(), "log 1.0");
        assertEquals(evaluator.evaluate(l, vars), new Constant(0));
    }

    @Test
    void testConstantAST() {
        Constant c = new Constant(2);
        assertTrue(c.isConstant());
        assertFalse(c.isCommand());
        assertEquals(c.toString(), "2.0");
        assertEquals(evaluator.evaluate(c, vars), new Constant(2));
    }

    @Test
    void testNamedConstantAST() {
        NamedConstant pi = new NamedConstant("pi", Math.PI);
        assertFalse(pi.isConstant());          
        assertFalse(pi.isCommand());
        assertEquals(evaluator.evaluate(pi, vars), new Constant(Math.PI));
    }

    @Test
    void testVariableAST() {
        Variable x = new Variable("x");
        assertFalse(x.isConstant());
        assertFalse(x.isCommand());
        assertEquals(x.toString(), "x");

        vars.put(new Variable("x"), new Constant(5));   // anpassa efter din Environment
        assertEquals(evaluator.evaluate(x, vars), new Constant(5));
    }

    @Test
    void testAssignmentAST() {
        Assignment a = new Assignment(new Constant(3), new Variable("x"));
        assertFalse(a.isConstant());
        assertFalse(a.isCommand());
        assertEquals(evaluator.evaluate(a, vars), new Constant(3));
        assertEquals(evaluator.evaluate(new Variable("x"), vars), new Constant(3)); // variabeln ska ha satts
    }
        // ---------- Constant ----------
    @Test
    void testConstantFull() {
        Constant c = new Constant(2);
        assertEquals(2.0, c.getValue());
        assertTrue(c.isConstant());
        assertFalse(c.isCommand());
        assertEquals("2.0", c.toString());
        assertEquals("-2.5", new Constant(-2.5).toString());
        assertEquals(new Constant(2), evaluator.evaluate(c, vars));
        assertEqualsContract(c, new Constant(2), new Constant(3));
        assertFalse(new Constant(2).equals(new Variable("x")));
    }

    // ---------- NamedConstant ----------
    @Test
    void testNamedConstantFull() {
        NamedConstant pi = new NamedConstant("pi", Math.PI);
        assertFalse(pi.isConstant());
        assertFalse(pi.isCommand());
        assertEquals(new Constant(Math.PI), evaluator.evaluate(pi, vars));
    }

    // ---------- Variable ----------
    @Test
    void testVariableFull() {
        Variable x = new Variable("x");
        assertFalse(x.isConstant());
        assertFalse(x.isCommand());
        assertEquals("x", x.toString());
        assertNotConstantValue(x);
        assertEqualsContract(x, new Variable("x"), new Variable("y"));

        // odefinierad variabel:  evaluator.evaluate ger variabeln själv (GISSNING)
        assertEquals(new Variable("x"), evaluator.evaluate(x, vars));

        vars.put(new Variable("x"), new Constant(5));
        assertEquals(new Constant(5), evaluator.evaluate(x, vars));
    }

    // ---------- Binära: Addition, Subtraction, Multiplication, Division ----------
    @Test
    void testAdditionFull() {
        Addition a = new Addition(new Constant(1), new Constant(2));
        assertFalse(a.isConstant());
        assertFalse(a.isCommand());
        assertEquals("+", a.getName());
        assertEquals(20, a.getPriority());
        assertEquals("1.0 + 2.0", a.toString());
        assertNotConstantValue(a);
        assertEquals(new Constant(1), a.getLhs());     // GISSNING: getLhs()/getRhs()
        assertEquals(new Constant(2), a.getRhs());
        assertEquals(new Constant(3), evaluator.evaluate(a, vars));
        assertEqualsContract(a,
                            new Addition(new Constant(1), new Constant(2)),
                            new Addition(new Constant(1), new Constant(3)));
        // samma operander men annan operator
        assertFalse(a.equals(new Subtraction(new Constant(1), new Constant(2))));
        // ordningen spelar roll i trädet
        assertFalse(a.equals(new Addition(new Constant(2), new Constant(1))));

        //  evaluator.evaluate med variabel
        Addition withVar = new Addition(new Variable("x"), new Constant(2));
        assertEquals(withVar, evaluator.evaluate(withVar, vars));     // x odefinierad: uttrycket kvarstår
        vars.put(new Variable("x"), new Constant(5));
        assertEquals(new Constant(7), evaluator.evaluate(withVar, vars));
    }

    @Test
    void testSubtractionFull() {
        Subtraction s = new Subtraction(new Constant(5), new Constant(2));
        assertFalse(s.isConstant());
        assertFalse(s.isCommand());
        assertEquals("-", s.getName());
        assertEquals(20, s.getPriority());
        assertEquals("5.0 - 2.0", s.toString());
        assertNotConstantValue(s);
        assertEquals(new Constant(5), s.getLhs());
        assertEquals(new Constant(2), s.getRhs());
        assertEquals(new Constant(3), evaluator.evaluate(s, vars));
        assertEquals(new Constant(-3),
                    evaluator.evaluate(new Subtraction(new Constant(2), new Constant(5)), vars)); // ej kommutativ
        assertEqualsContract(s,
                            new Subtraction(new Constant(5), new Constant(2)),
                            new Subtraction(new Constant(2), new Constant(5)));
    }

    @Test
    void testMultiplicationFull() {
        Multiplication m = new Multiplication(new Constant(3), new Constant(4));
        assertFalse(m.isConstant());
        assertFalse(m.isCommand());
        assertEquals("*", m.getName());
        assertEquals(40, m.getPriority());
        assertEquals("3.0 * 4.0", m.toString());
        assertNotConstantValue(m);
        assertEquals(new Constant(3), m.getLhs());
        assertEquals(new Constant(4), m.getRhs());
        assertEquals(new Constant(12), evaluator.evaluate(m, vars));
        assertEquals(new Constant(0),
                    evaluator.evaluate(new Multiplication(new Constant(0), new Constant(7)), vars));
        assertEqualsContract(m,
                            new Multiplication(new Constant(3), new Constant(4)),
                            new Multiplication(new Constant(3), new Constant(5)));
    }

    @Test
    void testDivisionFull() {
        Division d = new Division(new Constant(8), new Constant(2));
        assertFalse(d.isConstant());
        assertFalse(d.isCommand());
        assertEquals("/", d.getName());
        assertEquals(40, d.getPriority());
        assertEquals("8.0 / 2.0", d.toString());
        assertNotConstantValue(d);
        assertEquals(new Constant(8), d.getLhs());
        assertEquals(new Constant(2), d.getRhs());
        assertEquals(new Constant(4), evaluator.evaluate(d, vars));
        assertEquals(new Constant(0.5),
                    evaluator.evaluate(new Division(new Constant(1), new Constant(2)), vars));
        assertEqualsContract(d,
                            new Division(new Constant(8), new Constant(2)),
                            new Division(new Constant(2), new Constant(8)));
    }

    // ---------- Unära: Negation, Sin, Cos, Exp, Log ----------
    @Test
    void testNegationFull() {
        Negation n = new Negation(new Constant(3));
        assertFalse(n.isConstant());
        assertFalse(n.isCommand());
        assertEquals("-", n.getName());
        assertEquals(80, n.getPriority());
        assertEquals("- 3.0", n.toString());
        assertNotConstantValue(n);
        assertEquals(new Constant(3), n.getArgument());     
        assertEquals(new Constant(-3), evaluator.evaluate(n, vars));
        assertEquals(new Constant(3), evaluator.evaluate(new Negation(new Constant(-3)), vars));
        assertEqualsContract(n, new Negation(new Constant(3)), new Negation(new Constant(4)));
        assertFalse(n.equals(new Sin(new Constant(3))));
    }

    @Test
    void testSinFull() {
        Sin s = new Sin(new Constant(1));
        assertFalse(s.isConstant());
        assertFalse(s.isCommand());
        assertEquals("sin", s.getName());
        assertEquals(80, s.getPriority());
        assertEquals("sin 1.0", s.toString());
        assertNotConstantValue(s);
        assertEquals(new Constant(1), s.getArgument());
        assertEquals(new Constant(Math.sin(1.0)), evaluator.evaluate(s, vars));
        assertEquals(new Constant(0), evaluator.evaluate(new Sin(new Constant(0)), vars));
        assertEqualsContract(s, new Sin(new Constant(1)), new Sin(new Constant(2)));
        assertFalse(s.equals(new Cos(new Constant(1))));
    }

    @Test
    void testCosFull() {
        Cos c = new Cos(new Constant(1));
        assertFalse(c.isConstant());
        assertFalse(c.isCommand());
        assertEquals("cos", c.getName());
        assertEquals(80, c.getPriority());
        assertEquals("cos 1.0", c.toString());
        assertNotConstantValue(c);
        assertEquals(new Constant(1), c.getArgument());
        assertEquals(new Constant(Math.cos(1.0)), evaluator.evaluate(c, vars));
        assertEquals(new Constant(1), evaluator.evaluate(new Cos(new Constant(0)), vars));
        assertEqualsContract(c, new Cos(new Constant(1)), new Cos(new Constant(2)));
    }

    @Test
    void testExpFull() {
        Exp e = new Exp(new Constant(1));
        assertFalse(e.isConstant());
        assertFalse(e.isCommand());
        assertEquals("e^", e.getName());
        assertEquals(80, e.getPriority());
        assertEquals("e^ 1.0", e.toString());
        assertNotConstantValue(e);
        assertEquals(new Constant(1), e.getArgument());
        assertEquals(new Constant(Math.exp(1.0)), evaluator.evaluate(e, vars));
        assertEquals(new Constant(1), evaluator.evaluate(new Exp(new Constant(0)), vars));
        assertEqualsContract(e, new Exp(new Constant(1)), new Exp(new Constant(2)));
    }

    @Test
    void testLogFull() {
        Log l = new Log(new Constant(2));
        assertFalse(l.isConstant());
        assertFalse(l.isCommand());
        assertEquals("log", l.getName());
        assertEquals(80, l.getPriority());
        assertEquals("log 2.0", l.toString());
        assertNotConstantValue(l);
        assertEquals(new Constant(2), l.getArgument());
        assertEquals(new Constant(Math.log(2.0)), evaluator.evaluate(l, vars));
        assertEquals(new Constant(0), evaluator.evaluate(new Log(new Constant(1)), vars));
        assertEqualsContract(l, new Log(new Constant(2)), new Log(new Constant(3)));
    }

    // ---------- Assignment ----------
    @Test
    void testAssignmentFull() {
        Assignment a = new Assignment(new Constant(3), new Variable("x"));
        assertFalse(a.isConstant());
        assertFalse(a.isCommand());
        assertNotConstantValue(a);
        assertEquals(new Constant(3), a.getLhs());
        assertEquals(new Variable("x"), a.getRhs());
        assertEquals(new Constant(3), evaluator.evaluate(a, vars));
        assertEquals(new Constant(3), evaluator.evaluate(new Variable("x"), vars)); // variabeln ska ha satts
        assertEqualsContract(a,
                            new Assignment(new Constant(3), new Variable("x")),
                            new Assignment(new Constant(4), new Variable("x")));
        assertFalse(a.equals(new Assignment(new Constant(3), new Variable("y"))));
    }

    @Test
    void testAssignmentFromExpression() {
        // (1 + 2) = x  ->  x == 3
        Assignment a = new Assignment(new Addition(new Constant(1), new Constant(2)),
                                    new Variable("x"));
        assertEquals(new Constant(3), evaluator.evaluate(a, vars));
        assertEquals(new Constant(3), evaluator.evaluate(new Variable("x"), vars));
    }


    @Test
    void testAssignmentDouble(){
        Assignment a1 = new Assignment(new Constant(2), new Variable("x"));
        Assignment a2 = new Assignment(new Constant(3), new Variable("x"));

        evaluator.evaluate(new Addition(a1, a2), vars);
        assertEquals(evaluator.evaluate(new Variable("x"), vars), new Constant(2.0));
    
    }

}

