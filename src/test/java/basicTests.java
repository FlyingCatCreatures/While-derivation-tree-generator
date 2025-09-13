import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class basicTests {
    @Test
    void testAssignment() throws Exception {
        Map<String, Integer> expected = Map.of("a", 3);
        TestHelper.assertFinalState("assignment.while", expected);
    }

    @Test
    void testReassignment() throws Exception {
        Map<String, Integer> init = Map.of("a", 0);
        Map<String, Integer> expected = Map.of("a", 3);
        TestHelper.assertFinalState("assignment.while", init, expected);
    }

    @Test 
    void testLongVarNames() throws Exception {
        Map<String, Integer> expected = Map.of("ThisIsALongVariable", 2, "ThisIsAnotherLongVariable", 3);
        TestHelper.assertFinalState("longVarNames.while", expected);
    }
    
    @Test
    void testCompound() throws Exception {
        Map<String, Integer> expected = Map.of("x", 0, "a", 1, "b", 2);
        TestHelper.assertFinalState("compound.while", expected);
    }
    
    @Test
    void testWhileFalse() throws Exception {
        Map<String, Integer> init = Map.of("x", 0);
        Map<String, Integer> expected = Map.of("x", 0);
        TestHelper.assertFinalState("while_false.while", init, expected);
    }

    @Test
    void testRepeatUntilTrue() throws Exception {
        Map<String, Integer> init = Map.of("x", 0);
        Map<String, Integer> expected = Map.of("x", 1);
        TestHelper.assertFinalState("repeat-until-true.while", init, expected);
    }

    @Test
    void testRepeatUntilFive() throws Exception {
        Random random = new Random();
        for(int i=0; i<10; i++){
            int x = random.nextInt(501) - 250; // random int between -250 and 250
            Map<String, Integer> init = Map.of("x", x);
            Map<String, Integer> expected = Map.of("x", 5);
            TestHelper.assertFinalState("repeat-until5.while", init, expected);
        }
    }

    @Test
    void testSkip() throws Exception {
        Map<String, Integer> init = Map.of("x", 42);
        Map<String, Integer> expected = Map.of("x", 42);
        TestHelper.assertFinalState("skip.while", init, expected);
    }

    @Test
    void testBreak() throws Exception {
        Map<String, Integer> expected = Map.of("x", 0, "y", 0);
        TestHelper.assertFinalState("break.while", expected);
    }
    @Test
    void testIfTrue() throws Exception {
        Map<String, Integer> expected = Map.of("y", 1);
        TestHelper.assertFinalState("if_true.while", expected);
    }

    @Test
    void testIfFalse() throws Exception {
        Map<String, Integer> expected = Map.of("y", 2);
        TestHelper.assertFinalState("if_false.while", expected);
    }

    @Test
    void testWhileTrueOnce() throws Exception {
        Map<String, Integer> init = Map.of("x", 0);
        Map<String, Integer> expected = Map.of("x", 1);
        TestHelper.assertFinalState("while_true_once.while", init, expected);
    }

    @Test
    void testGrouping() throws Exception {
        Map<String, Integer> expected = Map.of("z", 5);
        TestHelper.assertFinalState("grouping.while", expected);
    }

    @Test
    void testArithmetic() throws Exception {
        Map<String, Integer> expected = Map.of("a", 7, "b", 6, "c", 2, "d", 13, "e", 28, "f", 13);
        TestHelper.assertFinalState("arithmetic.while", expected);
    }

    @Test
    void testBooleanOps() throws Exception {
        Map<String, Integer> expected = Map.of("x", 1, "y", 0, "z", 1);
        TestHelper.assertFinalState("boolean_ops.while", expected);
    }

}
