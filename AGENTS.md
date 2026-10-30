# AGENTS.md

## Directives & Règles pour les Agents

### Gestion des versions
- **Règle absolue** : À chaque modification du code source du mod ou recompilation du JAR, la version du mod doit impérativement être incrémentée (SemVer).
- Fichiers à synchroniser systématiquement avec la nouvelle version :
  1. `src/main/resources/META-INF/neoforge.mods.toml` (champ `version`)
  2. `build.ps1` (`$jarOut` et `$deployedJar`)
  3. `README.md` & `README.fr-FR.md` (titre et commandes d'exemple)
- Reconstruire le mod avec `build.ps1` après l'incrémentation de version pour déployer le JAR mis à jour dans le profil Modrinth.
