package com.example.bvpraktmme.kassenzettel.opencv;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.opencv.imgproc.Imgproc.MORPH_RECT;

public class ObjectRecognizer {

    private Mat imageAsMat;
    private Mat convertedImage;
    private Mat convertedImage2;
    private Bitmap image;

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap applyFilters() {

        convertedImage = FilterUtils.getMatBy(image);
        convertedImage2 = FilterUtils.getMatBy(image); // need to show in presentation
        imageAsMat = FilterUtils.getMatBy(image);

        Utils.bitmapToMat(image, imageAsMat);

        // applying filters
        FilterUtils.greyscaleFilter(imageAsMat, convertedImage);
        FilterUtils.threshold(convertedImage, convertedImage2);
        FilterUtils.guassianBlur(convertedImage, convertedImage, new Size(15,15), 0);
        FilterUtils.threshold(convertedImage, convertedImage);

        int whiteCount = Core.countNonZero(convertedImage);
        double blackSize = convertedImage.size().area() - whiteCount;

        if(whiteCount > blackSize) {
            Core.bitwise_not(convertedImage, convertedImage);
        }

        // preparation to find blobs
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(15,5));
        Mat dilated = new Mat();
        Imgproc.dilate(convertedImage, dilated, kernel, new Point(-1,-1), 5);

        // the list of blobs
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(dilated, contours, dilated.clone(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<RotatedRect> rotatedRects = new ArrayList<>();

        // preparation for rotation
        // firstly change the type of blobs to RotatedRect object from MatOfPoint Object
        for (MatOfPoint contour : contours) {
            MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contour.toArray());
            RotatedRect rotatedRect = Imgproc.minAreaRect(matOfPoint2f);
            rotatedRects.add(rotatedRect);
        }

        // preparation to find biggest blob
        RotatedRect biggestRect = null;
        if(rotatedRects.isEmpty()) {
            // TODO if there are not any blob
        } else {
            // finding biggest blob
            biggestRect = new RotatedRect();
            for(RotatedRect rect : rotatedRects) {
                if(biggestRect.size.width*biggestRect.size.height < rect.size.height*rect.size.width) {
                    biggestRect = rect;
                }
            }
        }

        // Rotate the founded Kassenzettel
        double angle = biggestRect.angle;
        Size size = biggestRect.size;

        if (angle < -45) {
            angle += 90.0;
            double temp = size.height;
            size.height = size.width;
            size.width = temp;
        }

        // to show in presentation
        Mat transform = Imgproc.getRotationMatrix2D(biggestRect.center, angle, 1.0);
        Mat rotated = new Mat();
        Imgproc.warpAffine(convertedImage2, rotated, transform, convertedImage2.size(), Imgproc.INTER_CUBIC);

        Mat cropped = new Mat();
        Size newS = new Size(size.width-175, size.height-175);
        Imgproc.getRectSubPix(rotated, newS, biggestRect.center, cropped);
        Core.copyMakeBorder(cropped, cropped, 10,10,10,10,Core.BORDER_CONSTANT, new Scalar(0));

        Bitmap bm = Bitmap.createBitmap(cropped.cols(), cropped.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cropped, bm);
        return bm;
    }

    /**
     * Method to find and cut out the area of the bill where items and prices area displayed
     * @return
     */
    public Bitmap findPriceArea(){
        //Transform the image into a mat
        Mat baseMat = FilterUtils.getMatBy(image);
        Utils.bitmapToMat(image, baseMat);
        Mat convertedImage = new Mat();

        //Conversion to greyscale then binary
        FilterUtils.greyscaleFilter(baseMat, convertedImage);
        FilterUtils.threshold(convertedImage, convertedImage);
        Mat lines = new Mat();
        Mat canny = new Mat();
        //Find edges then find the lines
        Imgproc.Canny(convertedImage, canny, 50, 200);
        //Set the parameters for which lines to detect

        double minLineLength = convertedImage.size().width*0.60;

        Imgproc.HoughLinesP(canny, lines,1, Math.PI/180,50,minLineLength,23);

        //Crop the image
        //Specify the size to crop, width is the same as original height goes up until the found line
        double firstDoubleLine = findFirstDoubleLine(lines, convertedImage.size().height);
        Size cropSize = new Size(convertedImage.size().width + 20, firstDoubleLine );
        //Create mat to crop into
        Mat cropped = new Mat();
        //Calculate the center , half the width and half the length of first line
        Point center = new Point(convertedImage.size().width/2, firstDoubleLine/2);
        Imgproc.getRectSubPix(convertedImage, cropSize, center, cropped );


        Bitmap bm = Bitmap.createBitmap(cropped.cols(), cropped.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cropped, bm);

        return bm;
    }

    private double findFirstDoubleLine(Mat lines, double imageSize){
        ArrayList<Double> yCoords = new ArrayList<>();

        List <Double> sortedY = new ArrayList<Double>();
        for(int i = 0; i< lines.rows()-1; i++) {
            double[] vec = lines.get(i, 0);
            double [] vec2 = lines.get(i+1, 0);
            double y1 = vec[1];
            double y2 = vec2[1];

            if(y1> imageSize*0.30 && y2 > imageSize*0.30 && y1<imageSize*0.8 && y2<imageSize*0.8) {
                sortedY.add(y1);
                sortedY.add(y2);
            }
        }

        Collections.sort(sortedY);
        for (int i = 0; i < sortedY.size()-1; i++) {

            double y1 = sortedY.get(i);
            double y2 = sortedY.get(i+1);

            if(Math.abs(y1 - y2) < 120 && Math.abs(y1 - y2) > 0){
                yCoords.add(Math.max(y1, y2));
            }
        }

        return Collections.min(yCoords);
    }
}
