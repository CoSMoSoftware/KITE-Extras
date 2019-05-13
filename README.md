This includes the utility modules for [KITE](https://github.com/webrtc/KITE/tree/kite-2.0). 

These modules contain the functions and tools that facilitate writing new tests, testing with KITE, and analyzing reports from KITE tests.

These modules might or might not be neccessary to be used as dependency for KITE test.

There are 2 modules in this repository, **KITE-Extras** and **KITE-Grid-Utils**. 

# 1. KITE-Extras

This module contains multiple tools for KTIE testing. Part of this module is used in the KITE Engine itself, namely the KITE-Framework module in KITE. Therefore, this is a mandatory dependency for KITE. 

Any changes made to this module need to be included in a new release, so that KITE-Framework  will still function with an older release. Changing version for KITE-Extras dependency in KITE-Framework should be done only after thorough testing.

This module includes the following packages:

## action
This package includes several utility JavaScript codes that can be executed by Selenium Webdriver. These codes can only be executed on a browser Webdriver, not compatible with Appium or any other webdrivers that control native apps on mobile or desktop Os.

## entities
This package includes classes that contain enums or static values that can be reused across modules of KITE and/or KITE tests. Using these variables is recommended to keep every modules in synchronization. New values and/or classes can be added to this package, but existing values must not be changed.

## exception
This package includes custom KITE Exception classes, designed to report back the error message as well as the Status of these errors. These classes were inspired by the Allure report's status: FAILED, PASSED, BROKEN, SKIPPED. Theses exceptions should be used in KITE tests with the report classes, in order to use KITE's Allure inspired report.

These exceptions and any new exceptions in the future should be well separated bases on their purposes. 

For example, the *KiteInteractionException* should be used to rethrow any Exception coming from the interaction between the webdriver and the browser/app, such as click, sendKeys, ... The status for this will be BROKEN as these errors will actually break the test, but don't reflect test failure. 

In the contrary, *KiteTestException* should be used in the check/verification steps in the tests. Any unexpected/undesired result coming from the test should throw this exception to show that the test has failed at that particular step.

## imgprocessing
This package includes the utility functions that can be used to process image data, comparing images/screenshots and other manipulation.

## instrumentation
This package includes the utility objects that can be used to apply network instrumentation. These object can sometimes be test specipic and should be used with caution and understanding of the code.

## manager
This package includes classes that act as 'manager' of some sort, such as ssh connection or conference room. New classes will be added in the future to facilitate the management of events happening during tests.

## pages
This package includes the base page object, that can be uses in KITE tests. These page objects serve the purpose of implementing the Page Object Module (POM) in Selenium test. 

The POM dictates that all of the webdriver API should be put in the pages. For better logging and error analyzing, these actions (webdriver API) should be well controlled in the page classes, any exception should be caught and rethrown with messages that will help developer to understand where things go wrong. Something along the line of this:

```
@ FindBy(id = "something")
WebElement something; 

try{
  something.click();
catch (Exception e) {
  throw new KiteInteractionException("Error while clicking something, due to: " + e.getMessage());
}
```

## pool
This package includes the classes that allow using different types of pool in Java. These classes are used in KITE's user management for example.

## report
This package includes the classes that allow Allure inspired reporting in KITE tests. These report can be used in individual steps or in tests. The report is generated at the end of the tests with correct timestamp and duration for each step. These report classes also support attachments like messages, json objects or screenshots.

These classes were designed with the inspiration from Allure reporting style. The report files generated by these classes can be served by `allure serve` command, and be used for allure reporting directly.

## stats
This package includes the Java objects for WebRTC stat objects. These classes were designed with the W3C madatory stats and stat attributes for WebRTC. The raw stats obtained with peerconnection `getStats()` will be processed into these objects and can be used in Java KITE tests.

## steps
This package includes the base step classes that can be used in KITE tests. Organizing the tests into small and individual steps/checks is a good way to maintain the tests and inprove tests' readability. 

These steps were designed to be used with KITE's report classes. Each step should have a different desciption for better reporting and logging.

These steps also serve in implementing the POM in KITE tests. Each of these step will contain the reference to the appropriate page object, and will call the page API accordingly. Example:

```
public class GoogleFirstResultCheck extends TestCheck {
  final String EXPECTED_RESULT = "CoSMo Software | WebRTC Technology & Implementation";
  
  public GoogleFirstResultCheck(WebDriver webDriver) {
    super(webDriver);
  }
  
  @Override
  public String stepDescription() {
    return "Open first result on Google result page and verify the page title";
  }
  
  @Override
  protected void step() throws KiteTestException {
    final GoogleResultPage resultPage = new GoogleResultPage(this.webDriver, logger);
    resultPage.openFirstResult();
    String found = resultPage.getTitle().trim();
    if (!found.equalsIgnoreCase(EXPECTED_RESULT)){
      throw new KiteTestException("The title of the first Google result was not correct: \n" +
        "Expected: " + EXPECTED_RESULT + " but found " + found, Status.FAILED);
    }
    Reporter.getInstance().screenshotAttachment(report, saveScreenshotPNG(webDriver));
  }
}
```
## usrmgmt
This package includes the classes for KITE's account management. The Account class here can be used as a base class for any type of account that might be uses in KITE tests that require log-in with an account (Ex: RingCentral, !Spank,...). 

This account manager works with a file containing the accounts. The accounts will be distributed to test runners and retrieved at the end of the tests. Having this account manager assures that no account is used in different runners at the same time.

## util
This package includes `public static` utility functions which are used widely across KITE Engine and KITE tests. These functions can be either utility Java functions that can be reused between test such as logging and timestamps, data converting,.. or Selenium/Appium webdriver functions that facilitate the complex combinations of WebDriver API usages, such as resizing/switching windows, drawing shapes/lines,...

# KITE-Grid-Utils
This module contains the classes that help with the setup of Selenium grid. These classes can be custom servlet for nodes for specific test cases or enhance logging for test-grid interactions.
