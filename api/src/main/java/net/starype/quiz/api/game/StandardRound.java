package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.Event;
import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.guessreceived.GuessReceivedHead;
import net.starype.quiz.api.game.guessreceived.RoundState;
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
    private List<ScoreDistribution> scoreDistributions;
    private RoundEndingPredicate endingCondition;
    private List<EntityEligibility> playerEligibilities;
    private RoundState roundState;
    private List<Event> events = new ArrayList<>();
    private EventHandler eventHandler;

    private GuessReceivedHead guessReceivedHead;
    private BiConsumer<RoundState, SettablePlayerGuessContext> guessReceivedConsumer;

    private BiConsumer<RoundState, SettablePlayerGuessContext> giveUpReceivedConsumer;

    public StandardRound(Question pickedQuestion, GuessReceivedHead guessReceivedHead,
                         BiConsumer<RoundState, SettablePlayerGuessContext> guessReceivedConsumer,
                         BiConsumer<RoundState, SettablePlayerGuessContext> giveUpReceivedConsumer,
                         List<ScoreDistribution> scoreDistributions, RoundEndingPredicate endingCondition,
                         List<EntityEligibility> playerEligibilities, RoundState roundState,
                         List<Event> events) {
        this.pickedQuestion = pickedQuestion;
        this.guessReceivedHead = guessReceivedHead;
        this.guessReceivedConsumer = guessReceivedConsumer;
        this.giveUpReceivedConsumer = giveUpReceivedConsumer;
        this.scoreDistributions = scoreDistributions;
        this.endingCondition = endingCondition;
        this.playerEligibilities = playerEligibilities;
        this.roundState = roundState;
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
    public void onGiveUpReceived(Player<?> source) {
        giveUpReceivedConsumer.accept(roundState, new SettablePlayerGuessContext(source, 0.0, false));
    }

    @Override
    public EntityEligibility initPlayerEligibility() {
        return playerEligibilities.get(0);
    }

    @Override
    public RoundEndingPredicate initEndingCondition() {
        return endingCondition;
    }

    @Override
    public ScoreDistribution initScoreDistribution() {
        return scoreDistributions.get(0);
    }

    @Override
    public GameRoundReport initReport(Map<Player<?>, Double> standings) {
        return null;
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }

    public static class Builder {
        private GuessReceivedHead guessReceivedHead;
        private BiConsumer<RoundState, SettablePlayerGuessContext> guessReceivedConsumer;
        private BiConsumer<RoundState, SettablePlayerGuessContext> giveUpReceivedConsumer;
        private List<ScoreDistribution> scoreDistributions = new ArrayList<>();
        private Question question;
        private RoundEndingPredicate endingCondition;
        private List<EntityEligibility> playerEligibility = new ArrayList<>();
        private RoundState roundState;
        private List<Event> events = new ArrayList<>();

        public Builder withGuessReceivedConsumer(BiConsumer<RoundState, SettablePlayerGuessContext> guessReceivedConsumer) {
            this.guessReceivedConsumer = guessReceivedConsumer;
            return this;
        }

        public Builder withGuessReceivedHead(GuessReceivedHead guessReceivedHead) {
            this.guessReceivedHead = guessReceivedHead;
            return this;
        }

        public Builder withGiveUpReceivedConsumer(BiConsumer<RoundState,
                SettablePlayerGuessContext> giveUpReceivedConsumer) {
            this.giveUpReceivedConsumer = giveUpReceivedConsumer;
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

        public Builder withEndingCondition(RoundEndingPredicate endingCondition) {
            this.endingCondition = endingCondition;
            return this;
        }

        public Builder withRoundState(RoundState roundState) {
            this.roundState = roundState;
            return this;
        }

        public Builder addPlayerEligibility(EntityEligibility playerEligibility) {
            this.playerEligibility.add(playerEligibility);
            return this;
        }

        public Builder addEvent(Event event) {
            events.add(event);
            return this;
        }

        public StandardRound build() {
            return new StandardRound(question, guessReceivedHead, guessReceivedConsumer,
                    giveUpReceivedConsumer, scoreDistributions, endingCondition,
                    playerEligibility, roundState, events);
        }

    }

}
