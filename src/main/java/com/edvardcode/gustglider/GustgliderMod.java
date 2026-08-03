package com.edvardcode.gustglider;

import com.edvardcode.gustglider.item.UmbrellaItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod(GustgliderMod.MOD_ID)
public class GustgliderMod {

    public static final String MOD_ID = "gustglider";

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    // Карта: цвет -> предмет
    public static final Map<String, RegistryObject<Item>> UMBRELLAS = new LinkedHashMap<>();

    public static final String[] COLORS = {
            "white", "light_gray", "gray", "black",
            "brown", "red", "orange", "yellow",
            "lime", "green", "cyan", "light_blue",
            "blue", "purple", "magenta", "pink"
    };

    static {
        for (String color : COLORS) {
            UMBRELLAS.put(color, ITEMS.register("umbrella_" + color,
                    () -> new UmbrellaItem(new Item.Properties().stacksTo(1).durability(128))));
        }
    }

    public GustgliderMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modEventBus);

        modEventBus.addListener(this::addToCreativeTab);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.TOOLS_AND_UTILITIES) {
            for (RegistryObject<Item> umbrella : UMBRELLAS.values()) {
                event.accept(umbrella.get());
            }
        }
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}