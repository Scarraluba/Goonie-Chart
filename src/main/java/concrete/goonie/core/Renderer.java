package concrete.goonie.core;

import concrete.goonie.ChartConfig;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public abstract class Renderer {

    /**
     * Chart configuration object containing theme, font, and color information.
     */
    protected ChartConfig config;

    protected Renderer(ChartConfig config) {
        this.config = config;
    }

    protected abstract boolean contains(Point2D point);

    protected abstract void draw(Graphics2D g2d, AffineTransform transform, int width, int height);

    protected abstract void move(double dx, double dy);

    public void autoScaleYAxis(ChartMouseHandler mouseHandler, int width, int height) {

    }
}
