package com.verdantartifice.thaumicwonders.client.gui.elements.enchanter;

import com.verdantartifice.thaumicwonders.client.gui.GuiEssentiaEnchanter;
import com.verdantartifice.thaumicwonders.common.utils.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;

@SideOnly(Side.CLIENT)
public class GuiButtonStartEnchant extends AbstractButtonEnchanter {
    public GuiButtonStartEnchant(GuiEssentiaEnchanter guiEnchanter, int buttonId, int x, int y) {
        super(guiEnchanter, buttonId, x, y, 176, 0, 15, 15, "");
    }

    @Override
    public void updateEnabled() {
        this.enabled = this.guiEnchanter.isReadyToEnchant();
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return this.enabled && mouseOver ? 1 : 0;
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
        GuiUtils.drawHoveringText(Collections.singletonList(StringHelper.getLocalizedString("essentia_enchanter", "gui", this.enabled ? "enabled" : "disabled")),
                mouseX, mouseY, this.guiEnchanter.width, this.guiEnchanter.height, -1, Minecraft.getMinecraft().fontRenderer);
    }
}
