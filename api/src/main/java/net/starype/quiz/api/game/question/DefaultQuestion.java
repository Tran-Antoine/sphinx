package net.starype.quiz.api.game.question;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.answer.AnswerEvaluator;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class DefaultQuestion implements Question {

    private String rawText;
    private String rawAnswer;
    private AnswerEvaluator answerEvaluator;
    private QuestionDifficulty difficulty;
    private Set<QuestionTag> tags;
    private UUID id;

    public DefaultQuestion(String rawText, String rawAnswer, AnswerEvaluator answerEvaluator, QuestionDifficulty difficulty, Set<QuestionTag> tags) {
        this.rawText = rawText;
        this.rawAnswer = rawAnswer;
        this.answerEvaluator = answerEvaluator;
        this.difficulty = difficulty;
        this.tags = tags;
        this.id = UUID.randomUUID();
    }

    @Override
    public Set<QuestionTag> getTags() {
        return tags;
    }

    @Override
    public void registerTag(QuestionTag tag) {
        tags.add(tag);
    }

    @Override
    public void unregisterTag(QuestionTag tag) {
        tags.remove(tag);
    }

    @Override
    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public String getRawQuestion() {
        return rawText;
    }

    @Override
    public String getDisplayableCorrectAnswer() {
        return rawAnswer;
    }

    @Override
    public Optional<Double> evaluateAnswer(Answer answer) {
        Answer processed = answerEvaluator.getProcessor().process(answer);
        if(!answerEvaluator.getValidityEvaluator().isValid(processed)) {
            return Optional.empty();
        }
        return Optional.of(answerEvaluator.getCorrectnessEvaluator().getCorrectness(processed));
    }

    @Override
    public UUID getId() {
        return id;
    }

    public static class Builder {

        private String rawText;
        private String rawAnswer;
        private AnswerEvaluator answerEvaluator;
        private QuestionDifficulty difficulty;
        private Set<QuestionTag> tags;

        public Builder withRawText(String rawText) {
            this.rawText = rawText;
            return this;
        }

        public Builder withRawAnswer(String rawAnswer) {
            this.rawAnswer = rawAnswer;
            return this;
        }

        public Builder withAnswerEvaluator(AnswerEvaluator answerEvaluator) {
            this.answerEvaluator = answerEvaluator;
            return this;
        }

        public Builder withDifficulty(QuestionDifficulty difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public Builder withTags(Set<QuestionTag> tags) {
            this.tags = tags;
            return this;
        }

        public DefaultQuestion build() {
            return new DefaultQuestion(rawText, rawAnswer, answerEvaluator, difficulty, tags);
        }
    }
}
