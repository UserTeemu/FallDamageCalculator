package dev.userteemu.falldamagecalc.calculator;

import dev.userteemu.falldamagecalc.gui.components.PotionEffectDisplay;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FallDamageCalculator {
    public static final ExecutorService executorService = Executors.newFixedThreadPool(1);

    /**
     * Note: Should be called from another thread
     * @param jumper player performing the jump
     * @param armorInventory array of armor, see GuiDamageCalculator#constructArmorInventory
     * @param jumpPotion PotionEffectDisplay from the GUI for jump potion
     * @param resistancePotion PotionEffectDisplay from the GUI for resistance potion
     * @param playerY Y level where the player is jumping from
     * @param groundLevel ground level where the player will land
     * @param jumpOnStart if the player jumps up when starting the momentum
     * @return health the player will have left
     * @throws DamageCalculationException only if it doesn't like people, aka always
     */
    public static double calculateFallDamage(EntityPlayer jumper, ItemStack[] armorInventory, PotionEffectDisplay jumpPotion, PotionEffectDisplay resistancePotion, double playerY, double groundLevel, boolean jumpOnStart) throws DamageCalculationException {
        double motionY = 0D;
        float fallDistance = 0F;
        AxisAlignedBB playerBB = jumper.getEntityBoundingBox().addCoord(0, playerY - jumper.posY, 0);

        if (jumpOnStart) {
            motionY = 0.42D;

            if (jumpPotion.effectActive) {
                motionY += (jumpPotion.level.getValue() + 1F) * 0.1F;
            }
        }

        long loops = 0;
        while (true) {
            loops++;

            playerBB = playerBB.addCoord(0, motionY, 0);
            if (playerBB.minY <= groundLevel) {
                break;
            }

            fallDistance = (float)((double)fallDistance - motionY);

            motionY -= 0.08D;
            motionY *= 0.9800000190734863D;

            if (loops > 1000000L) {
                throw new TooManyCycles();
            }
        }

        float damageMultiplier = 1F;
        double damageAmount = MathHelper.ceiling_double_int((fallDistance - 3F - (jumpPotion.effectActive ? (float)(jumpPotion.level.getValue() + 1) : 0F)) * damageMultiplier);
        damageAmount = applyPotionDamageCalculations(damageAmount, armorInventory, resistancePotion); // fall damage bypasses armor, no need to calculate it

        return Math.max(damageAmount - jumper.getAbsorptionAmount(), 0F); // health left after jump
    }

    private static double applyPotionDamageCalculations(double damage, ItemStack[] armor, PotionEffectDisplay resistancePotion) {
        if (resistancePotion.effectActive) {
            int i = (resistancePotion.level.getValue() + 1) * 5;
            int j = 25 - i;
            damage = j * damage / 25F;
        }

        if (damage <= 0F) {
            return 0F;
        } else {
            int k = EnchantmentHelper.getEnchantmentModifierDamage(armor, DamageSource.fall);

            if (k > 20) k = 20;
            if (k > 0) damage = (25 - k) * damage / 25F;

            return damage;
        }
    }

    public static class DamageCalculationException extends Exception {}
    public static class TooManyCycles extends DamageCalculationException {}
}
