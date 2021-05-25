package org.pu.jade.translators.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pu.jade.translators.agents.ClientAgent;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static org.pu.jade.translators.conf.Constants.languages;

public class ClientAgentGui extends TranslationGuiBase {

    private JList sourceLanguage;
    private JTextField wordCountField;

    public ClientAgentGui(ClientAgent clientAgent) {

        sourceLanguage = new JList(languages);
        sourceLanguage.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        components.add(0, sourceLanguage);

        JLabel sourceLanguagesLabel = new JLabel();
        sourceLanguagesLabel.setText("select target language");
        components.add(0, sourceLanguagesLabel);

        JLabel targetLanguagesLabel = new JLabel();
        targetLanguagesLabel.setText("select source languages");
        components.add(0, targetLanguagesLabel);

        JLabel emptyLabel = new JLabel();
        emptyLabel.setText(" ");
        JLabel emptyLabel2 = new JLabel();
        emptyLabel2.setText(" ");
        components.add(2, emptyLabel);
        components.add(5, emptyLabel2);

        wordCountField = new JTextField(16);
        JPanel wordCountPanel = new JPanel();
        JLabel wordCountLabel = new JLabel();
        wordCountLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        wordCountLabel.setLabelFor(wordCountField);
        wordCountLabel.setText("Text word count: ");
        wordCountPanel.add(wordCountLabel);
        wordCountPanel.add(wordCountField);
        components.add(components.size()-1, wordCountPanel);

        components.forEach((component) -> languagesPanel.add(component));

        submitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String srcLanguage = "";
                try {
                    srcLanguage = sourceLanguage.getSelectedValue().toString();
                    System.out.println(srcLanguage);
                }
                catch (NullPointerException ex) {
                    JOptionPane.showMessageDialog(frame, "Please, select source language!");
                }
                List<String> selectedTargetLanguages = languagesJlist.getSelectedValuesList();
                String rate = rateTextField.getText();
                String wordCount = wordCountField.getText();
                System.out.println(languagesJlist.getSelectedValuesList());
                System.out.println(rate);

                if (StringUtils.isEmpty(srcLanguage)) {
                    JOptionPane.showMessageDialog(frame, "Please, select source language!");
                    return;
                }

                if (selectedTargetLanguages.size() < 1) {
                    JOptionPane.showMessageDialog(frame, "Please, select atleast one target language!");
                    return;
                }

                if (selectedTargetLanguages.contains(srcLanguage)) {
                    JOptionPane.showMessageDialog(frame, "Source language cannot be part of target languages!");
                    return;
                }

                if (!NumberUtils.isCreatable(rate) || StringUtils.isEmpty(rate)) {
                    JOptionPane.showMessageDialog(frame, "Rate is empty or not a valid value!");
                    return;
                }

                if (!NumberUtils.isCreatable(wordCount) || StringUtils.isEmpty(wordCount) || Double.parseDouble(wordCount) < 1) {
                    JOptionPane.showMessageDialog(frame, "Text word count is empty or not a valid value!");
                    return;
                }

                clientAgent.setTargetLanguages(selectedTargetLanguages);
                clientAgent.setDesiredRatePerWord(Double.parseDouble(rate));
                clientAgent.setSourceLanguage(srcLanguage);
                clientAgent.setWordCount(Integer.parseInt(wordCount));
                hide();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clientAgent.doDelete();
            }
        });

        show();
    }
}
