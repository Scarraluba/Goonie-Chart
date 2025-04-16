package concrete.goonie;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("GoonieChart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);

        ChartConfig config = ChartThemes.darkTheme();
        Chart view = new Chart(config);

        frame.setContentPane(view);
        frame.setVisible(true);
    }
}