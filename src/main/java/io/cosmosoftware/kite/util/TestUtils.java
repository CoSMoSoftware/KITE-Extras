/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.util;

import io.cosmosoftware.kite.action.JSActionScript;
import io.cosmosoftware.kite.entities.Timeouts;
import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.AllureStepReport;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.steps.StepPhase;
import io.cosmosoftware.kite.steps.TestStep;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.cosmosoftware.kite.util.ReportUtils.getStackTrace;

/**
 * The type Test utils.
 */
public class TestUtils {
  
  static private final String IPV4_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
  
  private static final Logger logger = Logger.getLogger(TestUtils.class.getName());
  
  /**
   * Create a directory if not existing
   *
   * @param dirName directory name
   */
  public static void createDirs(String dirName) {
    dirName = verifyPathFormat(dirName);
    File dir = new File(dirName);
    if (!dir.exists()) {
      dir.mkdirs();
    }
  }
  
  public static String doHttpGet(String fullUrl) {
    StringBuilder result = new StringBuilder();
    
    CloseableHttpClient client = null;
    CloseableHttpResponse response = null;
    InputStream stream = null;
    BufferedReader reader = null;
    
    try {
      client = HttpClients.createDefault();
      response = client.execute(new HttpGet(fullUrl));
      stream = response.getEntity().getContent();
      reader = new BufferedReader(new InputStreamReader(stream));
      String line;
      while ((line = reader.readLine()) != null) {
        result.append(line);
      }
    } catch (Exception e) {
      logger.error("Exception while talking to the grid", e);
      result.delete(0, result.length());
      result.append(getStackTrace(e));
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the BufferedReader", e);
        }
      }
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the InputStream", e);
        }
      }
      if (response != null) {
        if (logger.isDebugEnabled()) logger.debug("response->" + response);
        try {
          response.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the CloseableHttpResponse", e);
        }
      }
      if (client != null) {
        try {
          client.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the CloseableHttpClient", e);
        }
      }
    }
    return result.toString();
  }
  
  /**
   * Download file.
   *
   * @param urlStr   the url str
   * @param filePath the file path
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void downloadFile(String urlStr, String filePath) throws IOException {
    if (urlStr.contains("~")) {
      urlStr = urlStr.replaceAll(
        "~", "/" + System.getProperty("user.home").replaceAll("\\\\", "/"));
    }
    logger.info("Downloading '" + filePath + "' from '" + urlStr + "'");
    
    ReadableByteChannel rbc = null;
    FileOutputStream fos = null;
    try {
      URL url = new URL(urlStr);
      rbc = Channels.newChannel(url.openStream());
      fos = new FileOutputStream(filePath);
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    } finally {
      if (fos != null)
        try {
          fos.close();
        } catch (IOException e) {
          logger.warn(e);
        }
      if (rbc != null)
        try {
          rbc.close();
        } catch (IOException e) {
          logger.warn(e);
        }
    }
  }
  
  /**
   * Execute command string.
   *
   * @param workingDir the working dir
   * @param command    the command
   * @param logger     the logger
   * @param logHeader  the log header
   *
   * @return the string
   * @throws Exception the exception
   */
  public static String executeCommand(String workingDir, List<String> command, Logger logger, String logHeader) throws Exception {
    ProcessBuilder builder =
      new ProcessBuilder(command);
    builder.directory(
      new File(workingDir)
        .getAbsoluteFile()); // this is where you set the root folder for the executable to run
    // with
    builder.redirectErrorStream(true);
    Process process = builder.start();
    
    Scanner s = new Scanner(process.getInputStream());
    StringBuilder text = new StringBuilder();
    while (s.hasNextLine()) {
      String line = s.nextLine();
      text.append(line);
      text.append("\n");
      logger.info("[nodejs console " + logHeader + " ] " + line);
    }
    s.close();
    
    int result = process.waitFor();
    System.out.printf("Process exited with result %d and output %s%n", result, text);
    return text.toString();
  }
  
  /**
   * Executes a JS script string with a given webdriver
   *
   * @param webDriver    the webdriver
   * @param scriptString the JS script to execute
   *
   * @return the result of the script execution
   */
  public static Object executeJsScript(WebDriver webDriver, String scriptString) throws KiteTestException {
    try {
      return ((JavascriptExecutor) webDriver).executeScript(scriptString);
    } catch (Exception e) {
      throw new KiteTestException("Unable to execute JavaScript code '"
        + scriptString.substring(0, scriptString.length() / 3) + "...' :"
        + e.getLocalizedMessage(), Status.BROKEN);
    }
  }
  
  /**
   * Gets node url.
   *
   * @param hubUrl    the hub url
   * @param sessionId the session id
   *
   * @return the node
   */
  public static String getNode(String hubUrl, String sessionId) {
    String node = null;
    
    String protocolAuthorityFormat = "%s://%s";
    
    CloseableHttpClient client = null;
    CloseableHttpResponse response = null;
    InputStream stream = null;
    JsonReader reader = null;
    
    try {
      URL url = new URL(hubUrl);
      client = HttpClients.createDefault();
      response =
        client.execute(
          new HttpGet(
            String.format(protocolAuthorityFormat, url.getProtocol(), url.getAuthority())
              + "/grid/api/testsession?session="
              + sessionId));
      stream = response.getEntity().getContent();
      reader = Json.createReader(stream);
      url = new URL(reader.readObject().getString("proxyId"));
      node = String.format(protocolAuthorityFormat, url.getProtocol(), url.getAuthority());
    } catch (Exception e) {
      logger.error("Exception while talking to the grid", e);
    } finally {
      if (reader != null) {
        reader.close();
      }
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the InputStream", e);
        }
      }
      if (response != null) {
        if (logger.isDebugEnabled()) {
          logger.debug("response->" + response);
        }
        try {
          response.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the CloseableHttpResponse", e);
        }
      }
      if (client != null) {
        try {
          client.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the CloseableHttpClient", e);
        }
      }
    }
    
    return node;
  }
  
  /**
   * Id to string string.
   *
   * @param id an int between 0 and 999
   *
   * @return a String with leading zero padding (e.g. 001, 029...)
   */
  public static String idToString(int id) {
    return "" + (id < 10 ? "00" + id : (id < 100 ? "0" + id : "" + id));
  }
  
  /**
   * Saves a JSON object into a file, with line breaks and indents.
   *
   * @param jsonStr  the json object as a String.
   * @param filename the file to be created with the json file.
   */
  public static void printJsonTofile(String jsonStr, String filename) {
    try {
      Map<String, Object> properties = new LinkedHashMap<>(1);
      properties.put(JsonGenerator.PRETTY_PRINTING, true);
      FileOutputStream fo = new FileOutputStream(filename);
      PrintWriter pw = new PrintWriter(fo, true);
      JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
      JsonWriter jsonWriter = writerFactory.createWriter(pw);
      JsonObject obj = Json.createReader(new StringReader(jsonStr)).readObject();
      jsonWriter.writeObject(obj);
      jsonWriter.close();
      pw.close();
      fo.close();
    } catch (Exception e) {
      logger.error("\r\n" + getStackTrace(e));
    }
  }
  
  /**
   * Saves a JSON object into a file, with line breaks and indents.
   *
   * @param testName the name of the test, which will be inlcuded in the file name
   * @param jsonStr  the json object as a String.
   * @param dirPath  the directory path where to save the file.
   */
  public static void printJsonTofile(String testName, String jsonStr, String dirPath) {
    try {
      String jsonFilename =
        testName.replace(" ", "")
          + "_"
          + new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date())
          + ".json";
      createDirs(dirPath);
      printJsonTofile(jsonStr, verifyPathFormat(dirPath) + jsonFilename);
    } catch (Exception e) {
      logger.error("\r\n" + getStackTrace(e));
    }
  }

  /**
   * Process the step in the new TestRunner and Kite
   * 
   * @param stepPhase        the StepPhase for this stepExecution
   * @param step             the test step to execute
   * @param parentStepReport the report of the parent step,
   *                         containing the status of the last step.
   */
  public static void processTestStep(StepPhase stepPhase, TestStep step, AllureStepReport parentStepReport) {
    if (!stepPhase.shouldProcess(step)) {
      logger.info("Do not execute Step "+ step.getClassName() + " because the phase don't match. ");
      return;
    }   
    step.init(stepPhase);
    if (!parentStepReport.failed() && !parentStepReport.broken()) {
      step.execute();
    } else {
      if (parentStepReport.canBeIgnore()) {
        step.execute();
      } else {
        step.skip();
      }
    }
    step.finish();
    parentStepReport.addStepReport(step.getStepReport());
  }
  
  /**
   * Reads the file content into a String
   *
   * @param path to the file
   *
   * @return the content of the file as a String
   * @throws IOException the io exception
   */
  public static String readFile(String path) throws IOException {
    String result = "";
    FileInputStream fin = new FileInputStream(path);
    BufferedReader buf = new BufferedReader(new InputStreamReader(fin));
    String line = buf.readLine();
    while (line != null) {
      result += line + "\r\n";
      line = buf.readLine();
    }
    buf.close();
    fin.close();
    return result;
  }
  
  /**
   * Record video from a video element and upload it to a server
   *
   * @param webDriver                     browser running the test
   * @param videoIndex                    video index in page's video array
   * @param recordingDurationInMillisecond duration to record
   * @param details                       Json object containing details about the video file (name, type, ..)
   * @param callbackUrl                   server url to send video back to
   *
   * @return the boolean
   */
  public static boolean recordVideoStream(
    WebDriver webDriver,
    int videoIndex,
    int recordingDurationInMillisecond,
    JsonObject details,
    String callbackUrl) {
    // todo: test this
    try {
      executeJsScript(webDriver,
        JSActionScript.recordVideoStreamScript(videoIndex, recordingDurationInMillisecond, details, callbackUrl));
      WebDriverWait wait =
        new WebDriverWait(webDriver, (recordingDurationInMillisecond + 10000) / 1000);
      wait.until(ExpectedConditions.presenceOfElementLocated(By.id("videoRecorded" + videoIndex)));
      return true;
    } catch (Exception e) {
      logger.error("video recording (index: " + videoIndex + ") failed for " + details.toString());
      return false;
    }
  }
  
  /**
   * Saves a screenshot of the webdriver/browser under "report/" + filename + ".png"
   *
   * @param webDriver the webdriver
   * @param path      the path
   * @param filename  the name of the file without path ("report/") and extension (" .png")
   *
   * @return true if successful, false otherwise
   */
  public static byte[] takeScreenshot(WebDriver webDriver, String path, String filename) {
    if (path != null) {
      try {
        File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        File dir = new File(path);
        if (!dir.isDirectory()) {
          dir.mkdirs();
        }
        if (!path.endsWith("/")) {
          path += "/";
        }
        String s = path + "screenshots/" + filename + ".png";
        File f = new File(s);
        FileUtils.copyFile(scrFile, f);
        return FileUtils.readFileToByteArray(f);
      } catch (Exception e) {
        logger.error("Exception in takeScreenshot(driver, " + path + ", " + filename
          + ") \r\n" + e.getLocalizedMessage());
        return null;
      }
    }
    return null;
  }
  
  /**
   * Convert time (ex: 12:34) to seconds for comparison.
   *
   * @param timeString time in string format.
   *
   * @return converted time to seconds.
   */
  public static int timeToSecond(String timeString) {
    List<String> splitedTimeString = Arrays.asList(timeString.split(":"));
    int count = splitedTimeString.size();
    switch (count) {
      // second only
      case 1: {
        return Integer.parseInt(splitedTimeString.get(0));
      }
      // minute and second (12:34)
      case 2: {
        return Integer.parseInt(splitedTimeString.get(0)) * 60
          + Integer.parseInt(splitedTimeString.get(1));
      }
      // hour, minute and second (12:34:56)
      case 3: {
        return Integer.parseInt(splitedTimeString.get(0)) * 3600
          + Integer.parseInt(splitedTimeString.get(0)) * 60
          + Integer.parseInt(splitedTimeString.get(1));
      }
    }
    return 0;
  }
  
  public static String verifyPathFormat(String url) {
    if (url != null && !url.endsWith("/")) {
      return url + "/";
    }
    return url;
  }
  
  /**
   * Check the video playback by verifying the pixel sum of 2 frame between a time interval of
   * 500ms. if (getSum(frame2) - getSum(frame1) != 0 ) => return "video", if getSum(frame2) ==
   * getSum(frame1) > 0 => return "still" if getSum(frame2) == getSum(frame1) == 0 => return "blank"
   *
   * @param webDriver webdriver that control the browser
   * @param index     index of the video element on the page in question
   *
   * @return "blank" or "still" or "video"
   * @throws InterruptedException the interrupted exception
   */
  public static String videoCheck(WebDriver webDriver, int index) throws KiteTestException {
    return videoCheckSum(webDriver, index).getString("result");
  }
  
  /**
   * Check the video playback by verifying the pixel sum of 2 frame between a time interval of
   * 500ms. if (getSum(frame2) - getSum(frame1) != 0 ) => return "video", if getSum(frame2) ==
   * getSum(frame1) > 0 => return "still" if getSum(frame2) == getSum(frame1) == 0 => return "blank"
   *
   * @param webDriver webdriver that control the browser
   * @param index     index of the video element on the page in question
   *
   * @return the check sum
   * @throws InterruptedException the interrupted exception
   */
  public static JsonObject videoCheckSum(WebDriver webDriver, int index) throws KiteTestException {
    String result = "blank";
    JsonObjectBuilder resultObject = Json.createObjectBuilder();
    long canvasData1 =
      (long) executeJsScript(webDriver, JSActionScript.getVideoFrameValueSumByIndexScript(index));
    waitAround(500);
    long canvasData2 =
      (long) executeJsScript(webDriver, JSActionScript.getVideoFrameValueSumByIndexScript(index));
    long canvasData3 = 0;
    if (canvasData1 != 0 || canvasData2 != 0) {
      long diff = Math.abs(canvasData2 - canvasData1);
      if (diff != 0) {
        result = "video";
      } else {
        waitAround(Timeouts.ONE_SECOND_INTERVAL);
        
        canvasData3 = (long) executeJsScript(webDriver, JSActionScript.getVideoFrameValueSumByIndexScript(index));
        result = Math.abs(canvasData3 - canvasData1) != 0 ? "video" : "still";
      }
    }
    
    resultObject
      .add("checksum1", canvasData1)
      .add("checksum2", canvasData2)
      .add("canvasData3", canvasData3) // if ever needed
      .add("result", result);
    return resultObject.build();
  }
  
  /**
   * Waits for a duration
   *
   * @param durationInMillisecond duration in milliseconds
   */
  public static void waitAround(int durationInMillisecond) {
    try {
      logger.debug("sleeping " + durationInMillisecond + "ms.");
      Thread.sleep(durationInMillisecond);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  public static String filePath( String filePath ) {
    return filePath.contains("~") ? filePath.replaceAll(
      "~", "/" + System.getProperty("user.home").replaceAll("\\\\", "/"))
      : filePath;
  }

  /**
   * Gets private ip by querying the hub against a session id.
   *
   * @param hupIpOrDns the hup ip or dns
   * @param sessionId  the session id
   *
   * @return the private ip
   */
  public static String getPrivateIp(String hupIpOrDns, String sessionId) {

    String privateIp = null;

    CloseableHttpClient client = null;
    CloseableHttpResponse response = null;
    InputStream stream = null;
    JsonReader reader = null;

    try {
      client = HttpClients.createDefault();
      HttpGet httpGet =
          new HttpGet("http://" + hupIpOrDns + ":4444/grid/api/testsession?session=" + sessionId);
      response = client.execute(httpGet);
      stream = response.getEntity().getContent();
      reader = Json.createReader(stream);
      JsonObject object = reader.readObject();
      String proxyId = object.getString("proxyId");
      URL url = new URL(proxyId);
      privateIp = url.getHost();
    } catch (Exception e) {
      logger.error("Exception while talking to the grid", e);
    } finally {
      if (reader != null)
        reader.close();
      if (stream != null)
        try {
          stream.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the InputStream", e);
        }
      if (response != null) {
        if (logger.isDebugEnabled())
          logger.debug("response->" + response);
        try {
          response.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the CloseableHttpResponse", e);
        }
      }
      if (client != null)
        try {
          client.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the CloseableHttpClient", e);
        }
    }

    return privateIp;

  }

  public static JsonArray getJsonArray(JsonObject jsonObject, String key) {
    JsonArray fileArray = null;
    try {
      String fileUrl = jsonObject.getString(key, "");
      if (!fileUrl.equals("") ) {
        String File = System.getProperty("java.io.tmpdir") + key + ".json";
        System.setProperty(key, fileUrl);
        if (fileUrl.contains("file://")) {
          downloadFile(fileUrl, File);
        } else {
          File = fileUrl;
        }
        fileArray = readJsonFile(File).getJsonArray(key);
      } else {
        fileArray = jsonObject.getJsonArray(key);
      }
    } catch (Exception e) {
      logger.error(getStackTrace(e));
    }
    return fileArray;
  }

  /**
   * Reads a json file into a JsonObject
   *
   * @param jsonFile the file to read
   *
   * @return the jsonObject
   */
  public static JsonObject readJsonFile(String jsonFile) {
    FileReader fileReader = null;
    JsonReader jsonReader = null;
    try {
      logger.info("Reading '" + jsonFile + "' ...");
      fileReader = new FileReader(new File(jsonFile));
      jsonReader = Json.createReader(fileReader);
      return jsonReader.readObject();
    } catch (Exception e) {
      logger.error(getStackTrace(e));
    } finally {
      if (fileReader != null) {
        try {
          fileReader.close();
        } catch (IOException e) {
          logger.warn(e.getMessage(), e);
        }
      }
      if (jsonReader != null) {
        jsonReader.close();
      }
    }
    return null;
  }
}
