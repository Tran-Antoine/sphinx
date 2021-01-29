package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.game.guessprocess.RoundState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class StandardRound implements GameRound, SettableRound {

    private Collection<? extends IDHolder<?>> players;
    private Question pickedQuestion;
    private List<ScoreDistribution> scoreDistributions = new ArrayList<>();
    private RoundEndingPredicate endingCondition;
    private List<EntityEligibility> playerEligibility;
    private RoundState roundState;

    public StandardRound(Question pickedQuestion) {
        this.pickedQuestion = pickedQuestion;
    }

    @Override
    public void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler) {
        this.players = players;
        game.sendInputToServer(server -> server.onQuestionReleased(pickedQuestion));
    }

    @Override
    public void addScoreDistribution(ScoreDistribution scoreDistribution) {
        scoreDistributions.add(scoreDistribution);
    }

    @Override
    public void withEndingCondition(RoundEndingPredicate roundEndingPredicate) {
        endingCondition = roundEndingPredicate;
    }

    @Override
    public void addPlayerEligibility(EntityEligibility entityEligibility) {
        playerEligibility.add(entityEligibility);
    }

    @Override
    public PlayerGuessContext onGuessReceived(Player<?> source, String message) {
        Double correctness = pickedQuestion.evaluateAnswer(Answer.fromString(message)).orElse(null);

        //TODO : Return statement

        return null;
    }
}
