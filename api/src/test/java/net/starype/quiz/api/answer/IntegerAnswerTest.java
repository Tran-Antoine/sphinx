package net.starype.quiz.api.answer;

import org.junit.Assert;
import org.junit.Test;

public class IntegerAnswerTest {

    private static IntegerAnswerFactory factory = new IntegerAnswerFactory();

    private void assertFormatValid(String answer) {
        /*Assert.assertTrue(factory
                .getValidityEvaluator()
                .isValid(Answer.fromString(answer)));*/
    }

    private void assertFormatInvalid(String answer) {
        /*Assert.assertFalse(factory
                .getValidityEvaluator()
                .isValid(Answer.fromString(answer)));*/
    }

    private void assertAnswerCorrectness(double expected, int range, String expectedAnswer, String answer) {
        Assert.assertEquals(expected, factory.withAcceptedRange(range)
                .createCorrectAnswer(Answer.fromString(expectedAnswer), new IdentityProcessor())
                .getCorrectnessEvaluator()
                .getCorrectness(Answer.fromString(answer)), 0.001);
    }

    private void assertAnswerIncorrect(int range, String expectedAnswer, String answer) {
        Assert.assertFalse(factory.withAcceptedRange(range)
                .createCorrectAnswer(Answer.fromString(expectedAnswer), new IdentityProcessor())
                .getCorrectnessEvaluator()
                .getCorrectness(Answer.fromString(answer)) > 0);
    }

    @Test
    public void answer_validity_evaluator() {
        assertFormatValid("1651");
        assertFormatValid("-1");
        assertFormatValid("-546");
        assertFormatValid("+546");
        assertFormatValid("0123456789");
        assertFormatValid("-0123456789");
        assertFormatValid("0");

        assertFormatInvalid(" 1 2");
        assertFormatInvalid("1+2");
        assertFormatInvalid("1-2");
        assertFormatInvalid("1a6564");
        assertFormatInvalid("01235651651516516516512165113456789");
        assertFormatInvalid("c 16564");
        assertFormatInvalid("c16564");
        assertFormatInvalid("This is some raw text");
    }

    @Test
    public void correctness_evaluator() {
        assertAnswerCorrectness(1.0, 4, "+51", "51");
        assertAnswerCorrectness(0.75, 4, "-51", "-50");
        assertAnswerCorrectness(0.25, 4, "50", "53");

        assertAnswerIncorrect(4, "50", "54");
        assertAnswerIncorrect(4, "50", "-50");
        assertAnswerIncorrect(1, "+921", "922");
        assertAnswerIncorrect(2, "921", "+923");
    }
}
