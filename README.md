# RPG Tab Overlay (NeoForge 1.21.1)

A clean, customizable RPG-themed player list (TAB menu) replacement for Minecraft NeoForge 1.21.1.

---

## Language / Langue
- [English](#english) (Default)
- [Français](#français)

---

<a name="english"></a>
## English

### Features
- **Fantasy RPG Design**: Dark semi-translucent backdrop framed with double gold/bronze trim and ornate corner accents.
- **Top Priority Display**: Automatically clears conflicting UI layers (such as RPG-HUD or PartyHUD) while holding the TAB key, guaranteeing full visibility.
- **Comprehensive Player Stats**:
  - **Player Avatar**: Dynamic player skin head rendering.
  - **Dynamic Health Bar**: Visual fill bar with exact numerical HP centered inside (e.g., `24/24`) and smooth color adaptation (green > orange > red).
  - **Food Level**: Hunger points displayed as `20/20`.
  - **XP Level**: Experience level formatted as `Lvl X`.
  - **Deaths Counter**: Vanilla statistics death count tracking.
  - **FTB Quests**: Total completed quests per team/player (safe reflection fallback if FTB Quests is not installed).
  - **Advancements**: Unlocked achievements count (crafting recipes are filtered out).
  - **Server Play Time**: Formatted total playtime (e.g., `2h 15m`).
  - **Latency / Ping**: Color-coded ping in milliseconds.
- **Offline Players Support**:
  - Displays data for previously connected players who are currently offline (play time, deaths, FTB quests, and JSON advancements).
- **Server Configuration** (`config/rpgtab-server.toml`):
  - Toggle any column on or off (avatar, health, food, xp, deaths, ftb, advancements, playtime, ping).
  - Customizable title text and colors (HEX format `0xRRGGBB` or `#RRGGBB`).
  - Toggle offline player visibility (enabled by default).

### Project Structure
```
rpgtab/
├── src/
│   ├── main/
│   │   ├── java/com/rpgtab/
│   │   │   ├── client/          # GUI rendering and client event listeners
│   │   │   ├── compat/          # FTB Quests & FTB Teams soft compatibility
│   │   │   ├── config/          # NeoForge server config specification
│   │   │   ├── network/         # S2C network synchronization packets
│   │   │   ├── server/          # Online & offline statistics collectors
│   │   │   └── RpgTabMod.java   # Mod entry point
│   │   └── resources/
│   │       └── META-INF/neoforge.mods.toml
│   └── test/
│       └── java/com/rpgtab/test/ # Standalone unit tests
└── build.ps1                    # Automated compilation and deployment script
```

### Build Instructions
Requires Java 21 LTS (OpenJDK or Zulu):
```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
```
The compiled JAR will be output to `build/` as `rpgtab-1.6.0-neoforge-1.21.1.jar`.

---

<a name="français"></a>
## Français

### Fonctionnalités
- **Design RPG Médiéval/Fantaisie** : Fond sombre translucide avec double bordure bronze/or et ornements d'angle dorés.
- **Affichage au Premier Plan Absolu** : Annule automatiquement les éléments d'interface conflictuels (comme RPG-HUD ou PartyHUD) lors de l'appui sur TAB pour une lisibilité optimale.
- **Statistiques Complètes par Joueur** :
  - **Tête du Joueur** : Avatar dynamique du skin.
  - **Barre de Vie Intégrée** : Remplissage dynamique avec affichage direct des PV au centre (ex: `24/24`) et couleur adaptative (vert > orange > rouge).
  - **Nourriture** : Niveau de faim sous forme `20/20`.
  - **Niveau d'XP** : Affiché sous forme `Lvl X`.
  - **Compteur de Morts** : Statistique vanille officielle des morts.
  - **Quêtes FTB Quests** : Nombre de quêtes terminées par joueur/équipe (support souple sans crash si le mod est absent).
  - **Progrès / Advancements** : Nombre d'avancements débloqués (les recettes de craft sont exclues).
  - **Temps de Jeu** : Temps passé sur le serveur formaté (ex: `2h 15m`).
  - **Latence / Ping** : Ping en millisecondes avec code couleur.
- **Support des Joueurs Hors-Ligne** :
  - Affiche les statistiques des joueurs ayant déjà joué sur le serveur même lorsqu'ils sont déconnectés (temps de jeu, morts, quêtes FTB, avancements JSON).
- **Configuration Serveur Complète** (`config/rpgtab-server.toml`) :
  - Activation ou désactivation de chaque colonne au choix.
  - Personnalisation du titre et des couleurs en hexadécimal (`0xRRGGBB` ou `#RRGGBB`).
  - Option d'affichage des joueurs hors ligne (activée par défaut).

### Compilation
Nécessite Java 21 LTS :
```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
```
Le fichier JAR sera généré dans `build/` sous le nom `rpgtab-1.6.0-neoforge-1.21.1.jar`.
