package net.phoenix.chatemojis.util;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.phoenix.chatemojis.chatemojis.AnimatedEmoji;

public class AnimatedDraw {
    public float x;
    public float y;
    public AnimatedEmoji emoji;
    public boolean dropShadow;
    public int index;

    public AnimatedDraw(float x, float y, AnimatedEmoji emoji, boolean dropShadow) {
        this.x = x;
        this.y = y;
        this.emoji = emoji;
        this.dropShadow = dropShadow;
        this.index = 0;
    }

    public DynamicTexture getCurrentFrame() {
        index+=1;
        if (index >= emoji.frames.size()) {
            index = 0;
        }
        return emoji.getFrame(index);
    }
}