package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.List;

public interface Question extends UUIDHolder {

    List<QuestionTag> getTags();

    default boolean isTagAttached(QuestionTag tag) {
        return getTags().contains(tag);
    }

    void registerTag(QuestionTag tag);

    void forgetTag(QuestionTag tag);

    QuestionDifficulty getDifficulty();

    String getQuestion();

    String getCorrectAnswer();

    int getMaxTrialAmount();

    boolean trySubmitAnswer(UUIDHolder entity, String answer);

    int getEntityTrialAmount(UUIDHolder entity);

    default boolean doesEntityStillHaveTrialLeft(UUIDHolder entity) {
        return getEntityTrialAmount(entity) >= getMaxTrialAmount() ||
                getMaxTrialAmount() == Integer.MAX_VALUE;
    }

}
