package com.verdantartifice.thaumicwonders.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;

@Mixin(value = TileInfusionMatrix.class, remap = false)
public interface TileInfusionMatrixMixin {
    @Accessor(value = "recipeInstability")
    int getRecipeInstability();

    @Accessor(value = "recipeInstability")
    void setRecipeInstability(int amount);
}
