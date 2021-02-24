package net.starype.quiz.api.answer;

import java.util.Set;

public class ExactAnswerEvaluator implements AnswerEvaluator {

    private Set<Answer> answers;
    private AnswerProcessor processor;

    public ExactAnswerEvaluator(Set<Answer> answers, AnswerProcessor processor) {
        this.answers = answers;
        this.processor = processor;
    }

    @Override
    public CorrectnessEvaluator getCorrectnessEvaluator() {
        return new ExactAnswerCorrectness(answers);
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return new AlwaysValid();
    }

    @Override
    public AnswerProcessor getProcessor() {
        return processor;
    }
}
