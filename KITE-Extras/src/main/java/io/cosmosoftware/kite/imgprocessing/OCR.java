/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.imgprocessing;

import io.cosmosoftware.kite.util.ReportUtils;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * The type Ocr.
 */
public class OCR {
  
  private static final Logger logger = Logger.getLogger(OCR.class.getName());
  
  /**
   * read characters on the crop of a given image
   *
   * @param pathFile path to the image (without the extension)
   * @param ext      extension png or jpg
   * @param xPos     x point of the setStartTimestamp of the crop
   * @param yPos     y point of the setStartTimestamp of the crop
   * @param width    width of the image
   * @param height   height of the crop
   *
   * @return the String read on the cropped image
   */
  public static String cropOCR(String pathFile, String ext, int xPos, int yPos, int width, int height) {
    File imageFile = new File(pathFile + ext);
    ITesseract instance = new Tesseract();
    File tessDataFolder = LoadLibs.extractTessResources("tessdata");
    instance.setDatapath(tessDataFolder.getPath());
    try {
      BufferedImage image = ImageIO.read(imageFile);
      File croppedFile = new File(pathFile + "_crop_" + xPos + ".png");
      BufferedImage dest = image.getSubimage(xPos, yPos, width, height);
      ImageIO.write(dest, "png", croppedFile);
      return instance.doOCR(croppedFile);
    } catch (Exception e) {
      logger.error(ReportUtils.getStackTrace(e));
    }
    return null;
  }
  
  /**
   * read characters on the crop of a given image
   *
   * @param pathFile path to the image (png jpg)
   * @param xPos     x point of the setStartTimestamp of the crop
   * @param yPos     y point of the setStartTimestamp of the crop
   * @param width    width of the image
   * @param height   height of the crop
   *
   * @return the String read on the cropped image
   */
  public static String cropOCR(String pathFile, int xPos, int yPos, int width, int height) {
    return cropOCR(pathFile, ".png", xPos, yPos, width, height);
  }
  
  /**
   * Parse an OCR string to a double, replacing space by the decimal dot.
   *
   * @param s the string to parse
   *
   * @return the double.
   * @throws NumberFormatException the number format exception
   */
  public static double getDouble(String s) throws NumberFormatException {
    if (!s.contains(".")) {
      s = s.trim().replaceFirst(" ", ".");
    }
    s = s.trim().replaceAll(" ", "").replaceAll("/", "7");
    return Double.parseDouble(s);
  }
  
  /**
   * read characters on a given image
   *
   * @param pathFile path to the image (png jpg)
   *
   * @return the String read on the image
   */
  public static String simpleOCR(String pathFile) {
    File imageFile = new File(pathFile);
    ITesseract instance = new Tesseract();
    File tessDataFolder = LoadLibs.extractTessResources("tessdata");
    instance.setDatapath(tessDataFolder.getPath());
    try {
      return instance.doOCR(imageFile);
    } catch (TesseractException e) {
      logger.error(ReportUtils.getStackTrace(e));
    }
    return null;
  }
  
}
