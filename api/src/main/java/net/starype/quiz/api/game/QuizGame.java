package net.starype.quiz.api.game;


import net.starype.quiz.api.server.GameServer;

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

    void start();
    void pause();
    void resume();
    void update();
    void forceStop();

    boolean isCurrentRoundFinished();
    boolean containsPlayerId(Object id);
    boolean nextRound();

    void onInputReceived(Object playerId, String message);
    void sendInputToServer(Consumer<GameServer<?>> action);
    void removePlayer(Object playerId);
}
