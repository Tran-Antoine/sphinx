package net.starype.quiz.api.game.answer;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface CorrectAnswerFactory {

    CorrectAnswer createCorrectAnswer(Set<Answer> answers, AnswerParser parser);

    default Set<Answer> parseList(Set<Answer> answers, AnswerParser parser) {
        return answers.stream()
                .map(parser::process)
                .collect(Collectors.toSet());
    }

    default CorrectAnswer createCorrectAnswer(Answer answer, AnswerParser parser) {
        Set<Answer> answersSet = new HashSet<>();
        answersSet.add(answer);
        return createCorrectAnswer(answersSet, parser);
    }

    ValidityEvaluator getValidityEvaluator();

}
