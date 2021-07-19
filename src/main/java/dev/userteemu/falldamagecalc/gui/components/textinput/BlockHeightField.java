package dev.userteemu.falldamagecalc.gui.components.textinput;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class BlockHeightField extends GuiTextField {
    private final String name;
    public int nameWidth;

    public BlockHeightField(int componentId, String name, String defaultValue, FontRenderer fontrendererObj, int x, int y, int width, int height) {
        super(componentId, fontrendererObj, x, y, width, height);
        this.name = name;
        this.nameWidth = fontrendererObj.getStringWidth(name);
        this.setMaxStringLength(10);
        this.setText(defaultValue);
    }

    @Override
    public void drawTextBox() {
        this.fontRendererInstance.drawString(name, this.xPosition + (this.width / 2) - (this.nameWidth / 2), this.yPosition - this.fontRendererInstance.FONT_HEIGHT - 8, 4210752);
        super.drawTextBox();
    }

    @Override
    public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_) {
        boolean result = super.textboxKeyTyped(p_146201_1_, p_146201_2_);
        if (result) {
            String text = this.getText();
            StringBuilder correctText = new StringBuilder();
            for (char c : text.toCharArray()) {
                if (Character.isDigit(c) || c == '.') correctText.append(c);
            }
            this.setText(correctText.toString());
        }
        return result;
    }

    public double getBlockY() {
        try {
            return Double.parseDouble(this.getText());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid block height, ID "+this.getId()+"! "+this.getText()+" is not a valid double value.");
        }
    }
}
