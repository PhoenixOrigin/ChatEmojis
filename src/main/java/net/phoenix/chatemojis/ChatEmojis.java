package net.phoenix.chatemojis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.phoenix.chatemojis.chatemojis.Emoji;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = ChatEmojis.MOD_ID)
public class ChatEmojis {
    public static final String MOD_ID = "chatemojis";
    public static final Map<String, Emoji> REGISTRY = new HashMap<>();
    public static Minecraft mc = null;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        System.out.println("ChatEmojis is loading...");
        mc = Minecraft.getMinecraft();
        registerEmojis();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerEmojis() {
        try {
            ResourceLocation manifest = new ResourceLocation("chatemojis", "textures/emojis/emoji_manifest.txt");
            InputStream input = Minecraft.getMinecraft().getResourceManager().getResource(manifest).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Loading emoji: " + line);
                ResourceLocation emojiLoc = new ResourceLocation("chatemojis", "textures/emojis/images/" + line);
                IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(emojiLoc);
                Emoji emoji = Emoji.fromResource(resource);
                if (emoji != null) {
                    String key = line.replace(".png", "");
                    REGISTRY.put(key, emoji);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

