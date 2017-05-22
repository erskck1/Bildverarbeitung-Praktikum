package com.example.bvpraktmme.kassenzettel.opencv;

import android.graphics.Bitmap;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class FilterUtils {

    public static void greyscaleFilter(final Mat image, Mat convertedImage) {
        Imgproc.cvtColor(image, convertedImage, Imgproc.COLOR_BGR2GRAY);
    }

    public static void guassianBlur(final Mat image, Mat convertedImage, Size ksize, int sigmaX) {
        Imgproc.GaussianBlur(image, convertedImage,ksize, sigmaX);
    }

    public static Mat getMatBy(final Bitmap image) {
        return new Mat (image.getWidth(), image.getHeight(), CvType.CV_8UC1);
    }
}
