/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cosmosoftware.kite.util;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import io.cosmosoftware.kite.exception.KiteInteractionException;
import io.cosmosoftware.kite.report.KiteLogger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.cosmosoftware.kite.entities.Timeouts.*;
import static io.cosmosoftware.kite.util.TestUtils.executeJsScript;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;

/**
 * Utility class serving WebDriver related operations.
 */
public class WebDriverUtils {
  
  private static final KiteLogger logger = KiteLogger.getLogger(WebDriverUtils.class.getName());
  
  /**
   * Finds and clicks an element with given description and a selector.
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given description of the element, for logging purpose
   * @param selector           given selector to locate element
   * @param action             true to use Actions to click the element instead of WebElement.click()
   *
   * @throws KiteInteractionException if an Exception occurs during method execution or fail.
   */
  public static void clickElement(WebDriver webDriver, String elementDescription, By selector, boolean action) throws KiteInteractionException {
    clickElement(webDriver, elementDescription, selector, 0, action);
  }
  
  /**
   * Finds and clicks an element with given description and a selector.
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given description of the element, for logging purpose
   * @param selector           given selector to locate element
   *
   * @throws KiteInteractionException if an Exception occurs during method execution or fail.
   */
  public static void clickElement(WebDriver webDriver, String elementDescription, By selector) throws KiteInteractionException {
    clickElement(webDriver, elementDescription, selector, -1, false);
  }
  
  /**
   * Finds and clicks an element with given description and a selector.
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given description of the element, for logging purpose
   * @param selector           given selector to locate element
   * @param index              index of the element with given selector
   * @param action             true to use Actions to click the element instead of WebElement.click()
   *
   * @throws KiteInteractionException if an Exception occurs during method execution or fail.
   */
  public static void clickElement(WebDriver webDriver, String elementDescription, By selector, int index, boolean action) throws KiteInteractionException {
    WebElement element = getElement(webDriver, elementDescription, selector, index);
    try {
      clickElement(webDriver, element, action);
      waitAround(ONE_SECOND_INTERVAL / 2);
    } catch (Exception e) {
      throw new KiteInteractionException(e.getClass().getName() + " in clicking element \"" + elementDescription + "\": " + e.getLocalizedMessage(), e);
    }
  }
  
  /**
   * Click element.
   *
   * @param webDriver the webdriver
   * @param element   a given WebElement
   * @param action    true to use Actions to click the element instead of WebElement.click()
   */
  public static void clickElement(WebDriver webDriver, WebElement element, boolean action) {
    if (action) {
      Actions actions = new Actions(webDriver);
      actions.moveToElement(element).click().perform();
    } else {
      element.click();
    }
  }
  
  /**
   * Close drivers.
   *
   * @param webDriverList the web driver list
   */
  public static void closeDrivers(List<WebDriver> webDriverList) {
    if (!webDriverList.isEmpty()) {
      logger.info("closeDrivers: closing down " + webDriverList.size() + " webDrivers");
      for (WebDriver webDriver : webDriverList) {
        try {
          // Open about:config in case of fennec (Firefox for Android) and close.
          if (((RemoteWebDriver) webDriver).getCapabilities().getBrowserName()
            .equalsIgnoreCase("fennec")) {
            webDriver.get("about:config");
            webDriver.close();
          }
          webDriver.quit();
        } catch (Exception e) {
          logger.error("Exception while closing/quitting the WebDriver", e);
        }
      }
      // prevent this get called more than once
      webDriverList.clear();
    }
  }
  
  /**
   * Performs a double tap action at a specific point
   *
   * @param webDriver the web driver
   * @param x         the x
   * @param y         the y
   */
  public static void doubleTap(WebDriver webDriver, int x, int y) {
    if (isMobileApp(webDriver)) {
      new TouchAction((MobileDriver) webDriver)
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).press(PointOption.point(x, y))
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).release()
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).press(PointOption.point(x, y))
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).release()
        .perform();
    } else {
      new Actions(webDriver).moveByOffset(x, y).doubleClick().perform();
    }
  }
  
  /**
   * Press down at the begin point and move to end point. Can be used to draw a line if the cursor
   * is in drawing/annotating mode.
   *
   * @param webDriver the web driver.
   * @param begin     point to begin.
   * @param end       end point
   */
  public static void drawLine(WebDriver webDriver, Point begin, Point end) {
    if (isMobileApp(webDriver)) {
      TouchAction action = new TouchAction((MobileDriver) webDriver);
      action.press(PointOption.point(begin.x, begin.y))
        .moveTo(PointOption.point(end.x, end.y))
        .release().perform();
    } else {
      Actions action = new Actions(webDriver);
      action.moveByOffset(begin.x, begin.y)
        .clickAndHold()
        .moveByOffset(end.x, end.y)
        .release().perform();
    }
  }
  
  /**
   * Press down at the begin point and move to fill the area of a square. Can be used to draw a
   * square if the cursor is in drawing/annotating mode.
   *
   * @param webDriver        the web driver.
   * @param begin            point to begin.
   * @param squareSideLength length of the side of the square.
   */
  public static void drawSquare(WebDriver webDriver, Point begin, int squareSideLength) {
    final int waitTime = 50;
    int startX = begin.x;
    int startY = begin.y;
    
    if (isMobileApp(webDriver)) {
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
    } else {
      Actions action = new Actions(webDriver);
      action.moveByOffset(startX, startY).clickAndHold();
      for (int measure = squareSideLength / 20; measure <= squareSideLength; measure += squareSideLength / 20) {
        int length = squareSideLength - measure;
        action.moveByOffset(length, 0)
          .moveByOffset(0, -length)
          .moveByOffset(-length, 0)
          .moveByOffset(0, length);
      }
      action.release().perform();
    }
  }
  
  /**
   * Find all elements with the corresponding locator and iterate through the list to find the one
   * that is required.
   *
   * @param webDriver the web driver
   * @param locator   locator to locate the elements
   * @param attribute type of the attribute we
   * @param value     the value
   *
   * @return web element
   */
  public static WebElement findElementWithCondition(
    WebDriver webDriver, By locator, String attribute, String value) {
    List<WebElement> elements = webDriver.findElements(locator);
    for (WebElement element : elements) {
      switch (attribute) {
        case "text": {
          if (element.getText().equalsIgnoreCase(value) ||
            element.getCssValue("value").equalsIgnoreCase(value)) {
            return element;
          }
          break;
        }
        default: {
          if (element.getAttribute(attribute).equalsIgnoreCase(value)) {
            return element;
          }
          break;
        }
      }
    }
    return null;
  }
  
  /**
   * Get the web element from web driver
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given description of the element, for logging purpose
   * @param selector           given selector to locate element
   *
   * @return the corresponding WebElement Object
   * @throws KiteInteractionException if an the element can not be found.
   */
  public static WebElement getElement(WebDriver webDriver, String elementDescription, By selector) throws KiteInteractionException {
    return getElement(webDriver, elementDescription, selector, -1);
  }
  
  /**
   * Get the web element from web driver with index
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given description of the element, for logging purpose
   * @param selector           given selector to locate element
   * @param index              index of element with given selector
   *
   * @return the corresponding WebElement Object
   * @throws KiteInteractionException if an the element can not be found.
   */
  public static WebElement getElement(WebDriver webDriver, String elementDescription, By selector, int index) throws KiteInteractionException {
    waitForElement(webDriver, selector, TEN_SECOND_INTERVAL);
    if (index == -1) {
      try {
        return webDriver.findElement(selector);
      } catch (Exception e) {
        throw new KiteInteractionException("Could not find element: \"" + elementDescription + "\"");
      }
    } else {
      WebElement element = webDriver.findElements(selector).get(index);
      if (element != null) {
        return element;
      } else {
        throw new KiteInteractionException("Could not find element: \"" + elementDescription + "\"");
      }
    }
  }
  
  /**
   * Switches to a new tab on a webdriver.
   *
   * @param webDriver the webdriver
   *
   * @throws KiteInteractionException the kite test exception
   */
  public static String getOtherWindowHandler(WebDriver webDriver) throws KiteInteractionException {
    String currentWindowHandler = webDriver.getWindowHandle();
    for (String windowHandler : webDriver.getWindowHandles()) {
      if (windowHandler != currentWindowHandler) {
        return windowHandler;
      }
    }
    return currentWindowHandler;
  }
  
  /**
   * Finds and get text from an element with given name and a given selector.
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given name of the element, for logging purpose
   * @param selector           given selector to locate element
   *
   * @return the text
   * @throws KiteInteractionException if an Exception occurs during method execution or fail.
   */
  public static String getText(WebDriver webDriver, String elementDescription, By selector) throws KiteInteractionException {
    WebElement element = getElement(webDriver, elementDescription, selector);
    try {
      String text = element.getText().trim();
      return text.isEmpty() ? null : text;
    } catch (Exception e) {
      throw new KiteInteractionException(e.getClass().getName() + " getting text from " + elementDescription);
    }
  }
  
  /**
   * Returns the dimension of the window if possible, if exception, returns a fixed one;
   *
   * @param webDriver the web driver
   *
   * @return window size
   */
  public static Dimension getWindowSize(WebDriver webDriver) {
    int fixedMeasure = 1024;
    try {
      return webDriver.manage().window().getSize();
    } catch (Exception e) {
      return new Dimension(fixedMeasure, fixedMeasure);
    }
  }
  
  /**
   * Looks for the number of elements with given selector
   *
   * @param webDriver      the webdriver.
   * @param selector       to locate the element
   * @param expectedNumber expected number of element visible on page.
   *
   * @return whether there are an exact number of element.
   */
  private static boolean hasExpectedNumberOfElements(WebDriver webDriver, By selector, int expectedNumber) {
    return webDriver.findElements(selector).size() == expectedNumber;
  }
  
  public static Boolean isAlive(WebDriver webDriver) {
    try {
      webDriver.getCurrentUrl();
      return true;
    } catch (Exception ex) {
      return false;
    }
  }
  
  /**
   * @return true if it is electron
   */
  public static boolean isElectron(WebDriver webDriver) {
    Capabilities capabilities = ((RemoteWebDriver) webDriver).getCapabilities();
    return "chrome".equals(capabilities.getBrowserName()) && capabilities.getVersion().isEmpty();
  }
  
  /**
   * Is ios boolean.
   *
   * @param webDriver the web driver
   *
   * @return whether the webdriver is on iOS
   */
  public static boolean isIOS(WebDriver webDriver) {
    if (isMobileApp(webDriver)) {
      Capabilities capabilities = ((RemoteWebDriver) webDriver).getCapabilities();
      String platform = capabilities.getPlatform().toString();
      return platform.toUpperCase().equalsIgnoreCase("MAC") || platform.toUpperCase().equalsIgnoreCase("IOS");
    }
    return false;
  }
  
  /**
   * Is mobile app boolean.
   *
   * @param webDriver the web driver
   *
   * @return whether the webdriver is in on a mobile app
   */
  public static boolean isMobileApp(WebDriver webDriver) {
    Capabilities capabilities = ((RemoteWebDriver) webDriver).getCapabilities();
    return capabilities.getBrowserName().isEmpty();
  }
  
  /**
   * Finds out if an element with given name and xpath value is visible on current page.
   *
   * @param webDriver the webdriver.
   * @param selector  given selector to locate element
   *
   * @return true if the element is visible
   */
  public static boolean isVisible(WebDriver webDriver, By selector) {
    try {
      WebElement element = webDriver.findElement(selector);
      return element != null && element.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }
  
  /**
   * Load the page, waiting for document.readyState to be complete
   *
   * @param url       the url of the web page
   * @param webDriver the webdriver.
   */
  public static void loadPage(WebDriver webDriver, String url, int timeout) {
    WebDriverWait driverWait = new WebDriverWait(webDriver, timeout);
    webDriver.get(url);
    driverWait.until(driver ->
      ((JavascriptExecutor) driver)
        .executeScript("return document.readyState")
        .equals("complete"));
  }
  
  /**
   * Resizes the windows to the screen's available width and height
   */
  public static void maximizeCurrentWindow(WebDriver webDriver) throws KiteInteractionException {
    if (!isElectron(webDriver)) {
      try {
        String getScreenHeight = "return screen.availHeight";
        String getScreenWidth = "return screen.availWidth";
        int screenHeight = (int) ((long) executeJsScript(webDriver, getScreenHeight));
        int screenWidth = (int) ((long) executeJsScript(webDriver, getScreenWidth));
        webDriver.manage().window().setSize(new Dimension(screenWidth, screenHeight));
      } catch (Exception e) {
        throw new KiteInteractionException("Could not maximize current window", e);
      }
    }
  }
  
  /**
   * Moves currently focused app to background for a duration
   *
   * @param webDriver the web driver
   * @param duration  duration to put in background
   */
  public static void moveAppToBackground(WebDriver webDriver, Duration duration) throws KiteInteractionException {
    try {
      if (webDriver instanceof AppiumDriver) {
        ((AppiumDriver) webDriver).runAppInBackground(duration);
      }
    } catch (Exception e) {
      throw new KiteInteractionException("Failed to move app to background: " + e.getLocalizedMessage());
    }
  }
  
  /**
   * Open Activity/App running in background
   *
   * @param webDriver the web driver
   * @param bundleId  id (package name) of the app
   */
  public static void openAppWithBundleId(WebDriver webDriver, String bundleId) throws KiteInteractionException {
    try {
      if (webDriver instanceof AppiumDriver) {
        ((AppiumDriver) webDriver).activateApp(bundleId);
      }
    } catch (Exception e) {
      throw new KiteInteractionException("Failed to open app with bundle ID: " + e.getLocalizedMessage());
    }
  }
  
  /**
   * Open a new tab (window handler should be on the new tab directly).
   *
   * @param webDriver the web driver
   */
  public static void openNewTab(WebDriver webDriver) {
    webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "t");
  }
  
  /**
   * Finds and sends a String to an element with given name and xpath value.
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given name of the element, for logging purpose
   * @param selector           given selector to locate element
   * @param index              the index of the element in the list of elements
   * @param message            String to send
   *
   * @throws KiteInteractionException if an Exception occurs during method execution or fail.
   */
  public static void sendKeysToElement(WebDriver webDriver, String elementDescription, By selector, int index, String message) throws KiteInteractionException {
    WebElement element = getElement(webDriver, elementDescription, selector, index);
    try {
      waitAround(ONE_SECOND_INTERVAL / 2);
      try {
        if (!element.getAttribute("value").isEmpty()) {
          element.clear();
        }
        if (!element.getText().isEmpty()) {
          element.clear();
        }
      } catch (Exception e) {
        // Ignore, just a check to see if the element has any existing value/ text.
      }
      element.sendKeys(message);
    } catch (Exception e) {
      throw new KiteInteractionException("Could not send key to " + elementDescription, e);
    }
  }
  
  /**
   * Finds and sends a String to an element with given name and xpath value.
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given name of the element, for logging purpose
   * @param selector           given selector to locate element
   * @param message            String to send
   *
   * @throws KiteInteractionException if an Exception occurs during method execution or fail.
   */
  public static void sendKeysToElement(WebDriver webDriver, String elementDescription, By selector, String message) throws KiteInteractionException {
    sendKeysToElement(webDriver, elementDescription, selector, -1, message);
  }
  
  /**
   * Sets implicit wait for the webdriver in milliseconds.
   *
   * @param webDriver          the web driver
   * @param waitInMilliSeconds the wait in milli seconds
   */
  public static void setImplicitWait(WebDriver webDriver, int waitInMilliSeconds) {
    webDriver.manage().timeouts().implicitlyWait(waitInMilliSeconds, TimeUnit.MILLISECONDS);
  }
  
  /**
   * Opens a new activity (changes to new activity)
   *
   * @param webDriver the web driver
   * @param activity  activity to open.
   */
  public static void startAppActivity(AndroidDriver webDriver, Activity activity) {
    webDriver.startActivity(activity);
  }
  
  /**
   * Performs a swipe up action from the beginning point to end point.
   *
   * @param webDriver the web driver
   * @param begin     the begin
   * @param end       the end
   *
   * @throws KiteInteractionException the kite test exception
   */
  public static void swipe(WebDriver webDriver, Point begin, Point end) throws KiteInteractionException {
    try {
      new TouchAction((MobileDriver) webDriver).press(PointOption.point(begin.x, begin.y))
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
        .moveTo(PointOption.point(end.x, end.y))
        .release().perform();
    } catch (Exception e) {
      throw new KiteInteractionException(e.getClass().getName() + " while swiping on device", e);
    }
    
  }
  
  /**
   * Performs a swipe down action from the middle of the screen, taking half screen height as travel
   * distance by default.
   *
   * @param webDriver the web driver
   *
   * @throws KiteInteractionException the kite test exception
   */
  public static void swipeDown(WebDriver webDriver) throws KiteInteractionException {
    Dimension size = getWindowSize(webDriver);
    swipe(webDriver, new Point(size.width / 2, size.height / 2), new Point(size.width / 2, 10));
  }
  
  /**
   * Performs a swipe left action starting from the edge of the screen to other edge.
   *
   * @param webDriver the web driver
   *
   * @throws KiteInteractionException the kite test exception
   */
  public static void swipeLeft(WebDriver webDriver) throws KiteInteractionException {
    Dimension size = getWindowSize(webDriver);
    swipe(webDriver, new Point(15, size.height / 2), new Point(size.width - 15, size.height / 2));
  }
  
  /**
   * Performs a swipe right action starting from the edge of the screen to other edge.
   *
   * @param webDriver the web driver
   *
   * @throws KiteInteractionException the kite test exception
   */
  public static void swipeRight(WebDriver webDriver) throws KiteInteractionException {
    Dimension size = getWindowSize(webDriver);
    swipe(webDriver, new Point(size.width - 15, size.height / 2), new Point(15, size.height / 2));
  }
  
  /**
   * Performs a swipe up action from the middle of the screen, taking half screen height as travel
   * distance by default.
   *
   * @param webDriver the web driver
   *
   * @throws KiteInteractionException the kite test exception
   */
  public static void swipeUp(WebDriver webDriver) throws KiteInteractionException {
    Dimension size = getWindowSize(webDriver);
    swipe(webDriver, new Point(size.width / 2, size.height / 2), new Point(size.width / 2, size.height - 10));
  }
  
  /**
   * Performs a swipe up action from the bottom middle of the screen,
   * all the way down to the top middle of the screen.
   */
  public static void swipeUpFromBottom(WebDriver driver) throws KiteInteractionException {
    Dimension size = getWindowSize(driver);
    swipe(driver, new Point(size.getWidth() / 2, size.getHeight() - 1), new Point(size.getWidth() / 2, 1));
  }
  
  /**
   * Switches to a new tab on a webdriver.
   *
   * @param webDriver the webdriver
   *
   * @throws KiteInteractionException the kite test exception
   */
  public static void switchWindowHandler(WebDriver webDriver) throws KiteInteractionException {
    String currentWindowHandler = webDriver.getWindowHandle();
    boolean changed = false;
    String windowHandler = getOtherWindowHandler(webDriver);
    if (windowHandler != currentWindowHandler) {
      webDriver.switchTo().window(windowHandler);
      changed = true;
    }
    if (!changed) {
      throw new KiteInteractionException(
        "Could not switch tab or window. Current window handler not found.");
    }
  }
  
  /**
   * Performs a simple tap action in the middle of the screen.
   *
   * @param webDriver the web driver
   */
  public static void tap(WebDriver webDriver) throws KiteInteractionException {
    Dimension size = webDriver.manage().window().getSize();
    tap(webDriver, size.width / 2, size.height / 2);
  }
  
  /**
   * Performs a simple tap action at a specific point
   *
   * @param webDriver the web driver
   * @param x         the x
   * @param y         the y
   */
  public static void tap(WebDriver webDriver, int x, int y, int times) throws KiteInteractionException {
    try {
      TouchAction action = new TouchAction((MobileDriver) webDriver);
      for (int count = 0; count < times; count++) {
        action.tap(PointOption.point(x, y))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(150)));
      }
      action.perform();
    } catch (Exception e) {
      throw new KiteInteractionException(e.getClass().getName() + " while tapping on device", e.getCause());
    }
  }
  
  /**
   * Performs a simple tap action at a specific point
   *
   * @param webDriver the web driver
   * @param x         the x
   * @param y         the y
   */
  public static void tap(WebDriver webDriver, int x, int y) throws KiteInteractionException {
    try {
      if (isMobileApp(webDriver)) {
        new TouchAction((MobileDriver) webDriver).tap(PointOption.point(x, y)).perform();
      } else {
        new Actions(webDriver).moveByOffset(x, y).click().perform();
      }
    } catch (Exception e) {
      throw new KiteInteractionException(e.getClass().getName() + " while tapping on device", e.getCause());
    }
  }
  
  /**
   * Waits for an element to be visible.
   *
   * @param webDriver the webdriver.
   * @param selector  to locate the element
   *
   * @throws KiteInteractionException if an Exception occurs during method execution or fail.
   */
  public static void waitForElement(WebDriver webDriver, By selector) throws KiteInteractionException {
    waitForElement(webDriver, selector, SHORT_TIMEOUT);
  }
  
  /**
   * Waits for an element to be visible
   *
   * @param webDriver the webdriver.
   * @param selector  to locate the element
   * @param timeout   defaultWait duration.
   *
   * @throws KiteInteractionException if an Exception occurs during method execution or fail.
   */
  public static void waitForElement(WebDriver webDriver, By selector, int timeout) throws KiteInteractionException {
    setImplicitWait(webDriver, ONE_SECOND_INTERVAL);
    for (int waitTime = 0; waitTime < timeout; waitTime += ONE_SECOND_INTERVAL) {
      try {
        //driverWait.until(ExpectedConditions.visibilityOfElementLocated(selector));
        if (webDriver.findElement(selector) != null) {
          setImplicitWait(webDriver, SHORT_TIMEOUT);
          return;
        }
      } catch (Exception e) {
        // in case the wait throw an abnormal exception, meaning the defaultWait does not defaultWait as it should.
        // logger.debug("Exception in waitForElement " + getStackTrace(e));
        // ignore
        waitAround(ONE_SECOND_INTERVAL);
      }
    }
    throw new KiteInteractionException("Timeout waiting for element: " + selector.toString());
  }
  
  /**
   * Wait for existences of a number of certain elements on page
   *
   * @param webDriver      the webdriver.
   * @param selector       to locate the element
   * @param expectedNumber expected number of element visible on page
   *
   * @throws KiteInteractionException if an Exception occurs during method execution or fail.
   */
  public static void waitForExpectedNumberOfElements(WebDriver webDriver, By selector, int expectedNumber) throws KiteInteractionException {
    for (int waitTime = 0; waitTime < DEFAULT_TIMEOUT; waitTime += ONE_SECOND_INTERVAL) {
      try {
        waitAround(ONE_SECOND_INTERVAL / 2);
        if (hasExpectedNumberOfElements(webDriver, selector, expectedNumber)) {
          waitAround(ONE_SECOND_INTERVAL);
          return;
        }
      } catch (Exception e) {
        // in case the defaultWait throw an abnormal exception, meaning the defaultWait does not defaultWait as it should.
      }
      waitAround(ONE_SECOND_INTERVAL);
    }
    throw new KiteInteractionException("Timeout waiting for " + expectedNumber + " elements: " + selector.toString());
  }
  
}
