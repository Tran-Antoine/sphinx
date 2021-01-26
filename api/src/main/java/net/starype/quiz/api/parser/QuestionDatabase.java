package net.starype.quiz.api.parser;

import net.starype.quiz.api.game.question.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionDatabase {
    void sync();
    List<Question> randomizedQuery(QuestionQuery queryMatcher, int maxCount);
    List<Question> listQuery(QuestionQuery queryMatcher);
    Optional<Question> pickQuery(QuestionQuery queryMatched);
}
