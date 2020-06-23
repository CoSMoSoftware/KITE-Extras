/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.manager;

import io.cosmosoftware.kite.entities.MeetingStatus;
import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.util.TestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
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
  private final int maxCapacity;
  private String roomUrl;
  private String randomizedId = randomizeId();
  private int userCount = 0;
  private int roomIndex = 0;
  private List<String> preconfiguredRooms = new ArrayList<>();

  //this is for the case where the roomURL is created after the call is initiated (e.g. Google Hangouts)
  private HashMap<Integer,String> dynamicUrls = new HashMap<>();

  public RoomManager(String roomUrl, int maxCapacity) {
    this.roomUrl = roomUrl;
    this.maxCapacity = maxCapacity;
    logger.info("new RoomManager(" + maxCapacity + " max per room) for " + roomUrl);
    logger.info("Please make sure that you have enough room for all users.");
    logger.info("getRoomUrl(random) function, with random value = true/false, will give you the base url with or without " +
            "a randomized ID.");
  }

  /**
   * Gets the base url provided in the payload
   * @return the base url
   */
  public synchronized String getRoomUrl(boolean random) {
    if (userCount == maxCapacity) {
      logger.warn("User count is at max capacity for the current room, " +
              "if you are putting more users than expected, please reconfigure. " +
              "Either use random room id or preconfigured room list " +
              "(ignore this warning if it's already the case).");
      userCount = 0;
      randomizedId = randomizeId();
    }
    userCount ++;
    if (random) {
      return roomUrl + randomizedId;
    }
    return roomUrl;
  }

  public synchronized String getRoomUrl() {
    return getRoomUrl(false);
  }

  /**
   * Gets the number of users per room
   * @return the number of users per room
   */
  public synchronized int getMaxCapacity() {
    return maxCapacity;
  }

  private String randomizeId() {
    return  UUID.randomUUID().toString();
  }

  public synchronized String getPreconfigureRooUrl() throws KiteTestException {
    if (userCount == maxCapacity) {
      logger.warn("User count is at max capacity for the current room, " +
              "if you are putting more users than expected, please reconfigure. " +
              "Either use random room id or preconfigured room list " +
              "(ignore this warning if it's already the case).");
      userCount = 0;
      roomIndex ++;

    }
    userCount ++;
    if (roomIndex > preconfiguredRooms.size() - 1) {
      throw new KiteTestException("The room manager has run out of preconfigured room to return. " +
              "Please make sure you have enough rooms for all users.", Status.BROKEN);
    } else {
      return this.roomUrl + preconfiguredRooms.get(roomIndex);
    }
  }

  public synchronized void setPreconfiguredRooms(List<String> preconfiguredRooms) {
    this.preconfiguredRooms = preconfiguredRooms;
    logger.info("Preconfigured room list found: " + this.preconfiguredRooms);
    logger.info("To use these room, please call getPreconfiguredRoomUrl instead of get getRoomUrl function");
  }

  public synchronized List<String> getPreconfiguredRooms() {
    return preconfiguredRooms;
  }

  public void setRoomUrl(String roomUrl) {
    this.roomUrl = roomUrl;
  }
}
