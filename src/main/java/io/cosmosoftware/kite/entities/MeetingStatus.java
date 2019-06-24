/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.entities;

import io.cosmosoftware.kite.report.KiteLogger;

/**
 * The type Meeting status.
 */
public class MeetingStatus {

  private final KiteLogger logger = KiteLogger.getLogger(this.getClass().getName());

  private final String meetingID;
  private long lastCheck = System.currentTimeMillis();
  private boolean meetingEnded = false;


  /**
   * Constructor
   *
   * @param id the meeting ID
   */
  public MeetingStatus(String id) {
    this.meetingID = id;
  }

  /**
   * The meeting will end with a probability of 1/chanceOfMeetingEnded. This check is done every
   * checkStatusPeriod seconds.
   *
   * @param chanceOfMeetingEnded denomitator of the probability. Probability of meeting ended
   * = 1 / chanceOfMeetingEnded
   * @param checkStatusPeriod how often to check if the meeting should be ended (in seconds)
   * @return boolean boolean
   */
  public synchronized boolean meetingEnded(int chanceOfMeetingEnded, int checkStatusPeriod) {
    long now = System.currentTimeMillis();
    if (now - lastCheck < checkStatusPeriod * 1000) {
      return meetingEnded;
    }
    lastCheck = now;
    int prob = (int) (Math.random() * chanceOfMeetingEnded) + 1;
    meetingEnded = (prob == chanceOfMeetingEnded);
    if (meetingEnded) {
      logger.info("The meeting " + meetingID + " has ended, all clients will refresh.");
    }
    return meetingEnded;
  }

}
