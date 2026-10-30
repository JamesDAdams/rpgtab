# RPG Tab Overlay (NeoForge 1.21.1)

A clean, highly customizable RPG-themed player list (TAB menu) replacement for Minecraft NeoForge 1.21.1.

![RPG Tab Overlay Preview](preview.png)

#### (Choose your language below / Choisissez votre langue ci-dessous)
[![English](https://img.shields.io/badge/Language-English-blue)](README.md)
[![Français](https://img.shields.io/badge/Language-Français-green)](README.fr-FR.md)

---

### 📌 Features
- **Fantasy RPG Design**: Dark semi-translucent backdrop framed with double gold/bronze trim and ornate corner accents.
- **Top Priority Display**: Automatically clears conflicting UI layers (such as RPG-HUD or PartyHUD) while holding the TAB key, guaranteeing full visibility.
- **Comprehensive Player Stats**:
  - **Player Avatar**: Dynamic player skin head rendering.
  - **FTB Team Name**: Displays the player's FTB Team name in italics directly below their username (e.g., *MyTeam*) only when the player belongs to a team.
  - **Player Dimension**: Displays each player's current dimension (`✦ Dim`: Overworld, Nether, End, or custom mod dimension).
  - **Dynamic Health Bar**: Visual fill bar with exact numerical HP centered inside (e.g., `20/20`) and smooth color adaptation (green > orange > red).
  - **Food Level**: Hunger points displayed as `20/20`.
  - **XP Level**: Experience level formatted as `Lvl X`.
  - **Deaths Counter**: Vanilla statistics death count tracking (`Dead X`).
  - **FTB Quests**: Total completed quests per team/player (safe reflection fallback if FTB Quests is not installed).
  - **Advancements**: Unlocked achievements count (crafting recipes are filtered out).
  - **Server Play Time**: Formatted total playtime (e.g., `1h 23m`).
  - **Latency / Ping**: Color-coded ping in milliseconds.
- **Client & Performance**:
  - **Client FPS**: Real-time FPS counter in the top-right corner of the TAB menu, fully compatible with **NeoForge GGTU (Resolution Scaler)** frame generation (displays total FPS, real frames, and generated frames, e.g. `136 FPS (68 + 68 gen)`).
- **Server Hardware & Network Metrics (Footer)**:
  - **Line 1 (Core Hardware & Network)**:
    - Real-time **TPS & MSPT** display (`☼ TPS: 20.0 (15.4ms)`).
    - Server **CPU usage** percentage against container allocated quota cores (`🖥 CPU: 614% / 600%`).
    - Real-time **GPU load** percentage (`⚡ GPU: 92%`).
    - Allocated & container cgroup **RAM & Swap** usage (`💾 RAM: 7.5 Go/16.8 Go`, swap displayed only when active).
    - Real-time **Network cumulative data transfer** matching Pelican dashboard (`🌐 Net: ↓ 35.0 MiB ↑ 48.6 MiB`).
    - Server **Uptime** since last reboot (`⏱ Up: 8m`).
  - **Line 2 (World Generation & Engine Status)**:
    - Real-time **Server Loaded Chunks** (`☵ 543 loaded`).
    - **Distant Horizons (DH)** real-time generation queue tracking (`🌌 DH: 53 gen`).
    - **C2ME OpenCL** GPU Acceleration Module status (`✔ C2ME OpenCL: Actif`).
- **Offline Players Support**:
  - Displays data for previously connected players who are currently offline (play time, deaths, FTB quests, and JSON advancements).
- **Server Configuration** (`config/rpgtab-server.toml`):
  - Toggle any column on or off (avatar, health, food, xp, deaths, ftb, advancements, playtime, ping, fps, dimension).
  - Customizable title text and colors (HEX format `0xRRGGBB` or `#RRGGBB`).
  - Toggle offline player visibility (enabled by default).
  - **Pelican Panel API Integration**: Configure `[pelican_api]` (`pelicanUrl`, `pelicanApiKey`, `pelicanServerId`) to fetch actual real-time container metrics directly from the Pelican panel.

---

### 🧩 Compatible Mods & Ecosystem Integrations
RPG Tab Overlay includes built-in soft compatibility (safe reflection hooks that never crash if the mod is absent) with the following ecosystem mods:

| Mod | Platform | Feature & Integration |
|---|---|---|
| **NeoForge GGTU (Resolution Scaler)** | Client | Displays real-time FPS with breakdown of native rendering frames and interpolated/generated frames (e.g. `136 FPS (68 + 68 gen)`). |
| **FTB Teams** | Server / Client | Automatically detects and formats party team names in italics directly under player usernames. |
| **FTB Quests** | Server / Client | Displays total team / player completed quest counters in real-time. |
| **C2ME (OpenCL Acceleration Module)** | Server | Detects GPU acceleration, OpenCL runtime device context, real-time load, and acceleration status. |
| **Distant Horizons (DH)** | Client / Server | Displays live Distant Horizons server-side status and tracks real-time LOD chunk generation queues (`🌌 DH: X gen`). |
| **Distant Horizons SeedGen** | Server | Detects SeedGen world generation engine status and validates terrain compatibility. |
| **Pelican / Pterodactyl Panel** | Server / Hosting | Live HTTP API integration to query real-time CPU quota, container cgroup RAM, container swap, and network I/O. |

---

### 📂 Project Structure
```
rpgtab/
├── src/
│   ├── main/
│   │   ├── java/com/rpgtab/
│   │   │   ├── client/          # GUI rendering, GGTU FPS hook & client listeners
│   │   │   ├── compat/          # FTB Quests & FTB Teams soft compatibility
│   │   │   ├── config/          # NeoForge server config specification
│   │   │   ├── network/         # S2C network synchronization packets
│   │   │   ├── server/          # Hardware probes, Pelican API, chunk collectors
│   │   │   └── RpgTabMod.java   # Mod entry point
│   │   └── resources/
│   │       └── META-INF/neoforge.mods.toml
│   └── test/
│       └── java/com/rpgtab/test/ # Standalone unit tests
└── build.ps1                    # Automated compilation and deployment script
```

### 🔨 Build Instructions
Requires Java 21 LTS (OpenJDK or Zulu):
```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
```
The compiled JAR will be output to `build/` as `rpgtab-1.33.0-neoforge-1.21.1.jar`.
