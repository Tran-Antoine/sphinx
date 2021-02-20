package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.event.Updatable;
import net.starype.quiz.api.game.event.UpdatableHandler;
import net.starype.quiz.api.game.round.GuessReceivedAction;
import net.starype.quiz.api.game.round.RoundState;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static net.starype.quiz.api.game.ScoreDistribution.Standing;

public class StandardRound implements GameRound {

    private Question pickedQuestion;
    private ScoreDistribution scoreDistribution;
    private EndingPredicate endingCondition;
    private EntityEligibility playerEligibility;
    private RoundState roundState;
    private Collection<Updatable> updatables;
    private final AtomicBoolean hasRoundEnded;
    private Consumer<GameRoundContext> callback = context -> {};

    private GuessReceivedAction guessReceivedAction;
    private GuessReceivedAction giveUpReceivedAction;

    public StandardRound(Question pickedQuestion,
                         GuessReceivedAction GuessReceivedAction,
                         GuessReceivedAction giveUpReceivedAction,
                         ScoreDistribution scoreDistribution, EndingPredicate endingCondition,
                         EntityEligibility playerEligibility, RoundState roundState,
                         Collection<Updatable> updatables) {
        this.pickedQuestion = pickedQuestion;
        this.guessReceivedAction = GuessReceivedAction;
        this.giveUpReceivedAction = giveUpReceivedAction;
        this.scoreDistribution = scoreDistribution;
        this.endingCondition = endingCondition;
        this.playerEligibility = playerEligibility;
        this.roundState = roundState;
        this.updatables = updatables;
        hasRoundEnded = new AtomicBoolean(true);
    }

    @Override
    public void start(QuizGame game, Collection<? extends Player<?>> players,
                      UpdatableHandler updatableHandler) {
        roundState.initPlayers(players);
        if(game == null) {
            throw new IllegalStateException("Game cannot be null");
        }
        game.sendInputToServer(server -> server.onQuestionReleased(pickedQuestion));
        this.callback = game::onRoundEnded;
        updatables.forEach(updatableHandler::registerEvent);
        updatables.forEach(event -> event.start(updatableHandler));
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
        giveUpReceivedAction.accept(roundState, new MutableGuessContext(source, 0.0, false,
                Answer.fromString(""), false));
        checkEndOfRound();
    }

    @Override
    public void checkEndOfRound() {
        synchronized (hasRoundEnded) {
            if(!endingCondition.ends() || hasRoundEnded.get()) {
                return;
            }
            hasRoundEnded.set(true);
            onRoundStopped();
            callback.accept(new GameRoundContext(this));
        }
    }

    @Override
    public EntityEligibility initPlayerEligibility() {
        return playerEligibility;
    }

    @Override
    public EndingPredicate initEndingCondition() {
        return endingCondition;
    }

    @Override
    public ScoreDistribution initScoreDistribution() {
        return scoreDistribution;
    }

    @Override
    public GameRoundReport initReport(List<Standing> standings) {
        return new SimpleGameReport(standings);
    }

    @Override
    public void onRoundStopped() {
        updatables.forEach((Updatable::shutDown));
    }

    @Override
    public GameRoundContext getContext() {
        return new GameRoundContext(this);
    }

    public static class Builder {
        private GuessReceivedAction guessReceivedAction;
        private GuessReceivedAction giveUpReceivedAction;
        private ScoreDistribution scoreDistribution;
        private Question question;
        private EndingPredicate endingPredicate;
        private EntityEligibility playerEligibility;
        private RoundState roundState;
        private Collection<Updatable> updatables = new ArrayList<>();

        public Builder withGuessReceivedAction(GuessReceivedAction guessReceivedAction) {
            this.guessReceivedAction = guessReceivedAction;
            return this;
        }

        public Builder withGiveUpReceivedConsumer(GuessReceivedAction giveUpReceivedConsumer) {
            this.giveUpReceivedAction = giveUpReceivedConsumer;
            return this;
        }

        public Builder withQuestion(Question question) {
            this.question = question;
            return this;
        }

        public Builder withScoreDistribution(ScoreDistribution scoreDistribution) {
            this.scoreDistribution = scoreDistribution;
            return this;
        }

        public Builder withEndingCondition(EndingPredicate endingCondition) {
            this.endingPredicate = endingCondition;
            return this;
        }

        public Builder withRoundState(RoundState roundState) {
            this.roundState = roundState;
            return this;
        }

        public Builder withPlayerEligibility(EntityEligibility playerEligibility) {
            this.playerEligibility = playerEligibility;
            return this;
        }

        public Builder addEvent(Updatable updatable) {
            updatables.add(updatable);
            return this;
        }

        public StandardRound build() {
            return new StandardRound(question, guessReceivedAction,
                    giveUpReceivedAction, scoreDistribution, endingPredicate,
                    playerEligibility, roundState, updatables);
        }

    }

}
