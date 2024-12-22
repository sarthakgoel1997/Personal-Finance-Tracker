package UserInterface;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import Model.*;
import Service.*;
import java.awt.*;
import java.io.*;
import java.util.List;

public class ManageIncomeSources extends JPanel {
    private User user;
    private JTable incomeTable;
    private DefaultTableModel tableModel;
    private IncomeService incomeService;

    public ManageIncomeSources(User user) {
        this.user = user;
        this.incomeService = new IncomeService();

        setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Manage Income Sources", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(headerLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Source", "Frequency", "Amount", "Added On"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        incomeTable = new JTable(tableModel);
        incomeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(incomeTable);
        add(scrollPane, BorderLayout.CENTER);

        // buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add Income");
        JButton editButton = new JButton("Edit Income");
        JButton deleteButton = new JButton("Delete Income");
        JButton exportCsvButton = new JButton("Export to CSV");
        JButton backButton = new JButton("Back to Dashboard");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportCsvButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // load income sources on initialization
        refresh();

        // button listeners
        addButton.addActionListener(e -> openAddIncomeForm());
        editButton.addActionListener(e -> editSelectedIncome());
        deleteButton.addActionListener(e -> deleteSelectedIncome());
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
                	List<Income> incomes = incomeService.getIncomes(user.getId());

                    // write table header
                    writer.println("ID,Source,Frequency,Amount,Added On");

                    // write expense data
                    for (Income income : incomes) {
                        // prepare each field to handle commas, quotes, and newlines
                    	String source = escapeCsvField(income.getSource());

                        writer.printf("%d,%s,%s,%.2f,%s%n",
                        		income.getId(),
                        		source,
                        		income.getFrequency(),
                                income.getAmount(),
                                income.getCreatedAtLocalTime());
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
        List<Income> incomes = incomeService.getIncomes(user.getId());
        tableModel.setRowCount(0);

        for (Income income : incomes) {
            tableModel.addRow(new Object[]{
        		income.getId(),
        		income.getSource(),
        		income.getFrequency(),
                income.getAmount(),
                income.getCreatedAtLocalTime()
            });
        }
    }

    private void openAddIncomeForm() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.getContentPane().removeAll();
        topFrame.add(new AddIncomeSourceForm(user, topFrame));
        topFrame.revalidate();
        topFrame.repaint();
    }

    private void editSelectedIncome() {
        int selectedRow = incomeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an income to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int incomeId = (int) tableModel.getValueAt(selectedRow, 0);
        Income income = incomeService.getIncomeById(incomeId);

        if (income != null) {
        	JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            EditIncomeDialog dialog = new EditIncomeDialog(parentFrame, income);
            dialog.setVisible(true);

            if (dialog.isUpdated()) {
            	refresh();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve selected income.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedIncome() {
        int selectedRow = incomeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an income to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int incomeId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this income?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            incomeService.deleteIncome(incomeId);
            JOptionPane.showMessageDialog(this, "Income deleted successfully.");
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
