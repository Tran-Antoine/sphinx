package net.starype.quiz.api.game.answer;

import org.junit.Assert;
import org.junit.Test;

public class IntegerCorrectAnswerTest {
    @Test
    public void test_answer_validity_evaluator() {
        IntegerCorrectAnswerFactory factory = new IntegerCorrectAnswerFactory();

        Assert.assertTrue(factory
                .getValidityEvaluator()
                .isValid(new Answer("1651")));
        Assert.assertTrue(factory
                .getValidityEvaluator()
                .isValid(new Answer("-1")));
        Assert.assertTrue(factory
                .getValidityEvaluator()
                .isValid(new Answer("-546")));
        Assert.assertTrue(factory
                .getValidityEvaluator()
                .isValid(new Answer(" +546 ")));
        Assert.assertTrue(factory
                .getValidityEvaluator()
                .isValid(new Answer("  0123456789  ")));
        Assert.assertTrue(factory
                .getValidityEvaluator()
                .isValid(new Answer(" -0123456789")));
        Assert.assertTrue(factory
                .getValidityEvaluator()
                .isValid(new Answer("0")));

        Assert.assertFalse(factory
                .getValidityEvaluator()
                .isValid(new Answer("1 2")));
        Assert.assertFalse(factory
                .getValidityEvaluator()
                .isValid(new Answer("1+2")));
        Assert.assertFalse(factory
                .getValidityEvaluator()
                .isValid(new Answer("1-2")));
        Assert.assertFalse(factory
                .getValidityEvaluator()
                .isValid(new Answer("1a6564")));
        Assert.assertFalse(factory
                .getValidityEvaluator()
                .isValid(new Answer("c 16564")));
        Assert.assertFalse(factory
                .getValidityEvaluator()
                .isValid(new Answer("c16564")));
        Assert.assertFalse(factory
                .getValidityEvaluator()
                .isValid(new Answer("This is some raw text")));
    }

    @Test
    public void test_correctness_evaluator() {
        IntegerCorrectAnswerFactory factory = new IntegerCorrectAnswerFactory();

        Assert.assertEquals(1.0, factory.setAcceptedRange(3)
                .createCorrectAnswer("  +51  ")
                .getCorrectnessEvaluator()
                .getCorrectness(new Answer("  51  ")), 0.001);
        Assert.assertEquals(0.75, factory.setAcceptedRange(3)
                .createCorrectAnswer("-51")
                .getCorrectnessEvaluator()
                .getCorrectness(new Answer("-50")), 0.001);
        Assert.assertEquals(0.75, factory.setAcceptedRange(3)
                .createCorrectAnswer("-51")
                .getCorrectnessEvaluator()
                .getCorrectness(new Answer("-50")), 0.001);
        Assert.assertEquals(0.25, factory.setAcceptedRange(3)
                .createCorrectAnswer(" 50")
                .getCorrectnessEvaluator()
                .getCorrectness(new Answer(" 53")), 0.001);

        Assert.assertFalse(factory.setAcceptedRange(3)
                .createCorrectAnswer(" 50")
                .getCorrectnessEvaluator()
                .getCorrectness(new Answer(" 54  ")) > 0);
        Assert.assertFalse(factory.setAcceptedRange(3)
                .createCorrectAnswer(" 50")
                .getCorrectnessEvaluator()
                .getCorrectness(new Answer(" -50  ")) > 0);
        Assert.assertFalse(factory.setAcceptedRange(0)
                .createCorrectAnswer(" +921  ")
                .getCorrectnessEvaluator()
                .getCorrectness(new Answer(" 922  ")) > 0);
        Assert.assertFalse(factory.setAcceptedRange(1)
                .createCorrectAnswer(" +921  ")
                .getCorrectnessEvaluator()
                .getCorrectness(new Answer(" +923  ")) > 0);
    }
}
