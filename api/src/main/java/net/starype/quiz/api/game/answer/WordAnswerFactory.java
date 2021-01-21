package net.starype.quiz.api.game.answer;

import java.util.Set;

public class WordAnswerFactory implements CorrectAnswerFactory {

    public ValidityEvaluator getValidityEvaluator() {
        return WordValidityEvaluator.getInstance();
    }

    @Override
    public AnswerEvaluator createCorrectAnswer(Set<Answer> answers, AnswerProcessor answerProcessor) {
        return new WordAnswerEvaluator(new WordCorrectness(processList(answers, answerProcessor)));
    }

}
