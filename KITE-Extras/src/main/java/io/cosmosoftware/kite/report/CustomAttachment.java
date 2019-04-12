package io.cosmosoftware.kite.report;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.*;
import java.util.UUID;


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
  
  public String getFileExtension() {
    return fileExtension;
  }
  
  public String getType() {
    return type;
  }
  
  public String getName() {
    return name;
  }
  
  public void setScreenshot(byte[] screenshot) {
    this.screenshot = screenshot;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public String getText() {
    return text;
  }
  
  public String getUuid() {
    return uuid;
  }
  
  public boolean isText(){
    return this.screenshot == null;
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
        fos = new FileOutputStream(file);
        fos.write(screenshot);
      }
    
    }
    // todo: better handling
    catch (FileNotFoundException e) {
      System.out.println("File not found" + e);
    }
    catch (IOException ioe) {
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
      }
      catch (IOException ioe) {
        System.out.println("Error while closing stream: " + ioe);
      }
    
    }
  }
  
}
