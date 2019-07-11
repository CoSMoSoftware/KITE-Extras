/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObjectBuilder;

/**
 * Standard stats take from https://www.w3.org/TR/webrtc-stats/
 */
public class StandardRTCStats {

  private static String[] RTCAudioHandlerStats() {
    final String[] keys = {
        "audioLevel",
        "totalAudioEnergy",
        "voiceActivityFlag",
        "totalSamplesDuration"
    };
    return merge(RTCMediaHandlerStats(), keys);
  }

  private static String[] RTCAudioReceiverStats() {
    final String[] keys = {
        "estimatedPlayoutTimestamp",
        "jitterBufferDelay",
        "jitterBufferEmittedCount",
        "totalSamplesReceived",
        "concealedSamples",
        "concealmentEvents"
    };
    return merge(RTCAudioHandlerStats(), keys);
  }

  private static String[] RTCAudioSenderStats() {
    final String[] keys = {
        "echoReturnLoss",
        "echoReturnLossEnhancement",
        "totalSamplesSent"
    };
    return merge(RTCAudioHandlerStats(), keys);
  }

  // certificate
  private static String[] RTCCertificateStats() {
    final String[] keys = {
        "fingerprint",
        "fingerprintAlgorithm",
        "base64Certificate",
        "issuerCertificateId"
    };
    return merge(RTCStats(), keys);
  }

  // codec
  private static String[] RTCCodecStats() {
    String[] keys = {
        "payloadType",
        "codecType",
        "transportId",
        "mimeType",
        "clockRate",
        "channels",
        "sdpFmtpLine",
        "implementation"
    };
    return merge(RTCStats(), keys);
  }

  // data-channel
  private static String[] RTCDataChannelStats() {
    final String[] keys = {
        "label",
        "protocol",
        "dataChannelIdentifier",
        "transportId",
        "state",
        "messagesSent",
        "bytesSent",
        "messagesReceived",
        "bytesReceived"
    };
    return merge(RTCStats(), keys);
  }

  // candidate-pair
  private static String[] RTCIceCandidatePairStats() {
    final String[] keys = {
        "transportId",
        "localCandidateId",
        "remoteCandidateId",
        "state",
        "nominated",
        "packetsSent",
        "packetsReceived",
        "bytesSent",
        "bytesReceived",
        "lastPacketSentTimestamp",
        "lastPacketReceivedTimestamp",
        "firstRequestTimestamp",
        "lastRequestTimestamp",
        "lastResponseTimestamp",
        "totalRoundTripTime",
        "currentRoundTripTime",
        "availableOutgoingBitrate",
        "availableIncomingBitrate",
        "circuitBreakerTriggerCount",
        "requestsReceived",
        "requestsSent",
        "responsesReceived",
        "responsesSent",
        "retransmissionsReceived",
        "retransmissionsSent",
        "consentRequestsSent",
        "consentExpiredTimestamp"
    };
    return merge(RTCStats(), keys);
  }

  // local-candidate + remote-candidate
  private static String[] RTCIceCandidateStats() {
    final String[] keys = {
        "transportId",
        "networkType",
        "ip",
        "port",
        "protocol",
        "candidateType",
        "priority",
        "url",
        "relayProtocol",
        "deleted"
    };
    return merge(RTCStats(), keys);
  }

  private static String[] RTCInboundRtpStreamStats() {
    final String[] keys = {
        "trackId",
        "receiverId",
        "remoteId",
        "framesDecoded",
        "lastPacketReceivedTimestamp",
        "averageRtcpInterval",
        "fecPacketsReceived",
        "bytesReceived",
        "packetsFailedDecryption",
        "packetsDuplicated",
        "perDscpPacketsReceived"
    };
    return merge(RTCReceivedRtpStreamStats(), keys);
  }

  // track
  private static String[] RTCMediaHandlerStats() {
    final String[] keys = {
        "trackIdentifier",
        "remoteSource",
        "ended",
        "kind",
        "priority"
    };
    return merge(RTCStats(), keys);
  }

  // stream
  private static String[] RTCMediaStreamStats() {
    final String[] keys = {
        "streamIdentifier",
        "trackIds",
    };
    return merge(RTCStats(), keys);
  }

  private static String[] RTCOutboundRtpStreamStats() {
    final String[] keys = {
        "trackId",
        "senderId",
        "remoteId",
        "lastPacketSentTimestamp",
        "targetBitrate",
        "framesEncoded",
        "totalEncodeTime",
        "averageRtcpInterval",
        "qualityLimitationReason",
        "qualityLimitationDurations",
        "perDscpPacketsSent"
    };
    return merge(RTCSentRtpStreamStats(), keys);
  }

  // peer-connection
  private static String[] RTCPeerConnectionStats() {
    final String[] keys = {
        "dataChannelsOpened",
        "dataChannelsClosed",
        "dataChannelsRequested",
        "dataChannelsAccepted"
    };
    return merge(RTCStats(), keys);
  }

  // inbound-rtp
  private static String[] RTCReceivedRtpStreamStats() {
    final String[] keys = {
        "packetsReceived",
        "packetsLost",
        "jitter",
        "packetsDiscarded",
        "packetsRepaired",
        "burstPacketsLost",
        "burstPacketsDiscarded",
        "burstLossCount",
        "burstDiscardCount",
        "burstLossRate",
        "burstDiscardRate",
        "gapLossRate",
        "gapDiscardRate"
    };
    return merge(RTCRtpStreamStats(), keys);
  }

  // remote-inbound-rtp
  private static String[] RTCRemoteInboundRtpStreamStats() {
    final String[] keys = {
        "localId",
        "roundTripTime",
        "fractionLost"
    };
    return merge(RTCReceivedRtpStreamStats(), keys);
  }

  // remote-outbound-rtp
  private static String[] RTCRemoteOutboundRtpStreamStats() {
    final String[] keys = {
        "localId",
        "remoteTimestamp"
    };
    return merge(RTCSentRtpStreamStats(), keys);
  }

  // csrc
  private static String[] RTCRtpContributingSourceStats() {
    final String[] keys = {
        "contributorSsrc",
        "inboundRtpStreamId",
        "packetsContributedTo",
        "audioLevel"
    };
    return merge(RTCStats(), keys);
  }

  // RTCRtpStreamStats
  private static String[] RTCRtpStreamStats() {
    final String[] keys = {
        "ssrc",
        "kind",
        "transportId",
        "codecId",
        "firCount",
        "pliCount",
        "nackCount",
        "sliCount",
        "qpSum"
    };
    return merge(RTCStats(), keys);
  }

  private static String[] RTCSenderAudioTrackAttachmentStats() {
    return RTCAudioSenderStats();
  }

  private static String[] RTCSenderVideoTrackAttachmentStats() {
    return RTCVideoSenderStats();
  }

  // outbound-rtp
  private static String[] RTCSentRtpStreamStats() {
    final String[] keys = {
        "packetsSent",
        "packetsDiscardedOnSend",
        "fecPacketsSent",
        "bytesSent",
        "bytesDiscardedOnSend"
    };
    return merge(RTCRtpStreamStats(), keys);
  }

  // RTCStats type
  private static String[] RTCStats() {
    final String[] keys = {
        "timestamp",
        "type",
        "id"
    };
    return keys;
  }

  // transport
  private static String[] RTCTransportStats() {
    final String[] keys = {
        "packetsSent",
        "packetsReceived",
        "bytesSent",
        "bytesReceived",
        "rtcpTransportStatsId",
        "iceRole",
        "dtlsState",
        "selectedCandidatePairId",
        "localCertificateId",
        "remoteCertificateId",
        "dtlsCipher",
        "srtpCipher"
    };
    return merge(RTCStats(), keys);
  }

  private static String[] RTCVideoHandlerStats() {
    final String[] keys = {
        "frameWidth",
        "frameHeight",
        "framesPerSecond"
    };
    return merge(RTCMediaHandlerStats(), keys);
  }

  private static String[] RTCVideoReceiverStats() {
    final String[] keys = {
        "estimatedPlayoutTimestamp",
        "jitterBufferDelay",
        "jitterBufferEmittedCount",
        "framesReceived",
        "keyFramesReceived",
        "framesDecoded",
        "framesDropped",
        "partialFramesLost",
        "fullFramesLost"
    };
    return merge(RTCVideoHandlerStats(), keys);
  }

  private static String[] RTCVideoSenderStats() {
    final String[] keys = {
        "framesCaptured",
        "framesSent",
        "hugeFramesSent",
        "keyFramesSent",
    };
    return merge(RTCVideoHandlerStats(), keys);
  }

  /**
   * Gets standard get stats.
   *
   * @param dataChannleEnabled the data channle enabled
   * @param audioVideo the audio video
   * @return the standard get stats
   */
  public static JsonObjectBuilder getStandardGetStats(boolean dataChannleEnabled, int audioVideo) {
    final String[] standardTypeEnum = getStandardTypeEnum(dataChannleEnabled);
    JsonObjectBuilder tmpJsonObjectBuilder = Json.createObjectBuilder();
    for (String enumType : standardTypeEnum) {
      String[] retval = null;
      switch (enumType) {
        case "codec":
          retval = RTCCodecStats();
          break;
        case "inbound-rtp":
          retval = RTCInboundRtpStreamStats();
          break;
        case "outbound-rtp":
          retval = RTCOutboundRtpStreamStats();
          break;
        case "remote-inbound-rtp":
          retval = RTCRemoteInboundRtpStreamStats();
          break;
        case "remote-outbound-rtp":
          retval = RTCRemoteOutboundRtpStreamStats();
          break;
        case "csrc":
          retval = RTCRtpContributingSourceStats();
          break;
        case "peer-connection":
          retval = RTCPeerConnectionStats();
          break;
        case "data-channel":
          retval = RTCDataChannelStats();
          break;
        case "stream":
          retval = RTCMediaStreamStats();
          break;
        case "track":
          retval = track(audioVideo);
          break;
        case "sender":
          retval = sender(audioVideo);
          break;
        case "receiver":
          retval = receiver(audioVideo);
          break;
        case "transport":
          retval = RTCTransportStats();
          break;
        case "candidate-pair":
          retval = RTCIceCandidatePairStats();
          break;
        case "local-candidate":
          retval = RTCIceCandidateStats();
          break;
        case "remote-candidate":
          retval = RTCIceCandidateStats();
          break;
        case "certificate":
          retval = RTCCertificateStats();
          break;
      }
      if (retval != null) {
        tmpJsonObjectBuilder.add(enumType, Arrays.toString(retval));
      }
    }

    return tmpJsonObjectBuilder;
  }

  // RTCStatsType enum
  // audio = 01
  // video = 10
  // audio + video = 11
  private static String[] getStandardTypeEnum(boolean dataChannelEnabled) {
    String[] rtcStatsType = null;
    if (dataChannelEnabled) {
      rtcStatsType = new String[]{
          "codec",
          "inbound-rtp",
          "outbound-rtp",
          "remote-inbound-rtp",
          "remote-outbound-rtp",
          "csrc",
          "peer-connection",
          /*"data-channel",*/
          "stream",
          "track",
          "sender",
          "receiver",
          "transport",
          "candidate-pair",
          "local-candidate",
          "remote-candidate",
          "certificate"
      };
    } else {
      rtcStatsType = new String[]{
          "codec",
          "inbound-rtp",
          "outbound-rtp",
          "remote-inbound-rtp",
          "remote-outbound-rtp",
          "csrc",
          "peer-connection",
          /*"data-channel",*/
          "stream",
          "track",
          "sender",
          "receiver",
          "transport",
          "candidate-pair",
          "local-candidate",
          "remote-candidate",
          "certificate"
      };
    }
    return rtcStatsType;
  }

  private static String[] merge(final String[] a, final String[] b) {
    List<String> lst = Collections.synchronizedList(new ArrayList<>());;
    for (String ele : a) {
      if (!lst.contains(ele)) {
        lst.add(ele);
      }
    }
    for (String ele : b) {
      if (!lst.contains(ele)) {
        lst.add(ele);
      }
    }
    return lst.toArray(new String[lst.size()]);
  }

  // receiver
  // audio = 01
  // video = 10
  // audio + video = 11
  private static String[] receiver(int receiverScore) {
    final String[] keys = {
        "streamIdentifier",
        "trackIds",
    };
    String[] retval = new String[0];
    if ((receiverScore & (1 << 0)) > 0) {
      retval = merge(retval, RTCAudioReceiverStats());
    }
    if ((receiverScore & (1 << 1)) > 0) {
      retval = merge(retval, RTCVideoReceiverStats());
    }
    retval = merge(retval, keys);
    return retval;
  }

  // sender
  // audio = 01
  // video = 10
  // audio + video = 11
  private static String[] sender(int senderScore) {
    final String[] keys = {
        "streamIdentifier",
        "trackIds",
    };
    String[] retval = new String[0];
    if ((senderScore & (1 << 0)) > 0) {
      retval = merge(retval, RTCAudioSenderStats());
    }
    if ((senderScore & (1 << 1)) > 0) {
      retval = merge(retval, RTCVideoSenderStats());
    }
    retval = merge(retval, keys);
    return retval;
  }

  /**
   * Returns JavaScript to collect browser stats using getStats() API
   *
   * @return the JavaScript as string.
   */
  private static final String stashStatsScript() {
    return "function getAllStats() {\n"
        + "    return new Promise( (resolve, reject) => {\n"
        + "        try{\n"
        + "            appController.call_.pcClient_.pc_.getStats().then((report) => {\n"
        + "                let statTypes = new Set();\n"
        + "                // type -> stat1, stat2, stat3\n"
        + "                let statTree = new Map();\n"
        + "                for (let stat of report.values()) {\n"
        + "                    const curType = stat.type;\n"
        + "                    const prvStat = statTree.get(curType);\n"
        + "                    if(prvStat) {\n"
        + "                        const _tmp = [...prvStat, stat];\n"
        + "                        statTree.set(curType, _tmp)\n"
        + "                    }else{\n"
        + "                        const _tmp = [stat];\n"
        + "                        statTree.set(curType, _tmp)\n"
        + "                    }\n"
        + "                }\n"
        + "                let retval = {};\n"
        + "                for (const [key, statsArr] of statTree) {\n"
        + "                    let keysArr = [];\n"
        + "                    for(const curStat of statsArr){\n"
        + "                        const keys = Object.keys(curStat);\n"
        + "                        keysArr = [ ...keysArr, ...keys ];\n"
        + "                    }\n"
        + "                    retval[key] = keysArr;\n"
        + "                }\n"
        + "                resolve(retval);\n"
        + "        });\n"
        + "        } catch(err) {\n"
        + "            reject(err);\n"
        + "        }\n"
        + "    });\n"
        + "}\n"
        + "function stashStats() {\n"
        + "    getAllStats().then( (data)=> {\n"
        + "        window.KITEStatsDiff = data;\n"
        + "    }, err => {\n"
        + "        console.log('error',err);\n"
        + "    });\n"
        + "}\n"
        + "stashStats()\n";
  }

  // audio = 01
  // video = 10
  // audio + video = 11
  private static String[] track(int trackScore) {
    final String[] keys = {
        "streamIdentifier",
        "trackIds",
    };
    String[] retval = new String[0];
    if ((trackScore & (1 << 0)) > 0) {
      retval = merge(retval, RTCSenderAudioTrackAttachmentStats());
    }
    if ((trackScore & (1 << 1)) > 0) {
      retval = merge(retval, RTCSenderVideoTrackAttachmentStats());
    }
    retval = merge(retval, keys);
    return retval;
  }

}
