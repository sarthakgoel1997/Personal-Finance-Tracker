package UserInterface;

import javax.swing.*;
import Service.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegistrationForm extends JPanel {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private MainWindow mainWindow;

    public RegistrationForm(MainWindow mainWindow) {
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
        JLabel titleLabel = new JLabel("User Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel, gbc);

        // first name label
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("First Name:"), gbc);

        // first name field
        gbc.gridx = 1;
        firstNameField = new JTextField(15);
        formPanel.add(firstNameField, gbc);

        // last name label
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Last Name:"), gbc);

        // last name field
        gbc.gridx = 1;
        lastNameField = new JTextField(15);
        formPanel.add(lastNameField, gbc);

        // email label
        gbc.gridy++;
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

        // register button
        gbc.gridy++;
        gbc.gridx = 0;
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(this::handleRegistration);
        formPanel.add(registerButton, gbc);

        // back to login button
        gbc.gridx = 1;
        JButton backButton = new JButton("User Login");
        backButton.addActionListener(e -> {
            mainWindow.getContentPane().removeAll();
            mainWindow.add(new LoginForm(mainWindow));
            mainWindow.revalidate();
            mainWindow.repaint();
        });
        formPanel.add(backButton, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void handleRegistration(ActionEvent e) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        UserService userService = new UserService();

        try {
            if (userService.registerUser(firstName, lastName, email, password)) {
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                mainWindow.getContentPane().removeAll();
                mainWindow.add(new LoginForm(mainWindow));
                mainWindow.revalidate();
                mainWindow.repaint();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
