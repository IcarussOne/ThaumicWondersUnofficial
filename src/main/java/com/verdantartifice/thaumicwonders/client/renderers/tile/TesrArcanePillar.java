package com.verdantartifice.thaumicwonders.client.renderers.tile;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.misc.BlockArcanePillar;
import com.verdantartifice.thaumicwonders.common.tiles.misc.TileArcanePillar;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.client.fx.FXDispatcher;

public class TesrArcanePillar extends TileEntitySpecialRenderer<TileArcanePillar> {

    @Override
    public void render(TileArcanePillar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        World world = te.getWorld();
        BlockPos pos = te.getPos();
        IBlockState state = world.getBlockState(pos);

        if(state.getBlock() == BlocksTW.ARCANE_PILLAR && state.getValue(BlockArcanePillar.TOP)) {
            BlockArcanePillar.EnumDirection direction = state.getValue(BlockArcanePillar.DIRECTION);
            float xOffset = direction.getNitorOffsetX();
            float zOffset = direction.getNitorOffsetZ();
            FXDispatcher.INSTANCE.drawNitorFlames(
                    (double) ((float) pos.getX() + 0.5F + xOffset) + world.rand.nextGaussian() * 0.025D,
                    (double) ((float) pos.getY() + 1.15F) + world.rand.nextGaussian() * 0.025D,
                    (double) ((float) pos.getZ() + 0.5F + zOffset) + world.rand.nextGaussian() * 0.025D,
                    world.rand.nextGaussian() * 0.0025D,
                    (double) world.rand.nextFloat() * 0.06D,
                    world.rand.nextGaussian() * 0.0025D, MapColor.GOLD.colorValue,
                    0
            );
        }
    }
}
