package net.starype.quiz.api.game.answer;

import java.util.HashSet;
import java.util.Set;

public interface CorrectAnswerFactory {

    CorrectAnswer createCorrectAnswer(Set<Answer> answers);

    default CorrectAnswer createCorrectAnswer(Answer answer, AnswerParser parser) {
        Set<Answer> answersSet = new HashSet<>();
        answersSet.add(parser.process(answer));
        return createCorrectAnswer(answersSet);
    }

    ValidityEvaluator getValidityEvaluator();

}
