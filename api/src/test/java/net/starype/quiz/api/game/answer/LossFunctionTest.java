package net.starype.quiz.api.game.answer;

import org.junit.Assert;
import org.junit.Test;

public class LossFunctionTest {

    private void TestLossFunction(LossFunction function, double expected, double input)
    {
        Assert.assertEquals(expected, function.evaluate(input), 0.001);
    }

    @Test
    public void linear_loss_function()
    {
        TestLossFunction(new LinearLossFunction(), 1.0, 0.0);
        TestLossFunction(new LinearLossFunction(), 0.5, 0.5);
        TestLossFunction(new LinearLossFunction(), 0.0, 1.0);
        TestLossFunction(new LinearLossFunction(), 0.0, 1.5);
        TestLossFunction(new LinearLossFunction(), 1.0, -1.0);
    }

    @Test
    public void cosine_loss_function()
    {
        TestLossFunction(new CosineLossFunction(), 1.0, 0.0);
        Assert.assertTrue(new CosineLossFunction().evaluate(1.0) < 0.1);
    }

    @Test
    public void normal_loss_function()
    {
        TestLossFunction(new NormalLossFunction(), 1.0, 0.0);
        Assert.assertTrue(new NormalLossFunction().evaluate(1.0) < 0.1);
    }

    @Test
    public void binary_loss_function()
    {
        TestLossFunction(new BinaryLossFunction(), 0.0, 1.0);
        TestLossFunction(new BinaryLossFunction(), 1.0, 0.0);
        TestLossFunction(new BinaryLossFunction(0.5), 1.0, 0.5);
        TestLossFunction(new BinaryLossFunction(), 0.0, 0.5);
    }

}
