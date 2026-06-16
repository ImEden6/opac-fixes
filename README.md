# OPAC Fixes

A Fabric Minecraft mod that provides claim protection compatibility fixes for **Open Parties and Claims (OPAC)** when used alongside other popular mods.

## Overview

Some mods implement custom explosion or block placement/breaking mechanics that bypass standard Fabric event listeners, allowing players or mobs to bypass OPAC claims protection. This mod surgically intercepts these behaviors to ensure that claims remain protected.

## Features

- **Mutant Monsters**: Prevents mutated explosions (e.g. Mutant Creepers) from destroying blocks and harming entities inside protected claims.
- **Supplementaries**: Prevents Red Merchants' bombs from bypass griefing blocks and entities in protected claims.
- **Botania**:
  - **Ring of Loki**: Prevents players from bypass-placing or bypass-breaking blocks inside protected claims using relic blueprints.
  - **Bore Lens**: Prevents mana bursts fired by players (via Mana Blaster) or block entities (via Mana Spreader) from breaking blocks inside claims. Fired bursts will dissipate on impact.
  - **Red String (Container, Dispenser, Spoofer, Comparator, etc.)**: Prevents unauthorized inventory access or block interactions across claim boundaries.
- **Conditional Loading**: Compatibility mixins for Mutant Monsters, Supplementaries, and Botania only load if the respective mods are detected at runtime.

## Server Administration & Configuration

### Allowing Spreader Automation (Bore Lens)
By default, Mana Spreaders equipped with a Bore Lens are blocked from breaking blocks inside protected claims. If server administrators wish to permit automated block-breaking setups (e.g., automated quarries or tree farms), they can explicitly whitelist the Botania mana burst entity:

1. Open the OPAC server config file (typically `config/openpartiesandclaims/server.json`).
2. Locate the `entitiesAllowedToGrief` list.
3. Add `"break$botania:mana_burst"` to the list.

## Installation

1.  Ensure you have **Fabric Loader** and **Fabric API** installed for Minecraft 1.20.1.
2.  Requires **Open Parties and Claims (OPAC)** to be present.
3.  Drop this mod into your `mods` folder.

## Development Setup

This project uses Gradle. To set up the workspace:

1.  Clone the repository.
2.  Open in your IDE.
3.  Run `./gradlew genSources` to generate Minecraft source code.
4.  Run `./gradlew build` to compile.

## License

See [License.txt](License.txt) for details.
