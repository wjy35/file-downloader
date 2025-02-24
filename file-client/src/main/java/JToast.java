import javax.swing.*;
import java.awt.*;

public class JToast extends JWindow {
    public JToast(Container parent, String message, int duration) {
        JLabel label = new JLabel(message);
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        getContentPane().add(label);
        pack();
        setAlwaysOnTop(true);

        Point parentLocation = parent.getLocationOnScreen();
        int parentX = parentLocation.x;
        int parentY = parentLocation.y;
        int parentWidth = parent.getWidth();
        int parentHeight = parent.getHeight();

        int x = parentX + (parentWidth - getWidth()) / 2;
        int y = parentY + (parentHeight - getHeight()) / 2;

        setLocation(x, y);
        setVisible(true);

        new Timer(duration, e -> dispose()).start();
    }
}