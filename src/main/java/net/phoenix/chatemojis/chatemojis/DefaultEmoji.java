package net.phoenix.chatemojis.chatemojis;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.phoenix.chatemojis.ChatEmojis;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class DefaultEmoji implements Emoji {

    public final IResource id;
    public final String name;
    private final int texWidth;
    private final int texHeight;

    private DefaultEmoji(IResource identifier, String name, int texWidth, int texHeight) {
        this.id = identifier;
        this.name = name;
        this.texHeight = texHeight;
        this.texWidth = texWidth;
    }

    public static DefaultEmoji fromResource(IResource resource) {
        try {
            try (InputStream inputStream = resource.getInputStream()) {
                BufferedImage image = TextureUtil.readBufferedImage(inputStream);
                int width = image.getWidth();
                int height = image.getHeight();
                return new DefaultEmoji(resource, resource.getResourceLocation().getResourcePath(), width, height);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getTexWidth() {
        return texWidth;
    }

    @Override
    public int getTexHeight() {
        return texHeight;
    }

    @Override
    public void bindTexture(int... pos) {
        ChatEmojis.mc.getTextureManager().bindTexture(id.getResourceLocation());
    }
}