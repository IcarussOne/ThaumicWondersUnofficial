package com.verdantartifice.thaumicwonders.common.compat.crafttweaker.handlers;

import com.blamejared.compat.thaumcraft.handlers.aspects.CTAspectStack;
import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.enchanter.EssentiaEnchanterRecipe;
import com.verdantartifice.thaumicwonders.common.crafting.enchanter.EssentiaEnchanterRecipeRegistry;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import thaumcraft.api.aspects.AspectList;

@ZenRegister
@ZenClass("mods." + ThaumicWonders.MODID + ".OsmoticEnchanter")
public class CTEssentiaEnchanter {
    @net.minecraftforge.fml.common.Optional.Method(modid = "modtweaker")
    @ZenMethod
    public static void addRecipe(IEnchantmentDefinition enchantmentDefinition, CTAspectStack[] aspectStacks, @Optional String textureLocation) {
        Enchantment enchantment = (Enchantment) enchantmentDefinition.getInternal();
        AspectList aspectList = new AspectList();
        for(CTAspectStack aspectStack : aspectStacks) {
            aspectList.add(aspectStack.getInternal().getInternal(), aspectStack.getAmount());
        }
        EssentiaEnchanterRecipe recipe = new EssentiaEnchanterRecipe(enchantment, aspectList);
        if(textureLocation != null) {
            recipe.setTextureLocation(new ResourceLocation(textureLocation), 32);
        }
        EssentiaEnchanterRecipeRegistry.addRecipe(recipe);
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = "modtweaker")
    @ZenMethod
    public static void removeRecipe(IEnchantmentDefinition enchantmentDefinition) {
        Enchantment enchantment = (Enchantment) enchantmentDefinition.getInternal();
        EssentiaEnchanterRecipeRegistry.removeRecipe(enchantment);
    }

    @ZenMethod
    public static void removeAll() {
        EssentiaEnchanterRecipeRegistry.removeAll();
    }
}
