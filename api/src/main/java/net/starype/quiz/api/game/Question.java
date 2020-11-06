package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.List;

public interface Question extends UUIDHolder {

    List<QuestionTag> getTags();

    default boolean isTagAttached(QuestionTag tag) {
        return getTags().contains(tag);
    }

    void registerTag(QuestionTag tag);

    void unregisterTag(QuestionTag tag);

    QuestionDifficulty getDifficulty();

    String getRawQuestion();

    String getCorrectAnswer();

    int getMaxTrialAmount();

}
