package com.rajanpreet.revival.entity;

import com.rajanpreet.revival.entity.ai.AvoidLightGoal;
import com.rajanpreet.revival.entity.ai.StalkerGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TheRevivedEntity extends HostileEntity {

    private static final int BLINDNESS_TICKS = 3 * 20;
    private static final double TELEPORT_DISTANCE = 15.0D;
    private static final int TELEPORT_ATTEMPTS = 16;

    public TheRevivedEntity(EntityType<TheRevivedEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new AvoidLightGoal(this, 1.0D));
        this.goalSelector.add(1, new StalkerGoal(this, 0.8D));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.7D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 12.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean hit = super.tryAttack(target);

        if (hit && target instanceof LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.BLINDNESS, BLINDNESS_TICKS, 0, false, false, true));

            if (!this.getWorld().isClient) {
                teleportToDarkness();
            }
        }
        return hit;
    }

    private void teleportToDarkness() {
        World world = this.getWorld();
        BlockPos origin = this.getBlockPos();

        BlockPos best = null;
        int bestLight = Integer.MAX_VALUE;

        for (int attempt = 0; attempt < TELEPORT_ATTEMPTS; attempt++) {
            double angle = this.random.nextDouble() * Math.PI * 2.0D;
            int x = origin.getX() + (int) Math.round(Math.cos(angle) * TELEPORT_DISTANCE);
            int z = origin.getZ() + (int) Math.round(Math.sin(angle) * TELEPORT_DISTANCE);

            BlockPos ground = findStandableColumn(world, x, origin.getY(), z);
            if (ground == null) {
                continue;
            }

            int light = world.getLightLevel(ground.up());
            if (light < bestLight) {
                bestLight = light;
                best = ground;
            }
        }

        if (best == null) {
            return;
        }

        double destX = best.getX() + 0.5D;
        double destY = best.getY();
        double destZ = best.getZ() + 0.5D;

        this.refreshPositionAndAngles(destX, destY, destZ, this.getYaw(), this.getPitch());
        this.getNavigation().stop();
        this.setVelocity(Vec3d.ZERO);

        world.playSound(null, destX, destY, destZ,
                SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 1.0F, 0.6F);
    }

    private static BlockPos findStandableColumn(World world, int x, int startY, int z) {
        BlockPos.Mutable feet = new BlockPos.Mutable();
        for (int y = startY + 4; y >= startY - 8; y--) {
            feet.set(x, y, z);
            BlockPos below = feet.down();
            if (world.getBlockState(feet).isAir()
                    && world.getBlockState(feet.up()).isAir()
                    && world.getBlockState(below).isSolidBlock(world, below)) {
                return feet.toImmutable();
            }
        }
        return null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }
}
