package com.verdantartifice.thaumicwonders.common.blocks.misc;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockTileTW;
import com.verdantartifice.thaumicwonders.common.tiles.misc.TileArcanePillar;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thaumcraft.api.blocks.BlocksTC;

public class BlockArcanePillar extends BlockTileTW<TileArcanePillar> {
    public static final PropertyBool TOP = PropertyBool.create("top");
    public static final PropertyEnum<EnumDirection> DIRECTION = PropertyEnum.create("direction", EnumDirection.class);
    public static final AxisAlignedBB[] PILLAR_AABBS = new AxisAlignedBB[] {
            new AxisAlignedBB(0.125, 0, 0.125, 0.875, 1.0, 0.875),
            new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.875, 0.875)
    };

    public BlockArcanePillar() {
        super(Material.ROCK, TileArcanePillar.class, "arcane_pillar");
        this.setHardness(2.5f);
        this.setResistance(2.0f);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TOP, false).withProperty(DIRECTION, EnumDirection.NORTH));
    }

    @SuppressWarnings("ConstantConditions")
    public static void revertPillar(World world, BlockPos bottomPos) {
        if(!world.isRemote) {
            BlockPos topPos = bottomPos.up();

            if (world.getBlockState(topPos).getBlock() == BlocksTW.ARCANE_PILLAR) {
                world.setBlockState(topPos, BlocksTC.stoneArcane.getDefaultState());
                world.playEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, topPos, Block.getStateId(BlocksTC.stoneArcane.getDefaultState()));
            }
            if (world.getBlockState(bottomPos).getBlock() == BlocksTW.ARCANE_PILLAR) {
                world.setBlockState(bottomPos, BlocksTC.stoneArcane.getDefaultState());
                world.playEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, topPos, Block.getStateId(BlocksTC.stoneArcane.getDefaultState()));
            }
        }
    }

    public static int getStackMeta(boolean isTop, EnumDirection direction) {
        return (isTop ? 8 : 0) + direction.ordinal();
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return PILLAR_AABBS[state.getValue(TOP) ? 1 : 0];
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(BlocksTC.stoneArcane));
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(!worldIn.isRemote) {
            if (state.getValue(TOP)) {
                IBlockState down = worldIn.getBlockState(pos.down());
                if(down.getBlock() == this) {
                    worldIn.setBlockState(pos.down(), BlocksTC.stoneArcane.getDefaultState());
                }
            } else {
                IBlockState up = worldIn.getBlockState(pos.up());
                if(up.getBlock() == this) {
                    worldIn.setBlockState(pos.up(), BlocksTC.stoneArcane.getDefaultState());
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        BlockPos posUp = pos.up();
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)
                && worldIn.getBlockState(posUp).getBlock().isReplaceable(worldIn, posUp);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(TOP, true).withProperty(DIRECTION, state.getValue(DIRECTION)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.IGNORE;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState();
    }

    @SuppressWarnings("deprecation")
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return state.getValue(TOP) ? EnumBlockRenderType.INVISIBLE : super.getRenderType(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return !state.getValue(TOP) && face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return !world.getBlockState(pos).getValue(TOP) && side == EnumFacing.DOWN;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TOP, meta / 8 == 1).withProperty(DIRECTION, EnumDirection.values()[meta % 8]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(TOP) ? 8 : 0) + (state.getValue(DIRECTION).ordinal());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TOP, DIRECTION);
    }

    public enum EnumDirection implements IStringSerializable {
        NORTH(new BlockPos(0, 0, -1)),
        NORTH_EAST(new BlockPos(1, 0, -1)),
        EAST(new BlockPos(1, 0, 0)),
        SOUTH_EAST(new BlockPos(1, 0, 1)),
        SOUTH(new BlockPos(0, 0, 1)),
        SOUTH_WEST(new BlockPos(-1, 0, 1)),
        WEST(new BlockPos(-1, 0, 0)),
        NORTH_WEST(new BlockPos(-1, 0, -1));

        public static final EnumDirection[] VALUES;
        private final Vec3d nitorOffset;

        EnumDirection(BlockPos nitorOffset) {
            this.nitorOffset = new Vec3d(0.5f + (float) nitorOffset.getX() * 0.3f, 2.15f, 0.5f + (float) nitorOffset.getZ() * 0.3f);
        }

        public Vec3d getNitorOffset() {
            return this.nitorOffset;
        }

        public Vec3d getNitorOffset(BlockPos pos) {
            return new Vec3d(pos.getX() + this.nitorOffset.x, pos.getY() + this.nitorOffset.y, pos.getZ() + this.nitorOffset.z);
        }

        public EnumDirection next() {
            return EnumDirection.values()[(this.ordinal() + 1) % EnumDirection.values().length];
        }

        @Override
        public String getName() {
            return this.toString().toLowerCase();
        }

        static {
            EnumDirection[] directions = EnumDirection.values();
            VALUES = new EnumDirection[directions.length];
            System.arraycopy(directions, 0, VALUES, 0, directions.length);
        }
    }
}
