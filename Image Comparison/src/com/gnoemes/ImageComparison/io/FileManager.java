package com.gnoemes.ImageComparison.io;

import com.gnoemes.ImageComparison.ImageComparison;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileManager {
private static final int MAX_WIDTH = 640;
private static final int MAX_HEIGHT = 512;
private BufferedImage[] images = new BufferedImage[2];
private BufferedImage image;
private boolean isOriginal = true;
private ImageComparison ic;

    public ImageIcon loadImage(File file) {
        BufferedImage img = new BufferedImage(MAX_WIDTH,MAX_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        try {
            img = ImageIO.read(file);
            if ((img.getHeight() > MAX_HEIGHT || img.getWidth() > MAX_WIDTH) && !isOriginal)
                img = resizeImage(img);
                image = img;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return new ImageIcon(img);
    }

    private BufferedImage resizeImage(BufferedImage img) {
        double scaleX = (double) MAX_WIDTH / img.getWidth();
        double scaleY = (double) MAX_HEIGHT / img.getHeight();

        AffineTransform tr = AffineTransform.getScaleInstance(scaleX,scaleY);
        AffineTransformOp op = new AffineTransformOp(tr,AffineTransformOp.TYPE_BILINEAR);

        int w = (int) (img.getWidth() * scaleX);
        int h = (int) (img.getHeight() * scaleY);
        BufferedImage resizedImage = new BufferedImage(w,h,img.getType());
        op.filter(img,resizedImage);
        return resizedImage;
    }

    public boolean readyToRecognizeOrUpload() {
        for (BufferedImage img : images) if (img == null) return false;
        return true;
    }

    public void uploadImage(File file) {
        try {
            ImageIO.write(ic.getResultImage(),"PNG",file);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, new JLabel("Error", JLabel.CENTER));
        }
        ic = null;
    }

    public boolean isRecognized() {
        return ic != null;
    }

    public void doRecognize() {
        ic = new ImageComparison(images[0], images[1]);
    }

    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage getImage(int id) {
        return images[id];
    }

    public void setImages(int id, BufferedImage img) {
        images[id] = img;
    }

    public void delImages(int id) {
        images[id] = null;
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        this.isOriginal = original;
    }

}