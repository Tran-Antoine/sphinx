package net.starype.quiz.discordimpl.util;

import net.dv8tion.jda.api.entities.Guild;
import net.starype.quiz.api.game.ScoreDistribution.Standing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;

public class ImageUtils {

    public static InputStream toInputStream(BufferedImage image) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
        } catch (IOException ignored) {

        }
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        return is;
    }

    public static InputStream generateLeaderboard(List<Standing> standings, Guild guild) {
        int width = 1500;
        int height = 900;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D graphics = image.createGraphics();

        initBackground(width, height, graphics);
        initLeaderboard(width, height, graphics, guild, standings);
        graphics.dispose();

        return ImageUtils.toInputStream(image);
    }

    private static void initBackground(int width, int height, Graphics2D graphics) {
        graphics.setBackground(new Color(54, 57, 63));
        graphics.clearRect(0, 0, width, height);
        graphics.setColor(Color.WHITE);
        graphics.addRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        graphics.setFont(new Font("Bowlby One SC", Font.PLAIN, height / 9)); // Bowlby One SC
        graphics.drawString("Results", width / 2 - 210, height / 7);
    }

    private static void initLeaderboard(int width, int height, Graphics2D graphics, Guild guild, List<Standing> standings) {

        int index = 0;
        int leaderboardSize = Math.min(standings.size(), 10);
        double maxScore = standings.get(0).getScoreAcquired();

        for (Standing standing : standings.subList(0, leaderboardSize)) {
            addLine(
                    graphics,
                    guild,
                    width, height,
                    index++,
                    standing, leaderboardSize, maxScore);
        }
    }

    private static void addLine(Graphics2D graphics, Guild guild, int width, int height, int index, Standing standing, int size, double maxScore) {

        int yShift = height / 15;

        int lineSpace = (height - yShift) / (size + 1);
        int lineThickness = (int) (0.55 * lineSpace);
        int imageThickness = (int) (1.2 * lineThickness);

        int lineLength = Math.min(
                (int) (standing.getScoreAcquired() / maxScore * width * 0.7) + (int) (1.4 * imageThickness),
                (int) (width * 0.9));

        int lineCenterY = (index + 1) * lineSpace;
        int lineTopY = lineCenterY - lineThickness / 2;

        drawLeaderboardLine(graphics, lineTopY, yShift, lineLength, lineThickness);
        drawScore(graphics, height, standing, lineLength, lineCenterY, yShift);
        drawAvatar(graphics, guild, standing, imageThickness, lineLength, width, lineCenterY, yShift);


    }

    private static void drawLeaderboardLine(Graphics2D graphics, int lineTopY, int yShift, int lineLength, int lineThickness) {
        graphics.setColor(randomColor());
        graphics.fill(new Rectangle(0, lineTopY + yShift, lineLength, lineThickness));
    }

    private static void drawScore(Graphics2D graphics, int height, Standing standing, int lineLength, int lineCenterY, int yShift) {
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("SansSerif", Font.BOLD, height / 25));
        graphics.drawString(String.valueOf(standing.getScoreAcquired()), lineLength + 12, lineCenterY + 6 + yShift);

    }

    private static void drawAvatar(Graphics2D graphics, Guild guild, Standing standing, int imageThickness, int lineLength, int width, int lineCenterY, int yShift) {
        Object id = standing.getPlayer().getId();

        String avatarUrl = guild
                .retrieveMemberById((String) id)
                .map(m -> m.getUser().getEffectiveAvatarUrl())
                .complete();

        byte[] data = retrieveData(avatarUrl);

        InputStream stream = new ByteArrayInputStream(data);
        try {
            Image image = ImageIO.read(stream);
            image = image.getScaledInstance(imageThickness, imageThickness, Image.SCALE_SMOOTH);
            graphics.drawImage(image, (int) (lineLength - imageThickness - 0.02 * width), lineCenterY - imageThickness / 2 + yShift, null);
        } catch (IOException ignored) {
        }
    }

    private static byte[] retrieveData(String url) {
        try {
            URL urlObject = new URL(url);
            return urlObject
                    .openStream()
                    .readAllBytes();
        } catch (IOException e) {
            return new byte[]{};
        }
    }

    private static Color randomColor() {
        Random random = new Random();
        return new Color(Color.HSBtoRGB(random.nextFloat(), 0.85F, 0.7F));
    }

    // https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
    public static BufferedImage toBufferedImage(Image image) {
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
