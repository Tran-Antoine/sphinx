package net.starpye.quiz.discordimpl.game;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.util.ImageUtils;
import net.starpye.quiz.discordimpl.util.MessageUtils;
import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.server.GameServer;
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
        Guild guild = channel
                .getGuild()
                .block();
        InputStream image = ImageUtils.generateLeaderboard(standings, guild);
        MessageUtils.sendAndTrack(
                spec -> spec.addFile("standings.png", image),
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
        sendAsLatexFile(question.getRawQuestion());
    }

    private void sendAsLatexFile(String message) {
        TeXFormula teXFormula = new TeXFormula(message);
        Image image = teXFormula.createBufferedImage(TeXFormula.SERIF, 200, Color.BLACK, Color.WHITE);
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        InputStream inputStream = ImageUtils.toInputStream(bufferedImage);
        MessageUtils.sendAndTrack(
                spec -> spec.addFile("image.png", inputStream),
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
