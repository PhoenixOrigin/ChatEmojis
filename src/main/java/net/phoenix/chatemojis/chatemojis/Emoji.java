package net.phoenix.chatemojis.chatemojis;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Emoji {

    public final IResource id;
    public final String name;
    private final int texWidth;
    private final int texHeight;

    private Emoji(IResource identifier, String name, int texWidth, int texHeight) {
        this.id = identifier;
        this.name = name;
        this.texHeight = texHeight;
        this.texWidth = texWidth;
    }

    public static Emoji fromResource(IResource resource) {
        try {
            try (InputStream inputStream = resource.getInputStream()) {
                BufferedImage image = TextureUtil.readBufferedImage(inputStream);
                int width = image.getWidth();
                int height = image.getHeight();
                return new Emoji(resource, resource.getResourceLocation().getResourcePath(), width, height);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getTexWidth() {
        return texWidth;
    }

    public int getTexHeight() {
        return texHeight;
    }
}