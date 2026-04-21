package com.verdantartifice.thaumicwonders.client.gui.elements.enchanter;

import com.verdantartifice.thaumicwonders.client.gui.GuiEssentiaEnchanter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonScrollAspects extends AbstractButtonEnchanter {
    private final boolean isNextRowButton;

    public GuiButtonScrollAspects(GuiEssentiaEnchanter guiEnchanter, int buttonId, int x, int y, boolean isNextRowButton) {
        super(guiEnchanter, buttonId, x, y, 192, isNextRowButton ? 16 : 0, 11, 11, "");
        this.isNextRowButton = isNextRowButton;
        this.visible = false;
    }

    @Override
    public void updateEnabled() {
        boolean flag;
        if(this.isNextRowButton) {
            int maxRows = this.guiEnchanter.getMaxAspectRows();
            flag = maxRows > 2 && this.guiEnchanter.getAspectRow() < maxRows - 2;
        } else {
            flag = this.guiEnchanter.getAspectRow() > 0;
        }
        this.enabled = flag;
        this.visible = flag;
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }
}
