package io.github.tivj.falldamagecalc.gui.components.itemslot;

import io.github.tivj.falldamagecalc.gui.components.EnchantmentChooser;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class ItemDestroySlot extends ItemSlotForEnchanting {
    private static final ResourceLocation itemDestroyIcon = new ResourceLocation("textures/gui/container/creative_inventory/tab_inventory.png");

    public ItemDestroySlot(EnchantmentChooser owner, int id, int x, int y, int width, int height) {
        super(owner, id, x, y, width, height);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        float widthMultiplier = width / 18F;
        float heightMultiplier = height / 18F;
        float pixelWidth = (1F / 128) * widthMultiplier / 2; // divided with 2 because the texture is 2 times bigger than the texture ItemSlot uses
        float pixelHeight = (1F / 128) * heightMultiplier / 2; // divided with 2 because the texture is 2 times bigger than the texture ItemSlot uses
        int u = 172;
        int v = 111;

        mc.getTextureManager().bindTexture(itemDestroyIcon);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        worldrenderer.pos(x, y + height, zLevel).tex(u / widthMultiplier * pixelWidth, (v + 18F) / heightMultiplier * pixelHeight).endVertex();
        worldrenderer.pos(x + width, y + height, zLevel).tex((u + 18F) / widthMultiplier * pixelWidth, (v + 18F) / heightMultiplier * pixelHeight).endVertex();
        worldrenderer.pos(x + width, y, zLevel).tex((u + 18F) / widthMultiplier * pixelWidth, v / heightMultiplier * pixelHeight).endVertex();
        worldrenderer.pos(x, y, zLevel).tex(u / widthMultiplier * pixelWidth, v / heightMultiplier * pixelHeight).endVertex();

        tessellator.draw();

        drawMouseHoverWhiteBox(mouseX, mouseY, widthMultiplier, heightMultiplier);
    }
}
