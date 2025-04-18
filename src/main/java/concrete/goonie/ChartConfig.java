package concrete.goonie;

import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;

public class ChartConfig {
    // Colors
    private Color backgroundColor = Color.WHITE;
    private Color axisColor = Color.BLACK;
    private Color gridColor = new Color(220, 220, 220);
    private Color bullishColor = Color.GREEN;
    private Color bearishColor = Color.RED;
    private Color textColor = Color.BLACK;
    private Color movingAverageColor = Color.BLUE;
    private Color trendlineColor = Color.ORANGE;
    private Color crosshairColor = Color.GRAY;

    // Fonts
    private Font textFont = new Font("Arial", Font.PLAIN, 10);
    private LocalDateTime startDateTime = LocalDateTime.of(2024, 1, 1, 0, 0);
    // Margins
    private int marginTop = 30;
    private int marginBottom = 40;
    private int marginLeft = 80;
    private int marginRight = 16;

    // Display Options
    private boolean showGrid = true;
    private boolean showAxisLabels = true;
    private boolean showCrosshair = true;

    // Dimensions
    private int candleWidth = 5;
    private int wickWidth = 1;
    private double gridSpacing = 50;
    private int labelPadding = 5;

    // Chart Behavior
    private boolean enableAntiAliasing = true;
    private boolean enableZoom = true;
    private boolean enablePan = true;
    private boolean autoScaleY = true;

    // Precision & Format
    private String timeFormat = "HH:mm";
    private int pricePrecision = 2;
    private int maxVisibleBars = 500;

    private int   yPad=45;

    // Crosshair styling
    private Stroke crosshairStroke = new BasicStroke(
            1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL,
            1f, new float[]{4f}, 0f
    );

    // Theme
    public enum Theme {LIGHT, DARK}

    private Theme theme = Theme.LIGHT;

    // -------- Getters --------
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getAxisColor() {
        return axisColor;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public Color getBullishColor() {
        return bullishColor;
    }

    public Color getBearishColor() {
        return bearishColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getMovingAverageColor() {
        return movingAverageColor;
    }

    public Color getTrendlineColor() {
        return trendlineColor;
    }

    public Font getTextFont() {
        return textFont;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public boolean isShowAxisLabels() {
        return showAxisLabels;
    }

    public boolean isShowCrosshair() {
        return showCrosshair;
    }

    public int getCandleWidth() {
        return candleWidth;
    }

    public int getWickWidth() {
        return wickWidth;
    }

    public double getGridSpacing() {
        return gridSpacing;
    }

    public int getLabelPadding() {
        return labelPadding;
    }

    public boolean isEnableAntiAliasing() {
        return enableAntiAliasing;
    }

    public boolean isEnableZoom() {
        return enableZoom;
    }

    public boolean isEnablePan() {
        return enablePan;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public int getPricePrecision() {
        return pricePrecision;
    }

    public int getMaxVisibleBars() {
        return maxVisibleBars;
    }

    public Color getCrosshairColor() {
        return crosshairColor;
    }

    public Stroke getCrosshairStroke() {
        return crosshairStroke;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setAutoScaleY(boolean autoScaleY) {
        this.autoScaleY = autoScaleY;
    }


    public int getyPad() {
        return yPad;
    }

    public void setyPad(int yPad) {
        this.yPad = yPad;
    }

    // -------- Fluent Setters --------
    public ChartConfig setBackgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public ChartConfig setAxisColor(Color color) {
        this.axisColor = color;
        return this;
    }

    public ChartConfig setGridColor(Color color) {
        this.gridColor = color;
        return this;
    }

    public ChartConfig setBullishColor(Color color) {
        this.bullishColor = color;
        return this;
    }

    public ChartConfig setBearishColor(Color color) {
        this.bearishColor = color;
        return this;
    }

    public ChartConfig setTextColor(Color color) {
        this.textColor = color;
        return this;
    }

    public ChartConfig setMovingAverageColor(Color color) {
        this.movingAverageColor = color;
        return this;
    }

    public ChartConfig setTrendlineColor(Color color) {
        this.trendlineColor = color;
        return this;
    }

    public ChartConfig setTextFont(Font font) {
        this.textFont = font;
        return this;
    }

    public ChartConfig setMarginTop(int px) {
        this.marginTop = px;
        return this;
    }

    public ChartConfig setMarginBottom(int px) {
        this.marginBottom = px;
        return this;
    }

    public ChartConfig setMarginLeft(int px) {
        this.marginLeft = px;
        return this;
    }

    public ChartConfig setMarginRight(int px) {
        this.marginRight = px;
        return this;
    }

    public ChartConfig setShowGrid(boolean show) {
        this.showGrid = show;
        return this;
    }

    public ChartConfig setShowAxisLabels(boolean show) {
        this.showAxisLabels = show;
        return this;
    }

    public ChartConfig setShowCrosshair(boolean show) {
        this.showCrosshair = show;
        return this;
    }

    public ChartConfig setCandleWidth(int w) {
        this.candleWidth = w;
        return this;
    }

    public ChartConfig setWickWidth(int w) {
        this.wickWidth = w;
        return this;
    }

    public ChartConfig setGridSpacing(double spacing) {
        this.gridSpacing = spacing;
        return this;
    }

    public ChartConfig setLabelPadding(int px) {
        this.labelPadding = px;
        return this;
    }

    public ChartConfig setEnableAntiAliasing(boolean enabled) {
        this.enableAntiAliasing = enabled;
        return this;
    }

    public ChartConfig setEnableZoom(boolean zoom) {
        this.enableZoom = zoom;
        return this;
    }

    public ChartConfig setEnablePan(boolean pan) {
        this.enablePan = pan;
        return this;
    }

    public ChartConfig setTimeFormat(String format) {
        this.timeFormat = format;
        return this;
    }

    public ChartConfig setPricePrecision(int precision) {
        this.pricePrecision = precision;
        return this;
    }

    public ChartConfig setMaxVisibleBars(int maxBars) {
        this.maxVisibleBars = maxBars;
        return this;
    }

    public ChartConfig setCrosshairColor(Color color) {
        this.crosshairColor = color;
        return this;
    }

    public ChartConfig setCrosshairStroke(Stroke stroke) {
        this.crosshairStroke = stroke;
        return this;
    }
    public boolean isAutoScaleY() {
        return autoScaleY;
    }

    public ChartConfig setTheme(Theme theme) {
        this.theme = theme;
        return this;
    }
    public static Font getFont(int style,float size) {
        Font interFont = new Font("Arial",style, (int) size);

        try {
            if(Font.BOLD == style){
                interFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/Inter-Bold.ttf")).deriveFont(size);
            }
            else{
                interFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/Inter-Regular.ttf")).deriveFont(size);
            }

            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(interFont);
        } catch (Exception e) {
          //  System.out.println("Inter font not found, falling back to Roboto/Arial");
        }
        return interFont;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
}
