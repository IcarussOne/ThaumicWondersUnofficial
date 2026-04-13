package com.verdantartifice.thaumicwonders.common.items.foci;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;

public class FocusEffectBlink extends FocusEffect {
    private Entity caster;

    @Override
    public Aspect getAspect() {
        return Aspect.ELDRITCH;
    }

    @Override
    public String getKey() {
        return "focus." + ThaumicWonders.MODID + ".blink";
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {};
    }

    @Override
    public int getComplexity() {
        return 10;
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return 0;
    }

    @Override
    public String getResearch() {
        //TODO: New research for this
        return "TWOND_FOCUS_TELEPORT_HOME";
    }

    @Override
    public boolean execute(RayTraceResult trace, @Nullable Trajectory trajectory, float power, int num) {
        if (trace == null) {
            this.resetCaster();
            return false;
        }

        PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(trace.hitVec.x, trace.hitVec.y, trace.hitVec.z,
                new String[]{this.getKey()}), new NetworkRegistry.TargetPoint(this.getPackage().world.provider.getDimension(),
                trace.hitVec.x, trace.hitVec.y, trace.hitVec.z, 64.0D));
        this.getPackage().world.playSound(null, trace.hitVec.x, trace.hitVec.y, trace.hitVec.z, SoundsTC.hhon,
                SoundCategory.PLAYERS, 0.8F, 0.85F + (float) (this.getPackage().getCaster().world.rand.nextGaussian() * 0.05F));

        if (trace.typeOfHit == RayTraceResult.Type.ENTITY && trace.entityHit instanceof EntityLivingBase && trace.entityHit.isNonBoss()) {
            //Randomly teleports the hit entity
            for (int i = 0; i < 64; i++) {
                if (this.teleportRandomly((EntityLivingBase) trace.entityHit)) {
                    this.resetCaster();
                    return true;
                }
            }
        } else if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
            //Teleport caster to the hit block
            if (this.smartTeleport(trace)) {
                this.resetCaster();
                return true;
            }
        }

        this.resetCaster();
        return false;
    }

    private boolean teleportTo(EntityLivingBase entity, double x, double y, double z) {
        EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }

        if(entity.isRiding()) {
            entity.dismountRidingEntity();
        }

        boolean flag = entity.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        if (flag)
        {
            entity.world.playSound(null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
            entity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
        }

        return flag;
    }

    private boolean teleportRandomly(EntityLivingBase entity) {
        double xPos = entity.posX + (entity.getRNG().nextDouble() - 0.5D) * 64.0D;
        double yPos = entity.posY + (double)(entity.getRNG().nextInt(64) - 32);
        double zPos = entity.posZ + (entity.getRNG().nextDouble() - 0.5D) * 64.0D;
        return this.teleportTo(entity, xPos, yPos, zPos);
    }

    private boolean smartTeleport(RayTraceResult trace) {
        if(this.caster instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) this.caster;
            BlockPos tpPos = this.getActualTeleportPos(entity, trace);
            return tpPos != null && this.teleportTo(entity, tpPos.getX() + 0.5, tpPos.getY(), tpPos.getZ() + 0.5);
        }
        return false;
    }

    private BlockPos getActualTeleportPos(EntityLivingBase entity, RayTraceResult trace) {
        World world = entity.world;
        BlockPos tpPos = trace.getBlockPos().up();
        if(trace.sideHit != EnumFacing.UP) {
            //Moving one block higher if it is not a solid block.
            if(world.getBlockState(tpPos).getMaterial().blocksMovement() && !world.getBlockState(tpPos).isFullBlock()) {
                tpPos = tpPos.up();
            }
            //Moves in front of the block if it cannot put the entity on top of the block.
            if(!this.isPositionValid(entity, world, tpPos)) {
                tpPos = trace.getBlockPos().offset(trace.sideHit);
            }
        }
        return this.isPositionValid(entity, world, tpPos) ? tpPos : null;
    }

    private boolean isPositionValid(EntityLivingBase entity, World world, BlockPos pos) {
        return world.isBlockLoaded(pos) && !world.getBlockState(pos).getMaterial().blocksMovement() && world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(entity.getEntityBoundingBox());
    }

    private void setCaster(@Nullable Entity caster) {
        this.caster = caster;
    }

    private void resetCaster() {
        this.caster = null;
    }

    @Override
    public void onCast(Entity caster) {
        this.setCaster(caster);
        caster.world.playSound(null, caster.getPosition().up(), SoundsTC.hhoff, SoundCategory.PLAYERS,
                0.8F, 0.45F + (float) (caster.world.rand.nextGaussian() * 0.05F));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderParticleFX(World world, double posX, double posY, double posZ, double velX, double velY, double velZ) {
        FXGeneric fb = new FXGeneric(world, posX, posY, posZ, velX, velY, velZ);
        int color = 0xB4D42;

        fb.setMaxAge(16 + world.rand.nextInt(16));
        fb.setParticles(384 + world.rand.nextInt(16), 1, 1);
        fb.setSlowDown(0.75F);
        fb.setAlphaF(1.0F, 0.0F);
        fb.setScale((float) (0.7F + world.rand.nextGaussian() * 0.3F));
        fb.setRandomMovementScale(0.01F, 0.01F, 0.01F);
        ParticleEngine.addEffectWithDelay(world, fb, 0);

        FXGeneric fb2 = new FXGeneric(world, posX, posY, posZ, velX + world.rand.nextGaussian() * 0.01D, velY + world.rand.nextGaussian() * 0.01D, velZ + world.rand.nextGaussian() * 0.01D);
        fb2.setMaxAge((int) (15.0F + 10.0F * world.rand.nextFloat()));
        fb2.setRBGColorF(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
        fb2.setAlphaF(0.0F, 1.0F, 1.0F, 0.0F);
        fb2.setGridSize(64);
        fb2.setParticles(128, 14, 1);
        fb2.setScale(4.0F + world.rand.nextFloat(), 0.25F + world.rand.nextFloat() * 0.25F);
        fb2.setLoop(true);
        fb2.setSlowDown(0.9);
        fb2.setGravity((float) (world.rand.nextGaussian() * 0.1D));
        fb2.setRandomMovementScale(0.0125F, 0.0125F, 0.0125F);
        fb2.setRotationSpeed((float) world.rand.nextGaussian());
        ParticleEngine.addEffectWithDelay(world, fb2, world.rand.nextInt(4));
    }
}
