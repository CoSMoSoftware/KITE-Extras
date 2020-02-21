/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.manager;

import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;

import com.jcraft.jsch.*;
import io.cosmosoftware.kite.exception.SSHManagerException;
import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.util.KiteConstants;
import io.cosmosoftware.kite.util.TestUtils;
import org.apache.log4j.MDC;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

/**
 * Class handling SSH command running.
 */
public class SSHManager implements Callable<SSHManager> {

  /**
   * The Constant logger.
   */
  private static final KiteLogger logger = KiteLogger.getLogger(SSHManager.class.getName());
  //logger on KITE Server version
  // todo: check logging issues
//  private static final Logger logger = Logger.getLogger(SSHManager.class.getName());

  static {
    JSch.setConfig("StrictHostKeyChecking", "no");
  }

  /**
   * The command line.
   */
  private String commandLine;
  private int count = 0;
  private int exitStatus = 0;
  /**
   * The host ip or name.
   */
  private String hostIpOrName;
  private int index = 0;
  /**
   * The key file address.
   */
  private String keyFilePath;
  /**
   * The password.
   */
  private String password;
  /**
   * The username.
   */
  private String username;

  /**
   * The max lines
   */
  private int maxLines = 10;

  /**
   * The command result.
   */
  private StringBuilder commandResult = new StringBuilder();

  /**
   * Instantiates a new Ssh manager.
   *
   * @param keyFilePath the key file path
   * @param username the username
   * @param hostIpOrName the host ip or name
   * @param commandLine the command line
   */
  public SSHManager(String keyFilePath, String username, String hostIpOrName, String commandLine) {
    this.keyFilePath = TestUtils.filePath(keyFilePath);
    this.username = username;
    this.hostIpOrName = hostIpOrName;
    this.commandLine = commandLine;
  }


  /**
   * Instantiates a new Ssh manager.
   *
   * @param keyFilePath the key file path
   * @param username the username
   * @param password the password
   * @param hostIpOrName the host ip or name
   * @param commandLine the command line
   */
  public SSHManager(String keyFilePath, String username, String password, String hostIpOrName,
      String commandLine) {
    this.keyFilePath = keyFilePath;
    this.username = username;
    this.password = password;
    this.hostIpOrName = hostIpOrName;
    this.commandLine = commandLine;
  }

  /**
   * Instantiates a new Ssh manager.
   *
   * @param keyFilePath the key file path
   * @param username the username
   * @param password the password
   * @param hostIpOrName the host ip or name
   * @param commandLine the command line
   * @param index the index
   * @param count the count
   */
  public SSHManager(String keyFilePath, String username, String password, String hostIpOrName,
      String commandLine, int index, int count) {
    this.keyFilePath = TestUtils.filePath(keyFilePath);
    this.username = username;
    this.password = password;
    this.hostIpOrName = hostIpOrName;
    this.commandLine = commandLine;
    this.index = index;
    this.count = count;
  }

  /**
   * Call.
   *
   * @return The same object in case of successful run
   * @throws Exception of KiteInsufficientValueException type if username and keyFileAddress is not
   * given and of SSHManagerException type if there found an error while connecting to the host
   * using SSH
   */
  @Override
  public SSHManager call() throws Exception {
    return  sendCommand();
  }

  private SSHManager sendCommand() throws InterruptedException, SSHManagerException {
    MDC.put(KiteConstants.MDC_TAG_NAME, this.hostIpOrName);
    Session session = null;
    Channel channel = null;
    InputStream inputStream = null;
    try {
      JSch jsch = new JSch();
      jsch.addIdentity(this.keyFilePath);

      // enter your own EC2 instance IP here
      session = jsch.getSession(this.username, this.hostIpOrName, 22);
      if (this.password != null) {
        session.setPassword(this.password);
      }
      logger.info("Connecting the session to " + this.hostIpOrName + " ...");
      session.connect();

      // run stuff
      String command = this.commandLine;
      channel = session.openChannel("exec");
      ((ChannelExec) channel).setCommand(command);
      ((ChannelExec) channel).setErrStream(System.err);
      logger.info("Running: " + this.commandLine);
      channel.connect();

      inputStream = channel.getInputStream();
      // start reading the input from the executed commands on the shell
      byte[] tmp = new byte[1024];
      this.commandResult.delete(0, this.commandResult.length());
      while (maxLines-- > 0) {
        Thread.sleep(1000);
        while (inputStream.available() > 0) {
          int i = inputStream.read(tmp, 0, 1024);
          if (i < 0) {
            break;
          }
          String string = new String(tmp, 0, i);
          this.commandResult.append(string);
          logger.info("stdout: \n" + string);
        }
        if (channel.isClosed()) {
          this.exitStatus = channel.getExitStatus();
          logger.info("exit-status: " + this.exitStatus);
          break;
        }
      }

      channel.disconnect();
      session.disconnect();
    } catch (JSchException | IOException e) {
      logger.warn(e.getClass().getSimpleName() + " in SSHManager: " + e.getMessage());
      throw new SSHManagerException(this, e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          logger.warn(e);
        }
      }
      if (channel != null) {
        channel.disconnect();
      }
      if (session != null) {
        session.disconnect();
      }
    }

    return this;
//    MDC.put(KiteConstants.MDC_TAG_NAME, this.hostIpOrName);
//    Session session = null;
//    Channel channel = null;
//    InputStream inputStream = null;
//    try {
//      JSch jsch = new JSch();
//      jsch.addIdentity(this.keyFilePath);
//      if (this.hostIpOrName.contains(":")) {
//        StringTokenizer st = new StringTokenizer(this.hostIpOrName, ":");
//        session = jsch.getSession(this.username, st.nextToken(), Integer.parseInt(st.nextToken()));
//      } else {
//        session = jsch.getSession(this.username, this.hostIpOrName, 22);
//      }
//      if (this.password != null) {
//        session.setPassword(this.password);
//      }
//      session.connect();
//
//      // run stuff
//      String command = this.commandLine;
//      channel = session.openChannel("exec");
//      ((ChannelExec) channel).setCommand(command);
//      ((ChannelExec) channel).setErrStream(System.err);
//      if (count > 0) {
//        logger.debug(
//            "Running ("
//                + (this.index + 1)
//                + "/"
//                + this.count
//                + ")' on " + this.hostIpOrName + " : "
//                + this.commandLine);
//      } else {
//        logger.debug(
//            "Running the following command on " + this.hostIpOrName + " : " + this.commandLine);
//      }
//      channel.connect();
//
//      inputStream = channel.getInputStream();
//      // setStartTimestamp reading the input from the executed commands on the shell
//      this.maxLines = 10;
//      byte[] tmp = new byte[1024];
//      while (this.maxLines-- > 0) {
//        Thread.sleep(ONE_SECOND_INTERVAL);
//        while (inputStream.available() > 0) {
//          int l = inputStream.read(tmp, 0, 1024);
//          if (l < 0) {
//            break;
//          }
//          String string = new String(tmp, 0,l);
//          this.commandResult.append(string);
//          logger.info("stdout: \n" + string);
//        }
//        if (channel.isClosed()) {
//          this.exitStatus = channel.getExitStatus();
//          logger.info("exit-status: " + this.exitStatus);
//          break;
//        }
//      }
//
//      channel.disconnect();
//      session.disconnect();
//    } catch (JSchException | IOException e) {
//      logger.warn(e.getClass().getSimpleName() + " in SSHManager: " + e.getLocalizedMessage());
//      this.exitStatus = -1;
//       throw new SSHManagerException(this, e);
//    } finally {
//      if (inputStream != null) {
//        try {
//          inputStream.close();
//        } catch (IOException e) {
//          logger.warn(e);
//        }
//      }
//      if (channel != null) {
//        channel.disconnect();
//      }
//      if (session != null) {
//        session.disconnect();
//      }
//    }
//    return this;
  }

  /**
   * Retried call.
   *
   * @return the SSH manager
   * @throws Exception the exception
   */
  public SSHManager retriedCall() throws Exception {
    int tryCount = 1;
    do {
      try {
        this.sendCommand();
        if (this.exitStatus == 0) {
          logger.debug("exit-status: " + this.exitStatus);
          break;
        }
        return this;
      } catch (Exception e) {
        // Unsuccessful SSH session creation
        String hostIpOrName = this.hostIpOrName;
        if (e.getCause() instanceof SSHManagerException) {
          hostIpOrName = ((SSHManagerException) e.getCause()).getSSHManager().getHostIpOrName();
        }
        if (tryCount >= KiteConstants.MAX_SSH_TRIES) {
          logger.error(
              String.format("Already tried SSH for %s %d times(s). Giving up now!", hostIpOrName,
                  KiteConstants.MAX_SSH_TRIES));
          this.exitStatus = -1;
        } else {
          logger.warn(String.format("Will try SSH for %s %d more time(s)", hostIpOrName,
              KiteConstants.MAX_SSH_TRIES - tryCount));
        }
//        waitAround(KiteConstants.SSH_RETRY_WAIT /** tryCount/2*/);
      }
    } while (tryCount++ < KiteConstants.MAX_SSH_TRIES);

    throw new Exception("Unable to configure the host " + this.hostIpOrName + " exit-status: " + this.exitStatus);
  }


  /**
   * Command successful boolean.
   *
   * @return the boolean
   */
  public boolean commandSuccessful() {
    return this.exitStatus == 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#finalize()
   */
  @Override
  protected void finalize() throws Throwable {
    MDC.remove("tag");
  }

  /**
   * Gets the host ip or name.
   *
   * @return the host ip or name
   */
  public String getHostIpOrName() {
    return hostIpOrName;
  }

  public int getMaxLines() {
    return maxLines;
  }

  public void setMaxLines(int maxLines) {
    this.maxLines = maxLines;
  }

  public void setHostIpOrName(String hostIpOrName) {
    this.hostIpOrName = hostIpOrName;
  }

  public void setCommandLine(String commandLine) {
    this.commandLine = commandLine;
  }

  public StringBuilder getCommandResult() {
    return commandResult;
  }

  public int getExitStatus() {
    return exitStatus;
  }
}
