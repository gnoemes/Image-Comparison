package com.gnoemes.ImageComparison;


import com.gnoemes.ImageComparison.core.ComponentManager;
import com.gnoemes.ImageComparison.core.Window;

import javax.swing.*;
import java.awt.image.BufferedImage;



public class ImageComparison {
    private int width;
    private int height;
    private int[] firstImgPixels;
    private int[] secondImgPixels;
    private int[] resultImgPixels;
    private int[][] difference;
    private BufferedImage resultImage;
    private int areaId;
    private boolean isFirstOriginal;
    private boolean isSameImages;

    public ImageComparison(BufferedImage firstImg, BufferedImage secondImg) {
    this.width = firstImg.getWidth();
    this.height = secondImg.getHeight();
    this.firstImgPixels = readPixelsFromImage(firstImg);
    this.secondImgPixels = readPixelsFromImage(secondImg);
    this.difference = new int[height][width];
    this.areaId = 1;

    isFirstOriginal = findSecondaryImage(firstImg,secondImg);

        if (isFirstOriginal)
            resultImgPixels = secondImgPixels;
        else resultImgPixels = firstImgPixels;

        if (!isSameImages) {
            recognize(firstImgPixels, secondImgPixels);
            showImage();
        }
    }

    private boolean findSecondaryImage (BufferedImage firstImg, BufferedImage secondImg) {
    int firstCount = 0;
    int secondCount = 0;
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                if (((firstImg.getRGB(j,i) ) > (secondImg.getRGB(j,i))))
                    firstCount++;
                else if (((secondImg.getRGB(j,i) ) > (firstImg.getRGB(j,i))))
                    secondCount++;
        if (firstCount == secondCount) {
            JOptionPane.showMessageDialog(null, new JLabel("Images are the same", JLabel.CENTER));
            isSameImages= true;
        }
        else isSameImages = false;
        return firstCount > secondCount;
    }

    private int[] readPixelsFromImage(BufferedImage image) {
        int[] pixels = new int[height * width];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                pixels[i * width + j] = image.getRGB(j,i) & 0xFFFFFF;
        return pixels;
    }

    private void recognize(int[] firstImgPixels, int[] secondImgPixels) {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                if (firstImgPixels[i * width + j] != secondImgPixels[i * width + j])
                   difference[i][j] = -1;
                else difference[i][j] = 0;
        detectAreas();
        drawResult();
    }

    private void detectAreas() {
        canThrowException:
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (difference[i][j] == -1) {
                    try {
                        markArea(i, j, areaId);
                        findAreaPoints(areaId);
                        drawBorder(areaId);
                        //One more time for unification of areas
                        markArea(i, j, areaId);
                        findAreaPoints(areaId);
                        drawBorder(areaId);
                        areaId++;
                    } catch (StackOverflowError ex) {
                        JOptionPane.showMessageDialog(null, new JLabel("Images is too different. Check image sizes or location of image on background", JLabel.CENTER));
                        break canThrowException;
                    }
                 }
            }
    }

    private int markArea(int y, int x, int areaId) {
        if (y <= 0 || x <= 0 || y >= height - 1 || x >= width -1)
            return 0;

        if (difference[y][x] == 0)
            return 0;

        if (difference[y][x] != areaId)
            difference[y][x] = areaId;

        if (difference[y-1][x] != areaId)
            markArea(y - 1, x, areaId);

        if (difference[y][x+1] != areaId)
            markArea(y,x+1,areaId);


        if (difference[y][x-1] != areaId)
            markArea(y,x-1,areaId);

        return markArea(y+1,x,areaId);
    }

    private void findAreaPoints(int area) {
        int x1 = 0,y1 = 0,x2 = 0,y2 = 0;
        boolean startPos = true;
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (difference[i][j] == area && startPos) {
                    x1 = j;
                    y1 = i;
                    startPos = false;
                }
                else if (difference[i][j] == area) {
                    x2 = j;
                    y2 = i;
                }
            }

        int lowerLeftCorner = expandArea(x1,y1,x2,y2,area); //x1 - n, y2 || x1 + n, y2
        int upperRightCorner = expandArea(x2,y2,x1,y1,area); // x2 + n, y1 || x2 - n, y1
//        expandArea(lowerLeftCorner,y1,x2,y2,areaId); //useless, right lower corner draw in draw border method
        expandArea(upperRightCorner,y2, lowerLeftCorner,y1,area); //two points for draw border method

    }

    private int expandArea(int x1, int y1, int x2, int y2, int areaId) {
        if (x1 <= 0 || y1 <= 0 || x1 >=width-1 || y1 >= height-1) return 0;

        if (difference[y1][x1] != areaId)
            difference[y1][x1] = areaId;

        if (difference[y2][x2] != areaId)
            difference[y2][x2] = areaId;

        if (Math.abs(x1-x2) <= 0 || Math.abs(y1-y2) <= 0)
            return x1;

        if (y1 <= y2 && x1 < x2) {
            if (y1 < y2) {
                if (difference[y1][x1 - 1] == areaId) {
                    if (difference[y1 - 1][x1] == areaId && difference[y1][x1 - 1] == 0 || difference[y1 - 1][x1 - 1] == areaId)
                        return expandArea(x1, y1 - 1, x2, y2, areaId);
                    else return expandArea(x1 - 1, y1, x2, y2, areaId);
                }
                return expandArea(x1, y1 + 1, x2, y2, areaId);
            }
            if (x1 < x2){
                return expandArea(x1 + 1, y1, x2, y2, areaId);
            }
        }

        if (y1 <= y2 && x1 > x2) {
            if (y1 < y2) {
                if (difference[y1][x1 + 1] == areaId) {
                    if (difference[y1 - 1][x1] == areaId && difference[y1][x1 + 1] == 0 || difference[y1 - 1][x1 + 1] == areaId)
                        return expandArea(x1, y1 - 1, x2, y2, areaId);
                    else return expandArea(x1 + 1, y1, x2, y2, areaId);
                }
                return expandArea(x1, y1 + 1, x2, y2, areaId);
            }
            if (x1 > x2) return expandArea(x1 - 1, y1, x2, y2, areaId);
        }

        if (y1 >= y2 && x1 < x2) {
            if (y1 > y2) {
                if (difference[y1][x1 - 1] == areaId) {
                    if (difference[y1 + 1][x1] == areaId && difference[y1][x1 - 1] == 0 || difference[y1 + 1][x1 - 1] == areaId)
                        return expandArea(x1, y1 + 1, x2, y2, areaId);
                    else return expandArea(x1 - 1, y1, x2, y2, areaId);
                }
                return expandArea(x1, y1 - 1, x2, y2, areaId);
            }
            if (x1 < x2){
                return expandArea(x1 + 1, y1, x2, y2, areaId);
            }
        }

        if (y1 >= y2 && x1 > x2) {
            if (y1 > y2) {
                if (difference[y1][x1 + 1] == areaId) {
                    if (difference[y1 + 1][x1] == areaId && difference[y1][x1 + 1] == 0 || difference[y1 + 1][x1 + 1] == areaId)
                        return expandArea(x1, y1 + 1, x2, y2, areaId);
                    else return expandArea(x1 + 1, y1, x2, y2, areaId);
                }
                return expandArea(x1, y1 - 1, x2, y2, areaId);
            }
            if (x1 > x2){
                return expandArea(x1 - 1, y1, x2, y2, areaId);
            }
        }

        return 0;
    }

    private void drawBorder(int area) {
        int x1 = 0,y1 = 0,x2 = 0,y2 = 0;
        boolean startPos = true;
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (difference[i][j] == area && startPos) {
                    x1 = j;
                    y1 = i;
                    startPos = false;
                }
                else if (difference[i][j] == area) {
                    x2 = j;
                    y2 = i;
                }
            }
        clearArea(x1,y1,x2,y2,area);
    }

    private void clearArea(int x1, int y1, int x2, int y2, int area) {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (((i >= y1-1) && (i <= y2+1)) && ((j == x1-1) || (j == x2+1))) difference[i][j] = area;
                else if (((j >= x1-1) && (j <= x2+1)) && ((i == y1-1) || (i == y2+1))) difference[i][j] = area;
                else if ((i >= y1) && (i <= y2) && (j >= x1) && (j <= x2)) difference[i][j] = 0;
            }
    }

    private void drawResult() {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                if (difference[i][j] != 0)
                    resultImgPixels[i * width + j] = 0xCC0000 & resultImgPixels[i * width + j];
    }

    private void showImage() {

        resultImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        resultImage = createImageFromPixels(resultImgPixels);

        JLabel a = new JLabel();
        a.setIcon(new ImageIcon(resultImage));
        JOptionPane.showMessageDialog(null, a);
    }

    private BufferedImage createImageFromPixels(int[] pixels)  {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                image.setRGB(j, i, pixels[i*width +j]);
        return image;
    }

    public BufferedImage getResultImage() {
        return resultImage;
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(Window::new);
    }
}
