package net.starype.quiz.api.game.answer;

import java.util.HashSet;
import java.util.Set;

public interface CorrectAnswerFactory {

    CorrectAnswer createCorrectAnswer(Set<String> answers) throws RuntimeException;

    default CorrectAnswer createCorrectAnswer(String answer) throws  RuntimeException {
        Set<String> answersSet = new HashSet<String>();
        answersSet.add(answer);
        return createCorrectAnswer(answersSet);
    }

    CandidateValidityEvaluator getValidityEvaluator();

}
