package com.verdantartifice.thaumicwonders.client.renderers.tile;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.client.renderers.models.block.ModelInfusionClawPart;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileInfusionClaw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.renderers.entity.RenderSpecialItem;
import thaumcraft.common.lib.SoundsTC;

import java.util.Random;

/**
 * This class is a modified version of the Gadomancy
 * <a href="https://github.com/makeo/Gadomancy/blob/master/src/main/java/makeo/gadomancy/client/renderers/tile/RenderTileInfusionClaw.java">RenderTileInfusionClaw</a>
 * for Minecraft version 1.7.10
 * <p>
 * Gadomancy is Open Source and distributed under the
 * GNU LESSER GENERAL PUBLIC LICENSE
 * for more read the LICENSE file
 */
@SideOnly(Side.CLIENT)
public class TesrInfusionClaw extends TileEntitySpecialRenderer<TileInfusionClaw> {
    public static final ResourceLocation CLAW_TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/blocks/infusion_claw.png");
    public static final ModelInfusionClawPart[] MODEL_PARTS = new ModelInfusionClawPart[8];
    private static final RenderSpecialItem RENDER_ORB = new RenderSpecialItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem());
    private static final ItemStack GAUNTLET = new ItemStack(ItemsTC.casterBasic);
    private static final ItemStack ORB = new ItemStack(ItemsTC.primordialPearl);
    private static final Random RANDOM = new Random();
    private static final double SCALE_SIDES = 0.85;
    private static final double SCALE_CIRCLE = 0.75;

    @Override
    public void render(TileInfusionClaw tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Minecraft minecraft = Minecraft.getMinecraft();

        if(minecraft.getRenderViewEntity() == null)
            return;

        float ticks = minecraft.getRenderViewEntity().ticksExisted + partialTicks;
        float elapsed = ticks - tile.lastRenderTick;
        tile.lastRenderTick = ticks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        this.renderParts(tile, elapsed, ticks);
        GlStateManager.popMatrix();
    }

    private void renderParts(TileInfusionClaw tile, float elapsed, float ticks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.5, 0.5);
        this.bindTexture(CLAW_TEXTURE);
        this.renderTop();
        this.renderSides(tile, elapsed);
        this.renderCircle(tile, elapsed);
        this.renderGauntlet(ticks);
        this.renderActivationEffect(tile, elapsed, ticks);
        GlStateManager.popMatrix();
    }

    private void renderTop() {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.5f - (1.0 / 16.0 * SCALE_SIDES), 0);
        GlStateManager.scale(SCALE_SIDES, SCALE_SIDES, SCALE_SIDES);
        this.renderPart(4);
        GlStateManager.popMatrix();
    }

    private void renderSides(TileInfusionClaw tile, float elapsed) {
        World world = tile.getWorld();
        BlockPos pos = tile.getPos();

        GlStateManager.pushMatrix();
        float speed = (float) Math.pow(1.1, Math.abs(tile.animationStates[8]));
        if (tile.animationStates[8] > 0) {
            tile.animationStates[8] += elapsed;

            if (tile.animationStates[8] > 20 * 2.5f) {
                tile.animationStates[8] = -tile.animationStates[8];
                tile.animationStates[9] = 1;

                createSideZap(tile);
            }
        } else if (tile.animationStates[8] < 0) {
            tile.animationStates[8] += elapsed * 0.5f;

            if (tile.animationStates[8] > 0) {
                tile.animationStates[8] = 0;
            }
        }

        for (int i = 0; i < 4; i++) {
            EnumFacing facing = EnumFacing.HORIZONTALS[i];
            float heightMove = this.getNextMoveOffset(tile, i, speed, elapsed);
            float widthMove = this.getNextMoveOffset(tile, i + 4, speed, elapsed);
            boolean powered = tile.getWorld() != null ? tile.getWorld().isSidePowered(tile.getPos(), facing) : false;

            GlStateManager.pushMatrix();
            GlStateManager.translate(-facing.getZOffset() * widthMove, heightMove, -facing.getXOffset() * widthMove);
            GlStateManager.translate((0.5 - (1f / 16f * SCALE_SIDES)) * facing.getXOffset(), 0, (0.5 - (1f / 16f * SCALE_SIDES)) * facing.getZOffset());
            GlStateManager.rotate(90, -1 * facing.getZOffset(), 0, facing.getXOffset());
            GlStateManager.scale(SCALE_SIDES, SCALE_SIDES, SCALE_SIDES);
            this.renderRedstonePart(powered);
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }

    private void renderCircle(TileInfusionClaw tile, float elapsed) {
        GlStateManager.pushMatrix();
        float speed = (float) Math.pow(1.1, Math.abs(tile.animationStates[9]));
        if(speed > 50) {
            speed = 50;
        }

        if(tile.animationStates[9] > 0) {
            tile.animationStates[9] += 0.5f * elapsed;

            if (tile.animationStates[9] > 20 * 5.0f) {
                tile.animationStates[9] = -tile.animationStates[9];

                tile.animationStates[11] = 0.5f;
            }
        } else if(tile.animationStates[9] < 0) {
            tile.animationStates[9] += elapsed * 0.5f;

            if (tile.animationStates[9] > 0) {
                tile.animationStates[9] = 0;
            }
        }
        for (int i = 0; i < 4; i++) {
            GlStateManager.pushMatrix();

            tile.animationStates[10] += (float) (speed * (elapsed * 0.001 * Math.PI));

            float angle = (float) (tile.animationStates[10] + (i * 0.5 * Math.PI));
            float x = (float) Math.sin(angle);
            float y = (float) Math.cos(angle);
            float radius = 0.25f;

            GlStateManager.translate(0, (1.0f / 16.0f) * Math.sin(angle / 1.4) + (1.0f / 16.0f), 0);
            GlStateManager.translate(radius * x, -0.27, radius * y);
            GlStateManager.rotate(45, (float) Math.cos(angle), 0, (float) -Math.sin(angle));
            GlStateManager.scale(SCALE_CIRCLE, SCALE_CIRCLE, SCALE_CIRCLE);

            renderPart(i);

            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    private void renderGauntlet(float ticks) {
        GlStateManager.rotate(((ticks/3) / 20.0F) * (180F / (float)Math.PI), 0, 1.0f, 0);
        GlStateManager.pushMatrix();

        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.scale(0.6f, 0.6f, 0.6f);
        GlStateManager.translate(0, -0.72, 0);
        GlStateManager.disableLighting();
        GlStateManager.pushAttrib();

        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItem(GAUNTLET, ItemCameraTransforms.TransformType.GROUND);
        RenderHelper.disableStandardItemLighting();

        GlStateManager.popAttrib();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderActivationEffect(TileInfusionClaw tile, float elapsed, float ticks) {
        if(tile.animationStates[11] > 0) {
            tile.animationStates[11] += elapsed / 70f;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, SCALE_SIDES - 0.15 - tile.animationStates[11], 0);

            Random random = new Random(187L);
            int q = !FMLClientHandler.instance().getClient().gameSettings.fancyGraphics ? 5 : 10;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder wr = tessellator.getBuffer();
            RenderHelper.disableStandardItemLighting();
            float f1 = ticks / 500.0F;
            float f3 = 0.9F;
            float f2 = 0.0F;

            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.disableAlpha();
            GlStateManager.enableCull();
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();

            for(int i = 0; i < q; ++i) {
                GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F + f1 * 360.0F, 0.0F, 0.0F, 1.0F);
                wr.begin(6, DefaultVertexFormats.POSITION_COLOR);
                float fa = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
                float f4 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
                fa /= 30.0F / (Math.min(ticks, 10) / 10.0F);
                f4 /= 30.0F / (Math.min(ticks, 10) / 10.0F);
                wr.pos(0.0F, 0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F - f2).endVertex();
                wr.pos(-0.866 * (double)f4, fa, -0.5F * f4).color(1.0F, 0.0F, 1.0F, 0.0F).endVertex();
                wr.pos(0.866 * (double)f4, fa, -0.5F * f4).color(1.0F, 0.0F, 1.0F, 0.0F).endVertex();
                wr.pos(0.0F, fa, f4).color(1.0F, 0.0F, 1.0F, 0.0F).endVertex();
                wr.pos(-0.866 * (double)f4, fa, -0.5F * f4).color(1.0F, 0.0F, 1.0F, 0.0F).endVertex();
                tessellator.draw();
            }

            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.disableCull();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableBlend();
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.popMatrix();

            GlStateManager.popMatrix();

            if(SCALE_SIDES - tile.animationStates[11] < -0.5f) {
                tile.animationStates[11] = 0;
            }
        }
    }

    private void createSideZap(TileInfusionClaw tile) {
        for(EnumFacing facing : EnumFacing.HORIZONTALS) {
            this.createZap(tile,
                    0.5f + 0.5f * facing.getXOffset(), 0.6f, 0.5f + 0.5f * facing.getZOffset(),
                    0.5f, 0, 0.5f);
        }
        this.playZapSound(tile);
    }

    private void createZap(TileInfusionClaw tile, float startX, float startY, float startZ, float endX, float endY, float endZ) {
        FXDispatcher.INSTANCE.arcLightning(
                tile.getPos().getX() + startX,
                tile.getPos().getY() + startY,
                tile.getPos().getZ() + startZ,
                tile.getPos().getX() + endX,
                tile.getPos().getY() + endY,
                tile.getPos().getZ() + endZ,
                1.0f, 1.0f, 1.0f, 0.1f);
    }

    private void playZapSound(TileInfusionClaw tile) {
        tile.getWorld().playSound(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), SoundsTC.zap, SoundCategory.BLOCKS,
                0.6f, tile.getWorld().rand.nextFloat() * 0.2f, false);
    }

    private float getNextMoveOffset(TileInfusionClaw tile, int id, float speed, float elapsed) {
        tile.animationStates[id] += (float) (RANDOM.nextDouble() * 2 * elapsed * speed);
        return (float) ((1d / 16d) * Math.sin(tile.animationStates[id] / 30));
    }

    private void renderRedstonePart(boolean powered) {
        renderPart(powered ? 6 : 5);
    }

    private void renderPart(int num) {
        MODEL_PARTS[num].render(Minecraft.getMinecraft().player, 0, 0, 0, 0, 0, 0.0625f);
    }

    static {
        for(int i = 0; i < MODEL_PARTS.length; i++) {
            MODEL_PARTS[i] = new ModelInfusionClawPart(i);
        }
    }
}
