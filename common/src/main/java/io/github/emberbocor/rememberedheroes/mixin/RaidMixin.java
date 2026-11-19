package io.github.emberbocor.rememberedheroes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import io.github.emberbocor.rememberedheroes.hero.HeroMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.raid.Raid;

@Mixin(Raid.class)
abstract class RaidMixin {
    @Shadow
    private BlockPos center;

    @Shadow
    private int raidOmenLevel;

    // The raid-win trigger runs for every player in the raid's level that just received Hero of the Village (amplifier raidOmenLevel - 1).
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/triggers/PlayerTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private ServerPlayer rememberedheroes$rememberHero(ServerPlayer hero) {
        HeroMemory.raidWon(center, hero, raidOmenLevel);
        return hero;
    }
}
