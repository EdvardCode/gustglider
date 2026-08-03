package com.edvardcode.gustglider.client;

import com.edvardcode.gustglider.GustgliderMod;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GustgliderMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GustgliderClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GustgliderMod.UMBRELLAS.values().forEach(umbrella -> {
                ItemProperties.register(
                        umbrella.get(),
                        new ResourceLocation("open"),
                        (stack, level, entity, seed) -> {
                            if (stack.getItem() != umbrella.get()) return 0.0F;
                            return stack.getOrCreateTag().getBoolean("isOpen") ? 1.0F : 0.0F;
                        }
                );
            });
        });
    }
}