package io.github.tivj.falldamagecalc.gui.components.itemslot;

import io.github.tivj.falldamagecalc.gui.GuiDamageCalculator;
import io.github.tivj.falldamagecalc.utils.ArmorType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class ItemSlotForMainGui extends ItemSlot {
    private final GuiDamageCalculator owner;
    private int availableXSpace;
    public ArmorType armorType;

    public ItemSlotForMainGui(GuiDamageCalculator owner, int id, int x, int y, int width, int height, int availableXSpace, ItemStack itemStack, ArmorType armorType) {
        super(owner.mc, id, x, y, width, height, itemStack == null ? null : itemStack.copy());
        this.owner = owner;
        this.armorType = armorType;
        this.availableXSpace = availableXSpace - width - 8;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);
        if (itemStack != null) {
            NBTTagList nbtTagList = itemStack.getEnchantmentTagList();
            if (nbtTagList != null) {
                List<String> enchantments = getEnchantmentLines(nbtTagList);
                int enchantmentY = y;
                for (String enchantment : enchantments) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x + width + 8, enchantmentY, 0F);

                    float scale = availableXSpace / (mc.fontRendererObj.getStringWidth(enchantment) + 8F);
                    GlStateManager.scale(scale, scale, 1F);
                    enchantmentY += (mc.fontRendererObj.FONT_HEIGHT + 4) * scale;

                    mc.fontRendererObj.drawString(enchantment, 0, 0, 4210752);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    @Override
    protected void onClick(int mouseX, int mouseY, int mouseButton) {
        super.onClick(mouseX, mouseY, mouseButton);
        this.owner.openEnchantmentChooser(this);
    }

    private List<String> getEnchantmentLines(NBTTagList nbtTagList) {
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < nbtTagList.tagCount(); i++) {
            Enchantment enchantment = Enchantment.getEnchantmentById(nbtTagList.getCompoundTagAt(i).getShort("id"));

            if (enchantment instanceof EnchantmentProtection) { //this will include protection, fire_protection, feather_falling, blast_protection, projectile_protection
                lines.add(enchantment.getTranslatedName(nbtTagList.getCompoundTagAt(i).getShort("lvl")));
            }
        }
        return lines;
    }

    @Override
    protected float getItemZOffset(RenderItem renderItem) {
        return -100 - renderItem.zLevel + this.zLevel;
    }
}
