package com.example.bvpraktmme.kassenzettel.opencv;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;

public class ObjectRecognizer {

    private int DELAY_CAPTION = 1500;
    private int DELAY_BLUR = 100;
    private int MAX_KERNEL_LENGTH = 31;

    private Mat imageAsMat;
    private Mat convertedImage;

    private Bitmap image;

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap applyFilters() {

        convertedImage = FilterUtils.getMatBy(image);
        imageAsMat = FilterUtils.getMatBy(image);
        Utils.bitmapToMat(image, imageAsMat);

        FilterUtils.greyscaleFilter(imageAsMat, convertedImage);
        FilterUtils.guassianBlur(convertedImage, convertedImage, new Size(45,45), 0);

        //Imgcodecs.imwrite("/sdcard/grayscale.png", destinationImage);

        Bitmap bm = Bitmap.createBitmap(convertedImage.cols(), convertedImage.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(convertedImage, bm);

        return bm;
    }
}
