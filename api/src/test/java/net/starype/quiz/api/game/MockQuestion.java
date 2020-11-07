package net.starype.quiz.api.game;

import java.util.Set;
import java.util.UUID;

class MockQuestion implements Question {

    @Override
    public double submitAnswer(String answer) {
        if (answer.equals("CORRECT")) {
            return 1;
        }
        return 0;
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
