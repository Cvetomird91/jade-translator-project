package org.pu.jade.translators.gui;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import static org.pu.jade.translators.conf.Constants.languages;

public class TranslationGuiBase extends JFrame {
    protected JFrame frame;
    protected JButton submitButton;
    protected JList languagesJlist;
    protected JTextField textField;
    protected List<JComponent> components = new ArrayList<>();
    protected JPanel languagesPanel;

    public TranslationGuiBase() {
        frame = new JFrame("Agent Settings");

        //create a panel
        languagesPanel = new JPanel(new GridLayout(3,4));

        languagesJlist = new JList(languages);
        languagesJlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        components.add(languagesJlist);
        frame.add(languagesPanel);

        textField = new JTextField(16);
        JPanel middlePanel = new JPanel();
        JLabel rateLabel = new JLabel();
        rateLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        rateLabel.setLabelFor(textField);
        rateLabel.setText("Rate by word: ");
        middlePanel.add(rateLabel);
        middlePanel.add(textField);
        components.add(middlePanel);

        submitButton = new JButton("Select languages");
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(submitButton);
        components.add(bottomPanel);

        //set the size of frame
        frame.setSize(600,400);
    }

    public void show() {
        frame.show();
    }

    public void hide() {
        frame.hide();
    }

}
