# RPG Tab Overlay — Modrinth Project Information

Use this information when creating your mod page on [Modrinth](https://modrinth.com):

---

### 1. General Project Details
- **Project Name**: `RPG Tab Overlay`
- **Project Type**: Mod
- **Summary / Short Description** (127 characters):
  > *A clean, customizable fantasy RPG player list (TAB menu) with health bars, stats, client FPS, and live server hardware metrics.*
- **Categories**: `Utility`, `GUI`, `Server Utility`, `Information`
- **Environment**: Client and Server (`Both`)
- **License**: MIT
- **Icon**: Upload `icon.png` (512x512 PNG included in the repository)

---

### 2. Full Description (Copy & Paste directly into Modrinth Description)

```markdown
# 🛡️ RPG Tab Overlay (NeoForge 1.21.1)

A clean, highly customizable medieval/fantasy RPG player list (**TAB menu**) replacement for **Minecraft NeoForge 1.21.1**.

![RPG Tab Overlay Preview](https://raw.githubusercontent.com/JamesDAdams/rpgtab/main/preview.png)

Transform your standard multiplayer player list into an immersive adventurer roster with live player stats, dynamic health gauges, client FPS (with frame generation support), and real-time server hardware performance monitoring.

---

### ✨ Features

#### 👑 Player Roster & Stats
- **Fantasy RPG Design**: Dark translucent backdrop framed with double gold/bronze borders and ornate corner accents.
- **Top-Priority HUD Layer**: Automatically suppresses conflicting HUD layers (like PartyHUD or RPG-HUD) while TAB is held for optimal readability.
- **Player Avatar**: Dynamic head render of player skins.
- **FTB Teams Support**: Displays party team names in italics directly below player usernames (*[Team]*).
- **Player Dimension**: Displays current dimension (`✦ Dim`: Overworld, Nether, The End, or modded dimensions).
- **Dynamic Health Bar**: Visual fill bar with exact numerical HP centered inside (e.g. `20/20`) with smooth green > orange > red color progression.
- **Food & Hunger**: Precise hunger points (`20/20`).
- **Experience Level**: Formatted level (`Lvl X`).
- **Deaths Counter**: Official vanilla death statistic tracking (`Dead X`).
- **FTB Quests**: Total completed quests per player or team.
- **Advancements**: Unlocked achievements count (crafting recipes excluded).
- **Playtime & Ping**: Total server playtime (e.g. `1h 23m`) and color-coded network latency in ms.

#### ⚡ Performance & Hardware Metrics
- **Client FPS (GGTU Frame Gen Compatible)**: Live FPS counter in the top-right corner, displaying real frames and generated/interpolated frames from **NeoForge GGTU (Resolution Scaler)** (e.g. `136 FPS (68 + 68 gen)`).
- **Server Performance Footer Bar**:
  - **Line 1 (Hardware & Network)**:
    - **TPS & MSPT**: Instantaneous tick performance (`☼ TPS: 20.0 (15.4ms)`).
    - **CPU Quota**: Process CPU usage normalized against container/host allocated cores (`🖥 CPU: 614% / 600%`).
    - **GPU Load**: Real-time GPU load percentage (`⚡ GPU: 92%`).
    - **RAM & Swap**: Container cgroup RAM & Swap usage (`💾 RAM: 7.5 Go/16.8 Go`, swap displayed only when active).
    - **Network Traffic**: Real-time cumulative data transfer (`🌐 Net: ↓ 35.0 MiB ↑ 48.6 MiB`).
    - **Uptime**: Server uptime since last reboot (`⏱ Up: 8m`).
  - **Line 2 (World Generation & Engines)**:
    - **Loaded Chunks**: Total active world chunks (`☵ 543 loaded`).
    - **Distant Horizons**: Real-time LOD chunk generation queue tracking (`🌌 DH: 53 gen`).
    - **C2ME OpenCL**: GPU chunk acceleration module status (`✔ C2ME OpenCL: Actif`).

---

### 🧩 Built-in Ecosystem Compatibility
Designed with safe, soft-reflection integrations — no hard dependencies and zero crashes if any mod is absent:

| Mod | Platform | Integration |
|---|---|---|
| **NeoForge GGTU (Resolution Scaler)** | Client | Native + interpolated frame generation breakdown. |
| **FTB Teams** | Server / Client | Party team name formatting below username. |
| **FTB Quests** | Server / Client | Live team/player completed quest counters. |
| **C2ME OpenCL** | Server | GPU acceleration context, real-time load and status. |
| **Distant Horizons (DH)** | Client / Server | Real-time LOD chunk generation queue monitoring. |
| **Pelican / Pterodactyl Panel** | Server | Direct API client integration to fetch exact dashboard metrics. |

---

### ⚙️ Server Configuration (`config/rpgtab-server.toml`)
Every single column, metric, color, and title can be toggled and customized to fit your server's style. Includes an optional `[pelican_api]` section to query your hosting panel directly.
```
