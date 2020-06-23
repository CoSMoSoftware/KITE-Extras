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


  public static String getStatsSdkString(
      String logstashUrl,
      String pc,
      String testName,
      String userNameCommand,
      String roomNameCommand,
      int statsPublishingInterval
  ) {
    return "function GetStats() {"
        + "this.receiverUrl = '';"
        + "this.userId = '';"
        + "this.roomId = '';"
//        + "this.sfu = '';"
        + "this.pcObject = null;"
        + "this.alwaysSendEverything = true;"
        + "this.lastStatsObjValues = {};"
        + "this.publishing = null;"
        + "this.testName = '';"
        + "}"
        + ""
        + "GetStats.prototype.init = function (receiverUrl, userId, roomId, pcObject, testName, alwaysSendEverything = true) {"
        + "this.receiverUrl = receiverUrl;"
        + "this.userId = userId;"
        + "this.roomId = roomId;"
        + "this.pcObject = pcObject;"
        + "this.testName = testName;"
        + "this.alwaysSendEverything = alwaysSendEverything;"
        + "};"
        + ""
        + "GetStats.prototype.startPublishing = function (interval) {"
        + "let publishingFunction = function () {"
        + "this.getStatsValues().then(statsObj => this.publish(statsObj)).then(resp => console.log(resp)).catch(err => console.log('Error in publishing getStats data: ', err))"
        + "};"
        + "this.publishing = setInterval(publishingFunction.bind(this), interval);"
        + "};"
        + ""
        + "GetStats.prototype.stopPublishing = function () {"
        + "console.log('Stop publishing for: ', this.pcObject);"
        + "clearInterval(this.publishing);"
        + "};"
        + ""
        + "GetStats.prototype.getStatsValues = async function () {"
        + "return this.pcObject.getStats().then((data) => {"
        + "let statsObj = {};"
        + "compareValueOf = this.compareValueOf.bind(this);"
        + "data.forEach(res => {"
        + "if (!this.alwaysSendEverything) {"
        + "const newValue = this.compareValueOf(res);"
        + "if (newValue) statsObj[res.id] = res;"
        + "} else {"
        + "if (res.type == 'candidate-pair') {"
        + "this.localid = res.localCandidateId;"
        + "this.remoteid = res.remoteCandidateId;"
        + "}"
        + "if (res.type != 'certificate') {"
        + "if (res.type == 'inbound-rtp' && !res.codecId) {"
        + "} else {"
        + "statsObj[res.id] = res;"
        + "}"
        + "}"
        + "}"
        + "});"
        + "return statsObj;"
        + "}).catch();"
        + "};"
        + ""
        + "GetStats.prototype.compareValueOf = function (item) {    "
        + "compareItem = cloneObject(item);"
        + "delete compareItem.timestamp;"
        + "if (compareItem.type != 'local-candidate' && compareItem.type != 'remote-candidate') {"
        + "if (JSON.stringify(compareItem) === JSON.stringify(this.lastStatsObjValues[compareItem.id])) {"
        + "return false;"
        + "}"
        + "}"
        + "this.lastStatsObjValues[compareItem.id] = cloneObject(compareItem);"
        + "return true;"
        + "};"
        + ""
        + "function cloneObject(obj) {"
        + "var clone = {};"
        + "for (var i in obj) {"
        + "if (obj[i] != null && typeof (obj[i]) == 'object') clone[i] = cloneObject(obj[i]); else clone[i] = obj[i];"
        + "}"
        + "return clone;"
        + "}"
        + ""
        + "GetStats.prototype.publish = function (statsObj) {"
        + "body = {"
        + "'userId': this.userId,"
        + "'roomId': this.roomId,"
        + "'testName': this.testName,"
        + "'stats': statsObj"
        + "};"
        + "return new Promise((resolve, reject) => {"
        + "var xhr = new XMLHttpRequest();"
        + "xhr.open('POST', this.receiverUrl);"
        + "xhr.onload = function () {"
        + "if (this.status >= 200 && this.status < 300) {"
        + "resolve(xhr.response);"
        + "} else {"
        + "reject({status: this.status, statusText: xhr.statusText});"
        + "}"
        + "};"
        + "xhr.onerror = function () {"
        + "reject({status: this.status, statusText: xhr.statusText});"
        + "};"
        + "xhr.send(JSON.stringify(body));"
        + "})"
        + "};"
        + ""
        + "function sendStats(pc, userId, roomId, testName) {"
        + "window.testStats = new GetStats();"
        + "testStats.init(\""
        + logstashUrl + "\", "+ userNameCommand + ", " + roomNameCommand + "," + pc + ", \""+ testName + "\");"
        + "testStats.startPublishing(" + statsPublishingInterval +");"
        + "console.log('SendStats started for ' + " + userNameCommand + " + ' (every " + statsPublishingInterval + "ms) at ->' , Date());"
        + "console.log('To stop publishing, call testStats.stopPublishing()');"
        + "} "
        + ""
        + "setTimeout(function () {"
        + "if (" + pc + ") {"
        + "sendStats(" + pc + ", " + userNameCommand + ", "  + roomNameCommand + ", '"  + testName + "');"
        + "}"
        + "}, " + statsPublishingInterval + ");";
  }

}
