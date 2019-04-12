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

  private static final String OUTPUT_FILE = "record.wav";

  private static final String LOG_FILE = "record.log";

  private static final String MAC_SOX_INSTALLATION_PATH = "/usr/local/bin/sox";

  private static final String WIN_SOX_INSTALLATION_PATH = "C:\\\\Program Files\\sox\\sox.exe";

  private static final String MAC_SOUND_DEVICE_NAME = "Soundflower (2ch)";

  private static final String WIN_SOUND_DEVICE_NAME = "VAC";

  private static final String MAC_AUDIO_DRIVER = "coreaudio";

  private static final String WIN_AUDIO_DRIVER = "waveaudio";

  /** @see HttpServlet#HttpServlet() */
  public AudioRecorder() {
    super();
    // TODO Auto-generated constructor stub
  }

  /** @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response) */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String record = request.getParameter("record");
    String duration = request.getParameter("duration");
    duration = duration == null ? "00:00" : duration;
    String download = request.getParameter("download");

    if (record != null) {

      // Command to record audio of specified duration trimming silence at the
      // beginning
      String[] command = {
        MAC_SOX_INSTALLATION_PATH,
        "-t",
        MAC_AUDIO_DRIVER,
        MAC_SOUND_DEVICE_NAME,
        "-r",
        "8000",
        "-c",
        "1",
        "-b",
        "16",
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
      for (String component : command) { System.out.print(component + " "); }
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
      if (mimeType != null) { response.setContentType(mimeType); }
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
