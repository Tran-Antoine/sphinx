package net.starype.quiz.api.game.answer;

import org.junit.Assert;
import org.junit.Test;

public class DoubleAnswerEvaluatorTest {

    private static final DoubleAnswerFactory factory = new DoubleAnswerFactory();

    private void assertFormatValid(String answer) {
        Assert.assertTrue(factory
                .getValidityEvaluator()
                .isValid(Answer.fromString(answer)));
    }

    private void assertFormatInvalid(String answer) {
        Assert.assertFalse(factory
                .getValidityEvaluator()
                .isValid(Answer.fromString(answer)));
    }

    private void assertAnswerCorrectness(double expected, double range, String expectedAnswer, String answer) {
        Assert.assertEquals(expected, factory.withAcceptedRange(range)
                .createCorrectAnswer(Answer.fromString(expectedAnswer), new IdentityProcessor())
                .getCorrectnessEvaluator()
                .getCorrectness(Answer.fromString(answer)), 0.001);
    }

    private void assertAnswerIncorrect(double range, String expectedAnswer, String answer) {
        System.out.println(factory.withAcceptedRange(range)
                .createCorrectAnswer(Answer.fromString(expectedAnswer), new IdentityProcessor())
                .getCorrectnessEvaluator()
                .getCorrectness(Answer.fromString(answer)));

        Assert.assertFalse(factory.withAcceptedRange(range)
                .createCorrectAnswer(Answer.fromString(expectedAnswer), new IdentityProcessor())
                .getCorrectnessEvaluator()
                .getCorrectness(Answer.fromString(answer)) > 0);
    }

    @Test
    public void answer_validity_evaluator() {
        assertFormatValid("1651");
        assertFormatValid("-1.0");
        assertFormatValid("-546,0");
        assertFormatValid(" +546 ");
        assertFormatValid("  0123456,789  ");
        assertFormatValid(" -012.3456789");
        assertFormatValid(".0");

        assertFormatInvalid(" 1 2");
        assertFormatInvalid("1+2");
        assertFormatInvalid("1-2");
        assertFormatInvalid("1a6.564");
        assertFormatInvalid("c 16564");
        assertFormatInvalid("16.,564");
        assertFormatInvalid("This is some raw text");
    }

    @Test
    public void correctness_evaluator() {
        assertAnswerCorrectness(1.0, 4.0, "+51.0", "51");
        assertAnswerCorrectness(0.975, 4.0, "-5.1", "-5,0");
        assertAnswerCorrectness(0.9925, 4.0, ",50", ".53");

        assertAnswerIncorrect(0.01, ",50", ".53");
        assertAnswerIncorrect(0.3, ",5", ".95");
    }
}
