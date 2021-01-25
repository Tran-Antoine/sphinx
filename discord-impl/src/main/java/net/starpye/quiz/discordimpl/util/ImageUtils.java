package net.starpye.quiz.discordimpl.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
}
