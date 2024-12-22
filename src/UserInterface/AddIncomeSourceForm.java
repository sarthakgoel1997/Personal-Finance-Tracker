package UserInterface;

import javax.swing.*;
import java.awt.*;
import Model.*;
import Service.*;

public class AddIncomeSourceForm extends JPanel {
    private User user;
    private JTextField incomeSourceField;
    private JTextField amountField;
    private JComboBox<String> frequencyDropdown;

    public AddIncomeSourceForm(User user, JFrame mainWindow) {
        this.user = user;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // title
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Add Income Source");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // source field
        gbc.gridx = 0;
        add(new JLabel("Source:"), gbc);
        gbc.gridx = 1;
        incomeSourceField = new JTextField(15);
        add(incomeSourceField, gbc);
        
        // frequency dropdown
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Frequency:"), gbc);
        gbc.gridx = 1;
        frequencyDropdown = new JComboBox<>(new String[] { "Week", "Month" });
        add(frequencyDropdown, gbc);

        // amount field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(15);
        add(amountField, gbc);

        // Add button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JButton addButton = new JButton("Add Income");
        addButton.addActionListener(e -> handleAddIncome(mainWindow));
        add(addButton, gbc);

        // Back button
        gbc.gridx = 1;
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> goBackToDashboard(mainWindow));
        add(backButton, gbc);
    }

    private void handleAddIncome(JFrame mainWindow) {
        String source = incomeSourceField.getText();
        String amountText = amountField.getText();
        String frequency = (String) frequencyDropdown.getSelectedItem();

        if (source.isEmpty() || amountText.isEmpty() || frequency == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            IncomeService incomeService = new IncomeService();
            boolean success = incomeService.addIncome(user.getId(), source, frequency, amount);

            if (success) {
                JOptionPane.showMessageDialog(this, "Income source added successfully!");
                goBackToDashboard(mainWindow);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add income source.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        }
    }

    private void goBackToDashboard(JFrame mainWindow) {
        mainWindow.getContentPane().removeAll();
        mainWindow.add(new ManageIncomeSources(user));
        mainWindow.revalidate();
        mainWindow.repaint();
    }
}
