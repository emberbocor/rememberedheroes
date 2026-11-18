package io.github.emberbocor.rememberedheroes;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(RememberedHeroes.MODID)
public class RememberedHeroes {
    public static final String MODID = "rememberedheroes";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RememberedHeroes(IEventBus modEventBus, ModContainer modContainer) {
    }
}
