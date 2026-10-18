package com.rajanpreet.revival.item;

import com.rajanpreet.revival.ModRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FragmentOfRevivalItem extends Item {

    private static final int NIGHT_VISION_TICKS = 10 * 20;
    private static final int SLOWNESS_TICKS = 5 * 20;

    public FragmentOfRevivalItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            world.playSound(
                    null,
                    user.getX(), user.getY(), user.getZ(),
                    ModRegistry.FRAGMENT_USE_SOUND,
                    SoundCategory.PLAYERS,
                    1.0F, 0.7F
            );

            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NIGHT_VISION, NIGHT_VISION_TICKS, 0, false, false, true));
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SLOWNESS, SLOWNESS_TICKS, 0, false, false, true));
        }

        if (!user.getAbilities().creativeMode) {
            stack.decrement(1);
        }

        return TypedActionResult.success(stack, world.isClient);
    }
}
