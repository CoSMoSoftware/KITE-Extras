/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.util;

import org.apache.log4j.Logger;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

// XXX todo:  move to KITE engine, then extend this for individual tests

/**
 * TestHelper is a Singleton class that collects and save KITE load testing stats into a CSV file.
 */
public class TestHelper {
  
  private static final Logger logger = Logger.getLogger(TestHelper.class.getName());
  private static HashMap<String, TestHelper> instance = new HashMap<String, TestHelper>();
  private static Map<String, String> keyValMap = new LinkedHashMap<String, String>();
  
  private final String filename;
  private FileOutputStream fout = null;
  private boolean initialized = false;
  private PrintWriter pw = null;
  private int testID = 1; // setStartTimestamp count at 1
  
  private TestHelper(String prefix) {
    filename = prefix + "report_" + new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date()) + ".csv";
  }
  
  /**
   * Close the printWriter object. It must be called once the test is over.
   */
  public void close() {
    try {
      if (pw != null) {
        logger.debug("Closing " + filename);
        pw.close();
        fout.close();
      }
    } catch (Exception e) {
      logger.error("\r\n" + ReportUtils.getStackTrace(e));
    }
  }
  
  /**
   * Gets instance.
   *
   * @param prefix the prefix
   *
   * @return and instance of TestHelper
   */
  public static TestHelper getInstance(String prefix) {
    try {
      if (!instance.containsKey(prefix)) {
        instance.put(prefix, new TestHelper(prefix));
      }
    } catch (Exception e) {
      logger.error("\r\n" + ReportUtils.getStackTrace(e));
    }
    return instance.get(prefix);
  }
  
  /**
   * Translate the JsonObject into a flat Map<String,String> of key - value pairs For nested
   * objects, the key becomes parentkey.key, to achieve the flat Map.
   *
   * @param json the JsonObject
   *
   * @return Map<String key, Object: either json value or another Map < String, Object>
   * @throws JsonException
   */
  private static Map<String, String> jsonToHashMap(JsonObject json) throws JsonException {
    Map<String, Object> retMap = new LinkedHashMap<String, Object>();
    keyValMap = new LinkedHashMap<String, String>(); // re-initialise it in case.
    StringBuilder keyBuilder = new StringBuilder();
    if (json != JsonObject.NULL) {
      retMap = toMap(json, "");
    }
    if (logger.isDebugEnabled()) {
      logger.debug("jsonToHashMap() dump");
      for (String key : keyValMap.keySet()) {
        logger.debug("keyList[" + key + "] = " + keyValMap.get(key));
      }
    }
    return keyValMap;
  }
  
  /**
   * Returns a JsonObject into a pretty printed String.
   *
   * @param jsonObject the json object
   *
   * @return a String representing the pretty printed JsonObject.
   */
  public static String jsonToString(JsonObject jsonObject) {
    Map<String, Object> properties = new LinkedHashMap<>(1);
    properties.put(JsonGenerator.PRETTY_PRINTING, true);
    final StringWriter stringWriter = new StringWriter();
    JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
    JsonWriter jsonWriter = writerFactory.createWriter(stringWriter);
    jsonWriter.write(jsonObject);
    jsonWriter.close();
    return stringWriter.toString();
  }
  
  /**
   * Convert the JSON Object into a line of keys that can be printed as the header of the CSV file.
   *
   * @param map
   *
   * @return line String to be printed in the CSV file
   */
  private String keysLine(Map<String, String> map) {
    String line = "";
    int i = 0;
    for (String key : map.keySet()) {
      line += key + (i++ < map.size() ? "," : "");
    }
    return line;
  }
  
  /**
   * Print the test statistic line.
   *
   * @param o    Object object containing the test results. Either a JsonObject or any Object with a             toString() method
   * @param path the file path where to save the file.
   */
  public synchronized void println(Object o, String path) {
    try {
      if (!initialized) {
        File dir = new File(path);
        if (!dir.isDirectory()) {
          dir.mkdirs();
        }
        fout = new FileOutputStream(path + filename);
        pw = new PrintWriter(fout, true);
      }
      Map<String, String> map = null;
      if (o instanceof LinkedHashMap) {
        map = (LinkedHashMap) o;
      } else if (o instanceof JsonObject) {
        map = TestHelper.jsonToHashMap((JsonObject) o);
      } else {
        pw.println(o.toString());
        return;
      }
      if (!initialized) {
        pw.println(keysLine(map));
      }
      pw.println(valuesLine(map));
    } catch (Exception e) {
      logger.error("\r\n" + ReportUtils.getStackTrace(e));
    }
    initialized = true;
  }
  
  /**
   * Recursively browse the jsonObject and returns a List<Object1> where Object is either a
   * List<Object> or another Map<String, Object> (see toMap)
   *
   * @param array  JsonArray
   * @param parent json key of the parent json node.
   *
   * @return List<Object1> where Object is either a List<Object> or another Map<String, Object> (see
   * toMap)
   * @throws JsonException
   */
  private static List<Object> toList(JsonArray array, String parent) throws JsonException {
    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < array.size(); i++) {
      Object value = array.get(i);
      parent = parent + "[" + i + "]";
      if (value instanceof JsonArray) {
        value = toList((JsonArray) value, parent);
      } else if (value instanceof JsonObject) {
        value = toMap((JsonObject) value, parent);
      }
      list.add(value);
    }
    return list;
  }
  
  /**
   * Recursively browse the jsonObject and returns a Map<String key, Object: either json value or
   * another map
   *
   * @param object JsonObject
   * @param parent json key of the parent json node.
   *
   * @return Map<String key, Object: either json value or another Map < String, Object>
   * @throws JsonException
   */
  private static Map<String, Object> toMap(JsonObject object, String parent) throws JsonException {
    Map<String, Object> map = new LinkedHashMap<String, Object>();
    Iterator<String> keysItr = object.keySet().iterator();
    while (keysItr.hasNext()) {
      String key = keysItr.next();
      Object value = object.get(key);
      if (value instanceof JsonArray) {
        value = toList((JsonArray) value, key);
      } else if (value instanceof JsonObject) {
        value = toMap((JsonObject) value, key);
      } else {
        String keyFull = parent + (parent.length() > 0 ? "." : "") + key;
        keyValMap.put(keyFull, value.toString());
      }
      map.put(key, value);
    }
    return map;
  }
  
  /**
   * Convert the JSON Object into a line of values that can be printed in the CSV file.
   *
   * @param map
   *
   * @return line String to be printed in the CSV file
   */
  private String valuesLine(Map<String, String> map) {
    String line = "";
    int i = 0;
    for (String key : map.keySet()) {
      line += map.get(key) + (i++ < map.size() ? "," : "");
    }
    return line;
  }
}
