import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

public class Main {
    private static final int PADDING_TOP = 118;
    private static final int PADDING_BOTTOM = 117;

    public static void main(String[] args) throws IOException {
        Files
                .list(Path.of("cards/before/"))
                .map(Path::toFile)
                .map(Main::readImage)
                .map(Main::process)
                .forEach(Main::output);
    }

    private static void output(Entry<String, BufferedImage> entry) {
        String filename = "cards/after/" + entry.getKey();
        catches(() -> ImageIO.write(entry.getValue(), "png", new File(filename)));
    }

    private static Entry<String, BufferedImage> process(Entry<String, BufferedImage> entry) {
        BufferedImage original = entry.getValue();
        int height = original.getWidth() - PADDING_TOP - PADDING_BOTTOM;
        BufferedImage image = new BufferedImage(original.getHeight(), height, BufferedImage.TYPE_INT_ARGB);
        AffineTransform transform = AffineTransform.getTranslateInstance(0, original.getWidth() - PADDING_TOP);
        transform.rotate(-Math.PI / 2);
        ((Graphics2D) image.getGraphics()).drawImage(original, transform, null);
        return Map.entry(entry.getKey(), image);
    }

    private static Entry<String, BufferedImage> readImage(File file) {
        return Map.entry(file.getName(), catches(() -> ImageIO.read(file)));
    }

    private static <T> T catches(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
