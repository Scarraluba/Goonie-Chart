package concrete.goonie.core.datarenderers;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.ChartMouseHandler;
import concrete.goonie.core.Renderer;
import concrete.goonie.datatypes.Candlestick;

import java.awt.*;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class CandlestickRenderer extends Renderer {
    private List<Candlestick> candles; // Store the candlestick data
    private ChartMouseHandler mouseHandler;

    public CandlestickRenderer(ChartConfig config, ChartMouseHandler mouseHandler) {
        super(config);
        this.mouseHandler = mouseHandler;
        this.candles = new ArrayList<>();  // Initialize the list
        loadCandlestickData(); // Load data into the list
    }

    // Method to load candlestick data (can be dynamically fetched or set)
    private void loadCandlestickData() {

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            // Load the file from resources
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Boom.csv");

            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + "Boom.csv");
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                // Skip the header if it exists
                br.readLine(); // Assuming the first line is the header

                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        String[] values = line.split("\t");

                        // Parse date and time
                        LocalDate date = LocalDate.parse(values[0], dateFormatter);
                        LocalTime time = (values.length > 8) ? LocalTime.parse(values[1], timeFormatter) : LocalTime.of(0, 0, 0);
                        LocalDateTime dateTime = LocalDateTime.of(date, time);

                        // Parse other fields
                        double open = Double.parseDouble(values[values.length > 8 ? 2 : 1]);
                        double high = Double.parseDouble(values[values.length > 8 ? 3 : 2]);
                        double low = Double.parseDouble(values[values.length > 8 ? 4 : 3]);
                        double close = Double.parseDouble(values[values.length > 8 ? 5 : 4]);
                        int tickVol = Integer.parseInt(values[values.length > 8 ? 6 : 5]);
                        int volume = Integer.parseInt(values[values.length > 8 ? 7 : 6]);
                        int spread = Integer.parseInt(values[values.length > 8 ? 8 : 7]);

                        // Create a Candle object and add it to the list
                        Candlestick candle = new Candlestick( open, high, low, close);

                        candles.add(candle);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        System.err.println("Error parsing line: " + line);
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.err.println("LOADED");



    }

    @Override
    protected boolean contains(Point2D point) {
        return false;
    }

    @Override
    protected void move(double dx, double dy) {
        // Handle movement if necessary (currently empty)
    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        Double minY = null;
        Double maxY = null;

        int n = (int) (transform.getScaleX() - 1);
        int barWidth = (n % 2 == 0 ? n - 1 : n) - 2;
        if (barWidth < 3) barWidth = 3;

        for (int i = 0; i < candles.size(); i++) {
            double xPos = i + 1.0;
            Candlestick candle = candles.get(i);

            double open = candle.getOpen();
            double close = candle.getClose();
            double high = candle.getHigh();
            double low = candle.getLow();

            Point2D openPoint = transform.transform(new Point2D.Double(xPos, open), null);
            Point2D closePoint = transform.transform(new Point2D.Double(xPos, close), null);
            Point2D highPoint = transform.transform(new Point2D.Double(xPos, high), null);
            Point2D lowPoint = transform.transform(new Point2D.Double(xPos, low), null);

            int x = (int) openPoint.getX();
            int yOpen = (int) openPoint.getY();
            int yClose = (int) closePoint.getY();
            int yHigh = (int) highPoint.getY();
            int yLow = (int) lowPoint.getY();

            if (x + barWidth / 2 < 0 || x - barWidth / 2 > width-config.getyPad() || yHigh > height || yLow < 0) {
                continue;
            }

            // First visible candle sets the base range
            if (minY == null || maxY == null) {
                minY = low;
                maxY = high;
            } else {
                minY = Math.min(minY, low);
                maxY = Math.max(maxY, high);
            }

            int barHeight = Math.abs(yOpen - yClose);
            g2d.setColor(close >= open ? config.getBullishColor(): config.getBearishColor());
            g2d.drawLine(x, yHigh, x, yLow);
            g2d.fill(new Rectangle2D.Double(x - (barWidth / 2), Math.min(yOpen, yClose), barWidth, barHeight));
        }

        if (minY != null && maxY != null) {

            mouseHandler.updateVisibleYRange(minY, maxY);
        }
    }

    // Candlestick data class


}
