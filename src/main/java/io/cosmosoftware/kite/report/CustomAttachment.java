/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.*;
import java.util.UUID;
import org.apache.commons.io.FileUtils;

import static io.cosmosoftware.kite.util.TestUtils.readJsonString;


public class CustomAttachment {

  private final String uuid = UUID.randomUUID().toString();
  private final String name;
  private final String type;
  private final String fileExtension;
  private String text;
  private byte[] screenshot;

  public CustomAttachment(String name, String type) {
    this.name = name;
    this.type = type;
    this.fileExtension = "";
  }

  public CustomAttachment(String name, String type, String fileExtension) {
    this.name = name;
    this.type = type;
    this.fileExtension = fileExtension;
  }

  public byte[] getScreenshot() {
    return screenshot;
  }

  public void setScreenshot(byte[] screenshot) {
    this.screenshot = screenshot;
  }

  public String getFileExtension() {
    return fileExtension;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getText() {
    return text;
  }

  public String getUuid() {
    return uuid;
  }

  public boolean isText() {
    return this.screenshot == null;
  }

  public void setText(String text) {
    this.text = text;
  }

  public boolean isJson () {
    return this.type.contains("json");
  }

  public JsonObject toJson() {
    return Json.createObjectBuilder()
        .add("name", name)
        .add("type", type)
        .add("source", uuid + "-attachment." + fileExtension)
        .build();
  }

  public void saveToFile(String reportPath) {
    File file = new File(reportPath + uuid + "-attachment." + fileExtension);
    FileOutputStream fos = null;
    BufferedWriter writer = null;
    try {

      // Writes bytes from the specified byte array to this file output stream
      if (isText() && text != null) {
        writer = new BufferedWriter(new FileWriter(file));
        writer.write(text);
      } else {
//        fos = new FileOutputStream(file);
//        fos.write(screenshot);
        FileUtils.writeByteArrayToFile(file, this.screenshot);
      }

    }
    // todo: better handling
    catch (FileNotFoundException e) {
      System.out.println("File not found" + e);
    } catch (IOException ioe) {
      System.out.println("Exception while writing file " + ioe);
    } finally {
      // close the streams using close method
      try {
        if (fos != null) {
          fos.close();
        }
        if (writer != null) {
          writer.close();
        }
      } catch (IOException ioe) {
        System.out.println("Error while closing stream: " + ioe);
      }

    }
  }

  public JsonObject getJsonText() {
    if (this.isJson()) {
      return readJsonString(this.text);
    }
    return null;
  }

}
