# WelcomeMat Lite

A lightweight, bloat-free welcome plugin for your Minecraft server. 
Designed for modern server environments (Folia/Paper/Spigot 1.21+), focusing purely on the essentials: Join/Leave Messages, Titles, Sounds, and customization.

## ✨ Features

- **Lightweight & Fast:** No database, no complex GUIs, no heavy particle engines. Just the essentials.
- **Folia Supported:** Fully compatible with Folia's regionized threading model.
- **PlaceholderAPI Support:** Use any PAPI placeholders in your welcome messages and titles.
- **Configurable Delays:** Add a delay (in ticks) before welcome messages appear.
- **Silent Join Support:** Prevent welcome messages for admins or vanished players using permissions.
- **Testing Commands:** Test your configuration in real-time without relogging.
- **Fully Customizable Messages:** All system messages and feedback are configurable in `messages.yml`.
- **Legacy Color Support:** Full support for `&` color codes (e.g., `&a&l`) in all messages and titles.

## 📥 Installation

1. Download the plugin JAR.
2. Place it in your server's `plugins` folder.
3. (Optional) Install [PlaceholderAPI](https://modrinth.com/plugin/placeholderapi) for extended placeholder support.
4. Restart your server.

## ⚙️ Configuration Files

### `config.yml`
Controls the core behavior of the welcome/leave effects.

```yaml
delays:
  join-ticks: 20

suppress-permissions:
  - "welcomemat.silent"
  - "essentials.silentjoin"

messages:
  join:
    enabled: true
    text: "&e%player% &ajoined the game"
  first-join:
    enabled: true
    text: "&dWelcome %player% to the server for the first time!"
  leave:
    enabled: true
    text: "&e%player% &cleft the game"

titles:
  join:
    enabled: true
    title: "&6Welcome!"
    subtitle: "&eEnjoy your stay, %player%!"
    fade-in: 10
    stay: 70
    fade-out: 20

sounds:
  join:
    enabled: true
    sound: "ENTITY_PLAYER_LEVELUP"
    volume: 1.0
    pitch: 1.0
```

### `messages.yml`
Controls all plugin feedback, error messages, and system text.

```yaml
prefix: "&8[&eWelcomeMat&8] &r"
command:
  no-permission: "&cYou do not have permission to use this command."
  reload-success: "&aConfiguration reloaded successfully!"
  # ... and more
```

## 📜 Commands & Permissions

| Command | Permission | Description |
|---------|------------|-------------|
| `/wm reload` | `welcomemat.reload` | Reloads all configuration files. |
| `/wm test <join\|leave> [player]` | `welcomemat.test` | Simulates an event (visible only to you). Supports optional target player data. |
| (none) | `welcomemat.silent` | Prevents the join message from being broadcasted (configurable). |

## 🔌 Placeholders & Colors

- **Colors:** Use legacy codes like `&6` (Gold), `&l` (Bold), etc., in any message.
- **Placeholders:** `%player%` works by default.
- **PlaceholderAPI:** If installed, you can use any PAPI placeholder (e.g., `%server_online%`) in:
  - Join/Leave Messages
  - Titles & Subtitles
  - Custom system messages in `messages.yml`

## 🛠 Building

To build the project yourself using Maven:

```bash
mvn clean package
```

The output JAR will be in `target/`.

---

**Original Project:** [WelcomeMat](https://github.com/coffeeisle/welcome-mat) (This is a simplified "Lite" fork).