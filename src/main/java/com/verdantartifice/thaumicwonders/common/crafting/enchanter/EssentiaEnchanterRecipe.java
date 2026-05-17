package com.verdantartifice.thaumicwonders.common.crafting.enchanter;

import com.google.common.base.Preconditions;
import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.awt.*;
import java.util.Objects;

public class EssentiaEnchanterRecipe {
    public static final ResourceLocation FALLBACK_ICON = new ResourceLocation(ThaumicWonders.MODID, "textures/enchants/unknown.png");

    protected final Enchantment enchantment;
    protected final AspectList aspectList;
    protected ResourceLocation textureLocation;
    protected int texX;
    protected int texY;
    protected int size;
    protected float scale;
    protected int color;

    public EssentiaEnchanterRecipe(Enchantment enchantment, AspectList aspectList) {
        Preconditions.checkArgument(enchantment != null, "Enchantment cannot be null");
        Preconditions.checkArgument(aspectList != null && aspectList.size() > 0, "AspectList cannot be null or empty");
        this.enchantment = enchantment;
        this.aspectList = aspectList;
        this.setTextureLocation(FALLBACK_ICON, 32);
        this.setColor(Color.WHITE.getRGB());
    }

    public ResourceLocation getTextureLocation() {
        return this.textureLocation;
    }

    public EssentiaEnchanterRecipe setTextureLocation(ResourceLocation location, int textureX, int textureY, int size) {
        this.textureLocation = location;
        this.texX = textureX;
        this.texY = textureY;
        this.size = size;
        this.scale = 16.0f / (float) size;
        return this;
    }

    public EssentiaEnchanterRecipe setTextureLocation(ResourceLocation location, int size) {
        return this.setTextureLocation(location, 0, 0, size);
    }

    public EssentiaEnchanterRecipe setTextureLocation(ResourceLocation location) {
        return this.setTextureLocation(location, 16);
    }

    public int getTexX() {
        return this.texX;
    }

    public int getTexY() {
        return this.texY;
    }

    public float getScale() {
        return this.scale;
    }

    public int getColor() {
        return this.color;
    }

    public EssentiaEnchanterRecipe setColor(int color) {
        this.color = color;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public void drawIcon(Minecraft mc, int posX, int posY) {
        mc.getTextureManager().bindTexture(this.textureLocation);
        GlStateManager.enableBlend();
        Color color = new Color(this.color);
        Gui.drawModalRectWithCustomSizedTexture(posX, posY, this.texX, this.texY, 16, 16, this.size, this.size);
        GlStateManager.disableBlend();
        mc.getTextureManager().bindTexture(Gui.ICONS);
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public int getMaxEnchantmentLevel() {
        return enchantment.getMaxLevel();
    }

    public AspectList getEnchantAspects() {
        return this.aspectList.copy();
    }

    public AspectList getEnchantAspects(int level) {
        AspectList aspects = new AspectList();
        for(Aspect aspect : this.aspectList.getAspects()) {
            aspects.add(aspect, this.aspectList.getAmount(aspect) * level);
        }
        return aspects;
    }

    public boolean canApplyTo(ItemStack stack) {
        return this.enchantment.canApply(stack);
    }

    public boolean canApplyWith(Enchantment enchantment) {
        return this.enchantment.isCompatibleWith(enchantment);
    }

    public ItemStack applyEnchant(ItemStack stack, int level) {
        stack.addEnchantment(this.enchantment, level);
        return stack;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass())
            return false;
        EssentiaEnchanterRecipe that = (EssentiaEnchanterRecipe) object;
        return Objects.equals(getEnchantment(), that.getEnchantment());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getEnchantment().getRegistryName());
    }
}
