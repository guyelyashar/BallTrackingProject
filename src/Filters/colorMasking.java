package Filters;


import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;

import java.util.ArrayList;


public class colorMasking implements PixelFilter, Interactive {
    private ArrayList<int[]> colors;  // Each element is an array of 3 integers: {R, G, B}
    private double THRESHOLD;

    public colorMasking(){
        colors=new ArrayList<>();
        colors.add(new int[]{30, 55, 150});  // Blue target
        colors.add(new int[]{50, 143, 50});  // Green target
        colors.add(new int[]{255,60,60}); //red target
        THRESHOLD = 45;
    }
    @Override
    public DImage processImage(DImage img) {
        short[][] red= img.getRedChannel();
        short[][] green= img.getGreenChannel();
        short[][] blue=img.getBlueChannel();

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
                if(matched){
                    red[ro][co]=255;
                    green[ro][co]=255;
                    blue[ro][co]=255;
                }
                else{
                    red[ro][co]=0;
                    green[ro][co]=0;
                    blue[ro][co]=0;
                }
            }
        }

        img.setColorChannels(red, green, blue);
        return img;
    }


    private double dist(int row, int col,short[][] red, short[][] green, short[][] blue, int RedTargetColor, int GreenTargetColor, int BlueTargetColor){
        int changered= Math.abs(RedTargetColor-red[row][col]);
        int changegreen=Math.abs(GreenTargetColor-green[row][col]);
        int changeblue=Math.abs(BlueTargetColor-blue[row][col]);
        return Math.sqrt((changered * changered) +(changegreen*changegreen)+(changeblue*changeblue));
    }




    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        short[][]red= img.getRedChannel();
        short[][]green=img.getGreenChannel();
        short[][]blue=img.getBlueChannel();
    }


    @Override
    public void keyPressed(char key) {
        if(key=='+' || key == '=') {
            THRESHOLD++;
            System.out.println(THRESHOLD);
        }
        if(key == '-' || key =='_'){
            THRESHOLD--;
            System.out.println(THRESHOLD);
        }

    }
}

