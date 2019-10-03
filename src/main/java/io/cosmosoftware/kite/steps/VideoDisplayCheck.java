package io.cosmosoftware.kite.steps;

import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static io.cosmosoftware.kite.entities.Timeouts.SHORT_TIMEOUT;
import static io.cosmosoftware.kite.util.ReportUtils.saveScreenshotPNG;
import static io.cosmosoftware.kite.util.ReportUtils.timestamp;
import static io.cosmosoftware.kite.util.TestUtils.videoCheck;

import io.cosmosoftware.kite.entities.VideoQuality;
import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.pages.BasePage;
import io.cosmosoftware.kite.report.Status;
import java.util.List;
import org.openqa.selenium.WebElement;

public class VideoDisplayCheck extends TestStep {
  protected final Object videoIndexOrId;
  protected final String videoName;
  protected final BasePage page;
  protected String customMessage;
  protected int interval = ONE_SECOND_INTERVAL;
  protected int duration = SHORT_TIMEOUT/2;
  protected boolean allowFreeze = false;

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
    reporter.screenshotAttachment(report,
        getVideoName() + timestamp(), saveScreenshotPNG(webDriver));
    reporter.textAttachment(report, getVideoName() , videoCheck, "plain");
    if (!VideoQuality.VIDEO.toString().equals(videoCheck)) {
      throw new KiteTestException(customMessage == null ? getVideoName() + " did not display correctly" : customMessage, Status.FAILED);
    }
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

  protected String mainCheck() {
    return videoCheck(webDriver, videoIndexOrId, interval, duration);
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
}
