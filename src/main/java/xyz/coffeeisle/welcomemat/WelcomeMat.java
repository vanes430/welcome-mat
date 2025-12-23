package xyz.coffeeisle.welcomemat;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.coffeeisle.welcomemat.commands.WelcomeMatCommand;
import xyz.coffeeisle.welcomemat.utils.SchedulerAdapter;

public class WelcomeMat extends JavaPlugin {
    private static WelcomeMat instance;
    private ConfigManager configManager;
    private SchedulerAdapter schedulerAdapter;
    private boolean papiHooked;

    @Override
    public void onEnable() {
        instance = this;
        
        // Check for PlaceholderAPI
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.papiHooked = true;
            getLogger().info("PlaceholderAPI found! Placeholders will be parsed.");
        }
        
        // Initialize simple config manager
        this.configManager = new ConfigManager(this);
        
        // Initialize Scheduler helper (kept for Folia support compatibility)
        this.schedulerAdapter = new SchedulerAdapter(this);

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
        
        // Register command
        getCommand("welcomemat").setExecutor(new WelcomeMatCommand(this));

        getLogger().info("WelcomeMat Lite has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("WelcomeMat Lite has been disabled!");
    }

    public static WelcomeMat getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public SchedulerAdapter getSchedulerAdapter() {
        return schedulerAdapter;
    }
    
    public String parsePlaceholders(org.bukkit.entity.Player player, String text) {
        if (text == null) return "";
        if (papiHooked) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        }
        return text.replace("%player%", player.getName());
    }
}
