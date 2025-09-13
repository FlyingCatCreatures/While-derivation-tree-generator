import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class gcdTest {
    int gcd(int a, int b){
        return b==0 ? a : gcd(b, a % b);
    }

    @Test
    void testGCD() throws Exception {
        for (int a=1; a<=20; a++){
            for (int b=1; b<=20; b++){
                Map<String, Integer> init = Map.of("a", a, "b", b);
                assertEquals(TestHelper.eval("gcd.while", init).get("a"), gcd(a,b));
            }
        }
    }
}
