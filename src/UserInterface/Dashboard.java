package UserInterface;

import javax.swing.*;
import Model.*;
import Service.*;
import org.jfree.chart.*;
import org.jfree.data.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.List;
import java.util.*;
import java.awt.*;

public class Dashboard extends JPanel {
    private User user;
    private JPanel chartPanel;
    private JComboBox<String> timeRangeDropdown;
    private JComboBox<String> chartTypeDropdown;

    public Dashboard(User user) {
        this.user = user;
        setLayout(new BorderLayout());

        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton manageExpensesButton = new JButton("Manage Expenses");
        manageExpensesButton.addActionListener(e -> openManageExpenses());
        buttonPanel.add(manageExpensesButton);

        JButton manageIncomeSourcesButton = new JButton("Manage Income Sources");
        manageIncomeSourcesButton.addActionListener(e -> openManageIncomeSources());
        buttonPanel.add(manageIncomeSourcesButton);

        JButton addBudgetButton = new JButton("Manage Budgets");
        addBudgetButton.addActionListener(e -> openManageBudgets());
        buttonPanel.add(addBudgetButton);
        
        JButton currencyConverterButton = new JButton("Currency Converter");
        currencyConverterButton.addActionListener(e -> openCurrencyConverter());
        buttonPanel.add(currencyConverterButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // chart panel
        chartPanel = new JPanel(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);

        // main panel for dropdowns and logout
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel timeRangeLabel = new JLabel("Select Time Range: ");
        timeRangeDropdown = new JComboBox<>(new String[]{"Week", "Month", "Year"});
        timeRangeDropdown.addActionListener(e -> updateChart());
        centerPanel.add(timeRangeLabel);
        centerPanel.add(timeRangeDropdown);

        JLabel chartTypeLabel = new JLabel("Select Chart Type: ");
        chartTypeDropdown = new JComboBox<>(new String[]{"Expenses", "Budget vs Expense", "Income vs Expense vs Savings"});
        chartTypeDropdown.addActionListener(e -> updateChart());
        centerPanel.add(chartTypeLabel);
        centerPanel.add(chartTypeDropdown);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // right-aligned logout button
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // user profile button
        JButton userProfileButton = new JButton("User Profile");
        userProfileButton.addActionListener(e -> openUserProfile());
        logoutPanel.add(userProfileButton);
        
        // logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        logoutPanel.add(logoutButton);

        mainPanel.add(logoutPanel, BorderLayout.EAST);

        // add the mainPanel to the top of the dashboard
        add(mainPanel, BorderLayout.NORTH);

        // initialize with default chart
        updateChart();
    }
    
    private void updateChart() {
        String selectedTimeRange = (String) timeRangeDropdown.getSelectedItem();
        String selectedChartType = (String) chartTypeDropdown.getSelectedItem();

        switch (selectedChartType) {
	        case "Expenses":
	            updateExpenseChart(selectedTimeRange);
	            break;
	        case "Budget vs Expense":
	            updateBudgetExpenseChart(selectedTimeRange);
	            break;
	        case "Income vs Expense vs Savings":
	            updateIncomeExpenseSavingsChart(selectedTimeRange);
	            break;
	    }
    }
    
    private void updateIncomeExpenseSavingsChart(String timeRange) {
        IncomeService incomeService = new IncomeService();
        ExpenseService expenseService = new ExpenseService();

        // aggregate income and expense
        double totalIncome = incomeService.getTotalIncomeByTimeRange(user.getId(), timeRange);
        double totalExpense = expenseService.getTotalExpenseByTimeRange(user.getId(), timeRange);
        double totalSavings = Math.max(totalIncome - totalExpense, 0);

        // prepare dataset for chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(totalIncome, "Income", timeRange);
        dataset.addValue(totalExpense, "Expenses", timeRange);
        dataset.addValue(totalSavings, "Savings", timeRange);

        // create a bar chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Income vs Expense vs Savings - Current " + timeRange,
            "Metric",
            "Amount",
            dataset
        );

        // update the chart panel
        updateChartPanel(chart);
    }
    
    private void updateExpenseChart(String timeRange) {
        ExpenseService expenseService = new ExpenseService();
        Map<String, Double> expenseData = expenseService.getExpensesByCategory(user.getId(), timeRange);

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : expenseData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
            "Expenses by Category - Current " + timeRange,
            dataset,
            true, true, false
        );

        updateChartPanel(chart);
    }
    
    private void updateBudgetExpenseChart(String timeRange) {
        BudgetService budgetService = new BudgetService();
        Map<String, Map<String, Map<String, Double>>> data = budgetService.getBudgetsAndExpenses(user.getId());

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Map<String, Map<String, Double>>> entry : data.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(timeRange)) continue;

            for (Map.Entry<String, Map<String, Double>> categoryEntry : entry.getValue().entrySet()) {
                String category = categoryEntry.getKey();
                double budget = categoryEntry.getValue().get("Budget");
                double expense = categoryEntry.getValue().get("Expense");

                dataset.addValue(budget, "Budget", category);
                dataset.addValue(expense, "Expense", category);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Budget vs Expense - Current " + timeRange,
            "Category",
            "Amount",
            dataset
        );

        updateChartPanel(chart);
    }
    
    private void updateChartPanel(JFreeChart chart) {
        chartPanel.removeAll();
        chartPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void logout() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame instanceof MainWindow) {
            ((MainWindow) topFrame).setSession(null);
            topFrame.getContentPane().removeAll();
            topFrame.add(new LoginForm((MainWindow) topFrame));
            topFrame.revalidate();
            topFrame.repaint();
        }
    }

    private void openManageExpenses() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.getContentPane().removeAll();
        topFrame.add(new ManageExpenses(user));
        topFrame.revalidate();
        topFrame.repaint();
    }

    private void openManageIncomeSources() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.getContentPane().removeAll();
        topFrame.add(new ManageIncomeSources(user));
        topFrame.revalidate();
        topFrame.repaint();
    }

    private void openManageBudgets() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.getContentPane().removeAll();
        topFrame.add(new ManageBudgets(user));
        topFrame.revalidate();
        topFrame.repaint();
    }
    
    private void openCurrencyConverter() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.getContentPane().removeAll();
        topFrame.add(new CurrencyConverterPanel(user, topFrame));
        topFrame.revalidate();
        topFrame.repaint();
    }
    
    private void openUserProfile() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.getContentPane().removeAll();
        topFrame.add(new UserProfile(user));
        topFrame.revalidate();
        topFrame.repaint();
    }
}
