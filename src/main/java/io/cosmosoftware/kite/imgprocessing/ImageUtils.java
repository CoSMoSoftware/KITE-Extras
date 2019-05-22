/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 * Proprietary and Confidential
 *
 * Only RingCentral, Inc. and affiliated companies may internally use this file.
 * Reproduction, copy, adaptation, works derived from this file are permitted
 * only if used for RingCentral Inc. and affiliated companies for internal
 * purposes. Sale, transfer or sub-license of this file and any modification or
 * derivative works from this file to any third parties are strictly prohibited.
 */

package io.cosmosoftware.kite.imgprocessing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * This class contains static functions related to image handling
 */
public class ImageUtils {
  
  /**
   * Verifies if the image contains a certain rgb value.
   *
   * @param originalImage the original image.
   * @param value         the rgb value.
   *
   * @return if the image contains a certain rgb value.
   */
  public static boolean containRGBValue(BufferedImage originalImage, int value) {
    for (int x = 0; x < originalImage.getWidth(); x++) {
      for (int y = 0; y < originalImage.getHeight(); y++) {
        if (originalImage.getRGB(x, y) == value) {
          return true;
        }
      }
    }
    return false;
  }
  
  /**
   * Translate a BufferedImage to a map of point and RGB values.
   *
   * @param originalImage the original image.
   *
   * @return the map of point and RGB values.
   */
  public static HashMap<Point, Integer> getRGBPointMap(BufferedImage originalImage) {
    HashMap<Point, Integer> temp = new HashMap<>();
    
    for (int x = 0; x < originalImage.getWidth(); x++) {
      for (int y = 0; y < originalImage.getHeight(); y++) {
        int clr = originalImage.getRGB(x, y);
        temp.put(new Point(x, y), clr);
      }
    }
    
    return temp;
  }
  
  /**
   * Goes through the RGB values of an image and count their recurrence.
   *
   * @param originalImage the original image.
   *
   * @return the map of RGB values and their recurrence count.
   */
  public static HashMap<Integer, Integer> getValueRecurrenceMap(BufferedImage originalImage) {
    HashMap<Integer, Integer> temp = new HashMap<>();
    
    for (int x = 0; x < originalImage.getWidth(); x++) {
      for (int y = 0; y < originalImage.getHeight(); y++) {
        int clr = originalImage.getRGB(x, y);
        
        if (!temp.containsKey(clr)) {
          temp.put(clr, 0);
        }
        int existingCount = temp.get(clr) + 1;
        temp.put(clr, existingCount);
      }
    }
    
    return temp;
  }
  
}
