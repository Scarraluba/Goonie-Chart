package concrete.goonie;

import concrete.goonie.core.ChartMouseHandler;
import concrete.goonie.core.ENUM_TIMEFRAME;
import concrete.goonie.core.datarenderers.CandlestickRenderer;
import concrete.goonie.core.axis.XAxis;
import concrete.goonie.core.axis.YAxis;


import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

public class Chart extends JPanel {

    private final ChartConfig config;
    private final ChartMouseHandler chartMouseHandler;
    private final CandlestickRenderer candleRenderer;
    private final YAxis yAxis;
    private final XAxis xAxis;

    public Chart(ChartConfig config) {
        this.config = config;
        this.chartMouseHandler = new ChartMouseHandler(this);
        this.xAxis = new XAxis(ENUM_TIMEFRAME.PERIOD_H1, config);
        this.yAxis = new YAxis(config);
        this.candleRenderer = new CandlestickRenderer(config, chartMouseHandler);

        init();

    }

    private void init() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension panelSize = getSize();
                chartMouseHandler.setChartSize(panelSize.width, panelSize.height);
            }
        });
        setBackground(config.getBackgroundColor());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Get the current transform once
        AffineTransform chartTransform = chartMouseHandler.getTransform();
        // Restore and draw axes
        g2d.setTransform(new AffineTransform());
        yAxis.draw(g2d, chartMouseHandler.getYAxisTransform(), getWidth(), getHeight());
        xAxis.draw(g2d, chartMouseHandler.getXAxisTransform(), getWidth(), getHeight());

        // Draw candles with the proper transform
        candleRenderer.draw(g2d, chartTransform, getWidth(), getHeight());

    }

    public ChartConfig getConfig() {
        return config;
    }
}
