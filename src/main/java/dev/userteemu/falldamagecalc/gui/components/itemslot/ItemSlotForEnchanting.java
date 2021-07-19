package dev.userteemu.falldamagecalc.gui.components.itemslot;

import dev.userteemu.falldamagecalc.utils.ArmorType;
import dev.userteemu.falldamagecalc.gui.components.EnchantmentChooser;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemSlotForEnchanting extends ItemSlot {
    public final EnchantmentChooser owner;

    public ItemSlotForEnchanting(EnchantmentChooser owner, int id, int x, int y, int width, int height, ArmorType armorType, ItemArmor.ArmorMaterial armorMaterial) {
        super(owner.mc, id, x, y, width, height, armorType, armorMaterial);
        this.owner = owner;
    }

    public ItemSlotForEnchanting(EnchantmentChooser owner, int id, int x, int y, int width, int height, ItemStack armor) {
        super(owner.mc, id, x, y, width, height, armor);
        this.owner = owner;
    }

    protected ItemSlotForEnchanting(EnchantmentChooser owner, int id, int x, int y, int width, int height) {
        super(owner.mc, id, x, y, width, height);
        this.owner = owner;
    }

    @Override
    protected void onClick(int mouseX, int mouseY, int mouseButton) {
        this.owner.itemSlots.get(0).itemStack = itemStack == null ? null : itemStack.copy();
        owner.selectedItem = this.owner.itemSlots.get(0).itemStack;
        super.onClick(mouseX, mouseY, mouseButton);
    }
}
