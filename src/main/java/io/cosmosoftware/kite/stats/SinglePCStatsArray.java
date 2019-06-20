/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.stats;

import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.util.ReportUtils;

import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Single pc stats array.
 */
public class SinglePCStatsArray extends BasePCStatsArray {
  
  private static final KiteLogger logger = KiteLogger.getLogger(SinglePCStatsArray.class.getName());
  
  public Map<String, String> getAVAvgBitrate(String mediaType) {
    Map<String, String> videosAvgBitrateMap = new HashMap<>();
    if (this.size() <= 1) {
      return videosAvgBitrateMap;
    }
    JsonObject lastStatObject = this.getLastObject();
    if (lastStatObject != null && lastStatObject.containsKey("localStats")) {
      JsonObject localStats = lastStatObject.getJsonObject("localStats");
      for (String itemKey : localStats.keySet()) {
        try {
          JsonObject jsonObj = localStats.getJsonObject(itemKey);
          boolean sentVideo = isSendAV(jsonObj, mediaType);
          if (isRecvAV(jsonObj, mediaType) || sentVideo) {
            JsonObject lastItem = jsonObj;
            JsonObject firstItem = this.getFirstAppearanceOf(itemKey, mediaType);
            long averageBitrate = computeAvgBitrate(firstItem, lastItem, sentVideo);
            String value = (averageBitrate > -1) ? "" + averageBitrate : "";
            videosAvgBitrateMap.put(itemKey, value);
          }
        } catch (Exception e) {
          logger.error(ReportUtils.getStackTrace(e));
        }
      }
    }
    return videosAvgBitrateMap;
  }
  
  public Map<String, String> getAVPacketLoss(String mediaType) {
    JsonObject lastStatObject = this.getLastObject();
    Map<String, String> packetLossMap = new HashMap<>();
    if (lastStatObject != null && lastStatObject.containsKey("localStats")) {
      JsonObject localStats = lastStatObject.getJsonObject("localStats");
      for (String itemKey : localStats.keySet()) {
        JsonObject jsonObject = localStats.getJsonObject(itemKey);
        boolean sent = isSendAV(jsonObject, mediaType);
        if (localStats.containsKey(itemKey)
          && (isRecvAV(jsonObject, mediaType)
          || sent)) {
          double loss = -1.0;
          try {
            double packetsLost = (double) Long.parseLong(jsonObject.getString("packetsLost"));
            double packetsCount = (double) Long.parseLong(jsonObject.getString(
              sent ? "packetsSent" : "packetsReceived"));
            loss = (100 * packetsLost) / (packetsCount + packetsLost);
          } catch (NullPointerException e) {
            logger.warn("getAVPacketLoss(" + mediaType + ") " + (sent ? "sent" : "recv")
              + " packetsLost not present in " + itemKey);
          }
          packetLossMap.put(itemKey, df.format(loss));
        }
      }
    }
    return packetLossMap;
  }
  
  /**
   * Get the list of googJitterReceived values as Map
   * Map<String, String>
   *
   * @return a Map object with the googJitterReceived
   */
  public Map<String, String> getAudiosJitter() {
    JsonObject lastStatObject = this.getLastObject();
    Map<String, String> jitterMap = new HashMap<>();
    if (lastStatObject != null && lastStatObject.containsKey("localStats")) {
      JsonObject localStats = lastStatObject.getJsonObject("localStats");
      for (String itemKey : localStats.keySet()) {
        JsonObject jsonObject = localStats.getJsonObject(itemKey);
        if (localStats.containsKey(itemKey) && isAV(jsonObject, AUDIO)) {
          String jitter = "-1";
          try {
            if (isRecvAV(jsonObject, AUDIO)) {
              jitter = jsonObject.getString("googJitterReceived");
            }
            if (isSendAV(jsonObject, AUDIO)) {
              jitter = "NA";
            }
          } catch (NullPointerException e) {
            logger.error("Error in getAudiosJitter(): "
              + " googJitterReceived not present in " + itemKey);
          }
          if (!jitterMap.containsKey(itemKey) || "-1".equals(jitterMap.get(itemKey))) {
            jitterMap.put(itemKey, jitter);
          }
        }
      }
    }
    return jitterMap;
  }
  
  private JsonObject getFirstAppearanceOf(String itemKey, String mediaType) {
    for (int i = 0; i < this.size(); i++) {
      if (this.get(i).containsKey("localStats")) {
        JsonObject localStats = this.get(i).getJsonObject("localStats");
        if (localStats != null && localStats.containsKey(itemKey)
          && (isRecvAV(localStats.getJsonObject(itemKey), mediaType)
          || isSendAV(localStats.getJsonObject(itemKey), mediaType))) {
          return localStats.getJsonObject(itemKey);
        }
      }
    }
    return null;
  }
  
  /**
   * Get the list of googFrameRateReceived/googFrameRateSent values as Map
   * Map<String, String>
   *
   * @return a Map object with the googFrameRateReceived
   */
  public Map<String, String> getFrameRate() {
    JsonObject lastStatObject = this.getLastObject();
    Map<String, String> resultMap = new HashMap<>();
    if (lastStatObject != null && lastStatObject.containsKey("localStats")) {
      JsonObject localStats = lastStatObject.getJsonObject("localStats");
      for (String itemKey : localStats.keySet()) {
        JsonObject jsonObject = localStats.getJsonObject(itemKey);
        if (localStats.containsKey(itemKey) && isAV(jsonObject, VIDEO)) {
          String frameRate = "-1";
          try {
            if (isRecvAV(jsonObject, VIDEO)) {
              frameRate = jsonObject.getString("googFrameRateReceived");
            }
            if (isSendAV(jsonObject, VIDEO)) {
              frameRate = jsonObject.getString("googFrameRateSent");
            }
          } catch (NullPointerException e) {
            logger.error("Error in getFrameRate(): "
              + " googFrameRateReceived/googFrameRateSent not present in " + itemKey);
          }
          if (!resultMap.containsKey(itemKey) || "-1".equals(resultMap.get(itemKey))) {
            resultMap.put(itemKey, frameRate);
          }
        }
      }
    }
    return resultMap;
  }
  
  /**
   * Get the sender's googRtt from the PC's
   * ssrc_4030852498_send object with mediaType = "video"
   *
   * @return the sender's video googRtt value (as a String)
   */
  public String getSentVideoRtt() {
    JsonObject lastStatObject = this.getLastObject();
    if (lastStatObject != null && lastStatObject.containsKey("localStats")) {
      JsonObject localStats = lastStatObject.getJsonObject("localStats");
      for (String itemKey : localStats.keySet()) {
        if (isSendAV(localStats.getJsonObject(itemKey), VIDEO)) {
          return localStats.getJsonObject(itemKey).getString("googRtt");
        }
      }
    }
    return "";
  }
  
  public Map<String, String> getTotalAVBytes(String mediaType) {
    JsonObject lastStatObject = this.getLastObject();
    Map<String, String> totalVideosBytesMap = new HashMap<>();
    if (lastStatObject != null && lastStatObject.containsKey("localStats")) {
      JsonObject localStats = lastStatObject.getJsonObject("localStats");
      for (String itemKey : localStats.keySet()) {
        if (isRecvAV(localStats.getJsonObject(itemKey), mediaType)) {
          totalVideosBytesMap.put(
            itemKey, localStats.getJsonObject(itemKey).getString("bytesReceived"));
        }
        if (isSendAV(localStats.getJsonObject(itemKey), mediaType)) {
          totalVideosBytesMap.put(
            itemKey, localStats.getJsonObject(itemKey).getString("bytesSent"));
        }
      }
    }
    return totalVideosBytesMap;
  }
}
