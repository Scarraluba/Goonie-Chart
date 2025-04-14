package concrete.goonie.core;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class ChartMouseHandler extends MouseAdapter implements MouseMotionListener, MouseWheelListener {
    private final Chart chartPanel;
    private AffineTransform transform;

    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private double translateX = 0;
    private double translateY = 0;

    private double width, height;

    // Mouse dragging state
    private boolean isDragging = false;
    private int lastX, lastY;

    private DragMode dragMode = DragMode.BOTH;

    private enum DragMode {
        NONE, HORIZONTAL, VERTICAL, BOTH
    }

    public ChartMouseHandler(Chart chartPanel) {
        this.chartPanel = chartPanel;
        this.transform = new AffineTransform();
        resetView();

        // Register MouseListeners
        chartPanel.addMouseMotionListener(this);
        chartPanel.addMouseListener(this);
        chartPanel.addMouseWheelListener(this);  // Ensure MouseWheelListener is added

    }


    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        determineCursorAndDragMode(x, y);
    }

    private void determineCursorAndDragMode(int x, int y) {
        boolean nearRight = x >= width - 35;
        boolean nearBottom = y >= height - 35;

        if (nearRight && nearBottom) {
            setDefaultCursor();
        } else if (nearRight) {
            setVerticalCursor();
        } else if (nearBottom) {
            setHorizontalCursor();
        } else {
            setMoveCursor();
        }
    }

    private void setDefaultCursor() {
        chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        dragMode = DragMode.NONE;
    }

    private void setVerticalCursor() {
        chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)); // vertical
        dragMode = DragMode.VERTICAL;
    }

    private void setHorizontalCursor() {
        chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)); // horizontal
        dragMode = DragMode.HORIZONTAL;
    }

    private void setMoveCursor() {
        chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)); // both
        dragMode = DragMode.BOTH;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (dragMode != DragMode.NONE) {
            isDragging = true;
            lastX = e.getX();
            lastY = e.getY();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!isDragging || dragMode == DragMode.NONE) return;

        int x = e.getX();
        int y = e.getY();
        int deltaX = x - lastX;
        int deltaY = y - lastY;

        handleDrag(deltaX, deltaY);

        lastX = x;
        lastY = y;

        updateTransform();
        chartPanel.repaint();
    }

    private void handleDrag(int deltaX, int deltaY) {
        switch (dragMode) {
            case HORIZONTAL -> translateX += deltaX;
            case VERTICAL -> translateY -= deltaY;
            case BOTH -> {
                translateX += deltaX;
                translateY -= deltaY;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isDragging = false;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        double zoomFactor = e.getWheelRotation() > 0 ? 0.9 : 1.1;

        boolean insideRight = mouseX <= width - 35;
        boolean insideBottom = mouseY <= height - 35;

        // Mode 1: Zoom both X and Y (near bottom-right)
        if (insideRight && insideBottom) {
            double flippedMouseY = height - mouseY;
            translateX -= (mouseX - translateX) * (zoomFactor - 1);
            translateY -= (flippedMouseY - translateY) * (zoomFactor - 1);
            scaleX *= zoomFactor;
            scaleY *= zoomFactor;
        }

        // Mode 2: X-axis only (near X edge)
        else if (insideRight && !insideBottom) {
            double anchorX = width - 50;
            translateX -= (anchorX - translateX) * (zoomFactor - 1);
            scaleX *= zoomFactor;
        }

        // Mode 3: Y-axis only (near Y edge)
        else if (!insideRight && insideBottom) {
            double flippedAnchorY = height - (height / 2); // center of screen in flipped Y
            translateY -= (flippedAnchorY - translateY) * (zoomFactor - 1);
            scaleY *= zoomFactor;
        }

        updateTransform();
        chartPanel.repaint();
    }


    public void resetView() {
        scaleX = 1.0;
        scaleY = 1.0;
        translateX = 0;
        translateY = 0;
        updateTransform();
        chartPanel.repaint();
    }

    public void setChartSize(double width, double height) {
        this.width = width;
        this.height = height;
        updateTransform();
    }

    private void updateTransform() {
        transform.setToIdentity();
        applyFlippingAndTranslation();
        applyZoom();
    }

    private void applyFlippingAndTranslation() {
        // Flip Y-axis and move origin to bottom-left
        transform.scale(1, -1);
        transform.translate(translateX, -height + translateY);
    }

    private void applyZoom() {
        // Apply zoom to the chart
        transform.scale(scaleX, scaleY);
    }

    private AffineTransform createAxisTransform(boolean isXAxis) {
        AffineTransform axisTransform = new AffineTransform();

        if (isXAxis) {
            axisTransform.translate(translateX, 0);  // allow horizontal panning
            axisTransform.scale(scaleX, 1);          // horizontal zoom
        } else {
            axisTransform.scale(1, -1);
            axisTransform.translate(0, -height + translateY);
            axisTransform.scale(1, scaleY);          // vertical zoom
        }

        return axisTransform;
    }

    public AffineTransform getTransform() {
        return new AffineTransform(transform);
    }

    public AffineTransform getXAxisTransform() {
        return createAxisTransform(true);
    }

    public AffineTransform getYAxisTransform() {
        return createAxisTransform(false);
    }

}
