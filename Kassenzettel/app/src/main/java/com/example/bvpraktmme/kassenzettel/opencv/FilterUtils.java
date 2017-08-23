package com.example.bvpraktmme.kassenzettel.opencv;

import android.graphics.Bitmap;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class FilterUtils {

    /**
     * Method to apply greyscale filter
     * @param image original image (source)
     * @param convertedImage filtered image object (target)
     */
    public static void greyscaleFilter(final Mat image, Mat convertedImage) {
        Imgproc.cvtColor(image, convertedImage, Imgproc.COLOR_BGR2GRAY);
    }

    /**
     * Method to apply guassianBlur filter
     * @param image original image (source)
     * @param convertedImage filtered image object (target)
     * @param ksize Gaussian kernel size. ksize.width and ksize.height can differ but they both must be positive and odd.
     * @param sigmaX Gaussian kernel standard deviation in X direction.
     */
    public static void guassianBlur(final Mat image, Mat convertedImage, Size ksize, int sigmaX) {
        Imgproc.GaussianBlur(image, convertedImage,ksize, sigmaX);
    }

    /**
     * Method to convert Bitmap format to Mat format
     * @param image original image (source)
     * @return convertedImage
     */
    public static Mat getMatBy(final Bitmap image) {
        return new Mat (image.getWidth(), image.getHeight(), CvType.CV_8UC1);
    }

    /**
     * Method to apply threshold filter
     * @param image original image (source)
     * @param convertedImage filtered image object (target)
     */
    public static void threshold(final Mat image, Mat convertedImage) {
        Imgproc.threshold(image, convertedImage, 0, 255, Imgproc.THRESH_OTSU);
    }
}
