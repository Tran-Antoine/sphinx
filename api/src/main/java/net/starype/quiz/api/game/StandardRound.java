package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.guessprocess.GuessReceivedHead;
import net.starype.quiz.api.game.guessprocess.RoundState;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class StandardRound implements GameRound {

    private Collection<? extends IDHolder<?>> players;
    private Question pickedQuestion;
    private List<ScoreDistribution> scoreDistributions = new ArrayList<>();
    private RoundEndingPredicate endingCondition;
    private List<EntityEligibility> playerEligibility;
    private RoundState roundState;

    private GuessReceivedHead guessReceivedHead;
    private BiConsumer<RoundState, SettablePlayerGuessContext> guessReceivedConsumer;

    public StandardRound(Question pickedQuestion, GuessReceivedHead guessReceivedHead,
                         BiConsumer<RoundState, SettablePlayerGuessContext> guessReceivedConsumer,
                         List<ScoreDistribution> scoreDistributions) {
        this.pickedQuestion = pickedQuestion;
        this.guessReceivedHead = guessReceivedHead;
        this.guessReceivedConsumer = guessReceivedConsumer;
        this.scoreDistributions = scoreDistributions;
    }

    @Override
    public void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler) {
        this.players = players;
        game.sendInputToServer(server -> server.onQuestionReleased(pickedQuestion));
    }

    @Override
    public PlayerGuessContext onGuessReceived(Player<?> source, String message) {
        Double correctness = pickedQuestion.evaluateAnswer(Answer.fromString(message)).orElse(null);
        SettablePlayerGuessContext playerGuessContext = new SettablePlayerGuessContext(source, correctness, false);

        guessReceivedHead.accept(source, message, correctness, roundState, playerGuessContext);
        guessReceivedConsumer.accept(roundState, playerGuessContext);

        return playerGuessContext;
    }

    @Override
    public void onGiveUpReceived(IDHolder<?> source) {

    }

    @Override
    public EntityEligibility initPlayerEligibility() {
        return null;
    }

    @Override
    public RoundEndingPredicate initEndingCondition() {
        return null;
    }

    @Override
    public List<ScoreDistribution> initScoreDistribution() {
        return null;
    }

    @Override
    public GameRoundReport initReport(Map<Player<?>, Double> standings) {
        return null;
    }



    @Override
    public GameRoundContext getContext() {
        return null;
    }

    public static class Builder {
        private GuessReceivedHead guessReceivedHead;
        private BiConsumer<RoundState, SettablePlayerGuessContext> guessReceivedConsumer;
        private List<ScoreDistribution> scoreDistributions = new ArrayList<>();
        private Question question;

        public Builder withGuessReceivedConsumer(BiConsumer<RoundState, SettablePlayerGuessContext> guessReceivedConsumer) {
            this.guessReceivedConsumer = guessReceivedConsumer;
            return this;
        }

        public Builder withGuessReceivedHead(GuessReceivedHead guessReceivedHead) {
            this.guessReceivedHead = guessReceivedHead;
            return this;
        }

        public Builder withQuestion(Question question) {
            this.question = question;
            return this;
        }

        public Builder addScoreDistribution(ScoreDistribution scoreDistribution) {
            scoreDistributions.add(scoreDistribution);
            return this;
        }

        public StandardRound build() {
            return new StandardRound(question, guessReceivedHead, guessReceivedConsumer,
                    scoreDistributions);
        }
    }

}
