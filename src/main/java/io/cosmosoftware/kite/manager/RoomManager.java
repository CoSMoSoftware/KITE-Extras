/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.manager;

import io.cosmosoftware.kite.entities.MeetingStatus;
import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.util.TestUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * RoomManager provides utility functions to manage video conf test cases with k rooms and n viewer
 * per room
 * <p>
 * It is using existing parameters "increment" as the number of usersPerRoom and "count"/"increment"
 * to get the number of rooms to be created.
 */
public class RoomManager extends ConcurrentHashMap<String, MeetingStatus> {

  private static final KiteLogger logger = KiteLogger.getLogger(RoomManager.class.getName());
  private final String baseURL;
  private final int usersPerRoom;
  private int roomId = 0;
  private int roomIndex = 0;
  private String[] roomNames = null;
  private boolean loopRooms;


  public RoomManager(String baseURL, int usersPerRoom, boolean loop) {
    this.baseURL = baseURL;
    this.usersPerRoom = usersPerRoom;
    this.loopRooms = loop;
    logger.info("new RoomManager(" + usersPerRoom + ") for " + baseURL);
  }


  private synchronized String getHubId(String hubIpOrDns) {
    if (hubIpOrDns == null) {
      return "";
    }
    String hubId = "";
    if (hubIpOrDns.indexOf(".") > 0) {
      hubId = hubIpOrDns.substring(0, hubIpOrDns.indexOf("."));
    }
    // Return ip without special characters to be used in Jitsi url
    String[] splittedHubIp = hubId.split("-");
    String rawHubId = splittedHubIp[0];
    for (int i = 1; i < splittedHubIp.length; i++) {
      rawHubId += TestUtils.idToString(Integer.parseInt(splittedHubIp[i]));
    }
    return rawHubId;
  }

  /**
   * Gets the meeting room name from the roomNames array.
   *
   * @param i the index of the room name in the array
   * @return the room name correspoding to index i in the array
   * @throws KiteTestException if i > roomNames.length - 1
   */
  public synchronized String getRoomName(int i) throws KiteTestException {
    if (i > roomNames.length - 1 && !loopRooms) {
      logger.error(
          "Error: only " + roomNames.length + " rooms in the room list, unable to create the "
              + i + "th room. Please check the config file.");
      throw new KiteTestException(
          "Unable to create the new room, there are not enough rooms provided in the room list.", Status.BROKEN);
    }
    return roomNames[i % roomNames.length];
  }

  /**
   * Gets the meeting room URL for the load testing.
   *
   * @param hubIpOrDns the IP or DNS of the Hub
   * @return the meeting room URL for the load testing.
   * @throws KiteTestException the exception
   */
  public synchronized String getRoomUrl(String hubIpOrDns) throws KiteTestException {
    boolean newMeeting = roomIndex == 0;
    if (usersPerRoom == 0) {
      newMeeting = true;
    } else if (roomIndex > 0 && roomIndex % usersPerRoom == 0) {
      roomId++;
      newMeeting = true;
    }
    roomIndex++;
    String meetingId;
    String result;
    String roomUrl;
    if (baseURL.contains("roomId")) {
      roomUrl = baseURL.endsWith("=") ? baseURL : baseURL + "=";
    } else if (baseURL.endsWith("html")) {
      return baseURL;
    } else {
      roomUrl = baseURL.endsWith("/") ? baseURL : baseURL + "/";
    }
    if (roomNames != null && roomNames.length > 0) {
      meetingId = getRoomName(roomId);
      result = roomUrl + meetingId;
    } else {
      meetingId = getHubId(hubIpOrDns) + getRandomRoomId(1000000);
      return roomUrl + meetingId;
    }
    if (newMeeting) {
      put(meetingId, new MeetingStatus(meetingId));
    }
    return result;
  }

  /**
   * Gets the meeting room URL when running the test locally (on open-source KITE)
   *
   * @return the meeting room URL
   * @throws KiteTestException the exception
   */
  public synchronized String getRoomUrl() throws KiteTestException {
    return getRoomUrl(null);
  }

  /**
   * Gets the base url provided in the payload
   * @return the base url
   */
  public synchronized String getBaseURL() {
    return baseURL;
  }

  /**
   * Gets the array of room name
   * @return the room names
   */
  public String[] getRoomNames() {
    return roomNames;
  }

  /**
   * Room list provided boolean.
   *
   * @return true if a room list was provided (this.roomNames != null)
   */
  public boolean roomListProvided() {
    return this.roomNames != null && this.roomNames.length > 0;
  }

  /**
   * Sets the roomNames array.
   *
   * @param roomNames a String[] containing the list of room names.
   */
  public void setRoomNames(String[] roomNames) {
    this.roomNames = roomNames;
  }

  private String getRandomRoomId(int roomIdLen) {
    return Integer.toString((int) Math.floor(Math.random() * Math.pow(10, roomIdLen)));
  }

}
