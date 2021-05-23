package org.pu.jade.translators.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pu.jade.translators.agents.TranslatorAgent;

import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

public class TranslatorAgentGui extends TranslationGuiBase {

    public TranslatorAgentGui(TranslatorAgent translatorAgent) {

        components.forEach((component) -> languagesPanel.add(component));

        submitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedValues = languagesJlist.getSelectedValuesList();
                String rate = textField.getText();
                System.out.println(languagesJlist.getSelectedValuesList());
                System.out.println(rate);

                if (selectedValues.size() <= 1) {
                    JOptionPane.showMessageDialog(frame, "Please, select atleast two languages!");
                    return;
                }

                if (!NumberUtils.isCreatable(rate) || StringUtils.isEmpty(rate)) {
                    JOptionPane.showMessageDialog(frame, "Rate is empty or not a valid value!");
                    return;
                }

                translatorAgent.setSpokenLanguages(selectedValues);
                translatorAgent.setRatePerWord(Double.parseDouble(rate));

                hide();
            }
        });
        show();
    }

}
