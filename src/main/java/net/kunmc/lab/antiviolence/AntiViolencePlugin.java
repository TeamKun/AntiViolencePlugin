package net.kunmc.lab.antiviolence;

import com.mojang.brigadier.CommandDispatcher;
import net.kunmc.lab.antiviolence.config.ConfigCommand;
import net.kunmc.lab.antiviolence.config.ConfigManager;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiViolencePlugin extends JavaPlugin {
    private static AntiViolencePlugin instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        configManager.load();
        CommandDispatcher<CommandListenerWrapper> dispatcher = ((CraftServer)Bukkit.getServer()).getServer().vanillaCommandDispatcher.a();
        ConfigCommand.register(dispatcher);
        Bukkit.getPluginManager().registerEvents(new AttackEventListener(), this);
    }

    @Override
    public void onDisable() {
    }

    public static AntiViolencePlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
