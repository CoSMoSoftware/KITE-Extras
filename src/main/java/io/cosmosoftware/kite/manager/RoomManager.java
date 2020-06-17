/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.manager;

import io.cosmosoftware.kite.entities.MeetingStatus;
import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.util.TestUtils;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RoomManager provides utility functions to manage video conf test cases with k rooms and n viewer
 * per room
 * <p>
 * It is using existing parameters "increment" as the number of maxCapacity and "count"/"increment"
 * to get the number of rooms to be created.
 */
public class RoomManager extends ConcurrentHashMap<String, MeetingStatus> {

  private static final KiteLogger logger = KiteLogger
      .getLogger(io.cosmosoftware.kite.manager.RoomManager.class.getName());
  private final String roomUrl;

  private final int maxCapacity;
  private int userCount = 0;
  private int roomIndex = 0;

  //this is for the case where the roomURL is created after the call is initiated (e.g. Google Hangouts)
  private HashMap<Integer,String> dynamicUrls = new HashMap<>();

  public RoomManager(String roomUrl, int maxCapacity) {
    this.roomUrl = roomUrl;
    this.maxCapacity = maxCapacity;
    logger.info("new RoomManager(" + maxCapacity + ") for " + roomUrl);
  }

  /**
   * Gets the base url provided in the payload
   * @return the base url
   */
  public synchronized String getRoomUrl() {
    return roomUrl;
  }


  /**
   * Gets the number of users per room
   * @return the number of users per room
   */
  public synchronized int getMaxCapacity() {
    return maxCapacity;
  }


}
