package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

import java.util.function.BiPredicate;

/**
 * BiPredicate that tests if the correctness of the answer is 1
 */
public class IsCorrectnessOne implements BiPredicate<RoundState, SettablePlayerGuessContext> {

//    @Override
//    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
//        if(Math.abs(playerGuessContext.getCorrectness() - 1) < 0.001) {
//            setToTrue();
//        } else {
//            setToFalse();
//        }
//    }

    @Override
    public boolean test(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        return Math.abs(playerGuessContext.getCorrectness() - 1) < 0.001;
    }
}
