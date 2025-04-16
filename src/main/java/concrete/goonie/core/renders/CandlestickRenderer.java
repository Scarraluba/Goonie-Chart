package concrete.goonie.core.renders;

import concrete.goonie.ChartConfig;

import java.awt.*;
import java.awt.geom.*;
import java.util.List;

public class CandlestickRenderer extends Renderer {
    private List<Candlestick> candles;
    private double screenCandleWidth = 6.0; // Desired width in screen pixels
    private Color upColor = new Color(0, 128, 0); // Green for up candles
    private Color downColor = new Color(255, 0, 0); // Red for down candles
    private Color wickColor = new Color(100, 100, 100); // Gray for wicks

    public CandlestickRenderer(ChartConfig config) {
        super(config);
    }

    @Override
    protected boolean contains(Point2D point) {
        return false;
    }
    @Override
    protected void move(double dx, double dy) {

    }
    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        int n = (int) (transform.getScaleX() - 1);
        int barWidth = (n % 2 == 0 ? n - 1 : n) - 2;
        if (barWidth < 3) barWidth = 3; // ensure visible bar

        for (int i = 0; i < 20; i++) {
            double xPos = i + 1.0;

            // Dummy OHLC data — you can customize this or pull from a real source
            double open = 30  ;
            double close = 100  ;
            double high = close + 20;
            double low = open - 10;

            Point2D openPoint = transform.transform(new Point2D.Double(xPos, open), null);
            Point2D closePoint = transform.transform(new Point2D.Double(xPos, close), null);
            Point2D highPoint = transform.transform(new Point2D.Double(xPos, high), null);
            Point2D lowPoint = transform.transform(new Point2D.Double(xPos, low), null);

            int x = (int) openPoint.getX();
            int yOpen = (int) openPoint.getY();
            int yClose = (int) closePoint.getY();
            int yHigh = (int) highPoint.getY();
            int yLow = (int) lowPoint.getY();

            int barHeight = Math.abs(yOpen - yClose);

            // Alternate colors for demo purposes
            if (close >= open) {
                g2d.setColor(Color.green);
            } else {
                g2d.setColor(Color.red);
            }

            g2d.drawLine(x, yHigh, x, yLow); // Wick
            g2d.fill(new Rectangle2D.Double(x - (barWidth / 2), Math.min(yOpen, yClose), barWidth, barHeight)); // Body
        }
    }

    private Rectangle2D calculateVisibleRect(AffineTransform transform, int width, int height) {
        try {
            AffineTransform inverse = transform.createInverse();
            Point2D left = inverse.transform(new Point(0, 0), null);
            Point2D right = inverse.transform(new Point(width, 0), null);
            return new Rectangle2D.Double(
                    left.getX() - 1, 0,  // -1 for margin
                    right.getX() - left.getX() + 2,  // +2 for margin
                    1
            );
        } catch (Exception e) {
            return new Rectangle2D.Double(0, 0, width, height);
        }
    }

    private double calculateCandleWidth(AffineTransform transform) {
        // Convert 6 screen pixels to data coordinates
        return 6.0 / transform.getScaleX();
    }

    private void drawCandle(Graphics2D g2d, int index, Candlestick candle, double width) {
        // Determine candle color
        Color candleColor = (candle.getClose() >= candle.getOpen()) ? upColor : downColor;
        g2d.setColor(candleColor);

        // Calculate candle coordinates
        double candleTop = Math.max(candle.getOpen(), candle.getClose());
        double candleBottom = Math.min(candle.getOpen(), candle.getClose());

        // Draw candle body
        Rectangle2D.Double body = new Rectangle2D.Double(
                index - width/2,
                candleBottom,
                width,
                candleTop - candleBottom
        );
        g2d.fill(body);

        // Draw candle wick
        g2d.setColor(wickColor);
        Line2D.Double wick = new Line2D.Double(
                index, candle.getHigh(),
                index, candle.getLow()
        );
        g2d.draw(wick);
    }
    // ... rest of the class remains the same ...

    // Candlestick data class
    public static class Candlestick {
        private final double open;
        private final double high;
        private final double low;
        private final double close;

        public Candlestick(double open, double high, double low, double close) {
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
        }

        public double getOpen() { return open; }
        public double getHigh() { return high; }
        public double getLow() { return low; }
        public double getClose() { return close; }

        @Override
        public String toString() {
            return "Candlestick{" +
                    "open=" + open +
                    ", high=" + high +
                    ", low=" + low +
                    ", close=" + close +
                    '}';
        }
    }
}