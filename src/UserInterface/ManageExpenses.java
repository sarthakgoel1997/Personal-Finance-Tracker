package UserInterface;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import Model.*;
import Service.ExpenseService;
import java.awt.*;
import java.io.*;
import java.util.List;

public class ManageExpenses extends JPanel {
    private User user;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private ExpenseService expenseService;

    public ManageExpenses(User user) {
        this.user = user;
        this.expenseService = new ExpenseService();

        setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Manage Expenses", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(headerLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Vendor", "Category", "Amount", "Added On"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        expenseTable = new JTable(tableModel);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        add(scrollPane, BorderLayout.CENTER);

        // buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add Expense");
        JButton editButton = new JButton("Edit Expense");
        JButton deleteButton = new JButton("Delete Expense");
        JButton exportCsvButton = new JButton("Export to CSV");
        JButton backButton = new JButton("Back to Dashboard");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportCsvButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // load expenses on initialization
        refresh();

        // button listeners
        addButton.addActionListener(e -> openAddExpenseForm());
        editButton.addActionListener(e -> editSelectedExpense());
        deleteButton.addActionListener(e -> deleteSelectedExpense());
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
                    List<Expense> expenses = expenseService.getExpenses(user.getId());

                    // write table header
                    writer.println("ID,Vendor,Category,Amount,Added On");

                    // write expense data
                    for (Expense expense : expenses) {
                        // prepare each field to handle commas, quotes, and newlines
                        String vendor = escapeCsvField(expense.getVendor());
                        String category = escapeCsvField(expense.getCategory());
                        String createdAt = escapeCsvField(expense.getCreatedAtLocalTime());

                        writer.printf("%d,%s,%s,%.2f,%s%n",
                            expense.getId(),
                            vendor,
                            category,
                            expense.getAmount(),
                            createdAt
                        );
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
        List<Expense> expenses = expenseService.getExpenses(user.getId());
        tableModel.setRowCount(0);

        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{
            	expense.getId(),
        		expense.getVendor(),
                expense.getCategory(),
                expense.getAmount(),
                expense.getCreatedAtLocalTime()
            });
        }
    }

    private void openAddExpenseForm() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.getContentPane().removeAll();
        topFrame.add(new AddExpenseForm(user, topFrame));
        topFrame.revalidate();
        topFrame.repaint();
    }

    private void editSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int expenseId = (int) tableModel.getValueAt(selectedRow, 0);
        Expense expense = expenseService.getExpenseById(expenseId);

        if (expense != null) {
        	JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            EditExpenseDialog dialog = new EditExpenseDialog(parentFrame, expense);
            dialog.setVisible(true);

            if (dialog.isUpdated()) {
            	refresh();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve selected expense.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int expenseId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this expense?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            expenseService.deleteExpense(expenseId);
            JOptionPane.showMessageDialog(this, "Expense deleted successfully.");
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
