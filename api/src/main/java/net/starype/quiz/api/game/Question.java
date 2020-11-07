package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

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

    default double submitAnswer(String answer) {
        return 1; // TODO: Use answer matcher
    }

}
