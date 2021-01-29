package net.starype.quiz.api.game.guessprocess;

public interface QuadriConsumer<A, B, C, D> {
    void accept(A a, B b, C c, D d);

    default QuadriConsumer<A, B, C, D> andThen(QuadriConsumer<A, B, C, D> after) {
        return (A a, B b, C c, D d) ->
        { accept(a, b, c, d); after.accept(a, b, c, d);};
    }
}
