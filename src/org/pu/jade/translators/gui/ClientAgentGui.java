package org.pu.jade.translators.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pu.jade.translators.agents.ClientAgent;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static org.pu.jade.translators.conf.Constants.languages;

public class ClientAgentGui extends TranslationGuiBase {

    JList sourceLanguage;

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

                clientAgent.setTargetLanguages(selectedTargetLanguages);
                clientAgent.setDesiredRatePerWord(Double.parseDouble(rate));
                clientAgent.setSourceLanguage(srcLanguage);
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
