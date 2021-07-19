package dev.userteemu.falldamagecalc.gui.components;

import dev.userteemu.falldamagecalc.gui.components.textinput.NumberOnlyTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class PotionEffectDisplay extends Gui {
    private static final ResourceLocation effectBackground = new ResourceLocation("falldamagecalculator", "textures/effect_background.png");
    private static final ResourceLocation potionEffects = new ResourceLocation("textures/gui/container/inventory.png");

    private static final int nextID = 0;

    public Potion potion;
    public int x;
    public int y;
    public NumberOnlyTextField level;
    public boolean effectActive = false;
    private final Minecraft mc = Minecraft.getMinecraft();

    public PotionEffectDisplay(int x, int y, FontRenderer fontRenderer, Potion potion) {
        this.x = x;
        this.y = y;
        this.potion = potion;
        this.level = new NumberOnlyTextField(30 + nextID, "0", fontRenderer, x + 32, y, NumberOnlyTextField.numberTextFieldWidth, 22, 3);
        this.level.setEnabled(effectActive);
    }

    public void render() {
        this.renderIcon();
        this.level.drawTextBox();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderIcon() {
        this.mc.getTextureManager().bindTexture(effectBackground);

        float f = 1F / 48F;
        float f1 = 1F / 24F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + 24, this.zLevel).tex((effectActive ? 24F : 0F) * f, 24F * f1).endVertex();
        worldrenderer.pos(x + 24, y + 24, this.zLevel).tex((effectActive ? 48F : 24F) * f, 24F * f1).endVertex();
        worldrenderer.pos(x + 24, y, this.zLevel).tex((effectActive ? 48F : 24F) * f, 0D).endVertex();
        worldrenderer.pos(x, y, this.zLevel).tex((effectActive ? 24F : 0F) * f, 0D).endVertex();
        tessellator.draw();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(potionEffects);

        int i1 = this.potion.getStatusIconIndex();
        this.drawTexturedModalRect(x + 3, y + 3, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + 24 && mouseY < this.y + 24) {
            effectActive = !effectActive;
            this.level.setEnabled(effectActive);
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
        } else this.level.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void updateDisplay() {
        this.level.updateCursorCounter();
    }

    public void keyTyped(char typedChar, int keyCode) {
        this.level.textboxKeyTyped(typedChar, keyCode);
    }
}