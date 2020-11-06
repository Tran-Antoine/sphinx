package net.starype.quiz.api.game.answer;

public class WordCandidateValidityEvaluator implements CandidateValidityEvaluator {

    private WordCandidateValidityEvaluator()
    {}

    private static WordCandidateValidityEvaluator instance = null;

    public static synchronized WordCandidateValidityEvaluator getInstance()
    {
        if(instance == null) {
            instance = new WordCandidateValidityEvaluator();
        }
        return instance;
    }

    @Override
    public boolean isValidCandidate(Answer answer) {
        return answer
                .getAnswer()
                .strip()
                .matches("[a-zA-Z'à-ÿ]+");
    }
}
