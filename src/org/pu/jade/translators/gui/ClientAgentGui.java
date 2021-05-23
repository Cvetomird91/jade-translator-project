package org.pu.jade.translators.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pu.jade.translators.agents.ClientAgent;

import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

public class ClientAgentGui extends TranslationGuiBase {
    public ClientAgentGui(ClientAgent clientAgent) {
        submitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedLanguages = languagesJlist.getSelectedValuesList();
                String rate = textField.getText();
                System.out.println(languagesJlist.getSelectedValuesList());
                System.out.println(rate);

                if (selectedLanguages.size() != 2) {
                    JOptionPane.showMessageDialog(frame, "Please, select two languages!");
                    return;
                }

                if (!NumberUtils.isCreatable(rate) || StringUtils.isEmpty(rate)) {
                    JOptionPane.showMessageDialog(frame, "Rate is empty or not a valid value!");
                    return;
                }

                clientAgent.setDesiredLanguages(selectedLanguages);
                clientAgent.setDesiredRatePerWord(Double.parseDouble(rate));
                frame.hide();
            }
        });
        show();
    }
}
