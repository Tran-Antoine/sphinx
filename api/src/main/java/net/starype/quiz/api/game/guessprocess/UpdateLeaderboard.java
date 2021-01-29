package net.starype.quiz.api.game.guessprocess;

public class UpdateLeaderboard extends ConditionalConsumer<RoundState> {

    @Override
    public void execute(RoundState roundState) {
        roundState.updateLeaderboard(roundState.getPlayerGuessContext().getPlayer(),
                roundState.getPlayerGuessContext().getCorrectness());
    }
}
