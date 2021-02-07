package net.starype.quiz.api.server;

import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.game.SimpleGame;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * An immutable connection between a server and a game object.
 * <p>
 * The main purpose of {@code ServerGate} is to provide a simple way for any quiz game to update the server when needed.
 * Server gates can be generated using {@link GameServer#createGate()}, then configured with {@link #withGame(QuizGame)}.
 * <p>
 * Since the {@link QuizGame} object is the one containing the gate, in most cases it will
 * configure the server with itself as the reference for the game object that will interact with the server in the future.
 * <p>
 * However, it is sometimes useful to configure the gate with a different type of game than the one creating it (typically in
 * an inheritance context), therefore game call backs should <strong>always</strong> use the lambda parameter provided (which is
 * nothing but the configured object) instead of {@code this}. For instance, {@link SimpleGame} contains
 * a default implementation for every required method, however, if one wishes to extend it for their own implementation, they might
 * want to work with a {@code GameServer<MySimpleGameExtension>} rather than a {@code GameServer<SimpleGame>}. In this case, the gate
 * should be created as a {@code ServerGate<MySimpleGameExtension>}. So it is important that {@code SimpleGame} does not use itself
 * as a reference for game call backs (but rather use the lambda parameter), otherwise working with a {@code ServerGate<MySimpleGameExtension>} will not be possible.
 * @param <T> the type of {@code QuizGame} the server works with
 */
public class ServerGate<T extends QuizGame> {

    private GameServer<? super T> server;
    private T game;

    /**
     * Initialize a gate with the given server and no game.
     * <p>
     * Since server gates are immutable objects, instances created from this constructor will never used for call backs.
     * Such objects are exclusively used with the idea of calling {@link #withGame(QuizGame)} as soon as the game object
     * is provided.
     * @param server the game server that will receive updates from the game object
     */
    public ServerGate(GameServer<? super T> server) {
        this(server, null);
    }

    /**
     * Initialize a gate with a given server and a given game.
     * <p>
     * All interactions will be made between these two objects.
     * @param server the game server object
     * @param game the game object
     */
    public ServerGate(GameServer<? super T> server, T game) {
        this.server = server;
        this.game = game;
    }

    /**
     * Notify the server with a given action.
     * <p>
     * The action provided depends on a game object and a game server object that are <strong>both</strong> provided
     * by the gate, since it was previously set up. External instances of {@link T} should never be used in stead of the
     * lambda parameter. For more information, check the class documentation.
     * @param request the action to perform depending on the game object and the server
     */
    public void gameCallback(BiConsumer<GameServer<? super T>, T> request) {
        Objects.requireNonNull(game, "Can't perform game call backs with no game set");
        request.accept(server, game);
    }

    /**
     * Notify the server with a given action that does not require any game object.
     * <p>
     * This type of callback is more accessible, since it accepts all actions regardless of which type of
     * game the server works with.
     * <p>
     * Typically, when questions are released, the server does not require any game object, all it wants is the data of
     * the question.
     * @param request the action to perform depending on the server
     */
    public void callback(Consumer<GameServer<?>> request) {
        request.accept(server);
    }

    /**
     * Creates a copy of the gate with a specific game object.
     * @param game the given game object
     * @return the copy created
     */
    public ServerGate<T> withGame(T game) {
        return new ServerGate<>(server, game);
    }
}
