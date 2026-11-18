package io.github.emberbocor.rememberedheroes.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import io.github.emberbocor.rememberedheroes.hero.HeroMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.raid.Raid;

@Mixin(Raid.class)
abstract class RaidMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @Shadow
    private BlockPos center;

    @Shadow
    private int raidOmenLevel;

    // The raid-win trigger runs for every player that just received Hero of the Village (amplifier raidOmenLevel - 1).
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/PlayerTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private ServerPlayer rememberedheroes$rememberHero(ServerPlayer hero) {
        HeroMemory.raidWon(level, center, hero, raidOmenLevel);
        return hero;
    }
}
