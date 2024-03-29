/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;

/**
 * Logger for KITE allowing adding a prefix to differentiate logs from the different clients.
 */
public class KiteLogger {

  private final Logger logger;
  private String prefix = "";
  private List<Appender> appenders = new ArrayList<>();

  private KiteLogger(Logger logger, String prefix) {
    this.logger = logger;
    this.prefix = prefix;
  }

  private KiteLogger(Logger logger) {
    this.logger = logger;
  }

  public static KiteLogger getLogger(String name, String prefix) {
    return new KiteLogger(LogManager.getLogger(name), String.valueOf(prefix));
  }

  public static KiteLogger getLogger(KiteLogger kiteLogger, String prefix) {
    return new KiteLogger(LogManager.getLogger(kiteLogger.getName()),
        String.valueOf(prefix));
  }

  public static KiteLogger getLogger(String name) {
    return new KiteLogger(LogManager.getLogger(name));
  }

  public void setPrefix(String prefix) {
    logger.info("setting prefix to " + prefix);
    this.prefix = prefix;
  }

  public void error(Object msg) {
    logger.error(prefix + msg);
  }

  public void error(Object msg, Throwable t) {
    logger.error(prefix + msg, t);
  }

  public void info(Object msg) {
    logger.info(prefix + msg);
  }

  public void info(Object msg, Throwable t) {
    logger.info(prefix + msg, t);
  }

  public void debug(Object msg) {
    logger.debug(prefix + msg);
  }

  public void debug(Object msg, Throwable t) {
    logger.debug(prefix + msg, t);
  }

  public void trace(Object msg) {
    logger.trace(prefix + msg);
  }

  public void trace(Object msg, Throwable t) {
    logger.trace(prefix + msg, t);
  }

  public void warn(Object msg) {
    logger.warn(prefix + msg);
  }

  public void warn(Object msg, Throwable t) {
    logger.warn(prefix + msg, t);
  }

  public void fatal(Object msg) {
    logger.fatal(prefix + msg);
  }

  public void fatal(Object msg, Throwable t) {
    logger.fatal(prefix + msg, t);
  }

  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  public final String getName() {
    return logger.getName();
  }

}
