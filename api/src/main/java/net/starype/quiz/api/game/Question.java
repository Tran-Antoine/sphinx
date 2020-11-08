package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.Optional;
import java.util.Set;

public interface Question extends UUIDHolder {

    Set<QuestionTag> getTags();

    default boolean isTagAttached(QuestionTag tag) {
        return getTags().contains(tag);
    }

    void registerTag(QuestionTag tag);

    void unregisterTag(QuestionTag tag);

    QuestionDifficulty getDifficulty();

    String getRawQuestion();

    String getDisplayableCorrectAnswer();

    Optional<Double> evaluateAnswer(Answer answer);

}
