package dev.ioliver.ujthbackend.services;

import java.io.File;

public class TargetService {

  private static TargetService instance;
  private final File targetsPath = new File("./targets");

  private TargetService() {
    do {
      if (targetsPath.exists()) break;
    } while (!targetsPath.mkdir());
  }

  public static TargetService getInstance() {
    if (instance == null) instance = new TargetService();
    return instance;
  }

}
