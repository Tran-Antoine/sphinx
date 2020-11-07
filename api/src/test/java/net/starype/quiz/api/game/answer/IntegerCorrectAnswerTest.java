package net.starype.quiz.api.game.answer;

import org.junit.Assert;
import org.junit.Test;

public class IntegerCorrectAnswerTest {

    private static IntegerCorrectAnswerFactory factory = new IntegerCorrectAnswerFactory();

    private void assertFormatValid(String answer) {
        Assert.assertTrue(factory
                .getValidityEvaluator()
                .isValid(new Answer(answer)));
    }

    private void assertFormatInvalid(String answer) {
        Assert.assertFalse(factory
                .getValidityEvaluator()
                .isValid(new Answer(answer)));
    }

    private void assertAnswerCorrectness(double expected, int range, String expectedAnswer, String answer) {
        Assert.assertEquals(expected, factory.setAcceptedRange(range)
                .createCorrectAnswer(expectedAnswer)
                .getCorrectnessEvaluator()
                .getCorrectness(new Answer(answer)), 0.001);
    }

    private void assertAnswerIncorrect(int range, String expectedAnswer, String answer) {
        Assert.assertFalse(factory.setAcceptedRange(range)
                .createCorrectAnswer(expectedAnswer)
                .getCorrectnessEvaluator()
                .getCorrectness(new Answer(answer)) > 0);
    }

    @Test
    public void answer_validity_evaluator() {
        assertFormatValid("1651");
        assertFormatValid("-1");
        assertFormatValid("-546");
        assertFormatValid(" +546 ");
        assertFormatValid("  0123456789  ");
        assertFormatValid(" -0123456789");
        assertFormatValid("0");

        assertFormatInvalid(" 1 2");
        assertFormatInvalid("1+2");
        assertFormatInvalid("1-2");
        assertFormatInvalid("1a6564");
        assertFormatInvalid("c 16564");
        assertFormatInvalid("c16564");
        assertFormatInvalid("This is some raw text");
    }

    @Test
    public void correctness_evaluator() {
        assertAnswerCorrectness(1.0, 3, " +51  ", "  51  ");
        assertAnswerCorrectness(0.75, 3, "-51", "-50");
        assertAnswerCorrectness(0.25, 3, " 50", " 53");

        assertAnswerIncorrect(3, "50", " 54  ");
        assertAnswerIncorrect(3, "50", " -50  ");
        assertAnswerIncorrect(0, " +921  ", " 922  ");
        assertAnswerIncorrect(1, " +921  ", " +923  ");
    }
}
