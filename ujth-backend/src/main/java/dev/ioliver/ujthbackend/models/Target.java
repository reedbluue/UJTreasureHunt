package dev.ioliver.ujthbackend.models;

import lombok.Data;
import org.opencv.core.Mat;

@Data
public class Target {
  private final String description;
  private final Mat image;
  private final Mat descriptors;
}
