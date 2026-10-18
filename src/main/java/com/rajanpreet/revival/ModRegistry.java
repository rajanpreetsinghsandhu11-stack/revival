package com.rajanpreet.revival;

import com.rajanpreet.revival.entity.TheRevivedEntity;
import com.rajanpreet.revival.item.FragmentOfRevivalItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

public final class ModRegistry {

    private ModRegistry() {}

    public static void init() {}

    public static final SoundEvent FRAGMENT_USE_SOUND = Registry.register(
            Registries.SOUND_EVENT,
            RevivalMod.id("fragment_use"),
            SoundEvent.of(RevivalMod.id("fragment_use"))
    );

    public static final EntityType<TheRevivedEntity> THE_REVIVED = Registry.register(
            Registries.ENTITY_TYPE,
            RevivalMod.id("the_revived"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, TheRevivedEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6F, 1.95F))
                    .trackRangeBlocks(64)
                    .trackedUpdateRate(3)
                    .build()
    );

    public static final Item FRAGMENT_OF_REVIVAL = Registry.register(
            Registries.ITEM,
            RevivalMod.id("fragment_of_revival"),
            new FragmentOfRevivalItem(new Item.Settings().maxCount(16))
    );

    public static final Item THE_REVIVED_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            RevivalMod.id("the_revived_spawn_egg"),
            new SpawnEggItem(THE_REVIVED, 0x1E2B22, 0x9E1B1B, new Item.Settings())
    );

    public static final ItemGroup REVIVAL_HORROR_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            RevivalMod.id("revival_horror"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.revival.revival_horror"))
                    .icon(() -> new ItemStack(FRAGMENT_OF_REVIVAL))
                    .entries((displayContext, entries) -> {
                        entries.add(THE_REVIVED_SPAWN_EGG);
                        entries.add(FRAGMENT_OF_REVIVAL);
                    })
                    .build()
    );
}
