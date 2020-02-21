/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.util;

import static io.cosmosoftware.kite.util.TestUtils.getDir;

/**
 * The Class KITEConstants.
 */
public class KiteConstants {

  /**
   * The Constant MDC_TAG_NAME.
   */
  final public static String MDC_TAG_NAME = "tag";

  /**
   * The Constant ENTITY.
   */
  final public static String ENTITY = "entity";

  /**
   * The Constant ENTITY_ID.
   */
  final public static String ENTITY_ID = "entity_id";

  /**
   * The Constant MAKE_PAAS.
   */
  final public static String MAKE_PAAS = "make_paas";

  /**
   * The Constant SHOULD_SHUTDOWN.
   */
  final public static String SHOULD_SHUTDOWN = "shouldShutdown";

  /**
   * The Constant TIME_FORMAT.
   */
  final public static String KITE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /**
   * The Constant JAR_DOWNLOAD_PATH.
   */
  final public static String JAR_DOWNLOAD_PATH = getDir("java.io.tmpdir");// + "lib" +
                                                                          // File.separator;

  /**
   * The Constant AZUREAUTH_PROPERTIES.
   */
  final public static String AZUREAUTH_PROPERTIES = "azureauth.properties";

  /**
   * The Constant GW3_SCRIPT.
   */
  final public static String GW3_SCRIPT = "gw3_script.sh";

  /**
   * The Constant GW12_SCRIPT.
   */
  final public static String GW12_SCRIPT = "gw12_script.sh";

  /**
   * The Constant INSTANCE_SCRIPT.
   */
  final public static String INSTANCE_SCRIPT = "instance_script.sh";

  /**
   * The Constant grid config file.
   */
  final public static String AWS_CREDENTIAL_CONFIG = "grid.config.json";

  /**
   * The max ssh tries.
   */
  final public static int MAX_SSH_TRIES = 10;

  /**
   * The retry wait.
   */
  final public static int SSH_RETRY_WAIT = 2000; // milliseconds

  /**
   * The Constant AMAZON_API_CALL_DELAY.
   */
  final public static int AMAZON_API_CALL_DELAY = 5000; // milliseconds

  /**
   * The Constant ANDROID_AMI_ID.
   */
  final public static String ANDROID_AMI_ID = "android_ami_id";

  /**
   * The Constant ANDROID_INSTANCE_TYPE.
   */
  final public static String ANDROID_INSTANCE_TYPE = "android_instance_type";

  /**
   * The Constant JSPATH.
   */
  final public static String JSPATH = "JSPATH";
  
  /**
   * The Constant LOCALSERVER.
   */
  final public static String LOCALSERVER = "localserver";


  final public static String ETH1 = "eth13";
  final public static String ETH2 = "eth14";
  

}
