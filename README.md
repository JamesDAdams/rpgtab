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
  - **Player Dimension**: Displays each player's current dimension (Overworld, Nether, End, or custom mod dimension).
  - **Dynamic Health Bar**: Visual fill bar with exact numerical HP centered inside (e.g., `24/24`) and smooth color adaptation (green > orange > red).
  - **Food Level**: Hunger points displayed as `20/20`.
  - **XP Level**: Experience level formatted as `Lvl X`.
  - **Deaths Counter**: Vanilla statistics death count tracking.
  - **FTB Quests**: Total completed quests per team/player (safe reflection fallback if FTB Quests is not installed).
  - **Advancements**: Unlocked achievements count (crafting recipes are filtered out).
  - **Server Play Time**: Formatted total playtime (e.g., `2h 15m`).
  - **Latency / Ping**: Color-coded ping in milliseconds.
- **Client & Performance**:
  - **Client FPS**: Real-time FPS counter in the top-right corner of the TAB menu, fully compatible with **NeoForge GGTU (Resolution Scaler)** frame generation (displays total FPS, real frames, and generated frames).
- **Server Hardware & Network Metrics (Footer)**:
  - Real-time **TPS & MSPT** display with color indicators.
  - Server **CPU usage** percentage formatted against container allocated cores (e.g. `77% / 600%`).
  - **GPU Acceleration**: Displays the GPU load percentage (`⚡ GPU: 0%`).
  - Allocated & container cgroup **RAM & Swap** usage (e.g. `4.8 Go/16.8 Go (Swap: 0.0 Go/8.0 Go)`).
  - Real-time **Network throughput & transfer totals** (current speed in Ko/s or Mo/s via `/proc/net/dev` and Pelican).
  - Real-time **Server Chunks**: Total loaded chunks on the server and real-time generating chunks (Vanilla + Distant Horizons).
  - Server **Uptime** since last reboot (⏱ Up:).
  - **C2ME OpenCL, Distant Horizons & SeedGen status**: Live indicators showing whether background chunk generation engines are active.
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
| **NeoForge GGTU (Resolution Scaler)** | Client | Displays real-time FPS with breakdown of native rendering frames and interpolated/generated frames (e.g. `144 FPS (48 + 96 gen)`). |
| **FTB Teams** | Server / Client | Automatically detects and formats party team names in italics directly under player usernames. |
| **FTB Quests** | Server / Client | Displays total team / player completed quest counters in real-time. |
| **C2ME (OpenCL Acceleration Module)** | Server | Detects GPU acceleration, OpenCL runtime device context, and acceleration status. |
| **Distant Horizons (DH)** | Client / Server | Displays live Distant Horizons server-side status and tracks LOD chunk generation queues. |
| **Distant Horizons SeedGen** | Server | Detects SeedGen world generation engine status and validates terrain stand-down conditions. |
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
The compiled JAR will be output to `build/` as `rpgtab-1.28.0-neoforge-1.21.1.jar`.
