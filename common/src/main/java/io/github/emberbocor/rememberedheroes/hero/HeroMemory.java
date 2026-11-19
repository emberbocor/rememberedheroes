package io.github.emberbocor.rememberedheroes.hero;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

public final class HeroMemory {
    private static final int REFRESH_INTERVAL = 20;
    // Raids give the effect for 48000 ticks; an effect with at most this many ticks left is the one this mod gives.
    // More than 200 ticks above the refresh, so the icon never starts blinking while the hero stays in the village.
    private static final int DURATION = 300;

    private HeroMemory() {
    }

    public static void raidWon(ServerLevel level, BlockPos raidCenter, ServerPlayer hero, int heroLevel) {
        HeroData.get(level).remember(raidCenter, hero.getUUID(), heroLevel);
    }

    public static void tick(ServerPlayer player) {
        if (player.tickCount % REFRESH_INTERVAL != 0) {
            return;
        }
        MobEffectInstance current = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
        if (current != null && !isRemembered(current)) {
            return;
        }
        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition();
        int heroLevel = HeroData.get(level).heroLevel(pos, player.getUUID());
        if (heroLevel <= 0 || !level.isVillage(pos)) {
            return;
        }
        if (current != null && current.getAmplifier() != heroLevel - 1) {
            player.removeEffect(MobEffects.HERO_OF_THE_VILLAGE);
        }
        player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, DURATION, heroLevel - 1, false, false, true));
    }

    /**
     * The effect of a won raid replaces this mod's effect instead of merging with it.
     */
    public static void effectAdded(LivingEntity entity, MobEffectInstance added) {
        if (added.getEffect() == MobEffects.HERO_OF_THE_VILLAGE && !isRemembered(added)) {
            MobEffectInstance current = entity.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
            if (current != null && isRemembered(current)) {
                entity.removeEffect(MobEffects.HERO_OF_THE_VILLAGE);
            }
        }
    }

    public static void villagerDied(Villager villager, DamageSource cause) {
        if (!(cause.getEntity() instanceof ServerPlayer player) || !(villager.level() instanceof ServerLevel level)) {
            return;
        }
        BlockPos pos = villager.blockPosition();
        if (level.isVillage(pos) && HeroData.get(level).forget(pos, player.getUUID())) {
            MobEffectInstance current = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
            if (current != null && isRemembered(current)) {
                player.removeEffect(MobEffects.HERO_OF_THE_VILLAGE);
            }
        }
    }

    /**
     * @return whether the player's Hero of the Village effect is only the one this mod gives (no gifts for it)
     */
    public static boolean onlyRemembered(Player player) {
        MobEffectInstance current = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
        return current != null && isRemembered(current);
    }

    private static boolean isRemembered(MobEffectInstance effect) {
        return effect.endsWithin(DURATION);
    }
}
