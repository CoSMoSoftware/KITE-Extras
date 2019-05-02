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
import io.cosmosoftware.kite.exception.KiteInteractionException;
import io.cosmosoftware.kite.report.Status;
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

/**
 * Utility class serving WebDriver related operations.
 */
public class WebDriverUtils {
  
  private static final Logger logger = Logger.getLogger(WebDriverUtils.class.getName());
  /**
   * Close drivers.
   *
   * @param webDriverList the web driver list
   */
  public static void closeDrivers(List<WebDriver> webDriverList) {
    logger.info("closeDrivers: closing down " + webDriverList.size() + " webDrivers");
    for (WebDriver webDriver : webDriverList)
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
  
  /**
   * Load the page, waiting for document.readyState to be complete
   * @param url the url of the web page
   * @param webDriver      the webdriver.
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
   * Sets implicit wait for the webdriver in milliseconds.
   *
   * @param webDriver          the web driver
   * @param waitInMilliSeconds the wait in milli seconds
   */
  public static void setImplicitWait(WebDriver webDriver, int waitInMilliSeconds) {
    webDriver.manage().timeouts().implicitlyWait(waitInMilliSeconds, TimeUnit.MILLISECONDS);
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
   * @return true if it is electron
   */
  public static boolean isElectron(WebDriver webDriver) {
    Capabilities capabilities = ((RemoteWebDriver) webDriver).getCapabilities();
    return "chrome".equals(capabilities.getBrowserName()) && capabilities.getVersion().isEmpty();
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
   * Opens a new activity (changes to new activity)
   *
   * @param webDriver the web driver
   * @param activity  activity to open.
   */
  public static void startAppActivity(AndroidDriver webDriver, Activity activity) {
    webDriver.startActivity(activity);
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
}
