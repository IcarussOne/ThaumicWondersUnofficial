package com.verdantartifice.thaumicwonders.proxy;

import com.verdantartifice.thaumicwonders.client.renderers.tile.*;
import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.tiles.devices.*;
import com.verdantartifice.thaumicwonders.common.tiles.misc.TileArcanePillar;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ProxyTESR {
    @SuppressWarnings("ConstantConditions")
    public void setupTESR() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileArcanePillar.class, new TesrArcanePillar());
        ClientRegistry.bindTileEntitySpecialRenderer(TileDimensionalRipper.class, new TesrDimensionalRipper());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEssentiaEnchanter.class, new TesrEssentiaEnchanter());
        ClientRegistry.bindTileEntitySpecialRenderer(TileInfusionClaw.class, new TesrInfusionClaw());
        ClientRegistry.bindTileEntitySpecialRenderer(TilePrimordialSiphon.class, new TesrPrimordialSiphon());
        ClientRegistry.bindTileEntitySpecialRenderer(TileVoidBeacon.class, new TesrVoidBeacon());

        this.setTileEntityItemTESR(BlocksTW.INFUSION_CLAW, TileInfusionClaw.class);
    }

    private <T extends TileEntity> void setTileEntityItemTESR(Block block, Class<T> tileClazz) {
        Item item = Item.getItemFromBlock(block);
        if(item != Items.AIR) {
            item.setTileEntityItemStackRenderer(new TileEntityItemStackRenderer() {
                @Override
                public void renderByItem(ItemStack itemStackIn) {
                    TileEntitySpecialRenderer<T> renderer = TileEntityRendererDispatcher.instance.getRenderer(tileClazz);
                    if(itemStackIn.getItem() == item && renderer != null) {
                        try {
                            renderer.render(tileClazz.newInstance(), 0, 0, 0, 0, -1, 0);
                        } catch (Exception ignored) {}
                    }
                }
            });
        }
    }
}
