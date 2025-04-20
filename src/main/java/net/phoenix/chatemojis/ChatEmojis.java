package net.phoenix.chatemojis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.phoenix.chatemojis.chatemojis.AnimatedEmoji;
import net.phoenix.chatemojis.chatemojis.Emoji;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = ChatEmojis.MOD_ID)
public class ChatEmojis {
    public static final String MOD_ID = "chatemojis";
    public static final Map<String, Emoji> REGISTRY = new HashMap<>();
    public static final Map<String, AnimatedEmoji> ANIMATED_REGISTRY = new HashMap<>();
    public static Minecraft mc = null;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        System.out.println("Loading ChatEmojis mod");
        mc = Minecraft.getMinecraft();
        registerEmojis();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerEmojis() {
        try {
            System.out.println("Loading default emojis");
            ResourceLocation manifest = new ResourceLocation("chatemojis", "textures/emojis/emoji_manifest.txt");
            InputStream input = Minecraft.getMinecraft().getResourceManager().getResource(manifest).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    System.out.println("Loading emoji: " + line);
                    ResourceLocation emojiLoc = new ResourceLocation("chatemojis", "textures/emojis/images/" + line);
                    IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(emojiLoc);

                    Emoji emoji = Emoji.fromResource(resource);
                    if (emoji != null) {
                        String key = line.replace(".png", "");
                        REGISTRY.put(key, emoji);
                    }
                } catch (FileNotFoundException ignored) {
                }
            }
            reader.close();
            System.out.println("Loading animated emojis");
            manifest = new ResourceLocation("chatemojis", "textures/emojis/animated_images_manifest.txt");
            input = Minecraft.getMinecraft().getResourceManager().getResource(manifest).getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            while ((line = reader.readLine()) != null) {
                System.out.println("Loading animated emoji: " + line);
                ResourceLocation emojiLoc = new ResourceLocation("chatemojis", "textures/emojis/animated_images/" + line);
                IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(emojiLoc);
                AnimatedEmoji emoji = AnimatedEmoji.fromResource(resource);
                if (emoji != null) {
                    String key = line.replace(".gif", "");
                    ANIMATED_REGISTRY.put(key, emoji);
                }
            }
            System.out.println(ANIMATED_REGISTRY.keySet());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

