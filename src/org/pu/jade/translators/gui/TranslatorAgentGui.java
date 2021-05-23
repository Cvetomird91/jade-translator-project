package org.pu.jade.translators.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pu.jade.translators.agents.TranslatorAgent;

import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

public class TranslatorAgentGui extends TranslationGuiBase {

    private JTextField discountPercentageTextField;
    private JTextField wordLimitField;

    public TranslatorAgentGui(TranslatorAgent translatorAgent) {
        setupComponents();
        components.forEach((component) -> languagesPanel.add(component));

        submitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedValues = languagesJlist.getSelectedValuesList();
                String rate = rateTextField.getText();
                String percentage = discountPercentageTextField.getText();
                String wordLimit = wordLimitField.getText();
                System.out.println(languagesJlist.getSelectedValuesList());
                System.out.println(rate);
                System.out.println(percentage + "%");
                System.out.println(wordLimit);

                if (selectedValues.size() <= 1) {
                    JOptionPane.showMessageDialog(frame, "Please, select atleast two languages!");
                    return;
                }

                if (!NumberUtils.isCreatable(rate) || StringUtils.isEmpty(rate)) {
                    JOptionPane.showMessageDialog(frame, "Rate is empty or not a valid value!");
                    return;
                }

                if (!NumberUtils.isCreatable(wordLimit) || Double.parseDouble(wordLimit) < 0) {
                    JOptionPane.showMessageDialog(frame, "Please, pass a valid word limit after which the discount is applied!");
                    return;
                }

                if (!NumberUtils.isCreatable(percentage) || Double.parseDouble(percentage) < 0 || Double.parseDouble(percentage) > 100) {
                    JOptionPane.showMessageDialog(frame, "Please, pass a valid discount percentage between 0 and 100!");
                    return;
                }

                translatorAgent.setSpokenLanguages(selectedValues);
                translatorAgent.setRatePerWord(Double.parseDouble(rate));
                translatorAgent.setDiscountPercentage(Double.parseDouble(percentage));
                translatorAgent.setWordLimitForDiscount(Double.parseDouble(wordLimit));

                hide();
            }
        });
        show();
    }

    private void setupComponents() {
        discountPercentageTextField = new JTextField(16);
        JPanel discountPercentagePanel = new JPanel();
        JLabel rateLabel = new JLabel();
        rateLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        rateLabel.setLabelFor(discountPercentageTextField);
        rateLabel.setText("Discount percentage: ");
        discountPercentagePanel.add(rateLabel);
        discountPercentagePanel.add(discountPercentageTextField);

        wordLimitField = new JTextField(16);
        JPanel wordLimitToDiscountPanel = new JPanel();
        JLabel wordLimitLabel = new JLabel();
        wordLimitLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        wordLimitLabel.setLabelFor(wordLimitField);
        wordLimitLabel.setText("Apply discount after word count: ");
        wordLimitToDiscountPanel.add(wordLimitLabel);
        wordLimitToDiscountPanel.add(wordLimitField);

        components.add(2, discountPercentagePanel);
        components.add(3, wordLimitToDiscountPanel);
    }

}
