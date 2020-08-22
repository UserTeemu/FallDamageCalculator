package io.github.tivj.falldamagecalc.gui.components.textinput;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class NumberOnlyTextField extends GuiTextField {
    public static int numberTextFieldWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth("00000");

    public NumberOnlyTextField(int componentId, String defaultValue, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, int maxLength) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
        this.setMaxStringLength(maxLength);
        this.setText(defaultValue);
    }

    public int getValue() {
        try {
            return Integer.parseInt(this.getText());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid text in numbers only text field, ID "+this.getId()+"! "+this.getText()+" is not a valid integer value.");
        }
    }

    @Override
    public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_) {
        boolean result = super.textboxKeyTyped(p_146201_1_, p_146201_2_);
        if (result) {
            String text = this.getText();
            StringBuilder correctText = new StringBuilder();
            for (char c : text.toCharArray()) {
                if (isCharValid(c)) correctText.append(c);
            }
            this.setText(correctText.toString());
        }
        return result;
    }

    protected boolean isCharValid(char c) {
        return Character.isDigit(c);
    }
}
