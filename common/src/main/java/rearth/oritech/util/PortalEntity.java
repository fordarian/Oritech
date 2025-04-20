package rearth.oritech.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rearth.oritech.Oritech;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PortalEntity extends Entity implements GeoEntity {
    
    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    
    private int age = 0;
    
    public Vec3d target;
    public RegistryKey<World> targetWorld;

    protected static final RawAnimation PORTAL = RawAnimation.begin().thenPlay("create").thenLoop("idle");
    
    
    public PortalEntity(EntityType<?> type, World world) {
        super(type, world);
        
    }
    
    @Override
    public boolean isCollidable() {
        return true;
    }
    
    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!this.getWorld().isClient) {
            if (target != null && targetWorld != null) {
                Oritech.LOGGER.info("Player {} will go to {} in {}",
                    player.getName().getString(), target, targetWorld.getValue());
    
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    ServerWorld destWorld = serverPlayer.getServer().getWorld(targetWorld);
                    if (destWorld != null) {
                        serverPlayer.teleport(
                            destWorld,
                            target.x, target.y, target.z,
                            serverPlayer.getYaw(), serverPlayer.getPitch()
                        );
    
                        Oritech.LOGGER.info("Teleported {} to {} in {}",
                            serverPlayer.getName().getString(), target, targetWorld.getValue());
                    } else {
                        Oritech.LOGGER.warn("Target dimension {} not found!",
                            targetWorld.getValue());
                    }
                } else {
                    Oritech.LOGGER.warn("Player {} is not a ServerPlayerEntity",
                        player.getName().getString());
                }
            } else {
                Oritech.LOGGER.info("Player {} has no teleport target or target dimension!",
                    player.getName().getString());
            }
    
            this.remove(RemovalReason.DISCARDED);
        }
    }
    
    @Override
    public void tick() {
        var world = this.getWorld();
        if (world.isClient) return;
        
        age++;
        
        if (age > 100) {
            this.remove(RemovalReason.DISCARDED);
        }
    }
    
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    
    }
    
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    
    }
    
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, state -> state.setAndContinue(PORTAL)));
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }
}
