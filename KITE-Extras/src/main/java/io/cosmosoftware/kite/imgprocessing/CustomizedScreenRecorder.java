/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */


package io.cosmosoftware.kite.imgprocessing;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.screenrecorder.ScreenRecorder;

/**
 * The type Specialized screen recorder.
 */
public class CustomizedScreenRecorder extends ScreenRecorder {
  
  private String name;
  
  /**
   * Instantiates a new Customized screen recorder.
   *
   * @param cfg           the cfg
   * @param captureArea   the capture area
   * @param fileFormat    the file format
   * @param screenFormat  the screen format
   * @param mouseFormat   the mouse format
   * @param audioFormat   the audio format
   * @param movieFolder   the movie folder
   * @param videoFileName the video file name
   *
   * @throws IOException  the io exception
   * @throws AWTException the awt exception
   */
  public CustomizedScreenRecorder(GraphicsConfiguration cfg,
                                  Rectangle captureArea, Format fileFormat, Format screenFormat,
                                  Format mouseFormat, Format audioFormat, File movieFolder,
                                  String videoFileName) throws IOException, AWTException {
    super(cfg, captureArea, fileFormat, screenFormat, mouseFormat,
      audioFormat, movieFolder);
    this.name = videoFileName;
  }
  
  @Override
  protected File createMovieFile(Format fileFormat) throws IOException {
    if (!movieFolder.exists()) {
      movieFolder.mkdirs();
    } else if (!movieFolder.isDirectory()) {
      throw new IOException("\"" + movieFolder + "\" is not a directory.");
    }
    
    SimpleDateFormat dateFormat = new SimpleDateFormat(
      "yyyy-MM-dd HH.mm.ss");
    
    return new File(movieFolder, name + "."
      + Registry.getInstance().getExtension(fileFormat));
  }
}