package net.starype.quiz.discordimpl.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.player.Player;
import net.starype.quiz.api.question.Question;
import net.starype.quiz.api.server.GameServer;
import net.starype.quiz.discordimpl.util.ImageUtils;
import net.starype.quiz.discordimpl.util.MessageUtils;
import org.scilab.forge.jlatexmath.TeXFormula;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DiscordGameServer extends DiscordLogContainer implements GameServer<DiscordQuizGame> {

    private TextChannel channel;
    private Consumer<DiscordQuizGame> endAction;

    public DiscordGameServer(TextChannel channel, Consumer<DiscordQuizGame> endAction) {
        super(channel);
        this.channel = channel;
        this.endAction = endAction;
    }

    @Override
    public void onRoundEnded(GameRoundReport report, DiscordQuizGame game) {
        sendAsText("That is the end of the round! Below are the results");
        sendLeaderboard(report.orderedStandings());
        if(game.isOutOfRounds()) {
            sendAsText("This was the last round. Use ?next to vote for displaying the game results");
        }
    }

    @Override
    public void onRoundStarting(DiscordQuizGame game, boolean firstRound) {
        String message = firstRound
                ? "Game successfully started! Moving on to the first round"
                : "All players seem ready. Moving on to the next round!";
        sendAsText(message);
    }

    @Override
    public void onGameOver(List<? extends Player<?>> playerStandings, DiscordQuizGame game) {
        sendAsText("Thanks for playing! Here are your results");
        sendLeaderboard(playerStandings
                .stream()
                .map(player -> new Standing(player, player.getScore().getPoints()))
                .collect(Collectors.toList()));
        endAction.accept(game);
    }

    private void sendLeaderboard(List<Standing> standings) {
        Guild guild = channel.getGuild();
        InputStream image = ImageUtils.generateLeaderboard(standings, guild);
        MessageUtils.sendAndTrack(
                image, "standings.png",
                channel,
                this);
    }

    @Override
    public void onPlayerGuessed(PlayerGuessContext context) {
        sendAsText(context.getPlayer().getNickname()+", your answer has been registered!");
    }

    @Override
    public void onNonEligiblePlayerGuessed(Player<?> player) {
        sendAsText(player.getNickname()+", you may not submit answers for this round anymore...");
    }

    @Override
    public void onPlayerGaveUp(Player<?> player) {
        sendAsText(player.getNickname()+" gave up on this round...");
    }

    @Override
    public void onPlayerScoreUpdated(Player<?> player) {
        // nothing to do
    }

    @Override
    public void onQuestionReleased(Question question) {
        String rawQuestion = question.getRawQuestion();
        if(question.isTagAttached("Math")) {
            sendAsLatexFile(rawQuestion);
        } else {
            sendAsText(rawQuestion);
        }
    }

    private void sendAsLatexFile(String message) {
        TeXFormula teXFormula = new TeXFormula(message);
        Image image = teXFormula.createBufferedImage(TeXFormula.SERIF, 200, Color.BLACK, Color.WHITE);
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        InputStream inputStream = ImageUtils.toInputStream(bufferedImage);
        MessageUtils.sendAndTrack(
                inputStream, "image.png",
                channel,
                this);
    }

    private void sendAsText(String message) {
        MessageUtils.sendAndTrack(
                message,
                channel,
                this
        );
    }
}
