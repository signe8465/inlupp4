import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class StandardTests {

    @BeforeAll
    static void initAll() {
    }

    @BeforeEach
    void init() {
    }

    @Test
    void someMathTests() {
        assertTrue(1 > 0);    // Assert that an expression is true
        assertFalse(0 == 1);  // Assert that an expression is false
        assertEquals(2+3, 5); // Assert that two objects .equals() each other
        // Assert that running a piece of code results in a particular exception being thrown:
        assertThrows(ArithmeticException.class, () -> {int x = 3 / 0;});
    }

    
    
    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
    }

}