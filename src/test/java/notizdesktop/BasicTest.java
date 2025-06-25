package notizdesktop;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test class to ensure Maven test phase runs successfully.
 */
public class BasicTest {

    @Test
    public void testBasicAssertion() {
        assertTrue(true, "This test should always pass");
    }

    @Test
    public void testStringEquality() {
        String expected = "Hello World";
        String actual = "Hello World";
        assertEquals(expected, actual, "Strings should be equal");
    }
}