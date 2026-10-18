package com.rajanpreet.revival.entity.ai;

import com.rajanpreet.revival.entity.TheRevivedEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class AvoidLightGoal extends Goal {

    private static final int LIGHT_THRESHOLD = 10;
    private static final int SCAN_RADIUS_XZ = 6;
    private static final int SCAN_RADIUS_Y = 2;
    private static final int SCAN_INTERVAL = 20;
    private static final double FLEE_DISTANCE = 10.0D;

    private final TheRevivedEntity mob;
    private final double speed;
    private int scanCooldown;

    public AvoidLightGoal(TheRevivedEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (--scanCooldown > 0) {
            return false;
        }
        scanCooldown = SCAN_INTERVAL;

        Vec3d awayFromLight = computeAwayFromLight();
        if (awayFromLight == null) {
            return false;
        }

        Vec3d destination = mob.getPos().add(awayFromLight.multiply(FLEE_DISTANCE));
        return mob.getNavigation().startMovingTo(destination.x, destination.y, destination.z, speed);
    }

    @Override
    public boolean shouldContinue() {
        return !mob.getNavigation().isIdle();
    }

    private Vec3d computeAwayFromLight() {
        World world = mob.getWorld();
        BlockPos origin = mob.getBlockPos();
        BlockPos.Mutable cursor = new BlockPos.Mutable();

        double pushX = 0.0D;
        double pushZ = 0.0D;
        boolean foundLight = false;

        for (int dx = -SCAN_RADIUS_XZ; dx <= SCAN_RADIUS_XZ; dx++) {
            for (int dy = -SCAN_RADIUS_Y; dy <= SCAN_RADIUS_Y; dy++) {
                for (int dz = -SCAN_RADIUS_XZ; dz <= SCAN_RADIUS_XZ; dz++) {
                    cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);

                    int luminance = world.getBlockState(cursor).getLuminance();
                    if (luminance < LIGHT_THRESHOLD) {
                        continue;
                    }
                    foundLight = true;

                    double sx = mob.getX() - (cursor.getX() + 0.5D);
                    double sz = mob.getZ() - (cursor.getZ() + 0.5D);

                    double weight = luminance / (sx * sx + sz * sz + 1.0D);
                    pushX += sx * weight;
                    pushZ += sz * weight;
                }
            }
        }

        if (!foundLight) {
            return null;
        }

        double length = Math.sqrt(pushX * pushX + pushZ * pushZ);
        if (length < 1.0E-6D) {
            return null;
        }
        return new Vec3d(pushX / length, 0.0D, pushZ / length);
    }
}
