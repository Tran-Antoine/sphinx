package net.starype.quiz.api.game.answer;

public interface AnswerEvaluator {

    CorrectnessEvaluator getCorrectnessEvaluator();
    ValidityEvaluator getValidityEvaluator();
    default AnswerProcessor getProcessor() {
        return new CleanStringProcessor();
    }

}
