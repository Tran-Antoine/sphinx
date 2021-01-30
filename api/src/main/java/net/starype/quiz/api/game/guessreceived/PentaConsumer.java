package net.starype.quiz.api.game.guessreceived;

public interface PentaConsumer<A, B, C, D, E> {

    void accept(A a, B b, C c, D d, E e);

    default PentaConsumer<A, B, C, D, E> andThen(PentaConsumer<A, B, C, D, E> after) {
        return (a, b, c, d, e) ->
        { accept(a, b, c, d, e); after.accept(a, b, c, d, e);};
    }
}
