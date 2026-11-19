package io.github.emberbocor.rememberedheroes.hero;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.rememberedheroes.RememberedHeroes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.saveddata.SavedData;

final class HeroData extends SavedData {
    // Vanilla needs a data fix type; this one has no fixes for data saved by the running version.
    private static final SavedData.Factory<HeroData> FACTORY =
            new SavedData.Factory<>(HeroData::new, HeroData::load, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);

    private final List<Village> villages = new ArrayList<>();

    static HeroData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, RememberedHeroes.MODID);
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

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag villageTags = new ListTag();
        for (Village village : villages) {
            ListTag heroTags = new ListTag();
            for (Object2IntMap.Entry<UUID> hero : village.heroes.object2IntEntrySet()) {
                CompoundTag heroTag = new CompoundTag();
                heroTag.putUUID("uuid", hero.getKey());
                heroTag.putInt("level", hero.getIntValue());
                heroTags.add(heroTag);
            }
            CompoundTag villageTag = new CompoundTag();
            villageTag.putLong("center", village.center.asLong());
            villageTag.put("heroes", heroTags);
            villageTags.add(villageTag);
        }
        tag.put("villages", villageTags);
        return tag;
    }

    private static HeroData load(CompoundTag tag, HolderLookup.Provider registries) {
        HeroData data = new HeroData();
        // Files saved by 1.20.x come wrapped in "sequences" by the random sequences data fix.
        CompoundTag villagesTag = tag.contains("villages", Tag.TAG_LIST) ? tag : tag.getCompound("sequences");
        for (Tag villageEntry : villagesTag.getList("villages", Tag.TAG_COMPOUND)) {
            CompoundTag villageTag = (CompoundTag) villageEntry;
            Object2IntMap<UUID> heroes = new Object2IntOpenHashMap<>();
            for (Tag heroEntry : villageTag.getList("heroes", Tag.TAG_COMPOUND)) {
                CompoundTag heroTag = (CompoundTag) heroEntry;
                heroes.put(heroTag.getUUID("uuid"), heroTag.getInt("level"));
            }
            data.villages.add(new Village(BlockPos.of(villageTag.getLong("center")), heroes));
        }
        return data;
    }

    private record Village(BlockPos center, Object2IntMap<UUID> heroes) {
    }
}
