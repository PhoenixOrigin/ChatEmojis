package net.phoenix.chatemojis.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.phoenix.chatemojis.ChatEmojis;
import net.phoenix.chatemojis.chatemojis.AnimatedEmoji;
import net.phoenix.chatemojis.chatemojis.Emoji;
import net.phoenix.chatemojis.util.AnimatedDraw;
import net.phoenix.chatemojis.util.Draw;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(FontRenderer.class)
public abstract class FontRendererMixin {
    @Unique
    private static final Pattern EMOJI_PATTERN = Pattern.compile(":([a-zA-Z0-9_]+):");

    @Shadow
    protected abstract int renderString(String text, float x, float y, int color, boolean dropShadow);

    @Shadow
    protected abstract void resetStyles();

    @Shadow
    protected abstract void enableAlpha();

    @Inject(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At("HEAD"), cancellable = true)
    public void drawStringInject(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        if (!text.contains(":") || !EMOJI_PATTERN.matcher(text).find()) return;
        List<Draw> draws = new ArrayList<>();
        List<AnimatedDraw> animatedDraws = new ArrayList<>();
        int cursorX = (int) x;
        int lastMatchEnd = 0;
        Matcher matcher = EMOJI_PATTERN.matcher(text);
        FontRenderer fontRenderer = (FontRenderer) (Object) this;


        while (matcher.find()) {
            resetStyles();
            enableAlpha();
            String beforeEmoji = text.substring(lastMatchEnd, matcher.start());
            if (dropShadow) {
                renderString(beforeEmoji, cursorX + 1, y + 1, color, true);
                renderString(beforeEmoji, cursorX, y, color, false);
            } else renderString(beforeEmoji, cursorX, y, color, false);
            int width = fontRenderer.getStringWidth(beforeEmoji);
            cursorX += width;

            String emojiKey = matcher.group(1);
            Emoji emoji = ChatEmojis.REGISTRY.get(emojiKey);
            if (emoji != null) {
                draws.add(new Draw(cursorX, y, emoji, dropShadow));
                cursorX += (int) ((int) ((8.0f / emoji.getTexHeight()) * emoji.getTexWidth()));
            } else {
                AnimatedEmoji animatedEmoji = ChatEmojis.ANIMATED_REGISTRY.get(emojiKey);
                if(animatedEmoji != null) {
                    animatedDraws.add(new AnimatedDraw(cursorX, y, animatedEmoji, dropShadow));
                    cursorX += (int) ((int) ((8.0f / animatedEmoji.getTexHeight()) * animatedEmoji.getTexWidth()));
                } else {
                    System.out.println("e");
                    resetStyles();
                    enableAlpha();
                    int w;
                    if (dropShadow) {
                        w = renderString(matcher.group(), cursorX + 1, y + 1, color, true);
                        w = Math.max(w, renderString(matcher.group(), cursorX, y, color, false));
                    } else w = renderString(matcher.group(), cursorX, y, color, false);

                    cursorX += w;
                }
            }

            lastMatchEnd = matcher.end();
        }

        if (lastMatchEnd < text.length()) {
            resetStyles();
            enableAlpha();
            String remaining = text.substring(lastMatchEnd);
            if (dropShadow) {
                renderString(remaining, cursorX + 1, y + 1, color, true);
                renderString(remaining, cursorX, y, color, false);
            } else renderString(remaining, cursorX, y, color, false);

            cursorX += ChatEmojis.mc.fontRendererObj.getStringWidth(remaining);
        }

        for (Draw draw : draws) {
            phoenixUtil$drawEmoji(draw.x, draw.y, draw.emoji);
        }
        for (AnimatedDraw draw : animatedDraws) {
            phoenixUtil$drawAnimatedEmoji(draw.x, draw.y, draw.emoji, draw);
        }

        cir.setReturnValue(cursorX);
    }

    @Unique
    private void phoenixUtil$drawAnimatedEmoji(float x, float y, AnimatedEmoji emoji, AnimatedDraw draw) {
        DynamicTexture frame = draw.getCurrentFrame();
        if (frame == null) return;
        Minecraft.getMinecraft().getTextureManager().bindTexture(ChatEmojis.mc.getTextureManager().getDynamicTextureLocation(emoji.name + draw.index, frame));

        float width = emoji.getTexWidth();
        float height = emoji.getTexHeight();
        float scale = 8.0f / height;
        float brightness = 1.0f;

        chatEmojis$render(x, y, width, height, scale, brightness);
    }

    @Unique
    private void phoenixUtil$drawEmoji(float x, float y, Emoji emoji) {
        Minecraft mc = Minecraft.getMinecraft();
        ResourceLocation texture = emoji.id.getResourceLocation();
        mc.getTextureManager().bindTexture(texture);

        float width = emoji.getTexWidth();
        float height = emoji.getTexHeight();
        float scale = 8.0f / height;
        float brightness = 1.0f;


        chatEmojis$render(x, y, width, height, scale, brightness);

    }

    @Unique
    private void chatEmojis$render(float x, float y, float width, float height, float scale, float brightness) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();

        GlStateManager.color(brightness, brightness, brightness, 1.0f);
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1.0f);


        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0, 0, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glEnd();

        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
    }





}


