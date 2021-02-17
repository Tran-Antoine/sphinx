package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.Event;
import net.starype.quiz.api.game.event.UpdatableHandler;
import net.starype.quiz.api.game.guessreceived.GuessReceivedAction;
import net.starype.quiz.api.game.guessreceived.RoundState;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static net.starype.quiz.api.game.ScoreDistribution.Standing;

public class StandardRound implements GameRound {

    private Question pickedQuestion;
    private List<ScoreDistribution> scoreDistributions;
    private RoundEndingPredicate endingCondition;
    private List<EntityEligibility> playerEligibilities;
    private RoundState roundState;
    private Collection<Event> events;
    private Consumer<GameRound> checkEndOfRound = gameRound -> {};

    private GuessReceivedAction guessReceivedAction;

    private GuessReceivedAction giveupReceivedAction;

    public StandardRound(Question pickedQuestion,
                         GuessReceivedAction GuessReceivedAction,
                         GuessReceivedAction giveupReceivedAction,
                         List<ScoreDistribution> scoreDistributions, RoundEndingPredicate endingCondition,
                         List<EntityEligibility> playerEligibilities, RoundState roundState,
                         Collection<Event> events) {
        this.pickedQuestion = pickedQuestion;
        this.guessReceivedAction = GuessReceivedAction;
        this.giveupReceivedAction = giveupReceivedAction;
        this.scoreDistributions = scoreDistributions;
        this.endingCondition = endingCondition;
        this.playerEligibilities = playerEligibilities;
        this.roundState = roundState;
        this.events = events;
    }

    @Override
    public void start(QuizGame game, Collection<? extends Player<?>> players,
                      UpdatableHandler updatableHandler) {
        roundState.initPlayers(players);
        if(game != null) {
            game.sendInputToServer(server -> server.onQuestionReleased(pickedQuestion));
            this.checkEndOfRound = gameRound -> game.checkEndOfRound(this);
        }
        endingCondition.initRoundState(roundState);
        events.forEach(updatableHandler::registerEvent);
        events.forEach(event -> event.start(updatableHandler));
    }

    @Override
    public PlayerGuessContext onGuessReceived(Player<?> source, String message) {

        Optional<Double> optCorrectness = pickedQuestion.evaluateAnswer(Answer.fromString(message));


        MutableGuessContext playerGuessContext = new MutableGuessContext(source, optCorrectness.orElse(0.0), false,
                Answer.fromString(message), optCorrectness.isPresent());

        guessReceivedAction.accept(roundState, playerGuessContext);

        checkEndOfRound();

        return playerGuessContext;
    }

    @Override
    public void onGiveUpReceived(Player<?> source) {
        giveupReceivedAction.accept(roundState, new MutableGuessContext(source, 0.0, false,
                Answer.fromString(""), false));
    }

    @Override
    public void checkEndOfRound() {
        checkEndOfRound.accept(this);
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
    public GameRoundReport initReport(List<Standing> standings) {
        return new SimpleGameReport(standings);
    }

    @Override
    public void onRoundStopped() {
        events.forEach((Event::shutDown));
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }

    public static class Builder {
        private GuessReceivedAction guessReceivedAction;
        private GuessReceivedAction giveupReceivedAction;
        private List<ScoreDistribution> scoreDistributions = new ArrayList<>();
        private Question question;
        private RoundEndingPredicate endingPredicate;
        private List<EntityEligibility> playerEligibility = new ArrayList<>();
        private RoundState roundState;
        private Collection<Event> events = new ArrayList<>();

        public Builder withGuessReceivedAction(GuessReceivedAction guessReceivedAction) {
            this.guessReceivedAction = guessReceivedAction;
            return this;
        }

        public Builder withGiveUpReceivedConsumer(GuessReceivedAction giveUpReceivedConsumer) {
            this.giveupReceivedAction = giveUpReceivedConsumer;
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
            this.endingPredicate = endingCondition;
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
            return new StandardRound(question, guessReceivedAction,
                    giveupReceivedAction, scoreDistributions, endingPredicate,
                    playerEligibility, roundState, events);
        }

    }

}
