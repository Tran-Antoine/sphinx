package net.starype.quiz.api.game.player;

/**
 * An object that can be represented by an ID.
 * The reason why `IDHolder` is generic is that some libraries use their own ID system, which makes IDHolders easier
 * to use if no type like UUID is enforced
 * @param <T> the type of ID hold by the object
 */
public interface IDHolder<T> {

    /**
     * @return the ID of the holder
     */
    T getId();
}
