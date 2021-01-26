package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.util.ImageUtil;
import net.starpye.quiz.discordimpl.util.ImageUtils;
import net.starype.quiz.api.game.GameRoundReport;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.ScoreDistribution.Standing;
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
import java.util.Random;
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
        InputStream image = generateStandingsImage(report);
        channel.createMessage(spec -> spec.addFile("standings.png", image)).subscribe();
    }

    private InputStream generateStandingsImage(GameRoundReport report) {
        int width = 1500;
        int height = 900;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D graphics = image.createGraphics();
        graphics.setBackground(new Color(54, 57, 63));
        graphics.clearRect(0, 0, width, height);
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Bowlby One SC", Font.PLAIN, height/9)); // Bowlby One SC
        graphics.drawString("Results", width/2 - 150, height/7);
        int index = 0;
        List<Standing> standings = report.orderedStandings();
        for(Standing standing : standings.subList(0, Math.min(standings.size(), 10))) {
            addLine(graphics, width, height, index++, standing, standings.size(), standings.get(0).getScoreAcquired());
        }

        graphics.dispose();
        return ImageUtils.toInputStream(image);
    }

    private void addLine(Graphics2D graphics, int width, int height, int index, Standing standing, int size, double maxScore) {

        int yShift = height / 15;

        int lineSpace = (height - yShift) / (size + 1);
        int lineThickness = (int) (0.55 * lineSpace);
        int imageThickness = (int) (1.2 * lineThickness);

        int lineLength = Math.min(
                (int) (standing.getScoreAcquired() / maxScore * width * 0.7) + (int) (1.4 * imageThickness),
                (int) (width * 0.9));

        int lineCenterY = (index+1) * lineSpace;
        int lineTopY =  lineCenterY - lineThickness/2;
        graphics.setColor(randomColor());
        graphics.fill(new Rectangle(0, lineTopY + yShift, lineLength, lineThickness));
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font( "SansSerif", Font.BOLD, height/20));
        graphics.drawString(String.valueOf(standing.getScoreAcquired()), lineLength + 12, lineCenterY + 3 + yShift);
        Object id = standing.getPlayer().getId();
        byte[] data = channel
                .getGuild()
                .block()
                .getMemberById((Snowflake) id)
                .block().getAvatar()
                .block()
                .getData();
        InputStream stream = new ByteArrayInputStream(data);
        try {
            Image image = ImageIO.read(stream);
            image = image.getScaledInstance(imageThickness, imageThickness, Image.SCALE_SMOOTH);
            graphics.drawImage(image, (int) (lineLength - imageThickness - 0.02 * width), lineCenterY - imageThickness/2 + yShift, null);
        } catch (IOException ignored) { }
    }

    private static Color randomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return new Color(r, g, b);
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
    public void onPlayerScoreUpdated(Player<?> player) { }

    @Override
    public void onQuestionReleased(Question question) {
        sendAsFile(question.getRawQuestion());
    }

    private void sendAsFile(String message) {

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
