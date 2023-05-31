package dev.ioliver.ujthbackend;

import org.opencv.core.*;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class TesteOpenCV {

  static {
    nu.pattern.OpenCV.loadLocally();
  }

  public static void main(String[] args) {
    Mat imagem1 = Imgcodecs.imread("./targets/3.jpg");
    Mat imagem2 = Imgcodecs.imread("./targets/3test.jpg");

    Mat cinza1 = new Mat();
    Mat cinza2 = new Mat();
    Imgproc.cvtColor(imagem1, cinza1, Imgproc.COLOR_BGR2GRAY);
    Imgproc.cvtColor(imagem2, cinza2, Imgproc.COLOR_BGR2GRAY);

    MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
    MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
    Mat descritores1 = new Mat();
    Mat descritores2 = new Mat();

    ORB detector = ORB.create();
    detector.detectAndCompute(cinza1, new Mat(), keypoints1, descritores1);
    detector.detectAndCompute(cinza2, new Mat(), keypoints2, descritores2);

    MatOfDMatch matches = new MatOfDMatch();
    DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
    matcher.match(descritores1, descritores2, matches);

    List<DMatch> correspondenciasList = matches.toList();
    correspondenciasList.sort((d1, d2) -> Float.compare(d1.distance, d2.distance));

    float limiteDistancia = 50.0f; // Ajuste esse valor de acordo com a sua necessidade
    List<DMatch> correspondenciasBoas = new ArrayList<>();
    for (DMatch correspondencia : correspondenciasList) {
      if (correspondencia.distance < limiteDistancia) {
        correspondenciasBoas.add(correspondencia);
      }
    }

    System.out.println(correspondenciasBoas.size());

    int limiteCorrespondencias = 10; // Ajuste esse valor de acordo com a sua necessidade
    if (correspondenciasBoas.size() >= limiteCorrespondencias) {
      System.out.println("As imagens são do mesmo ambiente/lugar.");
    } else {
      System.out.println("As imagens não são do mesmo ambiente/lugar.");
    }
  }
}
