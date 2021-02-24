package net.starype.quiz.api.database;

import net.starype.quiz.api.question.Question;

import java.util.List;
import java.util.Optional;

public interface QuizQueryable {

    List<Question> randomizedQuery(QuestionQuery queryMatcher, int maxCount);
    List<Question> listQuery(QuestionQuery queryMatcher);
    Optional<Question> pickQuery(QuestionQuery queryMatched);
}
