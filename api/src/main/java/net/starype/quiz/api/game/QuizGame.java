package net.starype.quiz.api.game;


import net.starype.quiz.api.server.GameServer;
import net.starype.quiz.api.server.ServerGate;

import java.util.function.Consumer;

/**
 * Core object of the library. Represents a quiz game containing all the necessary logic.
 * <p>
 * Quiz games should contain:
 * <ul>
 *     <li>
 *         Support for pausing and resuming (through {@link #pause()} and {@link #resume()}).
 *     </li>
 *     <li>
 *         Support for force stopping (through {@link #forceStop()}
 *     </li>
 *     <li>
 *         A way for objects depending on the game to request interaction with the server (through {@link #sendInputToServer(Consumer)}.
 *         Typically, when a question is released, it will ask the game to transmit the information to the server.
 *     </li>
 *     <li>
 *         Support for input reception (through {@link #onInputReceived(Object, String)}.
 *         All the game logic depends on the inputs sent by players. When and how input is sent depends on each
 *         implementation.
 *     </li>
 *     <li>
 *          A way to externally request the next round to start, if the current one is finished (through {@link #nextRound()}).
 *          By default, when a round terminates, the game will be paused until it is asked to continue.
 *     </li>
 *     <li>
 *         A system that detects whether the current round terminated and performs actions
 *         accordingly (through {@link #checkEndOfRound(GameRound)}.
 *     </li>
 *     <li>
 *         Other minor utility methods that can be useful externally
 *     </li>
 * </ul>
 * See {@link SimpleGame} for the main implementation.
 */
public interface QuizGame {

    /**
     * Start the game.
     * No interaction with the game object should be allowed unless a unique call to `start` was previously
     * made.
     */
    void start();

    /**
     * Pause the game.
     * The common behavior of this method should be to ignore all communications from players until {@link #resume()}
     * is called.
     */
    void pause();

    /**
     * Resume the game after it was paused.
     * If the game wasn't paused, the method has no effect
     */
    void resume();

    /**
     * Update the game object, allowing the latter to perform periodic actions such as even triggering, if needed.
     * The call frequency of update is up to the game object handler
     */
    void update();

    /**
     * Force-stop the game, shutting down all communications with players. Assuming the default game system, quiz games
     * should naturally stop when all rounds are played
     */
    void forceStop();

    /**
     * Determine whether the round currently in action is finished and should thus be skipped.
     * @return whether the current round is over
     */
    boolean isCurrentRoundFinished();

    /**
     * Retrieve whether the game contains the player with the given ID.
     * @param id an object representing a player ID
     * @return whether the object is present in the player list of the game
     */
    boolean containsPlayerId(Object id);

    /**
     * Skip the current round (usually when over) and start the following one.
     * @return whether the operation was a success
     */
    boolean nextRound();

    /**
     * Main communication gate between the players and the game logic.
     * The game handler should call this method when a player sends whatever input that must be transmitted to the game.
     * Whether the player was eligible for sending a message should be decided by the game itself, not beforehand.
     * @param playerId the ID of the player who sent a message
     * @param message the message provided
     */
    void onInputReceived(Object playerId, String message);


    /**
     * Main communication gate between the game object and a {@link GameServer}.
     * This method is often used by the game rounds, who would like to notify the server that a particular event
     * should be triggered. The default implementation of {@link QuizGame} uses `ServerGate`s, as an additional layer for
     * game-server communication.
     * @see ServerGate
     * @param action the action to perform from a `GameServer` object
     */
    void sendInputToServer(Consumer<GameServer<?>> action);

    /**
     * Remove a player of the player list, if present
     * @param playerId the ID of the player that is to be removed
     */
    void removePlayer(Object playerId);
}
