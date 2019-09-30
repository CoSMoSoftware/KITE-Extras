package io.cosmosoftware.kite.steps;

import static io.cosmosoftware.kite.util.TestUtils.videoQualityCheck;

import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.pages.BasePage;

public class VideoQualityCheck extends VideoDisplayCheck {

  protected boolean allowJerky = false;

  public VideoQualityCheck(Runner runner, BasePage page, Object videoIndexOrId,
      String videoName, boolean optional) {
    super(runner, page, videoIndexOrId, videoName, optional);
  }

  public VideoQualityCheck(Runner runner, BasePage page, Object videoIndexOrId,
      String videoName) {
    super(runner, page, videoIndexOrId, videoName);
  }

  public VideoQualityCheck(Runner runner, BasePage page, Object videoIndexOrId, boolean optional) {
    super(runner, page, videoIndexOrId, optional);
  }

  public VideoQualityCheck(Runner runner, BasePage page, Object videoIndexOrId) {
    super(runner, page, videoIndexOrId);
  }

  @Override
  public String stepDescription() {
    return "Check the video quality for " + getVideoName();
  }

  public void setAllowJerky(boolean allowJerky) {
    this.allowJerky = allowJerky;
  }

  public boolean allowJerky() {
    return allowJerky;
  }

  @Override
  protected String mainCheck() {
    return videoQualityCheck(webDriver, videoIndexOrId, interval, duration);
  }
}