package dev.ioliver.ujthbackend.Controllers;

import dev.ioliver.ujthbackend.services.OpenCVService;
import dev.ioliver.ujthbackend.services.TargetService;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("utjh")
public class UTJHController {

  private final TargetService TARGET_SERVICE =  TargetService.getInstance();
  private final OpenCVService OC_SERVICE = OpenCVService.getInstace();

  @PostMapping("check/{targetNumber}")
  public boolean checkIfImageMatch(@RequestParam("file") MultipartFile image, @PathVariable int targetNumber) {
    if(targetNumber < TARGET_SERVICE.getTARGETS().size())
      throw new RuntimeException("The target number is invalid.");

    Mat mat = OC_SERVICE.multipartToMat(image);
    return OC_SERVICE.checkIfImagesMatchWithTheTarget(mat, TARGET_SERVICE.getTARGETS().get(targetNumber - 1));
  }

  @GetMapping("target/{targetNumber}")
  public ResponseEntity<byte[]> checkIfImageMatch(@PathVariable int targetNumber) {
    if(targetNumber < TARGET_SERVICE.getTARGETS().size())
      throw new RuntimeException("The target number is invalid.");

    MatOfByte matOfByte = new MatOfByte();
    Imgcodecs.imencode(".jpg", TARGET_SERVICE.getTARGETS().get(targetNumber - 1).getImage(), matOfByte);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.IMAGE_JPEG);
    return new ResponseEntity<>(matOfByte.toArray(), headers, HttpStatus.OK);
  }
}
