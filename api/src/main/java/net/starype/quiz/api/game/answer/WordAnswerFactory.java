package net.starype.quiz.api.game.answer;

import java.util.Set;

public class WordAnswerFactory implements CorrectAnswerFactory {

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return WordValidityEvaluator.getInstance();
    }

    @Override
    public CorrectAnswer createCorrectAnswer(Set<Answer> answers, AnswerProcessor answerProcessor) {
        return new WordAnswer(new WordCorrectness(processList(answers, answerProcessor)));
    }

}
