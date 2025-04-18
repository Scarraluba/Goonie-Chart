package concrete.goonie.core;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.axis.XAxis;
import concrete.goonie.core.chartlayers.ChartWindow;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartMouseHandler extends MouseAdapter implements MouseMotionListener, MouseWheelListener, KeyListener {

    private final List<ChartWindow> panes;
    private final Chart chart;
    private final AffineTransform transform;
    private double width, height;
    private int lastX, lastY;
    private boolean isDragging = false;


    private final Map<ChartWindow, Double> paneTranslateY = new HashMap<>();
    private final Map<ChartWindow, Double> paneScaleY = new HashMap<>();
    private ChartWindow focusedPane;

    private DragMode dragMode = DragMode.BOTH;
    private ChartConfig config;

    private double scaleX = 19.0;
    private double scaleY = 19.0;
    private double translateX = 0;
    private double translateY = 0;

    public enum DragMode {
        NONE, HORIZONTAL, VERTICAL, BOTH
    }

    public ChartMouseHandler(ChartConfig config, List<ChartWindow> panes, Chart chart) {
        this.panes = panes;
        this.chart = chart;
        this.config = config;
        transform = new AffineTransform();
        initializePaneTransforms();
        resetView();
    }

    private void initializePaneTransforms() {
        paneTranslateY.clear();
        paneScaleY.clear();
        panes.forEach(pane -> {
            paneTranslateY.put(pane, 0.0);
            paneScaleY.put(pane, 19.0);
        });
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        focusedPane = (ChartWindow) e.getComponent();

        determineCursorAndDragMode(chart.getMousePosition().x, chart.getMousePosition().y);
        Point mousePosition = focusedPane.getMousePosition();

       if (mousePosition == null) return;
        focusedPane.showGui();

    }

    @Override
    public void mousePressed(MouseEvent e) {
        focusedPane = (ChartWindow) e.getComponent();
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
        updateAllPanels();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Only proceed if the event source is a ChartWindow we're tracking
        if (!(e.getComponent() instanceof ChartWindow)) {
            return;
        }

        ChartWindow exitedPane = (ChartWindow) e.getComponent();

        // Verify this pane is actually in our list of managed panes
        if (!panes.contains(exitedPane)) {
            return;
        }

        // Check if mouse really left the component (not just moved to a child component)
        Point mousePos = exitedPane.getMousePosition();
        if (mousePos != null &&
                mousePos.x >= 0 && mousePos.x < exitedPane.getWidth() &&
                mousePos.y >= 0 && mousePos.y < exitedPane.getHeight()) {
            // Mouse is still within the component bounds
            return;
        }

        // Only hide GUI if this was the focused pane
        if (exitedPane.equals(focusedPane)) {
            exitedPane.hideGui();
            focusedPane = null;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isDragging = false;

        focusedPane = null;

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        focusedPane = (ChartWindow) e.getComponent();
        Point mousePosition = focusedPane.getMousePosition();
        if (mousePosition == null) return;

        int mouseX = mousePosition.x;
        int mouseY = mousePosition.y;

        double zoomFactor = e.getWheelRotation() > 0 ? 0.9 : 1.1;
        boolean ctrlDown = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
        ChartConfig config = chart.getConfig();

        boolean insideRight = mouseX <= width - config.getyPad();
        boolean insideBottom = mouseY <= height - config.getMarginBottom();

        if (ctrlDown) {
            // X-axis only zoom
            translateX -= (mouseX - translateX) * (zoomFactor - 1);
            scaleX *= zoomFactor;
        } else if (insideRight && insideBottom) {
            // Y-axis zoom for focused pane only
            double flippedMouseY = height - mouseY;
            double currentTranslateY = paneTranslateY.getOrDefault(focusedPane, 0.0);
            double currentScaleY = paneScaleY.getOrDefault(focusedPane, 19.0);

            // Apply zoom centered at mouse position
            paneTranslateY.put(focusedPane, currentTranslateY - (flippedMouseY - currentTranslateY) * (zoomFactor - 1));
            paneScaleY.put(focusedPane, currentScaleY * zoomFactor);

            // X-axis is still shared
            translateX -= (mouseX - translateX) * (zoomFactor - 1);
            scaleX *= zoomFactor;
        } else if (insideRight) {
            // X-axis only
            translateX -= (mouseX - translateX) * (zoomFactor - 1);
            scaleX *= zoomFactor;
        } else if (insideBottom) {
            // Y-axis only for focused pane
            double flippedMouseY = height - mouseY;
            double currentTranslateY = paneTranslateY.getOrDefault(focusedPane, 0.0);
            double currentScaleY = paneScaleY.getOrDefault(focusedPane, 19.0);

            paneTranslateY.put(focusedPane, currentTranslateY - (flippedMouseY - currentTranslateY) * (zoomFactor - 1));
            paneScaleY.put(focusedPane, currentScaleY * zoomFactor);
        }

        updateAllPanels();
    }
    private void handleDrag(int deltaX, int deltaY) {
        // Always apply X-axis changes to all panes
        translateX += deltaX;

        // Apply Y-axis changes only to the focused pane
        if (focusedPane != null && (dragMode == DragMode.VERTICAL || dragMode == DragMode.BOTH)) {
            double currentTranslateY = paneTranslateY.getOrDefault(focusedPane, 0.0);
            paneTranslateY.put(focusedPane, currentTranslateY - deltaY);
        }

        updateAllPanels();
    }
    private void updateAllPanels() {
        for (ChartWindow pane : panes) {
            AffineTransform paneTransform = new AffineTransform();

            // Shared X-axis components
            paneTransform.scale(1, -1);  // Flip Y-axis

            // Individual Y translation
            double yTranslate = paneTranslateY.getOrDefault(pane, 0.0);
            paneTransform.translate(translateX, -height + yTranslate);

            // Individual Y scale
            double yScale = paneScaleY.getOrDefault(pane, 19.0);
            paneTransform.scale(scaleX, yScale);

            if (pane instanceof XAxis) {
                pane.setTransform(getXAxisTransform());
            } else {
                pane.setTransform(paneTransform);
            }

            pane.setChartHeight(height);
            pane.setChartWidth(width);
        }
    }

    public void resetView() {
        scaleX = 20;
        translateX = 0;

        // Reset all panes' Y-axis transforms
        panes.forEach(pane -> {
            paneTranslateY.put(pane, 0.0);
            paneScaleY.put(pane, 14.0);
        });

        updateAllPanels();
    }

    private void updateTransform() {
        transform.setToIdentity();
        applyFlippingAndTranslation();
        applyZoom();
    }

    private void applyFlippingAndTranslation() {
        transform.scale(1, -1);  // Flip the Y-axis
        transform.translate(translateX, -height + translateY);  // Apply translation
    }

    private void applyZoom() {
        transform.scale(scaleX, scaleY);
    }

    private void determineCursorAndDragMode(int x, int y) {
        boolean nearRight = x >= width - chart.getConfig().getyPad();
        boolean nearBottom = y >= height - chart.getConfig().getMarginBottom();

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
        panes.forEach(panel -> panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)));
        // xAxisPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        dragMode = DragMode.NONE;
    }

    private void setVerticalCursor() {
        panes.forEach(panel -> panel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)));
        // xAxisPanel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        dragMode = DragMode.VERTICAL;
    }

    private void setHorizontalCursor() {
        panes.forEach(panel -> panel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)));
        //xAxisPanel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        dragMode = DragMode.HORIZONTAL;
    }

    private void setMoveCursor() {
        panes.forEach(panel -> panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)));
        //  xAxisPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        dragMode = DragMode.BOTH;
    }

    public void setChartSize(double width, double height) {
        this.width = width;
        this.height = height;
        updateTransform();
        updateAllPanels();

        System.out.println("Size");
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void addListener(ChartWindow p) {
        p.setFocusable(true);
        p.addMouseMotionListener(this);
        p.addMouseListener(this);
        p.addMouseWheelListener(this);
        p.addKeyListener(this);

        paneTranslateY.put(p, 0.0);
        paneScaleY.put(p, 19.0);
        updateAllPanels();


    }

    public void removeListener(ChartWindow p) {
        p.setFocusable(false);
        p.removeMouseMotionListener(this);
        p.removeMouseListener(this);
        p.removeMouseWheelListener(this);
        p.removeKeyListener(this);

        paneTranslateY.remove(p);
        paneScaleY.remove(p);

        updateAllPanels();
    }

    public AffineTransform getTransform() {
        return transform;
    }

    public AffineTransform getXAxisTransform() {
        AffineTransform axisTransform = new AffineTransform();
        // Mirror the chart's horizontal translation and scaling
        axisTransform.translate(translateX, 0);  // Only translate horizontally
        axisTransform.scale(scaleX, 1);         // Only scale horizontally
        return axisTransform;
    }

    public AffineTransform getYAxisTransform() {
        AffineTransform axisTransform = new AffineTransform();
        axisTransform.scale(1, -1);              // Flip Y-axis
        axisTransform.translate(0, -height + translateY);  // Apply Y translation
        axisTransform.scale(1, scaleY);

        return axisTransform;
    }
}

