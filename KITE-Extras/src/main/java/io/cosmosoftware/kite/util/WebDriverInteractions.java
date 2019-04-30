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
import org.apache.log4j.Logger;
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
import static io.cosmosoftware.kite.util.WebDriverUtils.*;

/**
 * Utility class serving WebDriver related operations.
 */
public class WebDriverInteractions {
  
  private static final Logger logger = Logger.getLogger(WebDriverInteractions.class.getName());
  
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
   * Performs a swipe up action from the bottom middle of the screen,
   * all the way down to the top middle of the screen.
   */
  public static void swipeUpFromBottom(WebDriver driver) throws KiteInteractionException {
    Dimension size = getWindowSize(driver);
    swipe(driver, new Point(size.getWidth() / 2, size.getHeight() - 1), new Point(size.getWidth() / 2, 1));
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
  
}
