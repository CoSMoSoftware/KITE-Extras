package io.cosmosoftware.kite.grid;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Servlet implementation class AudioRecorder. */
@WebServlet("/AudioRecorder")
public class AudioRecorder extends HttpServlet {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant LENGTH_DIFFERENCE. */
  private static final long LENGTH_DIFFERENCE = 2;

  /** The Constant SPLITTED_FILE. */
  private static final String SPLITTED_FILE = "split";

  /** The Constant LOG_FILE. */
  private static final String LOG_FILE = "record.log";

  /** The Constant TRIMMED_FILE. */
  private static final String TRIMMED_FILE = "trim.wav";

  /** The Constant RECORDED_FILE. */
  private static final String RECORDED_FILE = "record.wav";

  /** The Constant WIN_NEW_FILE_OPTION. */
  private static final String WIN_NEW_FILE_OPTION = "-d";

  /** The Constant WIN_AUDIO_DRIVER. */
  private static final String WIN_AUDIO_DRIVER = "waveaudio";

  /** The Constant WIN_DIR_COMMAND. */
  private static final String WIN_DIR_COMMAND = "dir";

  /** The Constant MAC_AUDIO_DRIVER. */
  private static final String MAC_AUDIO_DRIVER = "coreaudio";

  /** The Constant MAC_DIR_COMMAND. */
  private static final String MAC_DIR_COMMAND = "ls";

  /** The Constant PROP_SOX_PATH. */
  private static final String PROP_SOX_PATH = "sox.path";

  /** The Constant PROP_PESQ_PATH. */
  private static final String PROP_PESQ_PATH = "pesq.path";

  /** The Constant PROP_AUDIO_DEVICE. */
  private static final String PROP_AUDIO_DEVICE = "audio.device";

  /**
   * Instantiates a new audio recorder.
   *
   * @see HttpServlet#HttpServlet() HttpServlet#HttpServlet()
   */
  public AudioRecorder() {
    super();
    // TODO Auto-generated constructor stub
    // System.setProperty(PROP_SOX_PATH, "/usr/local/bin/sox");
    // System.setProperty(PROP_PESQ_PATH,
    // "/Users/sajidhussain/Documents/ITU-T_pesq/source/PESQ");
    // System.setProperty(PROP_AUDIO_DEVICE, "Soundflower (2ch)");
  }

  /**
   * Do get. Set sox.path, pesq.path and audio.device before invocation.
   *
   * @param request the request with parameters record=1, duration=00:00, score=1, media=filepath,
   *     silence=0.0, download=1.
   * @param response the response 400 if required parameters or properties are missing, 0 if no
   *     audio was received, N if partial audio was received, stdout of the score computation.
   * @throws ServletException the servlet exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Entertain mac and windows only
    boolean isWindows = true;
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.indexOf("win") >= 0) {
      // Do Nothing
    } else if (osName.indexOf("mac") >= 0) {
      isWindows = false;
    } else {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST,
          "Only Windows and Mac OS X are supported for recording");
      return;
    }

    System.out.println("*** AudioRecorder PARAMETERS ***");
    String record = request.getParameter("record");
    String duration = request.getParameter("duration");
    duration = duration == null ? "00:00" : duration;
    System.out.println("record->" + record);
    System.out.println("duration->" + duration);

    String score = request.getParameter("score");
    String media = request.getParameter("media");
    String silence = request.getParameter("silence");
    silence = silence == null ? "0.0" : silence;
    System.out.println("score->" + score);
    System.out.println("media->" + media);
    System.out.println("silence->" + silence);

    String download = request.getParameter("download");
    System.out.println("download->" + download);
    System.out.println("*** AudioRecorder PARAMETERS ***");

    // Sox command line path
    String soxPath = System.getProperty(PROP_SOX_PATH);
    System.out.println(PROP_SOX_PATH + ": " + soxPath);

    List<String> listOutput = new ArrayList<String>();

    if (record != null) { // record=1

      if (soxPath == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, PROP_SOX_PATH + " is not found");
        return;
      }

      String newFile = "";
      String audioDriver = "";
      String audioDevice = "";

      if (isWindows) {
        newFile = WIN_NEW_FILE_OPTION;
        audioDriver = WIN_AUDIO_DRIVER;
      } else {
        audioDriver = MAC_AUDIO_DRIVER;
        audioDevice = System.getProperty(PROP_AUDIO_DEVICE);
        System.out.println(PROP_AUDIO_DEVICE + ": " + audioDevice);
        if (audioDevice == null) {
          response.sendError(
              HttpServletResponse.SC_BAD_REQUEST, PROP_AUDIO_DEVICE + " is not found");
          return;
        }
      }

      // Record the audio of specified duration trimming silence at the beginning
      String[] recordCommandWindows = {
        soxPath,
        "-t",
        audioDriver,
        "-r",
        "8000",
        "-c",
        "1",
        "-b",
        "16",
        newFile,
        RECORDED_FILE,
        "trim",
        "0",
        duration,
        "silence",
        "1",
        "0.1",
        "1%"
      };
      String[] recordCommandMac = {
        soxPath,
        "-r",
        "8000",
        "-c",
        "1",
        "-b",
        "16",
        RECORDED_FILE,
        "trim",
        "0",
        duration,
        "silence",
        "1",
        "0.1",
        "1%"
      };
      try {
        executeCommand(isWindows ? recordCommandWindows : recordCommandMac, true, false);
      } catch (InterruptedException e) {
        e.printStackTrace();
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        return;
      }

      response.getWriter().append("Check " + LOG_FILE + " for output!");

    } else if (score != null) { // score=1

      if (media == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "media is not provided in query");
        return;
      }

      try {
        long recordedFileDuration = Math.round(getDurationOfWavInSeconds(new File(RECORDED_FILE)));
        // No audio was present
        if (recordedFileDuration <= 0) {
          response.getWriter().append(recordedFileDuration + "");
          return;
        }

        if (soxPath == null) {
          response.sendError(HttpServletResponse.SC_BAD_REQUEST, PROP_SOX_PATH + " is not found");
          return;
        }

        // Trim the silence from both ends of the recorded file
        String[] trimRecordingCommand = {
          soxPath,
          RECORDED_FILE,
          TRIMMED_FILE,
          "silence",
          "1",
          "0.1",
          "1%",
          "reverse",
          "silence",
          "1",
          "0.1",
          "1%",
          "reverse"
        };
        executeCommand(trimRecordingCommand, false, true);

        // Split the trimmed file w.r.t. silence
        String[] splitCommand = {
          soxPath,
          TRIMMED_FILE,
          SPLITTED_FILE + ".wav",
          "silence",
          "1",
          "0.5",
          "1%",
          "1",
          silence,
          "1%",
          ":",
          "newfile",
          ":",
          "restart"
        };
        executeCommand(splitCommand, false, true);

        // Choose between Windows and Mac listing
        String dirCommand = MAC_DIR_COMMAND;
        if (isWindows) dirCommand = WIN_DIR_COMMAND;

        // List all the split files and choose the longest duration for score
        String[] listCommand = {dirCommand};
        listOutput = executeCommand(listCommand, false, true, SPLITTED_FILE);
        double longestDuration = 0;
        String outputFile = listOutput.get(0);
        for (String output : listOutput) {
          double fileDuration = getDurationOfWavInSeconds(new File(output));
          if (fileDuration > longestDuration) {
            longestDuration = fileDuration;
            outputFile = output;
          }
        }

        // Trim the silence from both ends of the original media
        String[] trimMediaCommand = {
          soxPath,
          media,
          TRIMMED_FILE,
          "silence",
          "1",
          "0.1",
          "1%",
          "reverse",
          "silence",
          "1",
          "0.1",
          "1%",
          "reverse"
        };
        executeCommand(trimMediaCommand, false, true);

        // File lengths were different
        long difference =
            Math.abs(
                Math.round(getDurationOfWavInSeconds(new File(TRIMMED_FILE)))
                    - Math.round(getDurationOfWavInSeconds(new File(outputFile))));
        if (difference > LENGTH_DIFFERENCE) {
          removeFiles(listOutput);
          response.getWriter().append(difference + "");
          return;
        }

        String pesqPath = System.getProperty(PROP_PESQ_PATH);
        System.out.println(PROP_PESQ_PATH + ": " + pesqPath);
        if (pesqPath == null) {
          removeFiles(listOutput);
          response.sendError(HttpServletResponse.SC_BAD_REQUEST, PROP_PESQ_PATH + " is not found");
          return;
        }

        // Compute PESQ score
        String[] pesqCommand = {pesqPath, "+8000", TRIMMED_FILE, outputFile};
        String pesqScore = executeCommand(pesqCommand, false, true);
        removeFiles(listOutput);
        response.getWriter().append(pesqScore);

      } catch (UnsupportedAudioFileException | InterruptedException e) {
        e.printStackTrace();
        removeFiles(listOutput);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        return;
      }

    } else if (download != null) { // download=1

      File downloadFile = new File(RECORDED_FILE);

      // gets MIME type of the file
      String mimeType =
          Files.probeContentType(
              FileSystems.getDefault().getPath(downloadFile.getAbsolutePath(), ""));

      // modifies response
      if (mimeType != null) response.setContentType(mimeType);
      response.setContentLength((int) downloadFile.length());

      // forces download
      String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
      response.setHeader("Content-Disposition", headerValue);

      byte[] buffer = new byte[4096];
      int bytesRead = -1;

      // obtains response's output stream
      OutputStream outStream = response.getOutputStream();
      FileInputStream inStream = new FileInputStream(downloadFile);
      while ((bytesRead = inStream.read(buffer)) != -1) outStream.write(buffer, 0, bytesRead);

      // flushes and closes all the streams
      inStream.close();
      outStream.flush();
      outStream.close();

    } else {

      response.getWriter().append("No matching parameter is found");
    }
  }

  /**
   * Do post.
   *
   * @param request the request
   * @param response the response
   * @throws ServletException the servlet exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub
    doGet(request, response);
  }

  /**
   * Execute command.
   *
   * @param command the command
   * @param logFile the log file
   * @param waitFor the wait for
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws InterruptedException the interrupted exception
   */
  private static String executeCommand(String[] command, boolean logFile, boolean waitFor)
      throws IOException, InterruptedException {
    Process process = executeCommand(command, logFile);

    String output = null;
    if (!logFile) output = buildOutput(process, null, null);

    if (waitFor) process.waitFor();

    return output;
  }

  /**
   * Execute command for array.
   *
   * @param command the command
   * @param logFile the log file
   * @param waitFor the wait for
   * @param filter the filter
   * @return the list
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws InterruptedException the interrupted exception
   */
  private static List<String> executeCommand(
      String[] command, boolean logFile, boolean waitFor, String filter)
      throws IOException, InterruptedException {
    Process process = executeCommand(command, logFile);

    List<String> output = new ArrayList<String>();
    if (!logFile) buildOutput(process, output, filter);

    if (waitFor) process.waitFor();

    return output;
  }

  /**
   * Execute command.
   *
   * @param command the command
   * @param logFile the log file
   * @return the process
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private static Process executeCommand(String[] command, boolean logFile) throws IOException {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true);
    if (logFile)
      processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(new File(LOG_FILE)));

    System.out.print("*** Executing: ");
    for (String component : command) System.out.print(component + " ");
    System.out.println("");

    return processBuilder.start();
  }

  /**
   * Builds the output.
   *
   * @param process the process
   * @param stringList the string list
   * @param filter the filter
   * @return the string
   */
  private static String buildOutput(Process process, List<String> stringList, String filter) {
    StringBuilder builder = new StringBuilder();

    Scanner scanner = new Scanner(process.getInputStream());
    System.out.println("*** BEGIN OUTPUT ***");
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      System.out.println(line);
      builder.append(line);
      if (stringList != null) {
        if (filter == null) stringList.add(line);
        else if (line.startsWith(filter)) stringList.add(line);
      }
    }
    System.out.println("*** END OUTPUT ***");
    scanner.close();

    return builder.toString();
  }

  /**
   * Gets the duration of wav in seconds.
   *
   * @param file the file
   * @return the duration of wav in seconds
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws UnsupportedAudioFileException the unsupported audio file exception
   */
  private static double getDurationOfWavInSeconds(File file)
      throws IOException, UnsupportedAudioFileException {
    AudioInputStream stream = null;
    try {
      stream = AudioSystem.getAudioInputStream(file);
      AudioFormat format = stream.getFormat();
      double length =
          file.length()
              / format.getSampleRate()
              / (format.getSampleSizeInBits() / 8.0)
              / format.getChannels();
      return length;
    } finally {
      try {
        stream.close();
      } catch (Exception ex) {
      }
    }
  }

  private static void removeFiles(List<String> fileList) {
    for (String filename : fileList) new File(filename).delete();
  }
}
