package net.starype.quiz.api.game.answer;

import java.util.Set;

public class WordCorrectAnswerFactory implements CorrectAnswerFactory {

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return WordValidityEvaluator.getInstance();
    }

    @Override
    public CorrectAnswer createCorrectAnswer(Set<Answer> answers, AnswerProcessor answerProcessor) {
        return new WordCorrectAnswer(new WordCorrectnessEvaluator(processList(answers, answerProcessor)));
    }

}
