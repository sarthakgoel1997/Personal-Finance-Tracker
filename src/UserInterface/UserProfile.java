package UserInterface;

import javax.swing.*;
import Model.User;
import Service.UserService;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UserProfile extends JPanel {
    private User user;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;

    public UserProfile(User user) {
        this.user = user;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // title
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("User Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, gbc);
        
        // reset gridwidth and increment row for the next components
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        // first name field
        gbc.gridx = 0;
        add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(user.getFirstName());
        add(firstNameField, gbc);
        
        // last name field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(user.getLastName());
        add(lastNameField, gbc);
        
        // email field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(user.getEmail());
        add(emailField, gbc);
        
        // current password field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Current Password (for password update):"), gbc);
        gbc.gridx = 1;
        currentPasswordField = new JPasswordField();
        add(currentPasswordField, gbc);
        
        // new password field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        newPasswordField = new JPasswordField();
        add(newPasswordField, gbc);
        
        // update profile button
        gbc.gridy++;
        gbc.gridx = 0;
        JButton updateProfileButton = new JButton("Update Profile");
        updateProfileButton.addActionListener(this::updateProfile);
        add(updateProfileButton, gbc);

        // back button
        gbc.gridx = 1;
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> navigateBack());
        add(backButton, gbc);
    }

    private void updateProfile(ActionEvent e) {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());

        UserService userService = new UserService();
        
        if(firstName.equals(user.getFirstName()) && lastName.equals(user.getLastName()) && email.equals(user.getEmail()) && currentPassword.isEmpty() && newPassword.isEmpty()) {
        	JOptionPane.showMessageDialog(this, "No changes made.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate and update user details
        if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty()) {
        	if((!currentPassword.isEmpty() && newPassword.isEmpty()) || (currentPassword.isEmpty() && !newPassword.isEmpty())) {
            	JOptionPane.showMessageDialog(this, "Both current and new password fields are required for password update.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        	
            boolean isPasswordUpdate = !currentPassword.isEmpty() && !newPassword.isEmpty();

            if (isPasswordUpdate) {
                // validate current password
                if (!userService.validatePassword(user.getId(), currentPassword)) {
                    JOptionPane.showMessageDialog(this, "Incorrect current password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // update password in the database
                if (!userService.updateUserPassword(user.getId(), newPassword)) {
                    JOptionPane.showMessageDialog(this, "Failed to update password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // update user details
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);

            if (userService.updateUserDetails(user)) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                navigateBack();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "All fields (except password fields) are required.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navigateBack() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.getContentPane().removeAll();
        topFrame.add(new Dashboard(user));
        topFrame.revalidate();
        topFrame.repaint();
    }
}
