package dev.ioliver.ujthbackend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ioliver.ujthbackend.dto.TargetJson;
import dev.ioliver.ujthbackend.models.Target;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class TargetService {

  private static final int TARGETS_AMOUNT = 3;
  private static TargetService instance;
  private final OpenCVService OC_SERVICE = OpenCVService.getInstace();
  private final File targetsPath = new File("./targets");
  private final File rawTargets = new File("./targets/targets.json");
  private final ObjectMapper mapper = new ObjectMapper();
  private final LinkedList<TargetJson> rawTargetsJson;
  private final LinkedList<Target> TARGETS;

  private TargetService(int targetsAmount) {
    boolean pathCreated;
    boolean fileCreated;
    do {
      if (targetsPath.exists() && rawTargets.exists()) break;
      try {
        pathCreated = targetsPath.mkdir();
        fileCreated = rawTargets.createNewFile();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } while (!pathCreated || !fileCreated);

    if (rawTargets.length() == 0) {
      try {
        FileWriter writer = new FileWriter(rawTargets);
        writer.write("[]");
        writer.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    this.rawTargetsJson = getRawTargetsJson();

    if (this.rawTargetsJson.size() != targetsAmount)
      throw new RuntimeException("The amount of targets don't correspond with the config in targets.json");

    TARGETS = new LinkedList<>(this.rawTargetsJson.stream()
        .map(raw -> {
          Mat image = Imgcodecs.imread(raw.path());
          return new Target(raw.description(), image, OC_SERVICE.getDescriptor(image));
        }).toList());
  }

  public static TargetService getInstance() {
    if (instance == null) instance = new TargetService(TARGETS_AMOUNT);
    return instance;
  }

  public LinkedList<Target> getTARGETS() {
    return TARGETS;
  }

  private LinkedList<TargetJson> getRawTargetsJson() {
    try {
      return new LinkedList<>(Arrays.stream(mapper.readValue(rawTargets, TargetJson[].class)).toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public LinkedList<Target> getList() {
    return this.TARGETS;
  }
}
