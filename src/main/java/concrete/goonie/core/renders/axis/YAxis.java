package concrete.goonie.core.renders.axis;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import concrete.goonie.core.renders.axis.AxisPosition; // your enum

public class YAxis extends Axis {
    private final DecimalFormat labelFormat = new DecimalFormat("#.##");

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        // Set basic drawing properties.
        g2d.setColor(axisColor);
        g2d.setFont(labelFont);

        // Determine fixed horizontal position for the axis line.
        int axisX = (position == AxisPosition.RIGHT) ? width - 1 : 0;
        int tickXStart = (position == AxisPosition.RIGHT) ? axisX - tickLength : axisX + tickLength;
        int labelX = (position == AxisPosition.RIGHT) ? tickXStart - 35 : tickXStart + 5;

        // 1. Draw the fixed vertical axis line along the right edge.
        g2d.drawLine(axisX, 0, axisX, height);

        // 2. Compute the visible data range along Y.
        // Get the top and bottom of the screen in data coordinates.
        Point2D screenTop = new Point2D.Double(0, 0);
        Point2D screenBottom = new Point2D.Double(0, height);
        try {
            transform.inverseTransform(screenTop, screenTop);
            transform.inverseTransform(screenBottom, screenBottom);
        } catch (Exception e) {
            e.printStackTrace();
        }
        double minDataY = Math.min(screenTop.getY(), screenBottom.getY());
        double maxDataY = Math.max(screenTop.getY(), screenBottom.getY());

        // 3. Compute the first tick value based on tickSpacing.
        // (tickSpacing is in data units for the Y axis)
        double firstTick = Math.ceil(minDataY / tickSpacing) * tickSpacing;

        // 4. Loop over the visible Y data range and draw ticks.
        for (double yValue = firstTick; yValue <= maxDataY; yValue += tickSpacing) {
            // Compute the tick's screen position by transforming the data Y value.
            Point2D tickPosData = new Point2D.Double(0, yValue);
            try {
                transform.transform(tickPosData, tickPosData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int screenY = (int) tickPosData.getY();

            // Draw tick mark.
            if (position == AxisPosition.RIGHT) {
                g2d.drawLine(tickXStart, screenY, axisX, screenY);
            } else {
                g2d.drawLine(axisX, screenY, tickXStart, screenY);
            }

            // Draw label for the tick.
            String label = labelFormat.format(yValue);
            g2d.setColor(textColor);
            g2d.drawString(label, labelX, screenY + 4); // You can adjust the vertical offset as needed
            g2d.setColor(axisColor);
        }
    }

    @Override
    protected boolean contains(Point2D point) {
        return false;
    }

    @Override
    protected void move(double dx, double dy) {
        // Implementation if needed
    }
}
