package com.verdantartifice.thaumicwonders.common.crafting.enchanter;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EssentiaEnchanterRecipeRegistry {
    private static final Set<EssentiaEnchanterRecipe> ENCHANTER_RECIPES = new HashSet<>();

    public static Set<EssentiaEnchanterRecipe> getRecipes() {
        return ENCHANTER_RECIPES;
    }

    public static void addRecipe(EssentiaEnchanterRecipe recipe) {
        if(recipe != null) {
            ENCHANTER_RECIPES.add(recipe);
        }
    }

    public static void addRecipe(Enchantment enchantment, AspectList aspectList) {
        addRecipe(new EssentiaEnchanterRecipe(enchantment, aspectList));
    }

    public static void removeRecipe(Enchantment enchantment) {
        ENCHANTER_RECIPES.removeIf(recipe -> recipe.enchantment == enchantment);
    }

    public static void removeAll() {
        ENCHANTER_RECIPES.clear();
    }

    public static List<EssentiaEnchanterRecipe> getValidEnchantments(ItemStack stack, EssentiaEnchanterRecipe... currentRecipes) {
        List<EssentiaEnchanterRecipe> recipes = new ArrayList<>();
        if(stack.isItemEnchantable()) {
            recipes.addAll(ENCHANTER_RECIPES.stream().filter(recipe -> recipe.canApplyTo(stack)).collect(Collectors.toList()));
            for(EssentiaEnchanterRecipe currentRecipe : currentRecipes) {
                recipes.removeIf(recipe -> recipe != currentRecipe && !recipe.canApplyWith(currentRecipe.enchantment));
            }
        }
        return recipes;
    }
}
