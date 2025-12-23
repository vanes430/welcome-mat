package xyz.coffeeisle.welcomemat;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {
    private final WelcomeMat plugin;

    public PlayerEventListener(WelcomeMat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ConfigManager config = plugin.getConfigManager();
        
        // Hide default join message immediately
        event.setJoinMessage(null);

        long delay = config.getGlobalDelay();
        // Capture specific state before delay if needed (though hasPlayedBefore persists)
        boolean isFirstJoin = !player.hasPlayedBefore();

        plugin.getSchedulerAdapter().runEntityLater(player, delay, () -> {
            if (!player.isOnline()) return;

            // Check if we should suppress the message (Vanish/Silent checks)
            boolean suppressMessage = false;
            for (String perm : config.getSuppressPermissions()) {
                if (player.hasPermission(perm)) {
                    suppressMessage = true;
                    break;
                }
            }

            // Join Message (Broadcast manually since we blocked the event message)
            if (!suppressMessage) {
                String message = null;
                if (config.isFirstJoinMessageEnabled() && isFirstJoin) {
                    message = plugin.parsePlaceholders(player, config.getFirstJoinMessage());
                } else if (config.isJoinMessageEnabled()) {
                    message = plugin.parsePlaceholders(player, config.getJoinMessage());
                }
                
                if (message != null && !message.isEmpty()) {
                    plugin.getServer().broadcastMessage(message);
                }
            }

            // Title
            if (config.isJoinTitleEnabled()) {
                player.sendTitle(
                    plugin.parsePlaceholders(player, config.getJoinTitle()),
                    plugin.parsePlaceholders(player, config.getJoinSubtitle()),
                    config.getTitleFadeIn(),
                    config.getTitleStay(),
                    config.getTitleFadeOut()
                );
            }

            // Sound
            if (config.isJoinSoundEnabled()) {
                Sound sound = config.getJoinSound();
                if (sound != null) {
                    player.playSound(
                        player.getLocation(),
                        sound,
                        config.getJoinSoundVolume(),
                        config.getJoinSoundPitch()
                    );
                }
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ConfigManager config = plugin.getConfigManager();

        if (config.isLeaveMessageEnabled()) {
            event.setQuitMessage(plugin.parsePlaceholders(player, config.getLeaveMessage()));
        } else {
            event.setQuitMessage(null);
        }
    }
}
