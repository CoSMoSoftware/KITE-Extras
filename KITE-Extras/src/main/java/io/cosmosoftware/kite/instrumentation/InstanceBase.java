/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import javax.json.JsonArray;
import javax.json.JsonObject;


/**
 * Parent class for Client and Gateway containing common methods and variables.
 */
public class InstanceBase {
  
  
  /**
   * The Commands.
   */
  protected final String name;
  protected final String[] commands;
  protected final String[] cleanUpCommands;
  /**
   * The Key file path.
   */
  protected final String keyFilePath;
  /**
   * The Pipe.
   */
  protected final String pipe = " || true && ";
  /**
   * The Username.
   */
  protected final String username;


  protected boolean commandExecuted = false;

  /**
   * Instantiates a new Instance base.
   *
   * @param jsonObject the json object
   *
   * @throws Exception the exception
   */
  protected InstanceBase(JsonObject jsonObject) throws Exception {
    name = jsonObject.getString("name");
    username = jsonObject.getString("username");
    keyFilePath = System.getProperty("user.home") + jsonObject.getString("keyFilePath");
    JsonArray jsArray = jsonObject.getJsonArray("commands");
    if (jsArray == null || jsArray.size() < 0) {
      throw new Exception("Error in json config client, commands are invalid.");
    }
    commands = new String[jsArray.size()];
    for (int i = 0; i < jsArray.size(); i++) {
      commands[i] = jsArray.getString(i);
    }

    jsArray = jsonObject.getJsonArray("cleanUpCommands");
    if (jsArray == null || jsArray.size() < 0) {
      cleanUpCommands = null;
    } else {
      cleanUpCommands = new String[jsArray.size()];
      for (int i = 0; i < jsArray.size(); i++) {
        cleanUpCommands[i] = jsArray.getString(i);
      }
    }

  }
  
  
  /**
   * Gets the command line to be executed by SSH
   *
   * @return the commandLine
   */
  public String getCommandLine() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < commands.length; i++) {
      builder.append(commands[i]);
      if (i < commands.length - 1) {
        builder.append(pipe);
      }
    }
    return builder.toString();
  }


  /**
   * Gets the command line to be executed by SSH
   *
   * @return the clean up command line
   */
  public String getCleanUpCommandLine() {
    if (cleanUpCommands == null || cleanUpCommands.length < 1) {
      return null;
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < cleanUpCommands.length; i++) {
      builder.append(cleanUpCommands[i]);
      if (i < cleanUpCommands.length - 1) {
        builder.append(pipe);
      }
    }
    return builder.toString();
  }
  
  
  /**
   * For debugging.
   *
   * @return a String representation of this object
   */
  @Override
  public String toString() {
    String s = "\r\n";
    s += "username: " + this.username + "\r\n";
    s += "keyFilePath: " + this.keyFilePath + "\r\n";
    s += "commands: " + "\r\n";
    for (String g : commands) {
      s += " " + g + "\r\n";
    }
    return s;
  }


  /**
   *
   * @return true if a clean up is required.
   */
  public boolean cleanUpRequired() {
    return this.commandExecuted;
  }

}
