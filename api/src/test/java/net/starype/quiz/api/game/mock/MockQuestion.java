package net.starype.quiz.api.game.mock;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.game.question.QuestionDifficulty;
import net.starype.quiz.api.game.question.QuestionTag;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class MockQuestion implements Question {

    @Override
    public Optional<Double> evaluateAnswer(Answer answer) {
        String text = answer.getAnswerText();
        double result;
        switch (text) {
            case "correct": result = 1; break;
            case "pretty-correct": result = 0.66; break;
            case "kinda-correct": result = 0.33; break;
            default: result = 0;
        }
        return Optional.of(result);
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
        return "How do you spell the word 'correct' ?";
    }

    @Override
    public String getDisplayableCorrectAnswer() {
        return "correct";
    }

    @Override
    public UUID getId() {
        return null;
    }
}
