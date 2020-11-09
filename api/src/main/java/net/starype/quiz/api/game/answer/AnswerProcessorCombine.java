package net.starype.quiz.api.game.answer;

import java.util.List;

public class AnswerProcessorCombine implements AnswerProcessor {

    private final AnswerProcessor firstAnswerProcessor;
    private final AnswerProcessor secondAnswerProcessor;

    @Override
    public Answer process(Answer answer) {
        return firstAnswerProcessor.process(secondAnswerProcessor.process(answer));
    }

    private AnswerProcessorCombine(AnswerProcessor firstAnswerProcessor, AnswerProcessor secondAnswerProcessor) {
        this.secondAnswerProcessor = secondAnswerProcessor;
        this.firstAnswerProcessor = firstAnswerProcessor;
    }

    public class Builder {
        private AnswerProcessor currentAnswerProcessor = new AnswerProcessorIdentity();

        public Builder withProcessor(AnswerProcessor answerProcessor) {
            currentAnswerProcessor = new AnswerProcessorCombine(currentAnswerProcessor, answerProcessor);
            return this;
        }

        public AnswerProcessor build() {
            return currentAnswerProcessor;
        }
    }
}
