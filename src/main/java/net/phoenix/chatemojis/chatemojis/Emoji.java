package net.phoenix.chatemojis.chatemojis;

public interface Emoji {
    String getName();

    int getTexWidth();

    int getTexHeight();

    void bindTexture(int... pos);
}
