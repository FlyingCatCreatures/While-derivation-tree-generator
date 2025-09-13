import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class factorialTest {
    int factorial(int n){
        return n==0 ? 1 : n * factorial(n-1);
    }

    @Test 
    void testFactorial() throws Exception {
        for (int n=0; n<=100; n++){
            Map<String, Integer> init = Map.of("n", n);
            assertEquals(TestHelper.eval("factorial.while", init).get("r"), factorial(n));
        }
    }
}
