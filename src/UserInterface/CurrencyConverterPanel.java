package UserInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import Model.*;
import Service.CurrencyAPI;

public class CurrencyConverterPanel extends JPanel {
	private JComboBox<String> baseCurrencyDropdown;
    private JComboBox<String> targetCurrencyDropdown;
    private JTextField amountField;
    private JLabel resultLabel;
    private Map<String, Double> exchangeRates;
    private User user;

    public CurrencyConverterPanel(User user, JFrame mainWindow) {
        this.user = user;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Currency Converter");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, gbc);

        // reset gridwidth for other components
        gbc.gridwidth = 1;

        // amount field
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(15);
        add(amountField, gbc);

        // base currency dropdown
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("From:"), gbc);
        gbc.gridx = 1;
        baseCurrencyDropdown = new JComboBox<>();
        add(baseCurrencyDropdown, gbc);

        // target currency dropdown
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("To:"), gbc);
        gbc.gridx = 1;
        targetCurrencyDropdown = new JComboBox<>();
        add(targetCurrencyDropdown, gbc);

        // convert button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(this::handleConvert);
        add(convertButton, gbc);

        // result label
        gbc.gridy++;
        gbc.gridx = 0;
        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(resultLabel, gbc);

        // back button
        gbc.gridy++;
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> goBackToDashboard(mainWindow));
        add(backButton, gbc);

        // get exchange rates
        loadExchangeRates();
    }
    
    // using SwingWorker to fetch and parse exchange rates in the background
    private void loadExchangeRates() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    String jsonResponse = CurrencyAPI.getLatestExchangeRates();
                    parseExchangeRates(jsonResponse);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CurrencyConverterPanel.this, "Failed to load exchange rates.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }
        };
        worker.execute();
    }

    private void parseExchangeRates(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse).getJSONObject("data");
            exchangeRates = new HashMap<>();

            for (Object key : jsonObject.keySet()) {
                String currency = (String) key;
                exchangeRates.put(currency, jsonObject.getDouble(currency));
            }

            // sort currencies alphabetically
            ArrayList<String> sortedCurrencies = new ArrayList<>(exchangeRates.keySet());
            Collections.sort(sortedCurrencies);

            // populate dropdowns
            baseCurrencyDropdown.setModel(new DefaultComboBoxModel<>(sortedCurrencies.toArray(new String[0])));
            baseCurrencyDropdown.setSelectedItem(null);

            targetCurrencyDropdown.setModel(new DefaultComboBoxModel<>(sortedCurrencies.toArray(new String[0])));
            targetCurrencyDropdown.setSelectedItem(null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error parsing exchange rates.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleConvert(ActionEvent e) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String baseCurrency = (String) baseCurrencyDropdown.getSelectedItem();
            String targetCurrency = (String) targetCurrencyDropdown.getSelectedItem();

            if (baseCurrency == null || targetCurrency == null || amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            double baseRate = exchangeRates.get(baseCurrency);
            double targetRate = exchangeRates.get(targetCurrency);
            double convertedAmount = (amount / baseRate) * targetRate;

            // format the result with comma separators for thousands and 2 decimal places
            String formattedAmount = String.format("%,.2f", amount);
            String formattedConvertedAmount = String.format("%,.2f", convertedAmount);

            String resultText = String.format("%s %s = %s %s", formattedAmount, baseCurrency, formattedConvertedAmount, targetCurrency);
            resultLabel.setText(resultText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        }
    }

    private void goBackToDashboard(JFrame mainWindow) {
        mainWindow.getContentPane().removeAll();
        mainWindow.add(new Dashboard(user));
        mainWindow.revalidate();
        mainWindow.repaint();
    }
}
