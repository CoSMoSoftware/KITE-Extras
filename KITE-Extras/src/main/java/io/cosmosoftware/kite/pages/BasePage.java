package io.cosmosoftware.kite.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import io.cosmosoftware.kite.exception.KiteInteractionException;
import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.util.WebDriverInteractions;
import io.cosmosoftware.kite.util.WebDriverUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static io.cosmosoftware.kite.entities.Timeouts.*;
import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static io.cosmosoftware.kite.util.TestUtils.executeJsScript;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;
import static io.cosmosoftware.kite.util.WebDriverInteractions.clickElement;
import static io.cosmosoftware.kite.util.WebDriverUtils.isElectron;

public abstract class BasePage {
  protected final Logger logger;
  protected final WebDriver webDriver;
  protected final WebDriverWait defaultWait;
  protected boolean isAppium;
  
  
  protected BasePage(WebDriver webDriver, Logger logger) {
    this.webDriver = webDriver;
    this.isAppium = webDriver instanceof AppiumDriver;
    this.logger = logger;
    this.defaultWait = new WebDriverWait(this.webDriver, EXTENDED_TIMEOUT_IN_SECONDS);
    PageFactory.initElements(webDriver, this);
  }
  
  
  // Element interactions :
  
  /**
   * Click element.
   *
   * @param element   a given WebElement
   *
   */
  public void click(WebElement element) throws KiteInteractionException {
    click(element, false);
  }
  
  /**
   * Click element.
   *
   * @param element   a given WebElement
   * @param useAction option whether to use the Actions class from org.openqa.selenium.interactions
   *
   */
  public void click(WebElement element, boolean useAction) throws KiteInteractionException {
    if (isAppium) {
      clickElement(webDriver, element,false);
    } else {
      clickElement(webDriver, element,useAction);
    }
  }
  
  /**
   * Performs a simple tap action at a specific point
   *
   * @param x         the x
   * @param y         the y
   */
  public void tap(int x, int y) throws KiteTestException {
    WebDriverInteractions.tap(webDriver, x, y);
  }
  
  /**
   * Performs a double tap action at a specific point
   *
   * @param x         the x
   * @param y         the y
   */
  public void doubleTap(int x, int y) {
    WebDriverInteractions.doubleTap(webDriver, x, y);
  }
  
  
  public void sendKeys(WebElement element, String input) throws KiteInteractionException {
    clearElementText(element);
    try {
      element.sendKeys(input);
    } catch (Exception e) {
      processInteractionException( "sendKeys", e);
    }
  }
  
 
  /**
   * Resizes the windows to the screen's available width and height
   */
  public  void maximizeCurrentWindow() throws KiteInteractionException {
    WebDriverUtils.maximizeCurrentWindow(webDriver);
  }
  
  private void clearElementText(WebElement element) {
    try {
      if (!element.getAttribute("value").isEmpty()) {
        element.clear();
      }
    } catch (Exception e) {
      // Ignore, just a check to see if the element has any existing value/ text.
    }
    try {
      if (!element.getText().isEmpty()) {
        element.clear();
      }
    } catch (Exception e) {
      // Ignore, just a check to see if the element has any existing value/ text.
    }
  }
  
  protected void processInteractionException(String interactionName, Exception e) throws KiteInteractionException {
    throw new KiteInteractionException(e.getClass().getName() + " while performing " + interactionName, e.getCause());
  }
  
  /**
   * Performs a swipe up action from the beginning point to end point.
   *
   * @param begin     the begin
   * @param end       the end
   *
   * @throws KiteTestException the kite test exception
   */
  public void swipe(Point begin, Point end) throws KiteTestException {
    WebDriverInteractions.swipe(webDriver,begin,end);
  }
  
  /**
   * Wait until the webElement elem is visible up to timeoutInSecond.
   *
   * @param elem             the WebElement
   * @param timeoutInSeconds the timeout duration in seconds
   * @throws KiteInteractionException when timeoutInSecond is reached
   */
  public void waitUntilVisibilityOf(WebElement elem, int timeoutInSeconds) throws KiteInteractionException {
    try {
      new WebDriverWait(this.webDriver, timeoutInSeconds).until(ExpectedConditions.visibilityOf(elem));
    } catch (Exception e) {
      throw new KiteInteractionException("The web element " + elem + " is not visible after " + timeoutInSeconds + "s", e);
    }
  }

  /**
   * Wait until the webElement found by the locator is visible up to timeoutInSecond.
   *
   * @param locator          to locate the element
   * @param timeoutInSeconds the timeout duration in seconds
   * @throws KiteInteractionException if an Exception occurs during method execution or fail.
   */
  public void waitUntilVisibilityOf(By locator, int timeoutInSeconds) throws KiteInteractionException {
    try {
      new WebDriverWait(this.webDriver, timeoutInSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
    } catch (Exception e) {
      throw new KiteInteractionException("The web element " + locator.toString() + " is not visible after " + timeoutInSeconds + "s", e);
    }
  }

}
