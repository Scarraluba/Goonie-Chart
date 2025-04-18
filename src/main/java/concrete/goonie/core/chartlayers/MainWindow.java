package concrete.goonie.core.chartlayers;

import concrete.goonie.ChartConfig;

import concrete.goonie.core.axis.AxisUtils;
import concrete.goonie.core.renderers.CandlestickRenderer;
import concrete.goonie.core.ENUM_TIMEFRAME;
import concrete.goonie.core.axis.YAxis;

import java.awt.*;

public class MainWindow extends ChartWindow {
    private final YAxis yAxis;
    private CandlestickRenderer candleRenderer;

    public MainWindow(ENUM_TIMEFRAME timeframe, ChartConfig config) {
        super(timeframe, config);
        this.yAxis = new YAxis(config);
        this.candleRenderer = new CandlestickRenderer(config);
        setBackground(config.getBackgroundColor());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call parent's paintComponent first

        Graphics2D g2d = (Graphics2D) g;
        if (transform == null) {
            System.out.println("transform == null");
            ;
        }
        AxisUtils.drawGridLines(g2d, transform, getWidth(), getHeight(),
                config, config.getStartDateTime(), timeframe);
        yAxis.draw(g2d, transform, getWidth(), getHeight());
        candleRenderer.draw(g2d, transform, getWidth(), getHeight());
    }

}