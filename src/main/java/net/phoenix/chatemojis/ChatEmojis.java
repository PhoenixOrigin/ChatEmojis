package net.phoenix.chatemojis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.phoenix.chatemojis.chatemojis.CustomAnimatedEmoji;
import net.phoenix.chatemojis.chatemojis.CustomEmoji;
import net.phoenix.chatemojis.chatemojis.DefaultEmoji;
import net.phoenix.chatemojis.chatemojis.Emoji;

import java.io.BufferedReader;
import java.io.File;
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
    public static final Map<String, CustomEmoji> CUSTOM_REGISTRY = new HashMap<>();
    public static final Map<String, CustomAnimatedEmoji> CUSTOM_ANIMATED_REGISTRY = new HashMap<>();
    public static Minecraft mc = null;


    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        System.out.println("Loading ChatEmojis mod");
        mc = Minecraft.getMinecraft();
        registerEmojis();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CustomAnimatedEmoji.clearCache();
        }
    }

    private void registerEmojis() {
        try {
            System.out.println("Loading default emojis");

            ResourceLocation manifest = new ResourceLocation("chatemojis", "textures/emojis/emoji_manifest.txt");

            InputStream input = mc.getResourceManager().getResource(manifest).getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    ResourceLocation emojiLoc = new ResourceLocation("chatemojis", "textures/emojis/images/" + line);
                    IResource resource = mc.getResourceManager().getResource(emojiLoc);

                    DefaultEmoji emoji = DefaultEmoji.fromResource(resource);
                    if (emoji != null) {
                        String key = line.replace(".png", "");
                        REGISTRY.put(key, emoji);
                    }
                } catch (FileNotFoundException ignored) {
                }
            }
            reader.close();


            File emojiFolder = new File(Minecraft.getMinecraft().mcDataDir, "chatemojis");
            emojiFolder.mkdirs();


            File[] files = emojiFolder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".png")) {
                        System.out.println("Loading custom emoji: " + file.getName());
                        REGISTRY.put(file.getName().replace(".png", ""), CustomEmoji.fromResource(file));
                    } else if (file.isFile() && file.getName().endsWith(".gif")) {
                        System.out.println("Loading custom animated emoji: " + file.getName());
                        REGISTRY.put(file.getName().replace(".gif", ""), CustomAnimatedEmoji.fromResource(file));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

