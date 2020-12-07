package net.starype.quiz.api.game.answer;

public interface AnswerEvaluator {

    CorrectnessEvaluator getCorrectnessEvaluator();
    ValidityEvaluator getValidityEvaluator();
    AnswerProcessor getProcessor();

}
