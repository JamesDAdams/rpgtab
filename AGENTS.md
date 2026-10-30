# AGENTS.md

## Agent Guidelines & Development Rules

### Version Management
- **Strict Rule**: Every single time the mod source code is modified or the JAR is recompiled, the mod version MUST be incremented (SemVer).
- Files that must be systematically updated with the new version:
  1. `src/main/resources/META-INF/neoforge.mods.toml` (`version` field)
  2. `build.ps1` (`$jarOut` and `$deployedJar`)
  3. `README.md` & `README.fr-FR.md` (titles and example commands)
- Always rebuild the mod using `build.ps1` after incrementing the version to deploy the updated JAR to the Modrinth profile.

### Code Style & Compatibility
- Keep soft compatibility (reflection-based) for optional mods such as FTB Quests, FTB Teams, C2ME OpenCL, and GGTU (Resolution Scaler).
- Ensure all standalone unit tests pass in `com.rpgtab.test.RpgTabUnitTests`.
