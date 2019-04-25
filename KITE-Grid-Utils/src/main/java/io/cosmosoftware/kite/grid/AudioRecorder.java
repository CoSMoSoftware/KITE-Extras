package io.cosmosoftware.kite.grid;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/** Servlet implementation class AudioRecorder */
@WebServlet("/AudioRecorder")
public class AudioRecorder extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static final String LOG_FILE = "record.log";

  private static final String OUTPUT_FILE = "record.wav";

  private static final String MAC_AUDIO_DRIVER = "coreaudio";

  private static final String WIN_AUDIO_DRIVER = "waveaudio";

  /** @see HttpServlet#HttpServlet() */
  public AudioRecorder() {
    super();
    // TODO Auto-generated constructor stub
  }

  /** @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response) */
  /**
   * In case of recording, the servlet will use sox.path property to get the sox installation path
   * and audio.device property to get the name of the sound device for mac.
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String record = request.getParameter("record");
    String duration = request.getParameter("duration");
    duration = duration == null ? "00:00" : duration;
    String download = request.getParameter("download");

    if (record != null) {

      String audioDriver = "";
      String audioDevice = "";
	  String inputParam = "-d";
      // Entertain mac and windows only
      String osName = System.getProperty("os.name").toLowerCase();
      if (osName.indexOf("win") >= 0) {
        audioDriver = WIN_AUDIO_DRIVER;
      } else if (osName.indexOf("mac") >= 0) {
        audioDriver = MAC_AUDIO_DRIVER;
        audioDevice = System.getProperty("audio.device");
		inputParam = "";
        if (audioDevice == null) {
          response.sendError(HttpServletResponse.SC_BAD_REQUEST, "audio.device is not found");
          return;
        }
      } else {
        response.sendError(
            HttpServletResponse.SC_BAD_REQUEST,
            "Only Windows and Mac OS X are supported for recording");
        return;
      }

      // Sox command line path
      String soxPath = System.getProperty("sox.path");
      if (soxPath == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "sox.path is not found");
        return;
      }

      // Command to record audio of specified duration trimming silence at the
      // beginning
      String[] command = {
        soxPath,
        "-t",
        audioDriver,
        audioDevice,
        "-r",
        "8000",
        "-c",
        "1",
        "-b",
        "16",
		inputParam,
        OUTPUT_FILE,
        "trim",
        "0",
        duration,
        "silence",
        "1",
        "0.1",
        "1%"
      };
      System.out.print("Executing: ");
      for (String component : command) {
        System.out.print(component + " ");
      }
      System.out.println("");

      ProcessBuilder pb = new ProcessBuilder(command);
      pb.redirectErrorStream(true);
      pb.redirectOutput(ProcessBuilder.Redirect.appendTo(new File(LOG_FILE)));
      Process p = pb.start();

      response.getWriter().append("Check " + LOG_FILE + " for output!");

    } else if (download != null) {

      File downloadFile = new File(OUTPUT_FILE);

      // gets MIME type of the file
      String mimeType =
          Files.probeContentType(
              FileSystems.getDefault().getPath(downloadFile.getAbsolutePath(), ""));

      // modifies response
      if (mimeType != null) {
        response.setContentType(mimeType);
      }
      response.setContentLength((int) downloadFile.length());

      // forces download
      String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
      response.setHeader("Content-Disposition", headerValue);

      byte[] buffer = new byte[4096];
      int bytesRead = -1;

      // obtains response's output stream
      OutputStream outStream = response.getOutputStream();
      FileInputStream inStream = new FileInputStream(downloadFile);
      while ((bytesRead = inStream.read(buffer)) != -1) {
        outStream.write(buffer, 0, bytesRead);
      }

      inStream.close();
      outStream.flush();
      outStream.close();
    }
  }

  /** @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response) */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub
    doGet(request, response);
  }
}
