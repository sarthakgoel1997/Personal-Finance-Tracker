package UserInterface;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import Model.*;
import Service.*;
import java.awt.*;
import java.io.*;
import java.util.List;

public class ManageBudgets extends JPanel {
    private User user;
    private JTable budgetTable;
    private DefaultTableModel tableModel;
    private BudgetService budgetService;

    public ManageBudgets(User user) {
        this.user = user;
        this.budgetService = new BudgetService();

        setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Manage Budgets", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(headerLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Category", "Frequency", "Amount", "Added On"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        budgetTable = new JTable(tableModel);
        budgetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(budgetTable);
        add(scrollPane, BorderLayout.CENTER);

        // buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add Budget");
        JButton editButton = new JButton("Edit Budget");
        JButton deleteButton = new JButton("Delete Budget");
        JButton exportCsvButton = new JButton("Export to CSV");
        JButton backButton = new JButton("Back to Dashboard");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportCsvButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // load budgets on initialization
        refresh();

        // button listeners
        addButton.addActionListener(e -> openAddBudgetForm());
        editButton.addActionListener(e -> editSelectedBudget());
        deleteButton.addActionListener(e -> deleteSelectedBudget());
        exportCsvButton.addActionListener(e -> exportToCsv());
        backButton.addActionListener(e -> navigateBackToDashboard());
    }
    
    private void exportToCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getAbsolutePath().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            // start a new thread for the export operation
            File finalFile = file;
            new Thread(() -> {
                try (PrintWriter writer = new PrintWriter(finalFile)) {
                	List<Budget> budgets = budgetService.getBudgets(user.getId());

                    // write table header
                	writer.println("ID,Category,Frequency,Amount,Added On");

                    // write expense data
                	for (Budget budget : budgets) {
                        // prepare each field to handle commas, quotes, and newlines
                        String category = escapeCsvField(budget.getCategory());
                        String createdAt = escapeCsvField(budget.getCreatedAtLocalTime());

                        writer.printf("%d,%s,%s,%.2f,%s%n",
                        		budget.getId(),
                        		category,
                        		budget.getFrequency(),
                        		budget.getAmount(),
                        		createdAt);
                    }

                    // notify user on the Event Dispatch Thread
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(this, "Data exported successfully to " + finalFile.getAbsolutePath())
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    // notify user on the Event Dispatch Thread
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(this, "An error occurred while exporting to CSV.", "Error", JOptionPane.ERROR_MESSAGE)
                    );
                }
            }).start();
        }
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        // escape internal quotes by doubling them
        field = field.replace("\"", "\"\"");

        // if the field contains a comma, newline, or quotes, wrap it in double quotes
        if (field.contains(",") || field.contains("\n") || field.contains("\"")) {
            field = "\"" + field + "\"";
        }

        return field;
    }

    public void refresh() {
        List<Budget> budgets = budgetService.getBudgets(user.getId());
        tableModel.setRowCount(0);

        for (Budget budget : budgets) {
            tableModel.addRow(new Object[]{
            	budget.getId(),
            	budget.getCategory(),
            	budget.getFrequency(),
            	budget.getAmount(),
            	budget.getCreatedAtLocalTime()
            });
        }
    }

    private void openAddBudgetForm() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.getContentPane().removeAll();
        topFrame.add(new AddBudgetForm(user, topFrame));
        topFrame.revalidate();
        topFrame.repaint();
    }

    private void editSelectedBudget() {
        int selectedRow = budgetTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a budget to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int budgetId = (int) tableModel.getValueAt(selectedRow, 0);
        Budget budget = budgetService.getBudgetById(budgetId);

        if (budget != null) {
        	JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            EditBudgetDialog dialog = new EditBudgetDialog(parentFrame, budget);
            dialog.setVisible(true);

            if (dialog.isUpdated()) {
            	refresh();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve selected budget.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedBudget() {
        int selectedRow = budgetTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a budget to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int budgetId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this budget?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            budgetService.deleteBudget(budgetId);
            JOptionPane.showMessageDialog(this, "Budget deleted successfully.");
            refresh();
        }
    }

    private void navigateBackToDashboard() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.getContentPane().removeAll();
        topFrame.add(new Dashboard(user));
        topFrame.revalidate();
        topFrame.repaint();
    }
}
