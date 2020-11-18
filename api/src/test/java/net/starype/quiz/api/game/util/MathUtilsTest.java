package net.starype.quiz.api.game.util;

import org.junit.Assert;
import org.junit.Test;

public class MathUtilsTest {

    private void testIntegerClamp(int value, int min, int max, int expected) {
        Assert.assertEquals(expected, MathUtils.clamp(value, min, max));
    }

    private void testDoubleClamp(double value, double min, double max, double expected) {
        Assert.assertEquals(expected, MathUtils.clamp(value, min, max), 0.001);
    }

    private void testLerp(double value, double inMin, double inMax, double outMin, double outMax, double expected) {
        Assert.assertEquals(expected, MathUtils.lerp(value, inMin, inMax, outMin, outMax), 0.001);
    }

    @Test
    public void clamp_integer() {
        testIntegerClamp(5, 1, 3, 3);
        testIntegerClamp(9, 1, 3, 3);
        testIntegerClamp(3, -91, 3, 3);
        testIntegerClamp(-3, -91, 3, -3);
        testIntegerClamp(-13, -9, 3, -9);
    }

    @Test
    public void clamp_double() {
        testDoubleClamp(5.0, 1.0, 3.0, 3.0);
        testDoubleClamp(9.0, 1.0, 3.0, 3.0);
        testDoubleClamp(3.0, -91.0, 3.0, 3.0);
        testDoubleClamp(-3.0, -91.0, 3.0, -3.0);
        testDoubleClamp(-13.0, -9.0, 3.0, -9.0);
    }

    @Test
    public void lerp_double() {
        testLerp(0.0, 0.0, 1.0, 0.0, 1.0, 0.0);
        testLerp(0.0, -1.0, 1.0, 0.0, 2.0, 1.0);
        testLerp(5.0, 0.0, 1.0, -1.0, 0.0, 4.0);
    }

}
