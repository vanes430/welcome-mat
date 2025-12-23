package xyz.coffeeisle.welcomemat.commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import xyz.coffeeisle.welcomemat.ConfigManager;
import xyz.coffeeisle.welcomemat.WelcomeMat;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WelcomeMatCommand implements CommandExecutor, TabCompleter {
    private final WelcomeMat plugin;

    public WelcomeMatCommand(WelcomeMat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigManager config = plugin.getConfigManager();

        if (args.length > 0) {
            String sub = args[0].toLowerCase();

            if (sub.equals("reload")) {
                if (!sender.hasPermission("welcomemat.reload")) {
                    sender.sendMessage(config.getMessage("command.no-permission"));
                    return true;
                }
                config.loadConfig();
                sender.sendMessage(config.getMessage("command.reload-success"));
                return true;
            }

            if (sub.equals("test")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(config.getMessage("command.player-only"));
                    return true;
                }
                if (!sender.hasPermission("welcomemat.test")) {
                    sender.sendMessage(config.getMessage("command.no-permission"));
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(config.getMessage("command.test.usage"));
                    return true;
                }

                Player player = (Player) sender;
                String type = args[1].toLowerCase();
                
                Player target = player;
                if (args.length >= 3) {
                    Player found = plugin.getServer().getPlayer(args[2]);
                    if (found != null) {
                        target = found;
                    } else {
                        sender.sendMessage(config.getMessage("command.test.target-not-found")
                            .replace("%target%", args[2]));
                    }
                }
                
                if (type.equals("join")) {
                    testJoin(player, target);
                } else if (type.equals("leave")) {
                    testLeave(player, target);
                } else {
                    sender.sendMessage(config.getMessage("command.test.usage"));
                }
                return true;
            }
        }

        sender.sendMessage(config.getMessage("command.help.header")
            .replace("%version%", plugin.getDescription().getVersion()));
        sender.sendMessage(config.getMessage("command.help.reload"));
        sender.sendMessage(config.getMessage("command.help.test"));
        return true;
    }

    private void testJoin(Player receiver, Player target) {
        ConfigManager config = plugin.getConfigManager();
        receiver.sendMessage(config.getMessage("command.test.simulating-join")
            .replace("%target%", target.getName()));

        long delay = config.getGlobalDelay();
        plugin.getSchedulerAdapter().runEntityLater(receiver, delay, () -> {
            if (!receiver.isOnline()) return;

            // Message
            String message = null;
            if (config.isJoinMessageEnabled()) {
                message = plugin.parsePlaceholders(target, config.getJoinMessage());
            }
            if (message != null && !message.isEmpty()) {
                receiver.sendMessage(message + config.getMessage("command.test.visible-only")
                    .replace(config.getMessage("prefix"), "")); // Remove prefix from suffix if present
            }

            // Title
            if (config.isJoinTitleEnabled()) {
                receiver.sendTitle(
                    plugin.parsePlaceholders(target, config.getJoinTitle()),
                    plugin.parsePlaceholders(target, config.getJoinSubtitle()),
                    config.getTitleFadeIn(),
                    config.getTitleStay(),
                    config.getTitleFadeOut()
                );
            }

            // Sound
            if (config.isJoinSoundEnabled()) {
                Sound sound = config.getJoinSound();
                if (sound != null) {
                    receiver.playSound(
                        receiver.getLocation(),
                        sound,
                        config.getJoinSoundVolume(),
                        config.getJoinSoundPitch()
                    );
                }
            }
        });
    }

    private void testLeave(Player receiver, Player target) {
        ConfigManager config = plugin.getConfigManager();
        
        String message = null;
        if (config.isLeaveMessageEnabled()) {
            message = plugin.parsePlaceholders(target, config.getLeaveMessage());
        }
        
        if (message != null && !message.isEmpty()) {
            receiver.sendMessage(message + config.getMessage("command.test.visible-only")
                .replace(config.getMessage("prefix"), ""));
        } else {
            receiver.sendMessage(config.getMessage("command.test.leave-disabled"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Stream.of("reload", "test", "help")
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("test")) {
            return Stream.of("join", "leave")
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("test")) {
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
