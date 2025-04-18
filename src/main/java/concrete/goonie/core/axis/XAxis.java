package concrete.goonie.core.axis;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.ENUM_TIMEFRAME;

import javax.swing.*;
import java.awt.*;

public class XAxis extends Axis {

    private final JButton cornerButton;

    public XAxis(ENUM_TIMEFRAME timeframe, ChartConfig config) {
        super(timeframe, config);
        setLayout(null); // Allow absolute positioning
        setPreferredSize(new Dimension(0, config.getMarginBottom()));
        setBackground(config.getBackgroundColor());

        cornerButton = new JButton("⚙"); // Use any label you like
        cornerButton.setFocusable(false);

        add(cornerButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (transform == null) {
            return;
        }

        AxisUtils.drawXAxisLabels(g2d, transform, getWidth(), getHeight(),
                config, config.getStartDateTime(), timeframe,
                AxisPosition.BOTTOM, 10);

        // Dynamically update button position and size
        int buttonWidth = config.getyPad();
        int buttonHeight = getHeight();
        int x = getWidth() - buttonWidth;
        cornerButton.setBounds(x, 0, buttonWidth, buttonHeight);
    }
}
