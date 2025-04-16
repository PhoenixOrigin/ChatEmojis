package net.phoenix.chatemojis.util;

import net.phoenix.chatemojis.chatemojis.AnimatedEmoji;
import net.phoenix.chatemojis.chatemojis.Emoji;

public class AnimatedDraw {
    public float x;
    public float y;
    public AnimatedEmoji emoji;
    public boolean dropShadow;

    public AnimatedDraw(float x, float y, AnimatedEmoji emoji, boolean dropShadow) {
        this.x = x;
        this.y = y;
        this.emoji = emoji;
        this.dropShadow = dropShadow;
    }
}