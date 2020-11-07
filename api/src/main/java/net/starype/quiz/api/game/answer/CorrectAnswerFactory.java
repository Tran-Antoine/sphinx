package net.starype.quiz.api.game.answer;

import java.util.HashSet;
import java.util.Set;

public interface CorrectAnswerFactory {

    CorrectAnswer createCorrectAnswer(Set<String> answers);

    default CorrectAnswer createCorrectAnswer(String answer) {
        Set<String> answersSet = new HashSet<String>();
        answersSet.add(answer);
        return createCorrectAnswer(answersSet);
    }

    ValidityEvaluator getValidityEvaluator();

}
