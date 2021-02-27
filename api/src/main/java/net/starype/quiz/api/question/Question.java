package net.starype.quiz.api.question;

import net.starype.quiz.api.answer.Answer;
import net.starype.quiz.api.player.IDHolder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface Question extends IDHolder<UUID> {

    Set<QuestionTag> getTags();

    default boolean isTagAttached(String tag) {
        return getTags()
                .stream()
                .map(QuestionTag::getTag)
                .anyMatch(t -> t.equals(tag));
    }

    void registerTag(QuestionTag tag);

    void unregisterTag(QuestionTag tag);

    QuestionDifficulty getDifficulty();

    String getRawQuestion();

    String getDisplayableCorrectAnswer();

    Optional<Double> evaluateAnswer(Answer answer);

}
