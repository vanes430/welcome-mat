package xyz.coffeeisle.welcomemat;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigManager {
    private final WelcomeMat plugin;
    private FileConfiguration config;
    private FileConfiguration messagesConfig;

    public ConfigManager(WelcomeMat plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        // Load main config
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        // Load messages.yml
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public String getMessage(String key) {
        String msg = messagesConfig.getString(key);
        if (msg == null) {
            return ChatColor.RED + "Missing message key: " + key;
        }
        String prefix = messagesConfig.getString("prefix", "");
        return color(msg.replace("%p%", prefix));
    }
    
    public long getGlobalDelay() {
        return config.getLong("delays.join-ticks", 20L);
    }
    
    public List<String> getSuppressPermissions() {
        if (!config.isSet("suppress-permissions")) {
            return java.util.Collections.emptyList();
        }
        return config.getStringList("suppress-permissions");
    }

    // --- Messages ---
    
    public boolean isJoinMessageEnabled() {
        return config.getBoolean("messages.join.enabled", true);
    }

    public String getJoinMessage() {
        String prefix = messagesConfig.getString("prefix", "");
        return color(config.getString("messages.join.text", "&e%player_name% &ajoined the game").replace("%p%", prefix));
    }
    
    public boolean isFirstJoinMessageEnabled() {
        return config.getBoolean("messages.first-join.enabled", true);
    }
    
    public String getFirstJoinMessage() {
        String prefix = messagesConfig.getString("prefix", "");
        return color(config.getString("messages.first-join.text", "&dWelcome %player_name% to the server!").replace("%p%", prefix));
    }

    public boolean isLeaveMessageEnabled() {
        return config.getBoolean("messages.leave.enabled", true);
    }

    public String getLeaveMessage() {
        String prefix = messagesConfig.getString("prefix", "");
        return color(config.getString("messages.leave.text", "&e%player_name% &cleft the game").replace("%p%", prefix));
    }

    // --- Titles ---

    public boolean isJoinTitleEnabled() {
        return config.getBoolean("titles.join.enabled", true);
    }

    public String getJoinTitle() {
        return color(config.getString("titles.join.title", "&6Welcome!"));
    }

    public String getJoinSubtitle() {
        return color(config.getString("titles.join.subtitle", "&eEnjoy your stay"));
    }
    
    public int getTitleFadeIn() { return config.getInt("titles.join.fade-in", 10); }
    public int getTitleStay() { return config.getInt("titles.join.stay", 70); }
    public int getTitleFadeOut() { return config.getInt("titles.join.fade-out", 20); }

    // --- Sounds ---

    public boolean isJoinSoundEnabled() {
        return config.getBoolean("sounds.join.enabled", true);
    }

    public Sound getJoinSound() {
        return getSound("sounds.join.sound", "ENTITY_PLAYER_LEVELUP");
    }
    
    public float getJoinSoundVolume() {
        return (float) config.getDouble("sounds.join.volume", 1.0);
    }

    public float getJoinSoundPitch() {
        return (float) config.getDouble("sounds.join.pitch", 1.0);
    }

    private Sound getSound(String path, String defaultSound) {
        String name = config.getString(path, defaultSound);
        try {
            return Sound.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound name in config: " + name);
            return null;
        }
    }
    
    private String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
