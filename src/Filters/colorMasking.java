package Filters;

import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;

import java.awt.*;
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
        getCenter(red, green, blue, 255, img);
        getCenter(green, red, blue, 143, img);
        getCenter(blue, red, green, 150, img);
        getCenter(blue, red, green, 65, img);
        img.setColorChannels(red, green, blue);
        return img;
    }

    private void getCenter(short[][] color, short[][] otherColor1, short[][] otherColor2, int colorVal, DImage img) {
        int rowCount = 0;
        int colCount = 0;
        int numTotalpixels = 0;
        int rowAverage;
        int colAverage;

        for (int row = 0; row < color.length; row++) {
            for (int col = 0; col < color[row].length; col++) {
                if (color[row][col] == colorVal) {
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

        String colorName = "";

        if (colorVal == 255) colorName = "Red";
        else if (colorVal == 143) colorName = "Green";
        else if (colorVal == 150) colorName = "Blue";
        else if (colorVal == 65) colorName = "Yellow";


//        System.out.println("Center for " + colorName + " ball : " + colAverage + ", " + rowAverage);
        if(color[rowAverage][colAverage]==0 && otherColor1[rowAverage][colAverage]==0 && otherColor2[rowAverage][colAverage]==0) { // if the coords are black then do multiple blob detection
            placeDot(rowAverage,colAverage,color,otherColor1,otherColor2,img);
        }else{
            doubleBlobDetection(color, otherColor1,otherColor2, colorVal,img);
        }

    }
    public void placeDot(int rowAverage, int colAverage, short [][] color, short[][] otherColor1, short[][] otherColor2, DImage img){
        if (rowAverage >= 2 && colAverage >= 2 && rowAverage <= img.getHeight() - 3 && colAverage <= img.getWidth() - 3) {
            for (int i = rowAverage - 2; i <= rowAverage + 2; i++) {
                for (int j = colAverage - 2; j <= colAverage + 2; j++) {
                    color[i][j] = 255;
                    otherColor1[i][j] = 255;
                    otherColor2[i][j] = 255;
                }
            }
        }

    }

    public boolean doubleBlobDetection(short[][] color, short[][] otherColor1, short[][] otherColor2, int colorVal, DImage img){
        ArrayList<Point> points=new ArrayList<>();
        ArrayList<Integer> avgColorValues=new ArrayList<>();
        int sumColor = 0;
        int avgColor=0;
        int numTotalpixels = 0;
        for (int row = 30; row < color.length-30; row+=5) {
            for (int col = 30; col < color[row].length-30; col+=5) {
                if (color[row][col] == colorVal) {
                    if (row >= 30 && col >= 30 && row <= img.getHeight() - 31 && col <= img.getWidth() - 31) {
                        for (int i = row - 30; i <= row + 30; i++) {
                            for (int j = col - 30; j <= col + 30; j++) {
                                sumColor += color[i][j];
                                numTotalpixels++;
                            }
                        }
                    }
                    avgColor= sumColor/numTotalpixels;
                    avgColorValues.add(avgColor);
                    points.add(new Point(row,col));
                    System.out.println("AverageColor: "+avgColor+ ", targetColor: "+colorVal);
                }
            }
        }
        //now we need to loop over the avgColors that we did and then see which is the closest to the value we want and that is most likely the center.
        return true;
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