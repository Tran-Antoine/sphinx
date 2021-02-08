package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.Event;
import net.starype.quiz.api.game.event.UpdatableHandler;
import net.starype.quiz.api.game.guessreceived.GuessReceivedAction;
import net.starype.quiz.api.game.guessreceived.GuessReceivedHead;
import net.starype.quiz.api.game.guessreceived.RoundState;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private Collection<PlayerSettable> toPlayerSet;

    private GuessReceivedHead guessReceivedHead;
    private GuessReceivedAction guessReceivedAction;

    private GuessReceivedAction giveUpReceivedConsumer;

    public StandardRound(Question pickedQuestion, GuessReceivedHead guessReceivedHead,
                         GuessReceivedAction GuessReceivedAction,
                         GuessReceivedAction giveUpReceivedConsumer,
                         List<ScoreDistribution> scoreDistributions, RoundEndingPredicate endingCondition,
                         List<EntityEligibility> playerEligibilities, RoundState roundState,
                         Collection<Event> events, Collection<PlayerSettable> toPlayersSet) {
        this.pickedQuestion = pickedQuestion;
        this.guessReceivedHead = guessReceivedHead;
        this.guessReceivedAction = GuessReceivedAction;
        this.giveUpReceivedConsumer = giveUpReceivedConsumer;
        this.scoreDistributions = scoreDistributions;
        this.endingCondition = endingCondition;
        this.playerEligibilities = playerEligibilities;
        this.roundState = roundState;
        this.events = events;
        this.toPlayerSet = toPlayersSet;
    }

    @Override
    public void start(QuizGame game, Collection<? extends Player<?>> players,
                      UpdatableHandler updatableHandler) {
        toPlayerSet.forEach(playerSettable -> playerSettable.setPlayers(players));
        if(game != null) {
            game.sendInputToServer(server -> server.onQuestionReleased(pickedQuestion));
            this.checkEndOfRound = gameRound -> game.checkEndOfRound(this);
        }
        events.forEach(updatableHandler::registerEvent);
        events.forEach(event -> event.start(updatableHandler));
    }

    @Override
    public PlayerGuessContext onGuessReceived(Player<?> source, String message) {
        Double correctness = pickedQuestion.evaluateAnswer(Answer.fromString(message)).orElse(null);
        MutableGuessContext playerGuessContext = new MutableGuessContext(source, correctness, false);

        guessReceivedHead.accept(new GuessReceivedParameters(message, correctness, roundState, playerGuessContext));
        guessReceivedAction.accept(roundState, playerGuessContext);

        checkEndOfRound();

        return playerGuessContext;
    }

    @Override
    public void onGiveUpReceived(Player<?> source) {
        giveUpReceivedConsumer.accept(roundState, new MutableGuessContext(source, 0.0, false));
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
        private GuessReceivedHead guessReceivedHead;
        private GuessReceivedAction guessReceivedAction;
        private GuessReceivedAction giveUpReceivedConsumer;
        private List<ScoreDistribution> scoreDistributions = new ArrayList<>();
        private Question question;
        private RoundEndingPredicate endingPredicate;
        private List<EntityEligibility> playerEligibility = new ArrayList<>();
        private RoundState roundState;
        private Collection<Event> events = new ArrayList<>();
        private Collection<PlayerSettable> playerSettables = new ArrayList<>();

        public Builder withGuessReceivedAction(GuessReceivedAction guessReceivedAction) {
            this.guessReceivedAction = guessReceivedAction;
            return this;
        }

        public Builder withGuessReceivedHead(GuessReceivedHead guessReceivedHead) {
            this.guessReceivedHead = guessReceivedHead;
            return this;
        }

        public Builder withGiveUpReceivedConsumer(GuessReceivedAction giveUpReceivedConsumer) {
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

        public Builder addPlayerSettable(PlayerSettable playerSettable) {
            playerSettables.add(playerSettable);
            return this;
        }

        public StandardRound build() {
            return new StandardRound(question, guessReceivedHead, guessReceivedAction,
                    giveUpReceivedConsumer, scoreDistributions, endingPredicate,
                    playerEligibility, roundState, events, playerSettables);
        }

    }

}
