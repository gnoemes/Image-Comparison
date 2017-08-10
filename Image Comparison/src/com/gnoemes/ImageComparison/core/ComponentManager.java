package com.gnoemes.ImageComparison.core;


import javax.swing.*;
import java.awt.*;


public class ComponentManager {
    private JButton btnOpenFirstFile = new JButton("Open");
    private JButton btnOpenSecondFile = new JButton("Open");
    private JButton btnDeleteFirstFile = new JButton("Delete");
    private JButton btnDeleteSecondFile = new JButton("Delete");
    private JLabel labelFirstImage = new JLabel("Load image", SwingConstants.CENTER);
    private JLabel labelSecondImage = new JLabel("Load image", SwingConstants.CENTER);
    private JButton btnRecognize = new JButton("Recognize");
    private JButton btnUpload = new JButton("Upload to file");
    private JCheckBox checkBoxOriginalSize = new JCheckBox("Original Size");

    public ComponentManager() {
    checkBoxOriginalSize.setToolTipText("The uploaded file will be of original size ");
    checkBoxOriginalSize.setSelected(true);
    checkBoxOriginalSize.setBackground(Color.GRAY);
    }

    public JCheckBox getCheckBoxOriginalSize() {
        return checkBoxOriginalSize;
    }

    public JButton getBtnRecognize() {
        return btnRecognize;
    }

    public JButton getBtnUpload() {
        return btnUpload;
    }

    public JLabel getLabelFirstImage() {
        return labelFirstImage;
    }

    public JLabel getLabelSecondImage() {
        return labelSecondImage;
    }

    public JButton getBtnOpenSecondFile() {
        return btnOpenSecondFile;
    }

    public JButton getBtnDeleteFirstFile() {
        return btnDeleteFirstFile;
    }

    public JButton getBtnDeleteSecondFile() {
        return btnDeleteSecondFile;
    }

    public JButton getBtnOpenFirstFile() {
        return btnOpenFirstFile;
    }
}
