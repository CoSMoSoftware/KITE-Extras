/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.util;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.usrmgmt.TypeRole;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static io.cosmosoftware.kite.util.TestUtils.executeCommand;
import static io.cosmosoftware.kite.util.WebDriverUtils.isElectron;

/**
 * The type Report utils.
 */
public class ReportUtils {

  private static final KiteLogger logger = KiteLogger.getLogger(ReportUtils.class.getName());

  /**
   * Gets the browser name from Capabilities
   *
   * @return the browser or device name (e.g. Chrome, Android...)
   */
  private static String getBrowserName(Capabilities capabilities) {
    return (capabilities.getBrowserName().isEmpty() ?
        ((capabilities.getCapability("platformName").toString().equalsIgnoreCase("ios"))
            || (capabilities.getCapability("platformName").toString().equalsIgnoreCase("mac"))) ?
            (capabilities.getCapability("deviceName").toString().isEmpty() ?
                "DEVICE_X" : capabilities.getCapability("deviceName").toString())
            : (capabilities.getCapability("deviceModel").toString().isEmpty() ?
                "DEVICE_X" : capabilities.getCapability("deviceModel").toString())
        : getBrowserShortName(capabilities.getBrowserName()));
  }


  /**
   * Gets the browser name corresponding to the webdriver
   *
   * @return the browser name corresponding to the webdriver
   */
  public static String getBrowserName(WebDriver webDriver) {
    return getBrowserName(((RemoteWebDriver) webDriver).getCapabilities());
  }

  /**
   * Gets the browser short name from Capabilities
   *
   * @return the first two letters or browser or device name (e.g. ch for Chrome, an for Android...)
   */
  private static String getBrowserShortName(String browserName) {
    return browserName.substring(0, browserName.length() > 1 ? 2 : browserName.length());
  }

  /**
   * Gets the browser version from Capabilities
   *
   * @return the version of the webDriver, in short form (69.0.3497.100 => 69)
   */
  private static String getBrowserVersion(Capabilities capabilities) {
    try {
      String version = (capabilities.getVersion().isEmpty() ?
          ("chrome".equals(capabilities.getBrowserName()) ?
              "electron" : capabilities.getCapability("platformVersion").toString())
          : capabilities.getVersion());

      if (version.contains(".")) {
        version = version.substring(0, version.indexOf("."));
      }
      return version;
    } catch (Exception e) {
      // For some mobile sessions, calling getVersion() results in npe
      return "0";
    }
  }

  /**
   * Get log header from webdriver's capabilities.
   *
   * @param webDriver the web driver
   * @param index the index
   * @return the logHeader
   */
  public static String getLogHeader(WebDriver webDriver, int index) {
    return getLogHeader(webDriver, index, null);
  }

  /**
   * Get log header from webdriver's capabilities and TypeRole
   *
   * @param webDriver the web driver
   * @param typeRole the type role
   * @return the logHeader
   */
  public static String getLogHeader(WebDriver webDriver, TypeRole typeRole) {
    return getLogHeader(webDriver, 0, typeRole);
  }

  /**
   * Get log header from webdriver's capabilities and TypeRole
   *
   * @param webDrivers the list of web driver
   * @return the logHeader
   */
  public static String getLogHeader(List<WebDriver> webDrivers) {
    String res = "";
    for (WebDriver webDriver : webDrivers) {
      res += getLogHeader(webDriver, 0, null);
    }
    return res;
  }

  /**
   * Get log header from webdriver's capabilities and TypeRole
   *
   * @param webDriver the web driver
   * @return the logHeader
   */
  public static String getLogHeader(WebDriver webDriver) {

    return getLogHeader(webDriver, -1, null);
  }

  /**
   * Get log header from webdriver's capabilities and TypeRole
   *
   * @return the logHeader
   */
  private static String getLogHeader(WebDriver webDriver, int index, TypeRole typeRole) {
    String str = "";
    if (webDriver != null) {
      Capabilities capabilities = ((RemoteWebDriver) webDriver).getCapabilities();
      str= (isElectron(webDriver) ? "elec_" :
          getBrowserName(capabilities) + getBrowserVersion(capabilities) + "_");
      str += getPlatform(capabilities) + "-";
      if (typeRole != null) {
        str += typeRole.getShortName();
      } else {
        str += "" + (index == -1 ? ((RemoteWebDriver) webDriver).getSessionId().toString()
            .substring(0, 5) : index);
      }
    } else {
      str = "unknown(no Web Driver)";
    }
    return str;
  }

  /**
   * Gets the browser version from Capabilities
   *
   * @return the version of the webDriver, in short form (69.0.3497.100 => 69)
   */
  private static String getPlatform(Capabilities capabilities) {
    //todo: better platform detection
    String platform = (capabilities.getPlatform().toString().isEmpty()
        ? "PLATFORM_X"
        : capabilities.getPlatform().toString());
    if ("XP".equals(platform) || "windows".equals(platform.toLowerCase())) {
      platform = "WIN";
    }
    return platform;
  }

  /**
   * Returns stack trace of the given exception.
   *
   * @param e Exception
   * @return string representation of e.printStackTrace()
   */
  public static String getStackTrace(Throwable e) {
    Writer writer = new StringWriter();
    e.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }

  public static byte[] saveScreenshotPNG(WebDriver driver, Rectangle cropZone) throws KiteTestException {
    try {
      if (cropZone != null) {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage img = ImageIO.read(screenshot);
        BufferedImage croppedImg =
            img.getSubimage(cropZone.getX(), cropZone.getY(), cropZone.getWidth(), cropZone.getHeight());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( croppedImg, "png", baos );
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
      } else {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
      }
    } catch (Exception e) {
      throw new KiteTestException("Failed to take screenshot: " + e.getLocalizedMessage(),
          Status.BROKEN, e, true);
    }
  }

  public static byte[] saveScreenshotPNG(WebDriver driver) throws KiteTestException {
    return saveScreenshotPNG(driver, null);
  }
  /**
   * Timestamp string.
   *
   * @return the current date/time as a String
   */
  public static String timestamp() {
    //must be file name safe (no /\?%*:|"<>)
    return new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
  }

  /**
   * Timestamp string.
   *
   * @param date value of date in milliseconds
   * @return the current date/time as a String
   */
  public static String timestamp(long date) {
    //must be file name safe (no /\?%*:|"<>)
    return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(date));
  }


  /**
   * Gets the browser console logs. Works only on Chrome.
   *
   * @return the browser console log as a String
   */
  public static String consoleLogs(WebDriver webDriver) {
    String log = "";
    List<LogEntry> logEntries;
    try {
      logEntries = webDriver.manage().logs().get(LogType.BROWSER).getAll();
    } catch (Exception e) {
      logEntries = new ArrayList<>();
    }
    if (logEntries.isEmpty()) {
      log += "The log was empty, either an error has happened, or this browser is not supported";
    } else {
      for (LogEntry entry : logEntries) {
        log += entry.getTimestamp() + ":" +  entry.getLevel() + " " + entry.getMessage().replaceAll("'", "") + "\r\n";
      }
    }
    return log;
  }

  /**
   * Zips file to .zip file with default file name
   * @param sourceFile  path to source file
   * @throws KiteTestException  if there's IOException
   */
  public static String zipFile(String sourceFile) {
    return zipFile(sourceFile, null, false);
  }

  /**
   * Zips a folder to a .zip file
   * @param sourceFile  path to source file
   * @param desFile .zip file name
   * @throws KiteTestException  if there's IOException
   */
  public static String zipFile(String sourceFile, String desFile) {
    return zipFile(sourceFile, desFile, false);
  }

  /**
   * Zips a folder to a .zip file
   * @param sourceFile  path to source file
   * @param desFile .zip file name
   * @param size add file size to the name
   * @throws KiteTestException  if there's IOException
   */
  public static String zipFile(String sourceFile, String desFile, boolean size){
    FileOutputStream fos= null;
    ZipOutputStream zipOut= null;
    desFile = desFile == null ? sourceFile : desFile;
    try {
      File fileToZip = new File(sourceFile);
      if(size) {
        desFile += "-size." +  FileUtils.sizeOfDirectory(fileToZip) + ".zip";
      } else {
        desFile = desFile.endsWith(".zip") ? desFile : desFile + ".zip";
      }

      fos = new FileOutputStream(desFile);
      zipOut = new ZipOutputStream(fos);
      zipFile(fileToZip, fileToZip.getName(), zipOut);
    } catch (IOException e) {
      logger.error("Could not zip file/folder: " + sourceFile + " \n" + e.getMessage());
    } finally {
      try {
        if (zipOut != null) {
          zipOut.close();
        } if (fos != null) {
          fos.close();
        }
      } catch (IOException e) {
        logger.error("Could not close stream after zipping file");
      }
    }
    return desFile;
  }

  private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
    if (fileToZip.isHidden()) {
      return;
    }
    if (fileToZip.isDirectory()) {
      if (fileName.endsWith("/")) {
        zipOut.putNextEntry(new ZipEntry(fileName));
        zipOut.closeEntry();
      } else {
        zipOut.putNextEntry(new ZipEntry(fileName + "/"));
        zipOut.closeEntry();
      }
      File[] children = fileToZip.listFiles();
      for (File childFile : children) {
        zipFile(childFile, childFile.getName(), zipOut);
      }
      return;
    }
    FileInputStream fis = new FileInputStream(fileToZip);
    ZipEntry zipEntry = new ZipEntry(fileName);
    zipOut.putNextEntry(zipEntry);
    byte[] bytes = new byte[1024];
    int length;
    while ((length = fis.read(bytes)) >= 0) {
      zipOut.write(bytes, 0, length);
    }
    fis.close();
  }

}
