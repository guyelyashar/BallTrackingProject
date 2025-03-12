package Filters;

import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;
import java.util.ArrayList;

public class colorMasking implements PixelFilter, Interactive {
    private ArrayList<int[]> colors;  // Each element is an array of 3 integers: {R, G, B}
    private double THRESHOLD;

    public colorMasking() {
        colors = new ArrayList<>();
        colors.add(new int[]{30, 55, 150});  // Blue target
        colors.add(new int[]{50, 143, 50});  // Green target
        colors.add(new int[]{255, 60, 60}); // Red target
        THRESHOLD = 45;
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        for (int ro = 0; ro < red.length; ro++) {
            for (int co = 0; co < red[0].length; co++) {
                boolean matched = false;
                for (int[] targetColor : colors) {
                    double distance = dist(ro, co, red, green, blue, targetColor[0], targetColor[1], targetColor[2]);
                    if (distance < THRESHOLD) {
                        matched = true;
                        break;
                    }
                }

                if (matched) {
                    red[ro][co] = 255;
                    green[ro][co] = 255;
                    blue[ro][co] = 255;
                } else {
                    red[ro][co] = 0;
                    green[ro][co] = 0;
                    blue[ro][co] = 0;
                }
            }
        }
        getCenter(red, green, blue);
        img.setColorChannels(red, green, blue);
        return img;
    }

    private void getCenter(short[][] red, short[][] green,short[][] blue) {
        int redRowCount = 0;
        int redColCount = 0;
        int numTotalpixels = 0;
        int redRowAverage;
        int redColAverage;

        for (int row = 0; row < red.length; row++) {
            for (int col = 0; col < red[row].length; col++) {
                if (red[row][col] == 255) {
                    redRowCount += row;
                    redColCount += col;
                    numTotalpixels++;
                }
            }
        }

        if (numTotalpixels == 0) {
            redRowAverage = redRowCount;
            redColAverage = redColCount;
        } else {
            redRowAverage = redRowCount / numTotalpixels;
            redColAverage = redColCount / numTotalpixels;
        }

        System.out.println("Center: " + redColAverage + ", " + redRowAverage);

        red[redRowAverage][redColAverage] = 127;
        green[redRowAverage][redColAverage] = 127;
        blue[redRowAverage][redColAverage] = 127;
    }

    private double dist(int row, int col, short[][] red, short[][] green, short[][] blue, int RedTargetColor, int GreenTargetColor, int BlueTargetColor){
        int changeRed = Math.abs(RedTargetColor - red[row][col]);
        int changeGreen = Math.abs(GreenTargetColor - green[row][col]);
        int changeBlue = Math.abs(BlueTargetColor - blue[row][col]);
        return Math.sqrt((changeRed * changeRed) + (changeGreen * changeGreen) + (changeBlue * changeBlue));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();
    }

    @Override
    public void keyPressed(char key) {
        if(key == '+' || key == '=') {
            THRESHOLD++;
            System.out.println(THRESHOLD);
        }
        if(key == '-' || key == '_'){
            THRESHOLD--;
            System.out.println(THRESHOLD);
        }

    }
}