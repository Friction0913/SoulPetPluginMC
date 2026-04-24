# SoulPetsPlugin

A **Minecraft Paper 1.21+** plugin that lets players capture, store, summon, and level up mobs as personal pets. Features RPG-style stats, hunger & health systems, combat assistance, pathfinding, and a fully customizable resource pack for unique item textures.

---

## Features

- **Capture any mob** with a crafted *Soul Pearl* (custom textured ender pearl)
- **Pet storage & summoning** — store up to 10 pets, summon up to 2 active at once
- **5 RPG stats** — Strength, Defense, Speed, Health, Hunting (level up through combat)
- **Hunger & health system** — pets need food, lose hunger over time, and regenerate health when well-fed
- **Combat assistance** — pets auto-aggro when you attack or are attacked; gain Hunting XP on kills
- **Pathfinding + teleport fallback** — pets follow you intelligently; teleport if stuck or too far
- **Hardcore death** — if a pet dies, it is permanently deleted
- **GUI management** — view, summon, store, feed, and delete pets through an inventory GUI
- **Custom resource pack** — unique Soul Pearl texture via CustomModelData 1001

---

## Requirements

- **Server:** Paper 1.21.4 (or newer)
- **Java:** 21
- **Resource Pack:** Required for players to see the custom Soul Pearl texture

---

## Installation

1. Download the latest `SoulPetsPlugin-1.0.0.jar` from [Releases](../../releases).
2. Place the JAR in your server's `plugins/` folder.
3. Download `SoulPetsResourcePack.zip` from the same release.
4. Host the ZIP on a direct-download URL (GitHub Releases, Dropbox, etc.).
5. Set the URL in `server.properties`:
   ```properties
   resource-pack=https://your-direct-url.com/SoulPetsResourcePack.zip
   ```
6. Restart the server.

---

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/pet` | Open the pet management GUI | `soulpets.gui` |
| `/pet summon <name>` | Summon a stored pet by name | `soulpets.summon` |
| `/pet store <name>` | Return an active pet to storage | `soulpets.store` |
| `/pet feed` | Feed your active pets with held food | `soulpets.feed` |
| `/pet info <name>` | View detailed stats of a pet | `soulpets.info` |
| `/pet rename <old> <new>` | Rename a stored pet | `soulpets.rename` |
| `/pet delete <name>` | Permanently delete a pet | `soulpets.delete` |
| `/petsadmin givepearl [player]` | Give a Soul Pearl to a player | `soulpets.admin` |
| `/petsadmin reload` | Reload the plugin configuration | `soulpets.admin` |

### Default Permissions
All player commands default to `true`. Admin commands default to `op`.

---

## Configuration

Edit `plugins/SoulPetsPlugin/config.yml`:

```yaml
max-stored-pets: 10
max-active-pets: 2
hunger-decay: 0.1
health-regen: 0.05
hunger-xp-block: 10

# Soul Pearl crafting recipe
soul-pearl:
  top: [ENDER_PEARL, DIAMOND, ENDER_PEARL]
  middle: [DIAMOND, GHAST_TEAR, DIAMOND]
  bottom: [ENDER_PEARL, DIAMOND, ENDER_PEARL]

# Preferred foods per entity type
preferred-foods:
  WOLF: BONE
  CAT: COD
  HORSE: GOLDEN_APPLE
  PARROT: COOKIE
  default: WHEAT

# XP curve
level-curve:
  base: 100
  multiplier: 1.5

# Messages
messages:
  prefix: "&6[SoulPets] &r"
  capture-success: "&aYou captured %pet%!"
  pet-death: "&c%pet% has died and its soul is lost forever..."
```

---

## Building from Source

```bash
# Clone the repository
git clone https://github.com/Friction0913/SoulPetPluginMC.git
cd SoulPetPluginMC

# Build the plugin JAR
./gradlew build
```

The compiled JAR will be in `build/libs/`.

To package the resource pack:
```bash
# Windows PowerShell
Compress-Archive -Path ".\resource-pack\*" -DestinationPath ".\resource-pack\SoulPetsResourcePack.zip"
```

---

## Resource Pack Notes

The custom Soul Pearl uses **CustomModelData 1001** on an ender pearl. If you already use a resource pack on your server, merge these files into your existing pack:
- `assets/minecraft/models/item/ender_pearl.json`
- `assets/minecraft/models/item/soul_pearl.json`
- `assets/minecraft/textures/item/soul_pearl.png`

---

## Support & Issues

Found a bug or have a suggestion? Open an [issue](../../issues).

---

## License

[MIT](LICENSE)
