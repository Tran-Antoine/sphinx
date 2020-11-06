package net.starype.quiz.api.game.answer;

public interface CorrectAnswer {

    CorrectnessEvaluator getCorrectnessEvaluator();
    CandidateValidityEvaluator getCandidateValidityEvaluator();

}
