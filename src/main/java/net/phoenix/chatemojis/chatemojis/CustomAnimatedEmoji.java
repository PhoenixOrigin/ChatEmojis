package net.phoenix.chatemojis.chatemojis;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.phoenix.chatemojis.ChatEmojis;
import net.phoenix.chatemojis.Util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CustomAnimatedEmoji implements Emoji {
    private static final ArrayList<CustomAnimatedEmoji.Pos> currentRenders = new ArrayList<>();
    private static final HashMap<CustomAnimatedEmoji.Pos, Integer> posMap = new HashMap<>();
    public final ArrayList<DynamicTexture> frames;
    public final String name;
    private final int texWidth;
    private final int texHeight;

    private CustomAnimatedEmoji(ArrayList<DynamicTexture> frames, String name, int texWidth, int texHeight) {
        this.frames = frames;
        this.name = name;
        this.texHeight = texHeight;
        this.texWidth = texWidth;
    }

    public static CustomAnimatedEmoji fromResource(File gifFile) {
        try {
            List<BufferedImage> frames = Util.getAllFrames(gifFile);
            BufferedImage first = frames.get(0);
            return new CustomAnimatedEmoji(
                    register(frames),
                    gifFile.getName(),
                    first.getWidth(),
                    first.getHeight()
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<DynamicTexture> register(List<BufferedImage> frames) throws IOException {
        ArrayList<DynamicTexture> nativeFrames = new ArrayList<>();

        for (BufferedImage frame : frames) {
            DynamicTexture texture = new DynamicTexture(frame);
            nativeFrames.add(texture);
        }

        return nativeFrames;
    }

    public static void clearCache() {
        posMap.entrySet().removeIf(cachedDraw -> !currentRenders.contains(cachedDraw.getKey()));
        currentRenders.clear();
    }

    public DynamicTexture getFrame(int index) {
        if (index < 0 || index >= frames.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return frames.get(index);
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
        Pos cachedPos = new Pos(pos[0], pos[1]);
        posMap.putIfAbsent(cachedPos, 0);

        posMap.put(cachedPos, posMap.get(cachedPos) + 1);

        if (posMap.get(cachedPos) >= frames.size()) {
            posMap.put(cachedPos, 0);
        }
        currentRenders.add(cachedPos);
        ChatEmojis.mc.getTextureManager().bindTexture(ChatEmojis.mc.getTextureManager().getDynamicTextureLocation(name + posMap.get(cachedPos), getFrame(posMap.get(cachedPos))));
    }

    public static class Pos {
        public float x;
        public float y;

        public Pos(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return Float.floatToIntBits(x) ^ Float.floatToIntBits(y);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Pos other = (Pos) obj;
            return Float.compare(x, other.x) == 0 && Float.compare(y, other.y) == 0;
        }
    }

}
