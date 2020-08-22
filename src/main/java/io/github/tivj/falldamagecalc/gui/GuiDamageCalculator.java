package io.github.tivj.falldamagecalc.gui;

import io.github.tivj.falldamagecalc.FallDamageCalculatorMod;
import io.github.tivj.falldamagecalc.calculator.FallDamageCalculator;
import io.github.tivj.falldamagecalc.gui.components.*;
import io.github.tivj.falldamagecalc.gui.components.itemslot.ItemSlot;
import io.github.tivj.falldamagecalc.gui.components.itemslot.ItemSlotForMainGui;
import io.github.tivj.falldamagecalc.gui.components.textinput.BlockHeightField;
import io.github.tivj.falldamagecalc.utils.ArmorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;

import java.awt.*;
import java.io.IOException;

public class GuiDamageCalculator extends GuiScreen {
    public int startX;
    public int startY;
    public int endX;
    public int endY;

    public final Minecraft mc = Minecraft.getMinecraft();
    private final EntityPlayer player = this.mc.thePlayer;
    public EnchantmentChooser enchantmentChooser;

    public ItemSlotForMainGui helmet;
    public ItemSlotForMainGui chestplate;
    public ItemSlotForMainGui leggings;
    public ItemSlotForMainGui boots;

    private double playerY;
    private double raytracedY;
    public BlockHeightField originalY;
    public BlockHeightField destinationY;
    private boolean jumpButtonDown = false;
    private GuiButton jumpButton;
    private GuiButton calculationButton;

    public PotionEffectDisplay jumpPotion;
    public PotionEffectDisplay resistancePotion;

    private float damageTaken = 0F;
    private float hp = this.player.getHealth();

    public GuiDamageCalculator() {
        this.playerY = this.player.posY;
        raytraceBlockPos();
    }

    public void raytraceBlockPos() {
        MovingObjectPosition result = this.player.rayTrace(100, 1F);
        if (!result.typeOfHit.equals(MovingObjectPosition.MovingObjectType.BLOCK)) {
            FallDamageCalculatorMod.printMessage("falldamagecalc.raytraceFail");
        } else {
            BlockPos pos = result.getBlockPos();
            AxisAlignedBB bb = null;
            int loops = 0;

            while (bb == null) {
                bb = this.mc.theWorld.getBlockState(pos).getBlock().getCollisionBoundingBox(this.mc.theWorld, pos, this.mc.theWorld.getBlockState(pos));
                pos = pos.down();

                if (bb == null && loops > 256) {
                    FallDamageCalculatorMod.printMessage("falldamagecalc.raytraceFail");
                    return;
                }
                loops++;
            }

            this.raytracedY = bb.maxY;
        }
    }

    private String jumpButtonTitle() {
        return I18n.format("falldamagecalc.jumpingBeforeFall." + (this.jumpButtonDown ? "true" : "false"));
    }

    @Override
    public void initGui() {
        super.initGui();

        this.startX = this.width / 6;
        this.startY = this.height / 5;
        this.endX = this.width - this.startX;
        this.endY = this.height - this.startY;

        initSideOptions();
        initArmor();
        initPotionOptions();
        calculationButton = new GuiButton(0, startX + (endX - startX) / 2 - 80, this.jumpPotion.y + 8, 160, 20, I18n.format("falldamagecalc.calculate"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        FallDamageCalculatorMod.drawColoredRect(startX, startY, endX, endY, -2130706433, this.zLevel);
        FallDamageCalculatorMod.drawCenteredStringWithoutShadow(fontRendererObj, I18n.format("falldamagecalc.title"), width / 2, startY + (endY - startY) / 24, 4210752);
        GlStateManager.color(1F, 1F, 1F, 1F);

        renderHealth();
        renderArmor(this.enchantmentChooser == null ? mouseX : -1, this.enchantmentChooser == null ? mouseY : -1);
        renderSideOptions(this.enchantmentChooser == null ? mouseX : -1, this.enchantmentChooser == null ? mouseY : -1);
        renderPotionOptions();
        renderDamageTaken();

        if (this.enchantmentChooser != null) {
            this.calculationButton.drawButton(this.mc, -1, -1);
            this.enchantmentChooser.render(mouseX, mouseY);
        } else {
            this.calculationButton.drawButton(this.mc, mouseX, mouseY);
        }
    }

    private void renderDamageTaken() {
        if (calculationResultColorMode != -2) {
            GlStateManager.pushMatrix();
            float scale = 22F / fontRendererObj.FONT_HEIGHT;
            String string = I18n.format("falldamagecalc.damageTaken", damageTaken);
            float length = fontRendererObj.getStringWidth(string) * scale;
            GlStateManager.translate(this.calculationButton.xPosition + (this.calculationButton.width / 2F) - length / 2, this.resistancePotion.y + 1, 0F);
            GlStateManager.scale(scale, scale, 0F);
            fontRendererObj.drawString(string, 0, 0, calculateResultColor());
            GlStateManager.popMatrix();
        }
    }

    private int calculationResultColorMultiplier = 0;
    private short calculationResultColorMode = -2; // -2 = text invisible, -1 = going from 40 to 0, 0 = normal, 1 = going to 40 from 0

    private int calculateResultColor() {
        if (calculationResultColorMode == -1) {
            if (calculationResultColorMultiplier == 0) calculationResultColorMode = 0;
            calculationResultColorMultiplier--;
            return getColor();
        } else if (calculationResultColorMode == 1) {
            calculationResultColorMultiplier++;
            if (calculationResultColorMultiplier == 10) calculationResultColorMode = -1;
            return getColor();
        }
        return 1973021;
    }

    private final Color normalTextColor = new Color(1973021);
    private final Color highlightTextColor = new Color(13008294);
    private int getColor() {
        int r = normalTextColor.getRed() + (int) ((highlightTextColor.getRed() - normalTextColor.getRed()) / 10F * calculationResultColorMultiplier);
        int g = normalTextColor.getGreen() + (int) ((highlightTextColor.getGreen() - normalTextColor.getGreen()) / 10F * calculationResultColorMultiplier);
        int b = normalTextColor.getBlue() + (int) ((highlightTextColor.getBlue() - normalTextColor.getBlue()) / 10F * calculationResultColorMultiplier);
        int a = normalTextColor.getAlpha() + (int) ((highlightTextColor.getAlpha() - normalTextColor.getAlpha()) / 10F * calculationResultColorMultiplier);

        return ((a & 0xFF) << 24) |
               ((r & 0xFF) << 16) |
               ((g & 0xFF) << 8)  |
               (b & 0xFF);
    }

    private void initArmor() {
        int slotSize = 27;
        int deltaY = (this.endY - this.startY) / 16;

        int x = (endX - startX - 16) / 5;
        int y = startY + ((endY - startY) / 8);

        this.boots = new ItemSlotForMainGui(this, 10, startX + 16, y, slotSize + deltaY, slotSize + deltaY, x, this.player.inventory.armorItemInSlot(0), ArmorType.BOOTS);
        this.leggings = new ItemSlotForMainGui(this, 11, startX + 16 + x, y, slotSize + deltaY, slotSize + deltaY, x, this.player.inventory.armorItemInSlot(1), ArmorType.LEGGINGS);
        this.chestplate = new ItemSlotForMainGui(this, 12, startX + 16 + x * 2, y, slotSize + deltaY, slotSize + deltaY, x, this.player.inventory.armorItemInSlot(2), ArmorType.CHESTPLATE);
        this.helmet = new ItemSlotForMainGui(this, 13, startX + 16 + x * 3, y, slotSize + deltaY, slotSize + deltaY, this.originalY.xPosition - (startX + 16 + x * 3), this.player.inventory.armorItemInSlot(3), ArmorType.HELMET);
    }

    private void renderArmor(int mouseX, int mouseY) {
        this.boots.render(mouseX, mouseY);
        this.leggings.render(mouseX, mouseY);
        this.chestplate.render(mouseX, mouseY);
        this.helmet.render(mouseX, mouseY);
    }

    private void initPotionOptions() {
        this.jumpPotion = new PotionEffectDisplay(startX + 8, this.helmet.y + 24 + (this.endY - this.startY) / 8, this.fontRendererObj, Potion.jump);
        this.resistancePotion = new PotionEffectDisplay(startX + 8, this.jumpPotion.y + Math.max((this.endY - this.startY) / 8, 24 + 4), this.fontRendererObj, Potion.resistance);
    }

    private void renderPotionOptions() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.jumpPotion.render();

        this.resistancePotion.render();
    }

    private void initSideOptions() {
        int height = this.fontRendererObj.FONT_HEIGHT + (endY - startY) / 10;
        int baseY = startY + 8;
        int width = Math.max(this.fontRendererObj.getStringWidth(I18n.format("falldamagecalc.jumpingBeforeFall.true")), this.fontRendererObj.getStringWidth(I18n.format("falldamagecalc.jumpingBeforeFall.false"))) + 16;
        int x = endX - 28 - width;

        this.originalY = new BlockHeightField(1, I18n.format("falldamagecalc.fromY"), String.valueOf(playerY), fontRendererObj, x, baseY + height, width, 20);
        this.destinationY = new BlockHeightField(2, I18n.format("falldamagecalc.toY"), String.valueOf(raytracedY), fontRendererObj, x, baseY + height * 2, width, 20);

        this.jumpButton = new GuiButton(3, x, startY + height * 3, width, 20, jumpButtonTitle());
    }

    private void renderSideOptions(int mouseX, int mouseY) {
        this.originalY.drawTextBox();
        this.destinationY.drawTextBox();
        this.jumpButton.drawButton(this.mc, this.enchantmentChooser == null ? mouseX : -1, this.enchantmentChooser == null ? mouseY : -1);
    }

    public ItemSlot getArmorPiece(ArmorType armorType) {
        switch (armorType) {
            case BOOTS:
                return this.boots;
            case LEGGINGS:
                return this.leggings;
            case CHESTPLATE:
                return this.chestplate;
            case HELMET:
                return this.helmet;
        }
        throw new NullPointerException("Armor type was a weird one.");
    }

    /**
     * Code partly taken from GuiIngame.renderPlayerStats
     */
    private void renderHealth() {
        GlStateManager.pushMatrix();
        int fullWidth = 0;
        int fullHeight = 0;

        this.mc.getTextureManager().bindTexture(icons);
        int health = MathHelper.ceiling_float_int(this.hp);
        float maxHealth = (float) player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
        float f1 = player.getAbsorptionAmount();
        float f2 = f1;
        int l1 = MathHelper.ceiling_float_int((maxHealth + f1) / 2F / 10F);
        int i2 = Math.max(10 - (l1 - 2), 3);

        for (int i6 = MathHelper.ceiling_float_int((maxHealth + f1) / 2F) - 1; i6 >= 0; --i6) {
            int posX = i6 % 10 * 8;
            int posY = -(MathHelper.ceiling_float_int((float) (i6 + 1) / 10F) - 1) * i2;

            if (fullWidth < posX) fullWidth = posX;
            if (fullHeight < posY) fullHeight = posY;
        }

        fullWidth += 9;
        fullHeight += 9;

        float scale = (endX - startX - 64F) / fullWidth; // Aspect ratio shall be the same, and it is determined based on the width
        GlStateManager.translate(startX + 32, endY - (fullHeight * scale) - 32, 0);
        GlStateManager.scale(scale, scale, 0F);

        for (int i6 = MathHelper.ceiling_float_int((maxHealth + f1) / 2F) - 1; i6 >= 0; --i6) {
            int posX = i6 % 10 * 8;
            int posY = -(MathHelper.ceiling_float_int((float) (i6 + 1) / 10F) - 1) * i2;

            this.drawTexturedModalRect(posX, posY, 16, 0, 9, 9);

            if (f2 > 0F) {
                if (f1 % 2F == 1F) {
                    this.drawTexturedModalRect(posX, posY, 16 + 153, 0, 9, 9);
                } else {
                    this.drawTexturedModalRect(posX, posY, 16 + 144, 0, 9, 9);
                }

                f2 -= 2F;
            } else {
                if (i6 * 2 + 1 < health) {
                    this.drawTexturedModalRect(posX, posY, 16 + 36, 0, 9, 9);
                }

                if (i6 * 2 + 1 == health) {
                    this.drawTexturedModalRect(posX, posY, 16 + 45, 0, 9, 9);
                }
            }
            if (fullWidth < posX) fullWidth = posX;
        }

        GlStateManager.popMatrix();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.originalY.textboxKeyTyped(typedChar, keyCode);
        this.destinationY.textboxKeyTyped(typedChar, keyCode);
        this.jumpPotion.keyTyped(typedChar, keyCode);
        this.resistancePotion.keyTyped(typedChar, keyCode);
        if (this.enchantmentChooser != null) this.enchantmentChooser.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.enchantmentChooser != null) {
            this.enchantmentChooser.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }

        if (this.helmet.mouseClicked(mouseX, mouseY, mouseButton)) return;
        if (this.chestplate.mouseClicked(mouseX, mouseY, mouseButton)) return;
        if (this.leggings.mouseClicked(mouseX, mouseY, mouseButton)) return;
        if (this.boots.mouseClicked(mouseX, mouseY, mouseButton)) return;

        this.originalY.mouseClicked(mouseX, mouseY, mouseButton);
        this.destinationY.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.jumpButton.mousePressed(this.mc, mouseX, mouseY)) {
            jumpButtonDown = !jumpButtonDown;
            this.jumpButton.displayString = jumpButtonTitle();
        }

        this.jumpPotion.onMouseClicked(mouseX, mouseY, mouseButton);
        this.resistancePotion.onMouseClicked(mouseX, mouseY, mouseButton);

        if (this.calculationButton.mousePressed(this.mc, mouseX, mouseY)) calculate();
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        if (this.enchantmentChooser != null) this.enchantmentChooser.updateScreen();
        this.originalY.updateCursorCounter();
        this.destinationY.updateCursorCounter();
        this.jumpPotion.updateDisplay();
        this.resistancePotion.updateDisplay();
        super.updateScreen();
    }

    public void openEnchantmentChooser(ItemSlotForMainGui slot) {
        int startX = slot.x + slot.width + 8;
        int startY = slot.y + 8;
        int width = startX + this.width / 5;
        int height = startY + this.height / 5;

        (this.enchantmentChooser = new EnchantmentChooser(startX, startY, Math.min(width, this.width), Math.min(height, this.height))).initGui(this, this.getArmorPiece(slot.armorType).itemStack, slot.armorType);
    }

    public void calculate() {
        this.calculationResultColorMode = -2;
        FallDamageCalculator.executorService.submit(() -> {
            try {
                double result = FallDamageCalculator.calculateFallDamage(
                        this.player,
                        constructArmorInventory(),
                        this.jumpPotion,
                        this.resistancePotion,
                        originalY.getBlockY(),
                        destinationY.getBlockY(),
                        this.jumpButtonDown
                );
                this.damageTaken = (float) result;
                this.hp = (float) (this.player.getHealth() - result);
                this.calculationResultColorMode = 1;
            } catch (Throwable e) {
                e.printStackTrace();
                FallDamageCalculatorMod.printMessage("falldamagecalc.calculationFail");
            }
        });
    }

    private ItemStack[] constructArmorInventory() {
        ItemStack[] armorInv = new ItemStack[4];
        armorInv[0] = this.boots.itemStack;
        armorInv[1] = this.leggings.itemStack;
        armorInv[2] = this.chestplate.itemStack;
        armorInv[3] = this.helmet.itemStack;
        return armorInv;
    }
}