package com.verdantartifice.thaumicwonders.client.renderers.tile;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.misc.BlockArcanePillar;
import com.verdantartifice.thaumicwonders.common.tiles.misc.TileArcanePillar;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.client.fx.FXDispatcher;

public class TesrArcanePillar extends TileEntitySpecialRenderer<TileArcanePillar> {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void render(TileArcanePillar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        World world = te.getWorld();
        BlockPos pos = te.getPos();
        if(world == null || pos == null) {
            return;
        }

        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() == BlocksTW.ARCANE_PILLAR && !state.getValue(BlockArcanePillar.TOP)) {
            BlockArcanePillar.EnumDirection direction = state.getValue(BlockArcanePillar.DIRECTION);
            Vec3d nitorOffset = direction.getNitorOffset(pos);
            FXDispatcher.INSTANCE.drawNitorFlames(
                     nitorOffset.x + world.rand.nextGaussian() * 0.025D,
                     nitorOffset.y + world.rand.nextGaussian() * 0.025D,
                     nitorOffset.z + world.rand.nextGaussian() * 0.025D,
                    world.rand.nextGaussian() * 0.0025D,
                    (double) world.rand.nextFloat() * 0.06D,
                    world.rand.nextGaussian() * 0.0025D, MapColor.GOLD.colorValue,
                    0
            );
        }
    }
}
