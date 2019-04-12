package io.cosmosoftware.kite.pages;

import io.cosmosoftware.kite.exception.KiteInteractionException;
import org.apache.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import static io.cosmosoftware.kite.util.TestUtils.executeJsScript;
import static io.cosmosoftware.kite.util.WebDriverUtils.isElectron;

public abstract class BasePage {
  protected final Logger logger = Logger.getLogger(this.getClass().getName());
  protected final WebDriver webDriver;
  
  
  protected BasePage(WebDriver webDriver) {
    this.webDriver = webDriver;
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
    this.click(element, false);
  }
  
  /**
   * Click element.
   *
   * @param element   a given WebElement
   * @param useAction option whether to use the Actions class from org.openqa.selenium.interactions
   *
   */
  public void click(WebElement element, boolean useAction) throws KiteInteractionException {
    try {
      if (useAction) {
        Actions actions = new Actions(this.webDriver);
        actions.moveToElement(element).click().perform();
      } else {
        element.click();
      }
    } catch (Exception e) {
      processInteractionException( "click", e);
    }
  }
  
  /**
   * Performs a double tap action at a specific point
   *
   * @param x         the x
   * @param y         the y
   */
  public void doubleTap(int x, int y) {
    new Actions(webDriver).moveByOffset(x, y).doubleClick().perform();
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
   * Press down at the begin point and move to end point. Can be used to draw a line if the cursor
   * is in drawing/annotating mode.
   *
   * @param begin     point to begin.
   * @param end       end point
   */
  public void drawLine(Point begin, Point end) throws KiteInteractionException {
    try {
      Actions action = new Actions(webDriver);
      action.moveByOffset(begin.x, begin.y)
        .clickAndHold()
        .moveByOffset(end.x, end.y)
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
  public void drawSquare(Point begin, int squareSideLength) throws KiteInteractionException {
    try {
      final int waitTime = 50;
      int startX = begin.x;
      int startY = begin.y;
      
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
    } catch (Exception e) {
      processInteractionException("draw square",e);
    }
  }
  
  
  /**
   * Resizes the windows to the screen's available width and height
   */
  public  void maximizeCurrentWindow() throws KiteInteractionException {
    try {
      if (!isElectron(webDriver)) {
        String getScreenHeight = "return screen.availHeight";
        String getScreenWidth = "return screen.availWidth";
        int screenHeight = (int) ((long) executeJsScript(webDriver, getScreenHeight));
        int screenWidth = (int) ((long) executeJsScript(webDriver, getScreenWidth));
        webDriver.manage().window().setSize(new Dimension(screenWidth, screenHeight));
      }
    } catch (Exception e) {
      processInteractionException("maximize current window", e);
    }
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
}
