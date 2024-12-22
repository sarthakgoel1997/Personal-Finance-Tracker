package UserInterface;

import javax.swing.*;
import Model.*;
import java.awt.*;
import Service.*;

public class AddBudgetForm extends JPanel {
    public AddBudgetForm(User user, JFrame mainWindow) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // title
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Add Budget");
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
        JComboBox<String> categoryDropdown = new JComboBox<>(new String[] { 
            "Apparel & Fashion", "Entertainment", "Finance", "Food & Beverages", 
            "Grocery", "Healthcare", "Retail", "Technology", "Transportation", 
            "Utilities", "Miscellaneous" 
        });
        categoryDropdown.setSelectedItem(null);
        add(categoryDropdown, gbc);

        // amount field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Budget Amount:"), gbc);
        gbc.gridx = 1;
        JTextField amountField = new JTextField(15);
        add(amountField, gbc);

        // frequency dropdown
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Frequency:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> frequencyDropdown = new JComboBox<>(new String[] { "Week", "Month", "Year" });
        add(frequencyDropdown, gbc);

        // buttons
        gbc.gridy++;
        gbc.gridx = 0;
        JButton addButton = new JButton("Add Budget");
        addButton.addActionListener(e -> {
            String category = (String) categoryDropdown.getSelectedItem();
            String amountText = amountField.getText();
            String frequency = (String) frequencyDropdown.getSelectedItem();
            
            if (category == null || amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }
            try {
                double amount = Double.parseDouble(amountText);
                BudgetService budgetService = new BudgetService();
                boolean success = budgetService.addBudget(user.getId(), category, amount, frequency);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Budget added successfully!");
                    mainWindow.getContentPane().removeAll();
                    mainWindow.add(new ManageBudgets(user));
                    mainWindow.revalidate();
                    mainWindow.repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add budget.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
            }
        });
        add(addButton, gbc);

        gbc.gridx = 1;
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            mainWindow.getContentPane().removeAll();
            mainWindow.add(new ManageBudgets(user));
            mainWindow.revalidate();
            mainWindow.repaint();
        });
        add(backButton, gbc);
    }
}
