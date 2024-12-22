package UserInterface;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import Model.*;
import Service.*;

public class AddExpenseForm extends JPanel {
    private User user;
    private JTextField amountField;
    private JComboBox<String> vendorDropdown;
    private JTextField vendorTextField;
    private JTextField categoryField;
    private Map<String, String> vendorCategoryMap;
    private JCheckBox miscellaneousCheckBox;
    private JPanel vendorPanel;

    public AddExpenseForm(User user, JFrame mainWindow) {
        this.user = user;

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
        JLabel titleLabel = new JLabel("Add Expense");
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
        amountField = new JTextField(15);
        add(amountField, gbc);

        // vendor panel - to dynamically switch between dropdown and text field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Vendor:"), gbc);
        gbc.gridx = 1;
        vendorPanel = new JPanel(new CardLayout());
        vendorDropdown = new JComboBox<>(new DefaultComboBoxModel<>(vendorCategoryMap.keySet().toArray(new String[0])));
        vendorDropdown.setSelectedItem(null);
        vendorDropdown.addActionListener(e -> updateCategoryField());

        // vendor text field for miscellaneous
        vendorTextField = new JTextField(15);
        vendorPanel.add(vendorDropdown, "Dropdown");
        vendorPanel.add(vendorTextField, "TextField");

        add(vendorPanel, gbc);

        // category field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryField = new JTextField(15);
        categoryField.setEditable(false);
        add(categoryField, gbc);

        // miscellaneous checkbox
        gbc.gridy++;
        gbc.gridx = 0;
        miscellaneousCheckBox = new JCheckBox("Miscellaneous Expense");
        miscellaneousCheckBox.addActionListener(e -> handleMiscellaneousCheckBox());
        gbc.gridwidth = 2;
        add(miscellaneousCheckBox, gbc);

        gbc.gridwidth = 1;

        // Add button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JButton addButton = new JButton("Add Expense");
        addButton.addActionListener(e -> handleAddExpense(mainWindow));
        add(addButton, gbc);

        // Back button
        gbc.gridx = 1;
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> goBackToDashboard(mainWindow));
        add(backButton, gbc);
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
            categoryField.setText("Miscellaneous");
            categoryField.setEditable(false);
        } else {
            cl.show(vendorPanel, "Dropdown");
            categoryField.setText("");
            updateCategoryField();
        }
    }

    private void handleAddExpense(JFrame mainWindow) {
        String amountText = amountField.getText();
        String vendor = miscellaneousCheckBox.isSelected() ? vendorTextField.getText() : (String) vendorDropdown.getSelectedItem();
        String category = categoryField.getText();

        if (amountText.isEmpty() || vendor == null || vendor.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            ExpenseService expenseService = new ExpenseService();
            boolean success = expenseService.addExpense(user.getId(), amount, vendor, category);

            if (success) {
                JOptionPane.showMessageDialog(this, "Expense added successfully!");
                goBackToDashboard(mainWindow);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add expense.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        }
    }

    private void goBackToDashboard(JFrame mainWindow) {
        mainWindow.getContentPane().removeAll();
        mainWindow.add(new ManageExpenses(user));
        mainWindow.revalidate();
        mainWindow.repaint();
    }
}
