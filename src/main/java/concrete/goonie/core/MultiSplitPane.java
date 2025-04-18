package concrete.goonie.core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class MultiSplitPane extends JPanel {
    public static final int HORIZONTAL_SPLIT = JSplitPane.HORIZONTAL_SPLIT;
    public static final int VERTICAL_SPLIT = JSplitPane.VERTICAL_SPLIT;

    private final int orientation;
    private final List<Component> components = new ArrayList<>();
    private final List<Double> weights = new ArrayList<>();
    private final List<Divider> dividers = new ArrayList<>();
    private int dividerSize = 1;

    private Color dividerColor = Color.BLACK;
    private Color background = Color.BLACK;
    private Color hoverColor = Color.BLUE;
    private int defaultDividerSize = 2;
    private int hoverDividerSize = 8;

    public MultiSplitPane(int orientation) {
        this.orientation = orientation;
        setLayout(new BorderLayout());
        setBorder(null); // Remove main panel border
    }

    // Add/remove components (unchanged)
    public void addComponent(Component component, double weight) {
        components.add(component);
        weights.add(weight);
        rebuild();
    }

    public void removeComponent(Component component) {
        int index = components.indexOf(component);
        if (index >= 0) {
            components.remove(index);
            weights.remove(index);
            rebuild();
        }
    }

    private void rebuild() {
        removeAll();
        dividers.clear();

        if (components.isEmpty()) return;

        if (components.size() == 1) {
            add(components.get(0), BorderLayout.CENTER);
            return;
        }

        JPanel container = new JPanel(new MultiSplitLayout());
        add(container, BorderLayout.CENTER);

        container.setBackground(background);

        for (int i = 0; i < components.size(); i++) {
            container.add(components.get(i));
            if (i < components.size() - 1) {
                Divider divider = new Divider(orientation);
                dividers.add(divider);
                container.add(divider);
            }
        }
        revalidate();
    }

    private class Divider extends JComponent {
        private final int orientation;
        private int dragOffset;
        private boolean isHovered = false;
        private float animationProgress = 0f;
        private Timer animationTimer = null;
        private static final int ANIMATION_DURATION = 150;
        private static final int ANIMATION_STEPS = 10;

        public Divider(int orientation) {
            this.orientation = orientation;
            setBorder(null);
            setCursor(orientation == HORIZONTAL_SPLIT ?
                    Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR) :
                    Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));

            // Animation timer remains the same
            animationTimer = new Timer(ANIMATION_DURATION / ANIMATION_STEPS, e -> {
                if (isHovered) {
                    animationProgress = Math.min(1f, animationProgress + (1f / ANIMATION_STEPS));
                } else {
                    animationProgress = Math.max(0f, animationProgress - (1f / ANIMATION_STEPS));
                }

                repaint();

                if ((isHovered && animationProgress >= 1f) || (!isHovered && animationProgress <= 0f)) {
                    animationTimer.stop();
                }
            });

            // Modified mouse listeners to use buffer area
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (isInBufferZone(e.getPoint())) {
                        isHovered = true;
                        if (!animationTimer.isRunning()) {
                            animationTimer.start();
                        }
                    }
                }

                public void mouseExited(MouseEvent e) {
                    if (!isInBufferZone(e.getPoint())) {
                        isHovered = false;
                        if (!animationTimer.isRunning()) {
                            animationTimer.start();
                        }
                    }  repaint();
                }

                public void mousePressed(MouseEvent e) {
                    if (isInBufferZone(e.getPoint())) {
                        dragOffset = orientation == HORIZONTAL_SPLIT ? e.getX() : e.getY();
                        isHovered = true;
                        animationProgress = 1f;
                        repaint();
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    boolean nowHovered = isInBufferZone(e.getPoint());
                    if (nowHovered != isHovered) {
                        isHovered = nowHovered;
                        if (!animationTimer.isRunning()) {
                            animationTimer.start();
                        }
                    }
                }

                public void mouseDragged(MouseEvent e) {
                    int index = dividers.indexOf(Divider.this);
                    if (index >= 0) {
                        Point p = SwingUtilities.convertPoint(Divider.this, e.getPoint(), MultiSplitPane.this);
                        int newPos = orientation == HORIZONTAL_SPLIT ? p.x - dragOffset : p.y - dragOffset;
                        setDividerPosition(index, newPos);
                        isHovered = true;
                        animationProgress = 1f;
                        repaint();
                    }
                }
            });
        }

        // Helper method to check if point is in buffer zone
        private boolean isInBufferZone(Point p) {
            // Added buffer area size
            int bufferSize = 8;
            if (orientation == HORIZONTAL_SPLIT) {
                int center = getWidth() / 2;
                return p.x >= center - bufferSize && p.x <= center + bufferSize;
            } else {
                int center = getHeight() / 2;
                return p.y >= center - bufferSize && p.y <= center + bufferSize;
            }
        }

        @Override
        public boolean contains(int x, int y) {
            // Make the component respond to mouse events in buffer zone
            return isInBufferZone(new Point(x, y));
        }

        // Rest of the Divider class remains the same...
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            Color currentColor = blendColors(dividerColor, hoverColor, animationProgress);
            int currentSize = (int) (defaultDividerSize + (hoverDividerSize - defaultDividerSize) * animationProgress);

            g2d.setColor(currentColor);

            if (orientation == HORIZONTAL_SPLIT) {
                int center = getWidth() / 2;
                g2d.fillRect(center - currentSize / 2, 0, currentSize, getHeight());
            } else {
                int center = getHeight() / 2;
                g2d.fillRect(0, (center - currentSize / 2), getWidth(), currentSize);
            }
        }
        // Helper method to blend two colors based on progress (0.0 to 1.0)
        private Color blendColors(Color color1, Color color2, float progress) {
            if (progress <= 0f) return color1;
            if (progress >= 1f) return color2;

            float inverse = 1f - progress;
            int red = (int) (color1.getRed() * inverse + color2.getRed() * progress);
            int green = (int) (color1.getGreen() * inverse + color2.getGreen() * progress);
            int blue = (int) (color1.getBlue() * inverse + color2.getBlue() * progress);
            int alpha = (int) (color1.getAlpha() * inverse + color2.getAlpha() * progress);

            return new Color(red, green, blue, alpha);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(
                    orientation == HORIZONTAL_SPLIT ? defaultDividerSize : 0,
                    orientation == VERTICAL_SPLIT ? defaultDividerSize : 0
            );
        }
    }
    // Custom layout manager
    private class MultiSplitLayout implements LayoutManager {
        @Override
        public void layoutContainer(Container parent) {
            int width = parent.getWidth();
            int height = parent.getHeight();
            int componentCount = components.size();

            if (componentCount == 0) return;

            int[] sizes = calculateSizes(width, height);

            int pos = 0;
            for (int i = 0; i < componentCount; i++) {
                Component comp = components.get(i);
                if (orientation == HORIZONTAL_SPLIT) {
                    comp.setBounds(pos, 0, sizes[i], height);
                    pos += sizes[i];
                    if (i < dividers.size()) {
                        dividers.get(i).setBounds(pos, 0, dividerSize, height);
                        pos += dividerSize;
                    }
                } else {
                    comp.setBounds(0, pos, width, sizes[i]);
                    pos += sizes[i];
                    if (i < dividers.size()) {
                        dividers.get(i).setBounds(0, pos, width, dividerSize);
                        pos += dividerSize;
                    }
                }
            }
        }

        private int[] calculateSizes(int totalWidth, int totalHeight) {
            int componentCount = components.size();
            int[] sizes = new int[componentCount];
            int totalSize = orientation == HORIZONTAL_SPLIT ? totalWidth : totalHeight;
            int availableSize = totalSize - (dividers.size() * dividerSize);

            // Initial sizes based on weights
            double totalWeight = weights.stream().mapToDouble(Double::doubleValue).sum();
            for (int i = 0; i < componentCount; i++) {
                sizes[i] = (int) (availableSize * weights.get(i) / totalWeight);
            }

            // Adjust for minimum sizes
            int remaining = availableSize;
            for (int i = 0; i < componentCount; i++) {
                Dimension min = components.get(i).getMinimumSize();
                int minSize = orientation == HORIZONTAL_SPLIT ?
                        (min != null ? min.width : 0) :
                        (min != null ? min.height : 0);
                if (sizes[i] < minSize) {
                    remaining -= minSize;
                    sizes[i] = minSize;
                } else {
                    remaining -= sizes[i];
                }
            }

            // Distribute remaining space
            if (remaining > 0) {
                double remainingWeight = 0;
                for (int i = 0; i < componentCount; i++) {
                    Dimension min = components.get(i).getMinimumSize();
                    int minSize = orientation == HORIZONTAL_SPLIT ?
                            (min != null ? min.width : 0) :
                            (min != null ? min.height : 0);
                    if (sizes[i] > minSize) {
                        remainingWeight += weights.get(i);
                    }
                }

                if (remainingWeight > 0) {
                    for (int i = 0; i < componentCount; i++) {
                        Dimension min = components.get(i).getMinimumSize();
                        int minSize = orientation == HORIZONTAL_SPLIT ?
                                (min != null ? min.width : 0) :
                                (min != null ? min.height : 0);
                        if (sizes[i] > minSize) {
                            sizes[i] += (int) (remaining * weights.get(i) / remainingWeight);
                        }
                    }
                }
            }

            return sizes;
        }

        // Other required LayoutManager methods
        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return new Dimension(100, 100);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(0, 0);
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }
    }

    public void setDividerPosition(int dividerIndex, int position) {
        if (dividerIndex < 0 || dividerIndex >= dividers.size()) return;

        // Calculate new sizes for adjacent components
        Component left = components.get(dividerIndex);
        Component right = components.get(dividerIndex + 1);

        Rectangle leftBounds = left.getBounds();
        Rectangle rightBounds = right.getBounds();

        if (orientation == HORIZONTAL_SPLIT) {
            int leftSize = position - leftBounds.x;
            int rightSize = rightBounds.x + rightBounds.width - position - dividerSize;

            if (leftSize > 0 && rightSize > 0) {
                left.setBounds(leftBounds.x, leftBounds.y, leftSize, leftBounds.height);
                right.setBounds(position + dividerSize, rightBounds.y, rightSize, rightBounds.height);
                dividers.get(dividerIndex).setBounds(position, leftBounds.y, dividerSize, leftBounds.height);
            }
        } else {
            int topSize = position - leftBounds.y;
            int bottomSize = rightBounds.y + rightBounds.height - position - dividerSize;

            if (topSize > 0 && bottomSize > 0) {
                left.setBounds(leftBounds.x, leftBounds.y, leftBounds.width, topSize);
                right.setBounds(rightBounds.x, position + dividerSize, rightBounds.width, bottomSize);
                dividers.get(dividerIndex).setBounds(leftBounds.x, position, leftBounds.width, dividerSize);
            }
        }

        repaint();
    }

    public void setDividerSize(int size) {
        this.dividerSize = size;
        for (Divider divider : dividers) {
            divider.revalidate();
        }
        revalidate();
    }

    // ===== NEW SETTERS =====
    public void setDividerColor(Color color) {
        this.dividerColor = color;
        repaintDividers();
    }

    public void setHoverColor(Color color) {
        this.hoverColor = color;
        repaintDividers();
    }

    public void setDefaultDividerSize(int size) {
        this.defaultDividerSize = size;
        revalidateDividers();
    }

    public void setHoverDividerSize(int size) {
        this.hoverDividerSize = size;
        repaintDividers();
    }

    public void setUniformBackground(Color color) {
        setBackground(color);
        for (Component component : components) {
            component.setBackground(color);
            if (component instanceof Container) {
                setContainerBackground((Container) component, color);
            }
        }
    }

    private void setContainerBackground(Container container, Color color) {
        for (Component comp : container.getComponents()) {
            comp.setBackground(color);
            if (comp instanceof Container) {
                setContainerBackground((Container) comp, color);
            }
        }
    }

    public void setComponentsBackground(Color color) {
        for (Component component : components) {
            component.setBackground(color);
        }

    }

    public void setDividersBackground(Color color) {
        background = color;
        repaintDividers();
    }

    private void repaintDividers() {
        for (Divider d : dividers) {
            d.repaint();
        }
    }

    private void revalidateDividers() {
        for (Divider d : dividers) {
            d.revalidate();
        }
    }
}