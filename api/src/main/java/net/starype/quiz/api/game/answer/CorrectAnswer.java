package net.starype.quiz.api.game.answer;

public interface CorrectAnswer {

    AnswerCorrectnessEvaluator getCorrectnessEvaluator();
    AnswerValidCandidateEvaluator getValidCandidateEvaluator();

}
