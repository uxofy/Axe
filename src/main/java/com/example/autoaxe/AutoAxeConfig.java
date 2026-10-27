package com.example.autoaxe;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class AutoAxeConfig {
    public static boolean enabled = true;
    public static boolean debug = false;
    public static int extraHits = 1; // сколько лишних ударов перед твоим кликом (1-3)

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("autoaxe.properties");
    }

    public static void load() {
        Path p = path();
        if (!Files.exists(p)) return;
        try (Reader r = Files.newBufferedReader(p)) {
            Properties props = new Properties();
            props.load(r);
            enabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
            debug = Boolean.parseBoolean(props.getProperty("debug", "false"));
            extraHits = Math.max(1, Math.min(3, Integer.parseInt(props.getProperty("extraHits", "1"))));
        } catch (IOException | NumberFormatException ignored) {
        }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(path())) {
            Properties props = new Properties();
            props.setProperty("enabled", String.valueOf(enabled));
            props.setProperty("debug", String.valueOf(debug));
            props.setProperty("extraHits", String.valueOf(extraHits));
            props.store(w, "AutoAxe");
        } catch (IOException ignored) {
        }
    }
}
