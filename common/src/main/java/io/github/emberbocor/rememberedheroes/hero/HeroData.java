package io.github.emberbocor.rememberedheroes.hero;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.emberbocor.rememberedheroes.RememberedHeroes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

final class HeroData extends SavedData {
    // Files saved by 1.20.x come wrapped in "sequences" by the random sequences data fix; a file with neither has no villages.
    private static final Codec<HeroData> CODEC = Codec.withAlternative(
                    Village.CODEC.listOf().fieldOf("villages").codec(),
                    Village.CODEC.listOf().lenientOptionalFieldOf("villages", List.of()).codec().lenientOptionalFieldOf("sequences", List.of()).codec())
            .xmap(HeroData::new, data -> data.villages);
    // Vanilla needs a data fix type; this one has no fixes for data saved by the running version.
    private static final SavedDataType<HeroData> TYPE =
            new SavedDataType<>(RememberedHeroes.MODID, HeroData::new, CODEC, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);

    private final List<Village> villages;

    private HeroData() {
        this(List.of());
    }

    private HeroData(List<Village> villages) {
        this.villages = new ArrayList<>(villages);
    }

    static HeroData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    // The distance vanilla uses for the players taking part in a raid (ServerLevel#getRaidAt).
    private static boolean near(BlockPos center, BlockPos pos) {
        return center.distSqr(pos) < Raid.VALID_RAID_RADIUS_SQR;
    }

    /**
     * A raid whose center is near a remembered village belongs to the closest one; otherwise it is a new village.
     */
    void remember(BlockPos raidCenter, UUID hero, int heroLevel) {
        Village village = closest(raidCenter);
        if (village == null) {
            village = new Village(raidCenter.immutable(), new Object2IntOpenHashMap<>());
            villages.add(village);
            setDirty();
        }
        if (!village.heroes.containsKey(hero) || heroLevel > village.heroes.getInt(hero)) {
            village.heroes.put(hero, heroLevel);
            setDirty();
        }
    }

    /**
     * @return the highest level the hero earned in the remembered villages near the position, 0 if none
     */
    int heroLevel(BlockPos pos, UUID hero) {
        int heroLevel = 0;
        for (Village village : villages) {
            if (near(village.center, pos)) {
                heroLevel = Math.max(heroLevel, village.heroes.getInt(hero));
            }
        }
        return heroLevel;
    }

    /**
     * @return whether a village near the position remembered the hero
     */
    boolean forget(BlockPos pos, UUID hero) {
        boolean forgotten = false;
        for (Village village : villages) {
            if (near(village.center, pos) && village.heroes.containsKey(hero)) {
                village.heroes.removeInt(hero);
                forgotten = true;
            }
        }
        if (forgotten) {
            villages.removeIf(village -> village.heroes.isEmpty());
            setDirty();
        }
        return forgotten;
    }

    @Nullable
    private Village closest(BlockPos raidCenter) {
        Village closest = null;
        for (Village village : villages) {
            if (near(village.center, raidCenter)
                    && (closest == null || village.center.distSqr(raidCenter) < closest.center.distSqr(raidCenter))) {
                closest = village;
            }
        }
        return closest;
    }

    private record Village(BlockPos center, Object2IntMap<UUID> heroes) {
        static final Codec<Village> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.xmap(BlockPos::of, BlockPos::asLong).fieldOf("center").forGetter(Village::center),
                Hero.CODEC.listOf().xmap(Village::toMap, Village::toList).fieldOf("heroes").forGetter(Village::heroes)
        ).apply(instance, Village::new));

        private static Object2IntMap<UUID> toMap(List<Hero> heroes) {
            Object2IntMap<UUID> map = new Object2IntOpenHashMap<>();
            heroes.forEach(hero -> map.put(hero.uuid(), hero.level()));
            return map;
        }

        private static List<Hero> toList(Object2IntMap<UUID> heroes) {
            return heroes.object2IntEntrySet().stream().map(hero -> new Hero(hero.getKey(), hero.getIntValue())).toList();
        }
    }

    private record Hero(UUID uuid, int level) {
        static final Codec<Hero> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("uuid").forGetter(Hero::uuid),
                Codec.INT.fieldOf("level").forGetter(Hero::level)
        ).apply(instance, Hero::new));
    }
}
