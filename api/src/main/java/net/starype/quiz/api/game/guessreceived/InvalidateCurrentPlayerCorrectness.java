package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

public class InvalidateCurrentPlayerCorrectness extends ConditionalConsumer<RoundState, SettablePlayerGuessContext> {
    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        playerGuessContext.setCorrectness(0.0);
    }

//    @Override
//    public void accept(Player<?> player, String message, Double correctness, RoundState roundState) {
//        roundState.setPlayerGuessContext(new SettablePlayerGuessContext(player,
//                Objects.requireNonNullElse(correctness, 0.0), true));
//    }


}
