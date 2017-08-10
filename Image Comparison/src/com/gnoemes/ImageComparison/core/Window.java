package com.gnoemes.ImageComparison.core;


import com.gnoemes.ImageComparison.io.FileManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class Window extends Canvas {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private ComponentManager c = new ComponentManager();
    private FileManager f = new FileManager();
    private JPanel optionsPanel;
    private JPanel screenPanel;
    private JFileChooser fileChooser = new JFileChooser();

    public Window() {
        initListeners();
        initContainers();
        initWindow();
    }

    private void initListeners() {
        c.getBtnUpload().addActionListener(e -> {
            if (c.getLabelFirstImage().getIcon()!= null && c.getLabelSecondImage().getIcon()!= null && f.isRecognized()) {
                int ret = fileChooser.showSaveDialog(null);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (f.readyToRecognizeOrUpload())
                        f.uploadImage(file);
                }
            }
            else if ( c.getLabelFirstImage().getIcon() == null || c.getLabelSecondImage().getIcon() == null)
                JOptionPane.showMessageDialog(null, new JLabel("Load images to recognize", JLabel.CENTER));
            else  JOptionPane.showMessageDialog(null, new JLabel("Recognize first", JLabel.CENTER));
        });
        c.getCheckBoxOriginalSize().addActionListener(e -> {
            f.setOriginal(c.getCheckBoxOriginalSize().isSelected());
        });
        c.getBtnRecognize().addActionListener(e -> {
            if (f.readyToRecognizeOrUpload()) {
                int firstImageWidth = f.getImage(0).getWidth();
                int secondImageWidth = f.getImage(1).getWidth();
                int firstImageHeight = f.getImage(0).getHeight();
                int secondImageHeight = f.getImage(1).getHeight();
                System.out.println(firstImageWidth + " " + firstImageHeight + " " + secondImageWidth + " " + secondImageHeight);
                if (firstImageWidth == secondImageWidth && firstImageHeight == secondImageHeight)
                    f.doRecognize();
                else JOptionPane.showMessageDialog(null, new JLabel("Make sure what images have equal size", JLabel.CENTER));
            }
            else JOptionPane.showMessageDialog(null, new JLabel("Nothing to recognize", JLabel.CENTER));
        });
        c.getBtnOpenFirstFile().addActionListener(e -> {
            int ret = fileChooser.showDialog(null,"Open image");
            if (ret == JFileChooser.APPROVE_OPTION ) {
                File file = fileChooser.getSelectedFile();
                if (file != null) {
                    c.getLabelFirstImage().setText("");
                    c.getLabelFirstImage().setIcon(f.loadImage(file));
                    f.setImages(0,f.getImage());
                }
                else c.getLabelFirstImage().setText("Invalid image");
            }
            else if (ret == JFileChooser.CANCEL_OPTION) c.getLabelFirstImage().setText("");
            else c.getLabelFirstImage().setText("Load image");
        });
        c.getBtnOpenSecondFile().addActionListener(e -> {
            int ret = fileChooser.showDialog(null,"Open image");
            if (ret == JFileChooser.APPROVE_OPTION ) {
                File file = fileChooser.getSelectedFile();
                if (file != null) {
                    c.getLabelSecondImage().setText("");
                    c.getLabelSecondImage().setIcon(f.loadImage(file));
                    f.setImages(1,f.getImage());
                }
                else c.getLabelSecondImage().setText("Invalid image");
            }
            else if (ret == JFileChooser.CANCEL_OPTION) c.getLabelSecondImage().setText("");
            else c.getLabelSecondImage().setText("Load image");
        });
        c.getBtnDeleteFirstFile().addActionListener(e -> {
            c.getLabelFirstImage().setText("Load image");
            c.getLabelFirstImage().setIcon(null);
            f.delImages(0);
        });
        c.getBtnDeleteSecondFile().addActionListener(e -> {
            c.getLabelSecondImage().setText("Load image");
            c.getLabelSecondImage().setIcon(null);
            f.delImages(1);
        });
    }

    private void initContainers() {
        optionsPanel = new JPanel();
        screenPanel = new JPanel();
        JPanel firstImageContainer = new JPanel();
        JPanel secondImageContainer = new JPanel();
        FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(imageFilter);
        optionsPanel.setLayout(new FlowLayout());
        optionsPanel.setBorder(new EtchedBorder());
        firstImageContainer.setBorder(new EtchedBorder());
        secondImageContainer.setBorder(new EtchedBorder());
        JPanel firstImageOptions = new JPanel(new FlowLayout());
        JPanel secondImageOptions = new JPanel(new FlowLayout());
        firstImageContainer.setLayout(new BorderLayout());
        secondImageContainer.setLayout(new BorderLayout());

        firstImageOptions.add(c.getBtnOpenFirstFile());
        firstImageOptions.add(c.getBtnDeleteFirstFile());
        firstImageContainer.add(firstImageOptions,BorderLayout.NORTH);
        firstImageContainer.add(c.getLabelFirstImage(),BorderLayout.CENTER);

        secondImageOptions.add(c.getBtnOpenSecondFile());
        secondImageOptions.add(c.getBtnDeleteSecondFile());
        secondImageContainer.add(secondImageOptions,BorderLayout.NORTH);
        secondImageContainer.add(c.getLabelSecondImage(),BorderLayout.CENTER);

        screenPanel.setLayout(new GridLayout(1,2));
        screenPanel.add(firstImageContainer);
        screenPanel.add(secondImageContainer);

        firstImageContainer.setBackground(Color.LIGHT_GRAY);
        firstImageOptions.setBackground(Color.LIGHT_GRAY);
        secondImageContainer.setBackground(Color.LIGHT_GRAY);
        secondImageOptions.setBackground(Color.LIGHT_GRAY);
        optionsPanel.setBackground(Color.GRAY);

        optionsPanel.add(c.getBtnRecognize());
        optionsPanel.add(c.getBtnUpload());
        optionsPanel.add(c.getCheckBoxOriginalSize());
}

    private void initWindow() {
        JFrame frame = new JFrame("Image Comparison");
        frame.setLayout(new BorderLayout());
        frame.setSize(WIDTH,HEIGHT);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(screenPanel,BorderLayout.CENTER);
        frame.add(optionsPanel,BorderLayout.SOUTH);
    }

}
