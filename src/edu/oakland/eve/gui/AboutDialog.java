package edu.oakland.eve.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class AboutDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel githublink;

    public AboutDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("About EVE");

        buttonOK.addActionListener(e -> onOK());
        githublink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                try{
                    Desktop.getDesktop().browse(new URI("http://github.com/beesenpai/EVE"));
                }
                catch(Exception e) {}
            }
        });
        pack();
        setVisible(true);
    }

    private void onOK() {
        // add your code here
        dispose();
    }
}
