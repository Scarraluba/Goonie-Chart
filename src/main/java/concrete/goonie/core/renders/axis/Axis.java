package concrete.goonie.core.renders.axis;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.renders.Renderer;

import java.awt.*;

/**
 * The {@code Axis} class is an abstract base class for chart axis renderers (e.g., X and Y axes).
 * It provides common configuration such as tick length, axis position, and chart settings.
 * <p>
 * Subclasses must implement the actual rendering logic by extending this class and
 * customizing axis-specific behaviors.
 *
 * @see AxisPosition
 * @see ChartConfig
 * @see concrete.goonie.core.renders.axis.XAxis
 * @see concrete.goonie.core.renders.axis.YAxis
 */
abstract class Axis extends Renderer {

    /**
     * The length of the axis ticks in pixels.
     */
    protected int tickLength = 5;

    /**
     * The position of the axis relative to the chart (e.g., LEFT, RIGHT, TOP, BOTTOM).
     */
    protected AxisPosition position = AxisPosition.RIGHT;

    /**
     * Chart configuration object containing theme, font, and color information.
     */
    protected ChartConfig config = new ChartConfig();

    /**
     * Sets the position of the axis.
     *
     * @param position the position of the axis (e.g., LEFT, RIGHT)
     */
    public void setPosition(AxisPosition position) {
        this.position = position;
    }

    /**
     * Returns the current position of the axis.
     *
     * @return the axis position
     */
    public AxisPosition getPosition() {
        return position;
    }

    /**
     * Sets the chart configuration used by the axis.
     *
     * @param config the {@link ChartConfig} instance containing style and rendering preferences
     */
    public void setConfig(ChartConfig config) {
        this.config = config;
    }
}
