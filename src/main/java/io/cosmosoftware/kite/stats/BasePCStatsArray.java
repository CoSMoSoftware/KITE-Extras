/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */
package io.cosmosoftware.kite.stats;

import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.util.ReportUtils;

import javax.json.JsonObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * RTCPeerConnectionStats, with attributes dataChannelsOpened, dataChannelsClosed
 */
public abstract class BasePCStatsArray extends ArrayList<JsonObject> {

  /**
   * The constant AUDIO.
   */
  public static final String AUDIO = "audio";
  /**
   * The constant VIDEO.
   */
  public static final String VIDEO = "video";
  /**
   * The constant df.
   */
  protected static final DecimalFormat df = new DecimalFormat("#0.0000");
  private static final KiteLogger logger = KiteLogger.getLogger(BasePCStatsArray.class.getName());

  /**
   * Compute avg bitrate long.
   *
   * @param firstItem the first item
   * @param lastItem the last item
   * @param sentVideo the sent video
   * @return the long
   */
  protected long computeAvgBitrate(
      JsonObject firstItem, JsonObject lastItem, boolean sentVideo) {
    long bitrate = -1;
    try {
      long startTS = Long.parseLong(firstItem.getString("timestamp"));
      long endTS = Long.parseLong(lastItem.getString("timestamp"));
      long duration = endTS - startTS;
      if (duration > 0) {
        long startBytes =
            Long.parseLong(firstItem.getString(sentVideo ? "bytesSent" : "bytesReceived"));
        long endBytes =
            Long.parseLong(lastItem.getString(sentVideo ? "bytesSent" : "bytesReceived"));
        bitrate = ((endBytes - startBytes) * 8000) / duration;
      }
    } catch (Exception e) {
      logger.error("Error in computeAvgBitrate(): " + ReportUtils.getStackTrace(e));
//      logger.error("Error in computeAvgBitrate(): " + e.getLocalizedMessage());
    }
    return bitrate;
  }

  /**
   * Gets av avg bitrate.
   *
   * @param mediaType the media type
   * @return the av avg bitrate
   */
  public abstract Map<String, String> getAVAvgBitrate(String mediaType);

  /**
   * Gets av packet loss.
   *
   * @param mediaType the media type
   * @return the av packet loss
   */
  public abstract Map<String, String> getAVPacketLoss(String mediaType);

  /**
   * Gets audios jitter.
   *
   * @return the audios jitter
   */
  public abstract Map<String, String> getAudiosJitter();

  /**
   * Gets frame rate.
   *
   * @return the frame rate
   */
  public abstract Map<String, String> getFrameRate();

  /**
   * Gets last object.
   *
   * @return the last object
   */
  protected JsonObject getLastObject() {
    return size() > 1 ? get(size() - 1) : null;
  }

  /**
   * Get the sender's googRtt from the PC's ssrc_4030852498_send object with mediaType = "video"
   *
   * @return the sender's video googRtt value (as a String)
   */
  public abstract String getSentVideoRtt();

  /**
   * Gets total av bytes.
   *
   * @param mediaType the media type
   * @return the total av bytes
   */
  public abstract Map<String, String> getTotalAVBytes(String mediaType);

  /**
   * Is av boolean.
   *
   * @param statObject the stat object
   * @param mediaType the media type
   * @return the boolean
   */
  protected boolean isAV(JsonObject statObject, String mediaType) {
    return isSendAV(statObject, mediaType) || isRecvAV(statObject, mediaType);
  }

  /**
   * Is recv av boolean.
   *
   * @param statObject the stat object
   * @param mediaType the media type
   * @return the boolean
   */
  protected boolean isRecvAV(JsonObject statObject, String mediaType) {
    return (statObject.getString("id").contains("ssrc_")
        && statObject.getString("id").contains("_recv")
        && statObject.getString("mediaType").equals(mediaType)
        && !statObject.getString("googTrackId").contains("fake-unified-plan")
        && !statObject.getString("googCodecName").equals(""));
  }

  /**
   * Is send av boolean.
   *
   * @param statObject the stat object
   * @param mediaType the media type
   * @return the boolean
   */
  protected boolean isSendAV(JsonObject statObject, String mediaType) {
    return (statObject.getString("id").contains("ssrc_")
        && statObject.getString("id").contains("_send")
        && statObject.getString("mediaType").equals(mediaType)
        && !statObject.getString("googTrackId").contains("fake-unified-plan")
        && !statObject.getString("googCodecName").equals(""));
  }
}
