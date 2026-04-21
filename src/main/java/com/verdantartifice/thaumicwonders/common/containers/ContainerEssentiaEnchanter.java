package com.verdantartifice.thaumicwonders.common.containers;

import com.verdantartifice.thaumicwonders.common.tiles.devices.TileEssentiaEnchanter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerEssentiaEnchanter extends Container {
    private final TileEssentiaEnchanter enchanter;

    public ContainerEssentiaEnchanter(InventoryPlayer inventoryPlayer, TileEssentiaEnchanter enchanter) {
        this.enchanter = enchanter;
        this.bindTileInventory();
        this.bindPlayerInventory(inventoryPlayer);
    }

    private void bindTileInventory() {
        this.addSlotToContainer(new SlotItemHandler(this.enchanter.stackHandler, 0, 8, 27));
    }

    private void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 122 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 180));
        }
    }

    public TileEssentiaEnchanter getEnchanter() {
        return this.enchanter;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if(index != 0) {
                if(slotStack.isItemEnchantable()) {
                    //Enchant Slot
                    if (!this.mergeItemStack(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if(index < 28) {
                    //Main Inventory
                    if (!this.mergeItemStack(slotStack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37 && !this.mergeItemStack(slotStack, 1, 28, false)) {
                    //Hotbar
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(slotStack, 1, 37, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
        }

        return stack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }

    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
    }
}
