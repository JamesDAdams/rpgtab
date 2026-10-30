# RPG Tab Overlay (NeoForge 1.21.1)

Mod Minecraft NeoForge 1.21.1 qui remplace l'overlay de la touche TAB (liste des joueurs) par un panneau de style RPG soigné, élégant et riche en informations.

![RPG Tab](screenshot.png)

## Fonctionnalités

- **Style RPG médiéval/fantaisie** : Cadre sombre élégant avec double liseré bronze/or et ornements d'angle dorés.
- **Statistiques complètes par joueur** :
  - **Tête du joueur** (skin dynamique / avatar).
  - **Barre de vie intégrée** avec affichage direct des PV (ex: `24/24`) et couleur adaptative (vert > orange > rouge).
  - **Niveau de faim** (`20/20`).
  - **Niveau d'XP** (`Lvl X`).
  - **Nombre de morts** (compteur officiel de statistiques vanille).
  - **Quêtes FTB Quests complétées** (intégration douce sans crash si le mod est absent).
  - **Advancements / Progrès débloqués** (exclut automatiquement les recettes simples de craft).
  - **Temps de jeu sur le serveur** formaté (ex: `2h 15m`).
  - **Latence (Ping)** colorée en millisecondes.
- **Support des joueurs déconnectés (Offline Players)** :
  - Affiche les statistiques des joueurs ayant déjà joué sur le serveur même lorsqu'ils sont hors ligne (morts, temps de jeu, quêtes FTB Quests sauvegardées, avancements JSON).
- **Mise au premier plan absolu** :
  - Annulation automatique des HUDs superposés (ex: RPG-HUD) lorsque TAB est pressé pour garantir une visibilité parfaite sans chevauchement.
- **Configurable via fichier serveur** (`config/rpgtab-server.toml`) :
  - Activation/désactivation de chaque colonne (avatar, health, food, xp, deaths, ftb, advancements, playtime, ping).
  - Configuration du titre et des couleurs en hexadécimal (ARGB ou RGB).
  - Option d'affichage des joueurs hors ligne.

## Structure du projet

```
rpgtab/
├── src/
│   ├── main/
│   │   ├── java/com/rpgtab/
│   │   │   ├── client/          # Rendu graphique RPG et gestion des événements client
│   │   │   ├── compat/          # Compatibilité et extraction FTB Quests / FTB Teams
│   │   │   ├── config/          # Définition de configuration serveur NeoForge
│   │   │   ├── network/         # Packets de synchronisation réseau S2C
│   │   │   ├── server/          # Collecte des statistiques en ligne & hors ligne
│   │   │   └── RpgTabMod.java   # Point d'entrée principal du mod
│   │   └── resources/
│   │       └── META-INF/neoforge.mods.toml
│   └── test/
│       └── java/com/rpgtab/test/ # Tests unitaires
└── build.ps1                    # Script automatisé de compilation ECJ & déploiement
```

## Compilation

Prérequis : Java 21 LTS (Zulu ou OpenJDK).

Exécutez le script PowerShell de build :
```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
```
Le fichier JAR prêt à l'emploi sera généré dans le dossier `build/` sous le nom `rpgtab-1.6.0-neoforge-1.21.1.jar`.
