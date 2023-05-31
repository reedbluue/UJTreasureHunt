package dev.ioliver.ujthbackend.services;

import dev.ioliver.ujthbackend.models.Target;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenCVService {
  private static OpenCVService instance;

  static {
    nu.pattern.OpenCV.loadLocally();
  }

  private final ORB DETECTOR = ORB.create();
  private final DescriptorMatcher MATCHER = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
  private final float MIN_CORRESPONDENCES = 300;

  private OpenCVService() {
  }

  public static OpenCVService getInstace() {
    if (instance == null) instance = new OpenCVService();
    return instance;
  }

  public Mat getDescriptor(Mat image) {
    Mat gray = new Mat();
    Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

    MatOfKeyPoint keypoints = new MatOfKeyPoint();
    Mat descriptors = new Mat();

    DETECTOR.detectAndCompute(gray, new Mat(), keypoints, descriptors);

    return descriptors;
  }

  public Mat multipartToMat(MultipartFile img) {
    byte[] imgBytes = new byte[0];
    try {
      imgBytes = img.getBytes();
      MatOfByte matOfByte = new MatOfByte(imgBytes);
      return Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_UNCHANGED);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean checkIfImagesMatchWithTheTarget(Mat image, Target target) {
    Mat targetMat = target.getImage();
    Mat originalGrayMat = new Mat();
    Mat targetGrayMat = new Mat();
    Imgproc.cvtColor(image, originalGrayMat, Imgproc.COLOR_BGR2GRAY);
    Imgproc.cvtColor(targetMat, targetGrayMat, Imgproc.COLOR_BGR2GRAY);

    MatOfKeyPoint originalKeypoints = new MatOfKeyPoint();
    MatOfKeyPoint targetKeypoints = new MatOfKeyPoint();
    Mat originalDescriptor = new Mat();
    Mat targetDescriptor = new Mat();

    DETECTOR.detectAndCompute(originalGrayMat, new Mat(), originalKeypoints, originalDescriptor);
    DETECTOR.detectAndCompute(targetGrayMat, new Mat(), targetKeypoints, targetDescriptor);

    MatOfDMatch matches = new MatOfDMatch();
    MATCHER.match(originalDescriptor, targetDescriptor, matches);

    List<DMatch> correspondenceList = matches.toList();
    correspondenceList.sort((d1, d2) -> Float.compare(d1.distance, d2.distance));

    List<DMatch> goodCorrespondences = new ArrayList<>();

    double minDist = Double.MAX_VALUE;
    double maxDist = Double.MIN_VALUE;

    for (DMatch correspondence : correspondenceList) {
      double dist = correspondence.distance;
      if (dist < minDist) {
        minDist = dist;
      }
      if (dist > maxDist) {
        maxDist = dist;
      }
    }

    double thresholdDist = 2 * minDist;

    for (DMatch correspondence : correspondenceList) {
      if (correspondence.distance < thresholdDist) {
        goodCorrespondences.add(correspondence);
      }
    }

    System.out.println(goodCorrespondences.size());

    return goodCorrespondences.size() <= MIN_CORRESPONDENCES;
  }
}
