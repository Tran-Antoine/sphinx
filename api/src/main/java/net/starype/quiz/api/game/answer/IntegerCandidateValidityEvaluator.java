package net.starype.quiz.api.game.answer;

public class IntegerCandidateValidityEvaluator implements CandidateValidityEvaluator {

    private IntegerCandidateValidityEvaluator()
    {}

    private static IntegerCandidateValidityEvaluator instance = null;

    public static synchronized IntegerCandidateValidityEvaluator getInstance()
    {
        if(instance == null) {
            instance = new IntegerCandidateValidityEvaluator();
        }
        return instance;
    }

    @Override
    public boolean isValidCandidate(Answer answer) {
        return answer
                .getAnswer()
                .strip()
                .matches("^[+-]?[0-9]+");
    }
}
