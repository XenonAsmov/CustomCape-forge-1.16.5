package me.xenon.cape.module;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.xenon.cape.Wrapper;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CustomCape implements Wrapper {
    private float crouchAnimation = 0.75f;
    private float runAnimation = 0.1f;
    private boolean wasCrouching = false;


    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        if (!(mc.currentScreen instanceof InventoryScreen)) {
            this.render((AbstractClientPlayerEntity)event.getPlayer(), event.getRenderer(), event.getMatrixStack(), event.getBuffers(), event.getLight(), mc.getRenderPartialTicks());
        }
    }

    private void render(AbstractClientPlayerEntity player, PlayerRenderer renderer, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, float partialTicks) {
        if (player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() != Items.ELYTRA) {
            if (player == mc.player) {
                PlayerModel playerModel = renderer.getEntityModel();
                matrixStack.push();
                float f = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
                float rotationRadians = f * 0.017453292F;
                double offsetX = Math.sin((double)rotationRadians) * 0.1;
                double offsetZ = -Math.cos((double)rotationRadians) * 0.1;
                boolean isCrouching = player.isCrouching();
                if (isCrouching != this.wasCrouching) {
                    this.wasCrouching = isCrouching;
                }

                crouchAnimation = isCrouching ? 0.3F : 0.0F;
                float crouchOffset = this.crouchAnimation;
                boolean isRunning = player.isSprinting();
                runAnimation = isRunning ? 0.1F : 0.0F;
                float runOffset = this.runAnimation;
                float yOffset = player.getHeight() - 0.4F + crouchOffset;
                matrixStack.translate(offsetX, (double)yOffset, offsetZ);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F - f));
                double d0 = MathHelper.lerp((double)partialTicks, player.prevChasingPosX, player.chasingPosX) - MathHelper.lerp((double)partialTicks, player.prevPosX, player.getPosX());
                double d1 = MathHelper.lerp((double)partialTicks, player.prevChasingPosY, player.chasingPosY) - MathHelper.lerp((double)partialTicks, player.prevPosY, player.getPosY());
                double d2 = MathHelper.lerp((double)partialTicks, player.prevChasingPosZ, player.chasingPosZ) - MathHelper.lerp((double)partialTicks, player.prevPosZ, player.getPosZ());
                double d3 = (double)MathHelper.sin(rotationRadians);
                double d4 = (double)(-MathHelper.cos(rotationRadians));
                float f1 = (float)d1 * 10.0F;
                f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
                float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
                f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
                float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
                f3 = MathHelper.clamp(f3, -20.0F, 20.0F);
                if (f2 < 0.0F) {
                    f2 = 0.0F;
                }

                float f4 = MathHelper.lerp(partialTicks, player.prevCameraYaw, player.cameraYaw);
                f1 += MathHelper.sin(MathHelper.lerp(partialTicks, player.prevDistanceWalkedModified, player.distanceWalkedModified) * 6.0F) * 32.0F * f4;
                float xRotation = -(6.0F + f2 / 2.0F + f1);
                xRotation -= crouchOffset * 100.0F;
                xRotation -= runOffset * 50.0F;
                matrixStack.rotate(Vector3f.XP.rotationDegrees(xRotation));
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(-(f3 / 2.0F)));
                matrixStack.rotate(Vector3f.YP.rotationDegrees(-180.0F));
                float crouchWave;
                if (isRunning) {
                    crouchWave = MathHelper.sin((float)player.ticksExisted * 0.5F) * runOffset * 5.0F;
                    matrixStack.rotate(Vector3f.ZP.rotationDegrees(crouchWave));
                }

                if (isCrouching) {
                    crouchWave = (float)Math.sin((double)player.ticksExisted * 0.2) * crouchOffset * 3.0F;
                    matrixStack.rotate(Vector3f.ZP.rotationDegrees(crouchWave));
                }

                matrixStack.scale(-1.0F, -1.0F, 1.0F);
                IVertexBuilder ivertexbuilder = buffer.getBuffer(RenderType.getEntitySolid(new ResourceLocation("cape", "textures/girlcape.png"))); // Можно заменить girlcape.png на свою png
                playerModel.renderCape(matrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY);
                matrixStack.pop();
            }
        }
    }
}

