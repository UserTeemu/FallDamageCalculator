package dev.userteemu.falldamagecalc.gui.components.textinput;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class ShortOnlyTextField extends GuiTextField {
    public ShortOnlyTextField(int componentId, String defaultValue, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
        this.setMaxStringLength(4);
        this.setText(defaultValue);
    }

    public short getValue() {
        try {
            return Short.parseShort(this.getText());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid text in numbers only text field, ID "+this.getId()+"! "+this.getText()+" is not a valid short value.");
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

            if (correctText.length() > 0) {
                long val = Integer.parseInt(correctText.toString());
                if (val > Short.MIN_VALUE) {
                    if (val < Short.MAX_VALUE) this.setText(correctText.toString());
                    else this.setText(String.valueOf(Short.MAX_VALUE));
                } else this.setText(String.valueOf(Short.MIN_VALUE));
            }
        }
        return result;
    }

    protected boolean isCharValid(char c) {
        return Character.isDigit(c);
    }
}
