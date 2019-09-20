/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Status;
import org.json.JSONObject;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static io.cosmosoftware.kite.util.ReportUtils.getStackTrace;
import static io.cosmosoftware.kite.util.TestUtils.executeJsScript;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;

public class GenerateChartsStep extends TestStep {

  private final JsonObject getChartsConfig;
  private final String pathToGenerateChartsFile;
  private final String pathToChartBundleMin;
  private final String pathToJar;

  public GenerateChartsStep(Runner runner, JsonObject getChartsConfig, String pathToJar) {
    super(runner);
    this.getChartsConfig = getChartsConfig;
    this.pathToGenerateChartsFile = getChartsConfig.getString("pathToGenerateChartsFile");
    this.pathToChartBundleMin = getChartsConfig.getString("pathToChartBundleMin");
    this.pathToJar = pathToJar;
    setOptional(true);
  }

  @Override
  public String stepDescription() { return "Generate charts"; }

  @Override
  protected void step() throws KiteTestException {
    try {
      logger.info("Loading scripts...");
      executeJsScript(webDriver, createChartsConfig());
      runScript(pathToChartBundleMin);
      runScript(pathToGenerateChartsFile);
      logger.info("Creating canvas...");
      executeJsScript(webDriver, getCreateCanvasScript());
      waitAround(5000);
      List<Map<String, Object>> configs = (List) executeJsScript(webDriver, getConfigsScript());
      logger.info("Creating charts...");
      Base64.Decoder decoder = Base64.getDecoder();
      for(int i = 0; i < configs.size(); i++) {
        // Get the title
        JSONObject jsonObject = new JSONObject(configs.get(i));
        String title = jsonObject.getJSONObject("options").getJSONObject("title").getString("text");

        executeJsScript(webDriver, getCreateChartScript(i));
        waitAround(2000);
        String screen = (String) executeJsScript(webDriver, getChartScript());
        reporter.screenshotAttachment(this.report, title, decoder.decode(screen));
      }
    } catch (IOException e) {
      logger.error(getStackTrace(e));
      throw new KiteTestException("Failed to load script files", Status.FAILED, e);
    } catch (KiteTestException e) {
      logger.error(getStackTrace(e));
      throw e;
    }
  }

  private void runScript(String pathToScript) throws IOException, KiteTestException {
    logger.info("Loading " + pathToScript);
    InputStream in = null;
    if (pathToJar != null) {
      JarFile jarFile = new JarFile(new File(pathToJar));
      if (pathToScript.startsWith("/")) {
        pathToScript = pathToScript.substring(1);
      }
      JarEntry jarEntry = jarFile.getJarEntry(pathToScript);
      in = jarFile.getInputStream(jarEntry);
    } else {
      in = getClass().getResourceAsStream(pathToScript);
    }      
    if (in == null) {
      throw new IOException("File " + pathToScript + " not found in classpath");
    }
    BufferedReader buf = new BufferedReader(new InputStreamReader(in));
    StringWriter out = new StringWriter();
    int b;
    while ((b=buf.read()) != -1) {
      out.write(b);
    }
    out.flush();
    out.close();
    in.close();
    executeJsScript(webDriver, out.toString());
  }

  
  private String createChartsConfig() {
    String config = "";
    JsonObject configJson = getChartsConfig.getJsonObject("config");
    // If the configuration exists
    if (configJson != null) {
      Iterator<String> keys = configJson.keySet().iterator();
      while (keys.hasNext()) {
        String key = keys.next();
        Object obj = configJson.get(key);
        if (obj instanceof JsonArray) {
          config += "window." + key + " = [";
          for (int i = 0; i < ((JsonArray) obj).size(); i++) {
            config += ((JsonArray) obj).get(i);
            if (i != ((JsonArray) obj).size() - 1) {
              config += ",";
            }
          }
          config += "];";
        } else {
          config += "window." + key + "=" + obj + ";";
        }
      }
    } else { // Default configuration
      config = "window.width = 600;"
          + "window.height = 400;"
          + "window.colors = ['#1DABE6', '#C3CED0', '#41ab48', '#E43034', '#000000', '#AF060F'];"
          + "window.filter = ['inbound-rtp', 'outbound-rtp', 'candidate-pair'];"
          + "window.charts = ['receivedAudioBitrates', 'receivedVideoBitrates', 'sentAudioBitrates',"
          + "                 'sentVideoBitrates', 'audioPacketLoss', 'videoPacketLoss',"
          + "                 'audioJitter', 'videoFramesSent', 'audioLevel', 'frameRate'];";
    }
    return config;
  }

  public String getCreateCanvasScript() {
    return "window.canvas = document.createElement(\"canvas\");"
      +  "window.canvas.style.display = \"none\";"
      +  "window.canvas.width = window.width;"
      +  "window.canvas.height = window.height;"
      +  "document.body.appendChild(window.canvas);";
  }

  public String getConfigsScript() {
    return "window.stats = window.getStatsObject(window.StatsOvertime);"
      +  "window.configs = window.getChartsConfigs(window.stats, window.charts);"
      +  "return window.configs;";
  }

  public String getCreateChartScript(int index) {
    return "window.chart = new Chart(window.canvas, window.configs[" + index + "]);";
  }

  public String getChartScript() {
    return "window.dataURL = window.canvas.toDataURL('image/png').split(',')[1];"
      +  "return window.dataURL;";
  }
  
}