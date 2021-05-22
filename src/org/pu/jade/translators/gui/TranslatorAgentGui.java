package org.pu.jade.translators.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pu.jade.translators.agents.TranslatorAgent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

public class TranslatorAgentGui extends JFrame {

    private TranslatorAgent translatorAgent;

    public TranslatorAgentGui(TranslatorAgent translatorAgent) {
        this.translatorAgent = translatorAgent;

        final JFrame frame = new JFrame("frame");

        //create a panel
        JPanel languagesPanel = new JPanel(new GridLayout(3,3));

        //String array to store weekdays
        String languages[]= { "English","French","Bulgarian",
                "German","Spanish","Italian","Portuguese"};

        JList languagesJlist = new JList(languages);
        languagesJlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        languagesPanel.add(languagesJlist);
        frame.add(languagesPanel);

        JTextField textField = new JTextField(16);
        JPanel middlePanel = new JPanel();
        JLabel rateLabel = new JLabel();
        rateLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        rateLabel.setLabelFor(textField);
        rateLabel.setText("Rate by word: ");
        middlePanel.add(rateLabel);
        middlePanel.add(textField);
        languagesPanel.add(middlePanel);

        JButton selectButton = new JButton("Select languages");
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(selectButton);
        languagesPanel.add(bottomPanel);

        //set the size of frame
        frame.setSize(400,400);
        frame.show();

        selectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedValues = languagesJlist.getSelectedValuesList();
                String rate = textField.getText();
                System.out.println(languagesJlist.getSelectedValuesList());
                System.out.println(rate);

                if (selectedValues.size() <= 1) {
                    JOptionPane.showMessageDialog(frame, "error");
                    return;
                }

                if (!NumberUtils.isCreatable(rate) || StringUtils.isEmpty(rate)) {
                    JOptionPane.showMessageDialog(frame, "Rate is empty or not a valid value!");
                    return;
                }

                translatorAgent.setSpokenLanguages(selectedValues);
                translatorAgent.setRatePerWord(Double.parseDouble(rate));

                frame.hide();
            }
        });

    }


}
