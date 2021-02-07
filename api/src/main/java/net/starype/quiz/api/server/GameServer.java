package net.starype.quiz.api.server;

import net.starype.quiz.api.game.GameRound;
import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.MutableGuessContext;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.game.event.UpdatableHandler;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;

import java.util.Collection;
import java.util.List;

/**
 * Core object of library. Represents a server with no game logic that listens to game events.
 * <p>
 * All quiz implementations should at least have a {@link QuizGame} object with a server that receives
 * updates from the game via a {@link ServerGate}. A server's purpose should usually be to display
 * information when received, in its own custom way. For the purpose of flexibility, the server is generic
 * thus determines what type of quiz game it works with. Servers should usually never store game instances
 * themselves, since the latter are provided in the event methods that should require them. Ideally, a single server
 * instance should be able to work with as many games of the right type at the same time as needed. All additional
 * game logic should be implemented in the quiz game rather than in the server, since all servers do is perform
 * actions when events occur, such as displaying a question, showing the results of a round, notify players when
 * the game ends, and so on.
 * @param <T> the type of {@link QuizGame} the server works with
 */
public interface GameServer<T extends QuizGame> {

    /**
     * Perform appropriate actions when a game object notifies the server that a game round was terminated.
     * <p>
     * Is provided a {@link GameRoundReport} instance that contains useful information about the round, such
     * as which player won, the standings, and so on.
     * @param report the report containing useful information about the round
     * @param game the game object that notified the server
     */
    void onRoundEnded(GameRoundReport report, T game);

    /**
     * Perform appropriate actions when a game object notifies the server that a new round is about to start.
     * Note that this event will also be triggered for the very first round. Note also that {@code onRoundStarted} should,
     * conventionally, be triggered before {@link GameRound#start(QuizGame, Collection, UpdatableHandler)}
     * @param game the game object that notifies the server
     * @param firstRound whether the very first round is about to start or not
     */
    void onRoundStarting(T game, boolean firstRound);

    /**
     * Perform appropriate actions when a game object notifies the server that the game is over (usually when
     * all rounds are terminated).
     * <p>
     * Note that in such a case, {@link #onRoundEnded(GameRoundReport, QuizGame)} should
     * by convention be called before this method, since it's only after the last round ends that the game itself ends.
     * <p>
     * Is also provided a list of players ordered by final score, from last to first that can be used as display
     * information.
     * @param playerStandings the list of players ordered by score
     * @param game the game object that notified the server
     */
    void onGameOver(List<? extends Player<?>> playerStandings, T game);

    /**
     * Perform appropriate actions when a game object notifies the server that an eligible player sent a guess.
     * <p>
     * Is provided a {@link MutableGuessContext} object that contains useful information about who the player is,
     * the accuracy of their answer (from {@code 0.0} to {@code 1.0}) and whether after sending this guess they
     * will be eligible for future guesses in this round. Typically, if the accuracy is {@code 100%}, the eligible
     * should, in most cases, be {@code false} since there is usually no point in accepting further answers from
     * a player that already guessed the correct one.
     * @param context the context containing useful information about the guess
     */
    void onPlayerGuessed(PlayerGuessContext context);

    /**
     * Perform appropriate actions when a game object notifies the server that a non eligible player tried
     * to send a guess.
     * <p>
     * This event might be let empty if the server just ignores non eligible guesses. It can be used to let the
     * player know that their guess was not taken into account.
     * @param player the non eligible player that sent a guess
     */
    void onNonEligiblePlayerGuessed(Player<?> player);

    /**
     * Perform appropriate actions when a game object notifies the server that a player gave up on the current
     * round.
     * <p>
     * Players who give up become non eligible players for the rest of the round.
     * @param player the player who gave up on the round
     */
    void onPlayerGaveUp(Player<?> player);

    /**
     * Perform appropriate actions when a game object notifies the server that a player's score has been
     * updated.
     * <p>
     * Usually, scores are never updated before the end of the round. Conventionally, since score updates
     * are part of a round, score updates should be called before {@link #onRoundEnded(GameRoundReport, QuizGame)}.
     * @param player the player whose score was updated
     */
    void onPlayerScoreUpdated(Player<?> player);

    /**
     * Perform appropriate actions when a game object notifies the server that a question was released.
     * <p>
     * Usually, this method will be used to display the given question so that the players can read it.
     * @param question the question that is to be revealed to the players
     */
    void onQuestionReleased(Question question);

    /**
     * Generates a {@link ServerGate} from the server instance, with no game object set.
     * <p>
     * See {@link ServerGate#withGame(QuizGame)} for further information.
     * @return a server gate object targeting the server instance
     */
    default ServerGate<T> createGate() {
        return new ServerGate<>(this);
    }
}
