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
        colors.add(new int[]{255, 140, 65}); //yellow target
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
                int [] tempTargetColor = new int[3];
                for (int[] targetColor : colors) {
                    tempTargetColor[0]=targetColor[0];
                    tempTargetColor[1]=targetColor[1];
                    tempTargetColor[2]=targetColor[2];
                    double distance = dist(ro, co, red, green, blue, targetColor[0], targetColor[1], targetColor[2]);
                    if (distance < THRESHOLD) {
                        matched = true;
                        break;
                    }
                }

                if (matched) {
                    if(tempTargetColor[0]==255 && tempTargetColor[1]==60 && tempTargetColor[2]==60){ //red
                        red[ro][co] = 255;
                        green[ro][co] = 60;
                        blue[ro][co] = 60;
                    }
                    if(tempTargetColor[0]==50 && tempTargetColor[1]==143 && tempTargetColor[2]==50){ //green
                        red[ro][co] = 50;
                        green[ro][co] = 143;
                        blue[ro][co] = 50;
                    }
                    if(tempTargetColor[0]==30 && tempTargetColor[1]==55 && tempTargetColor[2]==150){ //blue
                        red[ro][co] = 30;
                        green[ro][co] = 55;
                        blue[ro][co] = 150;
                    }
                    if(tempTargetColor[0]==255 && tempTargetColor[1]==140 && tempTargetColor[2]==65) { //yellow
                        red[ro][co] = 255;
                        green[ro][co] = 140;
                        blue[ro][co] = 65;
                    }

                } else {
                    red[ro][co] = 0;
                    green[ro][co] = 0;
                    blue[ro][co] = 0;
                }
            }
        }
        getCenter(red, green, blue,img);
        img.setColorChannels(red, green, blue);
        return img;
    }

    private void getCenter(short[][] red, short[][] green,short[][] blue, DImage img) {
        int rowCount = 0;
        int colCount = 0;
        int numTotalpixels = 0;
        int rowAverage;
        int colAverage;

        for (int row = 0; row < red.length; row++) {
            for (int col = 0; col < red[row].length; col++) {
                if (red[row][col] == 255) {
                    rowCount += row;
                    colCount += col;
                    numTotalpixels++;
                }
            }
        }

        if (numTotalpixels == 0) {
            rowAverage = rowCount;
            colAverage = colCount;
        } else {
            rowAverage = rowCount / numTotalpixels;
            colAverage = colCount / numTotalpixels;
        }

        System.out.println("Center: " + colAverage + ", " + rowAverage);
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