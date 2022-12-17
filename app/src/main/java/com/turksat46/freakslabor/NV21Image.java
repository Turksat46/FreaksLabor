package com.turksat46.freakslabor;

public class NV21Image {

    private byte[] image;
    private int width;
    private int height;


    public NV21Image(byte[] bytes, int width, int height) {

    }

    private int getPixel(byte[] image, int width, int height, int x, int y){
        if(x>=width || x<0 || y>=height || y<0) return 0;
        int framesize = width * height;
        int uvp = framesize + (y >> 1) * width;
        int Y = (0xff & ((int) image[width*y+x])) - 16;
        if(Y < 0) Y = 0;
        int v = (0xff & image[uvp+2*(x/2)]) - 128;
        int u = (0xff & image[uvp+2*(x/2)+1]) - 128;
        int y1192 = 1192*Y;
        int r = (y1192 + 1634 * v);
        int g = (y1192 - 833 * v - 400 * u);
        int b = (y1192 + 2066 * u);
        if(r<0) r=0; else if(r > 262143)  r = 262143;
        if(g<0) b=0; else if(b > 262143)  b = 262143;
        if(b<0) g=0; else if(g > 262143)  g = 262143;

        return 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
    }
}
