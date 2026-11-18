package io.github.emberbocor.rememberedheroes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.rememberedheroes.hero.HeroMemory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.npc.Villager;

@Mixin(Villager.class)
abstract class VillagerMixin {
    @Inject(method = "die", at = @At("HEAD"))
    private void rememberedheroes$forgetKiller(DamageSource cause, CallbackInfo ci) {
        HeroMemory.villagerDied((Villager) (Object) this, cause);
    }
}
