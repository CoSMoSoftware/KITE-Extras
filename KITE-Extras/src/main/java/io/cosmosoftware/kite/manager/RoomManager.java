/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.manager;

import io.cosmosoftware.kite.entities.MeetingStatus;
import io.cosmosoftware.kite.util.TestUtils;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * RoomManager provides utility functions to manage video conf test cases with k rooms and n viewer per room
 * <p>
 * It is using existing parameters "increment" as the number of usersPerRoom and "count"/"increment" to get
 * the number of rooms to be created.
 */
public class RoomManager extends ConcurrentHashMap<String, MeetingStatus> {
  
  private static final Logger logger = Logger.getLogger(RoomManager.class.getName());
  private static RoomManager roomManager = null;
  private final String baseURL;
  private final int usersPerRoom;
  private int roomId = 0;
  private int roomIndex = 0;
  private String[] roomNames = null;
  private boolean loopRooms;


  private RoomManager(String baseURL, int usersPerRoom, boolean loop) {
    this.baseURL = baseURL;
    this.usersPerRoom = usersPerRoom;
    this.loopRooms = loop;
    logger.info("new RoomManager(" + usersPerRoom + ") for " + baseURL);
  }
  
  private String getHubId(String hubIpOrDns) {
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
   * Gets instance.
   *
   * @return the instance
   * @throws Exception the exception
   */
  public static RoomManager getInstance() throws Exception {
    if (roomManager == null) {
      throw new Exception("RoomManager has not been instanciated yet, please call getInstance(String baseURL, int numberOfRooms, int usersPerRoom) first.");
    }
    return roomManager;
  }
  
  /**
   * Gets instance.
   *
   * @param baseURL      the base url
   * @param usersPerRoom the users per room
   *
   * @return the instance
   */
  public static RoomManager getInstance(String baseURL, int usersPerRoom) {
    return  RoomManager.getInstance(baseURL, usersPerRoom, false);
  }


  /**
   * Gets instance.
   *
   * @param baseURL      the base url
   * @param usersPerRoom the users per room
   * @param loop whether to start from first room when all the rooms have been used.
   *
   * @return the instance
   */
  public static RoomManager getInstance(String baseURL, int usersPerRoom, boolean loop) {
    if (roomManager == null) {
      roomManager = new RoomManager(baseURL, usersPerRoom, loop);
    }
    return roomManager;
  }
  
  /**
   * Gets the meeting room name from the roomNames array.
   *
   * @param i the index of the room name in the array
   *
   * @return the room name correspoding to index i in the array
   * @throws Exception if i > roomNames.length - 1
   */
  public String getRoomName(int i) throws Exception {
    if (i > roomNames.length - 1 && !loopRooms) {
      logger.error("Error: only " + roomNames.length + " rooms in the room list, unable to create the "
        + i + "th room. Please check the config file.");
      throw new Exception("Unable to create the new room, there are not enough rooms provided in the room list.");
    }
    return roomNames[i%roomNames.length ];
  }
  
  /**
   * Gets the meeting room URL for the load testing.
   *
   * @param hubIpOrDns the IP or DNS of the Hub
   *
   * @return the meeting room URL for the load testing.
   * @throws Exception the exception
   */
  public synchronized String getRoomUrl(String hubIpOrDns) throws Exception {
    boolean newMeeting = roomIndex == 0;
    if(usersPerRoom ==0){
      newMeeting = true;
    }
    else if (roomIndex > 0 && roomIndex % usersPerRoom == 0) {
      roomId++;
      newMeeting = true;
    }
    roomIndex++;
    
    String meetingId;
    String result;
    if (roomNames != null && roomNames.length > 0) {
      meetingId = getRoomName(roomId);
      if(baseURL.contains("roomId")){
        result = baseURL.endsWith("=")? baseURL + meetingId : baseURL + "=" + meetingId;
      }
      else {
        result = baseURL.endsWith("/")? baseURL +meetingId : baseURL + "/" + meetingId;
      }
    } else {
      meetingId = getHubId(hubIpOrDns) + getRandomRoomId(1000000);
      return this.baseURL + meetingId;
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
   * @throws Exception the exception
   */
  public synchronized String getRoomUrl() throws Exception {
    return getRoomUrl(null);
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

  public String getRandomRoomId(int roomIdLen){
    return Integer.toString((int)Math.floor(Math.random() * Math.pow(10,roomIdLen)));
  }
  
  
}
