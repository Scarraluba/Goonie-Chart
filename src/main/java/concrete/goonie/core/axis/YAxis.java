package concrete.goonie.core.axis;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.Renderer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * The {@code YAxis} class is responsible for rendering the Y-axis of a chart,
 * including grid lines, tick marks, and value labels. It supports both left and right
 * positions and dynamically calculates suitable grid spacing based on the visible range.
 */
public class YAxis implements Renderer {

    private Point2D topLeft = new Point2D.Double();
    private Point2D bottomRight = new Point2D.Double();
    private DecimalFormat decimalFormat = new DecimalFormat("#0.0000");

    private double effectiveMin;
    private double effectiveMax;
    private double range;
    private double gridSpacing;
    private double startGrid;
    private double getRange = -1;

    // Changed to be based on pixel density rather than fixed counts
    private int minGridLines = 3;  // Minimum lines regardless of height
    private int pixelsPerGridLine = 50;  // Aim for one grid line every 50 pixels
    private int maxLabelWidth = 0;
    private int maxGridLines= 15;
    private int tickLength = 5;

    private ChartConfig config;
    private AxisPosition position = AxisPosition.RIGHT;

    public YAxis(ChartConfig config) {
        this.config=config;
    }

    /**
     * Draws the full Y-axis, including grid lines and labels.
     *
     * @param g2d       the graphics context
     * @param transform the current chart transformation
     * @param width     the total chart width
     * @param height    the total chart height
     * @return
     */

    public double draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        drawAxisLines(g2d, transform, width, height);
        drawAxisLabels(g2d, transform, width, height);
        return gridSpacing;
    }

    /**
     * Draws the value labels on the Y-axis.
     *
     * @param g2d       the graphics context
     * @param transform the current chart transformation
     * @param width     the chart width
     * @param height    the chart height
     */
    private void drawAxisLabels(Graphics2D g2d, AffineTransform transform, int width, int height) {
        g2d.setFont(config.getTextFont());
        g2d.setColor(config.getTextColor());

        // First pass to compute max label width
        maxLabelWidth = 0;
        for (double y = startGrid; y <= effectiveMax; y += gridSpacing) {
            String label = decimalFormat.format(y).replace(',', '.');
            int labelWidth = g2d.getFontMetrics().stringWidth(label);
            if (labelWidth > maxLabelWidth) {
                maxLabelWidth = labelWidth;
            }
        }
        maxLabelWidth += 5;

        int axisX = (position == AxisPosition.RIGHT) ? width - 1 : 0;
        int labelX = (position == AxisPosition.RIGHT) ? axisX - maxLabelWidth - 5 : axisX + 5;

        // Second pass to draw labels
        for (double y = startGrid; y <= effectiveMax; y += gridSpacing) {
            Point2D pt = new Point2D.Double(0, y);
            transform.transform(pt, pt);
            double screenY = pt.getY();

            if (screenY > 0 && screenY < height) {
                String label = decimalFormat.format(y).replace(',', '.');
                FontMetrics fm = g2d.getFontMetrics();
                int labelY = (int) screenY + fm.getAscent() / 4;

                if (position == AxisPosition.RIGHT) {
                    int labelWidth = fm.stringWidth(label);
                    g2d.drawString(label, labelX + (maxLabelWidth - labelWidth)-(config.getMarginRight()/3), labelY);
                } else {
                    g2d.drawString(label, labelX, labelY);
                }
            }
        }
    }

    /**
     * Draws axis line and horizontal grid lines for the Y-axis.
     *
     * @param g2d       the graphics context
     * @param transform the current chart transformation
     * @param width     the chart width
     * @param height    the chart height
     */
    private void drawAxisLines(Graphics2D g2d, AffineTransform transform, int width, int height) {
        int axisX = (position == AxisPosition.RIGHT) ? width - maxLabelWidth-(config.getMarginRight()) : 0;

        g2d.drawLine(axisX, 0, axisX, height);

        try {
            topLeft = transform.inverseTransform(new Point2D.Double(0, 0), null);
            bottomRight = transform.inverseTransform(new Point2D.Double(0, height), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        effectiveMin = Math.min(topLeft.getY(), bottomRight.getY());
        effectiveMax = Math.max(topLeft.getY(), bottomRight.getY());
        range = effectiveMax - effectiveMin;

        // Calculate desired number of grid lines based on height
        int desiredGridLines = Math.max(minGridLines, height / pixelsPerGridLine);

        if (getRange == -1) {
            // Calculate grid spacing based on desired number of lines
            gridSpacing = calculateGridSpacing(range, desiredGridLines);
        } else {
            gridSpacing = getRange;
        }

        startGrid = Math.floor(effectiveMin / gridSpacing) * gridSpacing;

        g2d.setColor(config.getGridColor());

        for (double y = startGrid; y <= effectiveMax; y += gridSpacing) {
            Point2D pt = new Point2D.Double(0, y);
            transform.transform(pt, pt);
            double screenY = pt.getY();

            if (screenY > 0 && screenY < height ) {
                g2d.drawLine(0, (int) screenY, width-config.getyPad(), (int) screenY);

                if (position == AxisPosition.RIGHT) {
                    g2d.drawLine(width - tickLength, (int) screenY, width, (int) screenY);
                } else {
                    g2d.drawLine(width, (int) screenY, width + tickLength, (int) screenY);
                }
            }
        }
        g2d.setColor(config.getBackgroundColor());
        g2d.fillRect(axisX,0,Math.abs(width-axisX)-tickLength,height);
        config.setyPad(Math.abs(width-axisX));
    }

    /**
     * Calculates grid spacing based on range and desired number of lines
     */
    private double calculateGridSpacing(double range, int desiredLines) {
        double rawSpacing = range / desiredLines;
        return findNiceInterval(rawSpacing);
    }

    /**
     * Finds a "nice" interval close to the raw spacing that's easy to read
     */
    private double findNiceInterval(double rawSpacing) {
        double exponent = Math.floor(Math.log10(rawSpacing));
        double fraction = rawSpacing / Math.pow(10, exponent);

        // Standard nice values: 1, 2, 2.5, 5, 10
        double[] niceFractions = {1.0, 2.0, 2.5, 5.0, 10.0};
        double niceFraction = niceFractions[0];

        for (double nf : niceFractions) {
            if (nf >= fraction) {
                niceFraction = nf;
                break;
            }
        }

        // If we didn't find a nice fraction larger than our raw spacing,
        // use the smallest nice fraction at the next exponent
        if (niceFraction < fraction) {
            exponent += 1;
            niceFraction = niceFractions[0];
        }

        return niceFraction * Math.pow(10, exponent);
    }
    /**
     * Determines an appropriate interval between Y-axis grid lines based on the visible range.
     *
     * @param range the current visible Y range
     * @return the best matching grid interval
     */
    private double getIntervals(double range) {
        double[] yIntervals = {
                0.0000001, 0.00000025, 0.0000005, 0.000001, 0.0000025, 0.000005, 0.00001, 0.000025, 0.00005, 0.0001,
                0.00025, 0.0005, 0.001, 0.0025, 0.005, 0.01, 0.025, 0.05, 0.1, 0.25, 0.5,
                1, 2.5, 5, 10, 20, 25, 50, 100, 200, 250, 500, 1000, 2000,
                2500, 5000, 10000, 25000, 50000, 100000
        };
        double closestInterval = yIntervals[0];
        double closestDifference = Double.MAX_VALUE;

        for (double interval : yIntervals) {
            double numberOfLines = range / interval;
            if (numberOfLines >= minGridLines && numberOfLines <= maxGridLines) {
                return interval;
            }
            double diff = Math.abs(numberOfLines - (minGridLines + maxGridLines) / 2.0);
            if (diff < closestDifference) {
                closestDifference = diff;
                closestInterval = interval;
            }
        }

        return closestInterval;
    }

    /**
     * Sets the number of decimal places used when formatting Y-axis labels.
     *
     * @param places the number of decimal digits
     */
    public void setDecimalPlaces(int places) {
        StringBuilder pattern = new StringBuilder("#0.");
        for (int i = 0; i < places; i++) {
            pattern.append("0");
        }
        decimalFormat = new DecimalFormat(pattern.toString());
    }


    public void setRange(double range) {
        this.getRange = range;
    }
}
