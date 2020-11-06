package net.starype.quiz.api.game.answer;

import java.util.Set;

public interface CorrectAnswerFactory {

    CorrectAnswer createCorrectAnswer(Set<String> answer) throws RuntimeException;
    CandidateValidityEvaluator getValidityEvaluator();

}
