package com.verdantartifice.thaumicwonders.common.compat.groovyscript.handlers;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.enchanter.EssentiaEnchanterRecipe;
import com.verdantartifice.thaumicwonders.common.crafting.enchanter.EssentiaEnchanterRecipeRegistry;
import com.verdantartifice.thaumicwonders.common.init.InitRecipes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.awt.*;
import java.util.Collection;

@RegistryDescription(linkGenerator = ThaumicWonders.MODID)
public class OsmoticEnchanter extends VirtualizedRegistry<EssentiaEnchanterRecipe> {
    @GroovyBlacklist
    @Override
    public void onReload() {
        EssentiaEnchanterRecipeRegistry.removeAll();
        InitRecipes.initEssentiaEnchanterRecipes();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("enchantment('minecraft:smite'), resource('thaumicwonders', 'textures/enchants/smite.png'), aspect('aversio') * 30, aspect('mortuus') * 30")
    )
    public void addRecipe(Enchantment enchantment, ResourceLocation texture, AspectStack... aspectStacks) {
        this.recipeBuilder().enchantment(enchantment).aspect(aspectStacks).texture(texture).register();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<EssentiaEnchanterRecipe> streamRecipes() {
        return new SimpleObjectStream<>(EssentiaEnchanterRecipeRegistry.getRecipes());
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("enchantment('minecraft:smite')"))
    public void removeRecipe(Enchantment enchantment) {
        EssentiaEnchanterRecipeRegistry.removeRecipe(enchantment);
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example(commented = true))
    public void removeAll() {
        EssentiaEnchanterRecipeRegistry.removeAll();
    }

    @RecipeBuilderDescription(example = @Example(".enchantment(enchantment('minecraft:smite')).aspect(aspect('aversio') * 30, aspect('mortuus') * 30).texture(resource('thaumicwonders:textures/enchants/smite.png'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<EssentiaEnchanterRecipe> {
        @Property(comp = @Comp(not = "null"))
        private Enchantment enchantment;
        @Property(comp = @Comp(not = "null"), priority = 1001)
        private AspectList aspectList;
        @Property(comp = @Comp(not = "null"), priority = 1002)
        private ResourceLocation texture;
        @Property(priority = 1003)
        private int color;

        @GroovyBlacklist
        public RecipeBuilder() {
            this.enchantment = null;
            this.aspectList = new AspectList();
            this.texture = new ResourceLocation(ThaumicWonders.MODID, "textures/enchants/unknown.png");
            this.color = Color.WHITE.getRGB();
        }

        @RecipeBuilderMethodDescription(field = "enchantment")
        public RecipeBuilder enchantment(Enchantment enchantment) {
            this.enchantment = enchantment;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aspectList")
        public RecipeBuilder aspect(AspectList aspectList) {
            this.aspectList = aspectList;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aspectList")
        public RecipeBuilder aspect(Collection<AspectStack> aspectStacks) {
            this.aspect(aspectStacks.toArray(new AspectStack[0]));
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aspectList")
        public RecipeBuilder aspect(AspectStack... aspectStacks) {
            for(AspectStack aspectStack : aspectStacks) {
                this.aspect(aspectStack);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aspectList")
        public RecipeBuilder aspect(AspectStack aspectStack) {
            this.aspectList.add(aspectStack.getAspect(), aspectStack.getAmount());
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aspectList")
        public RecipeBuilder aspect(String aspect, int amount) {
            this.aspectList.add(Aspect.getAspect(aspect), amount);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "texture")
        public RecipeBuilder texture(ResourceLocation texture) {
            this.texture = texture;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "color")
        public RecipeBuilder color(int color) {
            this.color = color;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Osmotic Enchanter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(this.enchantment == null, "Enchantment cannot be null");
            msg.add(this.aspectList == null || this.aspectList.size() == 0, "Aspects cannot be null or empty");
            msg.add(this.texture == null, "Texture cannot be null");
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable EssentiaEnchanterRecipe register() {
            if(this.validate()) {
                EssentiaEnchanterRecipe recipe = new EssentiaEnchanterRecipe(this.enchantment, this.aspectList)
                        .setTextureLocation(this.texture)
                        .setColor(this.color);
                EssentiaEnchanterRecipeRegistry.addRecipe(recipe);
                return recipe;
            }
            return null;
        }
    }
}
