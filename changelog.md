# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.2] - 2026-06-17

### Added

- Added Open Parties and Claims (OPAC) boundary protection for Botania's Red String block entities (Container, Dispenser, Spoofer, Comparator, Fertilizer, etc.), blocking unauthorized cross-claim connections and inventory access.

## [1.1.1] - 2026-06-16

### Added

- Added Open Parties and Claims (OPAC) protection support for Botania's Bore Lens when fired from a Mana Spreader or Mana Blaster. Protected blocks are safe, and the mana burst will terminate upon impact.

## [1.1.0] - 2026-06-13

### Added

- Renamed the mod to `opac_fixes` (mod ID: `opac_fixes`, package: `com.mervyn.opac_fixes`).
- Implemented a Fabric `IMixinConfigPlugin` (`OpacFixesMixinPlugin`) for conditional loading of mixin classes based on mod presence at runtime.
- Added claim protection compatibility bridge (`OpacCompat`) via reflection to Open Parties and Claims.
- Added compatibility fix for Supplementaries' `BombExplosion` (Red Merchant bombs) to filter blocks and entities in OPAC-claimed areas.
- Added compatibility fix for Botania's `RingOfLokiItem` to prevent players from placing blocks or breaking blocks inside protected claims.
- Updated project to build with Gradle `8.14.5` and Fabric Loom `1.11-SNAPSHOT`.
- Configured build dependencies to use standard CurseForge Maven coordinates.

## [1.0.0] - 2026-05-13

### Added

- Initial release (as `mutant_creepers_no_explody`) containing Mutant Monsters explosion claim protection.
