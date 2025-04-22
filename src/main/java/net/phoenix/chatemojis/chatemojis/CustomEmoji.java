package net.phoenix.chatemojis.chatemojis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.phoenix.chatemojis.ChatEmojis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CustomEmoji implements Emoji {
    public final DynamicTexture tex;
    public final String name;
    private final int texWidth;
    private final int texHeight;

    private CustomEmoji(DynamicTexture identifier, String name, int texWidth, int texHeight) {
        this.tex = identifier;
        this.name = name;
        this.texHeight = texHeight;
        this.texWidth = texWidth;
    }

    public static CustomEmoji fromResource(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        DynamicTexture dynamicTexture = new DynamicTexture(image);
        String name = file.getName().replace(".png", "");
        return new CustomEmoji(dynamicTexture, name, image.getWidth(), image.getHeight());
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
        Minecraft.getMinecraft().getTextureManager().bindTexture(ChatEmojis.mc.getTextureManager().getDynamicTextureLocation(name, tex));
    }
}
