package io.cosmosoftware.kite.steps;

import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static io.cosmosoftware.kite.entities.Timeouts.SHORT_TIMEOUT;
import static io.cosmosoftware.kite.util.ReportUtils.saveScreenshotPNG;
import static io.cosmosoftware.kite.util.ReportUtils.timestamp;
import static io.cosmosoftware.kite.util.TestUtils.videoCheck;
import static io.cosmosoftware.kite.util.TestUtils.videoCheckByBytes;

import io.cosmosoftware.kite.entities.VideoQuality;
import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.pages.BasePage;
import io.cosmosoftware.kite.report.Status;
import java.util.List;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

public class VideoDisplayCheck extends TestStep {
  protected Object videoIndexOrId;
  protected final String videoName;
  protected final BasePage page;
  protected String customMessage;
  protected int interval = ONE_SECOND_INTERVAL;
  protected int duration = SHORT_TIMEOUT/2;
  protected boolean byteComparing = false;
  protected boolean allowFreeze = false;
  protected Rectangle rectangle;

  /**
   * Instantiates a new Test step.
   *
   * @param runner the runner
   */
  public VideoDisplayCheck(Runner runner, BasePage page, Object videoIndexOrId, String videoName, boolean optional) {
    super(runner);
    this.page = page;
    this.videoIndexOrId = videoIndexOrId;
    this.videoName = videoName;
    setOptional(optional);
  }
  /**
   * Instantiates a new Test step.
   *
   * @param runner the runner
   */
  public VideoDisplayCheck(Runner runner, BasePage page,  Object videoIndexOrId, String videoName) {
    super(runner);
    this.page = page;
    this.videoIndexOrId = videoIndexOrId;
    this.videoName = videoName;
  }

  /**
   * Instantiates a new Test step.
   *
   * @param runner the runner
   */
  public VideoDisplayCheck(Runner runner, BasePage page,  Object videoIndexOrId, boolean optional) {
    this(runner,page,videoIndexOrId,"");
    this.setOptional(optional);
  }

  /**
   * Instantiates a new Test step.
   *
   * @param runner the runner
   */
  public VideoDisplayCheck(Runner runner, BasePage page,  Object videoIndexOrId) {
    this(runner,page,videoIndexOrId,"");
  }

  @Override
  protected void step() throws KiteTestException {
    preliminaryCheck();
    String videoCheck = mainCheck();
    this.takeScreenshot();
    reporter.textAttachment(report, getVideoName(), videoCheck, "plain");
    if (resultNotOK(videoCheck)) {
      throw new KiteTestException(customMessage == null ? getVideoName() + " did not display correctly" : customMessage, Status.FAILED);
    }
  }

  protected void takeScreenshot() throws KiteTestException {
    if (this.byteComparing) {
      reporter.screenshotAttachment(report,
          getVideoName() + timestamp(), saveScreenshotPNG(webDriver, rectangle));
    } else {
      reporter.screenshotAttachment(report,
          getVideoName() + timestamp(), saveScreenshotPNG(webDriver));
    }
  }

  protected boolean resultNotOK(String videoCheck) throws KiteTestException {
    if (VideoQuality.VIDEO.toString().equals(videoCheck)) {
      return false;
    }
    if (VideoQuality.FREEZE.toString().equals(videoCheck)) {
      return !allowFreeze;
    }
    return true;
  }

    @Override
  public String stepDescription() {
    return "Check the display for " + getVideoName();
  }

  protected String getVideoName() {
    return "Video ("
        + (this.videoName.isEmpty() ? "" : (this.videoName + " - "))
        + (this.videoIndexOrId instanceof String ? "id: " : "index ") + this.videoIndexOrId
        + ")";
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

  public void setAllowFreeze(boolean allowFreeze) {
    this.allowFreeze = allowFreeze;
  }

  protected String mainCheck() throws KiteTestException {
    if (!this.byteComparing) {
      return videoCheck(webDriver, videoIndexOrId, interval, duration);
    } else {
      return videoCheckByBytes(webDriver, this.rectangle);
    }
  }



  protected void preliminaryCheck() throws KiteTestException {
    List<WebElement> videos = page.getVideos();
    if (videos != null) { // the page does not overwrite the base function
      if (videos.isEmpty()) {
        throw new KiteTestException(
            "Unable to find any UI element on the page matching this check", Status.FAILED);
      }
    }
  }

  protected void setCustomMessage(String customMessage) {
    this.customMessage = customMessage;
  }

  public void setByteComparing(boolean byteComparing) {
    this.byteComparing = byteComparing;
  }

  public boolean compareByBytes() {
    return this.byteComparing;
  }

  public void setRectangle(Rectangle rectangle) {
    this.rectangle = rectangle;
  }

  public void setVideoIndexOrId(Object videoIndexOrId) {
    this.videoIndexOrId = videoIndexOrId;
  }
}
