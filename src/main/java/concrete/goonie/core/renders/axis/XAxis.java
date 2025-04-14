package concrete.goonie.core.renders.axis;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

public class XAxis extends Axis {
    private final DecimalFormat labelFormat = new DecimalFormat("#.##");

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        g2d.setColor(axisColor);
        g2d.setFont(labelFont);

        // Decide where to place the axis (top or bottom)
        int axisY = (position == AxisPosition.TOP) ? 0 : height - 1;
        int tickYStart = (position == AxisPosition.TOP) ? axisY + tickLength : axisY - tickLength;
        int labelY = (position == AxisPosition.TOP) ? tickYStart + 12 : tickYStart - 2;

        // Draw fixed horizontal axis line
        g2d.drawLine(0, axisY, width, axisY);

        // Get visible X data range
        Point2D screenLeft = new Point2D.Double(0, 0);
        Point2D screenRight = new Point2D.Double(width, 0);
        try {
            transform.inverseTransform(screenLeft, screenLeft);
            transform.inverseTransform(screenRight, screenRight);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double minDataX = Math.min(screenLeft.getX(), screenRight.getX());
        double maxDataX = Math.max(screenLeft.getX(), screenRight.getX());

        // First tick
        double firstTick = Math.ceil(minDataX / tickSpacing) * tickSpacing;

        for (double xValue = firstTick; xValue <= maxDataX; xValue += tickSpacing) {
            Point2D tickPosData = new Point2D.Double(xValue, 0);
            try {
                transform.transform(tickPosData, tickPosData);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int screenX = (int) tickPosData.getX();

            // Draw tick
            g2d.drawLine(screenX, axisY, screenX, tickYStart);

            // Draw label
            String label = labelFormat.format(xValue);
            int strWidth = g2d.getFontMetrics().stringWidth(label);
            g2d.setColor(textColor);
            g2d.drawString(label, screenX - strWidth / 2, labelY);
            g2d.setColor(axisColor);
        }
    }

    @Override
    protected boolean contains(Point2D point) {
        return false;
    }

    @Override
    protected void move(double dx, double dy) {
        // Optional
    }
}
