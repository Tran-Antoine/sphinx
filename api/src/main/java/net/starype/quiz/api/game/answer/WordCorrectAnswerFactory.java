package net.starype.quiz.api.game.answer;

import java.util.Set;

public class WordCorrectAnswerFactory implements CorrectAnswerFactory {

    @Override
    public CandidateValidityEvaluator getValidityEvaluator() {
        return WordCandidateValidityEvaluator.getInstance();
    }

    @Override
    public CorrectAnswer createCorrectAnswer(Set<String> answer) throws RuntimeException {
        if(!answer.stream()
                .allMatch(s ->
                        WordCandidateValidityEvaluator.getInstance().isValidCandidate(new Answer(s))
                ))
        {
            throw new RuntimeException("At least one of the proposed answers doesn't statify the format specification");
        }

        return new WordCorrectAnswer(new WordCorrectnessEvaluator(answer));
    }

}
