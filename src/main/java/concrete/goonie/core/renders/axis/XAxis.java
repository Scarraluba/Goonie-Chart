package concrete.goonie.core.renders.axis;

import concrete.goonie.core.ENUM_TIMEFRAME;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.time.*;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code XAxis} class is responsible for rendering the time-based horizontal axis of a chart.
 * It calculates and draws grid lines, time labels, and adjusts spacing dynamically based on the zoom level and timeframe.
 *
 * <p>This class supports caching mechanisms to improve performance during frequent repaint operations
 * and adapts to user interactions through transformations.</p>
 *
 * Example usage:
 * <pre>{@code
 * XAxis xAxis = new XAxis(ENUM_TIMEFRAME.PERIOD_M15);
 * xAxis.setStartDateTime(LocalDateTime.of(2024, 1, 1, 0, 0));
 * chart.addRenderer(xAxis);
 * }</pre>
 *
 * @see Axis
 * @see ENUM_TIMEFRAME
 */

public class XAxis extends Axis {

    private static final int MIN_GRID_LINES = 12;
    private static final int MAX_GRID_LINES = 20;
    private static final int MIN_PIXEL_SPACING = 50;

    // Cached calculations for performance optimization
    private double lastScale = -1;
    private double lastTranslateX = -1;
    private double lastWidth = -1;
    private double[] cachedGridPositions;
    private String[] cachedLabels;
    private Font[] cachedFonts;
    private int[] cachedLabelWidths;

    // Month abbreviations used in label formatting
    private static final Map<Integer, String> MONTH_ABBREV = new HashMap<>();
    static {
        MONTH_ABBREV.put(1, "Jan"); MONTH_ABBREV.put(2, "Feb"); MONTH_ABBREV.put(3, "Mar");
        MONTH_ABBREV.put(4, "Apr"); MONTH_ABBREV.put(5, "May"); MONTH_ABBREV.put(6, "Jun");
        MONTH_ABBREV.put(7, "Jul"); MONTH_ABBREV.put(8, "Aug"); MONTH_ABBREV.put(9, "Sep");
        MONTH_ABBREV.put(10, "Oct"); MONTH_ABBREV.put(11, "Nov"); MONTH_ABBREV.put(12, "Dec");
    }

    private LocalDateTime startDateTime = LocalDateTime.of(2024, 1, 1, 0, 0);
    private ENUM_TIMEFRAME timeframe;
    private double lastTransformHash = -1;
    private double previousGridSpacing;

    /**
     * Constructs a new XAxis instance for the given timeframe.
     *
     * @param timeframe The ENUM_TIMEFRAME that determines how time is mapped to X axis.
     */
    public XAxis(ENUM_TIMEFRAME timeframe) {
        this.timeframe = timeframe;
        previousGridSpacing = 80;
    }

    /**
     * Renders the X axis with grid lines and time-based labels.
     *
     * @param g2d       The graphics context used to draw.
     * @param transform The current affine transform for scaling and translating the data.
     * @param width     The width of the drawing area.
     * @param height    The height of the drawing area.
     */
    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        double currentTransformHash = transform.getTranslateX() + transform.getScaleX() + width;

        if (currentTransformHash != lastTransformHash) {
            calculateGridPositions(g2d, transform, width);
            lastTransformHash = currentTransformHash;
        }

        int axisY = (position == AxisPosition.TOP) ? 0 : height - 1;
        int tickYStart = (position == AxisPosition.TOP) ? axisY + tickLength : axisY - tickLength;
        int labelY = (position == AxisPosition.TOP) ? tickYStart + 12 : tickYStart - 2;

        g2d.setColor(config.getAxisColor());
        g2d.drawLine(0, axisY, width, axisY);


        for (int i = 0; i < cachedGridPositions.length; i++) {
            double screenX = cachedGridPositions[i];
            if (screenX < 0 || screenX > width) continue;

            g2d.setColor(config.getGridColor());
            g2d.drawLine((int) screenX, 0, (int) screenX, height);
            g2d.setColor(config.getAxisColor());
            g2d.drawLine((int) screenX, axisY, (int) screenX, tickYStart);

            g2d.setFont(cachedFonts[i]);
            g2d.setColor(config.getTextColor());
            g2d.drawString(cachedLabels[i], (int) screenX - cachedLabelWidths[i] / 2, labelY);
        }
    }

    /**
     * Calculates the visible grid positions and precomputes label data.
     *
     * @param g2d       The graphics context used to measure fonts.
     * @param transform The current transform.
     * @param width     The width of the drawing surface.
     */
    private void calculateGridPositions(Graphics2D g2d, AffineTransform transform, int width) {
        try {
            Point2D.Double leftData = (Point2D.Double) transform.inverseTransform(new Point2D.Double(0, 0), null);
            Point2D.Double rightData = (Point2D.Double) transform.inverseTransform(new Point2D.Double(width, 0), null);

            double minX = Math.min(leftData.x, rightData.x);
            double maxX = Math.max(leftData.x, rightData.x);

            double gridSpacing = calculateOptimalGridSpacing(minX, maxX, width);
            double firstGrid = Math.floor(minX / gridSpacing) * gridSpacing;

            int gridCount = (int) ((maxX - firstGrid) / gridSpacing) + 2;
            cachedGridPositions = new double[gridCount];
            cachedLabels = new String[gridCount];
            cachedFonts = new Font[gridCount];
            cachedLabelWidths = new int[gridCount];

            LocalDateTime prevDateTime = null;
            for (int i = 0; i < gridCount; i++) {
                double dataX = firstGrid + i * gridSpacing;
                Point2D.Double screenPoint = new Point2D.Double(dataX, 0);
                transform.transform(screenPoint, screenPoint);
                cachedGridPositions[i] = screenPoint.x;

                LocalDateTime currentDateTime = startDateTime.plus(timeframe.getDuration().multipliedBy((long) dataX));
                cachedLabels[i] = getLabel(currentDateTime, prevDateTime);
                cachedFonts[i] = getLabelFont(currentDateTime, prevDateTime);
                cachedLabelWidths[i] = g2d.getFontMetrics(cachedFonts[i]).stringWidth(cachedLabels[i]);
                prevDateTime = currentDateTime;
            }
        } catch (NoninvertibleTransformException e) {
            throw new RuntimeException("Failed to calculate grid positions", e);
        }
    }

    /**
     * Determines the best spacing between grid lines based on current view and constraints.
     */
    private double calculateOptimalGridSpacing(double minX, double maxX, int width) {
        double visibleRange = maxX - minX;
        double pixelsPerUnit = width / visibleRange;
        double minDataSpacing = MIN_PIXEL_SPACING / pixelsPerUnit;
        double minSeconds = minDataSpacing * timeframe.getDuration().getSeconds();

        int[] intervals = {1, 2, 5, 10, 15, 30, 60, 120, 300, 600, 900, 1800, 3600,
                7200, 14400, 21600, 43200, 86400, 172800, 604800, 2592000};

        double previousSeconds = previousGridSpacing * timeframe.getDuration().getSeconds();
        double gridCount = visibleRange / previousGridSpacing;

        if (previousSeconds >= minSeconds && gridCount >= MIN_GRID_LINES && gridCount <= MAX_GRID_LINES) {
            return previousGridSpacing;
        }

        double bestSpacing = previousGridSpacing;
        double bestSpacingDiff = Double.MAX_VALUE;

        for (int interval : intervals) {
            if (interval >= minSeconds) {
                double candidateSpacing = interval / timeframe.getDuration().getSeconds();
                double candidateGridCount = visibleRange / candidateSpacing;

                if (candidateGridCount >= MIN_GRID_LINES && candidateGridCount <= MAX_GRID_LINES) {
                    double diff = Math.abs(candidateSpacing - previousGridSpacing);
                    if (diff < bestSpacingDiff) {
                        bestSpacing = candidateSpacing;
                        bestSpacingDiff = diff;
                    }
                }
            }
        }

        previousGridSpacing = bestSpacing;
        return bestSpacing;
    }

    /**
     * Generates a human-readable label for the axis grid line based on time progression.
     */
    private String getLabel(LocalDateTime current, LocalDateTime previous) {
        if (previous == null || current.getYear() != previous.getYear()) {
            return String.valueOf(current.getYear());
        } else if (current.getMonth() != previous.getMonth()) {
            return MONTH_ABBREV.get(current.getMonthValue());
        } else if (current.getDayOfMonth() != previous.getDayOfMonth()) {
            return String.valueOf(current.getDayOfMonth());
        } else {
            switch (timeframe) {
                case PERIOD_M1:
                case PERIOD_M5:
                case PERIOD_M15:
                case PERIOD_M30:
                    return String.format("%02d:%02d", current.getHour(), current.getMinute());
                case PERIOD_H1:
                case PERIOD_H4:
                    return String.format("%02d:00", current.getHour());
                case PERIOD_D1:
                    return current.getDayOfMonth() + " " + MONTH_ABBREV.get(current.getMonthValue());
                case PERIOD_W1:
                    return "W" + current.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
                case PERIOD_MN1:
                    return MONTH_ABBREV.get(current.getMonthValue()) + " '" + (current.getYear() % 100);
                default:
                    return String.valueOf(current.getHour());
            }
        }
    }

    /**
     * Determines the font style used for each label based on its time significance.
     */
    private Font getLabelFont(LocalDateTime current, LocalDateTime previous) {
        if (previous == null || current.getYear() != previous.getYear()) {
            return new Font("SansSerif", Font.BOLD, 12);
        } else if (current.getMonth() != previous.getMonth()) {
            return new Font("SansSerif", Font.BOLD, 11);
        } else if (current.getDayOfMonth() != previous.getDayOfMonth()) {
            return new Font("SansSerif", Font.BOLD, 10);
        } else {
            return new Font("SansSerif", Font.PLAIN, 9);
        }
    }

    /**
     * Sets the start date and time of the chart.
     *
     * @param startDateTime The base date from which X-axis labels are calculated.
     */
    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
        lastTransformHash = -1; // Invalidate cache
    }

    /**
     * Sets the timeframe used for rendering time-based labels.
     *
     * @param timeframe The new timeframe.
     */
    public void setTimeframe(ENUM_TIMEFRAME timeframe) {
        this.timeframe = timeframe;
        lastTransformHash = -1; // Invalidate cache
    }

    @Override
    protected boolean contains(Point2D point) {
        return false;
    }

    @Override
    protected void move(double dx, double dy) {
        // Optional override
    }
}
