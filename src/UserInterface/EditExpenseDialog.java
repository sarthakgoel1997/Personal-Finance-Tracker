package UserInterface;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import javax.swing.*;
import Model.*;
import Service.*;

public class EditExpenseDialog extends JDialog {
    private Expense expense;
    private Map<String, String> vendorCategoryMap;
    private JTextField amountField;
    private JComboBox<String> vendorDropdown;
    private JTextField vendorTextField;
    private JTextField categoryField;
    private JCheckBox miscellaneousCheckBox;
    private JPanel vendorPanel;
    private boolean isUpdated = false;

    public EditExpenseDialog(JFrame parent, Expense expense) {
        super(parent, "Edit Expense", true);
        this.expense = expense;

        // get vendor-category mappings
        VendorService vendorService = new VendorService();
        vendorCategoryMap = vendorService.getVendorCategoryMappings();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // title
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Edit Expense");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, gbc);

        // reset gridwidth and increment row for the next components
        gbc.gridwidth = 1;
        gbc.gridy++;

        // amount field
        gbc.gridx = 0;
        add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(String.valueOf(expense.getAmount()), 15);
        add(amountField, gbc);

        // vendor panel (to dynamically switch between dropdown and text field)
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Vendor:"), gbc);
        gbc.gridx = 1;
        vendorPanel = new JPanel(new CardLayout());

        vendorDropdown = new JComboBox<>(new DefaultComboBoxModel<>(vendorCategoryMap.keySet().toArray(new String[0])));
        vendorDropdown.setSelectedItem(expense.getVendor());
        vendorDropdown.addActionListener(e -> updateCategoryField());

        vendorTextField = new JTextField(expense.getVendor(), 15);
        vendorPanel.add(vendorDropdown, "Dropdown");
        vendorPanel.add(vendorTextField, "TextField");

        add(vendorPanel, gbc);

        // category Field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryField = new JTextField(expense.getCategory(), 15);
        categoryField.setEditable(false);
        add(categoryField, gbc);

        // miscellaneous checkbox
        gbc.gridy++;
        gbc.gridx = 0;
        miscellaneousCheckBox = new JCheckBox("Miscellaneous Expense");
        miscellaneousCheckBox.setSelected(!vendorCategoryMap.containsKey(expense.getVendor()));
        miscellaneousCheckBox.addActionListener(e -> handleMiscellaneousCheckBox());
        gbc.gridwidth = 2;
        add(miscellaneousCheckBox, gbc);

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

        // initialize the form with current expense values
        handleMiscellaneousCheckBox();

        pack();
        setLocationRelativeTo(parent);
    }

    private void updateCategoryField() {
        if (!miscellaneousCheckBox.isSelected()) {
            String selectedVendor = (String) vendorDropdown.getSelectedItem();
            if (vendorCategoryMap.containsKey(selectedVendor)) {
                categoryField.setText(vendorCategoryMap.get(selectedVendor));
            } else {
                categoryField.setText("");
            }
        }
    }

    private void handleMiscellaneousCheckBox() {
        CardLayout cl = (CardLayout) (vendorPanel.getLayout());
        if (miscellaneousCheckBox.isSelected()) {
            cl.show(vendorPanel, "TextField");
            vendorTextField.setText(expense.getVendor());
            categoryField.setText("Miscellaneous");
        } else {
            cl.show(vendorPanel, "Dropdown");
            vendorDropdown.setSelectedItem(expense.getVendor());
            updateCategoryField();
        }
    }

    private void handleSave() {
        String amountText = amountField.getText();
        String vendor = miscellaneousCheckBox.isSelected() ? vendorTextField.getText() : (String) vendorDropdown.getSelectedItem();
        String category = categoryField.getText();

        if (amountText.isEmpty() || vendor == null || vendor.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            expense = new Expense(expense.getId(), expense.getUserId(), amount, vendor, category, expense.getCreatedAt());
            ExpenseService expenseService = new ExpenseService();
            boolean success = expenseService.updateExpense(expense);

            if (success) {
                JOptionPane.showMessageDialog(this, "Expense updated successfully!");
                isUpdated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update expense.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        }
    }

    public boolean isUpdated() {
        return isUpdated;
    }
}
