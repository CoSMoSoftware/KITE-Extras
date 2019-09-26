package io.cosmosoftware.kite.steps;

import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static io.cosmosoftware.kite.entities.Timeouts.TEN_SECOND_INTERVAL;
import static io.cosmosoftware.kite.util.ReportUtils.saveScreenshotPNG;
import static io.cosmosoftware.kite.util.ReportUtils.timestamp;
import static io.cosmosoftware.kite.util.TestUtils.videoCheck;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.pages.BasePage;
import io.cosmosoftware.kite.report.Status;
import java.util.List;
import org.openqa.selenium.WebElement;

public class VideoDisplayCheck extends TestStep {
  protected final int videoIndex;
  protected final String videoName;
  protected final BasePage page;

  /**
   * Instantiates a new Test step.
   *
   * @param runner the runner
   */
  public VideoDisplayCheck(Runner runner, BasePage page, int videoIndex, String videoName, boolean optional) {
    super(runner);
    this.page = page;
    this.videoIndex = videoIndex;
    this.videoName = videoName;
    setOptional(optional);
  }
  /**
   * Instantiates a new Test step.
   *
   * @param runner the runner
   */
  public VideoDisplayCheck(Runner runner, BasePage page, int videoIndex, String videoName) {
    super(runner);
    this.page = page;
    this.videoIndex = videoIndex;
    this.videoName = videoName;
  }

  /**
   * Instantiates a new Test step.
   *
   * @param runner the runner
   */
  public VideoDisplayCheck(Runner runner, BasePage page, int videoIndex, boolean optional) {
    this(runner,page,videoIndex,"");
    this.setOptional(optional);
  }

  /**
   * Instantiates a new Test step.
   *
   * @param runner the runner
   */
  public VideoDisplayCheck(Runner runner, BasePage page, int videoIndex) {
    this(runner,page,videoIndex,"");
  }

  @Override
  protected void step() throws KiteTestException {
    try {
      List<WebElement> videos = page.getVideos();
      if (videos.isEmpty()) {
        throw new KiteTestException(
            "Unable to find any <video> element on the page", Status.FAILED);
      }

      String videoCheck = videoCheck(webDriver, 0,ONE_SECOND_INTERVAL, TEN_SECOND_INTERVAL);
      reporter.screenshotAttachment(report,
          "Video (index " + (this.videoName.isEmpty()
              ? "index " + this.videoIndex
              : this.videoName ) + ") " + timestamp(), saveScreenshotPNG(webDriver));
      if (!"video".equalsIgnoreCase(videoCheck)) {
        reporter.screenshotAttachment(report,
            "Video (index " +(this.videoName.isEmpty()
                ? "index " + this.videoIndex
                : this.videoName ) + ") " + timestamp(), saveScreenshotPNG(webDriver));
        reporter.textAttachment(report, "Video (" + (this.videoName.isEmpty()
            ? "index " + this.videoIndex
            : this.videoName ) + ") " , videoCheck, "plain");
      }
    } catch (KiteTestException e) {
      throw e;
    } catch (Exception e) {
      throw new KiteTestException("Error looking for the video", Status.BROKEN, e);
    }
  }

    @Override
  public String stepDescription() {
    return "Check the display for video (" +
        (this.videoName.isEmpty()
            ? "index " + this.videoIndex
            : this.videoName )
        +")";
  }
}
