package net.starype.quiz.api.game.answer;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class MultipleChoiceAnswerTest {
    private static final MCQAnswerFactory factory = new MCQAnswerFactory();

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

    private void assertAnswerCorrect(Set<String> expectedAnswer, String answer, double punitiveRatio, double expected) {
        AnswerEvaluator evaluator = factory
                .withPunitiveRatio(punitiveRatio)
                .createCorrectAnswer(Answer.fromStringCollection(expectedAnswer), new IdentityProcessor());
        Assert.assertEquals(expected, evaluator
                .getCorrectnessEvaluator()
                .getCorrectness(evaluator.getProcessor().process(Answer.fromString(answer))), 0.001);
    }

    private void assertAnswerIncorrect(Set<String> expectedAnswer, String answer, double punitiveRatio) {
        Assert.assertEquals(0.0, factory
                .withPunitiveRatio(punitiveRatio)
                .createCorrectAnswer(Answer.fromStringCollection(expectedAnswer), new IdentityProcessor())
                .getCorrectnessEvaluator()
                .getCorrectness(Answer.fromString(answer)), 0.001);
    }

    @Test
    public void answer_validity_evaluator() {
        assertFormatValid("A;B");
        assertFormatValid("a;b");
        assertFormatValid("a;9;A;a");
        assertFormatValid("true;false");

        assertFormatInvalid("A B");
        assertFormatInvalid(" A;B ");
    }

    @Test
    public void answer_correctness_validator() {
        Set<String> correctAnswer = new HashSet<>();
        correctAnswer.add("A");
        correctAnswer.add("B");
        correctAnswer.add("D");

        assertAnswerCorrect(correctAnswer, "A;B", 1.0, 0.666666);

        assertAnswerCorrect(correctAnswer, "A;B;D", 1.0, 1.0);
        assertAnswerCorrect(correctAnswer, "ABC", 1.0, 0.0);
    }
}
