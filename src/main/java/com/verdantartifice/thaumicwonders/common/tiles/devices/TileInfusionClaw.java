package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import com.verdantartifice.thaumicwonders.common.utils.PlayerHelper;
import com.verdantartifice.thaumicwonders.common.utils.StringHelper;
import com.verdantartifice.thaumicwonders.core.mixins.TileInfusionMatrixMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.common.tiles.crafting.TilePedestal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

public class TileInfusionClaw extends TileTW implements ITickable, IInteractWithCaster {
    private static final int ACTIVATION_EFFECT = 4;
    private static Field instabilityField;

    public InfusionRecipe cachedRecipe;
    public int countdown;
    public int count;
    protected UUID playerID;
    private EntityPlayer boundPlayer;
    private int cooldown;

    @SideOnly(Side.CLIENT)
    public float lastRenderTick = Minecraft.getMinecraft().player != null ? Minecraft.getMinecraft().player.ticksExisted : 0;
    /**
     * 0-3: heightMov sides<br>
     * 4-7: widthMov sides<br>
     * 8:   sides exp. speed<br>
     * 9:   center exp. speed<br>
     * 10:  rotation center<br>
     * 11:  primal orb offset
     */
    @SideOnly(Side.CLIENT)
    public float[] animationStates = new float[12];

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.playerID = UUID.fromString(compound.getString("playerId"));
        this.countdown = compound.getInteger("countdown");
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setString("playerId", this.playerID.toString());
        compound.setInteger("countdown", this.countdown);
        return compound;
    }

    @Override
    public void update() {
        this.count++;
        boolean did = false;
        boolean isPowered = this.world.isBlockPowered(this.pos);

        if(!this.world.isRemote && !isPowered) {
            TileEntity tile = this.world.getTileEntity(this.pos.offset(EnumFacing.DOWN));
            if(tile instanceof TileInfusionMatrix && ((TileInfusionMatrix) tile).active) {
                TileInfusionMatrix matrix = (TileInfusionMatrix) tile;
                if(this.isRunning()) {
                    this.cooldown--;
                    if(this.cooldown == (int) (7.5 * 20)) {
                        this.activateCraft(matrix);
                        did = true;
                    }
                } else if(!this.isMatrixCrafting(matrix)) {
                    if(this.checkAndUpdateRecipe(matrix)) {
                        this.resetCountdown();
                        did = true;
                    } else {
                        did |= this.runRecipeActivation(matrix);
                    }
                }
            }
        }

        if(did) {
            this.markDirty();
            this.syncTile(false);
        }
    }

    private void activateCraft(TileInfusionMatrix matrix) {
        EntityPlayer player = this.getBoundPlayer();
        if(player != null) {
            matrix.craftingStart(player);
            this.increaseRecipeInstability(matrix, 2);
        }
        this.cachedRecipe = null;
        this.resetCountdown();
    }

    /** Returns true if the recipe has changed, false otherwise. */
    public boolean checkAndUpdateRecipe(TileInfusionMatrix matrix) {
        if(this.countdown == 0 || this.count % 60 == 0) {
            EntityPlayer player = this.getBoundPlayer();
            if (player != null) {
                ItemStack inputStack = this.getRecipeInput();
                //Empty input is a invalid recipe.
                if(inputStack.isEmpty()) {
                    if(this.cachedRecipe != null) {
                        this.cachedRecipe = null;
                        return true;
                    }
                    return false;
                }

                ArrayList<ItemStack> components = new ArrayList<>(this.getRecipeComponents());
                InfusionRecipe recipe = ThaumcraftCraftingManager.findMatchingInfusionRecipe(components, inputStack, player);
                if (recipe != null && !recipe.equals(this.cachedRecipe)) {
                    this.cachedRecipe = recipe;
                    return true;
                } else if (recipe == null && this.cachedRecipe != null) {
                    this.cachedRecipe = null;
                    return true;
                }
            }
        }
        return false;
    }


    private boolean runRecipeActivation(TileInfusionMatrix matrix) {
        if(this.cachedRecipe != null) {
            if(this.countdown > 0) {
                this.countdown--;
                return true;
            } else {
                this.startRunning();
            }
        }
        return false;
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if(id == ACTIVATION_EFFECT) {
            if(this.world.isRemote) {
                this.animationStates[8] = 1.0f;
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    @Override
    public boolean onCasterRightClick(World world, ItemStack itemStack, EntityPlayer entityPlayer, BlockPos blockPos, EnumFacing enumFacing, EnumHand enumHand) {
        if (entityPlayer.isSneaking() || this.getBoundPlayer() == null) {
            if(!world.isRemote) {
                this.setBoundPlayer(entityPlayer);
                entityPlayer.sendMessage(new TextComponentTranslation(StringHelper.getTranslationKey("bind_player", "chat"), entityPlayer.getDisplayName()));
            }
            return true;
        }
        return false;
    }

    //##########################################################
    // Utility Methods

    public boolean isMatrixCrafting(TileInfusionMatrix matrix) {
        return matrix.active && matrix.crafting;
    }

    public boolean isRunning() {
        if(this.world.isRemote) {
            return this.animationStates[8] + this.animationStates[9] + this.animationStates[11] != 0;
        }
        return this.cooldown > 0;
    }

    @SuppressWarnings("ConstantConditions")
    private void startRunning() {
        this.world.addBlockEvent(this.pos, BlocksTW.INFUSION_CLAW, ACTIVATION_EFFECT, 0);
        this.cooldown = 22 * 20;
    }

    @Nullable
    public EntityPlayer getBoundPlayer() {
        if(this.boundPlayer == null) {
            this.boundPlayer = PlayerHelper.getPlayerFromUUID(this.playerID);
        }
        return this.boundPlayer;
    }

    public void setBoundPlayer(EntityPlayer player) {
        this.boundPlayer = player;
        this.playerID = PlayerHelper.getUUIDFromPlayer(player);
        this.markDirty();
        this.syncTile(false);
    }

    public void resetCountdown() {
        this.countdown = 60;
        this.markDirty();
        this.syncTile(false);
    }

    protected ItemStack getRecipeInput() {
        TileEntity tile = this.world.getTileEntity(this.pos.offset(EnumFacing.DOWN, 3));
        return tile instanceof TilePedestal ? ((TilePedestal) tile).getStackInSlot(0) : ItemStack.EMPTY;
    }

    protected NonNullList<ItemStack> getRecipeComponents() {
        NonNullList<ItemStack> components = NonNullList.create();
        BlockPos matrixPos = this.pos.offset(EnumFacing.DOWN);
        for(int xx = -8; xx <= 8; ++xx) {
            for(int zz = -8; zz <= 8; ++zz) {
                for(int yy = -3; yy <= 7; ++yy) {
                    if (xx == 0 && zz == 0)
                        continue;

                    BlockPos checkPos = matrixPos.add(xx, -yy, zz);
                    TileEntity tile = this.world.getTileEntity(checkPos);
                    if (tile instanceof TilePedestal) {
                        ItemStack stack = ((TilePedestal) tile).getStackInSlot(0);
                        if(!stack.isEmpty()) {
                            components.add(stack);
                        }
                    }
                }
            }
        }
        return components;
    }

    protected void increaseRecipeInstability(TileInfusionMatrix matrix, int increaseAmount) {
        try {
            int instability = ((TileInfusionMatrixMixin) matrix).getRecipeInstability();
            ((TileInfusionMatrixMixin) matrix).setRecipeInstability(instability + increaseAmount);
        } catch (Exception e) {
            ThaumicWonders.LOGGER.error("Failed to access TileInfusionMatrix#recipeInstability", e);
        }
    }
}
