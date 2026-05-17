package com.verdantartifice.thaumicwonders.client.renderers.models.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * This class is a modified version of the Gadomancy
 * <a href="https://github.com/makeo/Gadomancy/blob/master/src/main/java/makeo/gadomancy/client/models/ModelInfusionClawPart.java">ModelInfusionClawPart</a>
 * for Minecraft version 1.7.10
 * <p>
 * Gadomancy is Open Source and distributed under the
 * GNU LESSER GENERAL PUBLIC LICENSE
 * for more read the LICENSE file
 */
public class ModelInfusionClawPart extends ModelBase {
    private ModelRenderer shape;

    public ModelInfusionClawPart(int part) {
        this.textureWidth = 32;
        this.textureHeight = 64;
        this.shape = new ModelRenderer(this, 0, 8 * part);
        this.shape.addBox(-3.0f, -1.0f, -3.0f, 6, 2, 6);
        this.shape.setRotationPoint(0, 0, 0);
        this.shape.setTextureSize(this.textureWidth, this.textureHeight);
        this.shape.mirror = true;
        this.setRotationAngles(this.shape, 0, 0, 0);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.shape.render(scale);
    }

    private void setRotationAngles(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
