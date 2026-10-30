# RPG Tab Overlay (NeoForge 1.21.1)

Un remplacement soigné et hautement personnalisable de la liste des joueurs (menu TAB) façon RPG pour Minecraft NeoForge 1.21.1.

![Aperçu du RPG Tab Overlay](preview.png)

#### (Choisissez votre langue ci-dessous / Choose your language below)
[![English](https://img.shields.io/badge/Language-English-blue)](README.md)
[![Français](https://img.shields.io/badge/Language-Français-green)](README.fr-FR.md)

---

### 📌 Fonctionnalités
- **Design RPG Médiéval/Fantaisie** : Fond sombre translucide avec double bordure bronze/or et ornements d'angle dorés.
- **Affichage au Premier Plan Absolu** : Annule automatiquement les éléments d'interface conflictuels (comme RPG-HUD ou PartyHUD) lors de l'appui sur TAB pour une lisibilité optimale.
- **Statistiques Complètes par Joueur** :
  - **Tête du Joueur** : Avatar dynamique du skin.
  - **Nom de la Team FTB** : Affiche le nom de l'équipe FTB Teams en italique sous le pseudo du joueur (ex: *MaTeam*) uniquement lorsque le joueur appartient à une équipe.
  - **Dimension du Joueur** : Affiche la dimension actuelle de chaque joueur (Overworld, Nether, End, ou dimension de mod personnalisée).
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
- **Performances & Client** :
  - **FPS Client** : Compteur de FPS en temps réel en haut à droite du menu TAB, compatible avec **NeoForge GGTU (Resolution Scaler)** pour afficher les FPS réels et les frames générées.
- **Métriques Matérielles & Réseau du Serveur (Footer)** :
  - Affichage en temps réel des **TPS & MSPT** avec indicateurs de couleur.
  - Utilisation du **CPU** du serveur rapportée au nombre de cœurs alloués au conteneur (ex: `200%/600%`).
  - Consommation **RAM** conteneur et JVM (ex: `1.7 Go / 16.0 Go`).
  - Débit **Réseau** conteneur en temps réel (via `/proc/net/dev`, DL et UL en Ko/s ou Mo/s).
  - **Chunks Serveur** : Total des chunks chargés sur le serveur et chunks en cours de génération (Vanilla + Distant Horizons).
  - **Uptime** du serveur depuis le dernier reboot (⏱ Up:).
  - **Détection GPU, C2ME OpenCL, Distant Horizons & SeedGen** : Affiche le nom du GPU, l'accélération C2ME OpenCL ainsi que le statut Distant Horizons et SeedGen.
  - Options activables/désactivables individuellement dans `config/rpgtab-server.toml` (`[server_metrics]`).

### 📂 Structure du Projet
```
rpgtab/
├── src/
│   ├── main/
│   │   ├── java/com/rpgtab/
│   │   │   ├── client/          # Rendu graphique et listeners d'événements client
│   │   │   ├── compat/          # Compatibilité douce avec FTB Quests & FTB Teams
│   │   │   ├── config/          # Spécification de la configuration serveur NeoForge
│   │   │   ├── network/         # Packets de synchronisation réseau S2C
│   │   │   ├── server/          # Collecte des statistiques en ligne & hors ligne
│   │   │   └── RpgTabMod.java   # Point d'entrée du mod
│   │   └── resources/
│   │       └── META-INF/neoforge.mods.toml
│   └── test/
│       └── java/com/rpgtab/test/ # Tests unitaires autonomes
└── build.ps1                    # Script automatisé de compilation et de déploiement
```

### 🔨 Instructions de Compilation
Nécessite Java 21 LTS (OpenJDK ou Zulu) :
```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
```
Le fichier JAR sera généré dans `build/` sous le nom `rpgtab-1.15.0-neoforge-1.21.1.jar`.
