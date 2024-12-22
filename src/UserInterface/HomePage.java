package UserInterface;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JPanel {

    private JFrame parentFrame;

    public HomePage(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // top banner
        JLabel titleLabel = new JLabel("Personal Finance Tracker", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setForeground(new Color(34, 139, 34));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel purposeLabel = new JLabel("<html><center><b>Take control of your finances!</b><br>"
                + "Track expenses, set budgets, and save smartly with ease.</center></html>", SwingConstants.CENTER);
        purposeLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JLabel featuresLabel = new JLabel("<html><ul'>"
                + "<li>Expense Tracking</li>"
                + "<li>Budget Management</li>"
                + "<li>Currency Conversion</li>"
                + "<li>Data Visualization</li>"
                + "</ul></html>", SwingConstants.LEFT);
        featuresLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.addActionListener(e -> openLoginForm());

        JButton registerButton = new JButton("Register");
        styleButton(registerButton);
        registerButton.addActionListener(e -> openRegistrationForm());

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(purposeLabel, gbc);

        gbc.gridy = 1;
        centerPanel.add(featuresLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridy = 2;
        centerPanel.add(buttonPanel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("© 2024 Personal Finance Tracker. All Rights Reserved.", SwingConstants.CENTER);
        footerLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(footerLabel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(60, 179, 113));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(46, 139, 87));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 179, 113));
            }
        });
    }

    private void openLoginForm() {
        parentFrame.getContentPane().removeAll();
        parentFrame.add(new LoginForm((MainWindow) parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    private void openRegistrationForm() {
        parentFrame.getContentPane().removeAll();
        parentFrame.add(new RegistrationForm((MainWindow) parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
