package UserInterface;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import javax.swing.*;
import Model.*;
import Service.*;

public class EditIncomeDialog extends JDialog {
    private Income income;
    private JTextField sourceField;
    private JComboBox<String> frequencyDropdown;
    private JTextField amountField;
    private boolean isUpdated = false;

    public EditIncomeDialog(JFrame parent, Income income) {
        super(parent, "Edit Income", true);
        this.income = income;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // title
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Edit Income");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, gbc);

        // reset gridwidth and increment row for the next components
        gbc.gridwidth = 1;
        gbc.gridy++;

        // source field
        gbc.gridx = 0;
        add(new JLabel("Source:"), gbc);
        
        gbc.gridx = 1;
        sourceField = new JTextField(income.getSource(), 15);
        add(sourceField, gbc);

        // frequency field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Frequency:"), gbc);
        gbc.gridx = 1;
        frequencyDropdown = new JComboBox<>(new String[] { "Week", "Month" });
        frequencyDropdown.setSelectedItem(income.getFrequency());
        add(frequencyDropdown, gbc);
        
        // amount field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(String.valueOf(income.getAmount()), 15);
        add(amountField, gbc);

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
        String source = sourceField.getText();
        String frequency = (String) frequencyDropdown.getSelectedItem();

        if (amountText.isEmpty() || source.isEmpty() || frequency == null || frequency.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            income = new Income(income.getId(), income.getUserId(), source, frequency, amount, income.getCreatedAt());
            IncomeService incomeService = new IncomeService();
            boolean success = incomeService.updateIncome(income);

            if (success) {
                JOptionPane.showMessageDialog(this, "Income updated successfully!");
                isUpdated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update income.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        }
    }

    public boolean isUpdated() {
        return isUpdated;
    }
}
