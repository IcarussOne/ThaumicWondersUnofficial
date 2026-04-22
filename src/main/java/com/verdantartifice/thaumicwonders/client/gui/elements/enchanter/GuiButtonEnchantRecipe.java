package com.verdantartifice.thaumicwonders.client.gui.elements.enchanter;

import com.verdantartifice.thaumicwonders.client.gui.GuiEssentiaEnchanter;
import com.verdantartifice.thaumicwonders.common.compat.ModIds;
import com.verdantartifice.thaumicwonders.common.crafting.enchanter.EssentiaEnchanterRecipe;
import com.verdantartifice.thaumicwonders.common.utils.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiButtonEnchantRecipe extends AbstractButtonEnchanter {
    private EssentiaEnchanterRecipe buttonRecipe;
    private int enchantLevel;

    public GuiButtonEnchantRecipe(GuiEssentiaEnchanter guiEnchanter, int buttonId, int x, int y) {
        super(guiEnchanter, buttonId, x, y, 0, 0, 16, 16, "");
    }

    @Override
    public void updateEnabled() {
        ItemStack stack = this.guiEnchanter.getContainer().getEnchanter().getItemToEnchant();
        if(stack.isEmpty() || (this.buttonRecipe != null && !this.buttonRecipe.canApplyTo(stack))) {
            this.buttonRecipe = null;
        }
        this.enabled = this.buttonRecipe != null;
        this.visible = this.buttonRecipe != null;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        this.updateEnabled();
        if(this.visible && this.buttonRecipe != null) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.pushMatrix();
            mc.getTextureManager().bindTexture(this.buttonRecipe.getTextureLocation());
            GlStateManager.enableBlend();
            //GlStateManager.translate(this.guiEnchanter.getGuiLeft(), this.guiEnchanter.getGuiTop(), 0);
            Color color = new Color(this.buttonRecipe.getColor());
            GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, this.buttonRecipe.getTexX(), this.buttonRecipe.getTexY(), 16, 16, 16, 16);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mouseDragged(mc, mouseX, mouseY);

            if(this.enchantLevel > 0) {
                FontRenderer fontrenderer = mc.fontRenderer;
                String numeral = StringHelper.toRomanNumeral(this.enchantLevel);
                this.drawCenteredString(fontrenderer, numeral, this.x + this.width / 2, this.y + (this.height - 8) / 2, 0xE0E0E0);
            }
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
        if(this.buttonRecipe != null) {
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            List<String> tooltips = new ArrayList<>();
            Enchantment enchantment = this.buttonRecipe.getEnchantment();
            tooltips.add(enchantment.getTranslatedName(this.enchantLevel > 0 ? this.enchantLevel : 1));
            String descKey = "enchantment." + enchantment.getRegistryName() + ".desc";
            if(GuiScreen.isShiftKeyDown() && ModIds.enchantment_descriptions.isLoaded && I18n.hasKey(descKey)) {
                tooltips.add(I18n.format(descKey));
            }
            int aspectStart = this.appendAspectTooltip(this.buttonRecipe, tooltips, fontRenderer);
            GuiUtils.drawHoveringText(tooltips, mouseX, mouseY, this.guiEnchanter.width, this.guiEnchanter.height, -1, fontRenderer);
            this.drawAspects(this.buttonRecipe.getEnchantAspects(), aspectStart, mouseX, mouseY);
        }
    }

    public int appendAspectTooltip(EssentiaEnchanterRecipe recipe, List<String> tooltips, FontRenderer fontRenderer) {
        int aspectStart = tooltips.isEmpty() ? 2 : tooltips.size() * 10;
        AspectList recipeAspects = recipe.getEnchantAspects();
        if (recipeAspects != null && recipeAspects.size() > 0) {
            int totalWidth = Math.min(8, recipeAspects.size()) * 18;
            double strWidth = fontRenderer.getStringWidth(" ");
            int width = MathHelper.ceil(totalWidth / strWidth);
            int height = MathHelper.ceil(18.0 / fontRenderer.FONT_HEIGHT) * (int) Math.ceil(recipeAspects.size() / 8.0);
            for(int a = 0; a < height; ++a) {
                tooltips.add(StringUtils.repeat(" ", 120).substring(0, Math.min(120, width)));
            }
        }
        return aspectStart;
    }

    private void drawAspects(AspectList aspectList, int aspectStart, int mouseX, int mouseY) {
        if(aspectList != null && aspectList.size() > 0) {
            GlStateManager.pushMatrix();
            Aspect[] aspectsSortedByAmount = aspectList.getAspectsSortedByAmount();
            for (int i = 0; i < aspectsSortedByAmount.length; i++) {
                Aspect aspect = aspectsSortedByAmount[i];
                if (aspect != null) {
                    int x = mouseX + 12 + (18 * (i % 8));
                    int y = mouseY + aspectStart - 8 + (18 * (int) (i / 8.0));
                    UtilsFX.drawTag(x, y, aspect, aspectList.getAmount(aspect), 0, zLevel);
                }
            }
            GlStateManager.popMatrix();
        }
    }

    @Nullable
    public EssentiaEnchanterRecipe getButtonRecipe() {
        return this.buttonRecipe;
    }

    public void setButtonRecipe(@Nullable EssentiaEnchanterRecipe recipe, int level) {
        this.buttonRecipe = recipe;
        this.enchantLevel = level;
    }
}
