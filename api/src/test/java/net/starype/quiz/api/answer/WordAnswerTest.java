package net.starype.quiz.api.answer;

import org.junit.Assert;
import org.junit.Test;

public class WordAnswerTest {
    private static WordAnswerFactory factory = new WordAnswerFactory();

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

    private void assertAnswerCorrect(String expectedAnswer, String answer) {
        Assert.assertEquals(1.0, factory
                .createCorrectAnswer(Answer.fromString(expectedAnswer), new IdentityProcessor())
                .getCorrectnessEvaluator()
                .getCorrectness(Answer.fromString(answer)), 0.001);
    }

    private void assertAnswerIncorrect(String expectedAnswer, String answer) {
        Assert.assertEquals(0.0, factory
                .createCorrectAnswer(Answer.fromString(expectedAnswer), new IdentityProcessor())
                .getCorrectnessEvaluator()
                .getCorrectness(Answer.fromString(answer)), 0.001);
    }

    @Test
    public void answer_validity_evaluator() {
        assertFormatValid("Hello");
        assertFormatValid("HelloWorld");
        assertFormatValid("564");
        assertFormatValid("Banana");

        assertFormatInvalid(" Hello and welcome to the aperture computer headed enrichment center");
        assertFormatInvalid(" C++");
        assertFormatInvalid(" J a v a ");
        assertFormatInvalid(" c 4");
        assertFormatInvalid(" 541  1");
        assertFormatInvalid(" z efec  ");
    }

    @Test
    public void correctness_evaluator() {
        assertAnswerCorrect("hello", "hello");
        assertAnswerCorrect("helloW", "helloW");
        assertAnswerCorrect("helloW45", "helloW45");
        assertAnswerCorrect("Banana", "Banana");

        assertAnswerIncorrect("EZ" , "ZE");
        assertAnswerIncorrect("EZ" , "e z");
        assertAnswerIncorrect(" HelloWorld" , "hello world ");
    }
}
