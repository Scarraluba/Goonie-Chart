package concrete.goonie.core;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.renders.axis.XAxis;
import concrete.goonie.core.renders.axis.YAxis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

public class Chart extends JPanel {

    private final ChartConfig config;
    private final ChartMouseHandler chartMouseHandler;
    private final YAxis yAxis = new YAxis();
    private XAxis xAxis = new XAxis();

    public Chart(ChartConfig config) {
        this.config = config;
        this.chartMouseHandler = new ChartMouseHandler(this);
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

        // Draw content with full transform
        g2d.transform(chartMouseHandler.getTransform());
        g2d.setColor(new Color(30, 144, 255));
        g2d.fillRect(1, 1, 100, 100);

        // Restore and draw Y axis
        g2d.setTransform(new AffineTransform());
        yAxis.draw((Graphics2D) g, chartMouseHandler.getYAxisTransform(), getWidth(), getHeight());

        // Draw X axis on top
        xAxis.draw((Graphics2D) g, chartMouseHandler.getXAxisTransform(), getWidth(), getHeight());
    }



}
