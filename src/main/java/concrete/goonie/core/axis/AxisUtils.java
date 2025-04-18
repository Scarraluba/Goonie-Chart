package concrete.goonie.core.axis;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.ENUM_TIMEFRAME;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

import static concrete.goonie.core.axis.Axis.MONTH_ABBREV;


public class AxisUtils {
    private static final int MIN_GRID_LINES = 12;
    private static final int MAX_GRID_LINES = 20;
    private static final int MIN_PIXEL_SPACING = 50;
    private static double lastGridSpacing = 80.0;
    public static void drawGridLines(Graphics2D g2d, AffineTransform transform,
                                   int width, int height, ChartConfig config,
                                   LocalDateTime startDateTime, ENUM_TIMEFRAME timeframe) {
        try {
            // Calculate visible range
            Point2D.Double leftData = transformPoint(0, 0, transform, true);
            Point2D.Double rightData = transformPoint(width, 0, transform, true);
            
            double minX = Math.min(leftData.x, rightData.x);
            double maxX = Math.max(leftData.x, rightData.x);
            
            // Calculate grid spacing
            double gridSpacing = calculateOptimalGridSpacing(minX, maxX, width, timeframe);
            double firstGrid = Math.floor(minX / gridSpacing) * gridSpacing;
            
            int gridCount = (int) ((maxX - firstGrid) / gridSpacing) + 2;
            
            // Draw grid lines
            for (int i = 0; i < gridCount; i++) {
                double dataX = firstGrid + i * gridSpacing;
                Point2D.Double screenPoint = transformPoint(dataX, 0, transform, false);
                
                if (screenPoint.x < 0 || screenPoint.x > width - config.getyPad()) {
                    continue;
                }
                
                g2d.setColor(config.getGridColor());
                g2d.drawLine((int) screenPoint.x, 0, (int) screenPoint.x, height);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void drawXAxisLabels(Graphics2D g2d, AffineTransform transform,
                                      int width, int height, ChartConfig config,
                                      LocalDateTime startDateTime, ENUM_TIMEFRAME timeframe,
                                      AxisPosition position, int tickLength) {
        try {
            // Calculate visible range
            Point2D.Double leftData = transformPoint(0, 0, transform, true);
            Point2D.Double rightData = transformPoint(width, 0, transform, true);
            
            double minX = Math.min(leftData.x, rightData.x);
            double maxX = Math.max(leftData.x, rightData.x);
            
            // Calculate grid spacing
            double gridSpacing = calculateOptimalGridSpacing(minX, maxX, width, timeframe);
            double firstGrid = Math.floor(minX / gridSpacing) * gridSpacing;
            
            int gridCount = (int) ((maxX - firstGrid) / gridSpacing) + 2;
            
            // Calculate positions
            int axisY =  config.getMarginBottom();
            int tickYStart = (position == AxisPosition.TOP) ? axisY + tickLength : axisY - tickLength;
            int labelY = (position == AxisPosition.TOP) ? tickYStart + 12 : tickYStart - (config.getMarginBottom() / 3);
            
            // Draw ticks and labels
            LocalDateTime prevDateTime = null;
            for (int i = 0; i < gridCount; i++) {
                double dataX = firstGrid + i * gridSpacing;
                Point2D.Double screenPoint = transformPoint(dataX, 0, transform, false);
                
                if (screenPoint.x < 0 || screenPoint.x > width - config.getyPad()) {
                    continue;
                }
                
                // Draw tick
                g2d.setColor(config.getGridColor());
                g2d.drawLine((int) screenPoint.x, axisY, (int) screenPoint.x, tickYStart);
                
                // Draw label
                LocalDateTime currentDateTime = startDateTime.plus(timeframe.getDuration().multipliedBy((long) dataX));
                String label = getLabel(currentDateTime, prevDateTime, timeframe);
                Font font = getLabelFont(currentDateTime, prevDateTime, config);
                
                g2d.setFont(font);
                g2d.setColor(config.getTextColor());
                int labelWidth = g2d.getFontMetrics().stringWidth(label);
                g2d.drawString(label, (int) screenPoint.x - labelWidth / 2, labelY);
                
                prevDateTime = currentDateTime;
            }
            
            // Draw the main axis line
            g2d.setColor(config.getAxisColor());
            g2d.drawLine(0, axisY, width, axisY);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Point2D.Double transformPoint(double x, double y, AffineTransform transform, boolean inverse) {
        Point2D.Double point = new Point2D.Double(x, y);
        try {
            if (inverse) {
                transform.inverseTransform(point, point);
            } else {
                transform.transform(point, point);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return point;
    }

    public static double calculateOptimalGridSpacing(double minX, double maxX, int width, ENUM_TIMEFRAME timeframe) {
        double visibleRange = maxX - minX;
        double pixelsPerUnit = width / visibleRange;
        double minDataSpacing = MIN_PIXEL_SPACING / pixelsPerUnit;
        double minSeconds = minDataSpacing * timeframe.getDuration().getSeconds();

        int[] intervals = {
                1, 2, 5, 10, 15, 30, 60, 120, 300, 600, 900, 1800, 3600,
                7200, 14400, 21600, 43200, 86400, 172800, 604800, 2592000,
                7776000, 15552000, 31536000, 63072000, 94608000, 126144000,
                157680000, 189216000, 220752000, 252288000, 283824000, 315360000
        };

        double fallback = lastGridSpacing;
        double bestSpacing = -1;
        double bestSpacingDiff = Double.MAX_VALUE;

        for (int interval : intervals) {
            if (interval >= minSeconds) {
                double candidateSpacing = interval / timeframe.getDuration().getSeconds();
                double candidateGridCount = visibleRange / candidateSpacing;

                if (candidateGridCount >= MIN_GRID_LINES && candidateGridCount <= MAX_GRID_LINES) {
                    double diff = Math.abs(candidateSpacing - fallback);
                    if (diff < bestSpacingDiff) {
                        bestSpacing = candidateSpacing;
                        bestSpacingDiff = diff;
                    }
                }
            }
        }

        if (bestSpacing > 0) {
            lastGridSpacing = bestSpacing;
            return bestSpacing;
        }

        return fallback;
    }
    private static String getLabel(LocalDateTime current, LocalDateTime previous, ENUM_TIMEFRAME timeframe) {
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
    
    private static Font getLabelFont(LocalDateTime current, LocalDateTime previous, ChartConfig config) {
        if (previous == null || current.getYear() != previous.getYear()) {
            return config.getFont(Font.BOLD, 15);
        } else if (current.getMonth() != previous.getMonth()) {
            return config.getFont(Font.BOLD, 14);
        } else if (current.getDayOfMonth() != previous.getDayOfMonth()) {
            return config.getFont(Font.BOLD, 14);
        } else {
            return config.getFont(Font.PLAIN, 12);
        }
    }
}