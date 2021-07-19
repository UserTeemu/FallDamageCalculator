package dev.userteemu.falldamagecalc.utils;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;

import java.util.Arrays;
import java.util.List;

public enum ArmorType {
    HELMET(3, Arrays.asList(Items.leather_helmet, Items.chainmail_helmet, Items.iron_helmet, Items.golden_helmet, Items.diamond_helmet)),
    CHESTPLATE(2, Arrays.asList(Items.leather_chestplate, Items.chainmail_chestplate, Items.iron_chestplate, Items.golden_chestplate, Items.diamond_chestplate)),
    LEGGINGS(1, Arrays.asList(Items.leather_leggings, Items.chainmail_leggings, Items.iron_leggings, Items.golden_leggings, Items.diamond_leggings)),
    BOOTS(0, Arrays.asList(Items.leather_boots, Items.chainmail_boots, Items.iron_boots, Items.golden_boots, Items.diamond_boots));

    /**
     * 3 = helmet, 2 = chestplate, 1 = leggings, 0 = helmet
     */
    public final int slot;

    public final List<ItemArmor> items;

    ArmorType(int slot, List<ItemArmor> items) {
        this.slot = slot;
        this.items = items;
    }

    public Item getItem(ItemArmor.ArmorMaterial material) {
        if (material == null) return null;
        for (ItemArmor item : items) {
            if (item.getArmorMaterial().equals(material)) return item;
        }
        throw new IllegalArgumentException(material.getName() + " "+this.name()+" doesn't exist apparently. This should NOT happen ever.");
    }
}
