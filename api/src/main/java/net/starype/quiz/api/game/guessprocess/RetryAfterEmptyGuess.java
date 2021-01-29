package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.player.Player;

import java.util.Objects;

public class RetryAfterEmptyGuess extends HeadConsumer {

    @Override
    public void accept(Player<?> player, String message, Double correctness, RoundState roundState) {
        roundState.setPlayerGuessContext(new PlayerGuessContext(player,
                Objects.requireNonNullElse(correctness, 0.0), true));
    }
}
