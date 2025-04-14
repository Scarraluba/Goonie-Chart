package concrete.goonie.core.renders;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public abstract class Renderer {

    protected abstract boolean contains(Point2D point);

    protected abstract void draw(Graphics2D g2d, AffineTransform transform, int width, int height);

    protected abstract void move(double dx, double dy);

}
