package io.cosmosoftware.kite.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import io.cosmosoftware.kite.exception.KiteInteractionException;
import io.cosmosoftware.kite.exception.KiteTestException;
import org.apache.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;

import static io.cosmosoftware.kite.util.WebDriverUtils.getWindowSize;


public class MobilePage extends BasePage {
  
  protected MobilePage(WebDriver webDriver, Logger logger) {
    super(webDriver, logger);
    if (!(webDriver instanceof MobileDriver)) {
      logger.warn("This webdriver is not suitable to be used for this page. " +
        "Some of the interactions will now perform properly. " +
        "Please consider using AppiumDriver or MobileDriver.");
    }
  }
  
  /**
   * Click element.
   *
   * @param element   a given WebElement
   * @param useAction option whether to use the Actions class from org.openqa.selenium.interactions
   *
   */
  @Override
  public void click(WebElement element, boolean useAction) throws KiteInteractionException {
    click(element); // Actions method does not apply here
  }
  
  /**
   * Performs a double tap action at a specific point
   *
   * @param x         the x
   * @param y         the y
   */
  @Override
  public void doubleTap(int x, int y) {
    new TouchAction((MobileDriver) this.webDriver)
      .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).press(PointOption.point(x, y))
      .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).release()
      .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).press(PointOption.point(x, y))
      .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).release()
      .perform();
  }
  
  /**
   * Press down at the begin point and move to end point. Can be used to draw a line if the cursor
   * is in drawing/annotating mode.
   *
   * @param begin     point to begin.
   * @param end       end point
   */
  @Override
  public void drawLine(Point begin, Point end) throws KiteInteractionException {
    try {
      TouchAction action = new TouchAction((MobileDriver) webDriver);
      action.press(PointOption.point(begin.x, begin.y))
        .moveTo(PointOption.point(end.x, end.y))
        .release().perform();
    } catch (Exception e) {
      processInteractionException("draw line", e);
    }
  }
  
  /**
   * Press down at the begin point and move to fill the area of a square. Can be used to draw a
   * square if the cursor is in drawing/annotating mode.
   *
   * @param begin            point to begin.
   * @param squareSideLength length of the side of the square.
   */
  @Override
  public void drawSquare(Point begin, int squareSideLength) throws KiteInteractionException {
    try {
      final int waitTime = 50;
      int startX = begin.x;
      int startY = begin.y;
  
      TouchAction action = new TouchAction((MobileDriver) webDriver);
      action.press(PointOption.point(startX, startY));
  
      for (int measure = squareSideLength / 20; measure <= squareSideLength; measure += squareSideLength / 20) {
        int length = squareSideLength - measure;
        action.waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitTime)))
          .moveTo(PointOption.point(startX + length, startY))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitTime)))
          .moveTo(PointOption.point(startX + length, startY - length))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitTime)))
          .moveTo(PointOption.point(startX, startY - length))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitTime)))
          .moveTo(PointOption.point(startX, startY));
      }
      action.waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitTime)))
        .release()
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitTime)))
        .perform();
    } catch (Exception e) {
      processInteractionException("draw square",e);
    }
  }
  
  /**
   * Moves currently focused app to background for a duration
   *
   * @param duration  duration to put in background
   */
  public void moveAppToBackground(Duration duration) throws KiteInteractionException {
    try {
      ((AppiumDriver) webDriver).runAppInBackground(duration);
    } catch (Exception e) {
      processInteractionException("move app to background", e);
    }
  }
  
  /**
   * Open Activity/App running in background
   *
   * @param bundleId  id (package name) of the app
   */
  public void openAppWithBundleId(String bundleId) throws KiteInteractionException {
    try {
      ((AppiumDriver) webDriver).activateApp(bundleId);
    } catch (Exception e) {
      processInteractionException("open app with bundle id", e);
    }
  }
  
  /**
   * Opens a new activity (changes to new activity)
   *
   * @param activity  activity to open.
   */
  public void starAppActivity(Activity activity) throws KiteInteractionException {
    try {
      ((AndroidDriver) webDriver).startActivity(activity);
    } catch (Exception e) {
      processInteractionException("start app activity", e);
    }
  }
  
  /**
   * Performs a swipe up action from the beginning point to end point.
   *
   * @param begin     the begin
   * @param end       the end
   *
   * @throws KiteInteractionException the kite test exception
   */
  public void swipe(Point begin, Point end) throws KiteInteractionException {
    try {
      new TouchAction((MobileDriver) webDriver).press(PointOption.point(begin.x, begin.y))
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
        .moveTo(PointOption.point(end.x, end.y))
        .release().perform();
    } catch (Exception e) {
      processInteractionException("swipe", e);
    }
  }
  
  /**
   * Performs a swipe down action from the middle of the screen, taking half screen height as travel
   * distance by default.
   *
   * @throws KiteTestException the kite test exception
   */
  public void swipeDown() throws KiteTestException {
    Dimension size = getWindowSize(webDriver);
    swipe(new Point(size.width / 2, size.height / 2), new Point(size.width / 2, 10));
  }
  
  /**
   * Performs a swipe left action starting from the edge of the screen to other edge.
   *
   * @throws KiteTestException the kite test exception
   */
  public void swipeLeft() throws KiteTestException {
    Dimension size = getWindowSize(webDriver);
    swipe(new Point(15, size.height / 2), new Point(size.width - 15, size.height / 2));
  }
  
  /**
   * Performs a swipe right action starting from the edge of the screen to other edge.
   *
   * @param webDriver the web driver
   *
   * @throws KiteTestException the kite test exception
   */
  public void swipeRight(WebDriver webDriver) throws KiteTestException {
    Dimension size = getWindowSize(webDriver);
    swipe(new Point(size.width - 15, size.height / 2), new Point(15, size.height / 2));
  }
  
  /**
   * Performs a swipe up action from the middle of the screen, taking half screen height as travel
   * distance by default.
   *
   * @throws KiteTestException the kite test exception
   */
  public void swipeUp() throws KiteTestException {
    Dimension size = getWindowSize(webDriver);
    swipe(new Point(size.width / 2, size.height / 2), new Point(size.width / 2, size.height - 10));
  }
  
  /**
   * Performs a simple tap action at a specific point
   *
   * @param x         the x
   * @param y         the y
   *
   * @throws KiteTestException the kite test exception
   */
  public void tap(int x, int y) throws KiteTestException {
    try {
      new TouchAction((MobileDriver) webDriver)
        .tap(PointOption.point(x, y))
        .perform();
    } catch (Exception e) {
      processInteractionException("tap", e);
    }
  }
}
