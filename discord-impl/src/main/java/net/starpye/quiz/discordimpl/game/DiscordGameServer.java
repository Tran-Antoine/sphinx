package net.starpye.quiz.discordimpl.game;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.util.ImageUtils;
import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.server.GameServer;
import org.scilab.forge.jlatexmath.TeXFormula;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

public class DiscordGameServer implements GameServer<DiscordQuizGame> {

    private TextChannel channel;
    private Consumer<DiscordQuizGame> endAction;

    public DiscordGameServer(TextChannel channel, Consumer<DiscordQuizGame> endAction) {
        this.channel = channel;
        this.endAction = endAction;
    }

    @Override
    public void onRoundEnded(GameRoundReport report, DiscordQuizGame game) {
        Guild guild = channel
                .getGuild()
                .block();
        InputStream image = ImageUtils.generateLeaderboard(report.orderedStandings(), guild);
        channel.createMessage(spec -> spec.addFile("standings.png", image)).subscribe();
    }

    @Override
    public void onGameOver(List<? extends Player<?>> playerStandings, DiscordQuizGame game) {
        sendAsText("Game over!");
        endAction.accept(game);
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
        BufferedImage bufferedImage = toBufferedImage(image);
        InputStream inputStream = ImageUtils.toInputStream(bufferedImage);
        channel.createMessage(spec -> spec.addFile("image.png", inputStream)).block();
    }

    private void sendAsText(String message) {
        channel.createMessage(message).block();
    }

    // https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
    private BufferedImage toBufferedImage(Image image) {
        // Create a buffered image with transparency
        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bufferedImage;
    }
}
