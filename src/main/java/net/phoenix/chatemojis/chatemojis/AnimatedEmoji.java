package net.phoenix.chatemojis.chatemojis;

import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AnimatedEmoji {
    public final ArrayList<DynamicTexture> frames;
    public final String name;
    private final int texWidth;
    private final int texHeight;
    private final ResourceLocation location;

    private AnimatedEmoji(ArrayList<DynamicTexture> frames, String name, int texWidth, int texHeight, ResourceLocation location) {
        this.frames = frames;
        this.name = name;
        this.texHeight = texHeight;
        this.texWidth = texWidth;
        this.location = location;
    }

    public static AnimatedEmoji fromResource(IResource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            ArrayList<BufferedImage> frames = getFrames(inputStream); // fix here
            BufferedImage first = frames.get(0);
            return new AnimatedEmoji(
                    register(frames),
                    resource.getResourceLocation().getResourcePath(),
                    first.getWidth(),
                    first.getHeight(),
                    resource.getResourceLocation()
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static ArrayList<BufferedImage> getFrames(InputStream input) throws IOException {
        ArrayList<BufferedImage> frames = new ArrayList<>();
        ImageReader reader = new GIFImageReader(new GIFImageReaderSpi());
        reader.setInput(ImageIO.createImageInputStream(input), false); // set input from stream

        int numImages = reader.getNumImages(true);
        for (int i = 0; i < numImages; i++) {
            frames.add(reader.read(i));
        }

        return frames;
    }

    private static ArrayList<DynamicTexture> register(ArrayList<BufferedImage> frames) throws IOException {
        ArrayList<DynamicTexture> nativeFrames = new ArrayList<>();

        for (BufferedImage frame : frames) {
            DynamicTexture texture = new DynamicTexture(frame);
            nativeFrames.add(texture);
        }

        return nativeFrames;
    }

    public DynamicTexture getFrame(int index) {
        if (index < 0 || index >= frames.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return frames.get(index);
    }

    public int getTexWidth() {
        return texWidth;
    }

    public int getTexHeight() {
        return texHeight;
    }

    public ResourceLocation getLocation() {
        return location;
    }
}
