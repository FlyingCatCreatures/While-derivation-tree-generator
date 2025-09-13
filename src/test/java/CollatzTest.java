import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class CollatzTest {
    int collatzSteps(int n) {
        int steps = 0;
        while (n != 1) {
            steps++;
            if (n % 2 == 0) {
                n = n / 2;
            } else {
                n = 3 * n + 1;
            }
        }
        return steps;
    }

    @Test
    void testCollatz() throws Exception {
        for (int n = 1; n <= 500; n++) {
            Map<String, Integer> init = Map.of("x", n);
            int expected = collatzSteps(n);
            int actual = TestHelper.eval("collatz.while", init).get("steps");
            assertEquals(expected, actual);
        }
    }
}
