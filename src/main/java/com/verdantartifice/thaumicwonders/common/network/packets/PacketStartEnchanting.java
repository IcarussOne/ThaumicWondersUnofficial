package com.verdantartifice.thaumicwonders.common.network.packets;

import com.verdantartifice.thaumicwonders.common.tiles.devices.TileEssentiaEnchanter;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.aspects.AspectList;

public class PacketStartEnchanting implements IMessage {
    private BlockPos tilePos;
    private ItemStack stack;
    private AspectList aspectList;

    public PacketStartEnchanting(BlockPos tilePos, ItemStack stack, AspectList aspectList) {
        this.tilePos = tilePos;
        this.stack = stack;
        this.aspectList = aspectList;
    }

    public PacketStartEnchanting() {
        this(new BlockPos(0, 0, 0), ItemStack.EMPTY, new AspectList());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.tilePos = BlockPos.fromLong(buf.readLong());
        this.stack = ByteBufUtils.readItemStack(buf);
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        if(this.aspectList == null)
            this.aspectList = new AspectList();
        this.aspectList.readFromNBT(tag);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        this.aspectList.writeToNBT(tag);
        buf.writeLong(this.tilePos.toLong());
        ByteBufUtils.writeItemStack(buf, this.stack);
        ByteBufUtils.writeTag(buf, tag);
    }

    public static class Handler implements IMessageHandler<PacketStartEnchanting, IMessage> {
        @Override
        public IMessage onMessage(PacketStartEnchanting message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.world;
                TileEntity tile = world.getTileEntity(message.tilePos);
                if(tile instanceof TileEssentiaEnchanter) {
                    ((TileEssentiaEnchanter) tile).startCrafting(message.stack, message.aspectList);
                }
            });
            return null;
        }
    }
}
