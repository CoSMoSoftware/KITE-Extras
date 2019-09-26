/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.action;

import javax.json.JsonObject;

/**
 * The type Js action script.
 */
public class JSActionScript {

  /**
   * Script to click an element by executing JS code
   *
   * @param selector selector type
   * @param value selector value
   * @return the string
   */
  public static String clickByJsScript(String selector, String value) {
    return "document.getElementBy" + selector + "('" + value + "').click()";
  }

  /**
   * Script to check canvas pixel sum.
   *
   * @param index index of the video on the list of video elements.
   * @return the canvasCheck as string.
   */
  public static String getCanvasFrameValueSumByIndexScript(int index) {
    return "function getSum(total, num) {"
        + "    return total + num;"
        + "};"
        + "var canvas = document.createElement('canvas');"
        + "var ctx = canvas.getContext('2d');"
        + "var canvass = document.getElementsByTagName('canvas');"
        + "var canvas = canvass["
        + index
        + "];"
        + "if(canvas){"
        + "ctx.drawImage(canvas,0,0,canvas.height-1,canvas.width-1);"
        + "var imageData = ctx.getImageData(0,0,canvas.height-1,canvas.width-1).data;"
        + "var sum = imageData.reduce(getSum);"
        + "if (sum===255*(Math.pow(canvas.height-1,(canvas.width-1)*(canvas.width-1))))"
        + "   return 0;"
        + "return sum;"
        + "} else {"
        + "return 0 "
        + "}";
  }

  /**
   * Returns the test's getSDPOfferScript to retrieve simulcast.pc.localDescription.sdp or
   * simulcast.pc.remoteDescription.sdp. If it doesn't exist then the method returns 'unknown'.
   *
   * @param local boolean
   * @return the getSDPOfferScript as string.
   */
  public static String getSDPOfferScript(boolean local) {
    if (local) {
      return "var SDP;"
          + "try {SDP = pc.localDescription.sdp;} catch (exception) {} "
          + "if (SDP) {return SDP;} else {return 'unknown';}";
    } else {
      return "var SDP;"
          + "try {SDP = pc.remoteDescription.sdp;} catch (exception) {} "
          + "if (SDP) {return SDP;} else {return 'unknown';}";
    }
  }

  /**
   * Returns the test's canvasCheck to check if the video identified by the given query selector is
   * blank, and if it changes overtime.
   *
   * @param cssSelection the css selection
   * @return the canvasCheck as string.
   */
  public static String getVideoFrameValueSumByCssSelectorScript(String cssSelection) {
    return "function getSum(total, num) {"
        + "    return total + num;"
        + "};"
        + "var canvas = document.createElement('canvas');"
        + "var ctx = canvas.getContext('2d');"
        + "var video = document.querySelector('"
        + cssSelection
        + "');"
        + "ctx.drawImage(video,0,0,video.videoHeight-1,video.videoWidth-1);"
        + "var imageData = ctx.getImageData(0,0,video.videoHeight-1,video.videoWidth-1).data;"
        + "var sum = imageData.reduce(getSum);"
        + "if (sum===255*(Math.pow(video.videoHeight-1,(video.videoWidth-1)*(video.videoWidth-1))))"
        + "   return 0;"
        + "return sum;";
  }

  /**
   * Returns the test's canvasCheck to check if the video identified by the given id is blank, and
   * if it changes overtime.
   *
   * @param videoId the video id
   * @return the canvasCheck as string.
   */
  public static String getVideoFrameValueSumByIdScript(String videoId) {
    return "function getSum(total, num) {"
        + "    return total + num;"
        + "};"
        + "var canvas = document.createElement('canvas');"
        + "var ctx = canvas.getContext('2d');"
        + "var video = document.getElementById('"
        + videoId
        + "');"
        + "if(video){"
        + "ctx.drawImage(video,0,0,video.videoHeight-1,video.videoWidth-1);"
        + "var imageData = ctx.getImageData(0,0,video.videoHeight-1,video.videoWidth-1).data;"
        + "var sum = imageData.reduce(getSum);"
        + "if (sum===255*(Math.pow(video.videoHeight-1,(video.videoWidth-1)*(video.videoWidth-1)))) {"
        + "   return 0;}"
        + "return sum;"
        + "} else {"
        + "return 0 "
        + "}";
  }

  /**
   * Returns the test's canvasCheck to check if the video is blank.
   *
   * @param index index of the video on the list of video elements.
   * @return the canvasCheck as string.
   */
  public static String getVideoFrameValueSumByIndexScript(int index) {
    return "function getSum(total, num) {"
        + "    return total + num;"
        + "};"
        + "var canvas = document.createElement('canvas');"
        + "var ctx = canvas.getContext('2d');"
        + "var videos = document.getElementsByTagName('video');"
        + "var video = videos["
        + index
        + "];"
        + "if(video){"
        + "ctx.drawImage(video,0,0,video.videoHeight-1,video.videoWidth-1);"
        + "var imageData = ctx.getImageData(0,0,video.videoHeight-1,video.videoWidth-1).data;"
        + "var sum = imageData.reduce(getSum);"
        + "if (sum===255*(Math.pow(video.videoHeight-1,(video.videoWidth-1)*(video.videoWidth-1))))"
        + "   return 0;"
        + "return sum;"
        + "} else {"
        + "return 0 "
        + "}";
  }

  public static String getVideoFrameValueSumScript(Object indexOrId) {
    if (indexOrId instanceof String) {
      return getVideoFrameValueSumByIdScript((String)indexOrId);
    } else {
      return getVideoFrameValueSumByIndexScript((int)indexOrId);
    }
  }


  /**
   * Script to input a value to an element by executing JS code
   *
   * @param selector selector type
   * @param value selector value
   * @return the string
   */
  public static String inputByJsScript(String selector, String value) {
    return "document.getElementBy" + selector + "('" + value + "').value = '" + value + "'";
  }

  /**
   * Script to creates a Media recorder object on the video's source object, records for a given
   * duration and sends back the blob to a server to reconstruct the recorded video.
   *
   * @param videoIndex video index in page's video array
   * @param recordingDurationInMilisecond duration to record
   * @param details Json object containing details about the video file (name, type, ..)
   * @param callbackUrl server url to send video back to
   * @return the string
   */
  public static String recordVideoStreamScript(
      int videoIndex,
      int recordingDurationInMilisecond,
      JsonObject details,
      String callbackUrl) {
    String queryString = "";
    for (String key : details.keySet()) {
      queryString += "&" + key + "=" + details.getString(key);
    }
    return
        "var kite_videos = document.getElementsByTagName('video');"
            + "var kite_video = kite_videos["
            + videoIndex
            + "];"
            + "var title = document.title;"
            + "var kite_mediaRecorder = new MediaRecorder(kite_video.srcObject);"
            + "kite_mediaRecorder.ondataavailable ="
            + "function (event) {"
            + "   if (event.data && event.data.size > 0) {"
            + "      var httpRequest = new XMLHttpRequest();"
            + "      httpRequest.open('POST','"
            + callbackUrl
            + "?test=' + title + '"
            + queryString
            + "', true );"
            + "      httpRequest.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');"
            + "      httpRequest.send(event.data);"
            + "      kite_mediaRecorder.ondataavailable = null;"
            + "      var para = document.createElement('p');"
            + "      para.id = 'videoRecorded'+"
            + videoIndex
            + ";"
            + "      if(document.body != null){ "
            + "         setTimeout(function(){document.body.appendChild(para);}, "
            + recordingDurationInMilisecond
            + 500
            + "); "
            + "      }"
            + "   }"
            + "};"
            + "kite_mediaRecorder.setStartTimestamp("
            + recordingDurationInMilisecond
            + ");"
            + "setTimeout(function(){kite_mediaRecorder.finish();}, "
            + recordingDurationInMilisecond
            + ");";
  }
}
