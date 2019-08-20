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

import io.appium.java_client.MobileElement;
import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.report.Status;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

import static io.cosmosoftware.kite.imgprocessing.ImageUtils.containRGBValue;

/**
 * The type Image comparator.
 */
public class ImageComparator {

  private static final KiteLogger logger = KiteLogger.getLogger(ImageComparator.class.getName());

  private static boolean debug = false;

  private static int imageCount = 1;

  /**
   * Compares two BufferedImage
   *
   * @return true if the images are the same
   */
  private static boolean areEqual(BufferedImage image1, BufferedImage image2) {
    if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
      return false;
    }

    for (int x = 1; x < image2.getWidth(); x++) {
      for (int y = 1; y < image2.getHeight(); y++) {
        if (image1.getRGB(x, y) != image2.getRGB(x, y)) {
          return false;
        }
      }
    }
    return true;
  }

  private static List<Integer> bufferWithZero(List<Integer> array, int finalSize) {
    List<Integer> res = Collections.synchronizedList(new ArrayList<>());;
    int arraySize = array.size();
    int bufferCount = Math.abs(arraySize - finalSize);
    if (arraySize < finalSize) {
      for (int index = 0; index < array.size(); index++) {
        if (index < bufferCount / 2) {
          res.add(0);
        } else {
          if (index < arraySize + bufferCount / 2) {
            res.addAll(array);
            index = arraySize + bufferCount / 2;
          } else {
            res.add(0);
          }
        }
      }
    } else {
      for (int index = 0; index < array.size(); index++) {
        if (index > bufferCount / 2 && index < arraySize + bufferCount / 2) {
          res.add(array.get(index));
        } else {
          res.add(0);
        }
      }
    }
    return res;
  }

  /**
   * Clones the BufferedImage
   *
   * @param bufferedImage the BufferedImage to clone
   * @return a cloned BufferedImage
   */
  private static BufferedImage clone(BufferedImage bufferedImage) {
    BufferedImage clone =
        new BufferedImage(
            bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
    Graphics2D g = clone.createGraphics();
    g.drawImage(bufferedImage, 0, 0, null);
    g.dispose();
    return clone;
  }

  /**
   * Compare elements list.
   *
   * @param mobileElementList the mobile element list
   * @param offset the offset
   * @param screenShot1Bytes the byte array of screen shot 1
   * @param screenShot2Bytes the byte array  screen shot 2
   * @param windowSize the window size
   * @return the list of comparison results
   * @throws KiteTestException the io exception
   */
  public static List<Boolean> compareElements(
      List<MobileElement> mobileElementList,
      Rectangle offset,
      byte[] screenShot1Bytes,
      byte[] screenShot2Bytes,
      Dimension windowSize)
      throws KiteTestException {

    return compareRectangles(getRectangleList(mobileElementList), offset,
        createImageFromBytes(screenShot1Bytes),
        createImageFromBytes(screenShot2Bytes), windowSize);
  }

  /**
   * Compare elements list with a predefined checksum value.
   *
   * @param mobileElementList the mobile element list
   * @param offset the offset
   * @param screenShot the byte array of screen shot
   * @param windowSize the window size
   * @param expectedChecksum expected checksum value to compare
   * @return the list of comparison results
   * @throws KiteTestException the io exception
   */
  public static List<Boolean> compareElementsWithExpectedChecksum(
      List<MobileElement> mobileElementList,
      Rectangle offset,
      byte[] screenShot,
      Dimension windowSize,
      long expectedChecksum)
      throws KiteTestException {

    return compareRectanglesWithExpectedChecksum(
        getRectangleList(mobileElementList), offset, screenShot, windowSize, expectedChecksum);
  }

  /**
   * Verifies if the rectangles of a list contain a certain RGB value.
   *
   * @param mobileElementList the mobile element list
   * @param offset the offset
   * @param screenShot the byte array of screen shot
   * @param windowSize the window size
   * @param expectedValue expected rgb value
   * @return the list of verification results
   * @throws KiteTestException the io exception
   */
  public static List<Boolean> compareElementsWithExpectedRGBValue(
      List<MobileElement> mobileElementList,
      Rectangle offset,
      byte[] screenShot,
      Dimension windowSize,
      int expectedValue)
      throws KiteTestException {

    return compareRectanglesWithExpectedRGBValue(
        getRectangleList(mobileElementList), offset, screenShot, windowSize, expectedValue);
  }

  /**
   * Compare elements list.
   *
   * @param rectangleList the rectangle list representing video elements
   * @param offset the offset
   * @param screenShot1Bytes the byte array of screen shot 1
   * @param screenShot2Bytes the byte array  screen shot 2
   * @param windowSize the window size
   * @return the list of comparison results
   * @throws KiteTestException the io exception
   */
  public static List<Boolean> compareRectangles(
      List<Rectangle> rectangleList,
      Rectangle offset,
      byte[] screenShot1Bytes,
      byte[] screenShot2Bytes,
      Dimension windowSize)
      throws KiteTestException {
    return compareRectangles(rectangleList, offset, createImageFromBytes(screenShot1Bytes),
        createImageFromBytes(screenShot2Bytes), windowSize);
  }

  /**
   * Compare elements list.
   *
   * @param rectangleList the rectangle list representing video elements
   * @param offset the offset
   * @param screenShot1 the BufferedImage of screen shot 1
   * @param screenShot2 the BufferedImage  screen shot 2
   * @param windowSize the window size
   * @return the list of comparison results
   * @throws KiteTestException the io exception
   */
  public static List<Boolean> compareRectangles(
      List<Rectangle> rectangleList,
      Rectangle offset,
      BufferedImage screenShot1,
      BufferedImage screenShot2,
      Dimension windowSize)
      throws KiteTestException {
    if (debug) {
      logger.info("WindowSize->" + windowSize);
      for (int i = 0; i < rectangleList.size(); i++) {
        ImageComparator.printRectangle("MobileElement[" + i + "]", rectangleList.get(i));
      }
    }

    // Crop images from the first file
    List<BufferedImage> bufferedImageList1 =
        ImageComparator.getBufferedImageList(screenShot1, windowSize, rectangleList, offset);
    // Crop images from the second file
    List<BufferedImage> bufferedImageList2 =
        ImageComparator.getBufferedImageList(screenShot2, windowSize, rectangleList, offset);

    // Compute and accumulate results
    List<Boolean> result = Collections.synchronizedList(new ArrayList<>());;
    for (int index = 0; index < bufferedImageList1.size(); index++) {
      result.add(
          ImageComparator.areEqual(bufferedImageList1.get(index), bufferedImageList2.get(index)));
    }
    // Return results
    return result;
  }

  /**
   * Compare elements list with a predefined checksum value.
   *
   * @param rectangleList the rectangle list representing video elements
   * @param offset the offset
   * @param screenShot the byte array of screen shot
   * @param windowSize the window size
   * @param expectedChecksum expected checksum value to compare
   * @return the list of comparison results
   * @throws KiteTestException the io exception
   */
  public static List<Boolean> compareRectanglesWithExpectedChecksum(
      List<Rectangle> rectangleList,
      Rectangle offset,
      byte[] screenShot,
      Dimension windowSize,
      long expectedChecksum)
      throws KiteTestException {

    if (debug) {
      logger.info("WindowSize->" + windowSize);
      for (int i = 0; i < rectangleList.size(); i++) {
        ImageComparator.printRectangle("MobileElement[" + i + "]", rectangleList.get(i));
      }
    }

    // Crop images from the screenshot
    List<BufferedImage> bufferedImageList =
        ImageComparator
            .getBufferedImageList(createImageFromBytes(screenShot), windowSize, rectangleList,
                offset);

    List<Boolean> result = Collections.synchronizedList(new ArrayList<>());;
    for (BufferedImage bufferedImage : bufferedImageList) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        ImageIO.write(bufferedImage, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        Checksum checksumEngine = new Adler32();
        checksumEngine.update(imageInByte, 0, imageInByte.length);
        long checksum = checksumEngine.getValue();
        logger.debug("actual checksum:" + checksum + " - expected checksum:" + expectedChecksum);
        result.add(checksum == expectedChecksum);
      } catch (IOException e) {
        throw new KiteTestException(e.getClass().getName() + " comparing rectangle with checksum",
            Status.BROKEN, e);
      }
    }

    return result;
  }

  /**
   * Verifies if the elements of a list contain a certain RGB value.
   *
   * @param rectangleList the rectangle list representing video elements
   * @param offset the offset
   * @param screenShot the byte array of screen shot
   * @param windowSize the window size
   * @param expectedValue expected rgb value
   * @return the list of verification results
   * @throws KiteTestException the kite test exception
   */
  public static List<Boolean> compareRectanglesWithExpectedRGBValue(
      List<Rectangle> rectangleList,
      Rectangle offset,
      byte[] screenShot,
      Dimension windowSize,
      int expectedValue)
      throws KiteTestException {

    if (debug) {
      logger.info("WindowSize->" + windowSize);
      for (int i = 0; i < rectangleList.size(); i++) {
        ImageComparator.printRectangle("MobileElement[" + i + "]", rectangleList.get(i));
      }
    }

    // Crop images from the screenshot
    List<BufferedImage> bufferedImageList =
        ImageComparator
            .getBufferedImageList(createImageFromBytes(screenShot), windowSize, rectangleList,
                offset);

    List<Boolean> result = Collections.synchronizedList(new ArrayList<>());;
    for (BufferedImage bufferedImage : bufferedImageList) {
      result.add(containRGBValue(bufferedImage, expectedValue));
    }

    return result;
  }

  private static boolean contains(Rectangle rect1, Rectangle rect2) {
    return ImageComparator.getAwtRectangle(rect1).contains(ImageComparator.getAwtRectangle(rect2));
  }

  /**
   * Create a BufferedImage from a byte array
   *
   * @param imageData byte array containing the image data
   * @return a BufferedImage object.
   * @throws KiteTestException the kite test exception
   */
  public static BufferedImage createImageFromBytes(byte[] imageData) throws KiteTestException {
    try {
      return ImageIO.read(new ByteArrayInputStream(imageData));
    } catch (IOException e) {
      throw new KiteTestException(e.getClass().getName() + " create Image from Bytes",
          Status.BROKEN, e);
    }
  }

  /**
   * Crop an image with smaller rectangle
   *
   * @param image the image
   * @param crop the crop zone
   * @return BufferedImage of the cropped out zone
   */
  public static BufferedImage cropImage(BufferedImage image, Rectangle crop) {
    return image.getSubimage(crop.x, crop.y, crop.width, crop.height);
  }

  private static java.awt.Rectangle getAwtRectangle(Rectangle rectangle) {
    return new java.awt.Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  }

  /**
   * Crops the full size images into a list of smaller rectangles.
   *
   * @param bufferedShot the original BufferedImage
   * @param windowSize size of the window
   * @param srcRectangleList list of target crop rectangles
   * @param offset a Rectangle offset
   * @return the List of BufferedImage
   */
  private static List<BufferedImage> getBufferedImageList(
      BufferedImage bufferedShot,
      Dimension windowSize,
      List<Rectangle> srcRectangleList,
      Rectangle offset)
      throws KiteTestException {

    ImageComparator.writeImage(bufferedShot, "png", new File("screenShot" + imageCount + ".png"));

    int xFactor = bufferedShot.getWidth() / windowSize.width;
    int yFactor = bufferedShot.getHeight() / windowSize.height;

    int elementCount = 1;
    List<BufferedImage> bufferedImageList = new ArrayList<BufferedImage>();
    for (Rectangle srcRectangle : srcRectangleList) {
      BufferedImage cloneShot = ImageComparator.clone(bufferedShot);

      int rectCount = 1;
      List<Rectangle> rectangleList = getOverlaps(srcRectangle, srcRectangleList);
      if (rectangleList.size() > 0) {
        Graphics2D g = cloneShot.createGraphics();
        g.setColor(Color.WHITE);
        for (Rectangle rectangle : rectangleList) {
          g.fill(
              new java.awt.Rectangle(
                  rectangle.x * xFactor,
                  rectangle.y * yFactor,
                  rectangle.width * xFactor,
                  rectangle.height * yFactor));
          ImageComparator.writeImage(
              cloneShot,
              "png",
              new File("rectShot" + imageCount + "_" + elementCount + "_" + rectCount++ + ".png"));
        }
        g.dispose();
      }

      BufferedImage croppedShot =
          cloneShot.getSubimage(
              (srcRectangle.x + offset.x) * xFactor,
              (srcRectangle.y + offset.y) * yFactor,
              (srcRectangle.width - offset.width) * xFactor,
              (srcRectangle.height - offset.height) * yFactor);
      ImageComparator.writeImage(
          croppedShot, "png", new File("cropShot" + imageCount + "_" + elementCount++ + ".png"));

      bufferedImageList.add(croppedShot);
    }
    imageCount++;
    return bufferedImageList;
  }

  private static Rectangle getIntersection(Rectangle rect1, Rectangle rect2) {
    java.awt.Rectangle rectangle =
        ImageComparator.getAwtRectangle(rect1).intersection(ImageComparator.getAwtRectangle(rect2));
    return rectangle.isEmpty() ? null : ImageComparator.getOpenqaRectangle(rectangle);
  }

  /**
   * Calculate the normalized pixel values of an image
   *
   * @param image the image
   * @return int array values in [0->255]
   */
  private static List<Integer> getNormalizedPixelValues(BufferedImage image) {
    return normalize(((DataBufferByte) image.getRaster().getDataBuffer()).getData());
  }

  private static Rectangle getOpenqaRectangle(java.awt.Rectangle rectangle) {
    return new Rectangle(rectangle.x, rectangle.y, rectangle.height, rectangle.width);
  }

  /**
   * Gets the List of overlaps between the srcRectangleList and the otherRectangle
   *
   * @param otherRectangle the rectangle to get overlap with
   * @param srcRectangleList the source rectangles
   * @return the list of overlapped Rectangles
   */
  private static List<Rectangle> getOverlaps(
      Rectangle otherRectangle, List<Rectangle> srcRectangleList) {
    List<Rectangle> rectangleList = new ArrayList<Rectangle>();

    for (Rectangle srcRectangle : srcRectangleList) {
      // element and mobileElement are same
      // Rect of element and mobileElement are equal
      // mobileElement is inside element
      if (srcRectangle.equals(otherRectangle)
          || ImageComparator.contains(srcRectangle, otherRectangle)) {
        continue;
      }

      // element is inside mobileElement
      if (ImageComparator.contains(otherRectangle, srcRectangle)) {
        rectangleList.add(srcRectangle);
        continue;
      }

      Rectangle rectangle = ImageComparator.getIntersection(otherRectangle, srcRectangle);
      // mobileElement and element are not intersecting
      if (rectangle == null) {
        continue;
      }

      rectangleList.add(rectangle);
    }

    return rectangleList;
  }

  /**
   * Calculate the sum of normalized pixels of an image.
   *
   * @param image the image
   * @return the sum of the pixel values.
   * @throws KiteTestException if the image is too large.
   */
  public static int getPixelSum(BufferedImage image) throws KiteTestException {
    return getSum(getNormalizedPixelValues(image));
  }

  /**
   * Calculate the difference of the pixel sum between 2 images
   *
   * @param image1 first image
   * @param image2 second image
   * @return the sum difference
   * @throws KiteTestException the kite test exception
   */
  public static int getPixelSumDiff(BufferedImage image1, BufferedImage image2)
      throws KiteTestException {

    List<Integer> image1Pixels = getNormalizedPixelValues(image1);
    List<Integer> image2Pixels = getNormalizedPixelValues(image2);

    int finalSize =
        image1Pixels.size() > image2Pixels.size() ? image1Pixels.size() : image2Pixels.size();

    image1Pixels = bufferWithZero(image1Pixels, finalSize);
    image2Pixels = bufferWithZero(image2Pixels, finalSize);

    return Math.abs(getSum(image1Pixels) - getSum(image2Pixels));
  }

  private static String getRectangle(Rectangle rectangle) {
    return "{{"
        + rectangle.x
        + ", "
        + rectangle.y
        + "}, {"
        + rectangle.width
        + ", "
        + rectangle.height
        + "}}";
  }

  /**
   * Put all the elements' rectangles in to a list.
   *
   * @param mobileElementList the mobile element list
   * @return list of element's rectangles.
   */
  private static List<Rectangle> getRectangleList(List<MobileElement> mobileElementList) {
    List<Rectangle> srcRectangleList = Collections.synchronizedList(new ArrayList<>());;
    for (MobileElement mobileElement : mobileElementList) {
      srcRectangleList.add(mobileElement.getRect());
    }
    return srcRectangleList;
  }

  /**
   * Calculate the sum of an int array.
   *
   * @param array array of Integer.
   * @return the sum of the array.
   * @throws KiteTestException if the sum is too large.
   */
  public static int getSum(List<Integer> array) throws KiteTestException {
    int res = 0;
    for (int index : array) {
      if (res > Integer.MAX_VALUE - index) {
        throw new KiteTestException("The sum is too large at index " +
            "[" + index + "/" + array.size() + "], try again with smaller array/photo.",
            Status.BROKEN);
      }
      res += index;
    }
    return res;
  }

  /**
   * Turn all of the values of a byte array in [-127->128] to [0->255] .
   *
   * @param bytes the byte array with values in [-127->128]
   * @return int array values in [0->255]
   */
  private static List<Integer> normalize(byte[] bytes) {
    List<Integer> res = Collections.synchronizedList(new ArrayList<>());;
    for (byte index : bytes) {
      if (index < 0) {
        res.add(128 + Math.abs(index));
      }
    }
    return res;
  }

  private static void printRectangle(String text, Rectangle rectangle) {
    logger.info(text + "::" + ImageComparator.getRectangle(rectangle));
  }

  private static void writeImage(BufferedImage bufferedImage, String formatName, File output)
      throws KiteTestException {
    if (debug) {
      try {
        ImageIO.write(bufferedImage, formatName, output);
      } catch (IOException e) {
        throw new KiteTestException(e.getClass().getName() + " writing Image for debug purpose",
            Status.BROKEN, e, true);
      }
    }
  }
}
