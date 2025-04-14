package concrete.goonie.core.renders.axis;

import concrete.goonie.core.renders.Renderer;

import java.awt.*;

public abstract class Axis extends Renderer {
    protected int tickLength = 5;
    protected int tickSpacing = 50;
    protected Color axisColor = Color.BLACK;
    protected Color textColor = Color.DARK_GRAY;
    protected Font labelFont = new Font("Arial", Font.PLAIN, 10);
    protected AxisPosition position = AxisPosition.RIGHT; // Default

    public void setTickSpacing(int spacing) {
        this.tickSpacing = spacing;
    }

    public void setAxisColor(Color axisColor) {
        this.axisColor = axisColor;
    }

    public void setLabelFont(Font font) {
        this.labelFont = font;
    }

    public void setPosition(AxisPosition position) {
        this.position = position;
    }

    public AxisPosition getPosition() {
        return position;
    }

}
