package concrete.goonie;


import concrete.goonie.core.Chart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static class Ui extends JPanel {

        public Ui() {
            setLayout(new BorderLayout(0, 0));

            JPanel holder = new JPanel();
            holder.setLayout(new FlowLayout(FlowLayout.LEFT));

            JButton addWindow = new JButton("Add Window");
            Chart chart = new Chart(ChartThemes.darkTheme());
            addWindow.addActionListener(e -> {
                chart.addWindow();
            });
            holder.add(addWindow);

            JButton removeWindow = new JButton("Remove Window");
            removeWindow.addActionListener(e -> {
                //chart.removeSelectedWindow();
            });
            holder.add(removeWindow);


            add(holder, BorderLayout.NORTH);
            add(chart, BorderLayout.CENTER);
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("🔥 GoonieChart");

        // Load icon properly from resources
        ImageIcon icon = new ImageIcon(Main.class.getResource("/12.png"));
        frame.setIconImage(icon.getImage());

        // Optional: Taskbar icon fix for modern Java
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            try {
                taskbar.setIconImage(icon.getImage());
            } catch (Exception e) {
                System.out.println("Could not set taskbar icon.");
            }
        }

        // Splash screen logic
        SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
            Graphics2D g = splash.createGraphics();

            if (g != null) {
                g.drawString("Loading App...", 100, 150);
                splash.update();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            splash.close();
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setContentPane(new Ui());
        frame.setVisible(true);
    }

}