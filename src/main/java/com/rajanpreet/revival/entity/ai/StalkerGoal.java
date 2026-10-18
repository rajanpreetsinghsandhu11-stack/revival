package com.rajanpreet.revival.entity.ai;

import com.rajanpreet.revival.entity.TheRevivedEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class StalkerGoal extends Goal {

    private static final double COS_HALF_FOV = Math.cos(Math.toRadians(35.0D));
    private static final double COS_HALF_FOV_SQ = COS_HALF_FOV * COS_HALF_FOV;

    private static final double MAX_GAZE_DISTANCE_SQ = 48.0D * 48.0D;
    private static final double MELEE_HANDOFF_DISTANCE_SQ = 3.0D * 3.0D;
    private static final double RETREAT_DISTANCE_SQ = 5.0D * 5.0D;
    private static final double RETREAT_STEP = 8.0D;
    private static final int REPATH_INTERVAL = 10;

    private final TheRevivedEntity mob;
    private final double speed;
    private int repathTimer;

    public StalkerGoal(TheRevivedEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        PlayerEntity player = getStalkTarget();
        if (player == null) {
            return false;
        }
        return isWatchedBy(player) || mob.squaredDistanceTo(player) > MELEE_HANDOFF_DISTANCE_SQ;
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }

    @Override
    public void start() {
        this.repathTimer = 0;
    }

    @Override
    public void tick() {
        PlayerEntity player = getStalkTarget();
        if (player == null) {
            return;
        }

        mob.getLookControl().lookAt(player, 30.0F, 30.0F);

        if (isWatchedBy(player)) {
            if (mob.squaredDistanceTo(player) < RETREAT_DISTANCE_SQ) {
                if (--repathTimer <= 0) {
                    repathTimer = REPATH_INTERVAL;
                    retreatFrom(player);
                }
            } else {
                freeze();
            }
        } else {
            if (--repathTimer <= 0) {
                repathTimer = REPATH_INTERVAL;
                mob.getNavigation().startMovingTo(player, speed);
            }
        }
    }

    private void freeze() {
        mob.getNavigation().stop();
        Vec3d velocity = mob.getVelocity();
        mob.setVelocity(0.0D, velocity.y, 0.0D);
    }

    private void retreatFrom(PlayerEntity player) {
        Vec3d away = mob.getPos().subtract(player.getPos());
        Vec3d horizontal = new Vec3d(away.x, 0.0D, away.z).normalize();
        if (horizontal.lengthSquared() < 1.0E-6D) {
            return;
        }
        Vec3d destination = mob.getPos().add(horizontal.multiply(RETREAT_STEP));
        mob.getNavigation().startMovingTo(destination.x, destination.y, destination.z, speed * 1.15D);
    }

    private boolean isWatchedBy(PlayerEntity player) {
        Vec3d toMob = mob.getEyePos().subtract(player.getEyePos());
        double distSq = toMob.lengthSquared();
        if (distSq > MAX_GAZE_DISTANCE_SQ) {
            return false;
        }

        Vec3d look = player.getRotationVec(1.0F);
        double dot = look.dotProduct(toMob);
        if (dot <= 0.0D) {
            return false;
        }
        return dot * dot >= COS_HALF_FOV_SQ * distSq;
    }

    private PlayerEntity getStalkTarget() {
        return mob.getTarget() instanceof PlayerEntity player && player.isAlive() && !player.isSpectator()
                ? player
                : null;
    }
}
