package dev.userteemu.falldamagecalc;

import dev.userteemu.falldamagecalc.gui.GuiDamageCalculator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

@Mod(modid = FallDamageCalculatorMod.MODID, name = FallDamageCalculatorMod.NAME, version = FallDamageCalculatorMod.VERSION, clientSideOnly = true)
public class FallDamageCalculatorMod {
    public static final String MODID = "falldamagecalculator";
    public static final String NAME = "FallDamageCalculator";
    public static final String VERSION = "@MOD_VERSION@";

    @Mod.Instance(MODID)
    public static FallDamageCalculatorMod INSTANCE;

    public KeyBinding guiOpenKeybind;

    public FallDamageCalculatorMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void printMessage(String message) {
        ChatComponentTranslation translation = new ChatComponentTranslation("falldamagecalc.title");
        ChatComponentText separator = new ChatComponentText(" > ");
        translation.appendSibling(separator);
        translation.setChatStyle(translation.getChatStyle().setColor(EnumChatFormatting.RED).setBold(true).setItalic(false));

        translation.appendSibling(new ChatComponentTranslation(message));
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(translation);
    }

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        ClientRegistry.registerKeyBinding(guiOpenKeybind = new KeyBinding("falldamagecalc.title", 0, "key.categories.gameplay"));
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (this.guiOpenKeybind.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiDamageCalculator());
        }
    }

    public static void drawCenteredStringWithoutShadow(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawString(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
    }

    public static void drawColoredRect(int left, int top, int right, int bottom, int color, double zLevel) {
        float a = (float)(color >> 24 & 255) / 255F;
        float r = (float)(color >> 16 & 255) / 255F;
        float g = (float)(color >> 8 & 255) / 255F;
        float b = (float)(color & 255) / 255F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, zLevel).color(r, g, b, a).endVertex();
        worldrenderer.pos(left, top, zLevel).color(r, g, b, a).endVertex();
        worldrenderer.pos(left, bottom, zLevel).color(r, g, b, a).endVertex();
        worldrenderer.pos(right, bottom, zLevel).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}