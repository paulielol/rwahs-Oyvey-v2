//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package me.alpha432.oyvey.features.modules.client;

import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class SkinChanger
        extends Module {
    public static SkinChanger instance;
    public File tmp;
    public Setting<String> SkinName = (Setting<String>) this.register(new Setting("NameToSteal", "Quinlan", "The user of this skin holder?"));

    public SkinChanger() {
        super("SkinChanger", "Helps with creating Media", Category.CLIENT, false, false, false);
        instance = this;
    }

    @Override
    public void onEnable() {
        BufferedImage image = null;
        Command.sendSilentMessage("DEBUG | ATTEMPTING TO CREATE");
        try {
            this.tmp = new File("oyvey" + File.separator + "tmp");
            if (!this.tmp.exists()) {
                this.tmp.mkdirs();
            }
            Gson gson = new Gson();
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + SkinName);
            Reader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            Map<?, ?> map = (Map<?, ?>) gson.fromJson(reader, Map.class);
            ConcurrentHashMap<String, String> valsMap = new ConcurrentHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                valsMap.put(key, val);
            }
            reader.close();
            String uuid = valsMap.get("id");
            URL url2 = new URL("https://mc-heads.net/skin/" + uuid);
            image = ImageIO.read(url2);
            ImageIO.write(image, "png", new File("oyvey/tmp/skin.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        this.deleteSkinChangerFiles();
    }

    public void deleteSkinChangerFiles() {
        for (File file : mc.gameDir.listFiles()) {
            if (!file.isDirectory() && file.getName().contains("-skinchanger")) file.delete();
        }
    }
}
