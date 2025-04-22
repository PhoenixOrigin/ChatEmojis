package net.phoenix.chatemojis;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Util {

    public static String removeExtension(String s) {
        String separator = System.getProperty("file.separator");
        String filename;
        int lastSeparatorIndex = s.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = s;
        } else {
            filename = s.substring(lastSeparatorIndex + 1);
        }
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex == -1)
            return filename;
        return filename.substring(0, extensionIndex);
    }


    public static List<BufferedImage> getAllFrames(File gifFile) throws IOException {
        List<BufferedImage> frames = new ArrayList<>();
        ImageInputStream imageStream = ImageIO.createImageInputStream(gifFile);
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
        ImageReader gifReader;
        if (readers.hasNext()) {
            gifReader = readers.next();
        } else {
            throw new IOException("No suitable ImageReader found for GIF format.");
        }
        gifReader.setInput(imageStream);
        int frameCount = gifReader.getNumImages(true);
        for (int i = 0; i < frameCount; i++) {
            BufferedImage frame = gifReader.read(i);
            frames.add(frame);
        }
        return frames;
    }
}
