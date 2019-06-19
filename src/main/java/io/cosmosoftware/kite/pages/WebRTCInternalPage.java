package io.cosmosoftware.kite.pages;

import io.cosmosoftware.kite.exception.KiteInteractionException;
import io.cosmosoftware.kite.interfaces.Runner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;

import static io.cosmosoftware.kite.util.TestUtils.waitAround;
import static io.cosmosoftware.kite.util.WebDriverUtils.isChrome;

public class WebRTCInternalPage extends BasePage {

  @FindBy(className="peer-connection-dump-root")
  WebElement summary;
  
  @FindBy(tagName="button")
  WebElement downloadButton;

  
  
  public WebRTCInternalPage(Runner params) {
    super(params);
  }


  /**
   * Opens chrome://webrtc-internals in a new tab and get the dump
   * @return the webrtc-internals dump
   * @throws KiteInteractionException
   */
  public String downloadDump() throws KiteInteractionException {
    if (!isChrome(webDriver)) {
      return "webrtc-internals is only supported on Chrome";
    }
    ((JavascriptExecutor) webDriver).executeScript("window.open()");
    ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
    webDriver.switchTo().window(tabs.get(1));
    webDriver.get("chrome://webrtc-internals/");
    waitAround(1500);
    waitUntilVisibilityOf(summary, 6);
    click(summary, true);
    waitUntilVisibilityOf(downloadButton, 6);
    click(downloadButton);
    String script = "var dump_object = {" +
      "  'getUserMedia': userMediaRequests, " +
      "  'PeerConnections': peerConnectionDataStore, " +
      "  'UserAgent': navigator.userAgent " +
      "}; " +
      "return JSON.stringify(dump_object, null, ' '); ";
    String internalDump = (String)((JavascriptExecutor) webDriver).executeScript(script);
    webDriver.switchTo().window(tabs.get(0));
    return internalDump;
    
  }
    
  
}
