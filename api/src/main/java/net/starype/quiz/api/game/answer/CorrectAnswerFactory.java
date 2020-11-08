package net.starype.quiz.api.game.answer;

import java.util.HashSet;
import java.util.Set;

public interface CorrectAnswerFactory {

    CorrectAnswer createCorrectAnswer(Set<Answer> answers);

    default CorrectAnswer createCorrectAnswer(Answer answer) {
        Set<Answer> answersSet = new HashSet<>();
        answersSet.add(answer);
        return createCorrectAnswer(answersSet);
    }

    default CorrectAnswer createCorrectAnswer(String text) {
        return createCorrectAnswer(Answer.fromString(text));
    }

    ValidityEvaluator getValidityEvaluator();

}
