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
  - **Dimension du Joueur** : Affiche la dimension actuelle de chaque joueur (`✦ Dim`: Overworld, Nether, End, ou dimension de mod personnalisée).
  - **Barre de Vie Intégrée** : Remplissage dynamique avec affichage direct des PV au centre (ex: `20/20`) et couleur adaptative (vert > orange > rouge).
  - **Nourriture** : Niveau de faim sous forme `20/20`.
  - **Niveau d'XP** : Affiché sous forme `Lvl X`.
  - **Compteur de Morts** : Statistique vanille officielle des morts (`Dead X`).
  - **Quêtes FTB Quests** : Nombre de quêtes terminées par joueur/équipe (support souple sans crash si le mod est absent).
  - **Progrès / Advancements** : Nombre d'avancements débloqués (les recettes de craft sont exclues).
  - **Temps de Jeu** : Temps passé sur le serveur formaté (ex: `1h 23m`).
  - **Latence / Ping** : Ping en millisecondes avec code couleur.
- **Performances & Client** :
  - **FPS Client** : Compteur de FPS en temps réel en haut à droite du menu TAB, compatible avec **NeoForge GGTU (Resolution Scaler)** pour afficher les FPS réels et les frames générées (ex: `136 FPS (68 + 68 gen)`).
- **Métriques Matérielles & Réseau du Serveur (Footer)** :
  - **Ligne 1 (Matériel & Réseau)** :
    - Affichage en temps réel des **TPS & MSPT** (`☼ TPS: 20.0 (15.4ms)`).
    - Utilisation du **CPU** du serveur rapportée au nombre de cœurs alloués au conteneur (`🖥 CPU: 614% / 600%`).
    - Charge du **GPU** en pourcentage en direct (`⚡ GPU: 92%`).
    - Consommation **RAM & Swap** du conteneur (`💾 RAM: 7.5 Go/16.8 Go`, swap masqué si non utilisé).
    - Trafic cumulé **Réseau** en temps réel synchronisé avec Pelican (`🌐 Net: ↓ 35.0 MiB ↑ 48.6 MiB`).
    - **Uptime** du serveur depuis le dernier reboot (`⏱ Up: 8m`).
  - **Ligne 2 (Génération & Moteurs du Monde)** :
    - Nombre de **Chunks Chargés** sur le serveur (`☵ 543 loaded`).
    - Suivi en temps réel de la file de génération **Distant Horizons (DH)** (`🌌 DH: 53 gen`).
    - État d'accélération **C2ME OpenCL** (`✔ C2ME OpenCL: Actif`).
- **Support des Joueurs Hors-Ligne** :
  - Affiche les statistiques des joueurs ayant déjà joué sur le serveur même lorsqu'ils sont déconnectés (temps de jeu, morts, quêtes FTB, avancements JSON).
- **Configuration Serveur Complète** (`config/rpgtab-server.toml`) :
  - Activation ou désactivation de chaque colonne au choix (avatar, health, food, xp, deaths, ftb, adv, time, ping, fps, dimension).
  - Personnalisation du titre et des couleurs en hexadécimal (`0xRRGGBB` ou `#RRGGBB`).
  - Option d'affichage des joueurs hors ligne (activée par défaut).
  - **Intégration API Pelican Panel** : Configuration sous `[pelican_api]` (`pelicanUrl`, `pelicanApiKey`, `pelicanServerId`) pour remonter directement les valeurs réelles du dashboard Pelican (CPU, RAM, swap, débit réseau).

---

### 🧩 Mods Compatibles & Intégrations de l'Écosystème
RPG Tab Overlay intègre une compatibilité douce native (réflexion sans dépendance dure qui ne crash jamais si le mod est absent) avec les mods suivants :

| Mod | Plateforme | Rôle & Intégration |
|---|---|---|
| **NeoForge GGTU (Resolution Scaler)** | Client | Affiche le compteur de FPS avec distinction entre images réelles et frames générées (`136 FPS (68 + 68 gen)`). |
| **FTB Teams** | Serveur / Client | Détecte automatiquement l'équipe et affiche son nom en italique sous le pseudo. |
| **FTB Quests** | Serveur / Client | Affiche le total de quêtes accomplies par joueur ou équipe en temps réel. |
| **C2ME (OpenCL Acceleration Module)** | Serveur | Détecte le GPU OpenCL, le contexte de calcul, la charge et son statut d'accélération. |
| **Distant Horizons (DH)** | Client / Serveur | Affiche le statut du serveur et suit les chunks LOD dans la file de génération (`🌌 DH: X gen`). |
| **Distant Horizons SeedGen** | Serveur | Détecte l'état du moteur SeedGen et vérifie la compatibilité du terrain. |
| **Pelican / Pterodactyl Panel** | Hébergeur / Serveur | Intégration API HTTP en direct pour interroger le quota CPU, la RAM, le swap conteneur et le trafic réseau. |

---

### 📂 Structure du Projet
```
rpgtab/
├── src/
│   ├── main/
│   │   ├── java/com/rpgtab/
│   │   │   ├── client/          # Rendu graphique, hook FPS GGTU & listeners
│   │   │   ├── compat/          # Compatibilité douce avec FTB Quests & FTB Teams
│   │   │   ├── config/          # Spécification de la configuration serveur NeoForge
│   │   │   ├── network/         # Packets de synchronisation réseau S2C
│   │   │   ├── server/          # Sondes matérielles, API Pelican, chunks
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
Le fichier JAR sera généré dans `build/` sous le nom `rpgtab-1.33.0-neoforge-1.21.1.jar`.
