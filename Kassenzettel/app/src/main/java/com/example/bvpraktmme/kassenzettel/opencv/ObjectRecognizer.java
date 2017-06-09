package com.example.bvpraktmme.kassenzettel.opencv;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;

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
        convertedImage2 = FilterUtils.getMatBy(image);
        imageAsMat = FilterUtils.getMatBy(image);

        Utils.bitmapToMat(image, imageAsMat);

        FilterUtils.greyscaleFilter(imageAsMat, convertedImage);
        FilterUtils.threshold(convertedImage, convertedImage2);
        FilterUtils.guassianBlur(convertedImage, convertedImage, new Size(15,15), 0);
        FilterUtils.threshold(convertedImage, convertedImage);

        int whiteCount = Core.countNonZero(convertedImage);
        double blackSize = convertedImage.size().area() - whiteCount;

        if(whiteCount > blackSize) {
            Core.bitwise_not(convertedImage, convertedImage);
        }

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(15,5));

        Mat dilated = new Mat();

        Imgproc.dilate(convertedImage, dilated, kernel, new Point(-1,-1), 5);


        List<MatOfPoint> contours = new ArrayList<>();

        Imgproc.findContours(dilated, contours, dilated.clone(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<RotatedRect> rotatedRects = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contour.toArray());
            RotatedRect rotatedRect = Imgproc.minAreaRect(matOfPoint2f);
            rotatedRects.add(rotatedRect);
        }
        RotatedRect biggestRect = null;

        if(rotatedRects.isEmpty()) {

        } else {

            biggestRect = new RotatedRect();
            for(RotatedRect rect : rotatedRects) {
                if(biggestRect.size.width*biggestRect.size.height < rect.size.height*rect.size.width) {
                    biggestRect = rect;
                }
            }
        }


        double angle = biggestRect.angle;
        Size size = biggestRect.size;

            if (angle < -45) {
                angle += 90.0;
                double temp = size.height;
                size.height = size.width;
                size.width = temp;
            }

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
        //TODO use hough lines with canny edge detector
        Mat lines = new Mat();
        Mat canny = new Mat();
        //Find edges then find the lines
        Imgproc.Canny(convertedImage, canny, 50, 200);
        //Set the parameters for which lines to detect
        Imgproc.HoughLinesP(canny, lines,1, Math.PI/180,50,300,20 );
        //Draw the lines onto the picture
        for (int i = 0; i < lines.rows() ; i++) {
            double[] vec = lines.get(i, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            Imgproc.line(convertedImage, start, end, new Scalar(0,255, 0, 255),5);

        }
        //TODO find the first line or two lines close to each other and cut the picture accordingly

        Bitmap bm = Bitmap.createBitmap(convertedImage.cols(), convertedImage.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(convertedImage, bm);

        return bm;
    }
}
