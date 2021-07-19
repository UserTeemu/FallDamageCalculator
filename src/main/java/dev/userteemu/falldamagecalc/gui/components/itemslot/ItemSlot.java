package dev.userteemu.falldamagecalc.gui.components.itemslot;

import dev.userteemu.falldamagecalc.utils.ArmorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemSlot extends Gui {
    protected final Minecraft mc;
    protected final int id;

    public ItemStack itemStack;

    public int x;
    public int y;
    public int width;
    public int height;

    public ItemSlot(Minecraft mc, int id, int x, int y, int width, int height, ItemStack itemStack) {
        this(mc, id, x, y, width, height);
        this.itemStack = itemStack;
    }

    public ItemSlot(Minecraft mc, int id, int x, int y, int width, int height, ArmorType armorType, ItemArmor.ArmorMaterial armorMaterial) {
        this(mc, id, x, y, width, height);
        this.itemStack = new ItemStack(armorType.getItem(armorMaterial));
    }

    protected ItemSlot(Minecraft mc, int id, int x, int y, int width, int height) {
        this.mc = mc;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(int mouseX, int mouseY) {
        float widthMultiplier = width / 18F;
        float heightMultiplier = height / 18F;
        float pixelWidth = (1F / 128) * widthMultiplier;
        float pixelHeight = (1F / 128) * heightMultiplier;

        mc.getTextureManager().bindTexture(statIcons);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        worldrenderer.pos(x, y + height, zLevel).tex(0F, 18F / heightMultiplier * pixelHeight).endVertex();
        worldrenderer.pos(x + width, y + height, zLevel).tex(18F / widthMultiplier * pixelWidth, 18F / heightMultiplier * pixelHeight).endVertex();
        worldrenderer.pos(x + width, y, zLevel).tex(18F / widthMultiplier * pixelWidth, 0F).endVertex();
        worldrenderer.pos(x, y, zLevel).tex(0F, 0F).endVertex();

        tessellator.draw();

        if (itemStack != null && itemStack.getItem() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(widthMultiplier + x, heightMultiplier + y, getItemZOffset(mc.getRenderItem()));
            GlStateManager.scale(widthMultiplier, heightMultiplier, 1);
            mc.getRenderItem().renderItemIntoGUI(itemStack, 0, 0);
            GlStateManager.popMatrix();
        }

        drawMouseHoverWhiteBox(mouseX, mouseY, widthMultiplier, heightMultiplier);
    }

    protected float getItemZOffset(RenderItem renderItem) {
        return 0F;
    }

    /**
     * Draws transparent white box above the slot, if the mouse is over the slot
     */
    protected void drawMouseHoverWhiteBox(int mouseX, int mouseY, float widthMultiplier, float heightMultiplier) {
        if (isMouseOverSlot(mouseX, mouseY)) {
            drawRect((int) (x + widthMultiplier + 1), (int) (y + heightMultiplier +1), (int) (x + width - widthMultiplier + 1), (int) (y + height - heightMultiplier + 1), -2130706433);
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isMouseOverSlot(mouseX, mouseY)) {
            this.onClick(mouseX, mouseY, mouseButton);
            return true;
        }
        return false;
    }

    protected void onClick(int mouseX, int mouseY, int mouseButton) {
        this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public boolean isMouseOverSlot(int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
