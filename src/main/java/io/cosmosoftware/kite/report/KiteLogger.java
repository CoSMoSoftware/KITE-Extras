/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import org.apache.log4j.Logger;

/**
 * Logger for KITE allowing adding a prefix to differentiate logs from the different clients.
 */
public class KiteLogger {

  private final Logger logger;
  private String prefix = "";

  public KiteLogger(Logger logger, String prefix) {
    this.logger = logger;
    this.prefix = prefix;
  }

  public KiteLogger(Logger logger) {
    this.logger = logger;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public void error(String msg) {
    logger.error(prefix + msg);
  }

  public void info(String msg) {
    logger.info(prefix + msg);
  }

  public void debug(String msg) {
    logger.debug(prefix + msg);
  }

  public void trace(String msg) {
    logger.trace(prefix + msg);
  }

  public void warn(String msg) {
    logger.warn(prefix + msg);
  }

  public static KiteLogger getLogger(String name, String prefix) {
    return new KiteLogger(Logger.getLogger(name), String.valueOf(prefix));
  }
  
  public static KiteLogger getLogger(String name) {
    return new KiteLogger(Logger.getLogger(name));
  }
  
  
}
