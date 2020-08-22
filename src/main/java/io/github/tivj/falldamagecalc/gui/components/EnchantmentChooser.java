package io.github.tivj.falldamagecalc.gui.components;

import io.github.tivj.falldamagecalc.FallDamageCalculatorMod;
import io.github.tivj.falldamagecalc.gui.GuiDamageCalculator;
import io.github.tivj.falldamagecalc.gui.components.itemslot.ItemDestroySlot;
import io.github.tivj.falldamagecalc.gui.components.itemslot.ItemSlot;
import io.github.tivj.falldamagecalc.gui.components.itemslot.ItemSlotForEnchanting;
import io.github.tivj.falldamagecalc.gui.components.textinput.ShortOnlyTextField;
import io.github.tivj.falldamagecalc.utils.ArmorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

import static io.github.tivj.falldamagecalc.gui.components.textinput.NumberOnlyTextField.numberTextFieldWidth;

public class EnchantmentChooser extends Gui {
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public final Minecraft mc = Minecraft.getMinecraft();
    public GuiDamageCalculator owner;
    public ItemStack selectedItem;
    public ArmorType armorType;

    public List<ShortOnlyTextField> enchantmentFields = new ArrayList<>();
    public List<ItemSlotForEnchanting> itemSlots = new ArrayList<>();

    public EnchantmentChooser(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public void initGui(GuiDamageCalculator owner, ItemStack item, ArmorType armorType) {
        this.owner = owner;
        this.selectedItem = item;
        this.armorType = armorType;

        initEnchantmentFields();
        initItemSlots();
    }

    public void render(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 2);

        FallDamageCalculatorMod.drawColoredRect(startX, startY, endX, endY, -383046869, this.zLevel);
        GlStateManager.enableDepth();
        drawItemSlots(mouseX, mouseY);
        renderEnchantmentNames();

        for (ShortOnlyTextField textField : this.enchantmentFields) textField.drawTextBox();

        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
    }

    private void initEnchantmentFields() {
        this.enchantmentFields.clear();
        checkForEnoughWidth();

        int margin = 8;
        int lastY = startY + margin;
        int deltaY = ((this.endY - margin) - (this.startY + margin) - 20) / 4;

        for (int i = 0; i < 5; i++) {
            ShortOnlyTextField textField = new ShortOnlyTextField(20 + i, String.valueOf(EnchantmentHelper.getEnchantmentLevel(i, this.selectedItem)), mc.fontRendererObj, endX - numberTextFieldWidth - 8, lastY, numberTextFieldWidth, 20);
            this.enchantmentFields.add(textField);
            lastY += deltaY;
        }
    }

    private void checkForEnoughWidth() { // if the size changes in the future, these should be re-evaluated
        int slotSize = (int) (18F * ((float)owner.height / 480F));

        int width = 0;
        for (int i = 0; i < 5; i++) {
            int length = mc.fontRendererObj.getStringWidth(I18n.format(Enchantment.getEnchantmentById(i).getName()));
            if (length > width) width = length;
        }

        width += numberTextFieldWidth + 8 + 8 + 16 + (((this.endY - 8) - (this.startY + 8) - slotSize) / 3) * 2 + slotSize;
        if (width > this.endX - this.startX) this.endX = Math.min(startX + width, this.owner.width);
    }

    private void renderEnchantmentNames() {
        for (int i = 0; i < 5; i++) {
            ShortOnlyTextField textField = this.enchantmentFields.get(i);
            String name = I18n.format(Enchantment.getEnchantmentById(i).getName());
            mc.fontRendererObj.drawString(name, textField.xPosition - 8 - mc.fontRendererObj.getStringWidth(name), textField.yPosition + 8, 11389150);
        }
    }

    private void initItemSlots() {
        int margin = 8;
        int slotSize = (int) (18F * ((float)owner.height / 480F));
        int deltaY = ((this.endY - margin) - (this.startY + margin) - slotSize) / 3;
        int baseY = startY + margin;

        int nextAvailableSlotID = 0;

        // Selected item slot
        itemSlots.add(new ItemSlotForEnchanting(this, nextAvailableSlotID++, startX + 16 + deltaY, baseY, slotSize + deltaY, slotSize + deltaY, this.selectedItem));

        int armorMaterial = 0;

        // 1st vertical line from left
        for (int i = 0; i < 4; i++) {
            itemSlots.add(new ItemSlotForEnchanting(this, nextAvailableSlotID++, startX + 16, baseY + deltaY * i, slotSize, slotSize, this.armorType, ItemArmor.ArmorMaterial.values()[armorMaterial++]));
        }

        // 2nd vertical line from left
        itemSlots.add(new ItemSlotForEnchanting(this, nextAvailableSlotID++, startX + 16 + deltaY, baseY + deltaY * 3, slotSize, slotSize, this.armorType, ItemArmor.ArmorMaterial.values()[armorMaterial]));
        itemSlots.add(new ItemDestroySlot(this, nextAvailableSlotID, startX + 16 + deltaY, baseY + deltaY * 2, slotSize, slotSize));
    }

    private void drawItemSlots(int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(statIcons);
        for (ItemSlotForEnchanting itemSlot : this.itemSlots) {
            itemSlot.render(mouseX, mouseY);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (ShortOnlyTextField textField : this.enchantmentFields) textField.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            for (ItemSlotForEnchanting itemSlot : this.itemSlots) {
                if (itemSlot.mouseClicked(mouseX, mouseY, mouseButton)) return;
            }

            if (!isMouseInPopup(mouseX, mouseY)) {
                applyToItemSlot(this.owner.getArmorPiece(this.armorType));
                this.owner.enchantmentChooser = null;
            }
        }
    }

    public void updateScreen() {
        for (ShortOnlyTextField textField : this.enchantmentFields) textField.updateCursorCounter();
    }

    public void keyTyped(char typedChar, int keyCode) {
        for (ShortOnlyTextField textField : this.enchantmentFields) textField.textboxKeyTyped(typedChar, keyCode);
    }

    public void applyToItemSlot(ItemSlot itemSlot) {
        if (itemSlot != null) {
            if (this.selectedItem != null) {
                NBTTagList enchantmentList = new NBTTagList();
                for (int i = 0; i < this.enchantmentFields.size(); i++) {
                    ShortOnlyTextField enchantmentField = this.enchantmentFields.get(i);
                    short level = enchantmentField.getValue();

                    if (level > 0) {
                        NBTTagCompound enchantmentCompound = new NBTTagCompound();
                        enchantmentCompound.setShort("id", (short) i);
                        enchantmentCompound.setShort("lvl", level);
                        enchantmentList.appendTag(enchantmentCompound);
                    }
                }

                if (enchantmentList.tagCount() > 0) {
                    this.selectedItem.setTagInfo("ench", enchantmentList);
                } else if (this.selectedItem.hasTagCompound()) {
                    this.selectedItem.getTagCompound().removeTag("ench");
                }
                itemSlot.itemStack = this.selectedItem.copy();
            } else itemSlot.itemStack = null;
        }
    }

    public boolean isMouseInPopup(int mouseX, int mouseY) {
        return mouseX >= this.startX && mouseY >= this.startY && mouseX < this.endX && mouseY < this.endY;
    }
}