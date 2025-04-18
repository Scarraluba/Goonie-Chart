package concrete.goonie.core.chartlayers;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.axis.AxisUtils;
import concrete.goonie.core.renderers.CandlestickRenderer;
import concrete.goonie.core.ENUM_TIMEFRAME;
import concrete.goonie.core.axis.YAxis;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class SubWindow extends ChartWindow {
    private final SubWindowListener listener;
    private final YAxis yAxis;
    private final JPanel buttonPanel = new JPanel();
    private CandlestickRenderer candleRenderer;

    public SubWindow(ENUM_TIMEFRAME timeframe, ChartConfig config, SubWindowListener listener) {
        super(timeframe, config);
        this.listener = listener;
        this.yAxis = new YAxis(config);
        this.candleRenderer = new CandlestickRenderer(config);
        setBackground(config.getBackgroundColor());

        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);

        String[] buttonLabels = {"X", "↑", "↓", "⚙", "≡"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            customizeButton(button);
            button.addActionListener(e -> handleButtonAction(label));
            buttonPanel.add(button);
        }

        setLayout(null);
        add(buttonPanel);

        // Add component listener to handle resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionButtonPanel(buttonPanel);
            }
        });
        buttonPanel.setVisible(false);
    }

    private void customizeButton(JButton button) {
        // Remove default button styling
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);
        button.setFocusPainted(false);

        // Set font and color
        button.setFont(button.getFont().deriveFont(Font.BOLD, 12f));
        button.setForeground(Color.GRAY);

        // Create rounded border
        button.setBorder(new Border() {
            private final int radius = 10; // Border radius

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        });

        // Center text absolutely
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);

        // Set preferred size with padding
        button.setPreferredSize(new Dimension(20, 20));

        // Add hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.BLACK);
                button.setBorder(new Border() {
                    private final int radius = 10;

                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(100, 100, 100)); // Darker border on hover
                        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
                        g2.dispose();
                    }

                    @Override
                    public Insets getBorderInsets(Component c) {
                        return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
                    }

                    @Override
                    public boolean isBorderOpaque() {
                        return false;
                    }
                });
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.GRAY);
                button.setBorder(new Border() {
                    private final int radius = 10;

                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Color.LIGHT_GRAY);
                        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
                        g2.dispose();
                    }

                    @Override
                    public Insets getBorderInsets(Component c) {
                        return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
                    }

                    @Override
                    public boolean isBorderOpaque() {
                        return false;
                    }
                });
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call parent's paintComponent first

        Graphics2D g2d = (Graphics2D) g;
        AxisUtils.drawGridLines(g2d, transform, getWidth(), getHeight(),
                config, config.getStartDateTime(), timeframe);

        yAxis.draw(g2d, transform, getWidth(), (int)  getHeight());
        candleRenderer.draw(g2d, transform, getWidth(), getHeight());

        g2d.setColor(config.getGridColor());
        g2d.drawLine(0, 0, getWidth(), 0);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionButtonPanel(buttonPanel);
              listener.onResize(SubWindow.this);
            }
        });

//
//        g2d.setTransform(new AffineTransform());
    }

    private void positionButtonPanel(JPanel panel) {
        int panelWidth = 5 * 35; // 5 buttons * (20 width + 5 spacing)
        int panelHeight = config.getMarginBottom();
        int margin = config.getyPad();

        panel.setBounds(
                getWidth() - panelWidth - margin, // x position
                10,                          // y position
                panelWidth,                      // width
                panelHeight                      // height
        );
    }

    private void handleButtonAction(String buttonLabel) {
        switch (buttonLabel) {
            case "X":
                removeSelf();
                break;
            case "↑":
                // Move window up action
                break;
            case "↓":
                // Move window down action
                break;
            case "⚙":
                // Settings action
                break;
            case "≡":
                // Menu action
                break;
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // If you need to reposition the panel when window bounds change
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                positionButtonPanel((JPanel) comp);
            }
        }
    }

    public void removeSelf() {
        listener.onSubWindowRemoved(this);
    }

    public void setText(String string) {

    }

    @Override
    public void showGui() {
        buttonPanel.setVisible(true);
    }

    @Override
    public void hideGui() {
        buttonPanel.setVisible(false);
    }
}

