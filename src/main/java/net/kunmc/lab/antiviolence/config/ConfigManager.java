package net.kunmc.lab.antiviolence.config;

import net.kunmc.lab.antiviolence.AntiViolencePlugin;
import net.kunmc.lab.antiviolence.config.parser.BooleanParser;
import net.kunmc.lab.antiviolence.config.parser.DoubleParser;
import net.kunmc.lab.antiviolence.config.parser.Parser;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Map<String, Parser<?>> CONFIGS = new HashMap<>() {{
        put("damageMultiplier", new DoubleParser(0, Integer.MAX_VALUE));
        put("enabled", new BooleanParser());
        put("immediatelyKill", new BooleanParser());
    }};
    private FileConfiguration config;

    public static String[] getConfigPaths() {
        return CONFIGS.keySet().toArray(new String[0]);
    }

    public void load() {
        AntiViolencePlugin plugin = AntiViolencePlugin.getInstance();
        plugin.saveDefaultConfig();
        if (config != null) {
            plugin.reloadConfig();
        }
        config = plugin.getConfig();
    }

    public boolean setConfig(String path, String valueString) {
        if (!CONFIGS.containsKey(path)) {
            return false;
        }
        Parser<?> parser = CONFIGS.get(path);
        Object value = parser.parse(valueString);
        return setConfig(path, value);
    }

    private boolean setConfig(String path, Object value) {
        if (value == null) {
            return false;
        }
        AntiViolencePlugin plugin = AntiViolencePlugin.getInstance();
        config.set(path, value);
        plugin.saveConfig();
        return true;
    }

    public double getDamageMultiplier() {
        return config.getDouble("damageMultiplier");
    }

    public boolean isEnabled() {
        return config.getBoolean("enabled");
    }

    public boolean isImmediatelyKill() {
        return config.getBoolean("immediatelyKill");
    }
}
