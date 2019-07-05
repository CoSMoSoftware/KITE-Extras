/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import static io.cosmosoftware.kite.util.TestUtils.readJsonFile;

import io.cosmosoftware.kite.util.ReportUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

// XXX todo:  move to KITE engine, then extend this for individual tests

public class CSVHelper {

  private static final KiteLogger logger = KiteLogger.getLogger(CSVHelper.class.getName());
  private static Map<String, String> keyValMap = new LinkedHashMap<String, String>();

  private String filename;
  private FileOutputStream fout = null;
  private boolean initialized = false;
  private PrintWriter pw = null;

  public CSVHelper(String filename) {
    this.filename = filename;
  }

  /**
   * Translate the JsonObject into a flat Map<String,String> of key - value pairs For nested
   * objects, the key becomes parentkey.key, to achieve the flat Map.
   *
   * @param json the JsonObject
   * @return Map<String key, Object: either json value or another Map < String, Object>
   */
  private static Map<String, String> jsonToHashMap(JsonObject json) throws JsonException {
    keyValMap = new LinkedHashMap<>(); // re-initialise it in case.
    if (json != JsonObject.NULL) {
      toMap(json, "");
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
   * Recursively browse the jsonObject and returns a List<Object> where Object is either a
   * List<Object> or another Map<String, Object> (see toMap)
   *
   * @param array JsonArray
   * @param parent json key of the parent json node.
   * @return List<Object1> where Object is either a List<Object> or another Map<String, Object> (see
   * toMap)
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
   * @return Map<String key, Object: either json value or another Map < String, Object>
   */
  private static Map<String, Object> toMap(JsonObject object, String parent) throws JsonException {
    Map<String, Object> map = new LinkedHashMap<String, Object>();
    Iterator<String> keysItr = object.keySet().iterator();
    while (keysItr.hasNext()) {
      String key = keysItr.next();
      Object value = object.get(key);
      String keyFull = parent + (parent.length() > 0 ? "." : "") + key;
      if (value instanceof JsonArray) {
        value = toList((JsonArray) value, keyFull);
      } else if (value instanceof JsonObject) {
        value = toMap((JsonObject) value, keyFull);
      } else {
        keyValMap.put(keyFull, value.toString());
      }
      map.put(key, value);
    }
    return map;
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
   * Convert the JSON Object into a line of keys that can be printed as the header of the CSV file.
   *
   * @return line String to be printed in the CSV file
   */
  private static String keysLine(Map<String, String> map) {
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
   * @param o Object object containing the test results. Either a JsonObject or any Object with a
   * toString() method
   * @param path the file path where to save the file.
   * @param clientId the id of the client to whom the data belongs to
   */
  public synchronized void println(Object o, String path, String clientId) {
    try {
      if (!initialized) {
        File dir = new File(path);
        if (!dir.isDirectory()) {
          dir.mkdirs();
        }
        fout = new FileOutputStream(path + filename);
        pw = new PrintWriter(fout, true);
      }
      Map<String, String> map = new LinkedHashMap<>();
      if (clientId != null) {
        map.put("clientId", clientId);
      }
      if (o instanceof LinkedHashMap) {
        map.putAll((LinkedHashMap) o);
      } else if (o instanceof JsonObject) {
        map.putAll(jsonToHashMap((JsonObject) o));
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
   * Convert the JSON Object into a line of values that can be printed in the CSV file.
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
