package net.phoenix.chatemojis.util;

import net.phoenix.chatemojis.chatemojis.Emoji;

public class Draw {
    public float x;
    public float y;
    public Emoji emoji;
    public boolean dropShadow;

    public Draw(float x, float y, Emoji emoji, boolean dropShadow) {
        this.x = x;
        this.y = y;
        this.emoji = emoji;
        this.dropShadow = dropShadow;
    }
}