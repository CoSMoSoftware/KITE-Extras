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

import org.apache.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    for (WebDriver webDriver : webDriverList)
      try {
        // Open about:config in case of fennec (Firefox for Android) and close.
        logger.info("closeDrivers: closing down " + webDriverList.size() + " webDrivers");
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
   * Is electron boolean.
   *
   * @param webDriver the web driver
   *
   * @return true if it is electron
   */
  public static boolean isElectron(WebDriver webDriver) {
    Capabilities capabilities = ((RemoteWebDriver) webDriver).getCapabilities();
    return "chrome".equals(capabilities.getBrowserName()) && capabilities.getVersion().isEmpty();
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
  
}
