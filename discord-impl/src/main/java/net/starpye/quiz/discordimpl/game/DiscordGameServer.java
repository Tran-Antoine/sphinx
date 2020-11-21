package net.starpye.quiz.discordimpl.game;

import discord4j.core.object.entity.channel.TextChannel;
import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.server.GameServer;
import org.scilab.forge.jlatexmath.TeXFormula;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        StringBuilder builder = new StringBuilder();
        builder.append("---------------\n").append("That is the end of the round!\nHere are the results:\n");
        for(String value : report.rawMessages()) {
            builder.append(value);
        }
        builder.append("\n---------------\n").append("Waiting for the next round... Use command 'next' to get ready\n");
        if(builder.length() != 0) {
            sendAsText(builder.toString());
        }
    }

    @Override
    public void onGameOver(DiscordQuizGame game, List<? extends Player<?>> playerStandings) {
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
        sendAsText(player.getNickname()+" now has " + player.getScore() + " points");
    }

    @Override
    public void onQuestionReleased(Question question) {
        sendAsFile(question.getRawQuestion());
    }

    private void sendAsFile(String message) {

        TeXFormula teXFormula = new TeXFormula(message);
        Image image = teXFormula.createBufferedImage(TeXFormula.SERIF, 20, Color.BLACK, Color.WHITE);
        BufferedImage bufferedImage = toBufferedImage(image);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(bufferedImage, "jpeg", os);
        } catch (IOException e) {
            return;
        }

        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());

        channel.createMessage(spec -> spec.addFile("image.txt", inputStream)).block();
    }

    private void sendAsText(String message) {
        channel.createMessage(message).block();
    }

    // https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
    private BufferedImage toBufferedImage(Image image) {
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
