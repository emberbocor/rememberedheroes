# Remembered Heroes

Villages remember the players who saved them from a raid.

In vanilla, winning a raid makes you Hero of the Village for a while: villagers lower their prices and throw you gifts, and when the effect runs out the village forgets what you did. With Remembered Heroes the village keeps the memory. Each time you come back, you get Hero of the Village again at the highest level you ever earned there, with the trade discount. The gifts stay a reward for the raid you just won.

The mod runs on the server only. Players join with a plain vanilla client.

Available for NeoForge, Forge and Fabric.

## Features

- **Villages remember their heroes.** Every player who gets Hero of the Village for winning a raid is remembered by that village, together with the level of the effect.
- **The effect comes back.** A remembered hero who enters the village gets Hero of the Village again, at the highest level earned there. It wears off by itself shortly after the hero leaves.
- **Discounts, no gifts.** The returning effect gives the vanilla trade discount. Villagers only throw gifts for the effect of a raid you just won.
- **The raid's own effect stays vanilla.** While you still have the effect from a raid, the mod leaves it alone.
- **Killing a villager costs you the memory.** A hero who kills a villager in the village is forgotten by that village.
- **Saved with the world.** Memories are permanent and survive server restarts.
- **No config, nothing to install on clients.**

## How it works

- **Who is remembered.** When a raid is won, every player who receives Hero of the Village from it is remembered as a hero of that village, with the level of that effect. A player can be a hero of many villages.
- **Which village.** The village is placed at the center of the raid when it is won. A later raid whose center is within 96 blocks of a remembered village counts as the same village (the closest one, if several are in reach); a raid farther away starts a new village.
- **Being in the village.** You are in a remembered village when the game itself counts your position as part of a village (the area around beds, bells and job sites that villagers have claimed) and you are within 96 blocks of the village's raid center. 96 blocks is the distance at which vanilla counts players as taking part in a raid.
- **Levels.** The returning effect has the highest level you ever earned in that village. Winning another raid there at a higher Bad Omen or Raid Omen level raises it; a lower one keeps the old level. When you stand in more than one remembered village, the highest level applies.
- **The returning effect.** It is refreshed every second while you are in the village and lasts 15 seconds, so it ends shortly after you leave. Like the raid's effect, it shows an icon and no particles. It gives the vanilla trade discount for its level, but villagers do not throw gifts to a player whose only Hero of the Village effect is this one.
- **The raid's own effect.** As long as you still have the effect a raid gave you, the mod does not add, change or remove anything, and villagers throw gifts as usual. The returning effect takes over after the raid's effect runs out. If you win a raid while the returning effect is active, the raid's effect replaces it.
- **Losing the memory.** If a hero kills a villager that is in a remembered village, that village forgets the hero for good, and the returning effect is removed at once. The player counts as the killer in the same cases where vanilla villagers remember a murder: also with arrows and other projectiles the player fired or TNT the player lit, but not when a tamed wolf, a zombie, lava or a fall does the killing. The effect of a raid that is still active is not removed.
- **No messages.** The mod never shows text on screen.

## Requirements

| | NeoForge | Forge | Fabric |
|---|---|---|---|
| Minecraft | 26.1 to 26.1.2 | 26.1 to 26.1.2 | 26.1 to 26.1.2 |
| Loader | NeoForge 26.1.0.1-beta or newer | Forge 62.0.0 or newer | Fabric Loader 0.18.2 or newer |
| Jar | `rememberedheroes-neoforge-26.1-26.1.2-<version>.jar` | `rememberedheroes-forge-26.1-26.1.2-<version>.jar` | `rememberedheroes-fabric-26.1-26.1.2-<version>.jar` |

On Minecraft 26.1 to 26.1.2, NeoForge and Forge are separate loaders that do not run each other's mods, so each has its own jar. Pick the jar that matches your server's loader. Fabric API is not needed.

There is no config file.

### Client and server

Install the mod on the server only, on every loader. It adds no items, blocks, entities, commands, network messages or registries, so the server has nothing a client needs to know about:

- **NeoForge:** vanilla clients and NeoForge clients without the mod can join.
- **Forge:** vanilla clients and Forge clients without the mod can join. The mod is marked as server-only, so a Forge client without it still shows the server as compatible in the server list.
- **Fabric:** vanilla clients and Fabric clients without the mod can join.

Installing it on a client as well does no harm. Everything happens on the server, and the effect is the vanilla Hero of the Village effect.

In singleplayer, install it in your own game; the built-in server runs it.

## Saved data

The memories are stored per dimension in the world folder: `data/rememberedheroes/heroes.dat` in the folder of the dimension where the raid took place (for the Overworld, `<world>/dimensions/minecraft/overworld/data/rememberedheroes/heroes.dat`). The file is created once a village remembers its first hero. It holds each village's raid center and the UUID and level of each hero.

Memories saved in a world from Minecraft 1.20.1, 1.21.1 or 1.21.11 are still read after the world is upgraded to 26.1, and are then saved in the new file. The old file `data/rememberedheroes.dat` is left where it was.

## Removing the mod

The world opens normally. Villages no longer give the effect back. A returning effect that a player still has when the mod is removed runs out within 15 seconds; until then villagers treat it as a normal Hero of the Village effect. The mod's data stays in the world folder (`dimensions/minecraft/overworld/data/rememberedheroes/heroes.dat`, and the same file for each other dimension); vanilla ignores it, and it does no harm. If you install the mod again, the villages remember their heroes again.

## Compatibility

- **Other ways to get Hero of the Village.** The mod tells its own effect apart by its short duration (15 seconds or less). A Hero of the Village effect of 15 seconds or less from a command or another mod is treated as the mod's: villagers do not throw gifts for it. The raid's effect lasts 40 minutes, so this only applies to its last 15 seconds.
- **Mods that change raids or villager gifts** may conflict. A mod that gives the raid effect in its own way, or lets villagers give gifts in their own way, may bypass the mod.
- **Mods that change what counts as a village** change where the returning effect is given, because the mod asks the game whether a position is part of a village.

## Building

```
./gradlew build
```

The jars are written to `neoforge/build/libs/`, `forge/build/libs/` and `fabric/build/libs/`.

## License

MIT.
