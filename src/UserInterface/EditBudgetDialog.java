package UserInterface;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import javax.swing.*;
import Model.*;
import Service.*;

public class EditBudgetDialog extends JDialog {
    private Budget budget;
    private JComboBox<String> categoryDropdown;
    private JComboBox<String> frequencyDropdown;
    private JTextField amountField;
    private boolean isUpdated = false;

    public EditBudgetDialog(JFrame parent, Budget budget) {
        super(parent, "Edit Budget", true);
        this.budget = budget;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // title
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Edit Budget");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, gbc);

        // reset gridwidth and increment row for the next components
        gbc.gridwidth = 1;
        gbc.gridy++;

        // category field
        gbc.gridx = 0;
        add(new JLabel("Category:"), gbc);
        
        gbc.gridx = 1;
        categoryDropdown = new JComboBox<>(new String[] {
                "Apparel & Fashion", "Entertainment", "Finance", "Food & Beverages", 
                "Grocery", "Healthcare", "Retail", "Technology", "Transportation", 
                "Utilities", "Miscellaneous" 
        });
        categoryDropdown.setSelectedItem(budget.getCategory());
        add(categoryDropdown, gbc);
        
        // amount field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(String.valueOf(budget.getAmount()), 15);
        add(amountField, gbc);

        // frequency field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Frequency:"), gbc);
        gbc.gridx = 1;
        frequencyDropdown = new JComboBox<>(new String[] { "Week", "Month" });
        frequencyDropdown.setSelectedItem(budget.getFrequency());
        add(frequencyDropdown, gbc);

        // reset gridwidth for the remaining components
        gbc.gridwidth = 1;

        // Save button
        gbc.gridy++;
        gbc.gridx = 0;
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> handleSave());
        add(saveButton, gbc);

        // Cancel button
        gbc.gridx = 1;
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton, gbc);

        pack();
        setLocationRelativeTo(parent);
    }

    private void handleSave() {
        String amountText = amountField.getText();
        String category = (String) categoryDropdown.getSelectedItem();
        String frequency = (String) frequencyDropdown.getSelectedItem();

        if (amountText.isEmpty() || category == null || category.isEmpty() || frequency == null || frequency.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            budget = new Budget(budget.getId(), budget.getUserId(), category, frequency, amount, budget.getCreatedAt());
            BudgetService budgetService = new BudgetService();
            boolean success = budgetService.updateBudget(budget);

            if (success) {
                JOptionPane.showMessageDialog(this, "Budget updated successfully!");
                isUpdated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update budget.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        }
    }

    public boolean isUpdated() {
        return isUpdated;
    }
}
