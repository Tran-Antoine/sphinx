package net.starype.quiz.api.game.guessprocess;

public class UpdatePlayersCorrectness extends ConditionalConsumer<RoundState> {

    @Override
    public void execute(RoundState roundState) {
        roundState.setLeaderboard(roundState.getPlayerGuessContext().getPlayer(),
                roundState.getPlayerGuessContext().getCorrectness());
    }
}
