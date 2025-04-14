package concrete.goonie.core;

import java.awt.*;
import java.awt.geom.AffineTransform;

import concrete.goonie.*;

public interface IChartPanel {
    String getName(); // For identification

    int getPreferredHeight();

    boolean isVisible();

    void toggleVisibility(); // Collapse/Expand

    void draw(Graphics2D g2d, AffineTransform transform, int width, int height);
}
