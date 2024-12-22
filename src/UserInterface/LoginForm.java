package UserInterface;

import javax.swing.*;
import Model.*;
import Service.*;
import java.awt.*;
import java.awt.event.*;

public class LoginForm extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private MainWindow mainWindow;

    public LoginForm(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        setLayout(new BorderLayout());

        // add top-right Back to Homepage button
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton homeButton = new JButton("Back to Homepage");
        homeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        homeButton.addActionListener(e -> {
            mainWindow.getContentPane().removeAll();
            mainWindow.add(new HomePage(mainWindow));
            mainWindow.revalidate();
            mainWindow.repaint();
        });
        topPanel.add(homeButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("User Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel, gbc);

        // email label
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        // email field
        gbc.gridx = 1;
        emailField = new JTextField(15);
        formPanel.add(emailField, gbc);

        // password label
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Password:"), gbc);

        // password field
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        // login button
        gbc.gridy++;
        gbc.gridx = 0;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this::handleLogin);
        formPanel.add(loginButton, gbc);

        // register button
        gbc.gridx = 1;
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            mainWindow.getContentPane().removeAll();
            mainWindow.add(new RegistrationForm(mainWindow));
            mainWindow.revalidate();
            mainWindow.repaint();
        });
        formPanel.add(registerButton, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void handleLogin(ActionEvent e) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        UserService userService = new UserService();
        User loggedInUser = userService.loginUser(email, password);

        if (loggedInUser != null) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            mainWindow.setSession(email);
            mainWindow.getContentPane().removeAll();
            mainWindow.add(new Dashboard(loggedInUser));
            mainWindow.revalidate();
            mainWindow.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials, try again.");
        }
    }
}
