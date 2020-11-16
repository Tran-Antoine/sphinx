package net.starpye.quiz.discordimpl.test;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.game.question.QuestionDifficulty;
import net.starype.quiz.api.game.question.QuestionTag;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class MockQuestion implements Question {

    @Override
    public Set<QuestionTag> getTags() {
        return null;
    }

    @Override
    public void registerTag(QuestionTag tag) { }

    @Override
    public void unregisterTag(QuestionTag tag) { }

    @Override
    public QuestionDifficulty getDifficulty() {
        return null;
    }

    @Override
    public String getRawQuestion() {
        return "What is the color of the white horse of Henry IV?";
    }

    @Override
    public String getDisplayableCorrectAnswer() {
        return "White";
    }

    @Override
    public Optional<Double> evaluateAnswer(Answer answer) {
        return answer.getAnswerText().equalsIgnoreCase(getDisplayableCorrectAnswer())
                ? Optional.of(1.0)
                : Optional.of(0.0);
    }

    @Override
    public UUID getUUID() {
        return null;
    }
}
