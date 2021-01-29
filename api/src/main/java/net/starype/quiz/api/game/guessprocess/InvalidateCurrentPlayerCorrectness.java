package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.player.Player;

public class InvalidateCurrentPlayerCorrectness extends ConditionalConsumer<RoundState> {
    @Override
    public void execute(RoundState roundState) {
        Player<?> player = roundState.getPlayerGuessContext().getPlayer();
        boolean eligible = roundState.getPlayerGuessContext().isEligible();
        roundState.setPlayerGuessContext(new PlayerGuessContext(player, 0.0, eligible));
    }

//    @Override
//    public void accept(Player<?> player, String message, Double correctness, RoundState roundState) {
//        roundState.setPlayerGuessContext(new PlayerGuessContext(player,
//                Objects.requireNonNullElse(correctness, 0.0), true));
//    }


}
