package org.pu.jade.translators.gui;

import org.pu.jade.translators.agents.TranslatorAgent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JList;
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
        JPanel languagesPanel = new JPanel(new GridLayout(1,1));

        //String array to store weekdays
        String languages[]= { "English","French","Bulgarian",
                "German","Spanish","Italian","Portuguese"};

        JList languagesJlist = new JList(languages);
        languagesJlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        languagesPanel.add(languagesJlist);
        frame.add(languagesPanel);

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
                System.out.println(languagesJlist.getSelectedValuesList());

                if (selectedValues.size() <= 1) {
                    JOptionPane.showMessageDialog(frame, "error");
                }
            }
        });

    }


}
