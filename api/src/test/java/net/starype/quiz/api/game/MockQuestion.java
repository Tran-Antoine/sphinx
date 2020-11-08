package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

class MockQuestion implements Question {

    @Override
    public Optional<Double> evaluateAnswer(Answer answer) {
        if(answer.getAnswerText().equals("CORRECT")) {
            return Optional.of(1.0);
        }
        return Optional.of(0.0);
    }

    @Override
    public Set<QuestionTag> getTags() {
        return null;
    }

    @Override
    public void registerTag(QuestionTag tag) {
    }

    @Override
    public void unregisterTag(QuestionTag tag) {
    }

    @Override
    public QuestionDifficulty getDifficulty() {
        return null;
    }

    @Override
    public String getRawQuestion() {
        return null;
    }

    @Override
    public String getDisplayableCorrectAnswer() {
        return null;
    }

    @Override
    public UUID getUUID() {
        return null;
    }
}
