package dev.ioliver.ujthbackend;

import dev.ioliver.ujthbackend.services.TargetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;

public class TargetServiceTest {
  TargetService SERVICE = TargetService.getInstance();

  @Test
  public void needToBeOnlyOneInstance() {
    TargetService NEW = TargetService.getInstance();
    Assertions.assertEquals(SERVICE, NEW);
  }

  @Test
  public void needCreateATargetsFolder() throws MalformedURLException {
    File targetFolder = new File("./targets");
    Assertions.assertTrue(targetFolder.exists());
  }
}
