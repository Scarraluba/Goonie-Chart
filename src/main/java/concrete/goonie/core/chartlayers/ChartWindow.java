package concrete.goonie.core.chartlayers;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.Renderer;
import concrete.goonie.core.ENUM_TIMEFRAME;
import concrete.goonie.core.axis.YAxis;

import javax.swing.*;
import java.awt.geom.AffineTransform;


public abstract class ChartWindow extends JPanel implements Renderer {
    protected ChartConfig config;
    protected ENUM_TIMEFRAME timeframe;
    protected AffineTransform transform;
    protected boolean selected;

    protected double chartHeight;
    protected double chartWidth;
    private final YAxis yAxis;

    public ChartWindow(ENUM_TIMEFRAME timeframe, ChartConfig config) {
        this.timeframe = timeframe;
        this.config = config;
        this.yAxis = new YAxis(config);
    }

    public void setTransform(AffineTransform transform) {
        this.transform = transform;
        repaint();
    }

    public AffineTransform getTransform() {
        return transform;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public void setChartHeight(double chartHeight) {
        this.chartHeight = chartHeight;
    }

    public void setChartWidth(double chartWidth) {
        this.chartWidth = chartWidth;
    }

    public void showGui() {

    }
    public void hideGui() {

    }
}
