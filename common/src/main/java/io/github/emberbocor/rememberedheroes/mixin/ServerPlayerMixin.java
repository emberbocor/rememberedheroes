package io.github.emberbocor.rememberedheroes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.rememberedheroes.hero.HeroMemory;
import net.minecraft.server.level.ServerPlayer;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void rememberedheroes$refreshEffect(CallbackInfo ci) {
        HeroMemory.tick((ServerPlayer) (Object) this);
    }
}
