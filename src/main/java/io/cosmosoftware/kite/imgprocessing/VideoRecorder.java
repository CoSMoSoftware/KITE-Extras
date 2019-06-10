/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.imgprocessing;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.Status;
import org.monte.media.Format;
import org.monte.media.FormatKeys;
import org.monte.media.math.Rational;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.*;
import java.io.File;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

/**
 * The type Video recorder.
 */
public class VideoRecorder{
  
  private final int DEFAULT_TOP_MARGIN = 110;
  private File recordedVideoDirectoryPath;
  private CustomizedScreenRecorder recorder;
  private String fileFormat;
  
  /**
   * Instantiates a new Video recorder.
   *
   * @param recordedVideoDirectoryPath the recorded video directory path
   */
  public VideoRecorder(String recordedVideoDirectoryPath) {
    this.recordedVideoDirectoryPath = new File(recordedVideoDirectoryPath);
  }
  
  /**
   * Start recording with a given starting point and custom dimension
   *
   * @param startingPoint the starting point
   * @param width         the width
   * @param height        the height
   * @param videoName     the video name
   *
   * @throws KiteTestException the kite test exception
   */
  public void startRecording(Point startingPoint, int width, int height, String videoName) throws KiteTestException {
    try {
      this.recorder = new CustomizedScreenRecorder(
        getDefaultGraphicConfiguration(),
        startingPoint == null
          ? getDefaultCaptureArea()
          : new Rectangle((int)startingPoint.getX(), (int)startingPoint.getY(), width,height),
        this.fileFormat == null
          ? getDefaultFileFormat()
          : getFileFormat(this.fileFormat),
        getDefaultScreenFormat(),
        getDefaultMouseFormat(),
        null, // no audio
        this.recordedVideoDirectoryPath,
        videoName);
      this.recorder.start();
    } catch (Exception e) {
      e.printStackTrace();
      throw new KiteTestException("Error while creating video recorder and recording video: " + e.getLocalizedMessage(), Status.BROKEN);
    }
  }
  
  /**
   * Start recording for the whole screen
   *
   * @param videoName the video name
   *
   * @throws KiteTestException the kite test exception
   */
  public void startRecording(String videoName) throws KiteTestException {
    startRecording(null, 0,0, videoName);
  }
  
  
  /**
   * Start recording.
   *
   * @throws KiteTestException the kite test exception
   */
  public void startRecording() throws KiteTestException {
    startRecording( "recorded_video");
  }
  
  /**
   * Start recording the area of a web element, relatively to the
   * location and size of the webdriver's windows
   *
   * @param element   the element
   * @param webDriver the web driver
   * @param videoName the video name
   *
   * @throws Exception the exception
   */
  public void startRecording(WebElement element, WebDriver webDriver,String videoName) throws Exception
  {
    org.openqa.selenium.Point windowLocation = webDriver.manage().window().getPosition();
    org.openqa.selenium.Point elementLocation = element.getLocation();
    org.openqa.selenium.Dimension elementDimension = element.getSize();
    int topBarMargin = DEFAULT_TOP_MARGIN;
    // todo: adapt margin to each type of browser
    Point startingPoint = new Point(windowLocation.getX() + elementLocation.getX(), windowLocation.getY() + elementLocation.getY() + topBarMargin);
    startRecording(startingPoint , elementDimension.getWidth(), elementDimension.getHeight(), videoName);
  }
  
  /**
   * Stop recording.
   *
   * @throws KiteTestException the kite test exception
   */
  public void stopRecording() throws KiteTestException {
    if (this.recorder != null) {
      try {
        this.recorder.stop();
      } catch (Exception e) {
        throw new KiteTestException("Error while stopping recording video: " + e.getLocalizedMessage(), Status.BROKEN);
      }
    }
  }
  
  public void setFileFormat(String fileFormat) {
    this.fileFormat = fileFormat;
  }
  
  private static GraphicsConfiguration getDefaultGraphicConfiguration(){
    return GraphicsEnvironment
      .getLocalGraphicsEnvironment()
      .getDefaultScreenDevice()
      .getDefaultConfiguration();
  }
  
  private static Rectangle getDefaultCaptureArea() {
    return new Rectangle(0,0,
      (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
      (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
  }
  
  
  private static Format getDefaultMouseFormat(){
    return new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
      CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
      DepthKey, 24, FrameRateKey, Rational.valueOf(15),
      QualityKey, 1.0f,
      KeyFrameIntervalKey, 15 * 60);
  }
  
  private static Format getFileFormat(String format){
    return new Format(MediaTypeKey, FormatKeys.MediaType.FILE, MimeTypeKey, format);
  }
  
  private static Format getDefaultFileFormat() {
    return new Format(MediaTypeKey, FormatKeys.MediaType.FILE, MimeTypeKey, MIME_AVI);
  }
  
  private static Format getDefaultScreenFormat() {
    return new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
      CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
      DepthKey, 24, FrameRateKey, Rational.valueOf(15),
      QualityKey, 1.0f,
      KeyFrameIntervalKey, 15 * 60);
  }
}
