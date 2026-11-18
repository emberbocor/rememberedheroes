package io.github.emberbocor.rememberedheroes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.rememberedheroes.hero.HeroMemory;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.player.Player;

@Mixin(GiveGiftToHero.class)
abstract class GiveGiftToHeroMixin {
    @Inject(method = "isHero", at = @At("HEAD"), cancellable = true)
    private void rememberedheroes$noGiftForRemembered(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (HeroMemory.onlyRemembered(player)) {
            cir.setReturnValue(false);
        }
    }
}
